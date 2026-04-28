package net.sideways_stairs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.sideways_stairs.SidewaysStairsMod;
import net.sideways_stairs.client.model.HorizontalStairsBakedModel;
import net.sideways_stairs.item.HorizontalStairsItem;
import net.sideways_stairs.registry.ModItems;

public class ModelEvents {
    public static final ResourceLocation SHARED_ITEM_RL = new ResourceLocation(SidewaysStairsMod.MOD_ID, "item/horizontal_stairs");
    public static final ModelResourceLocation SHARED_ITEM_MRL = new ModelResourceLocation(SHARED_ITEM_RL, "");

    public static void register(IEventBus modBus) {
        modBus.addListener(ModelEvents::onModelRegistry);
        modBus.addListener(ModelEvents::onModelBake);
    }

    private static void onModelRegistry(ModelRegistryEvent event) {
        SidewaysStairsMod.LOGGER.info("[SidewaysStairs] ModelRegistryEvent fired, registering special model");
        ModelLoader.addSpecialModel(SHARED_ITEM_RL);
    }

    private static void onModelBake(ModelBakeEvent event) {
        // wrap block model variants
        for(ResourceLocation resourceLocation : new java.util.ArrayList<>(event.getModelRegistry().keySet())) {
            if(!(resourceLocation instanceof ModelResourceLocation)) {
                continue;
            }
            if(!resourceLocation.getNamespace().equals(SidewaysStairsMod.MOD_ID)) {
                continue;
            }
            if(resourceLocation.getPath().equals("horizontal_stairs")) {
                event.getModelRegistry().compute(resourceLocation, (k, original) -> original == null ? null : new HorizontalStairsBakedModel(original));
            }
        }

        IBakedModel sharedBaked = event.getModelRegistry().get(SHARED_ITEM_RL);
        if(sharedBaked == null) {
            SidewaysStairsMod.LOGGER.warn("[SidewaysStairs] Shared item model not found!");
            return;
        }

        // stored so ItemModelShaper can interact with it
        HorizontalStairsBakedModel wrapped = new HorizontalStairsBakedModel(sharedBaked);
        event.getModelRegistry().put(SHARED_ITEM_MRL, wrapped);

        net.minecraft.client.renderer.ItemModelMesher shaper = Minecraft.getInstance().getItemRenderer().getItemModelShaper();
        for(HorizontalStairsItem item : ModItems.SOURCE_TO_ITEM.values()) {
            shaper.register(item, SHARED_ITEM_MRL);
        }

        SidewaysStairsMod.LOGGER.info("[SidewaysStairs] Registered {} items to shared item model", ModItems.SOURCE_TO_ITEM.size());
    }
}
