package com.zeml.ripplez.init;

import com.zeml.ripplez.RipplesAddon;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class AddonTags {
    public static class Blocks{
        public static final TagKey<Block> HOT_BLOCK= TagKey.create(Registries.BLOCK, RipplesAddon.resLoc("hot_block"));
    }
}
