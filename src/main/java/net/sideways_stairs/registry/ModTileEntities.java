package net.sideways_stairs.registry;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;
import net.sideways_stairs.SidewaysStairsMod;
import net.sideways_stairs.tile.HorizontalStairsTileEntity;

public class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, SidewaysStairsMod.MOD_ID);
    public static final RegistryObject<TileEntityType<HorizontalStairsTileEntity>> HORIZONTAL_STAIRS = TILE_ENTITIES.register("horizontal_stairs", () -> TileEntityType.Builder.of(HorizontalStairsTileEntity::new, ModBlocks.HORIZONTAL_STAIRS_BLOCK.get()).build(null));
}
