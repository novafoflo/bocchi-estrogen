package com.example.bocchi;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = BocchiMod.MODID, value = Dist.CLIENT)
public class BocchiKeys {
    public static final String CATEGORY = "key.categories.bocchi_estrogen";
    public static final KeyMapping GROW = new KeyMapping("key.bocchi_estrogen.grow", GLFW.GLFW_KEY_PAGE_UP, CATEGORY);
    public static final KeyMapping SHRINK = new KeyMapping("key.bocchi_estrogen.shrink", GLFW.GLFW_KEY_PAGE_DOWN, CATEGORY);

    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post e) {
        Minecraft mc = Minecraft.getInstance();
        while (GROW.consumeClick()) change(mc, 0.1);
        while (SHRINK.consumeClick()) change(mc, -0.1);
    }

    private static void change(Minecraft mc, double delta) {
        double v = ClientConfig.BUST_SCALE.get() + delta;
        v = Math.round(Math.max(0.5, Math.min(3.0, v)) * 10.0) / 10.0;
        ClientConfig.BUST_SCALE.set(v);
        ClientConfig.BUST_SCALE.save();
        if (mc.player != null) mc.player.displayClientMessage(Component.literal("Taille : " + v), true);
    }
}
