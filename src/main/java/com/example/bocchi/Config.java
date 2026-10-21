package com.example.bocchi;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder B = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<String> TRIGGER_ITEM = B
            .comment("Item qui donne l'effet quand on le mange. Pilule d'Estrogen par defaut.")
            .define("triggerItem", "estrogen:estrogen_pill");
    public static final ModConfigSpec.IntValue DURATION_MINUTES = B
            .defineInRange("durationMinutes", 10, 1, 1440);
    public static final ModConfigSpec.BooleanValue FOLLOW_ESTROGEN = B
            .comment("Si Estrogen est installe: l'effet Girl Power d'Estrogen declenche aussi le corps Bocchi.",
                     "Attention: Estrogen dessine alors aussi sa propre poitrine (donc deux).")
            .define("followEstrogen", false);

    public static final ModConfigSpec SPEC = B.build();
}
