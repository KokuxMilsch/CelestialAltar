package com.kokuxmilsch.celestialaltar.integration.jei;

import com.kokuxmilsch.celestialaltar.CelestialAltar;
import com.kokuxmilsch.celestialaltar.recipe.CelestialAltarRecipe;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.List;
import java.util.Objects;

@JeiPlugin
public class JEICelestialAltarModPlugin implements IModPlugin {

    public static RecipeType<CelestialAltarRecipe> CELESTIAL_RITUAL_TYPE = new RecipeType<>(CelestialAltarRecipeCategory.UID, CelestialAltarRecipe.class);

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(CelestialAltar.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new CelestialAltarRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager rm = Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager();

        List<CelestialAltarRecipe> ritual_recipes = rm.getAllRecipesFor(CelestialAltarRecipe.Type.INSTANCE);
        registration.addRecipes(CELESTIAL_RITUAL_TYPE, ritual_recipes);
    }
}
