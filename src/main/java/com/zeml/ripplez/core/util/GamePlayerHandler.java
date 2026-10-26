package com.zeml.ripplez.core.util;

import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.LivingComponentAction;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTracker;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTracking;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.core.packets.server.EffectBombBlockPacket;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import com.zeml.ripplez.jojoimp.stands.killer_queen.KillerBombEffect;
import com.zeml.ripplez.powersystem.entityaction.ActionCL;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@EventBusSubscriber(modid = RipplesAddon.MOD_ID)
public class GamePlayerHandler {

    @SubscribeEvent
    public static void onEntityHurt(LivingIncomingDamageEvent event){
        LivingEntity user = event.getEntity();
        float amount = event.getOriginalAmount();
        LivingComponentAction actionComponent = LivingComponentAction.getComponent(user);
        EntityActionInstance curAction = actionComponent.getAction();
        if(actionComponent instanceof ActionCL actionCL){
            if(amount > actionCL.cancelAmount()){
                curAction.setPhase(ActionPhase.RECOVERY,0);
                curAction.syncPhaseChanges();
            }
        }
    }

    @SubscribeEvent
    public static void onUserBlock(UseItemOnBlockEvent event){
        ItemStack stack = event.getItemStack();
        ItemTracker tracker = ItemTracking.getItemTracker(stack,event.getLevel(),false);
        if(tracker != null && !event.getLevel().isClientSide){
            Player player = tracker.getTrackingPlayer((ServerLevel) event.getLevel());
            if(player != null){
                StandPower power = StandPower.get(player);
                if(power != null && event.getFace() != null){
                    power.userStandEffects.getEffectsOfType(AddonStandAbilities.KILLER_QUEEN_BOMB.get()).forEach(killerBombEffect -> {
                        if(killerBombEffect.getTargetType() == KillerBombEffect.TargetType.ITEM && killerBombEffect.getItemTracker().equals(tracker.trackerId)){
                            BlockPos truePos = getBlockPos(event);

                            power.userStandEffects.getEffectsOfType(AddonStandAbilities.KILLER_QUEEN_BOMB.get()).forEach(KillerBombEffect::remove);
                            KillerBombEffect newEffect = AddonStandAbilities.KILLER_QUEEN_BOMB.get().create(event.getLevel());
                            power.userStandEffects.addEffect(newEffect.withBlock(truePos));
                            PacketDistributor.sendToPlayer((ServerPlayer) player,new EffectBombBlockPacket(player.getId(),truePos));
                        }
                    });
                }
            }
        }
    }

    @NotNull
    private static BlockPos getBlockPos(UseItemOnBlockEvent event) {
        BlockPos truePos = event.getPos();
        switch (event.getFace()){
            case UP -> {truePos = truePos.above();}
            case DOWN -> {truePos = truePos.below();}
            case SOUTH -> {truePos = truePos.south();}
            case NORTH -> {truePos =truePos.north();}
            case EAST -> {truePos = truePos.east();}
            case WEST -> {truePos = truePos.west();}
        }
        return truePos;
    }


}