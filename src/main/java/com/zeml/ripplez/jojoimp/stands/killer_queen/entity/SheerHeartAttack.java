package com.zeml.ripplez.jojoimp.stands.killer_queen.entity;

import com.github.standobyte.jojo.config.RotpConfig;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.StandUtil;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.init.AddonEntityTypes;
import com.zeml.ripplez.init.AddonSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Iterator;

public class SheerHeartAttack extends TamableAnimal {
    private static final EntityDataAccessor<Integer> DATA_SWELL_DIR = SynchedEntityData.defineId(SheerHeartAttack.class, EntityDataSerializers.INT);
    private int timer = 0;
    private int oldSwell;
    private int swell;
    private int maxSwell = 15;
    private int explosionRadius = 3;
    private BlockPos lastHotBlock;
    public SheerHeartAttack(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }


    public SheerHeartAttack(Level level, LivingEntity owner){
        super(AddonEntityTypes.SHA.get(),level);
        setOwnerUUID(owner.getUUID());

    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SWELL_DIR, -1);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 35.0).add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.MAX_HEALTH, 10.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(2, new SheerHeartSwelGoal(this));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1D));
        this.goalSelector.addGoal(1,new OwnerHurtByTargetGoal(this));
        this.goalSelector.addGoal(1,new OwnerHurtTargetGoal(this));
        //this.goalSelector.addGoal(1,new GoToHotBlockGoal(this,1,5));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Monster.class, true));

    }

    @Override
    public boolean isDamageSourceBlocked(DamageSource damageSource) {
        return !damageSource.is(DamageTypes.FELL_OUT_OF_WORLD);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.isAlive()) {
            if(!this.isNoAi()){
                this.oldSwell = this.swell;
                int i = this.getSwellDir();
                if (i > 0 && this.swell == 0) {
                    if(!this.level().isClientSide){
                        if(this.getOwner() != null){
                            StandPower standPower = StandPower.get(this.getOwner());
                            if(standPower != null){
                                StandUtil.broadcastSound((ServerLevel) level(), this.position(),
                                        AddonSoundEvents.DETONATE, true, standPower,
                                        this.getSoundSource(), 1, 0.5f);
                            }
                        }else {
                            this.playSound(AddonSoundEvents.DETONATE.get(), 1.0F, 0.5F);
                        }
                    }
                }
                this.swell += i;
                if (this.swell < 0) {
                    this.swell = 0;
                }
                if (this.swell >= this.maxSwell) {
                    this.swell = this.maxSwell;
                    this.explodeCreeper();

                }
            }else {
                this.timer +=1;
                if(this.timer >=200){
                    this.timer = 0;
                }
            }
        }
        if(this.getTarget() != null && !this.getTarget().isAlive()){
            setTarget(null);
        }
    }

    @Override
    protected int getBaseExperienceReward() {
        return 0;
    }

    public void setSwellDir(int state) {
        this.entityData.set(DATA_SWELL_DIR, state);
    }

    public int getSwellDir() {
        return this.entityData.get(DATA_SWELL_DIR);
    }

    private void explodeCreeper() {
        if (!this.level().isClientSide) {
            this.dead = true;
            LivingEntity explo = this.getOwner() == null?this:this.getOwner();
            Level.ExplosionInteraction interaction = Level.ExplosionInteraction.MOB;
            if(getOwner() != null){
                interaction = RotpConfig.canStandBreakBlocks(getOwner())? Level.ExplosionInteraction.MOB: Level.ExplosionInteraction.NONE;
            }
            this.level().explode(explo, this.getX(), this.getY(), this.getZ(), (float)this.explosionRadius,interaction);
            if(this.getTarget() != null && getTarget() == this){
                if(!this.getTarget().isAlive()){
                    this.remove(RemovalReason.DISCARDED);
                }
            }else if(getTarget() == this){
                this.setTarget(null);
            }
            this.spawnLingeringCloud();
            if(this.getTarget() != null && this.getTarget().isAlive()){
                this.setNoAi(true);
                timer = 0;
            }

        }

    }


    private void spawnLingeringCloud() {
        Collection<MobEffectInstance> collection = this.getActiveEffects();
        if (!collection.isEmpty()) {
            AreaEffectCloud areaeffectcloud = new AreaEffectCloud(this.level(), this.getX(), this.getY(), this.getZ());
            areaeffectcloud.setRadius(2.5F);
            areaeffectcloud.setRadiusOnUse(-0.5F);
            areaeffectcloud.setWaitTime(10);
            areaeffectcloud.setDuration(areaeffectcloud.getDuration() / 2);
            areaeffectcloud.setRadiusPerTick(-areaeffectcloud.getRadius() / (float)areaeffectcloud.getDuration());
            Iterator var3 = collection.iterator();

            while(var3.hasNext()) {
                MobEffectInstance mobeffectinstance = (MobEffectInstance)var3.next();
                areaeffectcloud.addEffect(new MobEffectInstance(mobeffectinstance));
            }

            this.level().addFreshEntity(areaeffectcloud);
        }

    }

    public BlockPos getLastHotBlock() {
        return lastHotBlock;
    }

    public void setLastHotBlock(BlockPos lastHotBlock) {
        this.lastHotBlock = lastHotBlock;
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Nullable
    @Override
    public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }


}