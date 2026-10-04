package com.zeml.ripplez.jojoimp.stands.white_snake.entity;

import com.github.standobyte.jojo.customobjects.entity_projectile.ModdedProjectileEntity;
import com.github.standobyte.jojo.init.ModItemDataComponents;
import com.github.standobyte.jojo.mechanics.standdisc.StandDiscItem;
import com.github.standobyte.jojo.mechanics.standdisc.StandWrittenOnDisc;
import com.github.standobyte.jojo.powersystem.PowerClass;
import com.github.standobyte.jojo.powersystem.standpower.StandPower;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.core.packets.server.WhiteSnakeMusicDataPacket;
import com.zeml.ripplez.init.AddonDataAttachmentTypes;
import com.zeml.ripplez.init.AddonEntityTypes;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import com.zeml.ripplez.jojoimp.stands.white_snake.data.WhiteSnakeMusicData;
import com.zeml.ripplez.jojoimp.stands.white_snake.effect.MusicDisckedEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class ThrewDiscEntity extends ModdedProjectileEntity implements ItemSupplier {
    protected ItemStack disc;
    public ThrewDiscEntity(EntityType<? extends ThrewDiscEntity> type, Level level) {
        super(type, level);
    }

    public ThrewDiscEntity(LivingEntity shooter, Level level, ItemStack disc) {
        super(AddonEntityTypes.THREW_DISC.get(), shooter,level);
        this.disc = disc;
    }

    public void setDisc(ItemStack disc){
        this.disc = disc;
    }


    @Override
    protected void onHitEntity(EntityHitResult entityRayTraceResult) {
        super.onHitEntity(entityRayTraceResult);

    }

    @Override
    protected void afterEntityHit(EntityHitResult entityRayTraceResult, boolean entityHurt) {
        if(entityHurt){
            Entity entity = entityRayTraceResult.getEntity();
            if(entity instanceof LivingEntity living){
                if(disc.getItem() instanceof StandDiscItem){
                    if(StandPower.get(living) != null){
                        StandPower standPower = StandPower.get(living);
                        if(standPower.getStandInstance().isPresent()){
                            ItemStack newDisc = StandDiscItem.withStand(standPower.getStandInstance().get());
                            if(!level().isClientSide){
                                ItemEntity itemEntity = new ItemEntity(level(),this.getX(),this.getY(),this.getZ(),newDisc);
                                itemEntity.setPickUpDelay(100);
                                itemEntity.setDeltaMovement(this.getDeltaMovement().scale(.75));
                                level().addFreshEntity(itemEntity);
                            }
                        }
                    }
                    StandWrittenOnDisc discStand = disc.get(ModItemDataComponents.DISC_STAND.get());
                    if(discStand != null && discStand.isValid()){
                        PowerClass.STAND.attachPower(living);
                        StandPower stand = PowerClass.STAND.get(living);
                        if (stand != null) {
                            stand.setStandInstance(Optional.of(discStand.copyStandInstance()));
                        }
                    }
                }
                JukeboxPlayable jukeboxplayable = disc.get(DataComponents.JUKEBOX_PLAYABLE);
                if(jukeboxplayable != null){
                    Optional<Holder<JukeboxSong>> optional = JukeboxSong.fromStack(this.level().registryAccess(), this.disc);
                    if(optional.isPresent()){
                        StandPower standPower = userStandPower.get();
                        if(standPower != null){
                            MusicDisckedEffect music = AddonStandAbilities.MUSIC_DISC_EFFECT.get().create(level());
                            WhiteSnakeMusicData data = living.getData(AddonDataAttachmentTypes.DISC);
                            int total =optional.map(jukeboxSongHolder->{
                                JukeboxSong song = jukeboxSongHolder.value();
                                return song.lengthInTicks();

                            }).orElse(0);
                            data.setDisc(disc);
                            data.setTicks(0);
                            data.setTicksTotal(total);
                            PacketDistributor.sendToPlayersTrackingEntityAndSelf(living,new WhiteSnakeMusicDataPacket(living.getId(),disc,0,total));
                            standPower.userStandEffects.addEffect(music.withTarget(living));
                        }

                    }
                }
            }

        }
    }

    @Override
    public int ticksLifespan() {
        return 100;
    }

    @Override
    protected float getBaseDamage() {
        return 0;
    }

    @Override
    protected float getMaxHardnessBreakable() {
        return 0.3F;
    }

    @Override
    public boolean standDamage() {
        return false;
    }

    @Override
    public @NotNull ItemStack getItem() {
        return disc != null? disc: new ItemStack(Items.MUSIC_DISC_13);
    }

    @Override
    public void writeSpawnData(RegistryFriendlyByteBuf buffer) {
        super.writeSpawnData(buffer);
        ItemStack.STREAM_CODEC.encode(buffer, disc);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        if(disc != null){
            nbt.put("disc",disc.save(this.registryAccess()));
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("disc", 10)){
            CompoundTag compoundtag = nbt.getCompound("disc");
            this.setDisc(ItemStack.parse(this.registryAccess(), compoundtag).orElse(new ItemStack(Items.MUSIC_DISC_13)));
        }
    }

    @Override
    public void syncData(Supplier<? extends AttachmentType<?>> type) {
        super.syncData(type);


    }

    @Override
    public void readSpawnData(RegistryFriendlyByteBuf additionalData) {
        super.readSpawnData(additionalData);
        disc = ItemStack.STREAM_CODEC.decode(additionalData);
    }
}
