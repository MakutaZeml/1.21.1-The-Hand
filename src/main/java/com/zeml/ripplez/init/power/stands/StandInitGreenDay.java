package com.zeml.ripplez.init.power.stands;

import com.github.standobyte.jojo.init.power.ModStandAbilities;
import com.github.standobyte.jojo.powersystem.MovesetBuilder;
import com.github.standobyte.jojo.powersystem.ability.controls.InputKey;
import com.github.standobyte.jojo.powersystem.ability.controls.InputMethod;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.standpower.StandStats;
import com.github.standobyte.jojo.powersystem.standpower.StandUnlockableSkill;
import com.github.standobyte.jojo.powersystem.standpower.entity.EntityStandType;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import static com.github.standobyte.jojo.init.power.ModStands.SWITCH_SPECIAL;
import static com.github.standobyte.jojo.init.power.ModStands.USE_SPECIAL;

public class StandInitGreenDay {


    @ApiStatus.Internal
    public static EntityStandType create(ResourceLocation id) {
        return new EntityStandType(
                new StandStats.Builder()
                        .power(15.7)
                        .speed(10)
                        .range(7, 10)
                        .durability(15)
                        .precision(4)
                        .build(),

                new MovesetBuilder()

                        .addHumanoidStandStuff()

                        .addAbility("punch", ModStandAbilities.PUNCH)
                        .addAbility("punch2", ModStandAbilities.PUNCH)
                        .addAbility("punch3", ModStandAbilities.PUNCH)
                        .addAbility("punch4", ModStandAbilities.PUNCH, punch -> {
                            punch.setDefaultPhaseLength(ActionPhase.WINDUP, 5);
                        })
                        .addAbility("barrage", ModStandAbilities.BARRAGE)
                        .addAbility("heavy_punch", ModStandAbilities.HEAVY_PUNCH)
                        .addAbility("gd_chop", AddonStandAbilities.CHOP, punch -> {
                            punch.initIsFinisher();
                        })
                        .addAbility("heavy_charged", AddonStandAbilities.CHOP_CHARGE)
                        .addAbility("grab", ModStandAbilities.GRAB)
                        .addAbility("grab_throw", ModStandAbilities.GRAB_THROW)
                        .addAbility("grab_punch", ModStandAbilities.PUNCH, punch -> {
                            punch.initIsGrabVariation();
                        })
                        .addAbility("grab_barrage", ModStandAbilities.BARRAGE, punch -> {
                            punch.initIsGrabVariation();
                        })
                        .addAbility("grab_heavy_punch", ModStandAbilities.HEAVY_PUNCH, punch -> {
                            punch.initIsGrabVariation();
                        })
                        .addAbility("grab_uppercut", AddonStandAbilities.CHOP_CHARGE, punch -> {
                            punch.initIsGrabVariation();
                            punch.initIsFinisher("grab_heavy_punch");
                        })
//
                        .addAbility("molding_time", AddonStandAbilities.MOLDING_TIME)



                        .makeControlScheme("hotbar")
                        .bind("punch", InputMethod.CLICK, InputKey.LMB)
                        .bind("barrage", InputMethod.HOLD, InputKey.LMB)
                        .bind("heavy_punch", InputMethod.CLICK, InputKey.RMB)
                        .bind("heavy_charged", InputMethod.HOLD, InputKey.RMB)
                        .bind("grab", InputMethod.CLICK, InputKey.RMB.withModifier(InputKey.Modifier.CONTROL))
                        .bind("grab_throw", InputMethod.HOLD, InputKey.RMB)

                        .makeHotbar(0, USE_SPECIAL, SWITCH_SPECIAL)
                        .addToHotbar("molding_time", 0, InputMethod.HOLD)
                        .finalizeControlScheme()


                        .addSkill(StandUnlockableSkill.startingAbility("punch"))
                        .addSkill(StandUnlockableSkill.startingAbility("barrage"))
                        .addSkill(StandUnlockableSkill.startingAbility("heavy_punch"))
                        .addSkill(StandUnlockableSkill.startingAbility("gd_chop").prerequisiteSkill("heavy_punch"))
                        .addSkill(StandUnlockableSkill.startingAbility("heavy_charged").prerequisiteSkill("heavy_punch"))

                        .addSkill(StandUnlockableSkill.startingAbility("grab"))
                        .addSkill(StandUnlockableSkill.unlockableAbility("grab_throw", 100).prerequisiteSkill("grab").setIncomplete())
                        .addSkill(StandUnlockableSkill.unlockableAbility("molding_time", 500))


                        .addSkill(StandUnlockableSkill.unlockableAbility("leap", 250).setNotYetImplemented())
                        .addHumanoidStandSkills()

                , id)
                .discTooltipWIP();
    }
}