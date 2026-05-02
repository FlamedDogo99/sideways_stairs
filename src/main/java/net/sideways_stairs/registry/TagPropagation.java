package net.sideways_stairs.registry;

import net.minecraft.block.Block;
import net.minecraft.block.StairsBlock;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ITagCollection;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.sideways_stairs.SidewaysStairsMod;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.*;

@Mod.EventBusSubscriber(modid = SidewaysStairsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TagPropagation {
    private static final Unsafe UNSAFE;
    // offsets into net.minecraft.tags.Tag
    private static long valuesOffset = -1L;
    private static long valuesListOffset = -1L;
    private static long superTypeOffset = -1L;

    static {
        Unsafe unsafe = null;
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);
            unsafe = (Unsafe) f.get(null);
        } catch(Exception e) {
            SidewaysStairsMod.LOGGER.warn("Failed to get Unsafe: {}", e.getMessage());
        }
        UNSAFE = unsafe;

        if(unsafe != null) {
            try {
                Class<?> tagClass = Class.forName("net.minecraft.tags.Tag");
                for(Field f : tagClass.getDeclaredFields()) {
                    f.setAccessible(true);
                    if(Set.class.isAssignableFrom(f.getType())) {
                        valuesOffset = unsafe.objectFieldOffset(f);
                    } else if(List.class.isAssignableFrom(f.getType())) {
                        valuesListOffset = unsafe.objectFieldOffset(f);
                    } else if(Class.class.isAssignableFrom(f.getType())) {
                        superTypeOffset = unsafe.objectFieldOffset(f);
                    }
                }
            } catch(Exception e) {
                SidewaysStairsMod.LOGGER.warn("Failed to resolve Tag field offsets: {}", e.getMessage());
            }
        }
    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        if(UNSAFE == null || valuesOffset == -1L || superTypeOffset == -1L) {
            SidewaysStairsMod.LOGGER.warn("Tag field offsets unavailable, skipping propagation.");
            return;
        }

        Block horizontalStairs = ModBlocks.HORIZONTAL_STAIRS_BLOCK.get();
        ITagCollection<Block> collection = BlockTags.getAllTags();

        Set<ITag<Block>> tagsToJoin = new LinkedHashSet<>();
        for(Block src : ForgeRegistries.BLOCKS.getValues()) {
            if(!(src instanceof StairsBlock)) {
                continue;
            }
            for(ITag<Block> tag : collection.getAllTags().values()) {
                if(tag.contains(src)) {
                    tagsToJoin.add(tag);
                }
            }
        }

        for(ITag<Block> tag : tagsToJoin) {
            if(tag.contains(horizontalStairs)) {
                continue;
            }
            injectIntoTag(tag, horizontalStairs);
        }
    }

    @SuppressWarnings("unchecked")
    private static void injectIntoTag(ITag<Block> tag, Block block) {
        try {
            // update values Set.
            Set<Block> oldValues = (Set<Block>) UNSAFE.getObject(tag, valuesOffset);
            Set<Block> newValues = new LinkedHashSet<>(oldValues);
            newValues.add(block);
            UNSAFE.putObject(tag, valuesOffset, newValues);

            // updates valuesList to match.
            if(valuesListOffset != -1L) {
                com.google.common.collect.ImmutableList<Block> newList = com.google.common.collect.ImmutableList.copyOf(newValues);
                UNSAFE.putObject(tag, valuesListOffset, newList);
            }

            // widen closestCommonSuperType to Block so isInstance() passes for both StairsBlock and HorizontalStairsBlock.
            Class<?> currentSuperType = (Class<?>) UNSAFE.getObject(tag, superTypeOffset);
            if(!currentSuperType.isInstance(block)) {
                // go up block's hierarchy until we find a type that also covers the existing entries
                Class<?> candidate = block.getClass();
                while(candidate != null && !candidate.isAssignableFrom(currentSuperType)) {
                    candidate = candidate.getSuperclass();
                }
                if(candidate != null) {
                    UNSAFE.putObject(tag, superTypeOffset, candidate);
                }
            }
        } catch(Exception e) {
            SidewaysStairsMod.LOGGER.warn("injectIntoTag failed: {}", e.getMessage());
        }
    }
}