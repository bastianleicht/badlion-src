package net.badlion.client.gui.slideout;

import net.badlion.client.Wrapper;
import net.badlion.client.gui.slideout.RenderElement;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.fontrenderer.CustomFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class Slider extends RenderElement {
   private ResourceLocation slider = new ResourceLocation("textures/slideout/cogwheel/slider-bar.svg_large.png");
   private ResourceLocation sliderHandle = new ResourceLocation("textures/slideout/cogwheel/slider-circle.svg_large.png");
   private CustomFontRenderer fontRenderer;
   private double min;
   private double max;
   private double value;
   private double scale;
   private String text;
   private String[] displayText;
   private boolean drag;

   public Slider(String text, double min, double max, double defaultValue, double scale) {
      this.min = min;
      this.max = max;
      this.value = defaultValue;
      this.scale = scale;
      this.text = text;
   }

   public void setDisplayText(String[] displayText) {
      this.displayText = displayText;
   }

   public String[] getDisplayText() {
      return this.displayText;
   }

   public String getText() {
      return this.text;
   }

   public double getValue() {
      return this.value;
   }

   public double getScale() {
      return this.scale;
   }

   public double getMin() {
      return this.min;
   }

   public double getMax() {
      return this.max;
   }

   public void init() {
      this.fontRenderer = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getFontRenderer();
      super.init();
   }

   public void render() {
      int i = 3;
      double d0 = 1.0D;
      GL11.glScaled(d0, d0, d0);
      this.fontRenderer.drawString(this.text, (int)((double)this.getX() / d0) - i + this.getWidth() / 2 - this.fontRenderer.getStringWidth(this.text) / 2, (int)((double)this.getY() / d0) - i, -1);
      if(this.displayText != null) {
         this.fontRenderer.drawString(this.getCurrentDisplayText(), (int)((double)this.getX() / d0) + this.fontRenderer.getStringWidth(this.text) + i + this.getWidth() / 2 - this.fontRenderer.getStringWidth(this.text) / 2, (int)((double)this.getY() / d0) - i, -1);
      } else {
         String s = String.valueOf((double)Math.round(this.getValue() * 100.0D) / 100.0D);
         this.fontRenderer.drawString(s, (int)((double)this.getX() / d0) + this.fontRenderer.getStringWidth(this.text) + i + this.getWidth() / 2 - this.fontRenderer.getStringWidth(this.text) / 2, (int)((double)this.getY() / d0) - i, -1);
      }

      GL11.glScaled(1.0D / d0, 1.0D / d0, 1.0D / d0);
      int l1 = 14;
      int j = 4;
      int k = 72;
      int l = 54;
      int i1 = 566;
      int j1 = 10;
      int k1 = 22;
      Gui.drawRect(this.getX(), this.getY() + l1, (int)((double)this.getX() + (double)i1 * this.scale), (int)((double)(this.getY() + j) + (double)k * this.scale), 2003199590);
      GL11.glColor4f(0.37F, 0.37F, 0.37F, 0.9F);
      SlideoutGUI.renderImage(this.sliderHandle, this.getX() + (int)(-((double)l * this.scale) + (double)i1 * this.scale * (this.value / this.max)), this.getY() + j1 - (int)((double)k1 * this.scale), 108, 108, this.scale);
   }

   public String getCurrentDisplayText() {
      return this.displayText[(int)Math.max(0.0D, Math.ceil(this.value / (1.0D / (double)this.displayText.length)) - 1.0D)];
   }

   public void update(int mx, int my) {
      if(this.drag) {
         this.value = MathHelper.clamp_double(((double)mx - (double)this.getX()) / ((double)this.getX() + (double)this.getWidth() - (double)this.getX()) * this.max, this.min + 0.01D, this.max - 0.01D);
         if(!Mouse.isButtonDown(0)) {
            this.drag = false;
         }
      }

   }

   public boolean onClick(int mouseButton) {
      boolean flag = false;
      if(mouseButton == 0) {
         Minecraft minecraft = Minecraft.getMinecraft();
         int i = Wrapper.getInstance().getMouseX();
         int j = Wrapper.getInstance().getMouseY();
         if(i > this.getX() + (int)(-(54.0D * this.scale) + 566.0D * this.scale * (this.value / this.max)) && (double)i < (double)(this.getX() + (int)(-(54.0D * this.scale) + 566.0D * this.scale * (this.value / this.max))) + 108.0D * this.scale && j > this.getY() + 10 - (int)(27.0D * this.scale) && (double)j < (double)(this.getY() + 10 - (int)(27.0D * this.scale)) + 108.0D * this.scale) {
            this.drag = true;
            flag = true;
         }

         if(i > this.getX() && i < this.getX() + this.getWidth() && j > this.getY() + 10 && j < this.getY() + 10 + this.getHeight()) {
            this.value = MathHelper.clamp_double((double)(i - this.getX()) / (566.0D * this.scale) * this.max, this.min, this.max);
            this.drag = true;
            flag = true;
         }
      }

      return flag;
   }

   public int getWidth() {
      return (int)(566.0D * this.scale);
   }

   public int getHeight() {
      return (int)(162.0D * this.scale);
   }

   public void setValue(double value) {
      this.value = value;
   }
}
