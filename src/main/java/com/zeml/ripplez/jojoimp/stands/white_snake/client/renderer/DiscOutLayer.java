package com.zeml.ripplez.jojoimp.stands.white_snake.client.renderer;

import com.github.standobyte.jojo.init.ModItems;
import com.github.standobyte.jojo.mechanics.standdisc.StandDiscItem;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.zeml.ripplez.init.AddonDataAttachmentTypes;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DiscOutLayer<T extends LivingEntity, M extends HumanoidModel<T>, A extends HumanoidModel<T>> extends RenderLayer<T, M> {
    private final ItemInHandRenderer itemInHandRenderer;

    public DiscOutLayer(RenderLayerParent<T, M> renderer, ItemInHandRenderer itemInHandRenderer) {
        super(renderer);
        this.itemInHandRenderer = itemInHandRenderer;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T t, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean flag = t.isBaby();
        poseStack.pushPose();
        renderStandDisc(poseStack,buffer,packedLight,t,netHeadYaw,headPitch);
        renderMemoryDisc(poseStack,buffer,packedLight,t,netHeadYaw,headPitch);
        poseStack.popPose();
    }

    private void renderStandDisc(PoseStack poseStack,MultiBufferSource buffer, int packedLight ,T t,float netHeadYaw, float headPitch){
        poseStack.pushPose();
        StandPower standPower = StandPower.get(t);
        if(t.getData(AddonDataAttachmentTypes.DISC_OUT).areDiscsOut()){
            if(standPower != null){
                poseStack.mulPose(Axis.YP.rotationDegrees(netHeadYaw));
                poseStack.mulPose(Axis.XP.rotationDegrees(headPitch));
                poseStack.translate(-.15,-.3,-.35);
                poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
                standPower.getStandInstance().ifPresent(standInstance -> {
                    ItemStack newDisc = StandDiscItem.withStand(standInstance);
                    this.itemInHandRenderer.renderItem(t, newDisc, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight);
                });
            }
        }
        poseStack.popPose();
    }

    public void renderMemoryDisc(PoseStack poseStack,MultiBufferSource buffer, int packedLight ,T t,float netHeadYaw, float headPitch){
        poseStack.pushPose();
        if(t.getData(AddonDataAttachmentTypes.DISC_OUT).isHasMemory() && t.getData(AddonDataAttachmentTypes.DISC_OUT).areDiscsOut()){
            poseStack.mulPose(Axis.YP.rotationDegrees(netHeadYaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(headPitch));
            poseStack.translate(-.15,-.15,-.35);
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
            ItemStack newDisc = new ItemStack(ModItems.STAND_DISC.asItem());
            this.itemInHandRenderer.renderItem(t, newDisc, ItemDisplayContext.GROUND, false, poseStack, buffer, packedLight);
        }
        poseStack.popPose();
    }

}