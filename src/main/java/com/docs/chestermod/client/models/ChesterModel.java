package com.docs.chestermod.client.models;

import com.docs.chestermod.Chestermod;
import com.docs.chestermod.entities.ChesterEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.resources.ResourceLocation;

public class ChesterModel extends EntityModel<ChesterEntity> {
  public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
      new ResourceLocation(Chestermod.MODID, "chester"), "main");
  private final ModelPart bb_main;

  public ChesterModel(ModelPart root) {
    this.bb_main = root.getChild("bb_main");
  }

  public static LayerDefinition createBodyLayer() {
    MeshDefinition meshdefinition = new MeshDefinition();
    PartDefinition partdefinition = meshdefinition.getRoot();

    PartDefinition bb_main = partdefinition.addOrReplaceChild("bb_main",
        CubeListBuilder.create().texOffs(0, 0)
            .addBox(-6.0F, -10.0F, -6.0F, 12.0F, 9.0F, 12.0F, new CubeDeformation(0.0F))
            .texOffs(24, 21).addBox(4.0F, -5.0F, 4.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(12, 21).addBox(-7.0F, -5.0F, 4.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 21).addBox(-7.0F, -5.0F, -7.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 0).addBox(4.0F, -5.0F, -7.0F, 3.0F, 5.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(36, 25).addBox(-7.0F, -1.0F, -8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(19, 29).addBox(3.0F, -6.0F, -7.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(9, 29).addBox(2.0F, -5.0F, -7.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(21, 21).addBox(-1.0F, -5.0F, -7.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(9, 21).addBox(-4.0F, -5.0F, -7.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(10, 35).addBox(-2.0F, -6.0F, -7.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(26, 34).addBox(-3.0F, -6.0F, -8.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(33, 21).addBox(-3.0F, -5.0F, -8.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(17, 33).addBox(-3.0F, -4.0F, -8.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(7, 33).addBox(-3.0F, -3.0F, -8.0F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(36, 23).addBox(-6.0F, -1.0F, -8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(36, 9).addBox(-5.0F, -1.0F, -8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(36, 7).addBox(6.0F, -1.0F, -8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(36, 5).addBox(5.0F, -1.0F, -8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(36, 3).addBox(4.0F, -1.0F, -8.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(19, 29).addBox(-5.0F, -6.0F, -7.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)),
        PartPose.offset(0.0F, 24.0F, 0.0F));

    PartDefinition cube_r1 = bb_main.addOrReplaceChild("cube_r1",
        CubeListBuilder.create().texOffs(34, 27)
            .addBox(-1.0F, -14.0F, 4.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(26, 36).addBox(1.0F, -16.0F, 5.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(34, 34).addBox(0.0F, -15.0F, 4.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(12, 29).addBox(-1.0F, -13.0F, 3.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(22, 29).addBox(-2.0F, -12.0F, 2.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 8).addBox(-3.0F, -11.0F, 2.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -1.9199F, 0.0F));

    PartDefinition cube_r2 = bb_main.addOrReplaceChild("cube_r2",
        CubeListBuilder.create().texOffs(30, 36)
            .addBox(4.0F, -16.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(18, 35).addBox(3.0F, -15.0F, -2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(36, 0).addBox(2.0F, -14.0F, -2.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(29, 30).addBox(2.0F, -13.0F, -3.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 33).addBox(1.0F, -12.0F, -4.0F, 2.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
            .texOffs(0, 29).addBox(0.0F, -11.0F, -4.0F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)),
        PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0873F, -0.3927F, 0.0F));

    return LayerDefinition.create(meshdefinition, 64, 64);
  }

  @Override
  public void setupAnim(ChesterEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks,
      float netHeadYaw, float headPitch) {

  }

  @Override
  public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay,
      float red, float green, float blue, float alpha) {
    bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
  }
}
