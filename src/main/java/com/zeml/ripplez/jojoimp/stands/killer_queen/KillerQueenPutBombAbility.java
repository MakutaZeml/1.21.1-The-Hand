package com.zeml.ripplez.jojoimp.stands.killer_queen;

import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntityAbility;
import com.github.standobyte.jojo.subsystems.target.ActionTarget;
import com.github.standobyte.jojoimpl.stands._entitybase.StandEntityHeavyPunchChargedAbility;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import com.zeml.ripplez.powersystem.entityaction.ActionCL;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class KillerQueenPutBombAbility extends StandEntityHeavyPunchChargedAbility {

    public KillerQueenPutBombAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
        createActionObj = PuttingEntityBomb::new;
    }

    public static class PuttingEntityBomb extends StandEntityHeavyPunchChargedAbility.StandEntityChargedHeavy implements ActionCL {
        public PuttingEntityBomb(EntityActionType ability) {
            super(ability);
        }
        @Override
        public void actionTick() {
            if(phase == ActionPhase.BUTTON_CHARGE){
                if(buttonChargeRatio ==1){
                    setPhase(ActionPhase.PERFORM,0);
                    syncPhaseChanges();
                }
            }
        }

        @Override
        protected void hitEntity(ActionTarget target, Level level, StandEntity stand, DamageSource dmgSource, float dmgAmount, float explRadius) {
            Entity targetEntity = target.getMainEntity();
            if (targetEntity instanceof LivingEntity targetLiving) {
                standEntityAttack(stand, targetLiving, dmgSource, 0);
            }
            StandPower standPower = stand.getUserPower();
            if(standPower != null){
                standPower.userStandEffects.getEffectsOfType(AddonStandAbilities.KILLER_QUEEN_BOMB.get()).forEach(KillerBombEffect::remove);

                KillerBombEffect newEffect = AddonStandAbilities.KILLER_QUEEN_BOMB.get().create(level);
                standPower.userStandEffects.addEffect(newEffect.withTarget(targetEntity));
            }
        }

        @Override
        protected void hitBlock(ActionTarget target, Level level, StandEntity stand, DamageSource dmgSource, float dmgAmount, float explRadius) {
            BlockPos blockPos = target.getBlockPos();
            StandPower standPower = stand.getUserPower();
            if(standPower != null){
                standPower.userStandEffects.getEffectsOfType(AddonStandAbilities.KILLER_QUEEN_BOMB.get()).forEach(KillerBombEffect::remove);

                KillerBombEffect newEffect = AddonStandAbilities.KILLER_QUEEN_BOMB.get().create(level);
                standPower.userStandEffects.addEffect(newEffect.withBlock(blockPos));
            }
        }

        @Override
        public float cancelAmount() {
            return 16;
        }
    }

}