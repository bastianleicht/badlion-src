package net.badlion.client.gui.slideout.fontrenderer;

import java.awt.Font;
import net.badlion.client.Wrapper;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.util.ColorUtil;
import net.minecraft.client.Minecraft;

public class CustomFontRenderer {
   private CustomFontRenderer.RenderMode mode = CustomFontRenderer.RenderMode.CUSTOM;

   public CustomFontRenderer(Font font) {
   }

   public CustomFontRenderer() {
   }

   public void setRenderMode(CustomFontRenderer.RenderMode mode) {
      this.mode = mode;
   }

   public CustomFontRenderer.RenderMode getMode() {
      return this.mode;
   }

   public void drawString(String text, int x, int y, int color) {
      if(this.mode.equals(CustomFontRenderer.RenderMode.DEFAULT)) {
         Minecraft.getMinecraft().fontRendererObj.drawString(text, x, y, color);
      } else {
         this.drawString(text, x, y, 12, BadlionFontRenderer.FontType.TITLE, color);
      }

   }

   public void drawString(String text, int x, int y, int color, int size) {
      if(this.mode.equals(CustomFontRenderer.RenderMode.DEFAULT)) {
         Minecraft.getMinecraft().fontRendererObj.drawString(text, x, y, color);
      } else {
         this.drawString(text, x, y, size, BadlionFontRenderer.FontType.TITLE, color);
      }

   }

   public void drawString(String text, int x, int y, int fontHeight, BadlionFontRenderer.FontType type, int color) {
      if(this.mode.equals(CustomFontRenderer.RenderMode.DEFAULT)) {
         Minecraft.getMinecraft().fontRendererObj.drawString(text, x, y, color);
      } else {
         ColorUtil.bindHexColorRGBA(color);
         Wrapper.getInstance().getBadlionFontRenderer().drawString(x, y, text, fontHeight, type, true);
      }

   }

   public int getStringWidth(String string, int fontHeight, BadlionFontRenderer.FontType type) {
      return this.mode.equals(CustomFontRenderer.RenderMode.DEFAULT)?Minecraft.getMinecraft().fontRendererObj.getStringWidth(string):Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(string, fontHeight, type);
   }

   public int getStringWidth(String string) {
      return this.mode.equals(CustomFontRenderer.RenderMode.DEFAULT)?Minecraft.getMinecraft().fontRendererObj.getStringWidth(string):this.getStringWidth(string, 12, BadlionFontRenderer.FontType.TEXT);
   }

   public int getStringWidth(String string, BadlionFontRenderer.FontType type) {
      return this.mode.equals(CustomFontRenderer.RenderMode.DEFAULT)?Minecraft.getMinecraft().fontRendererObj.getStringWidth(string):Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(string, type);
   }

   public static enum RenderMode {
      DEFAULT,
      CUSTOM;
   }
}
