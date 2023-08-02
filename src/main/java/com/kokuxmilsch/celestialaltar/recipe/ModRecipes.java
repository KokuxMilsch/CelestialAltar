package com.kokuxmilsch.celestialaltar.recipe;

import com.kokuxmilsch.celestialaltar.CelestialAltar;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CelestialAltar.MODID);

    public static final RegistryObject<RecipeSerializer<CelestialAltarRecipe>> CELESTIAL_ALTAR_RECIPE = RECIPE_SERIALIZERS.register(CelestialAltarRecipe.Type.ID, () -> CelestialAltarRecipe.Serializer.INSTANCE);


    public static void register(IEventBus modEventBus){
        RECIPE_SERIALIZERS.register(modEventBus);
    }
}
