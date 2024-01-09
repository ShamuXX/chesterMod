package com.docs.chestermod;

import com.docs.chestermod.eventos.MobsAttrsEvent;
import com.docs.chestermod.eventos.MobsRendererEvents;
import com.docs.chestermod.init.InitItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.geckolib3.GeckoLib;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;
import static com.docs.chestermod.init.ModsInit.MOBS;

@Mod(Chestermod.MODID)
public class Chestermod {
  public static final String MODID = "chestermod";

  public Chestermod() {
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    InitItems.ITEMS.register(bus);
    EVENT_BUS.register(new MobsAttrsEvent());
    EVENT_BUS.register(new MobsRendererEvents());
    MOBS.register(bus);
    GeckoLib.initialize();
  }

}
