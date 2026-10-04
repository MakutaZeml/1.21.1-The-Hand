package com.zeml.ripplez.jojoimp.stands.white_snake;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.sound.ClientsideSoundsHelper;
import com.github.standobyte.jojo.init.ModSoundEvents;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntityAbility;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandOffsetFromUser;
import com.zeml.ripplez.jojoimp.stands.white_snake.entity.ThrewDiscEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;


public class ThrowDiscAbility extends StandEntityAbility {

    public ThrowDiscAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, ThrowingDisc::new);
        usageGroup = AbilityUsageGroup.COMBAT;
        setDefaultPhaseLength(ActionPhase.WINDUP, 6);
        setDefaultPhaseLength(ActionPhase.PERFORM, 1);
        setDefaultPhaseLength(ActionPhase.RECOVERY, 5);
    }


    @Override
    public ConditionCheck checkSpecificConditions(Power<?> context) {
        return WhiteSnakeUtil.standHasDisc(context) ? ConditionCheck.POSITIVE: ConditionCheck.NEGATIVE;
    }

    public static class ThrowingDisc extends EntityActionInstance{

        public ThrowingDisc(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void actionTick() {
            if (phase == ActionPhase.RECOVERY && getPhaseTicksLeft() == 1) {
                if(performer instanceof StandEntity stand){
                    stand.offsetFromUser.resetToIdle();
                }
            }else {
                setStandOffset(new Vec3(0, 0, 1.5), StandOffsetFromUser.Rotations.HEAD_XY, true);
            }

            if(performer instanceof StandEntity stand){
                stand.offsetFromUser.syncToTracking();
            }

        }

        @Override
        public void actionPerformStart() {
            ItemStack stack = performer.getItemBySlot(EquipmentSlot.MAINHAND).copy();
            if(!stack.isEmpty()){
                ThrewDiscEntity disc = new ThrewDiscEntity(performer,level(),stack);
                if(!level().isClientSide){
                    disc.shootFromRotation(performer,.75F,.4F);
                    level().addFreshEntity(disc);
                    performer.setItemSlot(EquipmentSlot.MAINHAND,ItemStack.EMPTY);
                }
                if (level().isClientSide() && ClientGlobals.canHearStands  && performer instanceof StandEntity stand){
                    level().playLocalSound(stand.getX(), stand.getEyeY(), stand.getZ(), ClientsideSoundsHelper.withStandSkin(
                                    ModSoundEvents.STAND_PUNCH_SWING.get(), stand),
                            stand.getSoundSource(), 1, 1, false);
                }
            }

        }
    }
}
