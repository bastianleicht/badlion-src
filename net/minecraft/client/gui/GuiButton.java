package net.minecraft.client.gui;

import net.badlion.client.Wrapper;
import net.badlion.client.gui.BadlionFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiButton extends Gui {
   protected static final ResourceLocation buttonTextures = new ResourceLocation("textures/gui/widgets.png");
   public static final ResourceLocation earthIcon = new ResourceLocation("textures/menu/home/earth-icon.svg_large.png");
   public static final ResourceLocation newButton = new ResourceLocation("textures/menu/home/regular-button.svg_large.png");
   public static final ResourceLocation newButtonHover = new ResourceLocation("textures/menu/home/hoverstate-button.svg_large.png");
   public static final ResourceLocation disconnectButton = new ResourceLocation("textures/menu/esc/disconnect-button.svg_large.png");
   public static final ResourceLocation backToGameButton = new ResourceLocation("textures/menu/esc/back-to-game-button.svg_large.png");
   public static final ResourceLocation singleplayerIcon = new ResourceLocation("textures/menu/home/singleplayer-icon.svg_large.png");
   public static final ResourceLocation multiplayerIcon = new ResourceLocation("textures/menu/home/multiplayer-icon.svg_large.png");
   protected int width;
   protected int height;
   public int xPosition;
   public int yPosition;
   public String displayString;
   public int id;
   public boolean enabled;
   public boolean visible;
   protected boolean hovered;
   private GuiButton.ButtonType buttonType;
   private ResourceLocation iconTexture;
   private int iconWidth;
   private int iconHeight;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$client$gui$GuiButton$ButtonType;

   public GuiButton(int p_i8_1_, int p_i8_2_, int p_i8_3_, int p_i8_4_, int p_i8_5_, String p_i8_6_, GuiButton.ButtonType p_i8_7_, ResourceLocation p_i8_8_, int p_i8_9_, int p_i8_10_) {
      this(p_i8_1_, p_i8_2_, p_i8_3_, p_i8_4_, p_i8_5_, p_i8_6_);
      this.buttonType = p_i8_7_;
      this.iconTexture = p_i8_8_;
      this.iconWidth = p_i8_9_;
      this.iconHeight = p_i8_10_;
   }

   public GuiButton(int p_i9_1_, int p_i9_2_, int p_i9_3_, int p_i9_4_, int p_i9_5_, String p_i9_6_, GuiButton.ButtonType p_i9_7_) {
      this(p_i9_1_, p_i9_2_, p_i9_3_, p_i9_4_, p_i9_5_, p_i9_6_);
      this.buttonType = p_i9_7_;
   }

   public GuiButton(int buttonId, int x, int y, String buttonText) {
      this(buttonId, x, y, 200, 20, buttonText);
   }

   public GuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
      this.buttonType = GuiButton.ButtonType.NORMAL;
      this.iconTexture = null;
      this.width = 200;
      this.height = 20;
      this.enabled = true;
      this.visible = true;
      this.id = buttonId;
      this.xPosition = x;
      this.yPosition = y;
      this.width = widthIn;
      this.height = heightIn;
      this.displayString = buttonText;
   }

   protected int getHoverState(boolean mouseOver) {
      int i = 1;
      if(!this.enabled) {
         i = 0;
      } else if(mouseOver) {
         i = 2;
      }

      return i;
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if(this.visible) {
         FontRenderer fontrenderer = mc.fontRendererObj;
         mc.getTextureManager().bindTexture(buttonTextures);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
         int i = this.getHoverState(this.hovered);
         if(this.hovered) {
            mc.getTextureManager().bindTexture(newButtonHover);
         } else {
            switch($SWITCH_TABLE$net$minecraft$client$gui$GuiButton$ButtonType()[this.buttonType.ordinal()]) {
            case 1:
               mc.getTextureManager().bindTexture(newButton);
               break;
            case 2:
               mc.getTextureManager().bindTexture(disconnectButton);
               break;
            case 3:
               mc.getTextureManager().bindTexture(backToGameButton);
            }
         }

         GL11.glEnable(3042);
         OpenGlHelper.glBlendFunc(770, 771, 1, 0);
         GL11.glBlendFunc(770, 771);
         int j = this.height * 5;
         Gui.drawModalRectWithCustomSizedTexture(this.xPosition, this.yPosition, 0.0F, 0.0F, 5, this.height, (float)j, (float)this.height);
         Gui.drawModalRectWithCustomSizedTexture(this.xPosition + 5, this.yPosition, 5.0F, 0.0F, this.width - 10, this.height, (float)this.width, (float)this.height);
         Gui.drawModalRectWithCustomSizedTexture(this.xPosition + this.width - 5, this.yPosition, (float)(j - 5), 0.0F, 5, this.height, (float)j, (float)this.height);
         GL11.glDisable(3042);
         this.mouseDragged(mc, mouseX, mouseY);
         int k = 14737632;
         if(!this.enabled) {
            k = 10526880;
         } else if(this.hovered) {
            k = 16777120;
         }

         int l = 14;
         int i1 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(this.displayString, l, BadlionFontRenderer.FontType.TITLE);
         int j1 = i1;
         int k1 = 0;
         if(this.iconTexture != null) {
            int l1 = 2;
            j1 = i1 + this.iconWidth + l1;
            k1 = this.iconWidth + l1;
            Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(this.iconTexture);
            Gui.drawModalRectWithCustomSizedTexture(this.xPosition + this.width / 2 - j1 / 2, this.yPosition + (this.height - 14) / 2 + 3, 0.0F, 0.0F, this.iconWidth, this.iconHeight, (float)this.iconWidth, (float)this.iconHeight);
         }

         Wrapper.getInstance().getBadlionFontRenderer().drawString(this.xPosition + this.width / 2 - j1 / 2 + k1, this.yPosition + (this.height - 14) / 2, this.displayString, l, BadlionFontRenderer.FontType.TITLE, true);
      }

   }

   protected void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
   }

   public void mouseReleased(int mouseX, int mouseY) {
   }

   public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
      return this.enabled && this.visible && mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
   }

   public boolean isMouseOver() {
      return this.hovered;
   }

   public void drawButtonForegroundLayer(int mouseX, int mouseY) {
   }

   public void playPressSound(SoundHandler soundHandlerIn) {
      soundHandlerIn.playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
   }

   public int getButtonWidth() {
      return this.width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$client$gui$GuiButton$ButtonType() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$client$gui$GuiButton$ButtonType;
      if($SWITCH_TABLE$net$minecraft$client$gui$GuiButton$ButtonType != null) {
         return var10000;
      } else {
         int[] var0 = new int[GuiButton.ButtonType.values().length];

         try {
            var0[GuiButton.ButtonType.NORMAL.ordinal()] = 1;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[GuiButton.ButtonType.RED.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[GuiButton.ButtonType.THICK_LINES.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$minecraft$client$gui$GuiButton$ButtonType = var0;
         return var0;
      }
   }

   public static enum ButtonType {
      NORMAL,
      RED,
      THICK_LINES;
   }
}
