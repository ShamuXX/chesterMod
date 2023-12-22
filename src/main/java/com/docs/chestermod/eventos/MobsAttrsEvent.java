package com.docs.chestermod.eventos;

import com.docs.chestermod.Chestermod;
import com.docs.chestermod.entities.ChesterEntity;
import com.docs.chestermod.init.ModsInit;

import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Chestermod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MobsAttrsEvent {
  @SubscribeEvent
  public static void entityAttributes(EntityAttributeCreationEvent event) {
    event.put(ModsInit.CHESTER.get(), ChesterEntity.getChesterEntityAttrs().build());
  }

}
