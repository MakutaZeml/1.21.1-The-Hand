package com.zeml.ripplez.core;

import com.github.standobyte.jojo.PacketsRegister;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.core.packets.server.DiscOutDataPacket;
import com.zeml.ripplez.core.packets.server.WhiteSnakeMusicDataPacket;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class AddonPackets {
    public static void register(RegisterPayloadHandlersEvent event){
        PayloadRegistrar registrar = event.registrar("1");
        PacketsRegister.registerPacket(registrar,PayloadRegistrar::playToClient, new WhiteSnakeMusicDataPacket.Handler(RipplesAddon.resLoc("music")));
        PacketsRegister.registerPacket(registrar,PayloadRegistrar::playToClient, new DiscOutDataPacket.Handler(RipplesAddon.resLoc("disc_out")));


    }

}
