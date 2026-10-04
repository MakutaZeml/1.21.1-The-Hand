package com.zeml.ripplez.powersystem.standpower.type;

import com.github.standobyte.jojo.init.ModSoundEvents;
import com.github.standobyte.jojo.mechanics.voiceline.VoiceLineServerSide;
import com.github.standobyte.jojo.powersystem.MovesetBuilder;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.StandStats;
import com.github.standobyte.jojo.powersystem.standpower.type.StandType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class WhiteAlbumType extends StandType {
    public WhiteAlbumType(StandStats stats, MovesetBuilder moveset, ResourceLocation id) {
        super(stats, moveset, id);
    }

    @Override
    public boolean summon(LivingEntity user, StandPower standPower) {
        if(!user.level().isClientSide){
            if (!user.isShiftKeyDown()) {
                VoiceLineServerSide.play(user, ModSoundEvents.VOICELINE_STAND_SUMMON);
            }
        }
        return super.summon(user, standPower);
    }
}
