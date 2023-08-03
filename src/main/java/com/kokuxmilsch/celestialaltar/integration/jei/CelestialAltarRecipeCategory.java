package com.kokuxmilsch.celestialaltar.integration.jei;

import com.kokuxmilsch.celestialaltar.CelestialAltar;
import com.kokuxmilsch.celestialaltar.block.ModBlocks;
import com.kokuxmilsch.celestialaltar.block.entity.CelestialAltarBlockEntity;
import com.kokuxmilsch.celestialaltar.recipe.CelestialAltarRecipe;
import com.mojang.blaze3d.platform.InputConstants;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CelestialAltarRecipeCategory implements IRecipeCategory<CelestialAltarRecipe> {

    public final static ResourceLocation UID = new ResourceLocation(CelestialAltar.MODID, CelestialAltarRecipe.Type.ID);
    public final static ResourceLocation TEXTURE = new ResourceLocation(CelestialAltar.MODID, "textures/gui/celestial_altar_jei.png");

    private final Font font = Minecraft.getInstance().font;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable glowStoneCharge;

    public CelestialAltarRecipeCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.ALTAR.get()));
        this.glowStoneCharge = helper.createDrawable(TEXTURE, 176, 47, 14 ,34);
    }

    @Override
    public RecipeType<CelestialAltarRecipe> getRecipeType() {
        return JEICelestialAltarModPlugin.CELESTIAL_RITUAL_TYPE;
    }


    @Override
    public Component getTitle() {
        return Component.translatable("menu.cel_altar.celestial_altar");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void draw(CelestialAltarRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        IRecipeCategory.super.draw(recipe, recipeSlotsView, guiGraphics, mouseX, mouseY);
        guiGraphics.drawString(this.font, Component.translatable("gui.cel_altar.jei.glowstone_charge").append(Integer.toString(recipe.getGlowstone()/4)), 2 ,72, 0xf100ff);
        int scaledGlowstone = (recipe.getGlowstone()*32)/ CelestialAltarBlockEntity.maxGlowStoneCharge;
        guiGraphics.blit(TEXTURE, 9, 48-scaledGlowstone, 176, 80-scaledGlowstone, 14, scaledGlowstone, 256, 256);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, CelestialAltarRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 80, 51).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.INPUT, 152, 51).addIngredients(recipe.getIngredients().get(1));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 80, 7).addItemStack(recipe.getResultItem());
    }
}
