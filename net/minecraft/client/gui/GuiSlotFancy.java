package net.minecraft.client.gui;

import net.badlion.client.Wrapper;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.fontrenderer.CustomFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class GuiSlotFancy {
   protected final Minecraft mc;
   protected int width;
   protected int height;
   protected int top;
   protected int bottom;
   protected int right;
   protected int left;
   protected final int slotHeight;
   private int scrollUpButtonID;
   private int scrollDownButtonID;
   protected int mouseX;
   protected int mouseY;
   protected boolean field_148163_i = true;
   protected int initialClickY = -2;
   protected float scrollMultiplier;
   protected float amountScrolled;
   protected int selectedElement = -1;
   protected long lastClicked;
   protected boolean field_178041_q = true;
   protected boolean showSelectionBox = true;
   protected boolean hasListHeader;
   private boolean clicked;
   private int itemPadding = 11;
   protected int headerPadding;
   private boolean enabled = true;
   private static final ResourceLocation badlionServerListHeader = new ResourceLocation("textures/menu/serverlist/server-list-header.svg_large.png");
   private static final ResourceLocation badlionServerScrollBar = new ResourceLocation("textures/menu/serverlist/vertical-scroller.svg_large.png");
   private static final ResourceLocation badlionBackButton = new ResourceLocation("textures/menu/serverlist/back-button.svg_large.png");
   private GuiScreen parent;
   private CustomFontRenderer fontRenderer;

   public GuiSlotFancy(Minecraft p_i2_1_, int p_i2_2_, int p_i2_3_, int p_i2_4_, int p_i2_5_, int p_i2_6_, GuiScreen p_i2_7_) {
      this.mc = p_i2_1_;
      this.width = p_i2_2_;
      this.height = p_i2_3_;
      this.top = p_i2_4_;
      this.bottom = p_i2_5_;
      this.slotHeight = p_i2_6_;
      this.left = 0;
      this.right = p_i2_2_;
      this.parent = p_i2_7_;
      this.fontRenderer = new CustomFontRenderer();
   }

   public void setDimensions(int p_setDimensions_1_, int p_setDimensions_2_, int p_setDimensions_3_, int p_setDimensions_4_) {
      this.width = p_setDimensions_1_;
      this.height = p_setDimensions_2_;
      this.top = p_setDimensions_3_;
      this.bottom = p_setDimensions_4_;
      this.left = 0;
      this.right = p_setDimensions_1_;
   }

   public void setShowSelectionBox(boolean p_setShowSelectionBox_1_) {
      this.showSelectionBox = p_setShowSelectionBox_1_;
   }

   protected void setHasListHeader(boolean p_setHasListHeader_1_, int p_setHasListHeader_2_) {
      this.hasListHeader = p_setHasListHeader_1_;
      this.headerPadding = p_setHasListHeader_2_;
      if(!p_setHasListHeader_1_) {
         this.headerPadding = 0;
      }

   }

   protected abstract int getSize0();

   protected abstract void elementClicked0(int var1, boolean var2, int var3, int var4);

   protected abstract boolean isSelected0(int var1);

   protected int getContentHeight() {
      return this.getSize0() * (this.slotHeight + this.headerPadding + this.itemPadding);
   }

   protected abstract void drawBackground0();

   protected void func_178040_a(int p_178040_1_, int p_178040_2_, int p_178040_3_) {
   }

   protected abstract void drawSlot0(int var1, int var2, int var3, int var4, int var5, int var6);

   protected void drawListHeader(int p_drawListHeader_1_, int p_drawListHeader_2_, Tessellator p_drawListHeader_3_) {
   }

   protected void func_148132_a(int p_148132_1_, int p_148132_2_) {
   }

   protected void func_148142_b(int p_148142_1_, int p_148142_2_) {
   }

   public int getSlotIndexFromScreenCoords(int p_getSlotIndexFromScreenCoords_1_, int p_getSlotIndexFromScreenCoords_2_) {
      int i = this.left + this.width / 2 - this.getListWidth0() / 2;
      int j = this.left + this.width / 2 + this.getListWidth0() / 2;
      int k = p_getSlotIndexFromScreenCoords_2_ - this.top - this.headerPadding + (int)this.amountScrolled - 4;
      int l = k / (this.slotHeight + this.itemPadding);
      return p_getSlotIndexFromScreenCoords_1_ < this.getScrollBarX0() && p_getSlotIndexFromScreenCoords_1_ >= i && p_getSlotIndexFromScreenCoords_1_ <= j && l >= 0 && k >= 0 && l < this.getSize0()?l:-1;
   }

   public void registerScrollButtons(int p_registerScrollButtons_1_, int p_registerScrollButtons_2_) {
      this.scrollUpButtonID = p_registerScrollButtons_1_;
      this.scrollDownButtonID = p_registerScrollButtons_2_;
   }

   protected void bindAmountScrolled() {
      this.amountScrolled = MathHelper.clamp_float(this.amountScrolled, 0.0F, (float)this.func_148135_f());
   }

   public int func_148135_f() {
      return Math.max(0, this.getContentHeight() - (this.bottom - this.top - 4));
   }

   public int getAmountScrolled() {
      return (int)this.amountScrolled;
   }

   public boolean isMouseYWithinSlotBounds(int p_isMouseYWithinSlotBounds_1_) {
      return p_isMouseYWithinSlotBounds_1_ >= this.top && p_isMouseYWithinSlotBounds_1_ <= this.bottom && this.mouseX >= this.left && this.mouseX <= this.right;
   }

   public void scrollBy(int p_scrollBy_1_) {
      this.amountScrolled += (float)p_scrollBy_1_;
      this.bindAmountScrolled();
      this.initialClickY = -2;
   }

   public void actionPerformed(GuiButton p_actionPerformed_1_) {
      if(p_actionPerformed_1_.enabled) {
         if(p_actionPerformed_1_.id == this.scrollUpButtonID) {
            this.amountScrolled -= (float)(this.slotHeight * 2 / 3);
            this.initialClickY = -2;
            this.bindAmountScrolled();
         } else if(p_actionPerformed_1_.id == this.scrollDownButtonID) {
            this.amountScrolled += (float)(this.slotHeight * 2 / 3);
            this.initialClickY = -2;
            this.bindAmountScrolled();
         }
      }

   }

   public void drawScreen(int p_drawScreen_1_, int p_drawScreen_2_, float p_drawScreen_3_) {
      if(this.field_178041_q) {
         this.mouseX = p_drawScreen_1_;
         this.mouseY = p_drawScreen_2_;
         int i = this.getScrollBarX0();
         int j = i + 6;
         this.bindAmountScrolled();
         GlStateManager.disableLighting();
         GlStateManager.disableFog();
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldrenderer = tessellator.getWorldRenderer();
         int k = this.width / 2 - this.getListWidth0() / 2 - 10;
         int l = this.width / 2 + this.getListWidth0() / 2 + 10;
         Gui.drawRect(k, 0, l, this.height, -685233615);
         int i1 = this.left + this.width / 2 - this.getListWidth0() / 2 + 2;
         int j1 = this.top + 4 - (int)this.amountScrolled;
         if(this.hasListHeader) {
            this.drawListHeader(i1, j1, tessellator);
         }

         this.drawSelectionBox(i1, j1, p_drawScreen_1_, p_drawScreen_2_);
         Gui.drawRect(k, this.top, l, this.top - 24, -12170409);
         boolean flag = false;
         if(this.mouseY > 40 && this.mouseY < 56 && this.mouseX > k + 4 && this.mouseX < k + 16 + 4) {
            flag = true;
            if(Mouse.isButtonDown(0)) {
               if(this.parent != null) {
                  this.mc.displayGuiScreen(this.parent);
               } else {
                  this.mc.displayGuiScreen(new GuiMainMenu());
               }

               this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            }
         }

         this.mc.getTextureManager().bindTexture(badlionBackButton);
         if(flag) {
            GL11.glColor4f(0.5F, 0.5F, 0.5F, 1.0F);
         } else {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         }

         Gui.drawModalRectWithCustomSizedTexture(k + 4, 40, 0.0F, 0.0F, 16, 16, 16.0F, 16.0F);
         int k1 = 36;
         GlStateManager.disableDepth();
         int l1 = 4;
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
         GlStateManager.disableAlpha();
         GlStateManager.shadeModel(7425);
         GlStateManager.disableTexture2D();
         worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldrenderer.pos((double)k, (double)(k1 + l1), 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 0).endVertex();
         worldrenderer.pos((double)l, (double)(k1 + l1), 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 0).endVertex();
         worldrenderer.pos((double)l, (double)k1, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         worldrenderer.pos((double)k, (double)k1, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         tessellator.draw();
         int i2 = this.func_148135_f();
         if(i2 > 0) {
            int j2 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
            j2 = MathHelper.clamp_int(j2, 32, this.bottom - this.top - 8);
            int k2 = (int)this.amountScrolled * (this.bottom - this.top - j2) / i2 + this.top;
            if(k2 < this.top) {
               k2 = this.top;
            }

            GL11.glEnable(3553);
            this.mc.getTextureManager().bindTexture(badlionServerScrollBar);
            GL11.glColor3d(1.0D, 1.0D, 1.0D);
            Gui.drawModalRectWithCustomSizedTexture(i + 2, k2, 0.0F, 0.0F, 3, j2, 3.0F, (float)j2);
            GL11.glDisable(3553);
         }

         this.func_148142_b(p_drawScreen_1_, p_drawScreen_2_);
         GlStateManager.enableTexture2D();
         GlStateManager.shadeModel(7424);
         GlStateManager.enableAlpha();
         GlStateManager.disableBlend();
         Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(badlionServerListHeader);
         GL11.glColor3d(1.0D, 1.0D, 1.0D);
         Gui.drawModalRectWithCustomSizedTexture(k, 0, 0.0F, 0.0F, l - k, 36, (float)(l - k), 36.0F);
         Gui.drawRect(k, this.bottom, l, this.height, -14144975);
         Gui.drawRect(k, this.bottom + 1, l, this.bottom, -683259049);
         GL11.glColor3d(1.0D, 1.0D, 1.0D);
         int l2 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth("SERVER LIST", 16, BadlionFontRenderer.FontType.HEADER);
         Wrapper.getInstance().getBadlionFontRenderer().drawString(l - l2 - 10, 10, "SERVER LIST", 16, BadlionFontRenderer.FontType.HEADER, true);
      }

   }

   public void drawRegionButton(int p_drawRegionButton_1_, int p_drawRegionButton_2_, int p_drawRegionButton_3_, int p_drawRegionButton_4_, boolean p_drawRegionButton_5_, String p_drawRegionButton_6_) {
      int i = p_drawRegionButton_4_ * 5;
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      if(p_drawRegionButton_5_) {
         this.mc.getTextureManager().bindTexture(GuiButton.newButtonHover);
      } else {
         this.mc.getTextureManager().bindTexture(GuiButton.backToGameButton);
      }

      GL11.glEnable(3042);
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      GL11.glBlendFunc(770, 771);
      Gui.drawModalRectWithCustomSizedTexture(p_drawRegionButton_1_, p_drawRegionButton_2_, 0.0F, 0.0F, 5, p_drawRegionButton_4_, (float)i, (float)p_drawRegionButton_4_);
      Gui.drawModalRectWithCustomSizedTexture(p_drawRegionButton_1_ + 5, p_drawRegionButton_2_, 5.0F, 0.0F, p_drawRegionButton_3_ - 10, p_drawRegionButton_4_, (float)i, (float)p_drawRegionButton_4_);
      Gui.drawModalRectWithCustomSizedTexture(p_drawRegionButton_1_ + p_drawRegionButton_3_ - 5, p_drawRegionButton_2_, (float)(i - 5), 0.0F, 5, p_drawRegionButton_4_, (float)i, (float)p_drawRegionButton_4_);
      GL11.glDisable(3042);
      this.fontRenderer.drawString(p_drawRegionButton_6_, p_drawRegionButton_1_ + 12, p_drawRegionButton_2_ - 1, -15274);
   }

   public void handleMouseInput() {
      if(this.isMouseYWithinSlotBounds(this.mouseY)) {
         if(Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.mouseY >= this.top && this.mouseY <= this.bottom) {
            int i = (this.width - this.getListWidth0()) / 2;
            int j = (this.width + this.getListWidth0()) / 2;
            int k = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
            int l = k / this.slotHeight;
            if(l < this.getSize0() && this.mouseX >= i && this.mouseX <= j && l >= 0 && k >= 0) {
               this.elementClicked0(l, false, this.mouseX, this.mouseY);
               this.selectedElement = l;
            } else if(this.mouseX >= i && this.mouseX <= j && k < 0) {
               this.func_148132_a(this.mouseX - i, this.mouseY - this.top + (int)this.amountScrolled - 4);
            }
         }

         if(Mouse.isButtonDown(0) && this.getEnabled()) {
            if(this.initialClickY != -1) {
               if(this.initialClickY >= 0) {
                  this.amountScrolled -= (float)(this.mouseY - this.initialClickY) * this.scrollMultiplier;
                  this.initialClickY = this.mouseY;
               }
            } else {
               boolean flag1 = true;
               if(this.mouseY >= this.top && this.mouseY <= this.bottom) {
                  int j2 = (this.width - this.getListWidth0()) / 2;
                  int k2 = (this.width + this.getListWidth0()) / 2;
                  int l2 = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
                  int i1 = l2 / this.slotHeight;
                  if(i1 < this.getSize0() && this.mouseX >= j2 && this.mouseX <= k2 && i1 >= 0 && l2 >= 0) {
                     boolean flag = i1 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
                     this.elementClicked0(i1, flag, this.mouseX, this.mouseY);
                     this.selectedElement = i1;
                     this.lastClicked = Minecraft.getSystemTime();
                  } else if(this.mouseX >= j2 && this.mouseX <= k2 && l2 < 0) {
                     this.func_148132_a(this.mouseX - j2, this.mouseY - this.top + (int)this.amountScrolled - 4);
                     flag1 = false;
                  }

                  int i3 = this.getScrollBarX0();
                  int j1 = i3 + 6;
                  if(this.mouseX >= i3 && this.mouseX <= j1) {
                     this.scrollMultiplier = -1.0F;
                     int k1 = this.func_148135_f();
                     if(k1 < 1) {
                        k1 = 1;
                     }

                     int l1 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getContentHeight());
                     l1 = MathHelper.clamp_int(l1, 32, this.bottom - this.top - 8);
                     this.scrollMultiplier /= (float)(this.bottom - this.top - l1) / (float)k1;
                  } else {
                     this.scrollMultiplier = 1.0F;
                  }

                  if(flag1) {
                     this.initialClickY = this.mouseY;
                  } else {
                     this.initialClickY = -2;
                  }
               } else {
                  this.initialClickY = -2;
               }
            }
         } else {
            this.initialClickY = -1;
         }

         int i2 = Mouse.getEventDWheel();
         if(i2 != 0) {
            if(i2 > 0) {
               i2 = -1;
            } else if(i2 < 0) {
               i2 = 1;
            }

            this.amountScrolled += (float)(i2 * this.slotHeight / 2);
         }
      }

   }

   public void setEnabled(boolean p_setEnabled_1_) {
      this.enabled = p_setEnabled_1_;
   }

   public boolean getEnabled() {
      return this.enabled;
   }

   public int getListWidth0() {
      return 220;
   }

   protected void drawSelectionBox(int p_drawSelectionBox_1_, int p_drawSelectionBox_2_, int p_drawSelectionBox_3_, int p_drawSelectionBox_4_) {
      int i = this.getSize0();
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();

      for(int j = 0; j < i; ++j) {
         int k = p_drawSelectionBox_2_ + j * (this.slotHeight + this.itemPadding) + this.headerPadding;
         int l = this.slotHeight - 4;
         if(k > this.bottom || k + l < this.top) {
            this.func_178040_a(j, p_drawSelectionBox_1_, k);
         }

         if(this.showSelectionBox && this.isSelected0(j)) {
            int i1 = this.left + (this.width / 2 - this.getListWidth0() / 2);
            int j1 = this.left + this.width / 2 + this.getListWidth0() / 2;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.disableTexture2D();
            worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            worldrenderer.pos((double)i1, (double)(k + l + 2), 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            worldrenderer.pos((double)j1, (double)(k + l + 2), 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
            worldrenderer.pos((double)j1, (double)(k - 2), 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            worldrenderer.pos((double)i1, (double)(k - 2), 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
            worldrenderer.pos((double)(i1 + 1), (double)(k + l + 1), 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos((double)(j1 - 1), (double)(k + l + 1), 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos((double)(j1 - 1), (double)(k - 1), 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            worldrenderer.pos((double)(i1 + 1), (double)(k - 1), 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
            tessellator.draw();
            GlStateManager.enableTexture2D();
         }

         this.drawSlot0(j, p_drawSelectionBox_1_, k, l, p_drawSelectionBox_3_, p_drawSelectionBox_4_);
         if(j == 0) {
            int l3 = p_drawSelectionBox_1_ + 76;
            int i4 = k - 1;
            int k1 = 36;
            int l1 = 10;
            int i2 = 5;
            int j2 = -12294537;
            int k2 = -12294537;
            int l2 = -12294537;
            int i3 = -12294537;
            Minecraft minecraft = this.mc;
            int j3 = Wrapper.getInstance().getMouseX();
            int k3 = Wrapper.getInstance().getMouseY();
            if(Mouse.isButtonDown(0)) {
               if(!this.clicked) {
                  this.clicked = true;
               }
            } else {
               this.clicked = false;
            }

            boolean flag = false;
            if(j3 > l3 && j3 < l3 + k1 && k3 > i4 && k3 < i4 + l1) {
               if(this.clicked) {
                  this.clicked = false;
                  this.connect("na.badlion.net", 25565);
               }

               flag = true;
            }

            this.drawRegionButton(l3, i4, k1, l1, flag, "NA");
            flag = false;
            l3 = l3 + k1 + i2;
            if(j3 > l3 && j3 < l3 + k1 && k3 > i4 && k3 < i4 + l1) {
               if(this.clicked) {
                  this.clicked = false;
                  this.connect("eu.badlion.net", 25565);
               }

               flag = true;
            }

            this.drawRegionButton(l3, i4, k1, l1, flag, "EU");
            flag = false;
            l3 = l3 + k1 + i2;
            if(j3 > l3 && j3 < l3 + k1 && k3 > i4 && k3 < i4 + l1) {
               if(this.clicked) {
                  this.clicked = false;
                  this.connect("sa.badlion.net", 25565);
               }

               flag = true;
            }

            this.drawRegionButton(l3, i4, k1, l1, flag, "SA");
            this.clicked = false;
         }
      }

   }

   public void connect(String p_connect_1_, int p_connect_2_) {
      if(this.mc.theWorld != null) {
         this.mc.theWorld.sendQuittingDisconnectingPacket();
         this.mc.loadWorld((WorldClient)null);
         this.mc.displayGuiScreen((GuiScreen)null);
      }

      this.mc.displayGuiScreen(new GuiConnecting(this.mc.currentScreen, this.mc, p_connect_1_, p_connect_2_));
   }

   protected int getScrollBarX0() {
      return this.width / 2 + 124;
   }

   protected void overlayBackground(int p_overlayBackground_1_, int p_overlayBackground_2_, int p_overlayBackground_3_, int p_overlayBackground_4_) {
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      float f = 32.0F;
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldrenderer.pos((double)this.left, (double)p_overlayBackground_2_, 0.0D).tex(0.0D, (double)((float)p_overlayBackground_2_ / 32.0F)).color(64, 64, 64, p_overlayBackground_4_).endVertex();
      worldrenderer.pos((double)(this.left + this.width), (double)p_overlayBackground_2_, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)p_overlayBackground_2_ / 32.0F)).color(64, 64, 64, p_overlayBackground_4_).endVertex();
      worldrenderer.pos((double)(this.left + this.width), (double)p_overlayBackground_1_, 0.0D).tex((double)((float)this.width / 32.0F), (double)((float)p_overlayBackground_1_ / 32.0F)).color(64, 64, 64, p_overlayBackground_3_).endVertex();
      worldrenderer.pos((double)this.left, (double)p_overlayBackground_1_, 0.0D).tex(0.0D, (double)((float)p_overlayBackground_1_ / 32.0F)).color(64, 64, 64, p_overlayBackground_3_).endVertex();
      tessellator.draw();
   }

   public void setSlotXBoundsFromLeft(int p_setSlotXBoundsFromLeft_1_) {
      this.left = p_setSlotXBoundsFromLeft_1_;
      this.right = p_setSlotXBoundsFromLeft_1_ + this.width;
   }

   public int getSlotHeight() {
      return this.slotHeight;
   }
}
