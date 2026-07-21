package com.zeml.ripplez.init.power;

import com.github.standobyte.jojo.core.JojoRegistries;
import com.github.standobyte.jojo.powersystem.ability.Ability;
import com.github.standobyte.jojo.powersystem.ability.AbilityType;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.jojoimp.stands.zh.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class AddonStandAbilities {
	public static final DeferredRegister<AbilityType<?>> ABILITY_TYPES = DeferredRegister.create(JojoRegistries.ABILITY_TYPES_REG, RipplesAddon.MOD_ID);

	public static final DeferredHolder<AbilityType<?>, AbilityType<Ability>> ERASURE = ABILITY_TYPES.register(
			"erasure", key -> new AbilityType<>(key, EraseAbility::new));

	public static final DeferredHolder<AbilityType<?>, AbilityType<Ability>> ERASE_FINISHER = ABILITY_TYPES.register(
			"erase_finisher", key -> new AbilityType<>(key, EraseFinisherAbility::new));
	public static final DeferredHolder<AbilityType<?>, AbilityType<Ability>> ERASE_SPACE = ABILITY_TYPES.register(
			"erase_space", key -> new AbilityType<>(key, EraseSpaceAbility::new));

	public static final DeferredHolder<AbilityType<?>, AbilityType<Ability>> ERASE_BARRAGE = ABILITY_TYPES.register(
			"erase_barrage", key -> new AbilityType<>(key, EraseBarrageAbility::new));

	public static final DeferredHolder<AbilityType<?>, AbilityType<Ability>> ERASE_ITEMS = ABILITY_TYPES.register(
			"erase_items", key -> new AbilityType<>(key, EraseItemsAbility::new));
}