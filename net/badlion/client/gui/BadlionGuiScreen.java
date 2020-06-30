package net.badlion.client.gui;

import net.badlion.client.Wrapper;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.mods.render.gui.elements.Button;
import net.badlion.client.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

public class BadlionGuiScreen extends GuiScreen {
   public void onPanelButtonClick(Button button) {
   }

   public static void drawRoundedRect(int x0, int y0, int x1, int y1, float radius, int color, float zLevel) {
      int i = 18;
      float f = 90.0F / (float)i;
      float f1 = (float)(color >> 24 & 255) / 255.0F;
      float f2 = (float)(color >> 16 & 255) / 255.0F;
      float f3 = (float)(color >> 8 & 255) / 255.0F;
      float f4 = (float)(color & 255) / 255.0F;
      GL11.glDisable(2884);
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      GL11.glColor4f(f2, f3, f4, f1);
      GL11.glBegin(5);
      GL11.glVertex3f((float)x0 + radius, (float)y0, zLevel);
      GL11.glVertex3f((float)x0 + radius, (float)y1, zLevel);
      GL11.glVertex3f((float)x1 - radius, (float)y0, zLevel);
      GL11.glVertex3f((float)x1 - radius, (float)y1, zLevel);
      GL11.glEnd();
      GL11.glBegin(5);
      GL11.glVertex3f((float)x0, (float)y0 + radius, zLevel);
      GL11.glVertex3f((float)x0 + radius, (float)y0 + radius, zLevel);
      GL11.glVertex3f((float)x0, (float)y1 - radius, zLevel);
      GL11.glVertex3f((float)x0 + radius, (float)y1 - radius, zLevel);
      GL11.glEnd();
      GL11.glBegin(5);
      GL11.glVertex3f((float)x1, (float)y0 + radius, zLevel);
      GL11.glVertex3f((float)x1 - radius, (float)y0 + radius, zLevel);
      GL11.glVertex3f((float)x1, (float)y1 - radius, zLevel);
      GL11.glVertex3f((float)x1 - radius, (float)y1 - radius, zLevel);
      GL11.glEnd();
      GL11.glBegin(6);
      float f5 = (float)x1 - radius;
      float f6 = (float)y0 + radius;
      GL11.glVertex3f(f5, f6, zLevel);

      for(int j = 0; j <= i; ++j) {
         float f7 = (float)j * f;
         GL11.glVertex3f((float)((double)f5 + (double)radius * Math.cos(Math.toRadians((double)f7))), (float)((double)f6 - (double)radius * Math.sin(Math.toRadians((double)f7))), zLevel);
      }

      GL11.glEnd();
      GL11.glBegin(6);
      f5 = (float)x0 + radius;
      f6 = (float)y0 + radius;
      GL11.glVertex3f(f5, f6, zLevel);

      for(int k = 0; k <= i; ++k) {
         float f8 = (float)k * f;
         GL11.glVertex3f((float)((double)f5 - (double)radius * Math.cos(Math.toRadians((double)f8))), (float)((double)f6 - (double)radius * Math.sin(Math.toRadians((double)f8))), zLevel);
      }

      GL11.glEnd();
      GL11.glBegin(6);
      f5 = (float)x0 + radius;
      f6 = (float)y1 - radius;
      GL11.glVertex3f(f5, f6, zLevel);

      for(int l = 0; l <= i; ++l) {
         float f9 = (float)l * f;
         GL11.glVertex3f((float)((double)f5 - (double)radius * Math.cos(Math.toRadians((double)f9))), (float)((double)f6 + (double)radius * Math.sin(Math.toRadians((double)f9))), zLevel);
      }

      GL11.glEnd();
      GL11.glBegin(6);
      f5 = (float)x1 - radius;
      f6 = (float)y1 - radius;
      GL11.glVertex3f(f5, f6, zLevel);

      for(int i1 = 0; i1 <= i; ++i1) {
         float f10 = (float)i1 * f;
         GL11.glVertex3f((float)((double)f5 + (double)radius * Math.cos(Math.toRadians((double)f10))), (float)((double)f6 + (double)radius * Math.sin(Math.toRadians((double)f10))), zLevel);
      }

      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glEnable(2884);
      GL11.glDisable(3042);
   }

