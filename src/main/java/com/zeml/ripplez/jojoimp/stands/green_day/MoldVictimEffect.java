package com.zeml.ripplez.jojoimp.stands.green_day;

import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffectType;
import com.github.standobyte.jojo.powersystem.standpower.effect.StandEffectInstance;
import com.github.standobyte.jojo.util.functions.DamageUtil;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.init.AddonDamageTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class MoldVictimEffect extends StandEffectInstance {
    int maxHeight;
    public MoldVictimEffect(@NotNull EntityCustomEffectType<?> effectType) {
        super(effectType);
        removeOnUserDeath = true;
        needsTarget = true;
    }

    @Override
    protected void start() {
        if(getTargetLiving() instanceof LivingEntity target){
            maxHeight = target.getBlockY();
        }
    }

    @Override
    protected void tick() {
        if(getTargetLiving() instanceof  LivingEntity target && getStandUser() instanceof LivingEntity user && userPower != null){
            MoldUserEffect.infection(userPower,level,user,target);
            if(maxHeight < target.getBlockY()){
                maxHeight = target.getBlockY();
            }else {
                if(tickCount%25==0 && maxHeight != target.getBlockY()){
                    var damageType = DamageUtil.type(level, AddonDamageTypes.MOLD);
                    DamageSource dmgSource = new DamageSource(damageType, user);
                    target.hurt(dmgSource,maxHeight-target.getBlockY());
                }
            }
        }
    }

    @Override
    protected void stop() {

    }

    @Override
    public void remove() {
        super.remove();
        RipplesAddon.getLogger().debug("quiting? {}", maxHeight);
    }

    @Override
    protected void writeAdditionalSaveData(CompoundTag nbt) {
        super.writeAdditionalSaveData(nbt);
        nbt.putInt("maxHeight",maxHeight);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        maxHeight = nbt.getInt("maxHeight");
    }
}