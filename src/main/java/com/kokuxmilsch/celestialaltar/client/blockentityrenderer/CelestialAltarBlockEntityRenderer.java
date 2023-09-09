package com.kokuxmilsch.celestialaltar.client.blockentityrenderer;

import com.kokuxmilsch.celestialaltar.block.ModBlocks;
import com.kokuxmilsch.celestialaltar.block.entity.CelestialAltarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.EmptyModel;
import net.minecraftforge.client.model.data.ModelData;

@OnlyIn(Dist.CLIENT)
public class CelestialAltarBlockEntityRenderer implements BlockEntityRenderer<CelestialAltarBlockEntity> {

    private final BlockEntityRendererProvider.Context context;

    public CelestialAltarBlockEntityRenderer(BlockEntityRendererProvider.Context pContext) {
        this.context = pContext;
    }


    @Override
    public void render(CelestialAltarBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        //context.getBlockRenderDispatcher().renderSingleBlock(ModBlocks.ALTAR.get().defaultBlockState(), pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pBlockEntity.getModelData(), RenderType.cutout());
        if(pBlockEntity.getClientProgress() >= 120 && pBlockEntity.getClientProgress() <= 399) {
            float[] afloat;
            afloat = DyeColor.CYAN.getTextureDiffuseColors();
            BeaconRenderer.renderBeaconBeam(pPoseStack, pBuffer, BeaconRenderer.BEAM_LOCATION, 1, 1, pBlockEntity.getLevel().getGameTime(), 0, pBlockEntity.getLevel().getMaxBuildHeight() - pBlockEntity.getBlockPos().getY(), afloat, 0.2f, 0.5f);
        }

    }


    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRenderOffScreen(CelestialAltarBlockEntity pBlockEntity) {
        return true;
    }
}
