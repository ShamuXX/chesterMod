package com.docs.chestermod.entities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.JumpControl;
import net.minecraft.world.entity.ai.goal.BreedGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;

import java.util.UUID;

public class ChesterEntity extends AbstractChestedHorse implements IAnimatable {
  private static final EntityDataAccessor<Boolean> DATA_ID_CHEST = SynchedEntityData
      .defineId(ChesterEntity.class, EntityDataSerializers.BOOLEAN);
  public static final int INV_CHEST_COUNT = 15;
  private static final double TELEPORT_DISTANCE = 16.0;
  public static final float ORIGINAL_SPEED = 1f;
  private static final int MAX_JUMP_COOLDOWN = 14;
  private int jumpCooldown = 0;
  private AnimationFactory factory = new AnimationFactory(this);

  private boolean tamed = false;

  public ChesterEntity(EntityType<? extends ChesterEntity> entityType, Level level) {
    super(entityType, level);
  }

  public static AttributeSupplier.Builder getChesterEntityAttrs() {
    return Mob.createMobAttributes()
        .add(ForgeMod.ENTITY_GRAVITY.get(), 2.5f)
        .add(Attributes.MOVEMENT_SPEED, 0.30D)
        .add(Attributes.MAX_HEALTH, 60);
  }

  protected void defineSynchedData() {
    super.defineSynchedData();
    this.entityData.define(DATA_ID_CHEST, false);

  }

  public boolean hasChest() {
    return true;
  }

  public boolean isSaddleable() {
    return false;
  }

  @Override
  public void tick() {
    super.tick();

    followOwner();
    swimInWater();

    UUID ownerUUID = this.getOwnerUUID();
    if (this.isTamed() && ownerUUID != null) {
      Player owner = this.level.getPlayerByUUID(ownerUUID);
      if (owner != null) {
        double distance = this.distanceTo(owner);
        if (distance > TELEPORT_DISTANCE) {
          teleportToOwner();
        }
      }
    }
  }

  private void swimInWater() {
    UUID ownerUUID = this.getOwnerUUID();
    Player owner = ownerUUID != null ? this.level.getPlayerByUUID(ownerUUID) : null;

    if (owner != null && owner.isInWater()) {
      double waterSurfaceY = owner.getY() + 0.5;

      double minY = waterSurfaceY - 1.0;
      if (this.getY() < minY) {
        this.teleportTo(this.getX(), minY, this.getZ());
      }

      this.getNavigation().setCanFloat(true);
      this.setSpeed(1.0f);

      this.getNavigation().moveTo(owner.getX(), waterSurfaceY, owner.getZ(), 2.0);

    } else {
      this.getNavigation().setCanFloat(false);
      this.setSpeed(ORIGINAL_SPEED);
    }
  }

  private void teleportToOwner() {
    UUID ownerUUID = this.getOwnerUUID();

    if (ownerUUID != null && this.level instanceof ServerLevel) {
      ServerLevel serverLevel = (ServerLevel) this.level;
      Entity ownerEntity = serverLevel.getEntity(ownerUUID);

      if (ownerEntity instanceof Player) {
        Player owner = (Player) ownerEntity;
        if (owner != null) {
          Vec3 ownerPos = owner.position();
          if (owner.isOnGround()) {
            this.teleportTo(ownerPos.x(), ownerPos.y(), ownerPos.z());
          }
        }
      }
    }
  }

  private void followOwner() {
    UUID ownerUUID = this.getOwnerUUID();

    if (ownerUUID != null) {
      double followSpeed = 1.0;
      double stopDistance = 2.0;
      PathNavigation navigation = this.getNavigation();
      LivingEntity owner = this.level.getPlayerByUUID(ownerUUID);

      if (owner != null) {
        double distance = this.distanceTo(owner);

        if (distance > stopDistance) {
          navigation.moveTo(owner, followSpeed);
          jump();
        } else {
          navigation.stop();
        }
      }
    }
  }

  private void jump() {
    if (this.isOnGround() && jumpCooldown <= 0) {
      JumpControl jumpControl = (JumpControl) this.getJumpControl();
      if (jumpControl != null) {
        jumpControl.jump();
        jumpCooldown = MAX_JUMP_COOLDOWN;
      } else {
        if (this.canJump) {
          this.setDeltaMovement(this.getDeltaMovement().add(0, 1.5, 0));
          this.canJump = false;
        }
      }
    }

    if (jumpCooldown > 0) {
      jumpCooldown--;
    }
  }

  @Override
  public boolean hurt(DamageSource damageSource, float p_29659_) {
    if (damageSource == DamageSource.FALLING_BLOCK) {
      return super.hurt(damageSource, 0);
    } else if (damageSource == DamageSource.DROWN) {
      return super.hurt(damageSource, 0);
    }
    return super.hurt(damageSource, 3);
  }