   public static void drawRoundedRect(int x0, int y0, int x1, int y1, float radius, int color) {
      int i = 18;
      float f = 90.0F / (float)i;
      float f1 = (float)(color >> 24 & 255) / 255.0F;
      float f2 = (float)(color >> 16 & 255) / 255.0F;
      float f3 = (float)(color >> 8 & 255) / 255.0F;
      float f4 = (float)(color & 255) / 255.0F;
      GL11.glDisable(2884);
      GL11.glDisable(3553);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      GL11.glColor4f(f2, f3, f4, f1);
      GL11.glBegin(5);
      GL11.glVertex2f((float)x0 + radius, (float)y0);
      GL11.glVertex2f((float)x0 + radius, (float)y1);
      GL11.glVertex2f((float)x1 - radius, (float)y0);
      GL11.glVertex2f((float)x1 - radius, (float)y1);
      GL11.glEnd();
      GL11.glBegin(5);
      GL11.glVertex2f((float)x0, (float)y0 + radius);
      GL11.glVertex2f((float)x0 + radius, (float)y0 + radius);
      GL11.glVertex2f((float)x0, (float)y1 - radius);
      GL11.glVertex2f((float)x0 + radius, (float)y1 - radius);
      GL11.glEnd();
      GL11.glBegin(5);
      GL11.glVertex2f((float)x1, (float)y0 + radius);
      GL11.glVertex2f((float)x1 - radius, (float)y0 + radius);
      GL11.glVertex2f((float)x1, (float)y1 - radius);
      GL11.glVertex2f((float)x1 - radius, (float)y1 - radius);
      GL11.glEnd();
      GL11.glBegin(6);
      float f5 = (float)x1 - radius;
      float f6 = (float)y0 + radius;
      GL11.glVertex2f(f5, f6);

      for(int j = 0; j <= i; ++j) {
         float f7 = (float)j * f;
         GL11.glVertex2f((float)((double)f5 + (double)radius * Math.cos(Math.toRadians((double)f7))), (float)((double)f6 - (double)radius * Math.sin(Math.toRadians((double)f7))));
      }

      GL11.glEnd();
      GL11.glBegin(6);
      f5 = (float)x0 + radius;
      f6 = (float)y0 + radius;
      GL11.glVertex2f(f5, f6);

      for(int k = 0; k <= i; ++k) {
         float f8 = (float)k * f;
         GL11.glVertex2f((float)((double)f5 - (double)radius * Math.cos(Math.toRadians((double)f8))), (float)((double)f6 - (double)radius * Math.sin(Math.toRadians((double)f8))));
      }

      GL11.glEnd();
      GL11.glBegin(6);
      f5 = (float)x0 + radius;
      f6 = (float)y1 - radius;
      GL11.glVertex2f(f5, f6);

      for(int l = 0; l <= i; ++l) {
         float f9 = (float)l * f;
         GL11.glVertex2f((float)((double)f5 - (double)radius * Math.cos(Math.toRadians((double)f9))), (float)((double)f6 + (double)radius * Math.sin(Math.toRadians((double)f9))));
      }

      GL11.glEnd();
      GL11.glBegin(6);
      f5 = (float)x1 - radius;
      f6 = (float)y1 - radius;
      GL11.glVertex2f(f5, f6);

      for(int i1 = 0; i1 <= i; ++i1) {
         float f10 = (float)i1 * f;
         GL11.glVertex2f((float)((double)f5 + (double)radius * Math.cos(Math.toRadians((double)f10))), (float)((double)f6 + (double)radius * Math.sin(Math.toRadians((double)f10))));
      }

      GL11.glEnd();
      GL11.glEnable(3553);
      GL11.glEnable(2884);
      GL11.glDisable(3042);
   }

   public static void drawOutlinedBox(int x, int y, int width, int height) {
      drawOutlinedBox(x, y, width, height, 0.75F);
   }

