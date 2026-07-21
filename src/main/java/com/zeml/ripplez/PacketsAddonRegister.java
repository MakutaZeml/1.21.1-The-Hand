package com.zeml.ripplez;

import com.github.standobyte.jojo.PacketsRegister;
import com.github.standobyte.jojo.PacketsRegister.PacketType;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class PacketsAddonRegister {
    public static void register(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");

        // Some examples from the base mod, uncomment and modify as needed:

        // Server packets:
        // registerPacket(registrar, PayloadRegistrar::playToServer, new ClAbilityInputPacket.Handler(JojoMod.resLoc("clkey")));

        // Client packets:
        // registerPacket(registrar, PayloadRegistrar::playToClient, new DatapackStandsPacket.Handler(JojoMod.resLoc("datastands")));
    }

    private static <T extends CustomPacketPayload> void registerPacket(PayloadRegistrar registrar, PacketType packetType, PacketsRegister.PacketOGHandler<T> handler) {
        packetType.register(registrar, handler.type(), StreamCodec.ofMember(handler::encode, handler::decode), handler::handle);
    }

    private static <T extends CustomPacketPayload> void registerPacket(PayloadRegistrar registrar, PacketType packetType, PacketsRegister.PacketCodecHandler<T> handler) {
        packetType.register(registrar, handler.type(), handler.reader(), handler::handle);
    }

}
