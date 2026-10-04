package com.zeml.ripplez.init;

import com.zeml.ripplez.RipplesAddon;

import com.zeml.ripplez.jojoimp.stands.white_snake.data.DiscOutData;
import com.zeml.ripplez.jojoimp.stands.white_snake.data.WhiteSnakeMusicData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public final class AddonDataAttachmentTypes {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, RipplesAddon.MOD_ID);

	public static final Supplier<AttachmentType<WhiteSnakeMusicData>> DISC = ATTACHMENT_TYPES.register("music",
			()->AttachmentType.builder(WhiteSnakeMusicData::new).serialize(WhiteSnakeMusicData.CODEC).build());

	public static final Supplier<AttachmentType<DiscOutData>> DISC_OUT = ATTACHMENT_TYPES.register("disc_out",
			()->AttachmentType.builder(DiscOutData::new).serialize(DiscOutData.CODEC).build());


}
