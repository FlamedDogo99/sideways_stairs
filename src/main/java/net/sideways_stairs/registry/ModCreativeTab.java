package net.sideways_stairs.registry;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.sideways_stairs.item.HorizontalStairsItem;

import javax.annotation.Nonnull;

public class ModCreativeTab extends ItemGroup {

    public static final ModCreativeTab INSTANCE = new ModCreativeTab();

    private static final ResourceLocation OAK_STAIRS_ID = new ResourceLocation("minecraft", "oak_stairs");

    private ModCreativeTab() {
        super("sideways_stairs");
    }

    @Override
    @Nonnull
    public ItemStack makeIcon() {
        HorizontalStairsItem preferred = ModItems.SOURCE_TO_ITEM.get(OAK_STAIRS_ID);
        HorizontalStairsItem item = preferred != null ? preferred : ModItems.SOURCE_TO_ITEM.values().stream().findFirst().orElse(null);

        if(item == null) {
            // SOURCE_TO_ITEM isn't populated yet, can't use it
            return new ItemStack(net.minecraft.item.Items.OAK_STAIRS);
        }

        ItemStack stack = new ItemStack(item);
        CompoundNBT tag = new CompoundNBT();
        tag.putString("Source", item.getSourceBlockId().toString());
        stack.getOrCreateTagElement("BlockEntityTag").merge(tag);
        return stack;
    }
}
