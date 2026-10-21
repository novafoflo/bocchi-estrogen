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
