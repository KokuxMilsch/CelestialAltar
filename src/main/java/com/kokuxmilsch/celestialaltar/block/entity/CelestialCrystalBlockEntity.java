package com.kokuxmilsch.celestialaltar.block.entity;

import com.kokuxmilsch.celestialaltar.block.CelestialCrystalBlock;
import com.kokuxmilsch.celestialaltar.client.helper.ClientHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CelestialCrystalBlockEntity extends BlockEntity {

    private static double direction = 0.0D;

    public CelestialCrystalBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CELESTIAL_CRYSTAL_BLOCK_ENTITY.get(), pPos, pBlockState);
    }


    public static void clientTick(Level level, BlockPos pos, BlockState blockState, CelestialCrystalBlockEntity celestialCrystalBlockEntity) {
        if(blockState.getValue(CelestialCrystalBlock.ACTIVATED) && !blockState.getValue(CelestialCrystalBlock.SPLIT)) {
            direction = direction + 1.5D;
            if (direction > 360.0D) {
                direction = 0.0D;
            }
            double radius = 1.0;

            double x = pos.getX() + 0.5 + Math.sin(direction / 10) * radius;
            double z = pos.getZ() + 0.5 + Math.cos(direction / 10) * radius;

            if(level instanceof ClientLevel) {
                ClientHelper.addParticleServerFormat((ClientLevel) level, ParticleTypes.PORTAL, x, pos.getY(), z, 0.1, 0, 0.1, 10, 0);
            }
        }
    }
}
