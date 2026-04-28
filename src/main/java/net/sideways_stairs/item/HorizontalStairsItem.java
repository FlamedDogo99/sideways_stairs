package net.sideways_stairs.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import net.sideways_stairs.tile.HorizontalStairsTileEntity;

import javax.annotation.Nonnull;

public class HorizontalStairsItem extends BlockItem {

    private final ResourceLocation sourceBlock;

    public HorizontalStairsItem(Block block, Properties props, ResourceLocation source) {
        super(block, props);
        this.sourceBlock = source;
    }

    @Override
    public void fillItemCategory(@Nonnull net.minecraft.item.ItemGroup group, @Nonnull net.minecraft.util.NonNullList<ItemStack> items) {
        if(this.category == group || group == net.minecraft.item.ItemGroup.TAB_SEARCH) {
            items.add(new ItemStack(this));
        }
    }

    @Override
    @Nonnull
    public ITextComponent getName(@Nonnull ItemStack stack) {
        Block src = ForgeRegistries.BLOCKS.getValue(sourceBlock);
        ITextComponent sourceName = src != null ? src.getName() : new TranslationTextComponent(sourceBlock.toString());
        // "item.sideways_stairs.horizontal_prefix" = "Horizontal %s"
        return new TranslationTextComponent("item.sideways_stairs.horizontal_prefix", sourceName);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(@Nonnull BlockPos pos, World world, PlayerEntity player, @Nonnull ItemStack stack, @Nonnull BlockState state) {
        TileEntity te = world.getBlockEntity(pos);
        if(te instanceof HorizontalStairsTileEntity) {
            ((HorizontalStairsTileEntity) te).setSourceBlock(sourceBlock);
        }
        return super.updateCustomBlockEntityTag(pos, world, player, stack, state);
    }

    public ResourceLocation getSourceBlockId() {
        return sourceBlock;
    }
}