  public boolean feedApple(Player player) {
    if (this.isAlive() && !this.isTamed() && player != null) {
      ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);
      if (heldItem.is(Items.APPLE)) {
        this.setTamed(true);
        this.setOwnerUUID(player.getUUID());
        this.followOwner();
        heldItem.shrink(1);
        return true;
      }
    }
    return false;
  }

  @Override
  public void onAddedToWorld() {
    super.onAddedToWorld();

    this.goalSelector.addGoal(0, new Goal() {
      @Override
      public boolean canUse() {
        return true;
      }

    });
  }

  private boolean canJump;

  @Override
  public boolean canJump() {
    return super.canJump();
  }

  public void setCanJump(boolean canJump) {
    this.canJump = canJump;
  }

  public void setChest(boolean p_30505_) {
    this.entityData.set(DATA_ID_CHEST, p_30505_);
  }

  protected int getInventorySize() {
    return this.hasChest() ? 17 : super.getInventorySize();
  }

  protected void dropEquipment() {
    super.dropEquipment();
  }

  public boolean isTamed() {
    return tamed;
  }

  public void setTamed(boolean tamed) {
    this.tamed = tamed;
  }

  public boolean canBreatheUnderwater() {
    return true;
  }

  public void addAdditionalSaveData(CompoundTag compound) {
    super.addAdditionalSaveData(compound);
    compound.putBoolean("HasChest", this.hasChest());

    if (this.hasChest()) {
      ListTag itemList = new ListTag();

      for (int i = 2; i < this.inventory.getContainerSize(); ++i) {
        ItemStack itemStack = this.inventory.getItem(i);
        if (!itemStack.isEmpty()) {
          CompoundTag itemTag = new CompoundTag();
          itemTag.putByte("Slot", (byte) i);
          itemStack.save(itemTag);
          itemList.add(itemTag);
        }
      }

      compound.put("Items", itemList);
    }

    if (this.getOwnerUUID() != null) {
      compound.putBoolean("Tamed", this.isTamed());
      compound.putUUID("Owner", this.getOwnerUUID());
    }
  }

  public void readAdditionalSaveData(CompoundTag compound) {
    super.readAdditionalSaveData(compound);
    this.setChest(compound.getBoolean("HasChest"));
    this.createInventory();

    if (this.hasChest()) {
      ListTag itemList = compound.getList("Items", 10);

      for (int i = 0; i < itemList.size(); ++i) {
        CompoundTag itemTag = itemList.getCompound(i);
        int slot = itemTag.getByte("Slot") & 255;
        if (slot >= 2 && slot < this.inventory.getContainerSize()) {
          this.inventory.setItem(slot, ItemStack.of(itemTag));
        }
      }
    }

    if (compound.hasUUID("Owner")) {
      this.setTamed(compound.getBoolean("Tamed"));
      this.setOwnerUUID(compound.getUUID("Owner"));
    }
    this.updateContainerEquipment();
  }

  public SlotAccess getSlot(int slotIndex) {
    return slotIndex == 499 ? new SlotAccess() {
      public ItemStack get() {
        return ChesterEntity.this.hasChest() ? new ItemStack(Items.CHEST) : ItemStack.EMPTY;
      }

      public boolean set(ItemStack stack) {
        if (stack.isEmpty()) {
          if (ChesterEntity.this.hasChest()) {
            ChesterEntity.this.setChest(false);
            ChesterEntity.this.createInventory();
          }
          return true;
        } else {
          return false;
        }
      }
    } : super.getSlot(slotIndex);
  }

  @Override
  protected void registerGoals() {
    this.goalSelector.addGoal(0, new FloatGoal(this));
    this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
    this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
    this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0D));
    this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0F));
    this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
  }

  @Override
  public InteractionResult mobInteract(Player player, InteractionHand hand) {
    ItemStack itemstack = player.getItemInHand(hand);

    this.playChestEquipsSound();

    if (itemstack.is(Items.APPLE)) {
      if (!this.isTamed()) {
        if (this.feedApple(player)) {
          customTamingParticles();
          return InteractionResult.SUCCESS;
        }
      } else {
        this.openCustomInventoryScreen(player);
      }
    } else if (this.hasChest() && this.isTamed()) {
      this.openCustomInventoryScreen(player);
    } else {
      return InteractionResult.SUCCESS;
    }

    return InteractionResult.SUCCESS;
  }

  protected void playChestEquipsSound() {
    this.playSound(SoundEvents.DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
  }

  public int getInventoryColumns() {
    return 5;
  }

  private void customTamingParticles() {
    ParticleOptions particleoptions = ParticleTypes.HEART;
    followOwner();
    for (int i = 0; i < 7; ++i) {
      double d0 = this.random.nextGaussian() * 0.02D;
      double d1 = this.random.nextGaussian() * 0.02D;
      double d2 = this.random.nextGaussian() * 0.02D;
      this.level.addParticle(particleoptions, this.getRandomX(1.0D), this.getRandomY() + 0.5D, this.getRandomZ(1.0D),
          d0, d1, d2);
    }
  }

  private <E extends IAnimatable> PlayState predicate(AnimationEvent<E> event) {
    if (event.isMoving()) {
      event.getController()
          .setAnimation(new AnimationBuilder().addAnimation("movement.model.new", true));
      return PlayState.CONTINUE;
    }
    event.getController()
        .setAnimation(new AnimationBuilder().addAnimation("movement.model.new", true));
    return PlayState.CONTINUE;
  }

  @Override
  public void registerControllers(AnimationData data) {
    data.addAnimationController(new AnimationController<>(this, "controller", 20, this::predicate));
  }

  @Override
  public AnimationFactory getFactory() {
    return this.factory;
  }

  protected void playStepSound(BlockPos pos, BlockState blockIn) {
    this.playSound(SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES, 0.15F, 1.0F);
  }

  protected SoundEvent getAmbientSound() {
    return SoundEvents.CAT_STRAY_AMBIENT;
  }

  protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
    return SoundEvents.DOLPHIN_HURT;
  }

  protected SoundEvent getDeathSound() {
    return SoundEvents.DOLPHIN_DEATH;
  }

  protected float getSoundVolume() {
    return 0.2F;
  }

}
