package com.zeml.ripplez.jojoimp.stands.killer_queen;

import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffectType;
import com.github.standobyte.jojo.powersystem.standpower.effect.StandEffectInstance;
import com.zeml.ripplez.jojoimp.stands.killer_queen.entity.SheerHeartAttack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class SheerHAEffect extends StandEffectInstance {

    public SheerHAEffect(@NotNull EntityCustomEffectType<?> effectType) {
        super(effectType);
        removeOnUserDeath = true;
        needsTarget = true;
    }

    @Override
    protected void start() {

    }

    @Override
    protected void tick() {
        if(getStandUser() instanceof LivingEntity user && getTargetLiving() instanceof SheerHeartAttack sheerHeartAttack){
            if(sheerHeartAttack.getTarget() == null){
                LivingEntity target = user.getLastAttacker();
                if(target == null || !target.isAlive()){
                    target = user.getLastHurtMob();
                }
                if(target != null && target.isAlive()){
                    sheerHeartAttack.setTarget(target);
                }
            }

            if(!user.isAlive() || !getTargetLiving().isAlive()){
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
        if(getTargetLiving() != null){
            getTargetLiving().remove(Entity.RemovalReason.DISCARDED);
        }
    }
}