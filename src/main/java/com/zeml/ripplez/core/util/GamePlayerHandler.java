package com.zeml.ripplez.core.util;

import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.LivingComponentAction;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.powersystem.entityaction.ActionCL;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.damagesource.DamageContainer;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;

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

}