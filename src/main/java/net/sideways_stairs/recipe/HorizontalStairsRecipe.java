package net.sideways_stairs.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.item.crafting.SpecialRecipeSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;
import net.sideways_stairs.item.HorizontalStairsItem;
import net.sideways_stairs.registry.ModItems;

import javax.annotation.Nonnull;

public class HorizontalStairsRecipe extends SpecialRecipe {

    public static final SpecialRecipeSerializer<HorizontalStairsRecipe> SERIALIZER = new SpecialRecipeSerializer<>(HorizontalStairsRecipe::new);

    public HorizontalStairsRecipe(ResourceLocation id) {
        super(id);
    }

    @Override
    public boolean matches(@Nonnull CraftingInventory inv, @Nonnull World world) {
        boolean hasString = false;
        ItemStack stair = ItemStack.EMPTY;

        for(int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if(s.isEmpty()) {
                continue;
            }

            if(s.getItem() == Items.STRING) {
                if(hasString) {
                    return false; // two strings
                }
                hasString = true;
            } else if(isStair(s.getItem())) {
                if(!stair.isEmpty()) {
                    return false; // two stairs
                }
                stair = s;
            } else {
                return false; // unknown ingredient
            }
        }

        if(!hasString || stair.isEmpty()) {
            return false;
        }

        ResourceLocation stairId = stair.getItem().getRegistryName();
        return stairId != null && ModItems.SOURCE_TO_ITEM.containsKey(stairId);
    }

    @Override
    @Nonnull
    public ItemStack assemble(@Nonnull CraftingInventory inv) {
        ItemStack stair = ItemStack.EMPTY;

        for(int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack s = inv.getItem(i);
            if(!s.isEmpty() && isStair(s.getItem())) {
                stair = s;
                break;
            }
        }

        if(stair.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ResourceLocation stairId = stair.getItem().getRegistryName();
        if(stairId == null) {
            return ItemStack.EMPTY;
        }

        HorizontalStairsItem result = ModItems.SOURCE_TO_ITEM.get(stairId);
        if(result == null) {
            return ItemStack.EMPTY;
        }

        CompoundNBT tag = new CompoundNBT();
        tag.putString("Source", stairId.toString());
        ItemStack out = new ItemStack(result);
        out.getOrCreateTagElement("BlockEntityTag").merge(tag);
        return out;
    }

    @Override
    public boolean canCraftInDimensions(int w, int h) {
        return w * h >= 2;
    }

    @Override
    @Nonnull
    public IRecipeSerializer<?> getSerializer() {
        return SERIALIZER;
    }

    private static boolean isStair(Item item) {
        if(item instanceof HorizontalStairsItem) {
            return false;
        }
        ResourceLocation id = item.getRegistryName();
        if(id == null) {
            return false;
        }
        net.minecraft.block.Block block = ForgeRegistries.BLOCKS.getValue(id);
        return block instanceof net.minecraft.block.StairsBlock;
    }
}