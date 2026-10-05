package com.zeml.ripplez.core.packets.server;

import com.github.standobyte.jojo.PacketsRegister;
import com.github.standobyte.jojo.client.ClientProxy;
import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffect;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez.init.AddonDataAttachmentTypes;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import com.zeml.ripplez.jojoimp.stands.killer_queen.KillerBombEffect;
import com.zeml.ripplez.jojoimp.stands.white_snake.data.DiscOutData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record EffectBombItemPacket(int entityID, UUID uuid) implements CustomPacketPayload {

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return type;
    }
    private static Type<EffectBombItemPacket> type;

    public static class Handler implements PacketsRegister.PacketOGHandler<EffectBombItemPacket> {

        public Handler(ResourceLocation packetId) {
            type = new Type<>(packetId);
        }

        @Override
        public Type<EffectBombItemPacket> type() {
            return type;
        }


        @Override
        public void encode(EffectBombItemPacket packet, RegistryFriendlyByteBuf buf) {
            buf.writeInt(packet.entityID);
            buf.writeUUID(packet.uuid);

        }

        @Override
        public EffectBombItemPacket decode(RegistryFriendlyByteBuf buf) {
            return new EffectBombItemPacket(buf.readInt(),buf.readUUID());
        }

        @Override
        public void handle(EffectBombItemPacket payload, IPayloadContext context) {
            Entity entity = ClientProxy.getEntityById(payload.entityID);
            if(entity instanceof LivingEntity livingEntity){
                StandPower power = StandPower.get(livingEntity);
                if(power != null){
                    power.userStandEffects.getEffectsOfType(AddonStandAbilities.KILLER_QUEEN_BOMB.get()).forEach(EntityCustomEffect::remove);

                    KillerBombEffect newEffect = AddonStandAbilities.KILLER_QUEEN_BOMB.get().create(livingEntity.level());
                    power.userStandEffects.addEffect(newEffect.withTracker(payload.uuid));

                }
            }
        }
    }

}