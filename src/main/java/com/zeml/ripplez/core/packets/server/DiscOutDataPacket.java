package com.zeml.ripplez.core.packets.server;

import com.github.standobyte.jojo.PacketsRegister.PacketOGHandler;
import com.github.standobyte.jojo.client.ClientProxy;
import com.zeml.ripplez.init.AddonDataAttachmentTypes;
import com.zeml.ripplez.jojoimp.stands.white_snake.data.DiscOutData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record DiscOutDataPacket(int entityID, boolean discsOut, boolean hasMemory) implements CustomPacketPayload {

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return type;
    }
    private static Type<DiscOutDataPacket> type;

    public static class Handler implements PacketOGHandler<DiscOutDataPacket>{

        public Handler(ResourceLocation packetId) {
            type = new Type<>(packetId);
        }

        @Override
        public Type<DiscOutDataPacket> type() {
            return type;
        }


        @Override
        public void encode(DiscOutDataPacket packet, RegistryFriendlyByteBuf buf) {
            buf.writeInt(packet.entityID);
            buf.writeBoolean(packet.discsOut);
            buf.writeBoolean(packet.hasMemory);
        }

        @Override
        public DiscOutDataPacket decode(RegistryFriendlyByteBuf buf) {
            int userID = buf.readInt();
            boolean discsOut = buf.readBoolean();
            boolean hasMemory = buf.readBoolean();
            return new DiscOutDataPacket(userID,discsOut, hasMemory);
        }

        @Override
        public void handle(DiscOutDataPacket payload, IPayloadContext context) {
            Entity entity = ClientProxy.getEntityById(payload.entityID);
            if(entity != null){
                entity.setData(AddonDataAttachmentTypes.DISC_OUT,new DiscOutData(payload.discsOut, payload.hasMemory));
            }
        }
    }

}
