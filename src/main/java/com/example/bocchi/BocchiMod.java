package com.example.bocchi;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(BocchiMod.MODID)
public class BocchiMod {
    public static final String MODID = "bocchi_estrogen";

    public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);

    public static final DeferredHolder<MobEffect, BocchiEffect> BOCCHI = EFFECTS.register("bocchi", BocchiEffect::new);

    // Fallback pill so the mod works even if the Estrogen item ID in the config is wrong.
    public static final DeferredItem<Item> BOCCHI_PILL = ITEMS.registerSimpleItem("bocchi_pill",
            new Item.Properties().food(new FoodProperties.Builder().nutrition(0).saturationModifier(0f).alwaysEdible().build()));

    public BocchiMod(IEventBus modBus, ModContainer container) {
        EFFECTS.register(modBus);
        ITEMS.register(modBus);
        modBus.addListener(BocchiMod::tabs);
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    private static void tabs(BuildCreativeModeTabContentsEvent e) {
        if (e.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) e.accept(BOCCHI_PILL);
    }
}
