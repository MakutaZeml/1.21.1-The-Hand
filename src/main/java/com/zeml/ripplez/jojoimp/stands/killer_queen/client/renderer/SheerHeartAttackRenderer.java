package com.zeml.ripplez.jojoimp.stands.killer_queen.client.renderer;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.entityrender.entities.SimpleEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.jojoimp.stands.killer_queen.entity.SheerHeartAttack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class SheerHeartAttackRenderer extends MobRenderer<SheerHeartAttack, SheerHeartAttackModel<SheerHeartAttack>> {
    public static final ResourceLocation TEXTURE = RipplesAddon.resLoc("textures/entity/sheer_heart.png");
    public SheerHeartAttackRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new SheerHeartAttackModel<>(renderManager.bakeLayer(SheerHeartAttackModel.SHA)),0);
    }

    @Override
    public void render(SheerHeartAttack entity, float yRotation, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if(!ClientGlobals.canSeeStands){
            return;
        }
        super.render(entity, yRotation, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(SheerHeartAttack entity) {
        return TEXTURE;
    }
}