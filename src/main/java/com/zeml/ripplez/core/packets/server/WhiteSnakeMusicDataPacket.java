package com.zeml.ripplez.core.packets.server;

import com.github.standobyte.jojo.PacketsRegister.PacketOGHandler;
import com.github.standobyte.jojo.client.ClientProxy;
import com.zeml.ripplez.init.AddonDataAttachmentTypes;
import com.zeml.ripplez.jojoimp.stands.white_snake.data.WhiteSnakeMusicData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record WhiteSnakeMusicDataPacket(int entityID, ItemStack disc, Integer ticks, Integer total) implements CustomPacketPayload {

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return type;
    }
    private static Type<WhiteSnakeMusicDataPacket> type;

    public static class Handler implements PacketOGHandler<WhiteSnakeMusicDataPacket>{

        public Handler(ResourceLocation packetId) {
            type = new Type<>(packetId);
        }

        @Override
        public Type<WhiteSnakeMusicDataPacket> type() {
            return type;
        }


        @Override
        public void encode(WhiteSnakeMusicDataPacket packet, RegistryFriendlyByteBuf buf) {
            buf.writeInt(packet.entityID);
            ItemStack.STREAM_CODEC.encode(buf,packet.disc);
            buf.writeInt(packet.ticks);
            buf.writeInt(packet.total);
        }

        @Override
        public WhiteSnakeMusicDataPacket decode(RegistryFriendlyByteBuf buf) {
            int userID = buf.readInt();
            ItemStack stack = ItemStack.STREAM_CODEC.decode(buf);
            int ticks = buf.readInt();
            int total = buf.readInt();

            return new WhiteSnakeMusicDataPacket(userID,stack,ticks,total);
        }

        @Override
        public void handle(WhiteSnakeMusicDataPacket payload, IPayloadContext context) {
            Entity entity = ClientProxy.getEntityById(payload.entityID);
            if(entity != null){
                WhiteSnakeMusicData data = entity.getData(AddonDataAttachmentTypes.DISC);
                data.setDisc(payload.disc);
                data.setTicks(payload.ticks);
                data.setTicksTotal(payload.total);

            }
        }
    }

}
