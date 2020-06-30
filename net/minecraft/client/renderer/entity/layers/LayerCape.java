package net.minecraft.client.renderer.entity.layers;

import net.badlion.client.manager.CapeManager;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class LayerCape implements LayerRenderer {
   private final RenderPlayer playerRenderer;

   public LayerCape(RenderPlayer playerRendererIn) {
      this.playerRenderer = playerRendererIn;
   }

   public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float p_177141_2_, float p_177141_3_, float partialTicks, float p_177141_5_, float p_177141_6_, float p_177141_7_, float scale) {
      CapeManager.renderCape(this.playerRenderer, entitylivingbaseIn, p_177141_2_, p_177141_3_, partialTicks, p_177141_5_, p_177141_6_, p_177141_7_, scale);
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
