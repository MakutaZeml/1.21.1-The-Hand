package com.zeml.ripplez.jojoimp.stands.zh;

import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.entityaction.ActionOBB;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.StandUtil;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntityAbility;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandOffsetFromUser;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandStatFormulas;
import com.github.standobyte.jojo.subsystems.hitboxes.ExtendableOBB;
import com.github.standobyte.jojo.subsystems.hitboxes.OBBCollisionUtil;
import com.github.standobyte.jojo.subsystems.hitboxes.OrientedBoundingBox;
import com.github.standobyte.jojo.util.functions.MathUtil;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.init.AddonSoundEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EraseSpaceAbility extends StandEntityAbility {


    public EraseSpaceAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, EraseSpace::new);
        usageGroup = AbilityUsageGroup.UTILITY;
        setDefaultPhaseLength(ActionPhase.BUTTON_CHARGE, 21);
        setButtonHoldPhase(ActionPhase.WINDUP);
        setDefaultPhaseLength(ActionPhase.PERFORM, 1);
        setDefaultPhaseLength(ActionPhase.RECOVERY, 12);
    }

    @Override
    public ConditionCheck checkSpecificConditions(Power<?> context) {
        StandPower standPower = PowerClass.STAND.cast(context);
        if(standPower != null && standPower.hasPower() && standPower.getSummonedStand() instanceof StandEntity stand){
            if(stand.getUser() != null && stand.getUser().distanceTo(stand) < 2){
                return ConditionCheck.POSITIVE;
            }
        }
        return ConditionCheck.NEGATIVE;
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


    public static class EraseSpace extends EntityActionInstance implements ActionOBB{
        private ExtendableOBB extendableOBB;
        private Vec3 finalPos;
        public EraseSpace(EntityActionType ability) {
            super(ability);
        }


        @Override
        public void onActionSet(@Nullable EntityActionInstance prevAction) {
            OrientedBoundingBox obb = new OrientedBoundingBox(new Vec3(0, 1.35, 0), 1d, 1d, 1d, getPerformer().getYRot(), getPerformer().getXRot());
            float speed = 0.28125f;
            if(performer instanceof  StandEntity stand){
                speed *= 20/StandStatFormulas.getChargedHeavyButtonWindup(stand.getAttackSpeed(), stand.getFinisherMeter());
            }
            this.extendableOBB = new ExtendableOBB(obb, speed, phasesLength.get(ActionPhase.BUTTON_CHARGE).intValue()+phasesLength.get(ActionPhase.PERFORM).intValue(),
                    phasesLength.get(ActionPhase.PERFORM).intValue(),
                    new Vec3(0, 1.35, 0));
        }

        @Override
        public void actionTick() {
            if(this.extendableOBB() != null){
                this.extendableOBB().tick();
                Vec3 pos = getPerformer().position();
                Vec3 offset = new Vec3(0.0, 1.5, 0.2)
                        .yRot(-getPerformer().yBodyRot * MathUtil.DEG_TO_RAD);
                this.extendableOBB().updatePosition(level(), pos, offset, getPerformer().getXRot(), getPerformer().getYRot());
                finalPos = this.extendableOBB().rotatableHitbox().center.add(getPerformer().getLookAngle().scale(extendableOBB().rotatableHitbox().extent.length()));
                if (getPhase() == ActionPhase.WINDUP) {
                    if (curPhaseTick > 21) {
                        this.extendableOBB().setIsMovingForward(false);
                    }
                }
            }
        }

        @Override
        public void onButtonStopHold() {
            if(getPhase() == ActionPhase.BUTTON_CHARGE || getPhase() == ActionPhase.WINDUP){
                setPhase(ActionPhase.PERFORM,0);
                if(extendableOBB() != null){
                    this.extendableOBB.setIsMovingForward(false);
                }
                syncPhaseChanges();

            }
        }

        @Override
        public void onSetPhase(ActionPhase newPhase) {
            switch (newPhase){
                case BUTTON_CHARGE ->{
                    setStandOffset(0, 1,
                            StandOffsetFromUser.Rotations.HEAD_XY,
                            false);
                }
                case RECOVERY -> {
                    this.extendableOBB = null;
                }
            }
        }

        @Override
        public void actionPerformStart() {
            List<? extends Entity> entities = OBBCollisionUtil.getEntitiesInOBB(level(), this.extendableOBB().rotatableHitbox(), entity -> entity != getPerformer() && entity != getPowerUser() && entity instanceof LivingEntity);
            if(performer instanceof StandEntity stand){
                LivingEntity user = stand.getUser();
                if(user != null){
                    if(entities.isEmpty()){
                        BlockHitResult result = level().clip(new ClipContext(extendableOBB().rotatableHitbox().center, finalPos, ClipContext.Block.COLLIDER, ClipContext.Fluid.ANY, CollisionContext.empty()));
                        BlockState blockCollision = OBBCollisionUtil.getCollidingBlock(level(), result.getBlockPos());
                        if (blockCollision != null){
                            switch (result.getDirection()){
                                case UP -> {
                                    if(!level().isClientSide){
                                        Vec3 position = result.getBlockPos().above().getBottomCenter();
                                        teleport(user,position,stand,level());
                                    }
                                }case DOWN -> {
                                    if(!level().isClientSide){
                                        Vec3 position = result.getBlockPos().below().getBottomCenter();
                                        teleport(user,position,stand,level());
                                    }
                                }
                                case WEST -> {
                                    if(!level().isClientSide){
                                        Vec3 position = result.getBlockPos().west().getBottomCenter();
                                        teleport(user,position,stand,level());
                                    }
                                }
                                case EAST -> {
                                    if(!level().isClientSide){
                                        Vec3 position = result.getBlockPos().east().getBottomCenter();
                                        teleport(user,position,stand,level());
                                    }
                                }
                                case NORTH -> {
                                    if(!level().isClientSide){
                                        Vec3 position = result.getBlockPos().north().getBottomCenter();
                                        teleport(user,position,stand,level());
                                    }
                                }
                                case SOUTH -> {
                                    if(!level().isClientSide){
                                        Vec3 position = result.getBlockPos().south().getBottomCenter();
                                        teleport(user,position,stand,level());
                                    }
                                }
                            }

                        }else {
                            if(!level().isClientSide){
                                teleport(user,finalPos,stand,level());
                            }
                        }
                    }else {
                        Entity closest = entities.getFirst();
                        for (Entity entity: entities){
                            if(entity.distanceTo(performer)<closest.distanceTo(performer)){
                                closest = entity;
                            }
                        }
                        if (!level().isClientSide) {
                            Vec3 position = this.extendableOBB().rotatableHitbox().center.add(getPerformer().getLookAngle().scale(closest.distanceTo(performer) - 1));
                            teleport(user,new Vec3(position.x,closest.getY(),position.z),stand,level());
                        }
                    }
                }
            }
        }

        private static void teleport(LivingEntity user, Vec3 position, StandEntity stand, Level level){
            user.teleportTo(position.x, position.y, position.z);
            user.fallDistance = 0;
            if(stand.getUserPower() != null){
                StandUtil.broadcastSound((ServerLevel) level, stand.position(),
                        AddonSoundEvents.ERASURE, true, stand.getUserPower(),
                        stand.getSoundSource(), 1, 1);
                stand.getUserPower().addExp(10);
            }

        }

        @Override
        public ExtendableOBB extendableOBB() {
            return this.extendableOBB;
        }
    }

}
