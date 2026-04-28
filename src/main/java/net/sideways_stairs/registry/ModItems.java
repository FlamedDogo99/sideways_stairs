package net.sideways_stairs.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.sideways_stairs.SidewaysStairsMod;
import net.sideways_stairs.item.HorizontalStairsItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@net.minecraftforge.fml.common.Mod.EventBusSubscriber(modid = SidewaysStairsMod.MOD_ID, bus = net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD)
public class ModItems {
    public static final Map<ResourceLocation, HorizontalStairsItem> SOURCE_TO_ITEM = new HashMap<>();

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();
        Block block = ModBlocks.HORIZONTAL_STAIRS_BLOCK.get();

        List<Block> snapshot = new ArrayList<>(ForgeRegistries.BLOCKS.getValues());
        for(Block src : snapshot) {
            if(!(src instanceof net.minecraft.block.StairsBlock)) {
                continue;
            }
            ResourceLocation srcId = src.getRegistryName();
            if(srcId == null) {
                continue;
            }

            ResourceLocation itemId = new ResourceLocation(SidewaysStairsMod.MOD_ID, "horizontal_" + srcId.getNamespace() + "_" + srcId.getPath());

            HorizontalStairsItem item = new HorizontalStairsItem(block, new Item.Properties().tab(ModCreativeTab.INSTANCE), srcId);
            item.setRegistryName(itemId);
            registry.register(item);

            SOURCE_TO_ITEM.put(srcId, item);
        }
    }
}
