package net.badlion.client.mods.render;

import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.mods.Mod;
import net.badlion.client.mods.render.gui.BoxedCoord;
import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class RenderMod extends Mod {
   public transient int x;
   public transient int y;
   protected double sizeX;
   protected double sizeY;
   private BoxedCoord centerBox;
   private BoxedCoord topLeftBox;
   private BoxedCoord bottomRightBox;
   private transient boolean setup;
   protected transient int defaultX;
   protected transient int defaultY;
   protected transient int defaultSizeX;
   protected transient int defaultSizeY;
   private transient int lastScaleFactor;
   public int startDisplayX;
   public int startDisplayY;
   private transient int lastDisplayWidth;
   private transient int lastDisplayHeight;
   private transient boolean guiEditing;
   protected transient BoxedCoord defaultTopLeftBox;
   protected transient BoxedCoord defaultCenterBox;
   protected transient BoxedCoord defaultBottomRightBox;
   protected transient int forceX;
   protected transient int forceY;

   public RenderMod(String name, int x, int y, int sizeX, int sizeY, boolean enableDefault) {
      super(name, enableDefault);
      this.sizeX = 1.0D;
      this.centerBox = null;
      this.topLeftBox = null;
      this.bottomRightBox = null;
      this.setup = false;
      this.guiEditing = true;
      this.forceX = -1;
      this.forceY = -1;
      this.startDisplayX = Minecraft.getMinecraft().displayWidth;
      this.startDisplayY = Minecraft.getMinecraft().displayHeight;
      this.x = x;
      this.y = y;
      this.sizeX = (double)sizeX;
      this.sizeY = (double)sizeY;
      this.defaultX = x;
      this.defaultY = y;
      this.defaultSizeX = sizeX;
      this.defaultSizeY = sizeY;
   }

   public RenderMod(String name, int x, int y, int sizeX, int sizeY) {
      this(name, x, y, sizeX, sizeY, true);
   }

   public boolean allowsGuiEditing() {
      return this.guiEditing;
   }

   public void disableGuiEditing() {
      this.guiEditing = false;
   }

   public void init() {
      super.init();
      if(this.centerBox == null || this.topLeftBox == null && this.bottomRightBox == null) {
         this.centerBox = this.defaultCenterBox.clone();
         this.topLeftBox = this.defaultTopLeftBox.clone();
         this.bottomRightBox = this.defaultBottomRightBox.clone();
      }

   }

   public double getScaleX() {
      return Math.max(0.1D, this.sizeX / (double)this.defaultSizeX);
   }

   public double getScaleY() {
      return Math.max(0.1D, this.sizeY / (double)this.defaultSizeY);
   }

   public int getSaveX() {
      return this.x;
   }

   public int getSaveY() {
      return this.y;
   }

   public int getX() {
      if(this.forceX != -1) {
         return this.forceX;
      } else {
         int i = this.centerBox.toXPos();
         return (int)Math.max(0.0D, Math.min((double)i - this.sizeX / 2.0D, (double)this.gameInstance.displayWidth - this.sizeX));
      }
   }

   public int getY() {
      if(this.forceY != -1) {
         return this.forceY;
      } else {
         int i = this.centerBox.toYPos();
         return (int)Math.max(0.0D, Math.min((double)i - this.sizeY / 2.0D, (double)this.gameInstance.displayHeight - this.sizeY));
      }
   }

   public void setPosition(int x, int y) {
   }

   public void setForcedRenderPos(int fX, int fY) {
      this.forceX = fX;
      this.forceY = fY;
   }

   public void setSize(double sizeX, double sizeY) {
      sizeX = Math.max(6.0D, sizeX);
      sizeY = Math.max(6.0D, sizeY);
      sizeX = MathHelper.clamp_double(sizeX, 1.0D, (double)(this.defaultSizeX * 24));
      sizeY = MathHelper.clamp_double(sizeY, 1.0D, (double)(this.defaultSizeY * 24));
      double d0 = (double)this.defaultSizeX / (double)this.defaultSizeY;
      double var10000 = sizeX / sizeY;
      this.sizeY = (double)((int)Math.round(sizeX / d0));
      this.sizeX = sizeX;
   }

   public void reset() {
      this.centerBox = this.defaultCenterBox.clone();
      this.topLeftBox = this.defaultTopLeftBox.clone();
      this.bottomRightBox = this.defaultBottomRightBox.clone();
      this.sizeX = (double)(this.bottomRightBox.toXPos() - this.topLeftBox.toXPos());
      this.sizeY = (double)(this.bottomRightBox.toYPos() - this.topLeftBox.toYPos());
      super.reset();
   }

   public void onEvent(Event event) {
      Minecraft minecraft = Minecraft.getMinecraft();
      if(!this.setup) {
         this.lastDisplayHeight = minecraft.displayHeight;
         this.lastDisplayWidth = minecraft.displayWidth;
         this.sizeX = (double)(this.bottomRightBox.toXPos() - this.topLeftBox.toXPos());
         this.sizeY = (double)(this.bottomRightBox.toYPos() - this.topLeftBox.toYPos());
         this.setup = true;
      } else if(minecraft.displayHeight != this.lastDisplayHeight || minecraft.displayWidth != this.lastDisplayWidth) {
         double d0 = Wrapper.getInstance().getActiveModProfile().getModConfigurator().getBoxWidth(minecraft.displayWidth);
         int i = this.topLeftBox.toXPos(d0);
         double d1 = (double)(this.bottomRightBox.toXPos(d0) - i);
         this.setSize(d1, this.sizeY);
         this.lastDisplayHeight = minecraft.displayHeight;
         this.lastDisplayWidth = minecraft.displayWidth;
      }

      if(Wrapper.getInstance().getScaleFactor() != this.lastScaleFactor) {
         this.lastScaleFactor = Wrapper.getInstance().getScaleFactor();
      }

      super.onEvent(event);
   }

   public int getDefaultX() {
      return this.defaultX;
   }

   public int getDefaultY() {
      return this.defaultY;
   }

   public void setPosition2(int defaultX, int defaultY) {
      this.x = defaultX;
      this.y = defaultY;
   }

   public int getSizeX() {
      return (int)this.sizeX;
   }

   public int getSizeY() {
      return (int)this.sizeY;
   }

   public int getSaveSizeX() {
      return (int)this.sizeX;
   }

   public int getSaveSizeY() {
      return (int)this.sizeY;
   }

   public int getDefaultSizeX() {
      return this.defaultSizeX;
   }

   public int getDefaultSizeY() {
      return this.defaultSizeY;
   }

   public void setDefaultSize(int sizeX, int sizeY) {
      this.defaultSizeX = sizeX;
      this.defaultSizeY = sizeY;
   }

   public void beginRender() {
      GL11.glPushMatrix();
      GL11.glScaled(2.0D / (double)Wrapper.getInstance().getScaleFactor(), 2.0D / (double)Wrapper.getInstance().getScaleFactor(), 1.0D);
      GL11.glScaled(0.5D, 0.5D, 1.0D);
      GL11.glScaled(this.getScaleX(), this.getScaleY(), 1.0D);
      GL11.glTranslatef((float)((double)this.getX() / this.getScaleX()), (float)((double)this.getY() / this.getScaleY()), 0.0F);
   }

   public void endRender() {
      GL11.glPopMatrix();
   }

   public BoxedCoord getCenterBox() {
      return this.centerBox;
   }

   public void setCenterBox(BoxedCoord centerBox) {
      this.centerBox = centerBox;
   }

   public BoxedCoord getTopLeftBox() {
      return this.topLeftBox;
   }

   public void setTopLeftBox(BoxedCoord topLeftBox) {
      this.topLeftBox = topLeftBox;
   }

   public BoxedCoord getBottomRightBox() {
      return this.bottomRightBox;
   }

   public void setBottomRightBox(BoxedCoord bottomRightBox) {
      this.bottomRightBox = bottomRightBox;
   }
}
