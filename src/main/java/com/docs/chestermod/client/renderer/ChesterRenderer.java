package com.docs.chestermod.client.renderer;

import com.docs.chestermod.entities.ChesterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import org.jetbrains.annotations.Nullable;

import com.docs.chestermod.Chestermod;
import com.docs.chestermod.client.models.*;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

public class ChesterRenderer extends GeoEntityRenderer<ChesterEntity> {

    public ChesterRenderer(EntityRendererProvider.Context context) {
        super(context, new ChesterModel());
    }

    @Override
    public ResourceLocation getTextureLocation(ChesterEntity instance) {
        return new ResourceLocation(Chestermod.MODID, "textures/entity/chester_texture.png");
    }

}
