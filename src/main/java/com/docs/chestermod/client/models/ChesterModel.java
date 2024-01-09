package com.docs.chestermod.client.models;

import com.docs.chestermod.Chestermod;
import com.docs.chestermod.entities.ChesterEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib3.model.AnimatedGeoModel;

public class ChesterModel extends AnimatedGeoModel<ChesterEntity> {
    @Override
    public ResourceLocation getAnimationResource(ChesterEntity animatable) {
        return new ResourceLocation(Chestermod.MODID, "animations/movement.model.new.json");
    }

    @Override
    public ResourceLocation getModelResource(ChesterEntity object) {
        return new ResourceLocation(Chestermod.MODID, "geo/chester_animated.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ChesterEntity object) {
        return new ResourceLocation(Chestermod.MODID, "textures/entity/chester_texture.png");
    }
}