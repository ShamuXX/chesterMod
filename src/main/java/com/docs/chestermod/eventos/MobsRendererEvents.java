package com.docs.chestermod.eventos;

import com.docs.chestermod.Chestermod;
import com.docs.chestermod.client.models.ChesterModel;
import com.docs.chestermod.client.renderer.ChesterRenderer;
import com.docs.chestermod.init.ModsInit;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Chestermod.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class MobsRendererEvents {
  @SubscribeEvent
  public static void entityRenders(EntityRenderersEvent.RegisterRenderers event) {
    event.registerEntityRenderer(ModsInit.CHESTER.get(), ChesterRenderer::new);
  }

  @SubscribeEvent
  public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
    event.registerLayerDefinition(ChesterModel.LAYER_LOCATION, ChesterModel::createBodyLayer);
  }
}
