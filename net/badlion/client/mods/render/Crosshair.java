package net.badlion.client.mods.render;

import java.util.Locale;
import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.events.event.RenderGame;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.Dropdown;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.Slider;
import net.badlion.client.gui.slideout.TextButton;
import net.badlion.client.gui.slideout.elements.ColorPicker;
import net.badlion.client.mods.render.RenderMod;
import net.badlion.client.mods.render.color.ModColor;
import net.badlion.client.mods.render.gui.BoxedCoord;
import net.badlion.client.util.DisplayUtil;
import net.badlion.client.util.ImageDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Color;

public class Crosshair extends RenderMod {
   private transient Slider widthSlider;
   private double widthS;
   private int width;
   private transient Slider heightSlider;
   private double heightS;
   private int height;
   private transient Slider gapSlider;
   private double gapS;
   private int gap;
   private transient Slider thicknessSlider;
   private double thicknessS;
   private int thickness;
   private MutableBoolean visibleHideGui = new MutableBoolean(true);
   private MutableBoolean visibleDebugScreen = new MutableBoolean(true);
   private MutableBoolean visibleSpectatorMode = new MutableBoolean(true);
   private MutableBoolean visibleThirdPerson = new MutableBoolean(true);
   private MutableBoolean highlightPlayer = new MutableBoolean(false);
   private MutableBoolean highlightHostile = new MutableBoolean(false);
   private MutableBoolean highlightPassive = new MutableBoolean(false);
   private MutableBoolean dynamicBow = new MutableBoolean(false);
   private MutableBoolean dynamicAttack = new MutableBoolean(false);
   private MutableBoolean outline = new MutableBoolean(false);
   private MutableBoolean dot = new MutableBoolean(false);
   private ModColor color = new ModColor(-15732736);
   private ModColor playerColor = new ModColor(-16777216);
   private ModColor hostileColor = new ModColor(-16777216);
   private ModColor passiveColor = new ModColor(-16777216);
   private ModColor outlineColor = new ModColor(-16777216);
   private ModColor dotColor = new ModColor(-16777216);
   private Crosshair.Type selected = Crosshair.Type.CROSS;
   private transient Dropdown typeDropdown;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$badlion$client$mods$render$Crosshair$Type;

