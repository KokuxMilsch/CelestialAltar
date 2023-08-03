package com.kokuxmilsch.celestialaltar.screen;

import com.kokuxmilsch.celestialaltar.CelestialAltar;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.client.event.RenderTooltipEvent;

public class CelestialAltarScreen extends AbstractContainerScreen<CelestialAltarMenu> {

    private static final ResourceLocation TEXTURE = new ResourceLocation(CelestialAltar.MODID, "/textures/gui/celestial_altar_gui.png");

    int x;
    int y;

    public CelestialAltarScreen(CelestialAltarMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);
        x = (width - imageWidth) / 2;
        y = (height - imageHeight) / 2;

        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderProgressArrows(pGuiGraphics, x, y);
    }

    private void renderProgressArrows(GuiGraphics pGuiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            int scaledProgress = menu.getScaledProgress();
            pGuiGraphics.blit(TEXTURE,x + 83, y + 47 - scaledProgress, 176, 43-scaledProgress, 11, scaledProgress);
        } else if(menu.isPreCrafting()) {
            pGuiGraphics.blit(TEXTURE,x + 27, y + 54, 176, 0, menu.getScaledPreRitualProgress(), 11);
            int scaledPreRitualProgress = menu.getScaledPreRitualProgress();
            pGuiGraphics.blit(TEXTURE,x + 148-scaledPreRitualProgress, y + 54, 224-scaledPreRitualProgress, 11, scaledPreRitualProgress, 11);
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

    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY-1, 0xf100ff, false);
        pGuiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY+3, 4210752, false);
        if(this.menu.isMultiblockActive() && this.menu.hasSkyAccess()) {
            pGuiGraphics.drawString(Minecraft.getInstance().font, "Altar is active", 100, this.titleLabelY-1, 0x00FF00);
        } else if(this.menu.hasSkyAccess()){
            pGuiGraphics.drawString(Minecraft.getInstance().font, "Multiblock Incomplete!", 40, 35, 0xFF0000);
        } else {
            pGuiGraphics.drawString(Minecraft.getInstance().font, "Altar needs clear sky above it!", 10, -10, 0xFF0000);
        }
    }
}
