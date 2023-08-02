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

    public CelestialAltarScreen(CelestialAltarMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
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

        renderProgressArrow(pGuiGraphics, x, y);
    }

    private void renderProgressArrow(GuiGraphics pGuiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            pGuiGraphics.blit(TEXTURE,x + 15, y + 13, 3, 167, menu.getScaledProgress(), 4);
        }
        pGuiGraphics.blit(TEXTURE,x + 12, y + 77, 1, 171, menu.getScaledGlowStoneCharge(), 4);
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics pGuiGraphics, int pX, int pY) {
        super.renderTooltip(pGuiGraphics, pX, pY);
        if(this.menu.isMultiblockActive()) {
            pGuiGraphics.drawString(Minecraft.getInstance().font, "Altar is active", pX, pY, 0x00FF00);
        } else {
            pGuiGraphics.drawString(Minecraft.getInstance().font, "Multiblock Incomplete!", pX, pY, 0xFF0000);
        }
    }
}
