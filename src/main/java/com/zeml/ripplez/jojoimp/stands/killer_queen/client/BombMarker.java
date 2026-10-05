package com.zeml.ripplez.jojoimp.stands.killer_queen.client;

import com.github.standobyte.jojo.client.ui.marker.MarkerRenderer;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import com.zeml.ripplez.jojoimp.stands.killer_queen.KillerBombEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class BombMarker extends MarkerRenderer {
    public BombMarker(Minecraft mc) {
        super("kq_explode_bomb", mc);
        renderThroughBlocks = true;
        useStandSkinColor = true;
    }

    @Override
    protected boolean shouldRender() {
        LocalPlayer player = mc.player;
        StandPower power = StandPower.get(player);
        if(power != null && power.hasPower()){
            return power.userStandEffects.getEffectsOfType(AddonStandAbilities.KILLER_QUEEN_BOMB.get()).findAny().isPresent();
        }
        return false;
    }

    @Override
    protected void updatePositions(List<MarkerInstance> list, float partialTick) {
        LocalPlayer player = mc.player;
        StandPower power = StandPower.get(player);
        if(power != null && power.hasPower()){
            List<KillerBombEffect>  effects = power.userStandEffects.getEffectsOfType(AddonStandAbilities.KILLER_QUEEN_BOMB.get()).toList();
            for (KillerBombEffect effect: effects){
                if(effect.getPos() != null){
                    list.add(new MarkerInstance(effect.getPos()));
                }
            }
        }
    }
}