   public static void drawOutlinedBox(int x, int y, int width, int height, float opacity) {
      if(opacity >= 0.0F && opacity <= 1.0F) {
         x = x + width;
         GL11.glDisable(3553);
         GL11.glColor4f(0.0F, 0.0F, 0.0F, opacity);
         GL11.glBegin(7);
         GL11.glVertex2f((float)(x - width), (float)y);
         GL11.glVertex2f((float)(x - width), (float)(y + height));
         GL11.glVertex2f((float)x, (float)(y + height));
         GL11.glVertex2f((float)x, (float)y);
         GL11.glEnd();
         GL11.glDisable(3042);
         GL11.glColor4f(0.65F, 0.65F, 0.65F, 0.75F);
         GL11.glBegin(7);
         GL11.glVertex2f((float)(x - width), (float)y);
         GL11.glVertex2f((float)(x - width - 1), (float)y);
         GL11.glVertex2f((float)(x - width - 1), (float)(y + height));
         GL11.glVertex2f((float)(x - width), (float)(y + height));
         GL11.glEnd();
         GL11.glBegin(7);
         GL11.glVertex2f((float)(x + 1), (float)y);
         GL11.glVertex2f((float)x, (float)y);
         GL11.glVertex2f((float)x, (float)(y + height));
         GL11.glVertex2f((float)(x + 1), (float)(y + height));
         GL11.glEnd();
         GL11.glBegin(7);
         GL11.glVertex2f((float)(x - width), (float)(y + height));
         GL11.glVertex2f((float)(x - width), (float)(y + height + 1));
         GL11.glVertex2f((float)x, (float)(y + height + 1));
         GL11.glVertex2f((float)x, (float)(y + height));
         GL11.glEnd();
         GL11.glBegin(7);
         GL11.glVertex2f((float)(x - width), (float)(y - 1));
         GL11.glVertex2f((float)(x - width), (float)y);
         GL11.glVertex2f((float)x, (float)y);
         GL11.glVertex2f((float)x, (float)(y - 1));
         GL11.glEnd();
         GL11.glBegin(4);
         GL11.glVertex2f((float)(x - width - 1), (float)y);
         GL11.glVertex2f((float)(x - width), (float)y);
         GL11.glVertex2f((float)(x - width), (float)(y - 1));
         GL11.glEnd();
         GL11.glBegin(4);
         GL11.glVertex2f((float)x, (float)(y - 1));
         GL11.glVertex2f((float)x, (float)y);
         GL11.glVertex2f((float)(x + 1), (float)y);
         GL11.glEnd();
         GL11.glBegin(4);
         GL11.glVertex2f((float)(x - width - 1), (float)(y + height));
         GL11.glVertex2f((float)(x - width), (float)(y + height + 1));
         GL11.glVertex2f((float)(x - width), (float)(y + height));
         GL11.glEnd();
         GL11.glBegin(4);
         GL11.glVertex2f((float)x, (float)(y + height));
         GL11.glVertex2f((float)x, (float)(y + height + 1));
         GL11.glVertex2f((float)(x + 1), (float)(y + height));
         GL11.glEnd();
      } else {
         throw new IllegalArgumentException("Opacity must be between 0.0 and 1.0 inclusive: " + opacity);
      }
   }

   public static void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height, int zLevel) {
      float f = 0.00390625F;
      float f1 = 0.00390625F;
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
      worldrenderer.pos((double)x, (double)(y + height), (double)zLevel).tex((double)((float)textureX * f), (double)((float)(textureY + height) * f1)).endVertex();
      worldrenderer.pos((double)(x + width), (double)(y + height), (double)zLevel).tex((double)((float)(textureX + width) * f), (double)((float)(textureY + height) * f1)).endVertex();
      worldrenderer.pos((double)(x + width), (double)y, (double)zLevel).tex((double)((float)(textureX + width) * f), (double)((float)textureY * f1)).endVertex();
      worldrenderer.pos((double)x, (double)y, (double)zLevel).tex((double)((float)textureX * f), (double)((float)textureY * f1)).endVertex();
      tessellator.draw();
   }

   public static void drawButton(int x, int y, int sizeX, int sizeY, boolean hovered, String name, int fontSize) {
      drawButton(x, y, sizeX, sizeY, hovered, name, fontSize, -1);
   }

   public static void drawButton(int x, int y, int sizeX, int sizeY, boolean hovered, String name, int fontSize, int textColor) {
      int i = sizeY * 5;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      if(hovered) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(GuiButton.newButtonHover);
      } else {
         Minecraft.getMinecraft().getTextureManager().bindTexture(GuiButton.newButton);
      }

      GL11.glEnable(3042);
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      GL11.glBlendFunc(770, 771);
      Gui.drawModalRectWithCustomSizedTexture(x, y, 0.0F, 0.0F, 5, sizeY, (float)i, (float)sizeY);
      Gui.drawScaledCustomSizeModalRect(x + 5, y, 5.0F, 0.0F, i - 5, sizeY, sizeX - 10, sizeY, (float)i, (float)sizeY);
      Gui.drawModalRectWithCustomSizedTexture(x + sizeX - 5, y, (float)(i - 5), 0.0F, 5, sizeY, (float)i, (float)sizeY);
      GL11.glDisable(3042);
      ColorUtil.bindHexColorRGBA(textColor);
      int j = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(name, fontSize, BadlionFontRenderer.FontType.TITLE);
      Wrapper.getInstance().getBadlionFontRenderer().drawString(x + sizeX / 2 - j / 2, y + Math.abs(sizeY - fontSize) / 2, name, fontSize, BadlionFontRenderer.FontType.TITLE, true);
   }
}
