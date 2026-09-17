package com.zeml.ripplez.jojoimp.stands.green_day;

import com.github.standobyte.jojo.init.ModStatusEffects;
import com.github.standobyte.jojo.mechanics.BleedingEffect;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.github.standobyte.jojo.subsystems.target.ActionTarget;
import com.github.standobyte.jojoimpl.stands._entitybase.StandEntityHeavyPunchAbility;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class GreenFinisherAbility extends StandEntityHeavyPunchAbility {


    public GreenFinisherAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
        this.createActionObj = Chop::new;
    }

    public static class Chop extends StandEntityHeavyPunch {
        public Chop(EntityActionType ability) {
            super(ability);
        }

        @Override
        protected void hitEntity(ActionTarget target, Level level, StandEntity stand, DamageSource dmgSource, float dmgAmount, float explRadius) {
            super.hitEntity(target, level, stand, dmgSource, dmgAmount, explRadius);
            if(target.getEntity() instanceof LivingEntity targetLiving && !(target.getEntity() instanceof StandEntity)){
                targetLiving.addEffect(new MobEffectInstance(ModStatusEffects.BLEEDING,1200,0,false,false,true),performer);
            }
        }
    }

}