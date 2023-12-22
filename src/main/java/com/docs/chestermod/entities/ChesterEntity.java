package com.docs.chestermod.entities;

import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
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
import net.minecraftforge.common.ForgeMod;

import java.util.UUID;;

public class ChesterEntity extends AbstractChestedHorse {
  private static final EntityDataAccessor<Boolean> DATA_ID_CHEST = SynchedEntityData
      .defineId(ChesterEntity.class, EntityDataSerializers.BOOLEAN);
  public static final int INV_CHEST_COUNT = 15;

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

    // Llama al método de seguimiento personalizado
    followOwner();
  }

  private void followOwner() {
    UUID ownerUUID = this.getOwnerUUID();

    if (ownerUUID != null) {
      double followSpeed = 1.0; // Ajusta la velocidad según sea necesario

      // Obtiene el sistema de navegación
      PathNavigation navigation = this.getNavigation();

      // Obtiene la entidad del mundo a partir de la UUID del propietario
      LivingEntity owner = this.level.getPlayerByUUID(ownerUUID);

      if (owner != null) {
        // Mueve la entidad hacia el dueño
        navigation.moveTo(owner, followSpeed);
      }
    }
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

    // Restablecer la tarea de seguimiento al entrar al mundo
    this.goalSelector.addGoal(0, new Goal() {
      @Override
      public boolean canUse() {
        return true;
      }

    });
  }

  public void setChest(boolean p_30505_) {
    this.entityData.set(DATA_ID_CHEST, p_30505_);
  }

  protected int getInventorySize() {
    System.err.println(super.getInventorySize());
    return this.hasChest() ? 17 : super.getInventorySize();
  }

  protected void dropEquipment() {
    super.dropEquipment();
  }

  public boolean isTamed() {
    System.err.println(tamed);
    return tamed;
  }

  public void setTamed(boolean tamed) {
    this.tamed = tamed;
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
    this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
    this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 1.0D));
    this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
    this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

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
        // Si el jugador utiliza una manzana, alimenta al mob
      } else {
        this.openCustomInventoryScreen(player);
      }
    } else if (this.hasChest() && this.isTamed()) {
      this.openCustomInventoryScreen(player); // open interface to access inventory
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

}
