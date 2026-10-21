package com.example.bocchi;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.WeakHashMap;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;

/** Draws pink hair, pink jacket and a bust on any player who has the Bocchi effect. */
public class BocchiLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(BocchiMod.MODID, "bocchi"), "main");
    private static final ResourceLocation TEX =
            ResourceLocation.fromNamespaceAndPath(BocchiMod.MODID, "textures/entity/bocchi.png");

    private final ModelPart hair, jacket, sleeveR, sleeveL, bust;
    private final float bustBaseY;
    private final Map<AbstractClientPlayer, Phys> phys = new WeakHashMap<>();

    /** Etat du ressort de la poitrine pour un joueur. */
    private static class Phys {
        int lastTick = -1;
        float lastDy, off, prevOff, vel;
    }

    public BocchiLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent, EntityModelSet models) {
        super(parent);
        ModelPart root = models.bakeLayer(LAYER);
        hair = root.getChild("hair");
        jacket = root.getChild("jacket");
        sleeveR = root.getChild("sleeve_r");
        sleeveL = root.getChild("sleeve_l");
        bust = root.getChild("bust");
        bustBaseY = bust.y;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();
        root.addOrReplaceChild("hair", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4.5F, -8.8F, -4.5F, 9F, 2F, 9F)      // fringe/top
                .addBox(-4.5F, -8.8F, 3.5F, 9F, 14F, 1.5F)    // long back hair
                .addBox(4F, -8.5F, -4.5F, 1.2F, 6F, 9F)       // side
                .addBox(-5.2F, -8.5F, -4.5F, 1.2F, 6F, 9F),   // side
                PartPose.ZERO);
        root.addOrReplaceChild("jacket", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-4F, 0F, -2F, 8F, 10F, 4F, new CubeDeformation(0.5F)), PartPose.ZERO);
        root.addOrReplaceChild("sleeve_r", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-3F, -2F, -2F, 4F, 10F, 4F, new CubeDeformation(0.4F)), PartPose.ZERO);
        root.addOrReplaceChild("sleeve_l", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-1F, -2F, -2F, 4F, 10F, 4F, new CubeDeformation(0.4F)), PartPose.ZERO);
        root.addOrReplaceChild("bust", CubeListBuilder.create().texOffs(19, 20)
                .addBox(-3.6F, -1.6F, -1.8F, 3.4F, 3.2F, 1.8F)
                .texOffs(22, 20)
                .addBox(0.2F, -1.6F, -1.8F, 3.4F, 3.2F, 1.8F),
                PartPose.offset(0F, 3.5F, -2.5F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void render(PoseStack ps, MultiBufferSource buffer, int light, AbstractClientPlayer p,
                       float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        MobEffectInstance inst = p.getEffect(BocchiMod.BOCCHI);
        int amp = inst != null ? inst.getAmplifier() : -1;
        if (Config.FOLLOW_ESTROGEN.get()) {
            Holder<MobEffect> girlPower = EstrogenCompat.effect();
            MobEffectInstance est = girlPower != null ? p.getEffect(girlPower) : null;
            if (est != null) amp = Math.max(amp, est.getAmplifier());
        }
        if (amp < 0 || p.isInvisible()) return;

        PlayerModel<AbstractClientPlayer> m = getParentModel();
        int ov = OverlayTexture.NO_OVERLAY;

        if (ClientConfig.SHOW_HAIR.get() || ClientConfig.SHOW_JACKET.get()) {
            VertexConsumer pink = buffer.getBuffer(RenderType.entityCutoutNoCull(TEX));
            if (ClientConfig.SHOW_HAIR.get()) draw(ps, m.head, hair, pink, light, ov);
            if (ClientConfig.SHOW_JACKET.get()) {
                draw(ps, m.body, jacket, pink, light, ov);
                draw(ps, m.rightArm, sleeveR, pink, light, ov);
                draw(ps, m.leftArm, sleeveL, pink, light, ov);
            }
        }

        boolean armor = ClientConfig.HIDE_UNDER_ARMOR.get() && !p.getItemBySlot(EquipmentSlot.CHEST).isEmpty();
        if (!armor) renderBust(ps, buffer, light, p, limbSwing, limbSwingAmount, partialTick, amp);
    }

    private void renderBust(PoseStack ps, MultiBufferSource buffer, int light, AbstractClientPlayer p,
                            float limbSwing, float limbSwingAmount, float partialTick, int amp) {
        float s = (float) (ClientConfig.BUST_SCALE.get() * (1.0 + ClientConfig.SIZE_PER_DOSE.get() * amp));
        bust.xScale = bust.yScale = 1F + (s - 1F) * 0.5F;
        bust.zScale = s;

        float y = 0F;
        if (ClientConfig.PHYSICS.get()) {
            Phys ph = phys.computeIfAbsent(p, k -> new Phys());
            if (ph.lastTick != p.tickCount) {           // un pas de simulation par tick
                ph.lastTick = p.tickCount;
                float dy = (float) (p.getY() - p.yo);
                float dv = dy - ph.lastDy;               // acceleration verticale du corps
                ph.lastDy = dy;
                ph.prevOff = ph.off;
                ph.vel += -0.35F * ph.off - 0.12F * ph.vel + dv * 6F;
                ph.off = Mth.clamp(ph.off + ph.vel, -1.5F, 1.5F);
            }
            float k = ClientConfig.PHYSICS_STRENGTH.get().floatValue();
            float spring = Mth.lerp(partialTick, ph.prevOff, ph.off);
            float walk = Mth.sin(limbSwing * 1.3324F) * limbSwingAmount * 0.5F;   // rebond de la marche
            y = (spring + walk) * k;
        }
        bust.y = bustBaseY + y;
        bust.xRot = y * 0.08F;

        // la poitrine prend la couleur de la peau du joueur
        VertexConsumer skin = buffer.getBuffer(RenderType.entityCutoutNoCull(p.getSkin().texture()));
        draw(ps, getParentModel().body, bust, skin, light, OverlayTexture.NO_OVERLAY);
    }

    private static void draw(PoseStack ps, ModelPart anchor, ModelPart part, VertexConsumer vc, int light, int ov) {
        ps.pushPose();
        anchor.translateAndRotate(ps);
        part.render(ps, vc, light, ov);
        ps.popPose();
    }
}
