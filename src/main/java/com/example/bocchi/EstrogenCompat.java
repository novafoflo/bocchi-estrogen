package com.example.bocchi;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.Optional;

/** Lien optionnel avec le mod Estrogen: on cherche son effet "Girl Power" par son identifiant. */
public final class EstrogenCompat {
    private static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath("estrogen", "estrogen");

    private EstrogenCompat() {}

    /** L'effet Girl Power d'Estrogen, ou null si le mod Estrogen n'est pas installe. */
    public static Holder<MobEffect> effect() {
        Optional<Holder.Reference<MobEffect>> o = BuiltInRegistries.MOB_EFFECT.getHolder(ID);
        return o.isPresent() ? o.get() : null;
    }
}
======== FIN ========

======== NOM DU FICHIER: src/main/java/com/example/bocchi/BocchiEvents.java ========
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
