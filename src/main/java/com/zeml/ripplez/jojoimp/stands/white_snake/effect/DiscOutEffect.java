package com.zeml.ripplez.jojoimp.stands.white_snake.effect;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.sound.ClientsideSoundsHelper;
import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffectType;
import com.github.standobyte.jojo.powersystem.standpower.effect.StandEffectInstance;
import com.zeml.ripplez.jojoimp.stands.white_snake.client.sound.WhiteSnakeMusicInstance;
import com.zeml.ripplez.core.packets.server.DiscOutDataPacket;
import com.zeml.ripplez.init.AddonDataAttachmentTypes;
import com.zeml.ripplez.init.AddonSoundEvents;
import com.zeml.ripplez.jojoimp.stands.white_snake.data.DiscOutData;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class DiscOutEffect extends StandEffectInstance {
    public DiscOutEffect(@NotNull EntityCustomEffectType<?> effectType) {
        super(effectType);
        needsTarget = true;
    }

    @Override
    protected void start() {
        LivingEntity living = getTargetLiving();
        if(living != null){
            if(!level.isClientSide){
                DiscOutData data = living.getData(AddonDataAttachmentTypes.DISC_OUT);
                data.setDiscsOut(true);
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(living,new DiscOutDataPacket(living.getId(), data.areDiscsOut(), data.isHasMemory()));
            }else if(ClientGlobals.canHearStands){
                WhiteSnakeMusicInstance sound = new WhiteSnakeMusicInstance(AddonSoundEvents.CD_OUT.get(), entity.getSoundSource(),1,1,living,level);
                ClientsideSoundsHelper.playNonVanillaClassSound(sound);
            }
        }
    }

    @Override
    protected void tick() {
        LivingEntity living = getTargetLiving();
        if(living != null && !level.isClientSide){
            DiscOutData data = living.getData(AddonDataAttachmentTypes.DISC_OUT);
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(living,new DiscOutDataPacket(living.getId(), data.areDiscsOut(), data.isHasMemory()));
            if(tickCount >= 1200){
                this.remove();
            }
        }
    }

    @Override
    protected void stop() {

    }

    @Override
    public void remove() {
        super.remove();
        LivingEntity living = getTargetLiving();
        if(living != null && !level.isClientSide){
            DiscOutData data = living.getData(AddonDataAttachmentTypes.DISC_OUT);
            data.setDiscsOut(false);
            PacketDistributor.sendToPlayersTrackingEntityAndSelf(living,new DiscOutDataPacket(living.getId(), data.areDiscsOut(), data.isHasMemory()));

        }
    }
}