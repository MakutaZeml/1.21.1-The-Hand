package com.zeml.ripplez.jojoimp.stands.killer_queen;

import com.github.standobyte.jojo.customobjects.DamageSourceModified;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTracker;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTracking;
import com.github.standobyte.jojo.subsystems.target.ActionTarget;
import com.github.standobyte.jojoimpl.stands._entitybase.StandEntityHeavyPunchAbility;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.core.packets.server.EffectBombItemPacket;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;

public class KQFinisherAbility extends StandEntityHeavyPunchAbility {


    public KQFinisherAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
        createActionObj= PutArmorBomb::new;
    }

    public static class PutArmorBomb extends StandEntityHeavyPunch {
        public PutArmorBomb(EntityActionType ability) {
            super(ability);
        }

        @Override
        protected void addKnockback(DamageSource dmgSource) {
            DamageSourceModified knockback = (DamageSourceModified) dmgSource;
            knockback.jojo_ripples$modifyKnockback(0f, .25f);
        }

        @Override
        protected void hitEntity(ActionTarget target, Level level, StandEntity stand, DamageSource dmgSource, float dmgAmount, float explRadius) {
            List<ItemStack> armor = new ArrayList<>();
            if(target.getEntity() instanceof LivingEntity living){
                living.getArmorSlots().forEach(stack ->{
                    if(!stack.isEmpty()){
                        if(stack.getItem() instanceof Equipable){
                            armor.add(stack);

                        }
                    }
                });
                RipplesAddon.getLogger().debug("WTF?!?!, {}", armor);
                if(!armor.isEmpty()){
                    int random = (int) Math.floor(armor.size()*Math.random());
                    ItemStack track = armor.get(random);
                    RipplesAddon.getLogger().debug("WTF {} {} ", random,track);
                    if(!level.isClientSide){
                        if(stand.getUser() instanceof Player player && track.getItem() instanceof Equipable equipable){
                            ServerLevel serverLevel = (ServerLevel) level;
                            ItemTracking trackingSystem = ItemTracking.getItemTracking(level);
                            ItemTracker tracker = trackingSystem.startTracking(track, serverLevel);
                            tracker.context = "first_bomb";
                            tracker.setTrackedByPlayer(player);
                            EquipmentSlot slot = equipable.getEquipmentSlot();
                            living.setItemSlot(slot, track);
                            StandPower power = StandPower.get(player);
                            if(power != null){
                                power.userStandEffects.getEffectsOfType(AddonStandAbilities.KILLER_QUEEN_BOMB.get()).forEach(KillerBombEffect::remove);
                                KillerBombEffect newEffect = AddonStandAbilities.KILLER_QUEEN_BOMB.get().create(level);
                                power.userStandEffects.addEffect(newEffect.withTracker(tracker.trackerId));
                                PacketDistributor.sendToPlayer((ServerPlayer) player,new EffectBombItemPacket(player.getId(),tracker.trackerId));
                            }
                        }
                    }

                }
            }


            super.hitEntity(target, level, stand, dmgSource, dmgAmount/4, explRadius);
        }
    }

}