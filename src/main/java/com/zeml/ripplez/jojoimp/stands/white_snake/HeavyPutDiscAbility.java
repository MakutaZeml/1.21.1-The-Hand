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
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntityAbility;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandStatFormulas;
import com.github.standobyte.jojo.subsystems.target.ActionTarget;
import com.github.standobyte.jojoimpl.stands._entitybase.StandEntityHeavyPunchAbility;
import com.zeml.ripplez.RipplesAddon;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Set;

public class HeavyPutDiscAbility extends StandEntityAbility {


    public HeavyPutDiscAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, HeavyPutDisc::new);
        usageGroup = AbilityUsageGroup.COMBAT;
        setDefaultPhaseLength(ActionPhase.WINDUP, StandStatFormulas.getHeavyAttackWindup(12, 0) /* 14 */);
        setDefaultPhaseLength(ActionPhase.PERFORM, 6);
        setDefaultPhaseLength(ActionPhase.RECOVERY, 12);
        noFinisherBarDecay = true;
    }


    @Override
    public boolean isAbilityAvailable(Power<?> context){
        return super.isAbilityAvailable(context) && WhiteSnakeUtil.standHasDisc(context);
    }

    @Override
    public void initActionFromConfig(EntityActionInstance action, Level level,
                                     LivingEntity powerUser, LivingEntity performer) {
        super.initActionFromConfig(action, level, powerUser, performer);
        if (!level.isClientSide() && performer instanceof StandEntity stand) {
            action.phasesLength.put(ActionPhase.WINDUP, StandStatFormulas.getHeavyAttackWindup(
                    stand.getAttackSpeed(), stand.getFinisherMeter()));
        }
    }

    public static class HeavyPutDisc extends StandEntityHeavyPunchAbility.StandEntityHeavyPunch {

        public HeavyPutDisc(EntityActionType ability) {
            super(ability);
        }


        protected void hitEntity(ActionTarget target, Level level, StandEntity stand,
                                 DamageSource dmgSource, float dmgAmount, float explRadius) {
            Entity targetEntity = target.getMainEntity();
            if (targetEntity instanceof LivingEntity targetLiving) {
                addKnockback(dmgSource);
                standEntityAttack(stand, targetLiving, dmgSource, 0);
                StandPower userPower = stand.getUserPower();
                if(userPower != null){
                    ItemStack disc = stand.getItemBySlot(EquipmentSlot.MAINHAND);
                    Set<ItemStack> stacks = WhiteSnakeUtil.putDisc(disc, userPower, targetLiving);
                    if(!stacks.isEmpty()){
                        if(!level().isClientSide){
                            stacks.forEach(stack -> {
                                if(stand.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()){
                                   stand.setItemSlot(EquipmentSlot.MAINHAND,stack);
                                } else if (stand.getItemBySlot(EquipmentSlot.OFFHAND).isEmpty()) {
                                    stand.setItemSlot(EquipmentSlot.OFFHAND,stack);
                                }else {
                                    ItemEntity itemEntity = new ItemEntity(level(),targetEntity.getX(),targetEntity.getY(),targetEntity.getZ(),stack);
                                    itemEntity.setPickUpDelay(100);
                                    level().addFreshEntity(itemEntity);
                                }
                            });
                        }
                    }

                }
            }
        }

        protected void addKnockback(DamageSource dmgSource) {
            DamageSourceModified knockback = (DamageSourceModified) dmgSource;
            knockback.jojo_ripples$modifyKnockback(0f, .1f);
        }

        @Override
        protected void hitBlock(ActionTarget target, Level level, StandEntity stand, DamageSource dmgSource, float dmgAmount, float explRadius) {
            return;
        }
    }



}
