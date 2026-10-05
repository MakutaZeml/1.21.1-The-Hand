package com.zeml.ripplez.jojoimp.stands.killer_queen.client;

import com.github.standobyte.jojo.client.standskin.StandSkin;
import com.github.standobyte.jojo.client.ui.marker.MarkerRenderer;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTrackDebugMarker;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTracker;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTracking;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import com.zeml.ripplez.jojoimp.stands.killer_queen.KillerBombEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Optional;

public class ItemBombMarker extends MarkerRenderer {

    public ItemBombMarker(Minecraft mc) {
        super("kq_explode_bomb", mc);
        renderThroughBlocks = true;
        useStandSkinColor = true;
    }

    @Override
    protected boolean shouldRender() {
        return true;
    }



    @Override
    protected void updatePositions(List<MarkerInstance> list, float partialTick) {
        ItemTracking allTrackers = ItemTracking.getItemTracking(mc.level);
        Player player = mc.player;
        StandPower standPower = StandPower.get(player);
        if(standPower != null){
            Optional<KillerBombEffect> optional = standPower.userStandEffects.getEffectOfType(AddonStandAbilities.KILLER_QUEEN_BOMB.get());
            if(optional.isPresent()){
                for (ItemTracker tracker : allTrackers.values()) {
                    if ("first_bomb".equals(tracker.context) && tracker.trackerId == optional.get().getItemTracker()) {
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
}