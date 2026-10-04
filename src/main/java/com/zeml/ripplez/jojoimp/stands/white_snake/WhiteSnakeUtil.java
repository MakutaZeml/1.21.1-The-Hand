package com.zeml.ripplez.jojoimp.stands.white_snake;

import com.github.standobyte.jojo.init.ModItemDataComponents;
import com.github.standobyte.jojo.mechanics.standdisc.StandDiscItem;
import com.github.standobyte.jojo.mechanics.standdisc.StandWrittenOnDisc;
import com.github.standobyte.jojo.powersystem.Power;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.zeml.ripplez.core.packets.server.WhiteSnakeMusicDataPacket;
import com.zeml.ripplez.init.AddonDataAttachmentTypes;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import com.zeml.ripplez.jojoimp.stands.white_snake.data.WhiteSnakeMusicData;
import com.zeml.ripplez.jojoimp.stands.white_snake.effect.MusicDisckedEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class WhiteSnakeUtil {

    public static boolean standHasDisc(Power<?> context){
        StandPower standPower = PowerClass.STAND.cast(context);
        if(standPower != null){
            StandEntity stand = standPower.getSummonedStandEntity();
            if(stand != null){
                ItemStack stack = stand.getItemBySlot(EquipmentSlot.MAINHAND);
                return stack.get(ModItemDataComponents.DISC_STAND.get()) != null ||stack.get(DataComponents.JUKEBOX_PLAYABLE) != null;
            }
        }
        return false;
    }


    public static Set<ItemStack> putDisc(ItemStack disc, StandPower userPower , LivingEntity targetLiving){
        Set<ItemStack> itemStacks = new HashSet<>();
        if(disc.get(DataComponents.JUKEBOX_PLAYABLE)!=null){
            JukeboxPlayable jukeboxplayable = disc.get(DataComponents.JUKEBOX_PLAYABLE);
            if(jukeboxplayable != null){
                Optional<Holder<JukeboxSong>> optional = JukeboxSong.fromStack(targetLiving.level().registryAccess(), disc);
                if(optional.isPresent()){
                    MusicDisckedEffect music = AddonStandAbilities.MUSIC_DISC_EFFECT.get().create(targetLiving.level());
                    WhiteSnakeMusicData data = targetLiving.getData(AddonDataAttachmentTypes.DISC);
                    int total =optional.map(jukeboxSongHolder->{
                        JukeboxSong song = jukeboxSongHolder.value();
                        return song.lengthInTicks();

                    }).orElse(0);
                    if(!data.getDisc().is(Items.STRUCTURE_VOID)){
                        itemStacks.add(data.getDisc().copy());
                    }

                    data.setDisc(new ItemStack(Items.STRUCTURE_VOID));
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(targetLiving,new WhiteSnakeMusicDataPacket(targetLiving.getId(),new ItemStack(Items.STRUCTURE_VOID),0,total));

                    data.setDisc(disc);
                    data.setTicks(0);
                    data.setTicksTotal(total);
                    PacketDistributor.sendToPlayersTrackingEntityAndSelf(targetLiving,new WhiteSnakeMusicDataPacket(targetLiving.getId(),disc,0,total));
                    if(userPower != null){
                        userPower.userStandEffects.addEffect(music.withTarget(targetLiving));
                    }
                }
            }
        }
        if(disc.get(ModItemDataComponents.DISC_STAND.get()) != null){
            StandPower targetPower = StandPower.get(targetLiving);
            if(targetPower != null && targetPower.getStandInstance().isPresent()){
                itemStacks.add(StandDiscItem.withStand(targetPower.getStandInstance().get()));
            }
            StandWrittenOnDisc discStand = disc.get(ModItemDataComponents.DISC_STAND.get());
            if(discStand != null && discStand.isValid()){
                PowerClass.STAND.attachPower(targetLiving);
                StandPower stand = PowerClass.STAND.get(targetLiving);
                if (stand != null) {
                    stand.setStandInstance(Optional.of(discStand.copyStandInstance()));
                }
            }

        }
        return itemStacks;
    }

}
