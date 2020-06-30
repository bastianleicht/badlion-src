package net.badlion.client.gui.slideout;

import net.badlion.client.gui.slideout.RenderElement;
import net.badlion.client.gui.slideout.fontrenderer.CustomFontRenderer;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class KeyBindElement extends RenderElement {
   private CustomFontRenderer fontRenderer;
   private boolean selected;
   private int keyCode;
   private boolean hovered;

   public KeyBindElement(int keyCode) {
      this.keyCode = keyCode;
   }

   public void init() {
      this.fontRenderer = new CustomFontRenderer();
   }

   public void render() {
      String s = this.selected?"_":Keyboard.getKeyName(this.keyCode);
      Gui.drawRect(this.getX(), this.getY(), this.getX() + this.getWidth(), this.getY() + this.getHeight(), -1723579324);
      double d0 = 1.3D;
      GL11.glScaled(d0, d0, d0);
      this.fontRenderer.drawString(s, (int)((double)this.getX() / d0 + (double)this.getWidth() / d0 / 2.0D - (double)this.fontRenderer.getStringWidth(s) * 1.2D / d0 / 2.0D), (int)((double)(this.getY() + 8) / d0), -1);
      GL11.glScaled(1.0D / d0, 1.0D / d0, 1.0D / d0);
   }

   public boolean onClick(int mouseButton) {
      if(mouseButton == 0) {
         this.selected = this.hovered;
      }

      return false;
   }

   public void keyTyped(char character, int keyCode) {
      if(this.selected) {
         this.keyCode = keyCode;
         this.selected = false;
      }

   }

   public void update(int mX, int mY) {
      this.hovered = false;
      if(mX > this.getX() && mX < this.getX() + this.getWidth() && mY > this.getY() && mY < this.getY() + this.getHeight()) {
         this.hovered = true;
      }

   }

   public int getKeyCode() {
      return this.keyCode;
   }

   public int getWidth() {
      return 64;
   }

   public int getHeight() {
      return 32;
   }

   public int getKey() {
      return this.keyCode;
   }
}
