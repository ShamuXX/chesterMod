package com.docs.chestermod;

import com.docs.chestermod.init.InitItems;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Chestermod.MODID)
public class Chestermod {
  public static final String MODID = "chestermod";

  public Chestermod() {
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    InitItems.ITEMS.register(bus);
  }
}
