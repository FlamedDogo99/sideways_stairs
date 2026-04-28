package net.sideways_stairs.registry;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.fml.RegistryObject;
import net.sideways_stairs.SidewaysStairsMod;
import net.sideways_stairs.block.HorizontalStairsBlock;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, SidewaysStairsMod.MOD_ID);

    public static final RegistryObject<Block> HORIZONTAL_STAIRS_BLOCK = BLOCKS.register("horizontal_stairs", () -> new HorizontalStairsBlock(Block.Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion()));
}
