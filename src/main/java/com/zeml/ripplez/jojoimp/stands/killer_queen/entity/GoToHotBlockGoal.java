package com.zeml.ripplez.jojoimp.stands.killer_queen.entity;

import com.zeml.ripplez.init.AddonTags;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.level.LevelReader;

public class GoToHotBlockGoal extends MoveToBlockGoal {

    public GoToHotBlockGoal(PathfinderMob mob, double speedModifier, int searchRange) {
        super(mob, speedModifier, searchRange);
    }

    @Override
    public void tick() {
        super.tick();
        if(isReachedTarget()){
            mob.setTarget(mob);
            if(mob instanceof SheerHeartAttack sheerHeartAttack){
                sheerHeartAttack.setLastHotBlock(blockPos);
            }
        }
    }

    @Override
    public boolean canUse() {
        return mob.isEffectiveAi();
    }

    @Override
    protected boolean isValidTarget(LevelReader levelReader, BlockPos blockPos) {
        boolean blockPost = true;
        if(mob instanceof SheerHeartAttack sha){
            blockPost = !blockPos.equals(sha.getLastHotBlock());
        }
        return levelReader.getBlockState(blockPos).is(AddonTags.Blocks.HOT_BLOCK) && blockPost;
    }
}