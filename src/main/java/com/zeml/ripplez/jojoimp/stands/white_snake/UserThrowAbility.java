package com.zeml.ripplez.jojoimp.stands.white_snake;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.sound.ClientsideSoundsHelper;
import com.github.standobyte.jojo.init.ModSoundEvents;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.PowerData;
import com.github.standobyte.jojo.powersystem.ability.Ability;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.EntityActionAbility;
import com.github.standobyte.jojo.powersystem.ability.condition.AvailableAbilities;
import com.github.standobyte.jojo.powersystem.entityaction.ActionAnimIdentifier;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez.jojoimp.stands.white_snake.entity.ThrewDiscEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;


public class UserThrowAbility extends EntityActionAbility {


    public UserThrowAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId,UserThrow::new);
        setDefaultPhaseLength(ActionPhase.WINDUP, 6);
        setDefaultPhaseLength(ActionPhase.PERFORM, 1);
        setDefaultPhaseLength(ActionPhase.RECOVERY, 5);
    }

    @Nullable
    @Override
    public Ability replaceWithSubAbility(Power<?> context, AvailableAbilities abilities){
        StandPower power = PowerClass.STAND.cast(context);
        if(power != null){
            if(power.getSummonedStandEntity() == null || !WhiteSnakeUtil.standHasDisc(power)){
                abilities.replaceOtherAbilityWith(context,"throw_disc",this);
            }
        }
        return super.replaceWithSubAbility(context, abilities);
    }

    @Override
    public ActionAnimIdentifier getEntityAnim(EntityActionInstance action) {
        if(action.getPowerUser().getMainArm() == HumanoidArm.LEFT){
            return ActionAnimIdentifier.getOrCreate(abilityId.nameInMoveset().concat("_l"),false);
        }
        return super.getEntityAnim(action);
    }



    public static class UserThrow extends EntityActionInstance {
        public UserThrow(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void actionPerformEnd() {
            ItemStack stack = performer.getItemBySlot(EquipmentSlot.MAINHAND).copy();
            if(!stack.isEmpty()){
                ThrewDiscEntity disc = new ThrewDiscEntity(performer,level(),stack);
                if(!level().isClientSide){
                    disc.shootFromRotation(performer,.75F,.4F);
                    level().addFreshEntity(disc);
                    performer.setItemSlot(EquipmentSlot.MAINHAND,ItemStack.EMPTY);
                }
                if (level().isClientSide() && ClientGlobals.canHearStands){
                    level().playLocalSound(performer.getX(), performer.getEyeY(), performer.getZ(), ModSoundEvents.STAND_PUNCH_SWING.get(),
                            performer.getSoundSource(), 1, 1, false);
                }
            }
        }
    }

    @Override
    public boolean isAbilityUnlocked(Power<?> context) {
        PowerData skillsData = context.getCurTypeData();
        return skillsData != null && !skillsData._lockedAbilities.contains("throw_disc");
    }
}