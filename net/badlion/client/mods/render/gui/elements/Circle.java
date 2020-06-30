package net.badlion.client.mods.render.gui.elements;

import net.badlion.client.mods.render.gui.SizeableComponent;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class Circle extends SizeableComponent {
   private float r;
   private float g;
   private float b;

   public Circle(String name, int sizeX, int sizeY, float r, float g, float b) {
      super(name, sizeX, sizeY);
      this.r = r;
      this.g = g;
      this.b = b;
   }

   public float[] getColor() {
      return new float[]{this.r, this.g, this.b};
   }

   public void setColor(float r, float g, float b) {
      this.r = r;
      this.g = g;
      this.b = b;
   }

   public void render(GuiIngame gameRenderer, int x0, int y0) {
      if(this.isVisible()) {
         GL11.glScaled((double)this.sizeX / 4.0D, (double)this.sizeY / 4.0D, 1.0D);
         this.drawCircle(gameRenderer, (int)((double)x0 / ((double)this.sizeX / 4.0D)), (int)((double)y0 / ((double)this.sizeY / 4.0D)), this.r, this.g, this.b);
         GL11.glScaled(1.0D / ((double)this.sizeX / 4.0D), 1.0D / ((double)this.sizeY / 4.0D), 1.0D);
      }

   }

   private void drawCircle(GuiIngame gameRenderer, int x0, int y0, float r, float g, float b) {
      float f = 0.0F;
      GlStateManager.pushMatrix();
      GL11.glScaled(2.0D, 2.0D, 2.0D);
      GL11.glDisable(3553);
      int i = (int)((double)x0 * 0.5D) + 6;
      int j = (int)((double)y0 * 0.5D) + 6;
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      GlStateManager.color(r, g, b, 1.0F);
      GL11.glEnable(2848);
      GL11.glLineWidth(2.0F);
      GL11.glBegin(6);
      GL11.glVertex2d((double)i, (double)j);

      for(int k = 0; k <= 100; ++k) {
         GL11.glVertex2d((double)i - 1.0D * Math.sin((double)k * 0.06283185307179587D), (double)j - 1.0D * Math.cos((double)k * 0.06283185307179587D));
      }

      GL11.glEnd();
      GL11.glDisable(2848);
      GL11.glEnable(3553);
      GlStateManager.popMatrix();
   }
}
