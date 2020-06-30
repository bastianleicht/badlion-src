package net.badlion.client.mods.render.gui.elements;

import net.badlion.client.mods.render.gui.Component;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;
import org.lwjgl.opengl.GL11;

public class Label extends Component {
   private String text;
   private int color;
   private double scale;

   public Label(String name, String text, int x, int y, double scale, int color) {
      super(name, x, y);
      this.text = text;
      this.color = color;
      this.scale = scale;
   }

   public double getScale() {
      return this.scale;
   }

   public void setScale(double scale) {
      this.scale = scale;
   }

   public void setColor(int color) {
      this.color = color;
   }

   public int getColor() {
      return this.color;
   }

   public void setText(String text) {
      this.text = text;
   }

   public String getText() {
      return this.text;
   }

   public int getSizeX() {
      return (int)(this.scale * (double)Minecraft.getMinecraft().fontRendererObj.getStringWidth(this.text));
   }

   public int getSizeY() {
      return (int)(8.0D * this.scale);
   }

   public void render(GuiIngame gameRenderer, int x, int y) {
      if(this.isVisible()) {
         GL11.glScaled(this.scale, this.scale, 1.0D);
         Minecraft.getMinecraft().fontRendererObj.drawString(this.getText(), (int)((double)x / this.scale), (int)((double)y / this.scale), this.getColor());
         GL11.glScaled(1.0D / this.scale, 1.0D / this.scale, 1.0D);
      }

   }
}
