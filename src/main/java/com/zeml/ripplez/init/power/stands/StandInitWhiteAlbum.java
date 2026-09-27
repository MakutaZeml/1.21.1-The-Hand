package com.zeml.ripplez.init.power.stands;

import com.github.standobyte.jojo.init.power.ModStandAbilities;
import com.github.standobyte.jojo.powersystem.MovesetBuilder;
import com.github.standobyte.jojo.powersystem.ability.controls.InputKey;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.standpower.StandStats;
import com.github.standobyte.jojo.powersystem.standpower.StandUnlockableSkill;
import com.github.standobyte.jojo.powersystem.standpower.type.StandType;
import com.github.standobyte.jojo.powersystem.standpower.type.SummonedStand;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import com.zeml.ripplez.powersystem.standpower.type.WhiteAlbumType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

public class StandInitWhiteAlbum {
    public static final String SKIN_VIEW_TYPE = "zwhite_album";
    @ApiStatus.Internal
    public static StandType create(ResourceLocation id) {
        return new WhiteAlbumType(new StandStats.Builder()
                .power(14)
                .speed(10)
                .range(10, 10)
                .durability(18)
                .precision(3.0)
                .build(),

                new MovesetBuilder()
                        .addAbility("punch", AddonStandAbilities.WHITE_PUNCH)
                        .addAbility("punch2", AddonStandAbilities.WHITE_PUNCH)
                        .addAbility("punch3", AddonStandAbilities.WHITE_PUNCH)
                        .addAbility("punch4", AddonStandAbilities.WHITE_PUNCH, punch -> {
                            punch.setDefaultPhaseLength(ActionPhase.WINDUP, 5);
                        })
                        .makeControlScheme("hotbar")
                        .bind("punch", InputMethod.CLICK, InputKey.LMB)

                        //.makeHotbar(0, InputKey.X, InputKey.C)
                        .finalizeControlScheme()

                        .addSkill(StandUnlockableSkill.startingAbility("punch"))

                ,id).init(stand -> {
                    stand.makeSummonedStandObj = SummonedStand.SyncableSummonedStand::new;
                    stand.skinUIType = SKIN_VIEW_TYPE;
                }).discTooltipWIP(true);


    }
}