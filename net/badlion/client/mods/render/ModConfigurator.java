package net.badlion.client.mods.render;

import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.KeyPress;
import net.badlion.client.events.event.RenderGame;
import net.badlion.client.mods.Mod;
import net.badlion.client.mods.misc.SlideoutAccess;
import net.badlion.client.mods.render.RenderMod;
import net.badlion.client.mods.render.Scoreboard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ModConfigurator extends Mod {
   private boolean editingmode;
   public int mouseOffsetX;
   public int mouseOffsetY;
   private RenderMod drag;
   private int resizeId;
   private RenderMod resize;
   private int resizeStartX;
   private int resizeStartY;
   private long clickTimer;
   private int resizeOriginX;
   private int resizeOriginY;
   private transient double boxes = 32.0D;
   private int dragStartX;
   private int dragStartY;
   private transient boolean mouseDown = false;

   public ModConfigurator() {
      super("ModConfigurator");
      this.zIndex = 500;
   }

   public boolean isInEditingMode() {
      return this.editingmode;
   }

   public void setEditing(boolean editingmode) {
      this.editingmode = editingmode;
   }

   public boolean isMouseDragging() {
      return this.drag != null;
   }

   public boolean isResizing() {
      return this.resize != null;
   }

   public void setDrag(RenderMod drag) {
      this.drag = drag;
   }

   public void init() {
      this.registerEvent(EventType.RENDER_GAME);
      this.registerEvent(EventType.GUI_KEY_PRESS);
      this.registerEvent(EventType.KEY_PRESS);
      super.init();
   }

   public void onEvent(Event event) {
      if(event instanceof KeyPress && this.drag != null) {
         double d0 = 0.0D;
         double d1 = 0.0D;
         double d2 = 50.0D;
         if(((KeyPress)event).getKeyID() == 203) {
            d0 = -1.0D / d2;
         } else if(((KeyPress)event).getKeyID() == 205) {
            d0 = 1.0D / d2;
         } else if(((KeyPress)event).getKeyID() == 200) {
            d1 = -1.0D / d2;
         } else if(((KeyPress)event).getKeyID() == 208) {
            d1 = 1.0D / d2;
         }

         Wrapper.getInstance().setEditing(true, this.drag);
         int l = this.getMaxZIndex();
         if(this.drag.getZIndex() != l) {
            this.drag.setZIndex(l + 1);
         }

         this.drag.getCenterBox().setPosition(this.drag.getCenterBox().getxBox(), this.drag.getCenterBox().getyBox(), this.drag.getCenterBox().getxOffset() + d0, this.drag.getCenterBox().getyOffset() + d1);
      }

      if(event instanceof RenderGame) {
         if(Minecraft.getMinecraft().currentScreen == null) {
            this.editingmode = false;
         }

         if(!this.editingmode) {
            if(this.drag != null) {
               this.drag = null;
            }

            return;
         }

         int j3 = Mouse.getX();
         int i = this.gameInstance.displayHeight - Mouse.getY();
         if(!Mouse.isButtonDown(0)) {
            this.mouseDown = false;
         }

         if(!this.mouseDown && Mouse.isButtonDown(0) && this.drag != null) {
            int k3 = this.drag.getX();
            int j = this.drag.getY();
            int j4 = this.drag.getSizeX();
            int k = this.drag.getSizeY();
            boolean flag = false;
            if(j3 >= k3 && j3 <= k3 + j4 && i >= j && i <= j + k) {
               flag = true;
            }

            if(!flag) {
               this.drag = null;
            }
         }

         if(!this.mouseDown && Mouse.isButtonDown(0)) {
            this.mouseDown = true;
            GL11.glPushMatrix();
            GL11.glScaled(0.5D, 0.5D, 1.0D);
            if(this.isInEditingMode()) {
               int l3 = this.getMaxZIndex();
               this.clickTimer = System.currentTimeMillis();

               for(int i4 = l3; i4 >= 0; --i4) {
                  for(Mod mod : Wrapper.getInstance().getActiveModProfile().getAllMods()) {
                     if(!(mod instanceof ModConfigurator) && mod.getZIndex() == i4 && mod instanceof RenderMod) {
                        RenderMod rendermod = (RenderMod)mod;
                        if(rendermod.isEnabled()) {
                           int i1 = rendermod.getX();
                           int j1 = rendermod.getY();
                           int k1 = rendermod.getSizeX();
                           int l1 = rendermod.getSizeY();
                           if(j3 >= i1 && j3 <= i1 + k1 && i >= j1 && i <= j1 + l1 && this.isInEditingMode()) {
                              this.mouseOffsetX = -(j3 - i1);
                              this.mouseOffsetY = -(i - j1);
                              this.drag = rendermod;
                              this.dragStartX = Wrapper.getInstance().getMouseX();
                              this.dragStartY = Wrapper.getInstance().getMouseY();
                           }

                           double d3 = (double)rendermod.getX();
                           double d4 = (double)rendermod.getY();
                           double d5 = (double)rendermod.getSizeX();
                           double d7 = (double)rendermod.getSizeY();
                           int k2 = 8;
                           if((double)j3 >= d3 - (double)k2 && (double)j3 <= d3 + (double)k2 && (double)i >= d4 - (double)k2 && (double)i <= d4 + (double)k2) {
                              this.resizeId = 0;
                              this.resize = rendermod;
                              this.resizeStartX = rendermod.x;
                              this.resizeStartY = rendermod.y;
                              this.resizeOriginX = rendermod.getSizeX();
                              this.resizeOriginY = rendermod.getSizeY();
                              this.mouseOffsetX = j3;
                              this.mouseOffsetY = i;
                           }

                           if((double)j3 >= d3 + d5 - (double)k2 && (double)j3 <= d3 + d5 + (double)k2 && (double)i >= d4 - (double)k2 && (double)i <= d4 + (double)k2) {
                              this.resizeId = 1;
                              this.resize = rendermod;
                              this.resizeStartX = rendermod.x;
                              this.resizeStartY = rendermod.y;
                              this.resizeOriginX = rendermod.getSizeX();
                              this.resizeOriginY = rendermod.getSizeY();
                              this.mouseOffsetX = j3;
                              this.mouseOffsetY = i;
                           }

                           if((double)j3 >= d3 - (double)k2 && (double)j3 <= d3 + (double)k2 && (double)i >= d4 + d7 - (double)k2 && (double)i <= d4 + d7 + (double)k2) {
                              this.resizeId = 2;
                              this.resize = rendermod;
                              this.resizeStartX = rendermod.getX();
                              this.resizeStartY = rendermod.getY();
                              this.resizeOriginX = rendermod.getSizeX();
                              this.resizeOriginY = rendermod.getSizeY();
                              this.mouseOffsetX = j3;
                              this.mouseOffsetY = i;
                           }

                           if((double)j3 >= d3 + d5 - (double)k2 && (double)j3 <= d3 + d5 + (double)k2 && (double)i >= d4 + d7 - (double)k2 && (double)i <= d4 + d7 + (double)k2) {
                              this.resizeId = 3;
                              this.resize = rendermod;
                              this.resizeStartX = rendermod.getX();
                              this.resizeStartY = rendermod.getY();
                              this.resizeOriginX = rendermod.getSizeX();
                              this.resizeOriginY = rendermod.getSizeY();
                              this.mouseOffsetX = j3;
                              this.mouseOffsetY = i;
                           }
                        }
                     }
                  }
               }

               GL11.glPopMatrix();
            }
         }

         Minecraft minecraft = Minecraft.getMinecraft();
         double d9 = 2.0D / (double)Wrapper.getInstance().getScaleFactor();
         GL11.glScaled(d9, d9, 1.0D);
         if(this.isInEditingMode()) {
            GL11.glPushMatrix();
            GL11.glScaled(0.5D, 0.5D, 1.0D);
            GL11.glDisable(3553);
            GL11.glLineWidth(1.0F);
            GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
            double d10 = (double)this.gameInstance.displayWidth / this.boxes;
            double d11 = (double)this.gameInstance.displayHeight / this.boxes;
            GL11.glBegin(1);

            for(int k4 = 0; (double)k4 <= this.boxes; ++k4) {
               GL11.glVertex2f((float)((double)k4 * d10), 0.0F);
               GL11.glVertex2f((float)((double)k4 * d10), (float)this.gameInstance.displayHeight);
            }

            for(int l4 = 0; (double)l4 <= this.boxes; ++l4) {
               GL11.glVertex2f(0.0F, (float)((double)l4 * d11));
               GL11.glVertex2f((float)this.gameInstance.displayWidth, (float)((double)l4 * d11));
            }

            GL11.glEnd();
            GL11.glEnable(3553);
            GL11.glPopMatrix();
            Gui.drawRect(0, 0, this.gameInstance.displayWidth, this.gameInstance.displayHeight, -1728053248);
            if(!Mouse.isButtonDown(0)) {
               Wrapper.getInstance().setEditing(false, (RenderMod)null);
            }

            if(!Mouse.isButtonDown(0)) {
               this.resize = null;
            }

            if(this.isResizing()) {
               ScaledResolution scaledresolution = new ScaledResolution(Minecraft.getMinecraft());
               int j5 = j3 - this.mouseOffsetX;
               int l5 = i - this.mouseOffsetY;
               j5 = (int)((double)j5 / (2.0D / (double)Wrapper.getInstance().getScaleFactor()));
               l5 = (int)((double)l5 / (2.0D / (double)Wrapper.getInstance().getScaleFactor()));
               int i2 = -1;
               if(this.resize.x < 0 && this.resize.y < 0) {
                  i2 = 0;
               }

               if(this.resize.x >= 0 && this.resize.y < 0) {
                  i2 = 1;
               }

               if(this.resize.x < 0 && this.resize.y >= 0) {
                  i2 = 2;
               }

               if(this.resize.x >= 0 && this.resize.y >= 0) {
                  i2 = 3;
               }

               double d12 = (double)(this.resize.startDisplayX / (2 * scaledresolution.getScaleFactor()));
               double d13 = (double)(minecraft.displayWidth / (2 * scaledresolution.getScaleFactor()));
               double d14 = (double)(this.resize.startDisplayY / (2 * scaledresolution.getScaleFactor()));
               double d15 = (double)(minecraft.displayHeight / (2 * scaledresolution.getScaleFactor()));
               int l2 = (int)(d12 - d13);
               if(i2 == 1 || i2 == 3) {
                  l2 = -l2;
               }

               int i3 = (int)(d14 - d15);
               if(i2 == 2 || i2 == 3) {
                  i3 = -i3;
               }

               switch(this.resizeId) {
               case 0:
                  this.resize.setSize((double)(this.resizeOriginX - j5), (double)(this.resizeOriginY - l5));
                  this.resize.setPosition(minecraft.displayWidth / (2 * scaledresolution.getScaleFactor()) - (this.resizeStartX + j5) - l2, minecraft.displayHeight / (2 * scaledresolution.getScaleFactor()) - (this.resizeStartY + l5) - i3);
                  break;
               case 1:
                  this.resize.setSize((double)(this.resizeOriginX + j5), (double)(this.resizeOriginY - l5));
                  break;
               case 2:
                  this.resize.setSize((double)(this.resizeOriginX - j5), (double)(this.resizeOriginY + l5));
                  break;
               case 3:
                  this.resize.setSize((double)(this.resizeOriginX + j5), (double)(this.resizeOriginY + l5));
               }
            } else if(this.isMouseDragging() && Mouse.isButtonDown(0)) {
               Wrapper.getInstance().setEditing(true, this.drag);
               int i5 = this.getMaxZIndex();
               if(this.drag.getZIndex() != i5) {
                  this.drag.setZIndex(i5 + 1);
               }

               int mod = j3 + this.mouseOffsetX;
               int renderMod = i + this.mouseOffsetY;
               mod = Math.min(Math.max(mod, 0), this.gameInstance.displayWidth);
               renderMod = Math.min(Math.max(renderMod, 0), this.gameInstance.displayHeight);
               int j6 = mod + this.drag.getSizeX() / 2;
               int l6 = renderMod + this.drag.getSizeY() / 2;
               int j2 = this.getXBox(j6);
               int k7 = this.getYBox(l6);
               double d6 = (double)this.getBoxXOffset(j6, j2) / this.getBoxWidth();
               double d8 = (double)this.getBoxYOffset(l6, k7) / this.getBoxHeight();
               if(this.hasBegunDragging()) {
                  this.drag.getCenterBox().setPosition(j2, k7, d6, d8);
               }

               j2 = this.getXBox(mod);
               k7 = this.getYBox(renderMod);
               this.drag.getTopLeftBox().setPosition(j2, k7, (double)this.getBoxXOffset(mod, j2) / this.getBoxWidth(), (double)this.getBoxYOffset(renderMod, k7) / this.getBoxHeight());
               j2 = this.getXBox(mod + this.drag.getSizeX());
               k7 = this.getYBox(renderMod + this.drag.getSizeY());
               this.drag.getBottomRightBox().setPosition(j2, k7, (double)this.getBoxXOffset(mod + this.drag.getSizeX(), j2) / this.getBoxWidth(), (double)this.getBoxYOffset(renderMod + this.drag.getSizeY(), k7) / this.getBoxHeight());
            }

            GL11.glPushMatrix();
            GL11.glScaled(0.5D, 0.5D, 1.0D);

            for(Mod mod1 : Wrapper.getInstance().getActiveModProfile().getAllMods()) {
               if(!(mod1 instanceof SlideoutAccess) && !(mod1 instanceof Scoreboard) && mod1 instanceof RenderMod && mod1.isEnabled() && ((RenderMod)mod1).allowsGuiEditing()) {
                  RenderMod rendermod1 = (RenderMod)mod1;
                  int k6 = rendermod1.getX();
                  int i7 = rendermod1.getY();
                  int j7 = rendermod1.getSizeX();
                  int l7 = rendermod1.getSizeY();
                  int i8 = -1;
                  Gui.drawRect(k6, i7, k6 + j7, i7 + l7, 872415231);
                  if(rendermod1.equals(this.drag) || rendermod1.equals(this.resize)) {
                     i8 = -1988048;
                  }

                  Gui.drawRect(k6, i7 - 1, k6 + j7, i7, i8);
                  Gui.drawRect(k6 - 1, i7, k6, i7 + l7, i8);
                  Gui.drawRect(k6 + j7, i7, k6 + j7 + 1, i7 + l7, i8);
                  Gui.drawRect(k6, i7 + l7, k6 + j7, i7 + l7 + 1, i8);
                  int j8 = 8;
                  if(this.isMouseInsideBox(j3, i, k6 - j8, i7 - j8, k6 + j8, i7 + j8)) {
                     i8 = -1988048;
                  } else {
                     i8 = -1;
                  }

                  Gui.drawRect(k6 - j8, i7 - j8, k6 + j8, i7 + j8, i8);
                  if(this.isMouseInsideBox(j3, i, k6 + j7 - j8, i7 - j8, k6 + j7 + j8, i7 + j8)) {
                     i8 = -1988048;
                  } else {
                     i8 = -1;
                  }

                  Gui.drawRect(k6 + j7 - j8, i7 - j8, k6 + j7 + j8, i7 + j8, i8);
                  if(this.isMouseInsideBox(j3, i, k6 - j8, i7 + l7 - j8, k6 + j8, i7 + l7 + j8)) {
                     i8 = -1988048;
                  } else {
                     i8 = -1;
                  }

                  Gui.drawRect(k6 - j8, i7 + l7 - j8, k6 + j8, i7 + l7 + j8, i8);
                  if(this.isMouseInsideBox(j3, i, k6 + j7 - j8, i7 + l7 - j8, k6 + j7 + j8, i7 + l7 + j8)) {
                     i8 = -1988048;
                  } else {
                     i8 = -1;
                  }

                  Gui.drawRect(k6 + j7 - j8, i7 + l7 - j8, k6 + j7 + j8, i7 + l7 + j8, i8);
                  GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                  GL11.glScaled(1.0D / d9, 1.0D / d9, 1.0D);
                  GL11.glScaled(d9, d9, 1.0D);
               }
            }

            GL11.glPopMatrix();
         }

         GL11.glScaled(1.0D / d9, 1.0D / d9, 1.0D);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

   }

   private boolean isMouseInsideBox(int mx, int my, int x1, int y1, int x2, int y2) {
      if(x1 < x2) {
         int i = x1;
         x1 = x2;
         x2 = i;
      }

      if(y1 < y2) {
         int j = y1;
         y1 = y2;
         y2 = j;
      }

      return mx > x2 && mx < x1 && my > y2 && my < y1;
   }

   private int getMaxZIndex() {
      int i = 0;

      for(Mod mod : Wrapper.getInstance().getActiveModProfile().getAllMods()) {
         if(!(mod instanceof ModConfigurator) && mod.getZIndex() > i) {
            i = mod.getZIndex();
         }
      }

      return i;
   }

   public int getXBox(int posX) {
      double d0 = (double)this.gameInstance.displayWidth / this.boxes;
      return (int)Math.floor((double)posX / d0);
   }

   public int getYBox(int posY) {
      double d0 = (double)this.gameInstance.displayHeight / this.boxes;
      return (int)Math.floor((double)posY / d0);
   }

   public int getBoxXOffset(int posX, int xBox) {
      double d0 = (double)this.gameInstance.displayWidth / this.boxes;
      return (int)((double)posX - d0 * (double)xBox);
   }

   public int getBoxYOffset(int posY, int yBox) {
      double d0 = (double)this.gameInstance.displayHeight / this.boxes;
      return (int)((double)posY - d0 * (double)yBox);
   }

   public double getBoxWidth() {
      return (double)this.gameInstance.displayWidth / this.boxes;
   }

   public double getBoxHeight() {
      return (double)this.gameInstance.displayHeight / this.boxes;
   }

   public double getBoxWidth(int displayWidth) {
      return (double)displayWidth / this.boxes;
   }

   public double getBoxHeight(int displayHeight) {
      return (double)displayHeight / this.boxes;
   }

   public boolean hasBegunDragging() {
      return Wrapper.getInstance().getMouseX() != this.dragStartX || Wrapper.getInstance().getMouseY() != this.dragStartY;
   }
}
