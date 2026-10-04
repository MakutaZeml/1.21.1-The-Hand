package com.zeml.ripplez.jojoimp.stands.white_snake.data;

import com.github.standobyte.jojo.entityattachment.SynchronizableEntityData;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.level.ServerPlayer;

public class DiscOutData implements SynchronizableEntityData {
    boolean discsOut;
    boolean hasMemory;

    public DiscOutData(boolean discsOut, boolean hasMemory){
        this.discsOut = discsOut;
        this.hasMemory = hasMemory;
    }

    public DiscOutData(){
        this(false, true);
    }

    public boolean areDiscsOut() {
        return discsOut;
    }

    public void setDiscsOut(boolean standDisc) {
        this.discsOut = standDisc;
    }

    public boolean isHasMemory() {
        return hasMemory;
    }

    public void setHasMemory(boolean hasMemory) {
        this.hasMemory = hasMemory;
    }

    public static final Codec<DiscOutData> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.BOOL.optionalFieldOf("discsOut",false).forGetter(DiscOutData::areDiscsOut),
                    Codec.BOOL.optionalFieldOf("hasMemory",true).forGetter(DiscOutData::isHasMemory)
            ).apply(instance, DiscOutData::new)

    );

    @Override
    public void syncToTracking(ServerPlayer trackingPlayer) {

    }
}
