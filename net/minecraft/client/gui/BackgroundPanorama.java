package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Project;

public class BackgroundPanorama {
   private final Minecraft mc;
   private DynamicTexture viewportTexture;
   private ResourceLocation backgroundPano;
   private int panoramaTimer;
   private static final ResourceLocation[] titlePanoramaPaths = new ResourceLocation[]{new ResourceLocation("textures/gui/title/background/panorama_0.png"), new ResourceLocation("textures/gui/title/background/panorama_1.png"), new ResourceLocation("textures/gui/title/background/panorama_2.png"), new ResourceLocation("textures/gui/title/background/panorama_3.png"), new ResourceLocation("textures/gui/title/background/panorama_4.png"), new ResourceLocation("textures/gui/title/background/panorama_5.png")};
   private int width = 100;
   private int height = 100;
   private float zLevel = 0.0F;

   public BackgroundPanorama(Minecraft p_i1_1_) {
      this.mc = p_i1_1_;
      this.viewportTexture = new DynamicTexture(256, 256);
      this.backgroundPano = p_i1_1_.getTextureManager().getDynamicTextureLocation("background", this.viewportTexture);
   }

   public void updateWidthHeight(int p_updateWidthHeight_1_, int p_updateWidthHeight_2_) {
      this.width = p_updateWidthHeight_1_;
      this.height = p_updateWidthHeight_2_;
   }

   public void tickPanorama() {
      ++this.panoramaTimer;
   }

   private void drawPanorama(int p_drawPanorama_1_, int p_drawPanorama_2_, float p_drawPanorama_3_) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      GlStateManager.matrixMode(5889);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      Project.gluPerspective(120.0F, 1.0F, 0.05F, 10.0F);
      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.loadIdentity();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.enableBlend();
      GlStateManager.disableAlpha();
      GlStateManager.disableCull();
      GlStateManager.depthMask(false);
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      int i = 8;

      for(int j = 0; j < i * i; ++j) {
         GlStateManager.pushMatrix();
         float f = ((float)(j % i) / (float)i - 0.5F) / 64.0F;
         float f1 = ((float)(j / i) / (float)i - 0.5F) / 64.0F;
         float f2 = 0.0F;
         GlStateManager.translate(f, f1, f2);
         GlStateManager.rotate(MathHelper.sin(((float)this.panoramaTimer + p_drawPanorama_3_) / 400.0F) * 25.0F + 20.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(-((float)this.panoramaTimer + p_drawPanorama_3_) * 0.1F, 0.0F, 1.0F, 0.0F);

         for(int k = 0; k < 6; ++k) {
            GlStateManager.pushMatrix();
            if(k == 1) {
               GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
            }

            if(k == 2) {
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if(k == 3) {
               GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            if(k == 4) {
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if(k == 5) {
               GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            this.mc.getTextureManager().bindTexture(titlePanoramaPaths[k]);
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            int l = 255 / (j + 1);
            float f3 = 0.0F;
            worldrenderer.pos(-1.0D, -1.0D, 1.0D).tex(0.0D, 0.0D).color(255, 255, 255, l).endVertex();
            worldrenderer.pos(1.0D, -1.0D, 1.0D).tex(1.0D, 0.0D).color(255, 255, 255, l).endVertex();
            worldrenderer.pos(1.0D, 1.0D, 1.0D).tex(1.0D, 1.0D).color(255, 255, 255, l).endVertex();
            worldrenderer.pos(-1.0D, 1.0D, 1.0D).tex(0.0D, 1.0D).color(255, 255, 255, l).endVertex();
            tessellator.draw();
            GlStateManager.popMatrix();
         }

         GlStateManager.popMatrix();
         GlStateManager.colorMask(true, true, true, false);
      }

      worldrenderer.setTranslation(0.0D, 0.0D, 0.0D);
      GlStateManager.colorMask(true, true, true, true);
      GlStateManager.matrixMode(5889);
      GlStateManager.popMatrix();
      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.enableDepth();
   }

   private void rotateAndBlurSkybox(float p_rotateAndBlurSkybox_1_) {
      this.mc.getTextureManager().bindTexture(this.backgroundPano);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, 256, 256);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.colorMask(true, true, true, false);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      GlStateManager.disableAlpha();
      int i = 3;

      for(int j = 0; j < i; ++j) {
         float f = 1.0F / (float)(j + 1);
         int k = this.width;
         int l = this.height;
         float f1 = (float)(j - i / 2) / 256.0F;
         worldrenderer.pos((double)k, (double)l, (double)this.zLevel).tex((double)(0.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
         worldrenderer.pos((double)k, 0.0D, (double)this.zLevel).tex((double)(1.0F + f1), 1.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
         worldrenderer.pos(0.0D, 0.0D, (double)this.zLevel).tex((double)(1.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
         worldrenderer.pos(0.0D, (double)l, (double)this.zLevel).tex((double)(0.0F + f1), 0.0D).color(1.0F, 1.0F, 1.0F, f).endVertex();
      }

      tessellator.draw();
      GlStateManager.enableAlpha();
      GlStateManager.colorMask(true, true, true, true);
   }

   public void renderSkybox(int p_renderSkybox_1_, int p_renderSkybox_2_, float p_renderSkybox_3_) {
      this.mc.getFramebuffer().unbindFramebuffer();
      GlStateManager.viewport(0, 0, 256, 256);
      this.drawPanorama(p_renderSkybox_1_, p_renderSkybox_2_, p_renderSkybox_3_);
      this.rotateAndBlurSkybox(p_renderSkybox_3_);
      this.rotateAndBlurSkybox(p_renderSkybox_3_);
      this.rotateAndBlurSkybox(p_renderSkybox_3_);
      this.rotateAndBlurSkybox(p_renderSkybox_3_);
      this.rotateAndBlurSkybox(p_renderSkybox_3_);
      this.rotateAndBlurSkybox(p_renderSkybox_3_);
      this.rotateAndBlurSkybox(p_renderSkybox_3_);
      this.mc.getFramebuffer().bindFramebuffer(true);
      GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      float f = this.width > this.height?120.0F / (float)this.width:120.0F / (float)this.height;
      float f1 = (float)this.height * f / 256.0F;
      float f2 = (float)this.width * f / 256.0F;
      int i = this.width;
      int j = this.height;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldrenderer.pos(0.0D, (double)j, (double)this.zLevel).tex((double)(0.5F - f1), (double)(0.5F + f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldrenderer.pos((double)i, (double)j, (double)this.zLevel).tex((double)(0.5F - f1), (double)(0.5F - f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldrenderer.pos((double)i, 0.0D, (double)this.zLevel).tex((double)(0.5F + f1), (double)(0.5F - f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      worldrenderer.pos(0.0D, 0.0D, (double)this.zLevel).tex((double)(0.5F + f1), (double)(0.5F + f2)).color(1.0F, 1.0F, 1.0F, 1.0F).endVertex();
      tessellator.draw();
   }
}
