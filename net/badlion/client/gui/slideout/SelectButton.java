package net.badlion.client.gui.slideout;

import java.util.ArrayList;
import java.util.List;
import net.badlion.client.Wrapper;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.elements.ColorPicker;
import net.badlion.client.gui.slideout.elements.ToggleButton;
import net.badlion.client.gui.slideout.fontrenderer.CustomFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.lwjgl.opengl.GL11;

public class SelectButton extends ToggleButton {
   private ResourceLocation toolTipBox;
   private ResourceLocation background;
   private CustomFontRenderer fontRenderer;
   private MutableBoolean enabled;
   private boolean hovered;
   private double scale;
   private boolean toggleable;
   private transient int tempZLevel;
   private String[] toolTipText;
   private int toolTipTimer;
   private boolean icon;

   public SelectButton(String text, MutableBoolean enabled, double scale, boolean icon) {
      super(text);
      this.toolTipBox = new ResourceLocation("textures/slideout/betterframes/tool-tip-box.svg_large.png");
      this.background = new ResourceLocation("textures/slideout/cogwheel/toggle-box.svg_large.png");
      this.toggleable = true;
      this.enabled = enabled;
      this.scale = scale;
      this.icon = icon;
   }

   public SelectButton(String text, MutableBoolean enabled, double scale) {
      this(text, enabled, scale, true);
   }

   public void init() {
      this.fontRenderer = new CustomFontRenderer();
   }

   public void render() {
      int i = this.isEnabled()?-14963585:-2338240;
      int j = this.icon?this.getWidth() - 9:this.getWidth();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(this.background);
      Gui.drawScaledCustomSizeModalRect(this.getX(), this.getY(), 0.0F, 0.0F, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight(), (float)this.getWidth(), (float)this.getHeight());
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      double d0 = 0.65D;
      GL11.glScaled(d0, d0, 1.0D);
      this.fontRenderer.drawString(this.getText(), (int)((double)(this.getX() + 3) / d0), (int)((double)this.getY() / d0), i, 16);
      GL11.glScaled(1.0D / d0, 1.0D / d0, 1.0D);
      if(this.icon) {
         Minecraft.getMinecraft().getTextureManager().bindTexture(this.isEnabled()?ColorPicker.buttonOn:ColorPicker.buttonOff);
         GL11.glColor3f(1.0F, 1.0F, 1.0F);
         Gui.drawModalRectWithCustomSizedTexture(this.getX() + j, this.getY() + 2, 0.0F, 0.0F, 8, 8, 8.0F, 8.0F);
      }

      if(this.toolTipTimer > 42 && this.toolTipText != null) {
         if(this.tempZLevel == 0) {
            this.tempZLevel = this.zLevel;
            this.zLevel = 999;
         }

         int k = 550;
         SlideoutGUI.renderImage(this.toolTipBox, this.getX() + this.getWidth() + 2, this.getY(), k, 226, 0.2D);
         int l = 0;

         for(String s : this.toolTipText) {
            GL11.glScaled(d0, d0, d0);
            this.fontRenderer.drawString(s, (int)((double)this.getX() / d0 + (double)this.getWidth() / d0 + 3.0D + ((double)k / d0 * 0.2D / 2.0D - (double)(this.fontRenderer.getStringWidth(s) / 2))), (int)((double)this.getY() / d0) + l + 3, -1);
            GL11.glScaled(1.0D / d0, 1.0D / d0, 1.0D / d0);
            l += 12;
         }
      } else if(this.tempZLevel != 0) {
         this.zLevel = this.tempZLevel;
         this.tempZLevel = 0;
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void update(int mX, int mY) {
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
         if(i > this.getX() && i < this.getX() + this.getWidth() && j > this.getY() && j < this.getY() + this.getHeight() && this.isToggleable()) {
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
      return 12;
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
}
