package net.badlion.client.gui.slideout;

import java.util.ArrayList;
import java.util.List;
import net.badlion.client.Wrapper;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.elements.ToggleButton;
import net.badlion.client.gui.slideout.fontrenderer.CustomFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.lwjgl.opengl.GL11;

public class TextButton extends ToggleButton {
   private ResourceLocation offResource = new ResourceLocation("textures/slideout/mods/off-button-red-bg.svg_large.png");
   private ResourceLocation onResource = new ResourceLocation("textures/slideout/mods/on-button-green-bg.svg_large.png");
   private ResourceLocation offButtonResource = new ResourceLocation("textures/slideout/mods/off-button.svg_large.png");
   private ResourceLocation onButtonResource = new ResourceLocation("textures/slideout/mods/on-button.svg_large.png");
   private ResourceLocation toolTipBox = new ResourceLocation("textures/slideout/betterframes/tool-tip-box.svg_large.png");
   private ResourceLocation background = new ResourceLocation("textures/slideout/cogwheel/toggle-box.svg_large.png");
   private CustomFontRenderer fontRenderer;
   private int switchOffset;
   private MutableBoolean enabled;
   private boolean hovered;
   private double scale;
   private boolean toggleable = true;
   private transient int tempZLevel;
   private String[] toolTipText;
   private int toolTipTimer;
   private boolean locked;
   private final int width = 75;

   public TextButton(String text, MutableBoolean enabled, double scale) {
      super(text);
      this.enabled = enabled;
      this.scale = scale;
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean locked) {
      this.locked = locked;
      this.setToggleable(!locked);
   }

   public void init() {
      this.fontRenderer = new CustomFontRenderer();
      if(!this.isLocked()) {
         if(this.isEnabled()) {
            this.switchOffset = 16;
         } else {
            this.switchOffset = 1;
         }
      }

   }

   public void render() {
      int i = this.isEnabled()?-14963585:-2338240;
      if(this.isLocked()) {
         i = -8947849;
      }

      int j = this.getWidth();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(this.background);
      Gui.drawScaledCustomSizeModalRect(this.getX(), this.getY(), 0.0F, 0.0F, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), (float)this.getWidth(), (float)this.getHeight());
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      double d0 = (double)(j - 5);
      double d1 = d0 * this.scale / (double)this.fontRenderer.getStringWidth(this.getText(), 16, BadlionFontRenderer.FontType.TITLE);
      if(d1 > 0.75D) {
         d1 = 0.75D;
      }

      GL11.glScaled(d1, d1, 1.0D);
      this.fontRenderer.drawString(this.getText(), (int)(((double)(this.getX() + 1) + (double)(j / 2) * this.scale - (double)this.fontRenderer.getStringWidth(this.getText(), 16, BadlionFontRenderer.FontType.TITLE) * d1 / 2.0D) / d1), (int)((double)(this.getY() + 3) / d1), i, 16);
      GL11.glScaled(1.0D / d1, 1.0D / d1, 1.0D);
      int k = 10;
      if(this.isEnabled() && !this.isLocked()) {
         GL11.glColor4f(0.7F, 0.7F, 0.7F, 1.0F);
         SlideoutGUI.renderImage(this.onResource, this.getX() + (j - 30) / 2, this.getY() + this.getHeight() - (5 + k), 30, k, 1.0D);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         SlideoutGUI.renderImage(this.onButtonResource, this.getX() + (j - 30) / 2 + this.switchOffset, this.getY() + this.getHeight() - (5 + k - 1), 13, 8, 1.0D);
      } else {
         if(this.isLocked()) {
            GL11.glColor4f(0.29F, 0.9F, 0.9F, 1.0F);
         } else {
            GL11.glColor4f(0.7F, 0.7F, 0.7F, 1.0F);
         }

         SlideoutGUI.renderImage(this.offResource, this.getX() + (j - 30) / 2, this.getY() + this.getHeight() - (5 + k), 30, k, 1.0D);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         SlideoutGUI.renderImage(this.offButtonResource, this.getX() + (j - 30) / 2 + this.switchOffset, this.getY() + this.getHeight() - (5 + k - 1), 13, 8, 1.0D);
      }

