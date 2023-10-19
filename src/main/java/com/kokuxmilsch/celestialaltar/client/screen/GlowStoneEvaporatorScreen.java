package com.kokuxmilsch.celestialaltar.client.screen;

import com.kokuxmilsch.celestialaltar.CelestialAltar;
import com.kokuxmilsch.celestialaltar.item.ModItems;
import com.kokuxmilsch.celestialaltar.menu.GlowStoneEvaporatorMenu;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;

public class GlowStoneEvaporatorScreen extends AbstractContainerScreen<GlowStoneEvaporatorMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(CelestialAltar.MODID, "/textures/gui/glowstone_evaporator_gui.png");

    public GlowStoneEvaporatorScreen(GlowStoneEvaporatorMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrows(pGuiGraphics, x, y);
        renderResultItem(pGuiGraphics, x, y);
    }

    private void renderResultItem(GuiGraphics pGuiGraphics, int pX, int pY) {
        int x = pX+80;
        int y = pY+7;
        switch (menu.getRitualType()) {
            case SUNNY -> pGuiGraphics.renderItem(ModItems.SUNNY_RITUAL.get().getDefaultInstance(), x, y);
            case RAIN -> pGuiGraphics.renderItem(ModItems.RAIN_RITUAL.get().getDefaultInstance(), x, y);
            case THUNDER -> pGuiGraphics.renderItem(ModItems.THUNDER_RITUAL.get().getDefaultInstance(), x, y);
            case DAY -> pGuiGraphics.renderItem(ModItems.DAY_RITUAL.get().getDefaultInstance(), x, y);
            case NIGHT -> pGuiGraphics.renderItem(ModItems.NIGHT_RITUAL.get().getDefaultInstance(), x, y);
            case EMPTY -> pGuiGraphics.renderItem(Items.BARRIER.getDefaultInstance(), x, y);
        }
    }

    private void renderProgressArrows(GuiGraphics pGuiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            int scaledProgress = menu.getScaledProgress();
            pGuiGraphics.blit(TEXTURE,x + 83, y + 47 - scaledProgress, 176, 43-scaledProgress, 11, scaledProgress);
        }
        int scaledGlowStoneCharge = menu.getScaledGlowStoneCharge();
        pGuiGraphics.blit(TEXTURE,x + 9, y + 48-scaledGlowStoneCharge, 176, 80-scaledGlowStoneCharge, 14, scaledGlowStoneCharge);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }
}
