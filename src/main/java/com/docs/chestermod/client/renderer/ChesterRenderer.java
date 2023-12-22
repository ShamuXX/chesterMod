package com.docs.chestermod.client.renderer;

import com.docs.chestermod.Chestermod;
import com.docs.chestermod.client.models.ChesterModel;
import com.docs.chestermod.entities.ChesterEntity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

public class ChesterRenderer extends MobRenderer<ChesterEntity, ChesterModel> {

  private static final ResourceLocation TEXTURE = new ResourceLocation(Chestermod.MODID,
      "textures/entities/chester.png");

  public ChesterRenderer(EntityRendererProvider.Context context) {
    super(context, new ChesterModel(context.bakeLayer(ChesterModel.LAYER_LOCATION)), 0.25f);
  }

  @Override
  public ResourceLocation getTextureLocation(ChesterEntity p_114482_) {
    return TEXTURE;
  }

}