      if(this.toolTipTimer > 42 && this.toolTipText != null) {
         if(this.tempZLevel == 0) {
            this.tempZLevel = this.zLevel;
            this.zLevel = 999;
         }

         int l = 550;
         SlideoutGUI.renderImage(this.toolTipBox, this.getX() + this.getWidth() + 2, this.getY(), l, 226, 0.2D);
         d0 = (double)(l - 5);
         int i1 = 0;

         for(String s : this.toolTipText) {
            int j1 = this.fontRenderer.getStringWidth(s);
            if(j1 > i1) {
               i1 = j1;
            }
         }

         if(i1 == 0) {
            i1 = 1;
         }

         d1 = d0 * this.scale / (double)i1;
         if(d1 > 0.75D) {
            d1 = 0.75D;
         }

         int k1 = 0;

         for(String s1 : this.toolTipText) {
            GL11.glScaled(d1, d1, d1);
            this.fontRenderer.drawString(s1, (int)((double)this.getX() / d1 + (double)this.getWidth() / d1 + 3.0D + ((double)l / d1 * 0.2D / 2.0D - (double)(this.fontRenderer.getStringWidth(s1) / 2))), (int)((double)this.getY() / d1) + k1 + 3, -1);
            GL11.glScaled(1.0D / d1, 1.0D / d1, 1.0D / d1);
            k1 += 12;
         }
      } else if(this.tempZLevel != 0) {
         this.zLevel = this.tempZLevel;
         this.tempZLevel = 0;
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void update(int mX, int mY) {
      if(!this.isLocked()) {
         if(this.isEnabled()) {
            int i = 16;
            if(this.switchOffset < i) {
               ++this.switchOffset;
               this.switchOffset = (int)((double)this.switchOffset * 1.005D);
            }
         } else if(this.switchOffset >= 2) {
            --this.switchOffset;
            this.switchOffset = (int)((double)this.switchOffset / 1.0001D);
            if(this.switchOffset < 1) {
               this.switchOffset = 1;
            }
         }
      } else {
         int j = 9;
         if(this.switchOffset < j) {
            ++this.switchOffset;
            this.switchOffset = (int)((double)this.switchOffset * 1.005D);
         } else if(this.switchOffset > j) {
            this.switchOffset = j;
         }
      }

      if(mX > this.getX() && mX < this.getX() + this.getWidth() && mY > this.getY() && mY < this.getY() + this.getHeight()) {
         ++this.toolTipTimer;
      } else {
         this.toolTipTimer = 0;
      }

   }

   public boolean onClick(int mouseButton) {
      if(mouseButton == 0) {
         Minecraft minecraft = Minecraft.getMinecraft();
         int i = Wrapper.getInstance().getMouseX();
         int j = Wrapper.getInstance().getMouseY();
         if(i > this.getX() && (double)i < (double)this.getX() + (double)this.getWidth() * this.scale && j > this.getY() && (double)j < (double)this.getY() + (double)this.getHeight() * this.scale && this.isToggleable()) {
            this.enabled.setValue(!this.enabled.booleanValue());
            return true;
         }
      }

      return false;
   }

   public int getWidth() {
      return 75;
   }

   public int getHeight() {
      return 30;
   }

   public void setEnabled(boolean enabled) {
      this.enabled.setValue(enabled);
   }

   public boolean isEnabled() {
      return this.enabled.booleanValue();
   }

   public void setToolTipText(String tooltip) {
      List<String> list = new ArrayList();
      int i = 13;

      while(tooltip.length() > i) {
         boolean flag = false;

         for(int j = i; j < tooltip.length(); ++j) {
            if(tooltip.split("")[j].equals(" ")) {
               list.add(tooltip.substring(0, j));
               tooltip = tooltip.substring(j, tooltip.length());
               flag = true;
            }
         }

         if(!flag) {
            break;
         }
      }

      if(tooltip.length() > 0) {
         list.add(tooltip);
      }

      this.toolTipText = (String[])list.toArray(new String[0]);
   }

   public String[] getToolTipText() {
      return this.toolTipText;
   }

   public boolean isToggleable() {
      return this.toggleable;
   }

   public void setToggleable(boolean toggleable) {
      this.toggleable = toggleable;
   }

   public double getScale() {
      return this.scale;
   }
}
