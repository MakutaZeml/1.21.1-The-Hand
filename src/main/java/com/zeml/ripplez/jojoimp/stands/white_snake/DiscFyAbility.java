package com.zeml.ripplez.jojoimp.stands.white_snake;

import com.github.standobyte.jojo.customobjects.DamageSourceModified;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.StandUtil;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntityAbility;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandStatFormulas;
import com.github.standobyte.jojo.subsystems.target.ActionTarget;
import com.github.standobyte.jojoimpl.stands._entitybase.StandEntityHeavyPunchChargedAbility;
import com.zeml.ripplez.init.AddonSoundEvents;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import com.zeml.ripplez.jojoimp.stands.white_snake.effect.DiscOutEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class DiscFyAbility extends StandEntityAbility {


    public DiscFyAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, HeavyDisc::new);
        usageGroup = AbilityUsageGroup.COMBAT;
        setDefaultPhaseLength(ActionPhase.BUTTON_CHARGE, 21);
        setButtonHoldPhase(ActionPhase.WINDUP);
        setDefaultPhaseLength(ActionPhase.PERFORM, 7);
        setDefaultPhaseLength(ActionPhase.RECOVERY, 12);
    }

    @Override
    public boolean isAbilityAvailable(Power<?> context) {
        return super.isAbilityAvailable(context) && StandUtil.getStandGrabTarget(context) == null;
    }

    @Override
    public void initActionFromConfig(EntityActionInstance action, Level level,
                                     LivingEntity powerUser, LivingEntity performer) {
        super.initActionFromConfig(action, level, powerUser, performer);
        if (!level.isClientSide() && performer instanceof StandEntity stand) {
            action.phasesLength.put(ActionPhase.BUTTON_CHARGE, StandStatFormulas.getChargedHeavyButtonWindup(
                    stand.getAttackSpeed(), stand.getFinisherMeter()));
            action.phasesLength.put(ActionPhase.PERFORM, StandStatFormulas.getChargedHeavyPunchWindup(
                    stand.getAttackSpeed(), stand.getFinisherMeter()));
        }
    }

    public static class HeavyDisc extends StandEntityHeavyPunchChargedAbility.StandEntityChargedHeavy {

        public HeavyDisc(EntityActionType ability) {
            super(ability);
        }


        protected void hitEntity(ActionTarget target, Level level, StandEntity stand,
                                 DamageSource dmgSource, float dmgAmount, float explRadius) {
            Entity targetEntity = target.getMainEntity();
            if (targetEntity instanceof LivingEntity targetLiving) {
                addKnockback(dmgSource);
                standEntityAttack(stand, targetLiving, dmgSource, 0);
                StandPower standPower = stand.getUserPower();
                if(standPower != null){
                    DiscOutEffect effect = AddonStandAbilities.DISC_OUT_EFFECT.get().create(level());
                    standPower.userStandEffects.addEffect(effect.withTarget(targetLiving));
                    StandUtil.broadcastSound((ServerLevel) level(), performer.position(),
                            AddonSoundEvents.STEP_BEHIND, true, standPower,
                            performer.getSoundSource(), 1, 1);
                }
            }
        }

        @Override
        protected void hitBlock(ActionTarget target, Level level, StandEntity stand, DamageSource dmgSource, float dmgAmount, float explRadius) {
            return;
        }

        protected void addKnockback(DamageSource dmgSource) {
            DamageSourceModified knockback = (DamageSourceModified) dmgSource;
            knockback.jojo_ripples$modifyKnockback(0f, .1f);
        }


    }

}
