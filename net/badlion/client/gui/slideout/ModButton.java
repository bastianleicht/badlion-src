package net.badlion.client.gui.slideout;

import net.badlion.client.Wrapper;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.GuiScreenEditing;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.elements.ToggleButton;
import net.badlion.client.gui.slideout.fontrenderer.CustomFontRenderer;
import net.badlion.client.mods.Mod;
import net.badlion.client.mods.render.ModConfigurator;
import net.badlion.client.mods.render.RenderMod;
import net.badlion.client.util.ImageDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ModButton extends ToggleButton {
   private ResourceLocation favoriteResource = new ResourceLocation("textures/slideout/cogwheel/favorite.png");
   private ResourceLocation nonFavoriteResource = new ResourceLocation("textures/slideout/cogwheel/favorite-outline.png");
   private ResourceLocation offResource = new ResourceLocation("textures/slideout/mods/off-button-red-bg.svg_large.png");
   private ResourceLocation onResource = new ResourceLocation("textures/slideout/mods/on-button-green-bg.svg_large.png");
   private ResourceLocation offButtonResource = new ResourceLocation("textures/slideout/mods/off-button.svg_large.png");
   private ResourceLocation onButtonResource = new ResourceLocation("textures/slideout/mods/on-button.svg_large.png");
   private ResourceLocation cogWheel = new ResourceLocation("textures/slideout/mods/cog.svg_large.png");
   private ResourceLocation back = new ResourceLocation("textures/slideout/mods/toggle-box.svg_large.png");
   private ResourceLocation image;
   private CustomFontRenderer fontRenderer;
   private boolean hoverSetting;
   private int switchOffset = 2;
   private Mod mod;
   private double scale;
   private int width;
   private int height;
   private boolean drag;
   private boolean blob;
   private int dragXOffset;
   private int dragYOffset;
   private final int cogMarginX = 3;
   private final int cogMarginY = 3;
   private final int cogPadding = 1;
   private final int cogWidth = 10;
   private final int cogHeight = 10;
   private final int favoriteMarginX = 40;
   private final int favoriteMarginY = 30;
   private final int favoriteWidth = 7;
   private final int favoriteHeight = 7;

   public ModButton(Mod mod, double scale) {
      super(mod.getName());
      this.mod = mod;
      this.scale = scale;
   }

   public void init() {
      ImageDimension imagedimension = this.mod.getIconDimension();
      this.image = new ResourceLocation("textures/slideout/mods/icons/" + this.mod.getName().replace(" ", "").toLowerCase() + ".svg_large.png");
      if(imagedimension != null) {
         this.width = imagedimension.getX();
         this.height = imagedimension.getY();
      }

      if(this.mod.isEnabled()) {
         this.switchOffset = 17;
      } else {
         this.switchOffset = 2;
      }

      this.fontRenderer = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getFontRenderer();
      super.init();
   }

   public void render() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      int i = this.mod.isEnabled()?-14963585:-2338240;
      double d0 = 0.175D;
      int j = this.getWidth();
      if(this.blob) {
         Minecraft minecraft = Minecraft.getMinecraft();
         int k = Wrapper.getInstance().getMouseX();
         int l = Wrapper.getInstance().getMouseY();
         this.zLevel = 1;
         double d1 = this.mod.getFontOffset() + 0.62D;
         GL11.glScaled(d1, d1, 1.0D);
         this.fontRenderer.drawString(this.mod.getDisplayName(), (int)(((double)(k - this.dragXOffset + 4) + (double)(j / 2) * this.scale - (double)this.fontRenderer.getStringWidth(this.mod.getName(), 16, BadlionFontRenderer.FontType.TITLE) * d1 / 2.0D) / d1), (int)((double)(l - this.dragYOffset + 14) / d1), -6710887, 16);
         GL11.glScaled(1.0D / d1, 1.0D / d1, 1.0D);
         Gui.drawRect(k - this.dragXOffset + 7, l - this.dragYOffset, k - this.dragXOffset + (int)((double)(j + 1) * this.scale), l - this.dragYOffset + (int)(43.0D * this.scale), -2006555034);
      } else {
         this.zLevel = -1;
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(this.back);
         GL11.glEnable(3042);
         GL11.glBlendFunc(770, 771);
         Gui.drawModalRectWithCustomSizedTexture(this.getX(), this.getY(), 0.0F, 0.0F, this.getWidth(), this.getHeight(), (float)this.getWidth(), (float)this.getHeight());
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         int j1 = this.getX();
         int k1 = this.getY();
         int l1 = 18;
         SlideoutGUI.renderImage(this.image, this.mod.offsetX + (int)((double)j1 + (double)(j / 2) * this.scale - (double)this.getImageWidth() * d0 * 0.8D / 2.0D), (int)((double)(k1 + this.mod.offsetY) + (double)l1 * this.scale - (double)this.getImageHeight() * d0 * 0.8D / 2.0D) + 4, this.getImageWidth(), this.getImageHeight(), 0.2D);
         double d3 = this.mod.getFontOffset() + 0.62D;
         GL11.glScaled(d3, d3, 1.0D);
         this.fontRenderer.drawString(this.mod.getDisplayName(), (int)(((double)(this.getX() + 1) + (double)(j / 2) * this.scale - (double)this.fontRenderer.getStringWidth(this.mod.getName(), 16, BadlionFontRenderer.FontType.TITLE) * d3 / 2.0D) / d3), (int)((double)(this.getY() + 3) / d3), i, 16);
         GL11.glScaled(1.0D / d3, 1.0D / d3, 1.0D);
         int i1 = 10;
         if(this.mod.isForceDisabled()) {
            GL11.glColor4f(0.29F, 0.9F, 0.9F, 1.0F);
            SlideoutGUI.renderImage(this.offResource, this.getX() + 5, this.getY() + this.getHeight() - (5 + i1), 30, i1, 1.0D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            SlideoutGUI.renderImage(this.offButtonResource, this.getX() + 4 + this.switchOffset, this.getY() + this.getHeight() - (5 + i1 - 1), 13, 8, 1.0D);
         } else if(this.mod.isEnabled()) {
            GL11.glColor4f(0.7F, 0.7F, 0.7F, 1.0F);
            SlideoutGUI.renderImage(this.onResource, this.getX() + 5, this.getY() + this.getHeight() - (5 + i1), 30, i1, 1.0D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            SlideoutGUI.renderImage(this.onButtonResource, this.getX() + 4 + this.switchOffset, this.getY() + this.getHeight() - (5 + i1 - 1), 13, 8, 1.0D);
         } else {
            GL11.glColor4f(0.7F, 0.7F, 0.7F, 1.0F);
            SlideoutGUI.renderImage(this.offResource, this.getX() + 5, this.getY() + this.getHeight() - (5 + i1), 30, i1, 1.0D);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            SlideoutGUI.renderImage(this.offButtonResource, this.getX() + 4 + this.switchOffset, this.getY() + this.getHeight() - (5 + i1 - 1), 13, 8, 1.0D);
         }

         if(this.mod != null && this.mod.hasSlideCogMenu()) {
            if(this.hoverSetting) {
               GL11.glColor4f(0.5F, 1.0F, 1.0F, 1.0F);
            } else {
               GL11.glColor4f(0.3F, 1.0F, 1.0F, 1.0F);
            }

            Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(this.cogWheel);
            double d2 = 0.33D;
            GL11.glScaled(d2, d2, d2);
            Gui.drawModalRectWithCustomSizedTexture((int)((double)(this.getX() + 1 + 36) / d2), (int)((double)(this.getY() + 36) / d2), 0.0F, 0.0F, 27, 29, 27.0F, 29.0F);
            GL11.glScaled(1.0D / d2, 1.0D / d2, 1.0D / d2);
         }

         if(this.mod != null) {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(this.mod.isFavorite()?this.favoriteResource:this.nonFavoriteResource);
            double d4 = 0.33D;
            GL11.glScaled(d4, d4, d4);
            Gui.drawModalRectWithCustomSizedTexture((int)((double)(this.getX() + 1 + 4) / d4), (int)((double)(this.getY() + 14) / d4), 0.0F, 0.0F, 19, 20, 19.0F, 20.0F);
            GL11.glScaled(1.0D / d4, 1.0D / d4, 1.0D / d4);
         }
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   public void update(int mouseX, int mouseY) {
      this.hoverSetting = false;
      if(mouseX > this.getX() + (this.getWidth() - 3 - 10 - 2) && mouseX < this.getX() + this.getWidth() - 3 && mouseY > this.getY() + (this.getHeight() - 10 - 3 - 2) && mouseY < this.getY() + this.getHeight() - 3) {
         this.hoverSetting = true;
      }

      this.blob = false;
      if(Mouse.isButtonDown(0)) {
         if(this.mod instanceof RenderMod && this.drag && (mouseX <= this.getX() || (double)mouseX >= (double)this.getX() + (double)this.getWidth() * this.scale || mouseY <= this.getY() || (double)mouseY >= (double)this.getY() + (double)this.getHeight() * this.scale)) {
            this.blob = true;
         }

         if(mouseX > Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth() && this.blob) {
            this.mod.setEnabled(true);
            Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().toggle();
            ModConfigurator modconfigurator = Wrapper.getInstance().getActiveModProfile().getModConfigurator();
            if(modconfigurator != null) {
               modconfigurator.setEditing(!modconfigurator.isInEditingMode());
            }

            modconfigurator.setDrag((RenderMod)this.mod);
            modconfigurator.mouseOffsetX = 0;
            modconfigurator.mouseOffsetY = 0;
            Minecraft.getMinecraft().displayGuiScreen(new GuiScreenEditing((GuiScreen)null));
         }
      } else {
         if(this.drag && mouseX > this.getX() && (double)mouseX < (double)this.getX() + (double)this.getWidth() * this.scale && mouseY > this.getY() && (double)mouseY < (double)this.getY() + (double)this.getHeight() * this.scale) {
            this.mod.toggle();
         }

         this.drag = false;
      }

      if(this.mod.isEnabled()) {
         int i = 17;
         if(this.switchOffset < i) {
            ++this.switchOffset;
            this.switchOffset = (int)((double)this.switchOffset * 1.005D);
         }
      } else if(this.switchOffset > 2) {
         --this.switchOffset;
         this.switchOffset = (int)((double)this.switchOffset / 1.0001D);
         if(this.switchOffset < 2) {
            this.switchOffset = 2;
         }
      }

   }

   public boolean onClick(int mouseButton) {
      if(mouseButton == 0) {
         Minecraft minecraft = Minecraft.getMinecraft();
         int i = Wrapper.getInstance().getMouseX();
         int j = Wrapper.getInstance().getMouseY();
         if(this.getMod() != null && this.getMod().hasSlideCogMenu() && i > this.getX() + (this.getWidth() - 3 - 10 - 2) && i < this.getX() + this.getWidth() - 3 && j > this.getY() + (this.getHeight() - 10 - 3 - 2) && j < this.getY() + this.getHeight() - 3) {
            Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().setPage(this.mod.getName());
            return true;
         }

         if(this.getMod() != null && i > this.getX() + (this.getWidth() - 40 - 7) && i < this.getX() + this.getWidth() - 40 && j > this.getY() + (this.getHeight() - 7 - 30) && j < this.getY() + this.getHeight() - 30) {
            this.mod.setFavorite(!this.mod.isFavorite());
            Wrapper.getInstance().getActiveModProfile().sortMods();
            return true;
         }

         if(i > this.getX() && (double)i < (double)this.getX() + (double)this.getWidth() * this.scale && j > this.getY() && (double)j < (double)this.getY() + (double)this.getHeight() * this.scale) {
            this.drag = true;
            this.dragXOffset = i - this.getX();
            this.dragYOffset = j - this.getY();
            return true;
         }
      }

      return false;
   }

   public int getWidth() {
      return Math.max(51, (int)(51.0D * this.scale));
   }

   public int getHeight() {
      return (int)(51.0D * this.scale);
   }

   public int getImageWidth() {
      return this.width;
   }

   public int getImageHeight() {
      return this.height;
   }

   public Mod getMod() {
      return this.mod;
   }

   public double getScale() {
      return this.scale;
   }

   public boolean isSelected() {
      Minecraft minecraft = Minecraft.getMinecraft();
      int i = Wrapper.getInstance().getMouseX();
      int j = Wrapper.getInstance().getMouseY();
      return i > this.getX() && (double)i < (double)this.getX() + (double)this.getWidth() * this.scale && j > this.getY() && (double)j < (double)this.getY() + (double)this.getHeight() * this.scale;
   }
}
