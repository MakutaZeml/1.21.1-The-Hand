package com.zeml.ripplez.jojoimp.stands.killer_queen;

import com.github.standobyte.jojo.client.input.AbilityInputState;
import com.github.standobyte.jojo.client.input.InputHandler;
import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffect;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.ability.AbilityId;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.github.standobyte.jojo.powersystem.ability.AbilityUsageGroup;
import com.github.standobyte.jojo.powersystem.ability.condition.ConditionCheck;
import com.github.standobyte.jojo.powersystem.entityaction.ActionPhase;
import com.github.standobyte.jojo.powersystem.entityaction.EntityActionInstance;
import com.github.standobyte.jojo.powersystem.entityaction.type.EntityActionType;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntityAbility;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTracker;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTracking;
import com.github.standobyte.jojo.util.functions_network.NetworkUtil;
import com.github.standobyte.jojo.util.objects_mc.ContainerSlotInput;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.core.packets.server.EffectBombItemPacket;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ItemInventoryBombAbility extends StandEntityAbility {

    public ItemInventoryBombAbility(AbilityType<?> abilityType, AbilityId abilityId) {
        super(abilityType, abilityId,BombItem::new);
        usageGroup = AbilityUsageGroup.INVENTORY;
        setDefaultPhaseLength(ActionPhase.PERFORM,1);
        setDefaultPhaseLength(ActionPhase.RECOVERY,10);
    }

    @Override
    public ConditionCheck checkSpecificConditions(Power<?> context) {
        if (context.getUser().level().isClientSide()) {
            Screen screen = Minecraft.getInstance().screen;
            if (screen instanceof AbstractContainerScreen invScreen) {
                boolean active = false;
                if (!InputHandler.inputsDisabled) {
                    Slot hovered = invScreen.getSlotUnderMouse();
                    if (hovered != null) {
                        ItemStack item = hovered.getItem();
                        active = item.getCount() == 1;
                    }
                }
                if (!active) {
                    return ConditionCheck.NEGATIVE;
                }
            }
        }
        return super.checkSpecificConditions(context);
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

    public static class BombItem extends EntityActionInstance {
        private Optional<ContainerSlotInput> inputInvSlot = Optional.empty();
        private ItemStack bombStack = ItemStack.EMPTY;
        public BombItem(EntityActionType ability) {
            super(ability);
        }

        @Override
        public void extraClientInput(FriendlyByteBuf input) {
            inputInvSlot = NetworkUtil.readOptional(input, ContainerSlotInput.STREAM_CODEC);
        }

        @Override
        public void onActionSet(@Nullable EntityActionInstance prevAction) {
            if (inputInvSlot != null && inputInvSlot.isPresent() && powerUser.getEntity(level()) instanceof Player player) {
                bombStack = inputInvSlot.get().getItem(player);
            }
        }

        @Override
        public void actionPerformStart() {
            Level level = level();
            if(!level.isClientSide){
                if(bombStack != null && !bombStack.isEmpty() && bombStack.getCount() == 1){
                    if(getPowerUser() instanceof Player player){
                        ServerLevel serverLevel = (ServerLevel) level();
                        ItemTracking trackingSystem = ItemTracking.getItemTracking(level);
                        ItemTracker tracker = trackingSystem.startTracking(bombStack, serverLevel);
                        tracker.context = "first_bomb";
                        tracker.setTrackedByPlayer(player);
                        StandPower power = StandPower.get(player);
                        if(power != null){
                            power.userStandEffects.getEffectsOfType(AddonStandAbilities.KILLER_QUEEN_BOMB.get()).forEach(KillerBombEffect::remove);

                            KillerBombEffect newEffect = AddonStandAbilities.KILLER_QUEEN_BOMB.get().create(level);
                            power.userStandEffects.addEffect(newEffect.withTracker(tracker.trackerId));

                            PacketDistributor.sendToPlayer((ServerPlayer) player,new EffectBombItemPacket(player.getId(),tracker.trackerId));
                        }

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