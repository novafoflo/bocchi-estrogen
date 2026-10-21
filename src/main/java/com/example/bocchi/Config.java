package com.example.bocchi;

import net.neoforged.neoforge.common.ModConfigSpec;

public class Config {
    private static final ModConfigSpec.Builder B = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<String> TRIGGER_ITEM = B
            .comment("Item ID that turns you into Bocchi when eaten/used. CHECK THIS against Estrogen's real pill ID (hover in F3+H).")
            .define("triggerItem", "estrogen:estrogen_pill");
    public static final ModConfigSpec.IntValue DURATION_MINUTES = B
            .defineInRange("durationMinutes", 10, 1, 1440);
    public static final ModConfigSpec.DoubleValue BUST_SCALE = B
            .comment("Base bust size multiplier. Each stacked dose adds more.")
            .defineInRange("bustScale", 1.0, 0.5, 3.0);

    public static final ModConfigSpec SPEC = B.build();
}
