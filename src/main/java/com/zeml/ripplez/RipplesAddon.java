package com.zeml.ripplez;

import com.mojang.logging.LogUtils;
import com.zeml.ripplez.core.AddonPackets;
import com.zeml.ripplez.init.*;
import com.zeml.ripplez.init.power.AddonPlayerPowers;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import com.zeml.ripplez.init.power.AddonStands;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.slf4j.Logger;

@Mod(RipplesAddon.MOD_ID)
public class RipplesAddon {
    public static final String MOD_ID = "ripplez";
    public static final Logger LOGGER = LogUtils.getLogger();

    public RipplesAddon(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.register(this);

        AddonStandAbilities.ABILITY_TYPES.register(modEventBus);
        AddonPlayerPowers.PLAYER_POWERS.register(modEventBus);
        AddonStands.STANDS.register(modEventBus);

        AddonBlocks.BLOCKS.register(modEventBus);
        AddonEntityTypes.ENTITY_TYPES.register(modEventBus);
        AddonDataAttachmentTypes.ATTACHMENT_TYPES.register(modEventBus);
        AddonItems.ITEMS.register(modEventBus);
        AddonSoundEvents.SOUNDS.register(modEventBus);
    }

    @SubscribeEvent
    private void commonSetup(FMLCommonSetupEvent event) {
    }

    @SubscribeEvent
    private void registerNetwork(RegisterPayloadHandlersEvent event) {
        AddonPackets.register(event);
    }

    public static ResourceLocation resLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static Logger getLogger() {
        return LOGGER;
    }
}