package com.kokuxmilsch.celestialaltar.screen;

import com.kokuxmilsch.celestialaltar.CelestialAltar;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {

    public static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CelestialAltar.MODID);

    public static final RegistryObject<MenuType<CelestialAltarMenu>> CELESTIAL_ALTAR_MENU = registerMenuType(CelestialAltarMenu::new, "celestial_altar_menu");


    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name) {
        return MENU_TYPES.register(name, () -> IForgeMenuType.create(factory));
    }

    public static void register(IEventBus modEventBus) {
        MENU_TYPES.register(modEventBus);
    }

}
