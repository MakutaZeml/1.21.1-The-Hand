package com.zeml.ripplez.jojoimp.stands.white_album;

import com.github.standobyte.jojo.client.ClientGlobals;
import com.github.standobyte.jojo.client.sound.ClientsideSoundsHelper;
import com.github.standobyte.jojo.client.sound.sounds.EntityLingeringSoundInstance;
import com.github.standobyte.jojo.init.ModSoundEvents;
import com.github.standobyte.jojo.powersystem.Moveset;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.ability.*;
import com.github.standobyte.jojo.powersystem.ability.condition.AvailableAbilities;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.LivingComponentAction;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.StandUtil;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandStatFormulas;
import com.github.standobyte.jojo.subsystems.ServerBlockDestroyTracker;
import com.github.standobyte.jojo.subsystems.entity_grab.LivingComponentGrab;
import com.github.standobyte.jojo.subsystems.target.ActionTarget;
import com.github.standobyte.jojo.subsystems.target.HitResultUtil;
import com.github.standobyte.jojo.util.OOPMoment;
import com.github.standobyte.jojo.util.functions.JojoModUtil;
import com.github.standobyte.jojoimpl.stands._entitybase.StandEntityPunchAbility;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.github.standobyte.jojoimpl.stands._entitybase.StandEntityPunchAbility.playHitSound;

