package com.zeml.ripplez.core.packets.server;

import com.github.standobyte.jojo.PacketsRegister;
import com.github.standobyte.jojo.client.ClientProxy;
import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffect;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import com.zeml.ripplez.jojoimp.stands.killer_queen.KillerBombEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EffectBombBlockPacket(int entityID, BlockPos pos)implements CustomPacketPayload {
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return type;
    }
    private static Type<EffectBombBlockPacket> type;

    public static class Handler implements PacketsRegister.PacketOGHandler<EffectBombBlockPacket> {

        public Handler(ResourceLocation packetId) {
            type = new Type<>(packetId);
        }

        @Override
        public Type<EffectBombBlockPacket> type() {
            return type;
        }


        @Override
        public void encode(EffectBombBlockPacket packet, RegistryFriendlyByteBuf buf) {
            buf.writeInt(packet.entityID);
            buf.writeBlockPos(packet.pos);
        }

        @Override
        public EffectBombBlockPacket decode(RegistryFriendlyByteBuf buf) {
            return new EffectBombBlockPacket(buf.readInt(),buf.readBlockPos());
        }

        @Override
        public void handle(EffectBombBlockPacket payload, IPayloadContext context) {
            Entity entity = ClientProxy.getEntityById(payload.entityID);
            if(entity instanceof LivingEntity livingEntity){
                StandPower power = StandPower.get(livingEntity);
                if(power != null){
                    power.userStandEffects.getEffectsOfType(AddonStandAbilities.KILLER_QUEEN_BOMB.get()).forEach(KillerBombEffect::remove);
                    KillerBombEffect newEffect = AddonStandAbilities.KILLER_QUEEN_BOMB.get().create(livingEntity.level());
                    power.userStandEffects.addEffect(newEffect.withBlock(payload.pos));

                }
            }
        }
    }
}