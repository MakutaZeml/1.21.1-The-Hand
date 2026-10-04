package com.zeml.ripplez.jojoimp.stands.white_snake.effect;

import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffectType;
import com.github.standobyte.jojo.powersystem.standpower.effect.StandEffectInstance;
import com.zeml.ripplez.core.packets.server.DiscOutDataPacket;
import com.zeml.ripplez.init.AddonDataAttachmentTypes;
import com.zeml.ripplez.jojoimp.stands.white_snake.data.DiscOutData;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

public class MemorylessEffect extends StandEffectInstance {
    public MemorylessEffect(@NotNull EntityCustomEffectType<?> effectType) {
        super(effectType);
        this.needsTarget = true;
    }

    @Override
    protected void start() {
        LivingEntity living = getTargetLiving();
        if(living != null){
            if(!level.isClientSide){
                DiscOutData data = living.getData(AddonDataAttachmentTypes.DISC_OUT);
                data.setHasMemory(false);
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS,MobEffectInstance.INFINITE_DURATION,2,false,false,false));
                living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,MobEffectInstance.INFINITE_DURATION,1,false,false,false));
                living.addEffect(new MobEffectInstance(MobEffects.DARKNESS,60,4,false,false,false));
                living.addEffect(new MobEffectInstance(MobEffects.HUNGER,200,2,false,false,false));

                PacketDistributor.sendToPlayersTrackingEntityAndSelf(living,new DiscOutDataPacket(living.getId(),data.areDiscsOut(),data.isHasMemory()));
            }
        }
    }

    @Override
    protected void tick() {
        LivingEntity living = getTargetLiving();
        if(living != null){
            if(living instanceof Mob psycho){
                psycho.setNoAi(true);
                psycho.removeFreeWill();
                psycho.setTarget(null);
            }

            if(!level.isClientSide){
                DiscOutData data = living.getData(AddonDataAttachmentTypes.DISC_OUT);
                PacketDistributor.sendToPlayersTrackingEntityAndSelf(living,new DiscOutDataPacket(living.getId(),data.areDiscsOut(),data.isHasMemory()));
            }
        }
    }

    @Override
    protected void stop() {

    }
}
