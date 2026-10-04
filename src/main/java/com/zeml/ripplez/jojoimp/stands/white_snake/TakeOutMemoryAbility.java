package com.zeml.ripplez.jojoimp.stands.white_snake;

import com.github.standobyte.jojo.customobjects.DamageSourceModified;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.ability.Ability;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.ability.condition.AvailableAbilities;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.StandUtil;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntityAbility;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandStatFormulas;
import com.github.standobyte.jojo.subsystems.target.ActionTarget;
import com.github.standobyte.jojo.subsystems.target.AimingEntity;
import com.github.standobyte.jojoimpl.stands._entitybase.StandEntityHeavyPunchAbility;
import com.zeml.ripplez.init.AddonDataAttachmentTypes;
import com.zeml.ripplez.init.AddonSoundEvents;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import com.zeml.ripplez.jojoimp.stands.white_snake.effect.MemorylessEffect;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TakeOutMemoryAbility extends StandEntityAbility {


    public TakeOutMemoryAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, TakingOutMemoryDisc::new);
        usageGroup = AbilityUsageGroup.COMBAT;
        setDefaultPhaseLength(ActionPhase.WINDUP, StandStatFormulas.getHeavyAttackWindup(12, 0) /* 14 */);
        setDefaultPhaseLength(ActionPhase.PERFORM, 6);
        setDefaultPhaseLength(ActionPhase.RECOVERY, 12);
        noFinisherBarDecay = true;
    }
    @Nullable
    @Override
    public Ability replaceWithSubAbility(Power<?> context, AvailableAbilities abilities) {
        LivingEntity user = context.getUser();
        if(user != null){
            Vec3 lookVec = user.getLookAngle();
            Vec3 startPos = user.getEyePosition();
            double distance = 3.0;
            Vec3 endPos = startPos.add(lookVec.scale(distance));
            AABB searchBox = user.getBoundingBox().inflate(distance).move(lookVec.scale(distance * 0.5));
            EntityHitResult result = ProjectileUtil.getEntityHitResult(user, startPos, endPos, searchBox,
                    entity -> entity != user && entity instanceof LivingEntity, distance * distance);
            if (result != null && result.getEntity() instanceof LivingEntity living) {
                if(living.getData(AddonDataAttachmentTypes.DISC_OUT).areDiscsOut() &&
                        living.getData(AddonDataAttachmentTypes.DISC_OUT).isHasMemory()){
                    abilities.replaceOtherAbilityWith(context,"heavy_punch",this);
                }
            }
        }
        return super.replaceWithSubAbility(context, abilities);
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

    public static class TakingOutMemoryDisc extends StandEntityHeavyPunchAbility.StandEntityHeavyPunch{

        public TakingOutMemoryDisc(EntityActionType ability) {
            super(ability);
        }


        @Override
        public void actionPerformStart() {
            Level level = level();
            if (performer instanceof StandEntity stand) {
                ActionTarget target = getPunchTarget(stand);
                if (!level.isClientSide()) {
                    StandPower standPower = StandPower.get(getPowerUser());
                    DamageSource dmgSource = makePunchDamageSource();
                    float dmgAmount = StandStatFormulas.getHeavyAttackDamage(stand.getAttackDamage());
                    float explRadius = Math.min((float) stand.getAttackDamage() * 0.125f, 10);

                    switch (target.getType()) {
                        case ENTITY -> hitEntity(target, level, stand, dmgSource, dmgAmount, explRadius);
                        case BLOCK -> hitBlock(target, level, stand, dmgSource, dmgAmount, explRadius);
                        default -> {}
                    }

                    punchedTarget = target;
                    standPower.consumeStamina(50);
                    stand.consumeFinisherMeter(1.0001f);
                }
                if (target.getType() == ActionTarget.TargetType.ENTITY) {
                    standRotationTarget = target;
                }
                else {
                    aimAs = AimingEntity.CAMERA_ENTITY;
                }
            }
        }

        protected void hitEntity(ActionTarget target, Level level, StandEntity stand,
                                 DamageSource dmgSource, float dmgAmount, float explRadius){
            Entity targetEntity = target.getMainEntity();
            if (targetEntity instanceof LivingEntity targetLiving){
                addKnockback(dmgSource);
                StandPower standPower = stand.getUserPower();
                if(standPower != null){
                    MemorylessEffect effect = AddonStandAbilities.NO_MEMORY_EFFECT.get().create(level);
                    standPower.userStandEffects.addEffect(effect.withTarget(targetLiving));
                    StandUtil.broadcastSound((ServerLevel) level(), performer.position(),
                            AddonSoundEvents.DISC_TOOK, true, standPower,
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
