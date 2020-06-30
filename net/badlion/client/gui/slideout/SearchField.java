package net.badlion.client.gui.slideout;

import net.badlion.client.Wrapper;
import net.badlion.client.gui.slideout.RenderElement;
import net.badlion.client.gui.slideout.fontrenderer.CustomFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class SearchField extends RenderElement {
   public static final ResourceLocation searchBorder = new ResourceLocation("textures/slideout/mods/search-border.svg_large.png");
   private ResourceLocation searchIcon = new ResourceLocation("textures/slideout/mods/search-icon.svg_large.png");
   private String text;
   private int x;
   private int y;
   private double scale;
   private int results = -1;
   private boolean focused;
   private CustomFontRenderer fontRenderer;
   private int backspace;
   private int timer;

   public SearchField(String defaultText, int x, int y, double scale) {
      this.text = defaultText;
      this.x = x;
      this.y = y;
      this.scale = scale;
   }

   public void init() {
      this.fontRenderer = new CustomFontRenderer();
   }

   public void keyTyped(char character, int keyCode) {
      if(keyCode == 1) {
         this.focused = false;
      }

      if(keyCode == 14 && this.text.length() > 0) {
         this.text = this.text.substring(0, this.text.length() - 1);
      }

      String s = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789 ";
      if(s.contains(String.valueOf(character))) {
         this.text = this.text + character;
      }

   }

   public void render() {
      Minecraft minecraft = Minecraft.getMinecraft();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      minecraft.getTextureManager().bindTexture(searchBorder);
      int i = (int)(1100.0D * this.scale);
      int j = (int)(90.0D * this.scale);
      int k = (int)(54.0D * this.scale);
      int l = (int)(54.0D * this.scale);
      if(this.results == 0 && this.text != null && this.text.length() > 0) {
         GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
      }

      Gui.drawModalRectWithCustomSizedTexture(this.getX(), this.getY(), (float)i, (float)j, i, j, (float)i, (float)j);
      minecraft.getTextureManager().bindTexture(this.searchIcon);
      int i1 = 13;
      int j1 = 2;
      int k1 = 4;
      int l1 = 3;
      double d0 = 0.65D;
      Gui.drawModalRectWithCustomSizedTexture(this.getX() + k1, this.getY() + l1, (float)k, (float)l, k, l, (float)k, (float)l);
      String s = this.text;
      if(this.focused && this.timer >= 55) {
         s = s + "_";
      }

      if(this.text.length() < 1 && !this.focused) {
         s = "Search";
      }

      GL11.glScaled(d0, d0, d0);
      this.fontRenderer.drawString(s, (int)((double)(this.getX() + i1) / d0), (int)((double)(this.getY() + j1) / d0), -8355712);
      GL11.glScaled(1.0D / d0, 1.0D / d0, 1.0D / d0);
   }

   public int getWidth() {
      return (int)(1250.0D * this.scale);
   }

   public int getHeight() {
      return (int)(90.0D * this.scale);
   }

   public void update(int mouseX, int mouseY) {
      if(this.timer++ > 110) {
         this.timer = 0;
      }

      if(this.timer % 5 == 0) {
         if(Keyboard.isCreated() && Keyboard.isKeyDown(14)) {
            if(this.backspace++ > 7 && this.text.length() > 0) {
               this.text = this.text.substring(0, this.text.length() - 1);
            }
         } else {
            this.backspace = 0;
         }
      }

   }

   public boolean onClick(int mouseButton) {
      if(mouseButton == 0) {
         Minecraft minecraft = Minecraft.getMinecraft();
         int i = Wrapper.getInstance().getMouseX();
         int j = Wrapper.getInstance().getMouseY();
         this.focused = false;
         if(i > this.getX() && i < this.getX() + (this.getWidth() - 50) && j > this.getY() && j < this.getY() + this.getHeight()) {
            this.focused = true;
            return true;
         }
      }

      return false;
   }

   public double getScale() {
      return this.scale;
   }

   public String getText() {
      return this.text;
   }

   public void setText(String text) {
      this.text = text;
   }

   public void setResults(int results) {
      this.results = results;
   }
}
