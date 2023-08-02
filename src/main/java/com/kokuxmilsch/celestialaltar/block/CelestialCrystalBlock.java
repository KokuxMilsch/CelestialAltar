package com.kokuxmilsch.celestialaltar.block;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class CelestialCrystalBlock extends Block {
    public CelestialCrystalBlock(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void animateTick(BlockState pState, Level pLevel, BlockPos pPos, RandomSource pRandom) {
        //circling particle effect
        super.animateTick(pState, pLevel, pPos, pRandom);
    }
}
