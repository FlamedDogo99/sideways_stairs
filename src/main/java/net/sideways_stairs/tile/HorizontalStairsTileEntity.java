package net.sideways_stairs.tile;

import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.registries.ForgeRegistries;
import net.sideways_stairs.client.model.HorizontalStairsModelData;
import net.sideways_stairs.registry.ModTileEntities;

import javax.annotation.Nonnull;

public class HorizontalStairsTileEntity extends TileEntity {

    private ResourceLocation sourceBlock;

    public HorizontalStairsTileEntity() {
        super(ModTileEntities.HORIZONTAL_STAIRS.get());
    }

    public void setSourceBlock(ResourceLocation id) {
        this.sourceBlock = id;
        setChanged();
        if(level != null && level.isClientSide) {
            requestModelDataUpdate();
        }
    }

    public ResourceLocation getSourceId() {
        return sourceBlock;
    }

    public Block getSourceBlock() {
        if(sourceBlock == null) {
            return null;
        }
        return ForgeRegistries.BLOCKS.getValue(sourceBlock);
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.load(state, nbt);
        if(nbt.contains("Source")) {
            sourceBlock = new ResourceLocation(nbt.getString("Source"));
        }
    }

    @Override
    @MethodsReturnNonnullByDefault
    public CompoundNBT save(@Nonnull CompoundNBT nbt) {
        super.save(nbt);
        if(sourceBlock != null) {
            nbt.putString("Source", sourceBlock.toString());
        }
        return nbt;
    }

    @Override
    @MethodsReturnNonnullByDefault
    public CompoundNBT getUpdateTag() {
        return save(new CompoundNBT());
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(worldPosition, 1, getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        load(getBlockState(), pkt.getTag());
        requestModelDataUpdate();
        if(level != null) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        Block src = getSourceBlock();
        if(src != null) {
            return new ModelDataMap.Builder().withInitial(HorizontalStairsModelData.SOURCE_STATE, src.defaultBlockState()).build();
        }
        return net.minecraftforge.client.model.data.EmptyModelData.INSTANCE;
    }
}
