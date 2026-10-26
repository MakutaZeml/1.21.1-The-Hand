package com.zeml.ripplez.jojoimp.stands.killer_queen.client;

import com.github.standobyte.jojo.client.ui.marker.MarkerRenderer;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTrackDebugMarker;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTracker;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTracking;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import com.zeml.ripplez.jojoimp.stands.killer_queen.KillerBombEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class BombMarker extends MarkerRenderer {
    public BombMarker(Minecraft mc) {
        super("kq_put_bomb", mc);
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
        ItemTracking allTrackers = ItemTracking.getItemTracking(mc.level);

        LocalPlayer player = mc.player;
        StandPower power = StandPower.get(player);
        if(power != null && power.hasPower()){
            List<KillerBombEffect>  effects = power.userStandEffects.getEffectsOfType(AddonStandAbilities.KILLER_QUEEN_BOMB.get()).toList();
            for (KillerBombEffect effect: effects){
                if(effect.getPos() != null){
                    list.add(new MarkerInstance(effect.getPos()));
                }
            }
            for (ItemTracker tracker : allTrackers.values()) {
                if ("first_bomb".equals(tracker.context)) {
                    Vec3 pos = null;
                    Entity entity = tracker.getAtEntity(mc.level);
                    if (entity != null) {
                        pos = entityMarkerPos(entity, partialTick);
                    }
                    if (pos == null) {
                        BlockPos blockPos = tracker.getAtBlockPos();
                        if (blockPos != null) {
                            pos = blockMarkerPos(blockPos);
                        }
                    }
                    if (pos != null) {
                        list.add(new ItemTrackDebugMarker.ItemMarkerInstance(pos, false, tracker.getItem()));
                    }
                }
            }
        }
    }
}