package net.sideways_stairs.block;

import com.google.common.collect.ImmutableList;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.sideways_stairs.item.HorizontalStairsItem;
import net.sideways_stairs.registry.ModItems;
import net.sideways_stairs.tile.HorizontalStairsTileEntity;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

public class HorizontalStairsBlock extends Block {

    public static final EnumProperty<HorizontalFacing> FACING = EnumProperty.create("facing", HorizontalFacing.class);

    private static final VoxelShape SHAPE_NE = VoxelShapes.or(box(0, 0, 0, 8, 16, 16), box(8, 0, 8, 16, 16, 16));
    private static final VoxelShape SHAPE_SE = VoxelShapes.or(box(0, 0, 0, 16, 16, 8), box(0, 0, 8, 8, 16, 16));
    private static final VoxelShape SHAPE_SW = VoxelShapes.or(box(8, 0, 0, 16, 16, 16), box(0, 0, 0, 8, 16, 8));
    private static final VoxelShape SHAPE_NW = VoxelShapes.or(box(0, 0, 8, 16, 16, 16), box(8, 0, 0, 16, 16, 8));

    public HorizontalStairsBlock(Properties props) {
        super(props);
        registerDefaultState(stateDefinition.any().setValue(FACING, HorizontalFacing.NE));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        switch(ctx.getHorizontalDirection()) {
            case NORTH:
                return defaultBlockState().setValue(FACING, HorizontalFacing.NE);
            case EAST:
                return defaultBlockState().setValue(FACING, HorizontalFacing.SE);
            case SOUTH:
                return defaultBlockState().setValue(FACING, HorizontalFacing.SW);
            case WEST:
                return defaultBlockState().setValue(FACING, HorizontalFacing.NW);
            default:
                return defaultBlockState();
        }
    }

    @Override
    @MethodsReturnNonnullByDefault
    public VoxelShape getShape(BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext ctx) {
        switch(state.getValue(FACING)) {
            case NE:
                return SHAPE_NE;
            case SE:
                return SHAPE_SE;
            case SW:
                return SHAPE_SW;
            case NW:
                return SHAPE_NW;
            default:
                return VoxelShapes.block();
        }
    }

    /**
     * returns tile entity or null if something's wrong
     */
    private HorizontalStairsTileEntity getTE(IBlockReader world, BlockPos pos) {
        TileEntity tileEntity = world.getBlockEntity(pos);
        return tileEntity instanceof HorizontalStairsTileEntity ? (HorizontalStairsTileEntity) tileEntity : null;
    }

    private Block sourceBlock(IBlockReader world, BlockPos pos) {
        HorizontalStairsTileEntity te = getTE(world, pos);
        if(te == null) {
            return null;
        }
        return te.getSourceBlock();
    }

    /**
     * Hardness from source block, defaults to 2.0
     */
    @Override
    public float getDestroyProgress(@Nonnull BlockState state, @Nonnull PlayerEntity player, @Nonnull IBlockReader world, @Nonnull BlockPos pos) {
        Block src = sourceBlock(world, pos);
        if(src != null) {
            return src.defaultBlockState().getDestroyProgress(player, world, pos);
        }
        return super.getDestroyProgress(state, player, world, pos);
    }

    /**
     * Sounds from source block
     */
    @Override
    @Nonnull
    public SoundType getSoundType(BlockState state, net.minecraft.world.IWorldReader world, BlockPos pos, net.minecraft.entity.Entity entity) {
        Block src = sourceBlock(world, pos);
        if(src != null) {
            return src.defaultBlockState().getSoundType(world, pos, entity);
        }
        return super.getSoundType(state, world, pos, entity);
    }

    @Override
    @Nonnull
    public List<ItemStack> getDrops(@Nonnull BlockState state, @Nonnull LootContext.Builder builder) {
        TileEntity rawTE = builder.getOptionalParameter(LootParameters.BLOCK_ENTITY);
        if(rawTE instanceof HorizontalStairsTileEntity) {
            HorizontalStairsTileEntity te = (HorizontalStairsTileEntity) rawTE;
            if(te.getSourceId() != null) {
                HorizontalStairsItem item = ModItems.SOURCE_TO_ITEM.get(te.getSourceId());
                if(item != null) {
                    net.minecraft.nbt.CompoundNBT tag = new net.minecraft.nbt.CompoundNBT();
                    tag.putString("Source", te.getSourceId().toString());
                    ItemStack stack = new ItemStack(item);
                    stack.getOrCreateTagElement("BlockEntityTag").merge(tag);
                    return ImmutableList.of(stack);
                }
            }
        }
        return Collections.emptyList();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new HorizontalStairsTileEntity();
    }

    @Override
    @MethodsReturnNonnullByDefault
    public BlockRenderType getRenderShape(@Nonnull BlockState state) {
        return BlockRenderType.MODEL;
    }
}
