package com.kokuxmilsch.celestialaltar.item;

import com.kokuxmilsch.celestialaltar.CelestialAltar;
import com.kokuxmilsch.celestialaltar.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CelestialAltar.MODID);

    public static final RegistryObject<CreativeModeTab> CELESTIAL_ALTAR_TAB = CREATIVE_MODE_TABS.register("celestial_altar_tab", () -> CreativeModeTab.builder()
            .icon(() -> Items.SCULK_SHRIEKER.getDefaultInstance())
            .title(Component.translatable("creativetab.celestial_altar_tab"))
            .displayItems((pParameters, pOutput)-> {
                pOutput.accept(ModItems.LIGHTNING_STEEL.get());
                pOutput.accept(ModItems.SKY_CRYSTAL_SHARD.get());
                pOutput.accept(ModItems.NIGHT_SKY_CRYSTAL_SHARD.get());
                pOutput.accept(ModItems.ENCHANTED_EYE_OF_ENDER.get());
                pOutput.accept(ModBlocks.ALTAR.get());
                pOutput.accept(ModBlocks.CELESTIAL_CRYSTAL.get());

                pOutput.accept(Items.CRYING_OBSIDIAN);
                pOutput.accept(Items.SCULK);
                pOutput.accept(Items.DEEPSLATE_BRICK_WALL);
                pOutput.accept(Items.RESPAWN_ANCHOR);
                pOutput.accept(Items.END_ROD);
                pOutput.accept(Items.SMOOTH_QUARTZ_STAIRS);
                pOutput.accept(Items.EMERALD_BLOCK);
                pOutput.accept(Items.GOLD_BLOCK);
                pOutput.accept(Items.DIAMOND_BLOCK);
                pOutput.accept(Items.BEACON);
                pOutput.accept(Items.SCULK_SHRIEKER);
            })
            .build()
    );

    public static void register(IEventBus modEventbus) {
        CREATIVE_MODE_TABS.register(modEventbus);
    }
}
