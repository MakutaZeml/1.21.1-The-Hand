package com.zeml.ripplez.jojoimp.stands.white_snake.client.renderer;

import com.github.standobyte.jojo.client.entityrender.entities.SimpleEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.zeml.ripplez.jojoimp.stands.white_snake.entity.ThrewDiscEntity;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemDisplayContext;

public class ThrowDiscRenderer extends SimpleEntityRenderer<ThrewDiscEntity, EntityModel<ThrewDiscEntity>> {
    private final ItemRenderer itemRenderer;

    public ThrowDiscRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager);
        this.itemRenderer = renderManager.getItemRenderer();
    }


    public void render(ThrewDiscEntity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25)) {
            poseStack.pushPose();
            poseStack.mulPose(this.entityRenderDispatcher.cameraOrientation());
            this.itemRenderer.renderStatic(((ItemSupplier)entity).getItem(), ItemDisplayContext.GROUND, packedLight, OverlayTexture.NO_OVERLAY, poseStack, buffer, entity.level(), entity.getId());
            poseStack.popPose();
            super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        }

    }

    public ResourceLocation getTextureLocation(ThrewDiscEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }

}
