package com.zeml.ripplez.jojoimp.stands.green_day;

import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntityAbility;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.init.power.AddonStandAbilities;

public class MoldAbility extends StandEntityAbility {
    public MoldAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, Mold::new);
        usageGroup = AbilityUsageGroup.SPECIAL;
        setDefaultPhaseLength(ActionPhase.BUTTON_CHARGE, 40);
        setDefaultPhaseLength(ActionPhase.PERFORM,1);
        setDefaultPhaseLength(ActionPhase.RECOVERY,20);
    }

    @Override
    public ConditionCheck checkSpecificConditions(Power<?> context) {
        StandPower standPower = PowerClass.STAND.cast(context);
        if(standPower != null && standPower.userStandEffects.getEffectsOfType(AddonStandAbilities.USER_MOLD.get()).findAny().isPresent()){
            return ConditionCheck.GREEN_HIGHLIGHT;
        }
        return super.checkSpecificConditions(context);
    }

    public static class Mold extends EntityActionInstance {
        boolean active;
        public Mold(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void onSetPhase(ActionPhase newPhase) {
            if(newPhase == ActionPhase.WINDUP || newPhase == ActionPhase.BUTTON_CHARGE){
                StandPower standPower = StandPower.get(performer);
                if(performer instanceof StandEntity standEntity){
                    standPower = standEntity.getUserPower();
                }
                if(standPower != null){
                    active = standPower.userStandEffects.getEffectOfType(AddonStandAbilities.USER_MOLD.get()).isPresent();
                }else {
                    active =false;
                }
            }
        }



        @Override
        public void actionPerformStart() {
            RipplesAddon.getLogger().debug("mold ? {}", active);
            StandPower power = StandPower.get(performer);
            if(performer instanceof StandEntity standEntity){
                power = standEntity.getUserPower();
            }
            if(power != null){
                if(active){
                    power.userStandEffects.getEffectsOfType(AddonStandAbilities.USER_MOLD.get()).forEach(MoldUserEffect::remove);
                }else {
                    MoldUserEffect moldUserEffect = AddonStandAbilities.USER_MOLD.get().create(level());
                    power.userStandEffects.addEffect(moldUserEffect);
                }
            }

        }
    }


}