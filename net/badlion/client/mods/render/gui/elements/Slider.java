package net.badlion.client.mods.render.gui.elements;

import net.badlion.client.mods.render.gui.SizeableComponent;
import net.badlion.client.mods.render.gui.elements.Panel;
import net.minecraft.client.gui.GuiIngame;
import org.lwjgl.input.Mouse;

public class Slider extends SizeableComponent {
   private double minValue;
   private double maxValue;
   private double defaultValue;
   private int backgroundColor;
   private int sliderColor;
   private double value;

   public Slider(String name, int sizeX, int sizeY, double minValue, double maxValue, double defaultValue, int backgroundColor, int sliderColor) {
      super(name, sizeX, sizeY);
      this.minValue = minValue;
      this.maxValue = maxValue;
      this.defaultValue = defaultValue;
      if(this.defaultValue > this.maxValue) {
         this.maxValue = this.defaultValue;
      }

      this.value = this.defaultValue;
      this.backgroundColor = backgroundColor;
      this.sliderColor = sliderColor;
   }

   public void setMinValue(int minValue) {
      if(this.minValue < this.maxValue) {
         this.minValue = (double)minValue;
         if(this.minValue > this.value) {
            this.value = this.minValue;
         }
      } else {
         System.out.println("WARNING! Minimum value cannot exceed maximum value!");
      }

   }

   public void setMaxValue(int maxValue) {
      if((double)maxValue > this.minValue) {
         this.maxValue = (double)maxValue;
         if(this.value > this.maxValue) {
            this.value = this.maxValue;
         }
      } else {
         System.out.println("WARNING! Maximum value cannot be lower than minimum value!");
      }

   }

   public double getMinValue() {
      return this.minValue;
   }

   public double getMaxValue() {
      return this.maxValue;
   }

   public double getDefaultValue() {
      return this.defaultValue;
   }

   public double getValue() {
      return this.value;
   }

   public void update(Panel parent, int mouseX, int mouseY, boolean dragging) {
      if(this.value > this.maxValue) {
         this.value = this.maxValue;
      }

      if(this.value < this.minValue) {
         this.value = this.minValue;
      }

      double d0 = (double)this.getSizeX() * (this.value / this.maxValue);
      if(!dragging && Mouse.isButtonDown(0) && mouseX > this.x && mouseX < this.x + this.getSizeX() && mouseY > this.y && mouseY < this.y + this.getSizeY()) {
         double d1 = (double)(mouseX - this.x) / (double)this.getSizeX();
         this.value = this.maxValue * d1;
      }

   }

   public void render(GuiIngame gameRenderer, int x, int y) {
      if(this.isVisible()) {
         GuiIngame.drawRect(x, y, x + this.sizeX, y + this.sizeY, this.backgroundColor);
         double d0 = (double)this.getSizeX() * (this.value / this.maxValue);
         GuiIngame.drawRect((int)((double)x + d0 - 2.0D), y, (int)((double)x + d0), y + this.getSizeY(), this.sliderColor);
      }

   }
}
