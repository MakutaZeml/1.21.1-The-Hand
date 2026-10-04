package com.zeml.ripplez.jojoimp.stands.white_album.client.renderer;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.entityrender.ModelUtil;
import com.github.standobyte.jojo.client.entityrender.parsemodel.loader.ResourceModelEntry;
import com.github.standobyte.jojo.client.entityrender.parsemodel.loader.RotpGeckoModelLoader;
import com.github.standobyte.jojo.client.firstperson.FirstPersonModelLayer;
import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.standskin.StandSkinsLoader;
import com.github.standobyte.jojo.mechanics.clothes.mannequin.MannequinEntity;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.type.SummonedStand;
import com.github.standobyte.v1_21_4_stuff.missingmethods.ARGB;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.init.power.AddonStands;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;

public class WhiteAlbumLayer <T extends LivingEntity, M extends HumanoidModel<T>> extends RenderLayer<T, M> implements FirstPersonModelLayer {
    public final ResourceModelEntry whiteModel;
    public static final ResourceLocation HERMIT = ResourceLocation.tryBuild(RipplesAddon.MOD_ID,"textures/entity/stand/white_album.png");
    public WhiteAlbumLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
        this.whiteModel = RotpGeckoModelLoader.getInstance().getModelContainer(RipplesAddon.resLoc("white_album"));
        this.whiteModel.rendererInit(WhiteAlbumModel::new);
    }

    @Override
    public void renderHandFirstPerson(HumanoidArm humanoidArm, PoseStack poseStack, MultiBufferSource buffer, int packedLight, LivingEntity t, LivingEntityRenderer<?, ?> livingEntityRenderer, float partialTick) {
        boolean slim = ModelUtil.isSlimModel(t);
        StandPower standData = StandPower.get(t);
        if(standData != null && standData.getPowerType() == AddonStands.WHITE_ALBUM.get() && standData.getSummonedStand() instanceof SummonedStand.SyncableSummonedStand stand) {
            StandSkin standSkin = StandSkinsLoader.getInstance().getSkin(standData);
            ResourceLocation texture = standSkin.getTexture(HERMIT);
            WhiteAlbumModel albumModel = this.whiteModel.getModel(standSkin);
            albumModel.setAllVisible(true);
            albumModel.setSlim(slim);
            albumModel.head.visible = false;
            albumModel.body.visible = false;
            albumModel.rightLeg.visible = false;
            albumModel.leftLeg.visible = false;
            albumModel.hat.visible = false;
            getParentModel().copyPropertiesTo(albumModel);

            float alpha = stand.unsummonAlpha(partialTick);
            int color = ARGB.white(alpha);
            ModelPart arm = FirstPersonModelLayer.getArm(albumModel, humanoidArm);
            VertexConsumer iVertexBuilder = buffer.getBuffer(RenderType.entityTranslucent(texture));
            arm.xRot = 0.0F;
            arm.render(poseStack, iVertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, color);
            ModelPart armSlim = humanoidArm == HumanoidArm.LEFT ? albumModel.leftArmSlim : albumModel.rightArmSlim;
            armSlim.copyFrom(arm);
            armSlim.render(poseStack, iVertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, color);
            arm.render(poseStack, iVertexBuilder, packedLight, OverlayTexture.NO_OVERLAY, color);
        }
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T t, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if(!ClientGlobals.canSeeStands || (t.isInvisible() && !(t instanceof MannequinEntity))){
            return;
        }
        StandPower standData = StandPower.get(t);
        if(standData != null && standData.getPowerType() == AddonStands.WHITE_ALBUM.get() && standData.getSummonedStand() instanceof SummonedStand.SyncableSummonedStand stand){
            boolean slim = ModelUtil.isSlimModel(t);
            StandSkin standSkin = StandSkinsLoader.getInstance().getSkin(standData);
            M parentModel = getParentModel();
            WhiteAlbumModel purpleModel = this.whiteModel.getModel(standSkin);

            purpleModel.setAllVisible(true);
            parentModel.copyPropertiesTo(purpleModel);
            purpleModel.setSlim(slim);

            float alpha = stand.unsummonAlpha(partialTicks);
            int color = ARGB.white(alpha);

            ResourceLocation texture = standSkin.getTexture(HERMIT);
            VertexConsumer ivertexbuilder = buffer.getBuffer(RenderType.entityTranslucent(texture));
            purpleModel.renderToBuffer(poseStack,ivertexbuilder,packedLight, OverlayTexture.NO_OVERLAY, color);

        }
    }
}