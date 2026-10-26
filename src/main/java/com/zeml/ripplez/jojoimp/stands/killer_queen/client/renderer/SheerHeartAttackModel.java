package com.zeml.ripplez.jojoimp.stands.killer_queen.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.jojoimp.stands.killer_queen.entity.SheerHeartAttack;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.world.entity.TamableAnimal;

public class SheerHeartAttackModel<S extends TamableAnimal> extends EntityModel<SheerHeartAttack> {
    public static final ModelLayerLocation SHA = new ModelLayerLocation(RipplesAddon.resLoc("sha"),"main");
    private final ModelPart bb_main;

    public SheerHeartAttackModel(ModelPart root) {
        this.bb_main = root.getChild("bb_main");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main", CubeListBuilder.create().texOffs(0, 13).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(22, 25).addBox(-2.0F, -7.0F, -2.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(24, 13).addBox(-3.0F, -6.0F, -3.0F, 6.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-3.0F, -4.0F, -5.0F, 6.0F, 3.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(8, 25).addBox(-2.0F, -3.0F, 4.0F, 4.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(22, 0).addBox(-5.0F, -4.0F, -3.0F, 10.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.0F, -5.0F, -6.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 4).addBox(-1.0F, -2.0F, -6.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 6).addBox(-1.5F, -6.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(5, 5).addBox(0.5F, -6.0F, -6.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(14, 25).addBox(5.0F, -3.0F, -3.0F, 1.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 25).addBox(-6.0F, -3.0F, -3.0F, 1.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(56, 0).addBox(-1.0F, -7.5F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(SheerHeartAttack entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        this.bb_main.yRot = netHeadYaw;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        bb_main.render(poseStack,vertexConsumer,packedLight,packedOverlay,color);
    }

}