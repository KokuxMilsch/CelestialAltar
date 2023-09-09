package com.kokuxmilsch.celestialaltar.block.entity;

import com.kokuxmilsch.celestialaltar.block.CelestialCrystalBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class CelestialCrystalBlockEntity extends BlockEntity {

    private static double direction = 0.0D;

    public CelestialCrystalBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CELESTIAL_CRYSTAL_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public static void tick(Level level, BlockPos pos, BlockState blockState, CelestialCrystalBlockEntity celestialCrystalBlockEntity) {
        if(blockState.getValue(CelestialCrystalBlock.ACTIVATED) && !blockState.getValue(CelestialCrystalBlock.SPLIT)) {
            direction = direction + 1.5D;
            if (direction > 360.0D) {
                direction = 0.0D;
            }
            double radius = 1.0;

            double x = pos.getX() + 0.5 + Math.sin(direction / 10) * radius;
            double z = pos.getZ() + 0.5 + Math.cos(direction / 10) * radius;

            for (int i = 0; i < 10; i++) {
                ((ServerLevel) level).sendParticles(ParticleTypes.PORTAL, x, pos.getY() + 0.2, z, 1, 0.1, 0, 0.1, 0);
            }
        }
    }
}
