package com.zeml.ripplez.jojoimp.stands.zh;

import com.github.standobyte.jojo.client.input.AbilityInputState;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntityAbility;
import com.github.standobyte.jojo.util.functions_network.NetworkUtil;
import com.github.standobyte.jojo.util.objects_mc.ContainerSlotInput;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EraseItemsAbility extends StandEntityAbility {


    public EraseItemsAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId, ItemDelete::new);
        usageGroup = AbilityUsageGroup.INVENTORY;
        setButtonHoldPhase(ActionPhase.PERFORM);
    }

    @Override
    public AbilityInputState cl_abilityInputState(Power<?> context) {
        AbilityInputState state = AbilityInputState.init();
        state.setFlag(AbilityInputState.ONLY_IN_CONTAINER, true);
        return state;
    }

    @Override
    public void writeExtraInput(FriendlyByteBuf serverboundBuf, LivingEntity user, boolean isClientPlayer) {
        if (isClientPlayer) {
            ContainerSlotInput hoveredItem = ContainerSlotInput.cl_HoveredSlot();
            NetworkUtil.writeOptionally(hoveredItem, serverboundBuf, ContainerSlotInput.STREAM_CODEC);
        }
    }

    public static class ItemDelete extends EntityActionInstance{
        private Optional<ContainerSlotInput> inputInvSlot = Optional.empty();
        private ItemStack deletedStack = ItemStack.EMPTY;
        public ItemDelete(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void extraClientInput(FriendlyByteBuf input) {
            inputInvSlot = NetworkUtil.readOptional(input, ContainerSlotInput.STREAM_CODEC);
        }

        @Override
        public void onActionSet(@Nullable EntityActionInstance prevAction) {
            if (inputInvSlot != null && inputInvSlot.isPresent() && powerUser.getEntity(level()) instanceof Player player) {
                deletedStack = inputInvSlot.get().getItem(player);
            }
        }

        @Override
        public void onButtonStopHold() {
            startRecovery();
        }

        @Override
        public void actionTick(){
            Level level = level();
            if(!level.isClientSide){
                if(deletedStack != null && !deletedStack.isEmpty()){
                    deletedStack.shrink(1);
                    StandPower power = StandPower.get(performer);
                    if(power != null && power.hasPower()){
                        power.addExp(2);
                    }
                }else {
                    setPhase(ActionPhase.RECOVERY, 0);
                    syncPhaseChanges();
                }
            }
        }

        @Override
        public boolean canBeCancelledInto(EntityActionType cancellingAbility) {
            return true;
        }

    }

}