   public void reset() {
      this.color = new ModColor(-16711936);
      this.playerColor = new ModColor(-16777216);
      this.hostileColor = new ModColor(-16777216);
      this.passiveColor = new ModColor(-16777216);
      this.outlineColor = new ModColor(-16777216);
      this.dotColor = new ModColor(-16777216);
      this.selected = Crosshair.Type.CROSS;
      this.visibleDebugScreen.setValue(true);
      this.visibleSpectatorMode.setValue(true);
      this.visibleThirdPerson.setValue(true);
      this.highlightPlayer.setValue(false);
      this.highlightHostile.setValue(false);
      this.highlightPassive.setValue(false);
      this.dynamicBow.setValue(false);
      this.dynamicAttack.setValue(false);
      this.outline.setValue(false);
      this.dot.setValue(false);
      this.width = 3;
      this.widthS = 0.075D;
      this.height = 3;
      this.heightS = 0.075D;
      this.gap = 2;
      this.gapS = 0.05D;
      this.thickness = 1;
      this.thicknessS = 0.1D;
      super.reset();
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(0, 3));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(0, 26));
      this.slideCogMenu.addElement(new Label("Settings", -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));
      this.slideCogMenu.addElement(this.typeDropdown = new Dropdown(new String[]{"Cross", "Circle", "Arrow", "Triangle", "Square", "None"}, this.selected.ordinal(), 0.19D));
      this.slideCogMenu.addElement(new ColorPicker("Crosshair Color", this.color, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Player Color", this.playerColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Hostile Color", this.hostileColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Passive Color", this.passiveColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Outline Color", this.outlineColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Dot Color", this.dotColor, 0.13D));
      this.slideCogMenu.addElement(this.widthSlider = new Slider("Width", 0.0D, 1.0D, this.widthS, 0.2D));
      this.widthSlider.setDisplayText(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40"});
      this.widthSlider.init();
      this.slideCogMenu.addElement(this.heightSlider = new Slider("Height", 0.0D, 1.0D, this.heightS, 0.2D));
      this.heightSlider.setDisplayText(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40"});
      this.heightSlider.init();
      this.slideCogMenu.addElement(this.gapSlider = new Slider("Gap", 0.0D, 1.0D, this.gapS, 0.2D));
      this.gapSlider.setDisplayText(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "40"});
      this.gapSlider.init();
      this.slideCogMenu.addElement(this.thicknessSlider = new Slider("Thickness", 0.0D, 1.0D, this.thicknessS, 0.2D));
      this.thicknessSlider.setDisplayText(new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"});
      this.thicknessSlider.init();
      this.slideCogMenu.addElement(new TextButton("Show in Third Person", this.visibleThirdPerson, 1.0D));
      this.slideCogMenu.addElement(new TextButton("Highlight Hostile Mobs", this.highlightHostile, 1.0D));
      this.slideCogMenu.addElement(new TextButton("Highlight Passive Mobs", this.highlightPassive, 1.0D));
      this.slideCogMenu.addElement(new TextButton("Highlight Players", this.highlightPlayer, 1.0D));
      this.slideCogMenu.addElement(new TextButton("Crosshair Outline", this.outline, 1.0D));
      this.slideCogMenu.addElement(new TextButton("Crosshair Dot", this.dot, 1.0D));
      this.slideCogMenu.addElement(new TextButton("Visible in Debug Screen", this.visibleDebugScreen, 1.0D));
      this.slideCogMenu.addElement(new TextButton("Visible in Spectator Mode", this.visibleSpectatorMode, 1.0D));
      this.slideCogMenu.addElement(new TextButton("Visible in Hide Gui", this.visibleHideGui, 1.0D));
      this.slideCogMenu.addElement(new TextButton("Dynamic Bow Animation", this.dynamicBow, 1.0D));
      this.slideCogMenu.addElement(new TextButton("Dynamic Attack Animation", this.dynamicAttack, 1.0D));
      this.color.init();
      this.playerColor.init();
      this.hostileColor.init();
      this.passiveColor.init();
      this.outlineColor.init();
      this.dotColor.init();
      super.createCogMenu();
   }

   public Crosshair() {
      super("Crosshair", DisplayUtil.getCenterX(), DisplayUtil.getCenterY(), 0, 0, false);
      this.iconDimension = new ImageDimension(76, 76);
      this.defaultTopLeftBox = new BoxedCoord(0, 0, 0.0D, 0.0D);
      this.defaultCenterBox = new BoxedCoord(0, 0, 0.0D, 0.0D);
      this.defaultBottomRightBox = new BoxedCoord(0, 0, 0.0D, 0.0D);
   }

   public void init() {
      if(this.selected == null) {
         this.selected = Crosshair.Type.CROSS;
      }

      this.registerEvent(EventType.RENDER_GAME);
      this.registerEvent(EventType.MOTION_UPDATE);
      this.disableGuiEditing();
      this.setFontOffset(0.017D);
      super.init();
   }

   public void onEvent(Event event) {
      if(event instanceof MotionUpdate && this.loadedCogMenu) {
         this.color.tickColor();
         this.playerColor.tickColor();
         this.hostileColor.tickColor();
         this.passiveColor.tickColor();
         this.outlineColor.tickColor();
         this.dotColor.tickColor();
         Crosshair.Type crosshair$type = this.selected;
         this.selected = Crosshair.Type.valueOf(this.typeDropdown.getValue().toUpperCase(Locale.US));
         if(!this.selected.equals(crosshair$type)) {
            Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().initPages();
         }

         this.widthS = this.widthSlider.getValue();
         this.width = Integer.parseInt(this.widthSlider.getCurrentDisplayText());
         this.heightS = this.heightSlider.getValue();
         this.height = Integer.parseInt(this.heightSlider.getCurrentDisplayText());
         this.gapS = this.gapSlider.getValue();
         this.gap = Integer.parseInt(this.gapSlider.getCurrentDisplayText());
         this.thicknessS = this.thicknessSlider.getValue();
         this.thickness = Integer.parseInt(this.thicknessSlider.getCurrentDisplayText());
      }

      if(event instanceof RenderGame && this.isEnabled()) {
         this.displayCrosshair();
      }

      super.onEvent(event);
   }

   public void displayCrosshair() {
      int[] aint = DisplayUtil.getScreenSize();
      int i = aint[0] / 2;
      int j = aint[1] / 2;
      if(!this.isEnabled() && !Minecraft.getMinecraft().gameSettings.thirdPersonView) {
         this.displayDefaultCrosshair(i, j);
      }

      if(this.isEnabled() && (Minecraft.getMinecraft().gameSettings.showDebugInfo > 0 && this.visibleThirdPerson.getValue().booleanValue() || Minecraft.getMinecraft().gameSettings.showDebugInfo <= 0 && (!Minecraft.getMinecraft().gameSettings.thirdPersonView || this.visibleHideGui.getValue().booleanValue()))) {
         if(Minecraft.getMinecraft().gameSettings.thirdPersonView) {
            double[] adouble = DisplayUtil.getScreenSizeDouble();
            GL11.glClear(256);
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0D, adouble[0], adouble[1], 0.0D, 1000.0D, 3000.0D);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
         }

         Color color = this.color.getColor();
         int k = this.gap;
         if(Minecraft.getMinecraft().objectMouseOver != null && Minecraft.getMinecraft().objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
            if(Minecraft.getMinecraft().objectMouseOver.entityHit instanceof EntityPlayer && this.highlightPlayer.getValue().booleanValue()) {
               color = this.playerColor.getColor();
            } else if(Minecraft.getMinecraft().objectMouseOver.entityHit instanceof EntityLiving) {
               if(this.highlightHostile.getValue().booleanValue() && hostileEntity(Minecraft.getMinecraft().objectMouseOver.entityHit)) {
                  color = this.hostileColor.getColor();
               } else if(this.highlightPassive.getValue().booleanValue()) {
                  color = this.passiveColor.getColor();
               }
            }
         }

         if(Minecraft.getMinecraft().thePlayer.getHeldItem() != null && this.dynamicBow.getValue().booleanValue() && Minecraft.getMinecraft().thePlayer.getHeldItem().getItem() == Items.bow && Minecraft.getMinecraft().thePlayer.getItemInUseCount() > 0) {
            ItemStack itemstack = Minecraft.getMinecraft().thePlayer.getHeldItem();
            float f1 = (float)(itemstack.getItem().getMaxItemUseDuration(itemstack) - Minecraft.getMinecraft().thePlayer.getItemInUseCount()) / 20.0F;
            if(f1 > 1.0F) {
               f1 = 1.0F;
            }

            k = this.gap + (int)((1.0F - f1) * (float)(this.gap + 5) * 2.0F);
         } else if(this.dynamicAttack.getValue().booleanValue()) {
            float f = Minecraft.getMinecraft().thePlayer.getSwingProgress(Minecraft.getMinecraft().getTimer().renderPartialTicks);
            if(f != 0.0F) {
               k = this.gap + (int)((1.0F - f) * (float)(this.gap + 5) * 2.0F);
            }
         }

         if(Wrapper.getInstance().getScaleFactor() < 2) {
            k *= 2;
         }

         if(!Minecraft.getMinecraft().gameSettings.showDebugProfilerChart || Minecraft.getMinecraft().gameSettings.showDebugProfilerChart && this.visibleDebugScreen.getValue().booleanValue()) {
            switch($SWITCH_TABLE$net$badlion$client$mods$render$Crosshair$Type()[this.selected.ordinal()]) {
            case 1:
               this.displayCrossCrosshair(i, j, k, color);
               break;
            case 2:
               this.displayCircleCrosshair(i, j, k, color);
               break;
            case 3:
               this.displayArrowCrosshair(i, j, k, color);
               break;
            case 4:
               this.displayTriangle(i, j, k, color);
               break;
            case 5:
               this.displaySquareCrosshair(i, j, k, color);
            }

            if(this.dot.getValue().booleanValue()) {
               DisplayUtil.displayFilledRectangle(i, j, i + 1, j + 1, this.dotColor.getColor());
            }
         }
      }

   }

   private void displayTriangle(int drawX, int drawY, int renderGap, Color color) {
      DisplayUtil.drawLines(new float[]{(float)drawX, (float)(drawY - renderGap) - (float)this.height / 2.0F, (float)(drawX - renderGap) - (float)this.width / 2.0F, (float)(drawY + renderGap) + (float)this.height / 2.0F, (float)(drawX - renderGap) - (float)this.width / 2.0F, (float)(drawY + renderGap) + (float)this.height / 2.0F, (float)(drawX + renderGap) + (float)this.width / 2.0F, (float)(drawY + renderGap) + (float)this.height / 2.0F, (float)(drawX + renderGap) + (float)this.width / 2.0F, (float)(drawY + renderGap) + (float)this.height / 2.0F, (float)drawX, (float)(drawY - renderGap) - (float)this.height / 2.0F}, 2.0F, color, true);
      if(this.outline.getValue().booleanValue()) {
         DisplayUtil.drawLines(new float[]{(float)drawX, (float)(drawY - renderGap) - (float)this.height / 2.0F - 2.0F, (float)(drawX - renderGap) - (float)this.width / 2.0F - 2.0F, (float)(drawY + renderGap) + (float)this.height / 2.0F + 1.0F, (float)(drawX - renderGap) - (float)this.width / 2.0F - 2.0F, (float)(drawY + renderGap) + (float)this.height / 2.0F + 1.0F, (float)(drawX + renderGap) + (float)this.width / 2.0F + 2.0F, (float)(drawY + renderGap) + (float)this.height / 2.0F + 1.0F, (float)(drawX + renderGap) + (float)this.width / 2.0F + 2.0F, (float)(drawY + renderGap) + (float)this.height / 2.0F + 1.0F, (float)drawX, (float)(drawY - renderGap) - (float)this.height / 2.0F - 2.0F}, 2.0F, this.outlineColor.getColor(), true);
         DisplayUtil.drawLines(new float[]{(float)drawX, (float)(drawY - renderGap) - (float)this.height / 2.0F + 2.0F, (float)(drawX - renderGap) - (float)this.width / 2.0F + 2.0F, (float)(drawY + renderGap) + (float)this.height / 2.0F - 1.0F, (float)(drawX - renderGap) - (float)this.width / 2.0F + 2.0F, (float)(drawY + renderGap) + (float)this.height / 2.0F - 1.0F, (float)(drawX + renderGap) + (float)this.width / 2.0F - 2.0F, (float)(drawY + renderGap) + (float)this.height / 2.0F - 1.0F, (float)(drawX + renderGap) + (float)this.width / 2.0F - 2.0F, (float)(drawY + renderGap) + (float)this.height / 2.0F - 1.0F, (float)drawX, (float)(drawY - renderGap) - (float)this.height / 2.0F + 2.0F}, 2.0F, this.outlineColor.getColor(), true);
      }

   }

   private void displayCrossCrosshair(int screenWidth, int screenHeight, int renderGap, Color color) {
      int i = this.thickness / 2;
      if(this.outline.getValue().booleanValue()) {
         DisplayUtil.displayFilledRectangle(screenWidth - i - 1, screenHeight - renderGap + 1, screenWidth - i, screenHeight - renderGap - this.height + 1, this.outlineColor.getColor());
         DisplayUtil.displayFilledRectangle(screenWidth + i + 1, screenHeight - renderGap + 1, screenWidth + i + 2, screenHeight - renderGap - this.height + 1, this.outlineColor.getColor());
         DisplayUtil.displayFilledRectangle(screenWidth - i - 1, screenHeight - renderGap + 2, screenWidth + i + 2, screenHeight - renderGap + 1, this.outlineColor.getColor());
         DisplayUtil.displayFilledRectangle(screenWidth - i - 1, screenHeight - renderGap - this.height, screenWidth + i + 2, screenHeight - renderGap - this.height + 1, this.outlineColor.getColor());
         DisplayUtil.displayFilledRectangle(screenWidth - i - 1, screenHeight + renderGap, screenWidth - i, screenHeight + renderGap + this.height + 1, this.outlineColor.getColor());
         DisplayUtil.displayFilledRectangle(screenWidth + i + 1, screenHeight + renderGap, screenWidth + i + 2, screenHeight + renderGap + this.height + 1, this.outlineColor.getColor());
         DisplayUtil.displayFilledRectangle(screenWidth - i - 1, screenHeight + renderGap - 1, screenWidth + i + 2, screenHeight + renderGap, this.outlineColor.getColor());
         DisplayUtil.displayFilledRectangle(screenWidth - i - 1, screenHeight + renderGap + this.height, screenWidth + i + 2, screenHeight + renderGap + this.height + 1, this.outlineColor.getColor());
         DisplayUtil.displayFilledRectangle(screenWidth + renderGap, screenHeight - i - 1, screenWidth + renderGap + this.width, screenHeight - i, this.outlineColor.getColor());
         DisplayUtil.displayFilledRectangle(screenWidth + renderGap, screenHeight + i + 1, screenWidth + renderGap + this.width, screenHeight + i + 2, this.outlineColor.getColor());
         DisplayUtil.displayFilledRectangle(screenWidth + renderGap - 1, screenHeight - i - 1, screenWidth + renderGap, screenHeight + i + 2, this.outlineColor.getColor());
         DisplayUtil.displayFilledRectangle(screenWidth + renderGap + this.width, screenHeight - i - 1, screenWidth + renderGap + this.width + 1, screenHeight + i + 2, this.outlineColor.getColor());
         DisplayUtil.displayFilledRectangle(screenWidth - renderGap + 1, screenHeight - i - 1, screenWidth - renderGap - this.width, screenHeight - i, this.outlineColor.getColor());
         DisplayUtil.displayFilledRectangle(screenWidth - renderGap + 1, screenHeight + i + 1, screenWidth - renderGap - this.width, screenHeight + i + 2, this.outlineColor.getColor());
         DisplayUtil.displayFilledRectangle(screenWidth - renderGap + 2, screenHeight - i - 1, screenWidth - renderGap + 1, screenHeight + i + 2, this.outlineColor.getColor());
         DisplayUtil.displayFilledRectangle(screenWidth - renderGap - this.width, screenHeight - i - 1, screenWidth - renderGap - this.width + 1, screenHeight + i + 2, this.outlineColor.getColor());
      }

      DisplayUtil.displayFilledRectangle(screenWidth - i, screenHeight - renderGap + 1, screenWidth + i + 1, screenHeight - renderGap - this.height + 1, color);
      DisplayUtil.displayFilledRectangle(screenWidth - i, screenHeight + renderGap, screenWidth + i + 1, screenHeight + renderGap + this.height, color);
      DisplayUtil.displayFilledRectangle(screenWidth - renderGap + 1, screenHeight - this.thickness / 2, screenWidth - renderGap - this.width + 1, screenHeight + i + 1, color);
      DisplayUtil.displayFilledRectangle(screenWidth + renderGap, screenHeight - this.thickness / 2, screenWidth + renderGap + this.width, screenHeight + i + 1, color);
      GL11.glLineWidth(2.0F);
   }

   private void displayCircleCrosshair(int drawX, int drawY, int renderGap, Color color) {
      int i = this.outline.getValue().booleanValue()?this.thickness + 4:this.thickness;
      if(i > 10) {
         i = 10;
      }

      DisplayUtil.drawCircle((float)drawX, (float)drawY, (float)renderGap, (float)i, color, true);
      if(this.outline.getValue().booleanValue()) {
         DisplayUtil.drawCircle((float)drawX, (float)drawY, (float)renderGap + (float)i / 2.0F - 1.5F, 2.0F, this.outlineColor.getColor(), true);
         DisplayUtil.drawCircle((float)drawX, (float)drawY, (float)renderGap - (float)i / 2.0F + 1.5F, 2.0F, this.outlineColor.getColor(), true);
      }

   }

   private void displaySquareCrosshair(int drawX, int drawY, int renderGap, Color color) {
      DisplayUtil.displayFilledRectangle(drawX - renderGap - this.thickness, drawY - renderGap - this.thickness, drawX + renderGap + this.thickness, drawY - renderGap, color);
      DisplayUtil.displayFilledRectangle(drawX - renderGap - this.thickness, drawY + renderGap, drawX + renderGap + this.thickness, drawY + renderGap + this.thickness, color);
      DisplayUtil.displayFilledRectangle(drawX - renderGap - this.thickness, drawY - renderGap, drawX - renderGap, drawY + renderGap, color);
      DisplayUtil.displayFilledRectangle(drawX + renderGap, drawY - renderGap, drawX + renderGap + this.thickness, drawY + renderGap, color);
      GL11.glLineWidth(2.0F);
      if(this.outline.getValue().booleanValue()) {
         DisplayUtil.displayRectangle(drawX - renderGap, drawY - renderGap, drawX + renderGap, drawY + renderGap, this.outlineColor.getColor());
         DisplayUtil.displayRectangle(drawX - renderGap - this.thickness, drawY - renderGap - this.thickness, drawX + renderGap + this.thickness, drawY + renderGap + this.thickness, this.outlineColor.getColor());
      }

   }

   private void displayDefaultCrosshair(int screenWidth, int screenHeight) {
      GL11.glEnable(3042);
      OpenGlHelper.glBlendFunc(775, 769, 1, 0);
      Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.icons);
      DisplayUtil.displayTexturedRectangle(screenWidth - 7, screenHeight - 7, 0, 0, 16, 16);
      GL11.glDisable(3042);
   }

   private void displayArrowCrosshair(int screenWidth, int screenHeight, int renderGap, Color color) {
      double d0 = (double)renderGap / ((double)this.gap + 1.0D * ((double)this.gap + 5.0D) * 2.0D);
      int i = (int)((double)this.width + d0 * (double)(this.width + 5) * 2.0D);
      int j = (int)((double)this.height + d0 * (double)(this.height + 5) * 2.0D);
      GL11.glPushMatrix();
      if(this.outline.getValue().booleanValue()) {
         GL11.glLineWidth((float)(this.thickness + 3));
         DisplayUtil.displayLine(screenWidth - i - 1, screenHeight + j + 1, screenWidth, screenHeight, this.outlineColor.getColor());
         DisplayUtil.displayLine(screenWidth, screenHeight, screenWidth + i + 1, screenHeight + j + 1, this.outlineColor.getColor());
      }

      GL11.glLineWidth((float)(this.thickness + 1));
      DisplayUtil.displayLine(screenWidth - i, screenHeight + j, screenWidth, screenHeight, color);
      DisplayUtil.displayLine(screenWidth, screenHeight, screenWidth + i, screenHeight + j, color);
      GL11.glLineWidth(2.0F);
      GL11.glPopMatrix();
   }

   public static boolean hostileEntity(Entity e) {
      return !(e instanceof EntityBat) && !(e instanceof EntityChicken) && !(e instanceof EntityCow) && !(e instanceof EntityHorse) && !(e instanceof EntityOcelot) && !(e instanceof EntityPig) && !(e instanceof EntitySheep) && !(e instanceof EntitySquid) && !(e instanceof EntityVillager) && !(e instanceof EntityWolf);
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$badlion$client$mods$render$Crosshair$Type() {
      int[] var10000 = $SWITCH_TABLE$net$badlion$client$mods$render$Crosshair$Type;
      if($SWITCH_TABLE$net$badlion$client$mods$render$Crosshair$Type != null) {
         return var10000;
      } else {
         int[] var0 = new int[Crosshair.Type.values().length];

         try {
            var0[Crosshair.Type.ARROW.ordinal()] = 3;
         } catch (NoSuchFieldError var6) {
            ;
         }

         try {
            var0[Crosshair.Type.CIRCLE.ordinal()] = 2;
         } catch (NoSuchFieldError var5) {
            ;
         }

         try {
            var0[Crosshair.Type.CROSS.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            var0[Crosshair.Type.NONE.ordinal()] = 6;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[Crosshair.Type.SQUARE.ordinal()] = 5;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[Crosshair.Type.TRIANGLE.ordinal()] = 4;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$badlion$client$mods$render$Crosshair$Type = var0;
         return var0;
      }
   }

   static enum Type {
      CROSS,
      CIRCLE,
      ARROW,
      TRIANGLE,
      SQUARE,
      NONE;
   }
}
