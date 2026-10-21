package com.example.bocchi;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BocchiEffect extends MobEffect {
    public BocchiEffect() {
        super(MobEffectCategory.NEUTRAL, 0xF29BC0);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }

    // Social anxiety: faster mining when alone, slowed down when other players are close.
    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide) return true;
        boolean alone = entity.level()
                .getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(10), p -> p != entity)
                .isEmpty();
        if (alone) {
            entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 60, Math.min(amplifier, 2), true, false));
        } else {
            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0, true, false));
        }
        return true;
    }
}
