package net.minecraft.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;

public class RenderCow extends RenderLiving {
   private static final ResourceLocation cowTextures = new ResourceLocation("textures/entity/cow/cow.png");

   public RenderCow(RenderManager renderManagerIn, ModelBase modelBaseIn, float shadowSizeIn) {
      super(renderManagerIn, modelBaseIn, shadowSizeIn);
   }

   protected ResourceLocation getEntityTexture(EntityCow entity) {
      return cowTextures;
   }
}
