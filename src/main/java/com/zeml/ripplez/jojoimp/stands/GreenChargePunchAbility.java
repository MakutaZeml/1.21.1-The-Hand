package com.zeml.ripplez.jojoimp.stands;

import com.github.standobyte.jojo.customobjects.DamageSourceModified;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojoimpl.stands._entitybase.StandEntityHeavyPunchChargedAbility;
import com.github.standobyte.jojoimpl.stands.starplatinum.HeavyPunchUppercutAbility;
import net.minecraft.world.damagesource.DamageSource;

public class GreenChargePunchAbility extends StandEntityHeavyPunchChargedAbility {

    public GreenChargePunchAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId);
        createActionObj = GreenCharge::new;
    }

    public static class GreenCharge extends StandEntityChargedHeavy {
        public GreenCharge(EntityActionType ability) {
            super(ability);
        }

        protected void addKnockback(DamageSource dmgSource) {
            DamageSourceModified knockback = (DamageSourceModified) dmgSource;
            knockback.jojo_ripples$verticalKnockback(1, -0.8f);
        }
    }
}
