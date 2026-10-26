package com.zeml.ripplez.init;

import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.jojoimp.stands.killer_queen.entity.SheerHeartAttack;
import com.zeml.ripplez.jojoimp.stands.white_snake.entity.ThrewDiscEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@EventBusSubscriber(modid = RipplesAddon.MOD_ID)
public final class AddonEntityTypes {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, RipplesAddon.MOD_ID);
	
	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(SHA.get(),SheerHeartAttack.createAttributes().build());
	}

	public static final DeferredHolder<EntityType<?>, EntityType<ThrewDiscEntity>> THREW_DISC = ENTITY_TYPES.register("threw_disc", key ->
			EntityType.Builder.<ThrewDiscEntity>of(ThrewDiscEntity::new, MobCategory.MISC)
//			.noLootTable()
					.sized(0.25F, 0.25F)
					.clientTrackingRange(4)
					.updateInterval(10)
					.build(createIDFor(key)));

	public static final DeferredHolder<EntityType<?>, EntityType<SheerHeartAttack>> SHA = ENTITY_TYPES.register("sha", key ->
			EntityType.Builder.<SheerHeartAttack>of(SheerHeartAttack::new, MobCategory.MISC)
					.sized(0.85F,0.7F)
					.clientTrackingRange(32)
					.updateInterval(2)
					.build(createIDFor(key)));

	public static String createIDFor(ResourceLocation key) {
		return key.toString();
	}
}
