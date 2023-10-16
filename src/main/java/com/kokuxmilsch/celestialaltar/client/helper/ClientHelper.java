package com.kokuxmilsch.celestialaltar.client.helper;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;


public class ClientHelper {
    private static final RandomSource random = RandomSource.create();

    public static void addParticleServerFormat(ClientLevel level, ParticleOptions particle, double px, double py, double pz, double xDist, double yDist, double zDist, int count, int speed, boolean force) {
        if (count == 0) {
            double d0 = (speed * xDist);
            double d2 = (speed * yDist);
            double d4 = (speed * zDist);

            level.addParticle(particle, force, px, py, pz, d0, d2, d4);

        } else {
            for(int i = 0; i < count; ++i) {
                double d1 = random.nextGaussian() * xDist;
                double d3 = random.nextGaussian() * yDist;
                double d5 = random.nextGaussian() * zDist;
                double d6 = random.nextGaussian() * (double)speed;
                double d7 = random.nextGaussian() * (double)speed;
                double d8 = random.nextGaussian() * (double)speed;

                level.addParticle(particle, force, px + d1, py + d3, pz + d5, d6, d7, d8);
            }
        }
    }

    public static void addParticleServerFormat(ClientLevel level, ParticleOptions particle, double px, double py, double pz, double xDist, double yDist, double zDist, int count, int speed) {
        addParticleServerFormat(level, particle, px, py, pz, xDist, yDist, zDist, count, speed, false);
    }
}
