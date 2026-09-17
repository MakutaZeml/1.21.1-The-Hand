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

public class StandInitWhiteSnake {

    @ApiStatus.Internal
    public static EntityStandType create(ResourceLocation id){
        return new EntityStandType(
                new StandStats.Builder()
                        .power(16.5)
                        .speed(12)
                        .range(15, 20)
                        .durability(17)
                        .precision(8)
                        .build(),

                new MovesetBuilder()

                        .addHumanoidStandStuff()


                        .addAbility("punch", ModStandAbilities.PUNCH)
                        .addAbility("punch2", ModStandAbilities.PUNCH)
                        .addAbility("punch3", ModStandAbilities.PUNCH)
                        .addAbility("punch4", ModStandAbilities.PUNCH, punch -> {
                            punch.setDefaultPhaseLength(ActionPhase.WINDUP, 5);
                        })
                        .addAbility("heavy_punch", ModStandAbilities.HEAVY_PUNCH)

                        .addAbility("grab",ModStandAbilities.GRAB)

                        .addAbility("block_toss", ModStandAbilities.BLOCK_TOSS)

                        .addAbility("put_disc", AddonStandAbilities.PUT_DISC)
                        .addAbility("take_memory",AddonStandAbilities.TAKE_OUT_MEMORY, take -> take.isSubAbility = true)
                        .addAbility("discfy",AddonStandAbilities.DISCFY)
                        .addAbility("take_stand",AddonStandAbilities.TAKE_OUT_STAND, take -> take.isSubAbility=true)

                        .addAbility("barrage", ModStandAbilities.BARRAGE)

                        .addAbility("grab_barrage", ModStandAbilities.BARRAGE)
                        .addAbility("user_throw_disc",AddonStandAbilities.USER_THROW_DISC,disc -> disc.isSubAbility=true)

                        .makeControlScheme("hotbar")
                        .bind("punch", InputMethod.CLICK, InputKey.LMB)
                        .bind("barrage", InputMethod.HOLD, InputKey.LMB)
                        .bind("heavy_punch", InputMethod.CLICK, InputKey.RMB)
                        .bind("grab", InputMethod.CLICK, InputKey.RMB.withModifier(InputKey.Modifier.CONTROL))
                        .bind("block_toss", InputMethod.HOLD, InputKey.LMB)
                        .bind("put_disc",InputMethod.CLICK,InputKey.RMB)
                        .bind("discfy", InputMethod.HOLD, InputKey.RMB)

                        .makeHotbar(0, InputKey.X, InputKey.C)

                        .finalizeControlScheme()


                        .addAbility("throw_disc", AddonStandAbilities.THROW_DISC)
                        .inHotbar(0, InputMethod.CLICK)

                        .addSkill(StandUnlockableSkill.startingAbility("punch"))
                        .addSkill(StandUnlockableSkill.startingAbility("barrage"))
                        .addSkill(StandUnlockableSkill.startingAbility("heavy_punch"))
                        .addSkill(StandUnlockableSkill.startingAbility("grab"))
                        .addSkill(StandUnlockableSkill.startingAbility("block_toss").setIncomplete())
                        .addSkill(StandUnlockableSkill.unlockableAbility("put_disc",200).prerequisiteSkill("discfy"))
                        .addSkill(StandUnlockableSkill.unlockableAbility("discfy",100))
                        .addSkill(StandUnlockableSkill.startingAbility("take_memory").prerequisiteSkill("discfy"))
                        .addSkill(StandUnlockableSkill.startingAbility("take_stand").prerequisiteSkill("discfy"))
                        .addSkill(StandUnlockableSkill.unlockableAbility("throw_disc",200).prerequisiteSkill("discfy"))
                        .addSkill(StandUnlockableSkill.unlockableAbility("melt_ur_heart",100))
                        .addSkill(StandUnlockableSkill.unlockableAbility("fake_snake",200).prerequisiteSkill("melt_ur_heart"))
                        .addSkill(StandUnlockableSkill.unlockableAbility("disguise",100).prerequisiteSkill("melt_ur_heart"))

                , id).discTooltipWIP();
    }
}
