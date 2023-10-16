package com.kokuxmilsch.celestialaltar.item;

import com.kokuxmilsch.celestialaltar.CelestialAltar;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CelestialAltar.MODID);


    public static final RegistryObject<Item> LIGHTNING_STEEL = ITEMS.register("lightning_steel", () -> new GlowingItem(new Item.Properties().rarity(Rarity.RARE)));

    public static final RegistryObject<Item> SKY_CRYSTAL_SHARD = ITEMS.register("sky_crystal_shard", () -> new GlowingItem(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> NIGHT_SKY_CRYSTAL_SHARD = ITEMS.register("night_sky_crystal_shard", () -> new GlowingItem(new Item.Properties().rarity(Rarity.EPIC)));

    //public static final RegistryObject<Item> ENCHANTED_EYE_OF_ENDER = ITEMS.register("enchanted_eye_of_ender", () -> new EnchantedEyeOfEnder(new Item.Properties().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> SUNNY_RITUAL = ITEMS.register("sunny_ritual", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));

    public static final RegistryObject<Item> RAIN_RITUAL = ITEMS.register("rain_ritual", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));

    public static final RegistryObject<Item> THUNDER_RITUAL = ITEMS.register("thunder_ritual", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));

    public static final RegistryObject<Item> DAY_RITUAL = ITEMS.register("day_ritual", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));

    public static final RegistryObject<Item> NIGHT_RITUAL = ITEMS.register("night_ritual", () -> new Item(new Item.Properties().rarity(Rarity.UNCOMMON).stacksTo(1)));


    public static void register(IEventBus modEventBus) {
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
    }

}
