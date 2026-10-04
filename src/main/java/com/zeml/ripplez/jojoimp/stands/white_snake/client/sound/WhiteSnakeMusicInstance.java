package com.zeml.ripplez.jojoimp.stands.white_snake.client.sound;

import com.zeml.ripplez.init.AddonDataAttachmentTypes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class WhiteSnakeMusicInstance extends AbstractTickableSoundInstance {
    private Entity entity;
    public WhiteSnakeMusicInstance(SoundEvent soundEvent, SoundSource source, float volume, float pitch, Entity entity, Level level) {
        super(soundEvent, source, RandomSource.create(level.random.nextLong()));
        this.volume = volume;
        this.pitch = pitch;
        this.entity = entity;
        this.x = entity.getX();
        this.y = entity.getY();
        this.z = entity.getZ();
        this.attenuation = Attenuation.LINEAR;
    }

    @Override
    public boolean canPlaySound() {
        if(entity != null) {
            return entity.isAlive() || entity.getData(AddonDataAttachmentTypes.DISC).getDisc().get(DataComponents.JUKEBOX_PLAYABLE) != null;
        }
        return false;
    }
    @Override
    public void tick() {
        if (entity != null) {
            if(Minecraft.getInstance().player != null){
                float dis = entity.distanceTo(Minecraft.getInstance().player);
                this.volume =Math.max(1-(dis*dis)/256,0);
            }
            if (entity.isRemoved()) {
                if (entity.isSilent()) {
                    stop();
                }
                else {
                    entity = null;
                }
            } else {
                this.x = entity.getX();
                this.y = entity.getY();
                this.z = entity.getZ();
            }

        }
    }
}
