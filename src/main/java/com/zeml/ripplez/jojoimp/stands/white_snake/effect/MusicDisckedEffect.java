package com.zeml.ripplez.jojoimp.stands.white_snake.effect;

import com.github.standobyte.jojo.client.sound.ClientsideSoundsHelper;
import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffectType;
import com.github.standobyte.jojo.powersystem.standpower.effect.StandEffectInstance;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.core.packets.server.WhiteSnakeMusicDataPacket;
import com.zeml.ripplez.init.AddonDataAttachmentTypes;
import com.zeml.ripplez.jojoimp.stands.white_snake.client.sound.WhiteSnakeMusicInstance;
import com.zeml.ripplez.jojoimp.stands.white_snake.data.WhiteSnakeMusicData;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MusicDisckedEffect extends StandEffectInstance {

    public MusicDisckedEffect(@NotNull EntityCustomEffectType<?> effectType) {
        super(effectType);
        needsTarget=true;
    }


    @Override
    protected void start() {
        LivingEntity living = getTargetLiving();
        if(living != null){
            WhiteSnakeMusicData disc = living.getData(AddonDataAttachmentTypes.DISC);
            if(!disc.getDisc().isEmpty()){
                JukeboxPlayable jukeboxplayable = disc.getDisc().get(DataComponents.JUKEBOX_PLAYABLE);
                if(jukeboxplayable != null){
                    Optional<Holder<JukeboxSong>> optional = JukeboxSong.fromStack(this.level.registryAccess(),disc.getDisc());
                    SoundEvent soundEvent = optional.map(jukeboxSongHolder -> {
                        JukeboxSong song = jukeboxSongHolder.value();
                        Holder<SoundEvent> soundEventHolder = song.soundEvent();
                        return soundEventHolder.value();
                    }).orElse(null);
                    if (soundEvent != null && level.isClientSide){
                        WhiteSnakeMusicInstance sound = new WhiteSnakeMusicInstance(soundEvent,living.getSoundSource(),1,1,living,level);
                        ClientsideSoundsHelper.playNonVanillaClassSound(sound);
                    }
                }
            }

        }
    }

    @Override
    protected void tick() {
        LivingEntity target = getTargetLiving();
        if(target != null){
            WhiteSnakeMusicData data = target.getData(AddonDataAttachmentTypes.DISC);
            if(data.getTicks() >= data.getTicksTotal()){
                this.remove();
            }
            if(!level.isClientSide){
                data.setTicks(tickCount);
            }

        }
    }


    @Override
    protected void stop() {

    }


    @Override
    public void remove() {
        super.remove();
        LivingEntity target = getTargetLiving();
        if(target != null){
            WhiteSnakeMusicData data = target.getData(AddonDataAttachmentTypes.DISC);
            ItemStack stack = data.getDisc();
            if(!level.isClientSide){
                if(entity.isAlive()){
                    if(stack.get(DataComponents.JUKEBOX_PLAYABLE) != null){
                        ItemEntity itemEntity = new ItemEntity(level,target.getX(),target.getY(),target.getZ(),stack);
                        itemEntity.setPickUpDelay(60);
                        level.addFreshEntity(itemEntity);
                    }
                }

                WhiteSnakeMusicData newData = new WhiteSnakeMusicData();
                target.setData(AddonDataAttachmentTypes.DISC,newData);
                PacketDistributor.sendToPlayersTrackingEntity(target,new WhiteSnakeMusicDataPacket(target.getId(),newData.getDisc(),newData.getTicks(), newData.getTicksTotal()));

            }
        }
    }
}