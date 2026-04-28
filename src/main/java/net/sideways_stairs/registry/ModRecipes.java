package net.sideways_stairs.registry;

import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sideways_stairs.SidewaysStairsMod;
import net.sideways_stairs.recipe.HorizontalStairsRecipe;

@Mod.EventBusSubscriber(modid = SidewaysStairsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModRecipes {

    @SubscribeEvent
    public static void onRegisterRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        // SOURCE_TO_ITEM should be populated here
        HorizontalStairsRecipe.SERIALIZER.setRegistryName(new ResourceLocation(SidewaysStairsMod.MOD_ID, "horizontal_stair"));
        event.getRegistry().register(HorizontalStairsRecipe.SERIALIZER);
    }
}
