package com.docs.chestermod.init;

import com.docs.chestermod.Chestermod;
import com.docs.chestermod.entities.ChesterEntity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModsInit {
  public static final DeferredRegister<EntityType<?>> MOBS = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
      Chestermod.MODID);

  public static final RegistryObject<EntityType<ChesterEntity>> CHESTER = MOBS.register("chester",
      () -> EntityType.Builder.of(ChesterEntity::new, MobCategory.CREATURE)
          .build(Chestermod.MODID + ":chester"));
}
