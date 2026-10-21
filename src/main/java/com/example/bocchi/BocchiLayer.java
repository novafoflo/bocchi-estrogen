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
import net.minecraft.world.effect.MobEffectInstance;

/** Draws pink hair, pink jacket and a bust on any player who has the Bocchi effect. */
public class BocchiLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(BocchiMod.MODID, "bocchi"), "main");
    private static final ResourceLocation TEX =
            ResourceLocation.fromNamespaceAndPath(BocchiMod.MODID, "textures/entity/bocchi.png");

    private final ModelPart hair, jacket, sleeveR, sleeveL, bust;

    public BocchiLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent, EntityModelSet models) {
        super(parent);
        ModelPart root = models.bakeLayer(LAYER);
        hair = root.getChild("hair");
        jacket = root.getChild("jacket");
        sleeveR = root.getChild("sleeve_r");
        sleeveL = root.getChild("sleeve_l");
        bust = root.getChild("bust");
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
        root.addOrReplaceChild("bust", CubeListBuilder.create().texOffs(0, 0)
                .addBox(-3.6F, -1.6F, -1.8F, 3.4F, 3.2F, 1.8F)
                .addBox(0.2F, -1.6F, -1.8F, 3.4F, 3.2F, 1.8F),
                PartPose.offset(0F, 3.5F, -2.5F));
        return LayerDefinition.create(mesh, 64, 64);
    }

    @Override
    public void render(PoseStack ps, MultiBufferSource buffer, int light, AbstractClientPlayer p,
                       float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {
        MobEffectInstance inst = p.getEffect(BocchiMod.BOCCHI);
        if (inst == null || p.isInvisible()) return;

        VertexConsumer vc = buffer.getBuffer(RenderType.entityCutoutNoCull(TEX));
        PlayerModel<AbstractClientPlayer> m = getParentModel();
        int ov = OverlayTexture.NO_OVERLAY;

        float s = (float) (Config.BUST_SCALE.get() * (1.0 + 0.35 * inst.getAmplifier()));
        bust.xScale = bust.yScale = 1F + (s - 1F) * 0.5F;
        bust.zScale = s;

        draw(ps, m.head, hair, vc, light, ov);
        draw(ps, m.body, jacket, vc, light, ov);
        draw(ps, m.rightArm, sleeveR, vc, light, ov);
        draw(ps, m.leftArm, sleeveL, vc, light, ov);
        draw(ps, m.body, bust, vc, light, ov);
    }

    private static void draw(PoseStack ps, ModelPart anchor, ModelPart part, VertexConsumer vc, int light, int ov) {
        ps.pushPose();
        anchor.translateAndRotate(ps);
        part.render(ps, vc, light, ov);
        ps.popPose();
    }
}
