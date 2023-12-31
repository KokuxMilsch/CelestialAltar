package com.kokuxmilsch.celestialaltar.block.entity;

import com.kokuxmilsch.celestialaltar.CelestialAltar;
import com.kokuxmilsch.celestialaltar.block.ModBlocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CelestialAltar.MODID);

    public static final RegistryObject<BlockEntityType<CelestialAltarBlockEntity>> ALTAR_BLOCK_ENTITY = BLOCK_ENTITIES.register("celestial_altar_block_entity",
            () -> BlockEntityType.Builder.of(CelestialAltarBlockEntity::new, ModBlocks.ALTAR.get()).build(null));

    public static final RegistryObject<BlockEntityType<CelestialCrystalBlockEntity>> CELESTIAL_CRYSTAL_BLOCK_ENTITY = BLOCK_ENTITIES.register("celestial_crystal_block_entity",
            () -> BlockEntityType.Builder.of(CelestialCrystalBlockEntity::new, ModBlocks.CELESTIAL_CRYSTAL.get()).build(null));

    public static final RegistryObject<BlockEntityType<GlowStoneEvaporatorBlockEntity>> GLOWSTONE_EVAPORATOR_BLOCK_ENTITY = BLOCK_ENTITIES.register("glostone_evaporator_block_entity",
            () -> BlockEntityType.Builder.of(GlowStoneEvaporatorBlockEntity::new, ModBlocks.GLOW_STONE_EVAPORATOR.get()).build(null));



    public static void register(IEventBus modEventbus) {
        BLOCK_ENTITIES.register(modEventbus);
    }
}
