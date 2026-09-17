package com.zeml.ripplez.jojoimp.stands.green_day;

import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffect;
import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffectType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.effect.StandEffectInstance;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MoldUserEffect extends StandEffectInstance {
    public MoldUserEffect(@NotNull EntityCustomEffectType<?> effectType) {
        super(effectType);
        needsTarget = false;
    }

    @Override
    protected void start() {
        RipplesAddon.getLogger().debug("starting");
    }

    @Override
    protected void tick() {
        if(getStandUser() instanceof LivingEntity user && userPower != null){
            LivingEntity center = user;
            if(userPower.getSummonedStandEntity() instanceof StandEntity stand){
                center = stand;
            }
            infection(userPower,level,user,center);
            if(!userPower.isSummoned()){
                this.remove();
            }
        }

    }

    public static void infection(@NotNull StandPower userPower, Level level, LivingEntity user, LivingEntity centerEntity){
        if(userPower.getPowerType() != null && userPower.getPowerType().getStandStats() != null){
            double range =  userPower.getPowerType().getStandStats().rangeMax();
            List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class,centerEntity.getBoundingBox().inflate(range),
                    livingEntity -> livingEntity.isAlive() && !livingEntity.isAlliedTo(user) && !livingEntity.is(user));
            StandEntity standEntity = userPower.getSummonedStandEntity();
            RipplesAddon.getLogger().debug("Epstein {}", entities);
            List<MoldVictimEffect> effects = userPower.userStandEffects.getEffectsOfType(AddonStandAbilities.SPREAD_MOLD.get()).toList();
            List<LivingEntity> curTargets = new ArrayList<>();
            for (MoldVictimEffect effect: effects){
                curTargets.add(effect.getTargetLiving()) ;
            }

            for (LivingEntity victim: entities){
                if(standEntity != null && standEntity == victim){
                    continue;
                }
                if(centerEntity == victim){
                    continue;
                }
                if(!curTargets.contains(victim)){
                    MoldVictimEffect moldUserEffect = AddonStandAbilities.SPREAD_MOLD.get().create(level);
                    moldUserEffect.withTarget(victim);
                    userPower.userStandEffects.addEffect(moldUserEffect);
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
        RipplesAddon.getLogger().debug("sexmoved");
        if(userPower != null){
            userPower.userStandEffects.getEffectsOfType(AddonStandAbilities.SPREAD_MOLD.get()).forEach(MoldVictimEffect::remove);
        }
    }
}