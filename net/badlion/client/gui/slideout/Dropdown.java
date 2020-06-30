package net.badlion.client.gui.slideout;

import net.badlion.client.Wrapper;
import net.badlion.client.gui.slideout.RenderElement;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.fontrenderer.CustomFontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class Dropdown extends RenderElement {
   private ResourceLocation dropdown = new ResourceLocation("textures/slideout/cogwheel/dropdown-menu.svg_large.png");
   private ResourceLocation dropdownBackground = new ResourceLocation("textures/slideout/cogwheel/dropdown-bg.svg_large.png");
   private CustomFontRenderer fontRenderer;
   private boolean open;
   private int scrollOffset;
   private String value;
   private int valueIndex;
   private String[] list;
   private double scale;
   private int hoveredIndex;

   public Dropdown(String[] list, int defaultValueIndex, double scale) {
      this.list = list;
      this.scale = scale;
      if(list != null) {
         this.value = list[defaultValueIndex];
         this.valueIndex = defaultValueIndex;
      }

   }

   public void setList(String[] list) {
      this.list = list;
      if(list != null) {
         this.value = list[this.valueIndex];
      }

   }

   public void init() {
      this.fontRenderer = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getFontRenderer();
      super.init();
   }

   public void render() {
      double d0 = this.scale / 0.18D;
      int i = 6;
      int j = 480;
      int k = 50;
      int l = 58;
      if(this.open) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         SlideoutGUI.renderImage(this.dropdownBackground, this.getX() + 2, this.getY() + (int)(78.0D * this.scale), 480, l * this.list.length + 13, this.scale);
         if(this.list != null) {
            int i1 = this.getY() + this.getHeight();

            for(int j1 = 0; j1 < this.list.length; ++j1) {
               String s = this.list[j1];
               int k1 = (int)((double)j1 * (double)l * this.scale + (double)i1 + (double)(this.scrollOffset * (int)(40.0D * this.scale)));
               if(this.hoveredIndex == j1) {
                  Gui.drawRect(this.getX() + 6, k1 - 2, this.getX() - 2 + (int)((double)j * this.scale), k1 + (int)((double)k * this.scale), -7829368);
               }

               GL11.glScaled(d0, d0, d0);
               this.fontRenderer.drawString(s, (int)((double)(this.getX() + 9) / d0), (int)((double)(k1 - 4) / d0), -2631709);
               GL11.glScaled(1.0D / d0, 1.0D / d0, 1.0D / d0);
            }
         }
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      SlideoutGUI.renderImage(this.dropdown, this.getX(), this.getY(), 508, 108, this.scale);
      if(this.value != null) {
         GL11.glScaled(d0, d0, d0);
         this.fontRenderer.drawString(this.value.substring(0, Math.min(40, this.value.length())), (int)((double)(this.getX() + i) / d0), (int)(((double)this.getY() + 3.2D) / d0), -11053477);
         GL11.glScaled(1.0D / d0, 1.0D / d0, 1.0D / d0);
      }

   }

   public void update(int mX, int mY) {
      if(this.open) {
         int i = 480;
         int j = 98 * this.list.length;
         if(mX > this.getX() && (double)mX < (double)this.getX() + (double)i * this.scale && mY > this.getY() && (double)mY < (double)this.getY() + (double)j * this.scale) {
            for(int k = 0; k < this.list.length; ++k) {
               int l = this.getX() + 2;
               int i1 = 110;
               int j1 = 40;
               int k1 = 58;
               int l1 = (int)((double)k * (double)k1 * this.scale + (double)this.getY() + (double)((int)((double)i1 * this.scale)) + (double)(this.scrollOffset * (int)((double)j1 * this.scale)));
               if(mX > l && (double)mX < (double)l + (double)i * this.scale && mY > l1 && (double)mY < (double)l1 + (double)k1 * this.scale) {
                  this.hoveredIndex = k;
               }
            }
         }
      }

   }

   public boolean onClick(int mouseButton) {
      int i = Wrapper.getInstance().getMouseX();
      int j = Wrapper.getInstance().getMouseY();
      boolean flag = i > this.getX() && i < this.getX() + this.getWidth() && j > this.getY() && j < this.getY() + this.getHeight();
      boolean flag1 = false;
      if(this.open && !flag) {
         int k = 480;
         int l = 98 * this.list.length;
         if(i > this.getX() && (double)i < (double)this.getX() + (double)k * this.scale && j > this.getY() && (double)j < (double)this.getY() + (double)l * this.scale) {
            if(mouseButton == 0) {
               this.value = this.list[this.hoveredIndex];
               this.valueIndex = this.hoveredIndex;
            }

            flag1 = true;
         }
      }

      if(mouseButton == 0) {
         if(this.open && flag) {
            this.open = false;
         } else {
            this.open = flag;
         }

         if(flag) {
            flag1 = true;
         }

         if(!this.open) {
            for(int i1 = 0; i1 < this.list.length; ++i1) {
               if(this.value.equals(this.list[i1])) {
                  this.hoveredIndex = i1;
               }
            }
         }

         this.zLevel = this.open?0:-1;
      }

      if(this.scrollOffset > 0) {
         this.scrollOffset = 0;
      }

      if(this.scrollOffset < -(this.list.length - 2)) {
         this.scrollOffset = -(this.list.length - 2);
      }

      return flag1;
   }

   public int getWidth() {
      return (int)(508.0D * this.scale);
   }

   public int getHeight() {
      return (int)(108.0D * this.scale);
   }

   public void setOpen(boolean open) {
      this.open = open;
   }

   public boolean isOpen() {
      return this.open;
   }

   public String getValue() {
      return this.value;
   }

   public double getScale() {
      return this.scale;
   }

   public String[] getList() {
      return this.list;
   }

   public int getValueIndex() {
      return this.valueIndex;
   }
}
