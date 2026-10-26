package com.zeml.ripplez.jojoimp.stands.killer_queen;

import com.github.standobyte.jojo.entityattachment.custom_effect.EntityCustomEffectType;
import com.github.standobyte.jojo.powersystem.standpower.effect.StandEffectInstance;
import com.github.standobyte.jojo.powersystem.standpower.entity.StandEntity;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTrackDebugMarker;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTracker;
import com.github.standobyte.jojo.subsystems.itemtracking.ItemTracking;
import com.zeml.ripplez.RipplesAddon;
import com.zeml.ripplez.core.packets.server.QuiteAllBombEffectsCauseReasons;
import com.zeml.ripplez.init.power.AddonStandAbilities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;



public class KillerBombEffect extends StandEffectInstance {
    private TargetType targetType;
    private BlockPos pos;
    private UUID itemTracker;

    public KillerBombEffect(@NotNull EntityCustomEffectType<?> effectType) {
        super(effectType);
        removeOnUserLogout = true;
    }

    @Override
    protected void start() {

    }

    @Override
    protected void tick() {
        if(targetType == null){
            this.remove();
        }
        LivingEntity user = getStandUser();
        if(user != null && userPower != null){
            double range = userPower.getPowerType().getStandStats().rangeMax();
            switch (targetType){
                case BLOCK -> {
                    if(level.getBlockState(pos).isAir() || Math.sqrt(user.distanceToSqr(pos.getCenter()))>range){
                        this.remove();
                    }

                }
                case ENTITY -> {
                    Entity target = getTarget();
                    if(target != null && target.isAlive()){
                        if(target.distanceTo(user)>range){
                            this.remove();
                        }
                        AABB aabb = getTarget().getBoundingBox();
                        List<Entity> entities = level.getEntities(getTarget(),aabb).stream().filter(entity1 -> entity1 instanceof LivingEntity && entity1.isAlive())
                                .filter(entity1-> !entity1.getUUID().equals(user.getUUID()))
                                .filter(entity1 -> !(entity1 instanceof StandEntity stand && stand.getUser() != null && stand.getUser().getUUID().equals(user.getUUID())))
                                .toList();
                        if(!entities.isEmpty()){
                            KQExplodeBomb.jankKQExplosion(level,user,userPower,false,target,null,null);
                            remove();
                        }
                    }else {
                        this.remove();
                    }
                }
                case ITEM -> {
                    ItemTracking allTrackers = ItemTracking.getItemTracking(level);
                    for (ItemTracker tracker : allTrackers.values()){
                        if ("first_bomb".equals(tracker.context) && tracker.trackerId.equals(itemTracker)) {
                            if(tracker.getItem() == null || tracker.getPos(level,1)==null ||
                                    (tracker.getPos(level,1) != null && tracker.getPos(level,1).distanceTo(user.position())>range)){
                                userPower.userStandEffects.getEffectsOfType(AddonStandAbilities.KILLER_QUEEN_BOMB.get()).forEach(KillerBombEffect::remove);
                                if(!level.isClientSide && user instanceof ServerPlayer serverPlayer){
                                    PacketDistributor.sendToPlayer(serverPlayer,new QuiteAllBombEffectsCauseReasons(serverPlayer.getId()));
                                }
                            }
                        }
                    }
                }
                default -> {this.remove();}
            }
        }

    }

    @Override
    protected void stop() {

    }

    @Override
    public void remove() {
        super.remove();
        if(itemTracker != null){
            ItemTracking allTrackers = ItemTracking.getItemTracking(level);
            for (ItemTracker tracker : allTrackers.values()) {
                if ("first_bomb".equals(tracker.context) && tracker.trackerId == this.itemTracker) {
                    if(!level.isClientSide){
                        tracker.setDisappeared((ServerLevel) level);
                    }
                }
            }
        }
    }

    public StandEffectInstance setTarget(TargetType type){
        this.targetType = type;
        return this;
    }

    public StandEffectInstance withBlock(BlockPos pos){
        this.pos = pos;
        return setTarget(TargetType.BLOCK);
    }

    @Override
    public StandEffectInstance withTarget(Entity target) {
        this.targetType = TargetType.ENTITY;
        return super.withTarget(target);
    }

    public StandEffectInstance withTracker(UUID itemTracker){
        this.itemTracker = itemTracker;
        return this.setTarget(TargetType.ITEM);
    }

    public UUID getItemTracker(){
        return this.itemTracker;
    }

    public TargetType getTargetType(){
        return this.targetType;
    }

    public BlockPos getBlockPos(){
        return this.pos;
    }

    @Nullable
    public Vec3 getPos(){
        switch (targetType){
            case ENTITY -> {
                return getTarget().position().add(0,getTarget().getBbHeight(),0);
            }
            case BLOCK -> {
                return Vec3.upFromBottomCenterOf(pos, 1.0);
            }
        }
        return null;
    }


    public enum TargetType {
        ENTITY,
        BLOCK,
        ITEM
    }


    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        Optional<UUID> optionalUUID = Optional.of(nbt.getUUID("tracker"));
        optionalUUID.ifPresent(uuid -> this.itemTracker = uuid);

        Optional<Integer> optionalX = Optional.of(nbt.getInt("x"));
        Optional<Integer> optionalY = Optional.of(nbt.getInt("y"));
        Optional<Integer> optionalZ = Optional.of(nbt.getInt("z"));

        if(optionalX.isPresent() && optionalY.isPresent() && optionalZ.isPresent()){
            this.pos = new BlockPos(optionalX.get(),optionalY.get(),optionalZ.get());
        }


    }

    @Override
    protected void writeAdditionalSaveData(CompoundTag nbt) {
        super.writeAdditionalSaveData(nbt);
        if(this.pos != null){
            nbt.putInt("x",this.pos.getX());
            nbt.putInt("y",this.pos.getY());
            nbt.putInt("z",this.pos.getZ());
        }
        if(itemTracker != null){
            nbt.putUUID("tracker",this.itemTracker);
        }

    }

    @Override
    public void readAdditionalPacketData(FriendlyByteBuf buf, boolean clientIsUser) {
        super.readAdditionalPacketData(buf, clientIsUser);
        this.targetType = buf.readEnum(TargetType.class);
        switch (targetType){
            case BLOCK -> {
                this.pos = buf.readBlockPos();
            }
            case ITEM -> {
                this.itemTracker = buf.readUUID();
            }
        }

    }

    @Override
    public void writeAdditionalPacketData(FriendlyByteBuf buf, boolean sendingToUser) {
        super.writeAdditionalPacketData(buf, sendingToUser);
        if(this.targetType != null){
            buf.writeEnum(this.targetType);
            if(this.pos != null){
                buf.writeBlockPos(this.pos);
            }
            if(this.itemTracker != null){
                buf.writeUUID(this.itemTracker);
            }

        }

    }
}