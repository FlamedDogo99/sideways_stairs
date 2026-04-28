package net.sideways_stairs;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.eventbus.api.IEventBus;
import net.sideways_stairs.registry.ModBlocks;
import net.sideways_stairs.registry.ModCreativeTab;
import net.sideways_stairs.registry.ModRecipes;
import net.sideways_stairs.registry.ModTileEntities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SidewaysStairsMod.MOD_ID)
public class SidewaysStairsMod {

    public static final String MOD_ID = "sideways_stairs";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public SidewaysStairsMod() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modBus);
        ModTileEntities.TILE_ENTITIES.register(modBus);
        // make ModCreativeTab load before items are registered.
        Class<?> _tab = ModCreativeTab.class;
        // ModItems registers itself with @EventBusSubscriber + RegistryEvent
        Class<?> _recipes = ModRecipes.class;

        // register client events explicitly on the mod bus
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            net.sideways_stairs.client.ModelEvents.register(modBus);
        });
    }
}
