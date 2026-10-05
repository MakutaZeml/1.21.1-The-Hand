package com.zeml.ripplez.jojoimp.stands.killer_queen;

import com.github.standobyte.jojo.config.RotpConfig;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.ability.*;
import com.github.standobyte.jojo.powersystem.ability.condition.AvailableAbilities;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.ability.input.ActionInputBuffer;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.HeldInput;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.StandUtil;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTracker;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTracking;
import com.github.standobyte.jojo.util.functions.DamageUtil;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.init.AddonDamageTypes;
import com.zeml.ripplez.init.AddonSoundEvents;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import com.zeml.ripplez.powersystem.entityaction.ActionCL;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.item.PrimedTnt;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class KQExplodeBomb extends EntityActionAbility {
    public KQExplodeBomb(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, ExplodeFirstBomb::new);
        usageGroup = AbilityUsageGroup.COMBAT;
        setDefaultPhaseLength(ActionPhase.BUTTON_CHARGE, 21);
        setDefaultPhaseLength(ActionPhase.PERFORM, 2);
        setDefaultPhaseLength(ActionPhase.RECOVERY, 12);
    }


    @Override
    public HeldInput onKeyPress(Level level, LivingEntity user, FriendlyByteBuf extraClientInput, InputMethod inputMethod, float clickHoldResolveTime, ActionInputBuffer.BufferingState bufferingState) {
        StandPower power = PowerClass.STAND.get(user); if (power == null) return null;
        StandEntity standEntity = power.getSummonedStandEntity();
        if (standEntity == null){
            return super.onKeyPress(level, user, extraClientInput, inputMethod, clickHoldResolveTime, bufferingState);
        }
        return setOrBufferAction(level, user, standEntity, inputMethod, extraClientInput, clickHoldResolveTime, bufferingState);
    }


    @Nullable
    @Override
    public Ability replaceWithSubAbility(Power<?> context, AvailableAbilities abilities) {
        StandPower standPower = PowerClass.STAND.cast(context);
        if(standPower != null){
            if(standPower.userStandEffects.getEffectOfType(AddonStandAbilities.KILLER_QUEEN_BOMB.get()).isPresent()){
                abilities.replaceOtherAbilityWith(context,"kq_put_bomb",this);
            }
        }
        return super.replaceWithSubAbility(context, abilities);
    }

    @Override
    protected LivingEntity getPerformer(LivingEntity user) {
        StandEntity stand = StandUtil.getSummonedStand(user);
        return stand != null? stand:user;
    }

    public static class ExplodeFirstBomb extends EntityActionInstance implements ActionCL {
        public ExplodeFirstBomb(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void actionPerformStart() {
            LivingEntity living = getPerformer();
            StandPower standPower;
            if(living instanceof StandEntity stand){
                standPower = stand.getUserPower();
            }else {
                standPower = StandPower.get(living);
            }
            if(standPower != null){
                LivingEntity user = standPower.getUser();
                Optional<KillerBombEffect> bombOptional = standPower.userStandEffects.getEffectOfType(AddonStandAbilities.KILLER_QUEEN_BOMB.get());
                bombOptional.ifPresent(killerBombEffect -> {
                    switch (killerBombEffect.getTargetType()){
                        case ENTITY -> {
                            Entity entity = killerBombEffect.getTarget();
                            if(entity.isAlive()){
                                jankKQExplosion(level(),user,standPower,true,entity,null, null);
                                killerBombEffect.remove();
                            }
                        }
                        case BLOCK -> {
                            BlockPos blockPos = killerBombEffect.getBlockPos();
                            jankKQExplosion(level(),user,standPower,true,null,blockPos, null);
                            killerBombEffect.remove();
                        }
                        case ITEM -> {

                        }
                    }
                });
            }
        }

        @Override
        public float cancelAmount() {
            return 16;
        }
    }




    public static void jankKQExplosion(Level level, LivingEntity user,StandPower standPower, boolean byKillerQueen
                                      ,@Nullable Entity target, @Nullable BlockPos blockPos, @Nullable Vec3 position){
        double radius;
        double damageRadius;
        Vec3 pos;

        if(!level.isClientSide){
            StandUtil.broadcastSound((ServerLevel) level, user.position(),
                    AddonSoundEvents.DETONATE, true, standPower,
                    user.getSoundSource(), 1, 1);
        }

        if(target != null){
            pos = target.position();
            damageRadius =(1.5f*Math.sqrt(target.getBbHeight()*Math.pow(target.getBbWidth(),2)/.648));
            if(standPower.getPowerType() != null && standPower.getPowerType().getStandStats() != null){
                radius = Math.min(damageRadius,standPower.getPowerType().getStandStats().rangeMax());
            }else {
                radius = damageRadius;
            }
        } else {
            radius = damageRadius = 3;
            if(blockPos != null){
                pos = blockPos.getCenter();
            } else if (position != null) {
                pos = position;
            } else {
                pos = Vec3.ZERO;
            }
        }
        AABB aabb = new AABB(
                pos.x-radius,
                pos.y-radius,
                pos.z-radius,
                pos.x +radius,
                pos.y+radius,
                pos.z+radius
        );


        List<Entity> entities = level.getEntities(user,aabb).stream().filter(entity -> !entity.getUUID().equals(user.getUUID()) && !(entity instanceof ItemEntity))
                .toList();

        if(!entities.isEmpty()){
            entities.forEach(entity -> {
                if(isThisTrue(entity,target,user,byKillerQueen)){
                    double distRatio = Math.sqrt(entity.distanceToSqr(pos)) /(Math.round(radius)*2);
                    Vec3 entityPos = entity instanceof PrimedTnt ? entity.position() : entity.getEyePosition(1.0F);
                    Vec3 diff = entityPos.subtract(pos);
                    double lengthSqr = diff.lengthSqr();
                    if (lengthSqr != 0.0) {
                        diff = diff.scale(1 / Math.sqrt(lengthSqr));

                        double seenPercent = getSeenPercent(pos, entity, blockPos);
                        double impact = (1.0 - distRatio) * seenPercent;
                        double knockback = impact;
                        if (entity instanceof LivingEntity livingentity) {
                            knockback *= 1.0 - livingentity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE);
                        }
                        diff = diff.scale(knockback);
                        float damage = getEntityDamageAmount(impact,damageRadius*2);
                        if (damage > 0) {
                            if(!level.isClientSide){
                                hurtEntity(entity, damage, diff,user);
                            }

                        }
                    }
                }
            });

        }
        if(!level.isClientSide){
            StandUtil.broadcastSound((ServerLevel) level, pos,
                    SoundEvents.GENERIC_EXPLODE, true, standPower,
                    user.getSoundSource(), 1, 1);
            if(blockPos != null){
                level.destroyBlock(blockPos, false,user);
            }
        }
        standPower.userStandEffects.getEffectsOfType(AddonStandAbilities.KILLER_QUEEN_BOMB.get()).forEach(KillerBombEffect::remove);
    }

    private static boolean isThisTrue(Entity entity, @Nullable Entity target,LivingEntity user ,boolean byKillerQueen){
        boolean basic = entity != user && !(entity instanceof StandEntity stand && stand.getUser().getUUID().equals(user.getUUID()));
        if(target != null){
            return ((entity != target && !byKillerQueen) || (entity == target && byKillerQueen)) && basic;
        }
        return basic;
    }

    public static float getEntityDamageAmount( double impact, double diameter) {
        return (float)((impact * impact + impact) / 2.0 * 6.0 * diameter);
    }

    protected static void hurtEntity(Entity entity, float damage, Vec3 knockbackVec, LivingEntity user) {
        var damageType = DamageUtil.type(user.level(), AddonDamageTypes.KQ_EXPLOSION);
        DamageSource dmgSource = new DamageSource(damageType, user);
        entity.hurt(dmgSource, damage);
        entity.setDeltaMovement(entity.getDeltaMovement().add(knockbackVec));

    }

    public static float getSeenPercent(Vec3 explosionVector, Entity entity,@Nullable BlockPos blockPos) {
        if(blockPos == null){
            return Explosion.getSeenPercent(explosionVector,entity);
        }
        AABB aabb = entity.getBoundingBox();
        double d0 = 1.0 / ((aabb.maxX - aabb.minX) * 2.0 + 1.0);
        double d1 = 1.0 / ((aabb.maxY - aabb.minY) * 2.0 + 1.0);
        double d2 = 1.0 / ((aabb.maxZ - aabb.minZ) * 2.0 + 1.0);
        double d3 = (1.0 - Math.floor(1.0 / d0) * d0) / 2.0;
        double d4 = (1.0 - Math.floor(1.0 / d2) * d2) / 2.0;
        if (!(d0 < 0.0) && !(d1 < 0.0) && !(d2 < 0.0)) {
            int i = 0;
            int j = 0;

            for(double d5 = 0.0; d5 <= 1.0; d5 += d0) {
                for(double d6 = 0.0; d6 <= 1.0; d6 += d1) {
                    for(double d7 = 0.0; d7 <= 1.0; d7 += d2) {
                        double d8 = Mth.lerp(d5, aabb.minX, aabb.maxX);
                        double d9 = Mth.lerp(d6, aabb.minY, aabb.maxY);
                        double d10 = Mth.lerp(d7, aabb.minZ, aabb.maxZ);
                        Vec3 vec3 = new Vec3(d8 + d3, d9, d10 + d4);

                        BlockHitResult blockHitResult = entity.level().clip(new ClipContext(vec3, explosionVector, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, entity));

                        if (blockHitResult.getType() == HitResult.Type.MISS) {
                            ++i;
                        }else if(blockHitResult.getType() == HitResult.Type.BLOCK){
                            BlockPos newPost = blockHitResult.getBlockPos();
                            RipplesAddon.getLogger().debug("WHY {} {} {}",newPost, blockPos.mutable(),newPost ==  blockPos.mutable());
                            if(newPost.equals(blockPos)){
                                ++i;
                            }
                        }
                        ++j;
                        RipplesAddon.getLogger().debug("ñayos {} {}", i,j);
                    }
                }
            }

            return (float)i / (float)j;
        } else {
            return 0.0F;
        }
    }
}