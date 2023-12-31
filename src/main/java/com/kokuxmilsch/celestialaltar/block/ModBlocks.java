package com.kokuxmilsch.celestialaltar.block;

import com.kokuxmilsch.celestialaltar.CelestialAltar;
import com.kokuxmilsch.celestialaltar.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CelestialAltar.MODID);



    public static final RegistryObject<Block> ALTAR = registerBlock("altar", () -> new CelestialAltarBlock(BlockBehaviour.Properties.copy(Blocks.SCULK_SHRIEKER)));

    public static final RegistryObject<Block> CELESTIAL_CRYSTAL = registerBlock("celestial_crystal", () -> new CelestialCrystalBlock(BlockBehaviour.Properties.copy(Blocks.AMETHYST_BLOCK).requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> GLOW_STONE_EVAPORATOR = registerBlock("glowstone_evaporator", () -> new GlowStoneEvaporatorBlock(BlockBehaviour.Properties.copy(Blocks.RESPAWN_ANCHOR).requiresCorrectToolForDrops()));

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }


    private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
