package com.zeml.ripplez.jojoimp.stands.killer_queen.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class SheerHeartSwelGoal extends Goal {
    private final SheerHeartAttack shearheart;
    private LivingEntity target;

    public SheerHeartSwelGoal(SheerHeartAttack shearheart) {
        this.shearheart = shearheart;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    public boolean canUse() {
        LivingEntity livingentity = this.shearheart.getTarget();
        return this.shearheart.getSwellDir() > 0 || livingentity != null && this.shearheart.distanceToSqr(livingentity) < 9.0D;
    }

    public void start() {
        this.shearheart.getNavigation().stop();
        this.target = this.shearheart.getTarget();
    }


    public void stop() {
        this.target = null;
    }

    public void tick() {
        if (this.target == null) {
            this.shearheart.setSwellDir(-1);
        } else if (this.shearheart.distanceToSqr(this.target) > 49.0D) {
            this.shearheart.setSwellDir(-1);
        } else if (!this.shearheart.getSensing().hasLineOfSight(this.target)) {
            this.shearheart.setSwellDir(-1);
        } else {
            this.shearheart.setSwellDir(1);
        }
    }

}
