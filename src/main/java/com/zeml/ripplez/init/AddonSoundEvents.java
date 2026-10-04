package com.zeml.ripplez.init;

import com.zeml.ripplez.RipplesAddon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class AddonSoundEvents {
	public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, RipplesAddon.MOD_ID);

	//-----------------------------------------------DISC--------------------------------------------------------------------------
	public static final DeferredHolder<SoundEvent, SoundEvent> ALELUYA = SOUNDS.register("aleluya", SoundEvent::createVariableRangeEvent);
	public static final ResourceKey<JukeboxSong> ALELUYA_KEY = createSong("aleluya");

	//-----------------------------------------------SOUND--------------------------------------------------------------------------
	public static final DeferredHolder<SoundEvent, SoundEvent> CD_OUT = SOUNDS.register("disc_out", SoundEvent::createVariableRangeEvent);

	public static final DeferredHolder<SoundEvent, SoundEvent> STEP_BEHIND = SOUNDS.register("step_behind", SoundEvent::createVariableRangeEvent);
	public static final DeferredHolder<SoundEvent, SoundEvent> DISC_TOOK = SOUNDS.register("disc_extract", SoundEvent::createVariableRangeEvent);

	public static final DeferredHolder<SoundEvent, SoundEvent> ERASURE = SOUNDS.register("erasure", SoundEvent::createVariableRangeEvent);

	private static ResourceKey<JukeboxSong> createSong(String name){
		return ResourceKey.create(Registries.JUKEBOX_SONG, RipplesAddon.resLoc(name));
	}

}