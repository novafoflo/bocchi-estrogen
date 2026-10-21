package com.example.bocchi;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;

@EventBusSubscriber(modid = BocchiMod.MODID)
public class BocchiEvents {
    @SubscribeEvent
    public static void onFinishUse(LivingEntityUseItemEvent.Finish e) {
        LivingEntity who = e.getEntity();
        if (who.level().isClientSide) return;
        String id = BuiltInRegistries.ITEM.getKey(e.getItem().getItem()).toString();
        if (id.equals(Config.TRIGGER_ITEM.get()) || id.equals(BocchiMod.MODID + ":bocchi_pill")) {
            MobEffectInstance cur = who.getEffect(BocchiMod.BOCCHI);
            int amp = cur == null ? 0 : Math.min(cur.getAmplifier() + 1, 3); // doses stack
            who.addEffect(new MobEffectInstance(BocchiMod.BOCCHI, Config.DURATION_MINUTES.get() * 1200, amp));
        }
    }
}
