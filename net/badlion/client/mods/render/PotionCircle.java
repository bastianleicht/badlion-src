package net.badlion.client.mods.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class PotionCircle {
   protected static final transient ResourceLocation inventoryBackground = new ResourceLocation("textures/gui/container/inventory.png");
   private transient int maxDuration = 0;
   private final transient Minecraft gameInstance;
   private final PotionEffect type;

   public PotionCircle(PotionEffect type) {
      this.type = type;
      this.gameInstance = Minecraft.getMinecraft();
   }

   public PotionEffect getType() {
      return this.type;
   }

   private void drawCircle(GuiIngame gameRenderer, int x0, int y0, double value, double maxValue) {
      float f = 0.0F;
      GlStateManager.pushMatrix();
      GL11.glScaled(2.0D, 2.0D, 2.0D);
      GL11.glDisable(3553);
      int i = (int)((double)x0 * 0.5D) + 6;
      int j = (int)((double)y0 * 0.5D) + 6;
      int k = (int)(100.0D * (Math.ceil(value) / maxValue));
      GlStateManager.color(0.0F, 0.0F, 0.0F, 0.5F);
      GL11.glBegin(6);
      GL11.glVertex2d((double)i, (double)j);
      GL11.glEnd();
      GlStateManager.color(0.98F, 0.7F, 0.2F);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GL11.glBegin(6);
      GL11.glVertex2d((double)i, (double)j);
      GL11.glEnd();
      if(value < maxValue * 0.1D && System.currentTimeMillis() % 2000L > 1000L) {
         GlStateManager.color(0.6F, 0.2F, 0.2F, 1.0F);
      } else {
         GlStateManager.color(0.2F, 0.2F, 0.2F, 1.0F);
      }

      GL11.glEnable(2848);
      GL11.glLineWidth(2.0F);
      GL11.glBegin(6);
      GL11.glVertex2d((double)i, (double)j);
      GL11.glEnd();
      GL11.glDisable(2848);
      GL11.glEnable(3553);
      GlStateManager.popMatrix();
   }

   public void render(GuiIngame gameRenderer, int x, int y, int color) {
      if(this.getType() != null) {
         Potion potion = Potion.potionTypes[this.getType().getPotionID()];
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         if(this.getType().getDuration() > this.maxDuration) {
            this.maxDuration = this.getType().getDuration();
         }

         this.drawCircle(gameRenderer, x, y, (double)this.getType().getDuration(), (double)this.maxDuration);
         GL11.glScaled(0.5D, 0.5D, 0.5D);
         gameRenderer.drawString(this.gameInstance.fontRendererObj, Potion.getDurationString(this.getType()), (int)((double)x * 0.5D * 4.0D) + 15, (int)((double)y * 0.5D * 4.0D) + 32, color);
         GL11.glScaled(2.0D, 2.0D, 2.0D);
         this.gameInstance.getTextureManager().bindTexture(inventoryBackground);
         if(potion.hasStatusIcon()) {
            int i = potion.getStatusIconIndex();
            double d0 = 0.6D;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glScaled(d0, d0, d0);
            gameRenderer.drawTexturedModalRect((int)((double)(x + 7) / d0), (int)((double)(y + 4) / d0), 0 + i % 8 * 18, 198 + i / 8 * 18, 18, 18);
            GL11.glScaled(1.0D / d0, 1.0D / d0, 1.0D / d0);
         }
      } else {
         this.drawCircle(gameRenderer, x, y, 0.0D, 0.0D);
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }
}
