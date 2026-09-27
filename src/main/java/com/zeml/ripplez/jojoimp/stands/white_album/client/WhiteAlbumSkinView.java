package com.zeml.ripplez.jojoimp.stands.white_album.client;

import com.github.standobyte.jojo.client.entityanim.IHumanoidAnimModel;
import com.github.standobyte.jojo.client.entityanim.RotpAnimDefinition;
import com.github.standobyte.jojo.client.entityanim.pose.AnimFramePose;
import com.github.standobyte.jojo.client.entityrender.stand.StandEntityRenderer;
import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.standskin.StandSkinsScreen;
import com.github.standobyte.jojo.client.util.functions.ClientUtil;
import com.github.standobyte.jojo.mixininterface.LivingRendererLayers;
import com.github.standobyte.jojo.powersystem.entityaction.ActionAnimIdentifier;
import com.github.standobyte.jojo.powersystem.standpower.type.StandType;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.zeml.ripplez.jojoimp.stands.white_album.client.renderer.WhiteAlbumLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class WhiteAlbumSkinView extends StandSkinsScreen.SkinView {
    public static final ActionAnimIdentifier POSE_ANIM_ID = ActionAnimIdentifier.getOrCreate("stand_info");
    private RenderLayer<?,?>layer;
    private AnimFramePose pose;

    public WhiteAlbumSkinView(StandType standType, StandSkin skin, StandSkinsScreen screen, int x, int y, int standY, int row, int column, boolean isBottomRow) {
        super(standType, skin, screen, x, y, standY, row, column, isBottomRow);
        Minecraft mc = Minecraft.getInstance();
        LivingEntityRenderer<?, ?> renderer = (LivingEntityRenderer<?, ?>) mc.getEntityRenderDispatcher().getRenderer(mc.player);
        for (RenderLayer<?, ?> layer : ((LivingRendererLayers<?, ?>) renderer).jojo_ripples$allLayers()) {
            if (layer instanceof WhiteAlbumLayer<?,?>) {
                this.layer = layer;

                RotpAnimDefinition anim = skin.getStandAnimation(POSE_ANIM_ID);
                if (anim != null) {
                    this.pose = anim.calcAnimPose(0, 1, null, null).deepCopy();
                }
                return;
            }
        }
    }

    @Override
    public void renderStandModel(GuiGraphics gui, float posX, float posY,
                                 float scale, float scaleZoom, float yRot, float xRot, float xOffsetRatio, float yOffsetRatio,
                                 StandType standType, StandSkin standSkin,
                                 boolean extractRenderState, float ticks, int color, StandEntityRenderer.MenuType screen) {
        if(layer instanceof WhiteAlbumLayer<?,?> layer1) {
            HumanoidModel<?> model = layer1.whiteModel.getModel(skin);
            ResourceLocation texture = skin.getTexture(WhiteAlbumLayer.HERMIT);

            Quaternionf rotation = new Quaternionf()
                    .rotateX(xRot)
                    .rotateY(yRot);

            PoseStack poseStack = gui.pose();
            poseStack.pushPose();

            poseStack.translate(posX, posY, 350.0);

            poseStack.translate(xOffsetRatio, yOffsetRatio, 0);
            poseStack.scale(-scale, scale, scale);
            poseStack.scale(scaleZoom, scaleZoom, scaleZoom);
            poseStack.mulPose(rotation);
            poseStack.translate(0, -1.35, 0);
            poseStack.mulPose(Axis.YP.rotationDegrees(180));

            Lighting.setupForEntityInInventory();

            if (pose != null) {
                ((IHumanoidAnimModel) model).jojo_ripples$setupHumanoidPose(pose);
            }

            // FIXME doesn't get cropped by OpenGL's scissor
            RenderSystem.runAsFancy(() -> {
                MultiBufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
                VertexConsumer buffer = bufferSource.getBuffer(model.renderType(texture));
                model.renderToBuffer(poseStack, buffer, ClientUtil.MAX_LIGHT, OverlayTexture.NO_OVERLAY, color);
            });

            poseStack.popPose();

            Lighting.setupFor3DItems();
        }
    }

}