public class WhitePunch extends EntityActionAbility {
    public WhitePunch(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, Punch::new);
        usageGroup = AbilityUsageGroup.COMBAT;
        setDefaultPhaseLength(ActionPhase.WINDUP, 4);
        setDefaultPhaseLength(ActionPhase.PERFORM, 2);
        setDefaultPhaseLength(ActionPhase.RECOVERY, 20);
    }

    @Override
    public Ability replaceWithSubAbility(Power<?> context, AvailableAbilities abilities) {
        StandPower standPower = PowerClass.STAND.cast(context);
        Ability punch = getComboPunch(standPower);
        if (punch != null) return punch;

        return super.replaceWithSubAbility(context, abilities);
    }

    @Override
    public void initActionFromConfig(EntityActionInstance action, Level level, LivingEntity standUser, LivingEntity standEntity) {
        super.initActionFromConfig(action, level, standUser, standEntity);
        StandPower standPower = StandPower.get(standUser);
        if (!level.isClientSide() && standEntity instanceof StandEntity stand && standPower != null && standPower.getPowerType() != null) {
            action.phasesLength.put(ActionPhase.WINDUP, StandStatFormulas.getLightAttackWindup(
                    standPower.getPowerType().getStandStats().speed(), 0, stand.getCurStandAction() == null));
        }
    }

    public static class Punch extends EntityActionInstance{
        protected boolean playedSwingSound;
        public Punch(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void actionTick() {
            Level level = performer.level();
            StandPower standPower = StandPower.get(performer);
            if (level.isClientSide() && !(playedSwingSound) && standPower != null) {
                int ticksDiff = (int) (calcFullTicks(ActionPhase.PERFORM, 0) - getFullTicksPassed());
                if (ticksDiff <= 2) {
                    level.playLocalSound(performer.getX(), performer.getEyeY(), performer.getZ(), ClientsideSoundsHelper.withStandSkin(
                                    ModSoundEvents.STAND_PUNCH_SWING.get(), standPower),
                            performer.getSoundSource(), 1, 1, false);
                    playedSwingSound = true;
                }

            }
        }

        @Override
        public void actionPerformStart() {
            Level level = level();
            ActionTarget target = aimAtPunchTarget(performer);
            if (!level.isClientSide()) {
                StandPower standPower = StandPower.get(getPowerUser());

                if (playHitSound(target, level)) {
                    StandUtil.broadcastSound((ServerLevel) level, target.getCenterPos(),
                            ModSoundEvents.STAND_PUNCH_LIGHT, true, standPower,
                            performer.getSoundSource(), 1, 1);
                }

                switch (target.getType()) {
                    case ENTITY -> hitEntity(target, level, performer, standPower);
                    case BLOCK -> hitBlock(target, level, performer, standPower);
                    default -> {}
                }

                punchedTarget = target;
                standPower.consumeStamina(10);
            }
        }

        protected void hitEntity(ActionTarget target, Level level, LivingEntity user, StandPower power) {
            Entity targetEntity = target.getMainEntity();
            if (targetEntity instanceof LivingEntity targetLiving) {
                DamageSource dmgSource = makePunchDamageSource();
                float dmgAmount = StandStatFormulas.getLightAttackDamage(power.getPowerType().getStandStats().power());
                if (standEntityAttack(user, targetLiving, dmgSource, dmgAmount)) {

                }
            }
        }

        public static boolean standEntityAttack(LivingEntity user, Entity target, DamageSource dmgSource, float dmgAmount) {
            boolean hurt = target.hurt(dmgSource, dmgAmount);
            if (hurt) {
                if (target instanceof LivingEntity targetLiving) {
                    if (user != null) {
                        LivingEntity aggroTo = targetLiving.hasLineOfSight(user) ? user :
                                StandUtil.isEntityStandUser(targetLiving) ? user : null;
                        if (aggroTo != null && aggroTo != dmgSource.getEntity()) {
                            Brain<?> brain = targetLiving.getBrain();
                            Optional<LivingEntity> brainAttackTarget = brain.getMemoryInternal(MemoryModuleType.ATTACK_TARGET);
                            if (brainAttackTarget != null && brainAttackTarget.filter(t -> t == dmgSource.getEntity()).isPresent()) {
                                brain.setMemory(MemoryModuleType.ATTACK_TARGET, aggroTo);
                            }
                        }
                    }
                }
            }
            return hurt;
        }

        protected void hitBlock(ActionTarget target, Level level, LivingEntity user, StandPower standPower) {
            BlockPos blockPos = target.getBlockPos();
            BlockState blockState = level.getBlockState(blockPos);
            if (JojoModUtil.canEntityDestroy(level, blockPos, blockState, user)) {
                double standStrength = standPower.getPowerType().getStandStats().power();
                float blockDamage = (float) standStrength * StandStatFormulas.getBlockMiningEfficiency(standStrength) * 0.05f;
                float blockHardness = StandStatFormulas.getBlockHardness(standStrength, blockState, level, blockPos);

                ServerBlockDestroyTracker.BlockBreakResult blockPunch = ServerBlockDestroyTracker.addBlockDestroyProgress((ServerLevel) level, user,
                        blockPos, blockState, blockDamage / blockHardness);
                if (blockPunch.progressNew >= 1) {
                    boolean dropBlock = !isUserCreative();
                    level.destroyBlock(blockPos, dropBlock, user);
                    blockDamage -= blockPunch.progressAdded * blockHardness;

                }
            }

        }

        @Override
        public boolean savePrevPoseForAnimTransition(EntityActionInstance prevAction) {
            return prevAction instanceof WhitePunch.Punch;
        }


    }



    public static ActionTarget aimAtPunchTarget(LivingEntity user) {
        return HitResultUtil.clipEntityLook(user, entity -> user.canAttackType(entity.getType()), 0);
    }

    protected String[] comboPunchNames;
    @Nullable
    protected Ability getComboPunch(StandPower standPower) {
        if (standPower == null) return null;

        Moveset moveset = standPower.getMoveset();
        LivingEntity standEntity = standPower.getUser();

        if (this.isSubAbility) return null;

        if (this.comboPunchNames == null) {
            String baseName = this.name();
            int punchesCount = 1;
            for (int i = 2; ; i++) {
                String comboPunchName = baseName + i;
                if (moveset.getAbility(comboPunchName) != null) {
                    punchesCount = i;
                }
                else break;
            }
            comboPunchNames = new String[punchesCount];
            comboPunchNames[0] = baseName;
            for (int i = 1; i < comboPunchNames.length; i++) {
                comboPunchNames[i] = baseName + String.valueOf(i + 1);
            }
        }

        int startFromPunch = 0;
        if (standEntity != null) {
            AbilityId curAbility = LivingComponentAction.getComponent(standEntity).comboString.getLast();
            if (curAbility != null) {
                String actionName = curAbility.nameInMoveset();
                for (int i = 0; i < comboPunchNames.length; i++) {
                    if (comboPunchNames[i].equals(actionName)) {
                        startFromPunch = i + 1;
                        break;
                    }
                }
            }
        }

        int size = comboPunchNames.length;
        for (int i = 0; i < size; i++) {
            int index = (startFromPunch + i) % size;
            String nextPunchName = comboPunchNames[index];
            Ability nextPunch = moveset.getAbility(nextPunchName);
            if (nextPunch != null && nextPunch.isAbilityAvailable(standPower)) {
                return nextPunch;
            }
        }

        return null;
    }
}