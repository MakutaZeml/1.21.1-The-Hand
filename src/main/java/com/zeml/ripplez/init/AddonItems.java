package com.zeml.ripplez.init;

import com.zeml.ripplez.RipplesAddon;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.github.standobyte.jojo.init.ModItems.MAIN_TAB;

@EventBusSubscriber(modid = RipplesAddon.MOD_ID)
public final class AddonItems {
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RipplesAddon.MOD_ID);

    public static final DeferredItem<Item> ALELUYA_DISC = ITEMS.register("music_disc_aleluya",
            ()-> new Item(new Item.Properties().jukeboxPlayable(AddonSoundEvents.ALELUYA_KEY).stacksTo(1).rarity(Rarity.RARE)));


    @SubscribeEvent
    public static void addToTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == MAIN_TAB.getKey()) {
            event.accept(ALELUYA_DISC);
        }
    }
}