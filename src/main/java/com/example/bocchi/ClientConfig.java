package com.example.bocchi;

import net.neoforged.neoforge.common.ModConfigSpec;

/** Reglages visuels (cote client): config/bocchi_estrogen-client.toml */
public class ClientConfig {
    private static final ModConfigSpec.Builder B = new ModConfigSpec.Builder();

    public static final ModConfigSpec.DoubleValue BUST_SCALE = B
            .comment("Taille de la poitrine (0.5 a 3.0). Se change aussi en jeu avec Page Haut / Page Bas.")
            .defineInRange("bustScale", 1.3, 0.5, 3.0);
    public static final ModConfigSpec.DoubleValue SIZE_PER_DOSE = B
            .comment("Agrandissement ajoute par dose supplementaire (0 = aucun).")
            .defineInRange("sizePerDose", 0.25, 0.0, 1.0);
    public static final ModConfigSpec.BooleanValue PHYSICS = B
            .comment("Physique: la poitrine rebondit quand on marche, saute ou tombe.")
            .define("physics", true);
    public static final ModConfigSpec.DoubleValue PHYSICS_STRENGTH = B
            .comment("Force de la physique (0 = aucune, 1 = normal, 3 = tres fort).")
            .defineInRange("physicsStrength", 1.0, 0.0, 3.0);
    public static final ModConfigSpec.BooleanValue SHOW_HAIR = B
            .comment("Afficher les cheveux roses de Bocchi.")
            .define("showPinkHair", false);
    public static final ModConfigSpec.BooleanValue SHOW_JACKET = B
            .comment("Afficher la veste rose de Bocchi.")
            .define("showPinkJacket", false);
    public static final ModConfigSpec.BooleanValue HIDE_UNDER_ARMOR = B
            .comment("Cacher la poitrine quand un plastron est equipe (evite que l'armure la traverse).")
            .define("hideUnderChestArmor", true);

    public static final ModConfigSpec SPEC = B.build();
}
