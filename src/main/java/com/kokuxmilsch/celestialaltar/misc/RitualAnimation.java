package com.kokuxmilsch.celestialaltar.misc;

import com.kokuxmilsch.celestialaltar.block.CelestialCrystalBlock;
import com.kokuxmilsch.celestialaltar.block.GlowStoneEvaporatorBlock;
import com.kokuxmilsch.celestialaltar.block.entity.CelestialAltarBlockEntity;
import com.kokuxmilsch.celestialaltar.client.helper.ClientHelper;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import org.apache.logging.log4j.core.jmx.Server;

public class RitualAnimation {
    public static void animateServer(ServerLevel pLevel, BlockPos pPos, int progress) {
        //max Progress = 400t
        if (progress == 1) {
            CelestialAltarBlockEntity.playSound(pLevel, SoundEvents.BEACON_POWER_SELECT, pPos, 0.8f, 2.5f);
        }
        if (progress == 30) {
            pLevel.setBlock(pPos.offset(2, 2, 2), pLevel.getBlockState(pPos.offset(2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 1), 2);
            pLevel.setBlock(pPos.offset(2, 2, -2), pLevel.getBlockState(pPos.offset(2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 1), 2);
            pLevel.setBlock(pPos.offset(-2, 2, 2), pLevel.getBlockState(pPos.offset(-2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 1), 2);
            pLevel.setBlock(pPos.offset(-2, 2, -2), pLevel.getBlockState(pPos.offset(-2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 1), 2);
            CelestialAltarBlockEntity.playSound(pLevel, SoundEvents.RESPAWN_ANCHOR_CHARGE, pPos, 1f, 1f);
        }
        if (progress == 40) {
            pLevel.setBlock(pPos.offset(2, 2, 2), pLevel.getBlockState(pPos.offset(2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 2), 2);
            pLevel.setBlock(pPos.offset(2, 2, -2), pLevel.getBlockState(pPos.offset(2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 2), 2);
            pLevel.setBlock(pPos.offset(-2, 2, 2), pLevel.getBlockState(pPos.offset(-2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 2), 2);
            pLevel.setBlock(pPos.offset(-2, 2, -2), pLevel.getBlockState(pPos.offset(-2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 2), 2);
            CelestialAltarBlockEntity.playSound(pLevel, SoundEvents.RESPAWN_ANCHOR_CHARGE, pPos, 1f, 1f);
        }
        if (progress == 50) {
            pLevel.setBlock(pPos.offset(2, 2, 2), pLevel.getBlockState(pPos.offset(2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 3), 2);
            pLevel.setBlock(pPos.offset(2, 2, -2), pLevel.getBlockState(pPos.offset(2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 3), 2);
            pLevel.setBlock(pPos.offset(-2, 2, 2), pLevel.getBlockState(pPos.offset(-2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 3), 2);
            pLevel.setBlock(pPos.offset(-2, 2, -2), pLevel.getBlockState(pPos.offset(-2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 3), 2);
            CelestialAltarBlockEntity.playSound(pLevel, SoundEvents.RESPAWN_ANCHOR_CHARGE, pPos, 1f, 1f);
        }
        if (progress == 60) {
            pLevel.setBlock(pPos.offset(2, 2, 2), pLevel.getBlockState(pPos.offset(2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 4), 2);
            pLevel.setBlock(pPos.offset(2, 2, -2), pLevel.getBlockState(pPos.offset(2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 4), 2);
            pLevel.setBlock(pPos.offset(-2, 2, 2), pLevel.getBlockState(pPos.offset(-2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 4), 2);
            pLevel.setBlock(pPos.offset(-2, 2, -2), pLevel.getBlockState(pPos.offset(-2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 4), 2);
            CelestialAltarBlockEntity.playSound(pLevel, SoundEvents.RESPAWN_ANCHOR_CHARGE, pPos, 1f, 1f);
        }
        if (progress > 60 && progress < 110) {
            //beam animation
            //double increment = (double) Math.round((((double) (progress - 60)) * 0.03333D) * 100) / 100;
            //BlockPos particlePos = pPos.offset(2, 4, 2);
            //pLevel.sendParticles(ParticleTypes.END_ROD, particlePos.getX() + 0.5 - increment, particlePos.getY() + (increment / 2), particlePos.getZ() + 0.5 - increment, 1, 0, 0, 0, 0);

            //particlePos = pPos.offset(2, 4, -2);
            //pLevel.sendParticles(ParticleTypes.END_ROD, particlePos.getX() + 0.5 - increment, particlePos.getY() + (increment / 2), particlePos.getZ() + 0.5 + increment, 1, 0, 0, 0, 0);

            //particlePos = pPos.offset(-2, 4, 2);
            //pLevel.sendParticles(ParticleTypes.END_ROD, particlePos.getX() + 0.5 + increment, particlePos.getY() + (increment / 2), particlePos.getZ() + 0.5 - increment, 1, 0, 0, 0, 0);

            //particlePos = pPos.offset(-2, 4, -2);
            //pLevel.sendParticles(ParticleTypes.END_ROD, particlePos.getX() + 0.5 + increment, particlePos.getY() + (increment / 2), particlePos.getZ() + 0.5 + increment, 1, 0, 0, 0, 0);

            CelestialAltarBlockEntity.playSound(pLevel, SoundEvents.BEACON_POWER_SELECT, pPos, 2f, 1f);
        }
        if (progress == 115) {
            CelestialAltarBlockEntity.playSound(pLevel, SoundEvents.BEACON_ACTIVATE, pPos, 1.2f, 3f);
            //pLevel.sendParticles(ParticleTypes.EXPLOSION, pPos.getX() + 0.5, pPos.getY() + 4.5, pPos.getZ() + 0.5, 4, 0, 0, 0, 1);

        }
        if (progress >= 110 && progress <= 120) {
            //pLevel.sendParticles(ParticleTypes.FLASH, pPos.getX() + 0.5, pPos.getY() + 4.5, pPos.getZ() + 0.5, 1, 0, 0, 0, 0);
        }
        if (progress == 120) {
            pLevel.setBlock(pPos.above(4), pLevel.getBlockState(pPos.above(4)).trySetValue(CelestialCrystalBlock.SPLIT, true), 2);

            CelestialAltarBlockEntity.playSound(pLevel, SoundEvents.BEACON_POWER_SELECT, pPos, 1f, 2.5f);
            for (int i = 0; i < 10; i++) {
                CelestialAltarBlockEntity.playSound(pLevel, SoundEvents.PORTAL_AMBIENT, pPos, 0.5f, 2f);
            }
        }
        if (progress >= 120 && progress < 300) {
            //pLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, pPos.getX() + 0.5, pPos.getY() + 1.5, pPos.getZ() + 0.5, 10, 0.4, 0.3, 0.4, 0);
        }
        if (progress == 300) {
            CelestialAltarBlockEntity.playSound(pLevel, SoundEvents.ENDER_DRAGON_GROWL, pPos, 0.5f, 1f);
            CelestialAltarBlockEntity.playSound(pLevel, SoundEvents.BEACON_DEACTIVATE, pPos, 0.5f, 1f);
        }
        if (progress >= 300 && progress < 350 && progress % 2 == 0) {
            //pLevel.sendParticles(ParticleTypes.SONIC_BOOM, pPos.getX() + 0.5, pPos.getY() + 0.5 + (double) ((progress - 300) / 2), pPos.getZ() + 0.5, 2, 0, 0, 0, 1);
        }
        if (progress == 280) {
            for (int i = 0; i < 4; i++) {
                CelestialAltarBlockEntity.playSound(pLevel, SoundEvents.BELL_RESONATE, pPos, 0.5f, 4);
            }
        }
        if (progress == 399) {
            CelestialAltarBlockEntity.playSound(pLevel, SoundEvents.RESPAWN_ANCHOR_SET_SPAWN, pPos, 2f, 1f);
            CelestialAltarBlockEntity.playSound(pLevel, SoundEvents.BEACON_DEACTIVATE, pPos, 0.9f, 2f);
            CelestialAltarBlockEntity.playSound(pLevel, SoundEvents.LIGHTNING_BOLT_THUNDER, pPos, 0.5f, 3f);
            CelestialAltarBlockEntity.playSound(pLevel, SoundEvents.END_PORTAL_SPAWN, pPos, 0.5f, 3f);
            //pLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pPos.getX() + 0.5, pPos.getY() + 4.5, pPos.getZ() + 0.5, 1, 0, 0, 0, 1);

            pLevel.setBlock(pPos.offset(2, 2, 2), pLevel.getBlockState(pPos.offset(2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 0), 2);
            pLevel.setBlock(pPos.offset(2, 2, -2), pLevel.getBlockState(pPos.offset(2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 0), 2);
            pLevel.setBlock(pPos.offset(-2, 2, 2), pLevel.getBlockState(pPos.offset(-2, 2, 2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 0), 2);
            pLevel.setBlock(pPos.offset(-2, 2, -2), pLevel.getBlockState(pPos.offset(-2, 2, -2)).trySetValue(GlowStoneEvaporatorBlock.CHARGE, 0), 2);

            pLevel.setBlock(pPos.above(4), pLevel.getBlockState(pPos.above(4)).trySetValue(CelestialCrystalBlock.SPLIT, false), 2);
        }
    }

    public static void animateClient(ClientLevel pClientLevel, BlockPos pPos, int progress) {
        //max Progress = 400t
        if (progress > 60 && progress < 110) {
            //beam animation
            double increment = (double) Math.round((((double) (progress - 60)) * 0.03333D) * 100) / 100;
            BlockPos particlePos = pPos.offset(2, 4, 2);
            ClientHelper.addParticleServerFormat(pClientLevel, ParticleTypes.END_ROD, particlePos.getX() + 0.5 - increment, particlePos.getY() + (increment / 2), particlePos.getZ() + 0.5 - increment, 0, 0, 0, 1, 0);

            particlePos = pPos.offset(2, 4, -2);
            ClientHelper.addParticleServerFormat(pClientLevel, ParticleTypes.END_ROD, particlePos.getX() + 0.5 - increment, particlePos.getY() + (increment / 2), particlePos.getZ() + 0.5 + increment, 0, 0, 0, 1, 0);

            particlePos = pPos.offset(-2, 4, 2);
            ClientHelper.addParticleServerFormat(pClientLevel, ParticleTypes.END_ROD, particlePos.getX() + 0.5 + increment, particlePos.getY() + (increment / 2), particlePos.getZ() + 0.5 - increment, 0, 0, 0, 1, 0);

            particlePos = pPos.offset(-2, 4, -2);
            ClientHelper.addParticleServerFormat(pClientLevel, ParticleTypes.END_ROD, particlePos.getX() + 0.5 + increment, particlePos.getY() + (increment / 2), particlePos.getZ() + 0.5 + increment, 0, 0, 0, 1, 0);
        }
        if (progress == 115) {
            for (int i = 0; i < 4; i++) {
                ClientHelper.addParticleServerFormat(pClientLevel, ParticleTypes.EXPLOSION, pPos.getX() + 0.5, pPos.getY() + 4.5, pPos.getZ() + 0.5, 4, 0, 0, 0, 1);
            }
        }
        if (progress >= 110 && progress <= 120) {
            ClientHelper.addParticleServerFormat(pClientLevel, ParticleTypes.FLASH, pPos.getX() + 0.5, pPos.getY() + 4.5, pPos.getZ() + 0.5, 0, 0, 0, 1, 0);
        }
        if (progress >= 120 && progress < 300) {
            ClientHelper.addParticleServerFormat(pClientLevel, ParticleTypes.ENCHANTED_HIT, pPos.getX() + 0.5, pPos.getY() + 1.5, pPos.getZ() + 0.5, 0.3, 0.3, 0.4, 10, 0);
        }
        if (progress >= 300 && progress < 350 && progress % 2 == 0) {
            ClientHelper.addParticleServerFormat(pClientLevel, ParticleTypes.SONIC_BOOM, pPos.getX() + 0.5, pPos.getY() + 0.5 + (double) ((progress - 300) / 2), pPos.getZ() + 0.5, 0, 0, 0, 2, 1);
        }
        if (progress == 399) {
            ClientHelper.addParticleServerFormat(pClientLevel, ParticleTypes.EXPLOSION_EMITTER, pPos.getX() + 0.5, pPos.getY() + 4.5, pPos.getZ() + 0.5, 0, 0, 0, 1, 1);
        }
    }
}
