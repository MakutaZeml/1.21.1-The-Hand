package com.zeml.ripplez.jojoimp.stands.killer_queen;

import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.ability.*;
import com.github.standobyte.jojo.powersystem.ability.condition.AvailableAbilities;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import com.zeml.ripplez.jojoimp.stands.killer_queen.entity.SheerHeartAttack;
import org.jetbrains.annotations.Nullable;

public class SheerHeartAttackAbility extends EntityActionAbility {

    public SheerHeartAttackAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, Sheer::new);
        setDefaultPhaseLength(ActionPhase.WINDUP,5);
        setDefaultPhaseLength(ActionPhase.PERFORM,2);
        usageGroup = AbilityUsageGroup.SPECIAL;
    }


    @Override
    public ConditionCheck checkSpecificConditions(Power<?> context) {
        StandPower standPower = PowerClass.STAND.cast(context);
        if(standPower != null){
            if(standPower.userStandEffects.getEffectOfType(AddonStandAbilities.KILLER_QUEEN_2_BOMB.get()).isPresent()){
                return ConditionCheck.GREEN_HIGHLIGHT;
            }
        }
        return super.checkSpecificConditions(context);
    }


    public static class Sheer extends EntityActionInstance{
        boolean active = false;
        public Sheer(EntityActionType ability) {
            super(ability);
        }


        @Override
        public void actionPerformStart() {
            StandPower standPower = StandPower.get(performer);
            if(standPower != null){
                active = standPower.userStandEffects.getEffectOfType(AddonStandAbilities.KILLER_QUEEN_2_BOMB.get()).isPresent();
            }
        }

        @Override
        public void actionPerformEnd() {
            StandPower standPower = StandPower.get(performer);
            if(active){
                if(standPower != null){
                    standPower.userStandEffects.getEffectsOfType(AddonStandAbilities.KILLER_QUEEN_2_BOMB.get()).forEach(SheerHAEffect::remove);
                }
            }else {
                SheerHeartAttack sheerHeartAttack = new SheerHeartAttack(level(),performer);
                SheerHAEffect effect = AddonStandAbilities.KILLER_QUEEN_2_BOMB.get().create(level());
                sheerHeartAttack.copyPosition(performer);
                level().addFreshEntity(sheerHeartAttack);
                if(standPower != null){
                    standPower.userStandEffects.addEffect(effect.withTarget(sheerHeartAttack));
                }
            }
        }
    }

}