package com.zeml.ripplez.init.power;

import com.github.standobyte.jojo.core.JojoRegistries;
import com.github.standobyte.jojo.powersystem.standpower.entity.EntityStandType;
import com.github.standobyte.jojo.powersystem.standpower.type.StandType;

import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.init.power.stands.StandInitGreenDay;
import com.zeml.ripplez.init.power.stands.StandInitWhiteSnake;
import com.zeml.ripplez.init.power.stands.StandInitZaHando;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.github.standobyte.jojo.init.power.ModStands.PLAYER_CAN_GET_FROM_ARROW;

public class AddonStands {
	public static final DeferredRegister<StandType> STANDS = DeferredRegister.create(JojoRegistries.DEFAULT_STANDS_REG, RipplesAddon.MOD_ID);
	
	public static final DeferredHolder<StandType, EntityStandType> ZA_HANDO = STANDS.register("za_hando", StandInitZaHando::create);
	public static final DeferredHolder<StandType, EntityStandType> WHITE_SNAKE = STANDS.register("white_snake", StandInitWhiteSnake::create);
	public static final DeferredHolder<StandType, EntityStandType> GREEN_DAY = STANDS.register("green_day", StandInitGreenDay::create);


	static {
		PLAYER_CAN_GET_FROM_ARROW.add(ZA_HANDO);

	}

}
