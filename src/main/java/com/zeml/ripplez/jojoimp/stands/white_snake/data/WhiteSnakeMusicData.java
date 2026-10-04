package com.zeml.ripplez.jojoimp.stands.white_snake.data;

import com.github.standobyte.jojo.entityattachment.SynchronizableEntityData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class WhiteSnakeMusicData implements SynchronizableEntityData {
    private ItemStack disc;
    private int ticks;
    private int ticksTotal;

    public WhiteSnakeMusicData(){
        this.disc = new ItemStack(Items.STRUCTURE_VOID);
        this.ticks = 0;
        this.ticksTotal = 0;
    }


    public WhiteSnakeMusicData(ItemStack disc,int ticks ,int ticksTotal) {
        this.disc = disc;
        this.ticks = ticks;
        this.ticksTotal = ticksTotal;
    }

    public ItemStack getDisc() {
        return disc;
    }

    public void setDisc(ItemStack disc) {
        this.disc = disc;
    }

    public int getTicks() {
        return ticks;
    }

    public void setTicks(int ticks) {
        this.ticks = ticks;
    }

    public int getTicksTotal() {
        return ticksTotal;
    }

    public void setTicksTotal(int ticksTotal) {
        this.ticksTotal = ticksTotal;
    }

    public static final Codec<WhiteSnakeMusicData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
                ItemStack.CODEC.optionalFieldOf("disc",ItemStack.EMPTY).forGetter(WhiteSnakeMusicData::getDisc),
                Codec.INT.optionalFieldOf("ticks",0).forGetter(WhiteSnakeMusicData::getTicks),
                Codec.INT.optionalFieldOf("totalTicks",0).forGetter(WhiteSnakeMusicData::getTicksTotal)
                ).apply(instance, WhiteSnakeMusicData::new)

    );

    @Override
    public void syncToTracking(ServerPlayer trackingPlayer) {

    }
}
