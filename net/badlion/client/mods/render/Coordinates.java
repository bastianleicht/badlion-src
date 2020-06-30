package net.badlion.client.mods.render;

import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.GUIClickMouse;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.events.event.RenderGame;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.Dropdown;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.ModPreviewRenderer;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.TextButton;
import net.badlion.client.gui.slideout.elements.ColorPicker;
import net.badlion.client.gui.slideout.fontrenderer.CustomFontRenderer;
import net.badlion.client.mods.render.ModOrientation;
import net.badlion.client.mods.render.RenderMod;
import net.badlion.client.mods.render.ShowDirection;
import net.badlion.client.mods.render.color.ModColor;
import net.badlion.client.mods.render.gui.BoxedCoord;
import net.badlion.client.util.ColorUtil;
import net.badlion.client.util.ImageDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.lwjgl.opengl.GL11;

public class Coordinates extends RenderMod {
   private MutableBoolean biomeEnabled = new MutableBoolean(true);
   private MutableBoolean directionEnabled = new MutableBoolean(true);
   private transient TextButton biomeButton;
   private transient TextButton directionButton;
   private transient TextButton fancyFontButton;
   private transient TextButton roundButton;
   private ModColor biomeColor = new ModColor(-1);
   private ModColor directionColor = new ModColor(-1);
   private ModColor primaryColor = new ModColor(-47872);
   private ModColor secondaryColor = new ModColor(-1);
   private ModColor backgroundColor = new ModColor(-1289213133);
   private MutableBoolean useFancyFont = new MutableBoolean(false);
   private MutableBoolean roundLocation = new MutableBoolean(true);
   private transient CustomFontRenderer fontRenderer;
   private ModOrientation modOrientation = ModOrientation.VERTICAL;
   private MutableBoolean changed = new MutableBoolean(true);
   private transient Dropdown orientationDropdown;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$badlion$client$mods$render$ShowDirection$Direction;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$badlion$client$mods$render$ModOrientation;

   public Coordinates() {
      super("Coordinates", -100, -80, 113, 48);
      this.iconDimension = new ImageDimension(40, 78);
      this.defaultTopLeftBox = new BoxedCoord(2, 1, 0.7666666666666667D, 0.35555555555555557D);
      this.defaultCenterBox = new BoxedCoord(3, 2, 0.5166666666666667D, 0.4740740740740741D);
      this.defaultBottomRightBox = new BoxedCoord(4, 3, 0.2833333333333333D, 0.6222222222222222D);
   }

   public void init() {
      this.fontRenderer = new CustomFontRenderer();
      this.registerEvent(EventType.RENDER_GAME);
      this.registerEvent(EventType.MOTION_UPDATE);
      this.setFontOffset(0.009D);
      super.init();
   }

   public void reset() {
      this.biomeButton.setEnabled(true);
      this.directionButton.setEnabled(true);
      this.fancyFontButton.setEnabled(false);
      this.roundButton.setEnabled(true);
      this.biomeColor = new ModColor(-1);
      this.directionColor = new ModColor(-1);
      this.primaryColor = new ModColor(-47872);
      this.secondaryColor = new ModColor(-1);
      this.backgroundColor = new ModColor(-1289213133);
      this.modOrientation = ModOrientation.VERTICAL;
      this.updateSize();
      super.reset();
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 10));
      this.slideCogMenu.addElement(new ModPreviewRenderer(this, 0, 0, true, this.modOrientation == ModOrientation.HORIZONTAL?0:15));
      this.slideCogMenu.addElement(new Label("Settings", -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));
      this.slideCogMenu.addElement(this.orientationDropdown = new Dropdown(new String[]{"Horizontal", "Vertical"}, this.modOrientation.equals(ModOrientation.VERTICAL)?1:0, 0.19D));
      this.slideCogMenu.addElement(new ColorPicker("Main Color", this.primaryColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Secondary Color", this.secondaryColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Background Color", this.backgroundColor, 0.13D, true));
      this.slideCogMenu.addElement(new ColorPicker("Biome Color", this.biomeColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Direction Color", this.directionColor, 0.13D));
      this.biomeButton = new TextButton("Show Biome", this.biomeEnabled, 1.0D);
      this.directionButton = new TextButton("Show Direction", this.directionEnabled, 1.0D);
      this.slideCogMenu.addElement(this.biomeButton);
      this.slideCogMenu.addElement(this.directionButton);
      this.slideCogMenu.addElement(new Padding(0, 10));
      this.fancyFontButton = new TextButton("Fancy Font", this.useFancyFont, 1.0D);
      this.roundButton = new TextButton("Round Location", this.roundLocation, 1.0D);
      this.slideCogMenu.addElement(this.fancyFontButton);
      this.slideCogMenu.addElement(this.roundButton);
      this.biomeColor.init();
      this.directionColor.init();
      this.primaryColor.init();
      this.secondaryColor.init();
      this.backgroundColor.init();
      super.createCogMenu();
      this.updateSize();
   }

   public void onEvent(Event e) {
      if(e instanceof MotionUpdate) {
         ModOrientation modorientation = this.orientationDropdown.getValue().equals("Horizontal")?ModOrientation.HORIZONTAL:ModOrientation.VERTICAL;
         if(modorientation != this.modOrientation) {
            this.modOrientation = modorientation;
            this.updateSize();
            Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().initPages();
         }

         this.biomeColor.tickColor();
         this.directionColor.tickColor();
         this.primaryColor.tickColor();
         this.secondaryColor.tickColor();
         this.backgroundColor.tickColor();
      }

      if(e instanceof GUIClickMouse && ((GUIClickMouse)e).getMouseButton() == 0) {
         int k4 = Wrapper.getInstance().getMouseX();
         int i = Wrapper.getInstance().getMouseY();
         if(k4 > this.biomeButton.getX() && k4 < this.biomeButton.getX() + this.biomeButton.getWidth() && i > this.biomeButton.getY() && i < this.biomeButton.getY() + this.biomeButton.getHeight()) {
            this.changed.setValue(true);
         }

         if(k4 > this.directionButton.getX() && k4 < this.directionButton.getX() + this.directionButton.getWidth() && i > this.directionButton.getY() && i < this.directionButton.getY() + this.directionButton.getHeight()) {
            this.changed.setValue(true);
         }

         if(k4 > this.fancyFontButton.getX() && k4 < this.fancyFontButton.getX() + this.fancyFontButton.getWidth() && i > this.fancyFontButton.getY() && i < this.fancyFontButton.getY() + this.fancyFontButton.getHeight()) {
            this.changed.setValue(true);
         }

         if(k4 > this.roundButton.getX() && k4 < this.roundButton.getX() + this.roundButton.getWidth() && i > this.roundButton.getY() && i < this.roundButton.getY() + this.roundButton.getHeight()) {
            this.changed.setValue(true);
         }
      }

      if(e instanceof RenderGame && this.isEnabled()) {
         int l4 = 2;
         int i5 = 0;
         int j = 0;
         if(this.changed.isTrue()) {
            this.updateSize();
            this.changed.setValue(false);
         }

         this.beginRender();
         int k = 0;
         if(this.modOrientation == ModOrientation.VERTICAL) {
            k = 24;
            if(this.biomeEnabled.isTrue()) {
               k += 12;
            }
         }

         int l = 0;
         int i1 = 0;
         int k1 = 8 + l4 * 2 + k;
         String s = "%.1f";
         String s4 = "";
         String s1;
         String s2;
         String s3;
         if(this.roundLocation.getValue().booleanValue()) {
            s = "%,d";
            if(Wrapper.getInstance().getActiveModProfile().getModConfigurator().isInEditingMode()) {
               s1 = String.format(s, new Object[]{Integer.valueOf(100)});
               s2 = String.format(s, new Object[]{Integer.valueOf(70)});
               s3 = String.format(s, new Object[]{Integer.valueOf(-100)});
            } else {
               s1 = String.format(s, new Object[]{Integer.valueOf((int)this.gameInstance.thePlayer.posX)});
               s2 = String.format(s, new Object[]{Integer.valueOf((int)this.gameInstance.thePlayer.renderArmYaw)});
               s3 = String.format(s, new Object[]{Integer.valueOf((int)this.gameInstance.thePlayer.posZ)});
            }
         } else if(Wrapper.getInstance().getActiveModProfile().getModConfigurator().isInEditingMode()) {
            s1 = String.format(s, new Object[]{Double.valueOf(100.5D)});
            s2 = String.format(s, new Object[]{Double.valueOf(70.2D)});
            s3 = String.format(s, new Object[]{Double.valueOf(-100.5D)});
         } else {
            s1 = String.format(s, new Object[]{Double.valueOf(this.gameInstance.thePlayer.posX)});
            s2 = String.format(s, new Object[]{Double.valueOf((double)this.gameInstance.thePlayer.renderArmYaw)});
            s3 = String.format(s, new Object[]{Double.valueOf(this.gameInstance.thePlayer.posZ)});
         }

         if(this.biomeEnabled.isTrue()) {
            if(Wrapper.getInstance().getActiveModProfile().getModConfigurator().isInEditingMode()) {
               s4 = "MushroomIslandShore";
            } else {
               BlockPos blockpos = new BlockPos((int)this.gameInstance.thePlayer.posX, (int)this.gameInstance.thePlayer.renderArmYaw, (int)this.gameInstance.thePlayer.posZ);
               s4 = Minecraft.getMinecraft().theWorld.getBiomeGenForCoords(blockpos).biomeName;
            }
         }

         int k2 = 0;
         s1 = s1.replace(" ", ",");
         s2 = s2.replace(" ", ",");
         s3 = s3.replace(" ", ",");
         int l1;
         int i2;
         int j2;
         int l2;
         if(!this.useFancyFont.getValue().booleanValue()) {
            l1 = this.gameInstance.fontRendererObj.getStringWidth("X: " + s1);
            i2 = this.gameInstance.fontRendererObj.getStringWidth("Y: " + s2);
            j2 = this.gameInstance.fontRendererObj.getStringWidth("Z: " + s3);
            if(this.biomeEnabled.isTrue()) {
               k2 = this.gameInstance.fontRendererObj.getStringWidth(s4);
            }

            l2 = this.gameInstance.fontRendererObj.getStringWidth(" ");
         } else {
            l1 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth("X: " + s1, 12, BadlionFontRenderer.FontType.TITLE);
            i2 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth("Y: " + s2, 12, BadlionFontRenderer.FontType.TITLE);
            j2 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth("Z: " + s3, 12, BadlionFontRenderer.FontType.TITLE);
            if(this.biomeEnabled.isTrue()) {
               k2 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(s4, 12, BadlionFontRenderer.FontType.TITLE);
            }

            l2 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(" ", 12, BadlionFontRenderer.FontType.TITLE);
         }

         int k5;
         if(this.modOrientation.equals(ModOrientation.VERTICAL)) {
            k5 = Math.max(l1, Math.max(i2, Math.max(j2, k2)));
         } else {
            int i3 = 2;
            if(this.biomeEnabled.isTrue()) {
               ++i3;
            }

            k5 = l1 + i2 + j2 + k2 + l2 * i3;
         }

         if(this.directionEnabled.isTrue()) {
            if(this.modOrientation.equals(ModOrientation.VERTICAL)) {
               if(k5 - l1 < 20) {
                  k5 += 20 - (k5 - l1);
               }
            } else {
               k5 += 32;
            }
         }

         if(!this.useFancyFont.getValue().booleanValue()) {
            int j1 = l + k5 + l4 * 2;
            Gui.drawRect(l, i1, j1, k1, this.backgroundColor.getColorInt());
            i5 = (int)((double)i5 / this.getScaleX()) + l4;
            j = (int)((double)j / this.getScaleY()) + l4;
            label0:
            switch($SWITCH_TABLE$net$badlion$client$mods$render$ModOrientation()[this.modOrientation.ordinal()]) {
            case 1:
               int l5 = 0;
               int k3 = this.gameInstance.fontRendererObj.getStringWidth("X: ");
               this.gameInstance.fontRendererObj.drawString("X: ", i5, j, this.primaryColor.getColorInt());
               this.gameInstance.fontRendererObj.drawString(s1, i5 + k3, j, this.secondaryColor.getColorInt());
               int currentY = j + 12;
               int l3 = this.gameInstance.fontRendererObj.getStringWidth("Y: ");
               this.gameInstance.fontRendererObj.drawString("Y: ", i5, currentY, this.primaryColor.getColorInt());
               this.gameInstance.fontRendererObj.drawString(s2, i5 + l3, currentY, this.secondaryColor.getColorInt());
               currentY = currentY + 12;
               int j4 = this.gameInstance.fontRendererObj.getStringWidth("Z: ");
               this.gameInstance.fontRendererObj.drawString("Z: ", i5, currentY, this.primaryColor.getColorInt());
               this.gameInstance.fontRendererObj.drawString(s3, i5 + j4, currentY, this.secondaryColor.getColorInt());
               if(this.biomeEnabled.isTrue()) {
                  currentY += 12;
                  this.gameInstance.fontRendererObj.drawString(s4, i5, currentY, this.biomeColor.getColorInt());
               }

               if(this.directionEnabled.isTrue()) {
                  ShowDirection.Direction showdirection$direction1 = ShowDirection.Direction.values()[MathHelper.floor_double((double)(this.gameInstance.thePlayer.rotationYaw * 4.0F / 180.0F) + 0.5D) & 7];
                  this.gameInstance.fontRendererObj.drawString(showdirection$direction1.name(), k5 - 8, currentY, this.primaryColor.getColorInt());
                  switch($SWITCH_TABLE$net$badlion$client$mods$render$ShowDirection$Direction()[showdirection$direction1.ordinal()]) {
                  case 1:
                     this.gameInstance.fontRendererObj.drawString("++", k5 - 8, currentY, this.secondaryColor.getColorInt());
                     break label0;
                  case 2:
                     this.gameInstance.fontRendererObj.drawString("+", k5 - 8, currentY, this.secondaryColor.getColorInt());
                     this.gameInstance.fontRendererObj.drawString("-", k5 - 8, l5, this.secondaryColor.getColorInt());
                     break label0;
                  case 3:
                     this.gameInstance.fontRendererObj.drawString("--", k5 - 8, l5, this.secondaryColor.getColorInt());
                     break label0;
                  case 4:
                     this.gameInstance.fontRendererObj.drawString("-", k5 - 8, l5, this.secondaryColor.getColorInt());
                     this.gameInstance.fontRendererObj.drawString("-", k5 - 8, currentY, this.secondaryColor.getColorInt());
                     break label0;
                  case 5:
                     this.gameInstance.fontRendererObj.drawString("--", k5 - 8, currentY, this.secondaryColor.getColorInt());
                     break label0;
                  case 6:
                     this.gameInstance.fontRendererObj.drawString("-", k5 - 8, currentY, this.secondaryColor.getColorInt());
                     this.gameInstance.fontRendererObj.drawString("+", k5 - 8, l5, this.secondaryColor.getColorInt());
                     break label0;
                  case 7:
                     this.gameInstance.fontRendererObj.drawString("++", k5 - 8, l5, this.secondaryColor.getColorInt());
                     break label0;
                  case 8:
                     this.gameInstance.fontRendererObj.drawString("+", k5 - 8, l5, this.secondaryColor.getColorInt());
                     this.gameInstance.fontRendererObj.drawString("+", k5 - 8, currentY, this.secondaryColor.getColorInt());
                  }
               }
               break;
            case 2:
               this.fontRenderer.setRenderMode(CustomFontRenderer.RenderMode.DEFAULT);
               String s5 = "X:";
               String s6 = "Y:";
               String s7 = "Z:";
               StringBuilder stringbuilder = new StringBuilder();
               this.fontRenderer.drawString(s5, i5, j, this.primaryColor.getColorInt());
               stringbuilder.append(s5);
               this.fontRenderer.drawString(" " + s1, i5 + this.fontRenderer.getStringWidth(stringbuilder.toString()), j, this.secondaryColor.getColorInt());
               stringbuilder.append(" ").append(s1);
               this.fontRenderer.drawString(" " + s6, i5 + this.fontRenderer.getStringWidth(stringbuilder.toString()), j, this.primaryColor.getColorInt());
               stringbuilder.append(" ").append(s6);
               this.fontRenderer.drawString(" " + s2, i5 + this.fontRenderer.getStringWidth(stringbuilder.toString()), j, this.secondaryColor.getColorInt());
               stringbuilder.append(" ").append(s2);
               this.fontRenderer.drawString(" " + s7, i5 + this.fontRenderer.getStringWidth(stringbuilder.toString()), j, this.primaryColor.getColorInt());
               stringbuilder.append(" ").append(s7);
               this.fontRenderer.drawString(" " + s3, i5 + this.fontRenderer.getStringWidth(stringbuilder.toString()), j, this.secondaryColor.getColorInt());
               stringbuilder.append(" ").append(s3);
               this.fontRenderer.drawString(" " + s4, i5 + this.fontRenderer.getStringWidth(stringbuilder.toString()), j, this.biomeColor.getColorInt());
               stringbuilder.append("  ").append(s4);
               if(this.directionEnabled.isTrue()) {
                  ShowDirection.Direction showdirection$direction = ShowDirection.Direction.values()[MathHelper.floor_double((double)(this.gameInstance.thePlayer.rotationYaw * 4.0F / 180.0F) + 0.5D) & 7];
                  this.fontRenderer.drawString(showdirection$direction.name(), k5 - 10, j, this.primaryColor.getColorInt());
                  String s8 = "";
                  String s9 = "";
                  switch($SWITCH_TABLE$net$badlion$client$mods$render$ShowDirection$Direction()[showdirection$direction.ordinal()]) {
                  case 1:
                     s9 = "++";
                     break;
                  case 2:
                     s8 = "-";
                     s9 = "+";
                     break;
                  case 3:
                     s8 = "--";
                     break;
                  case 4:
                     s9 = "-";
                     s8 = "-";
                     break;
                  case 5:
                     s9 = "--";
                     break;
                  case 6:
                     s8 = "+";
                     s9 = "-";
                     break;
                  case 7:
                     s8 = "++";
                     break;
                  case 8:
                     s9 = "+";
                     s8 = "+";
                  }

                  if(!s8.isEmpty()) {
                     GL11.glScaled(0.95D, 0.95D, 1.0D);
                     this.fontRenderer.drawString(s8, k5 - 16, j - 2, this.secondaryColor.getColorInt());
                     GL11.glScaled(1.0526315789473684D, 1.0526315789473684D, 1.0D);
                  }

                  if(!s9.isEmpty()) {
                     GL11.glScaled(0.95D, 0.95D, 1.0D);
                     this.fontRenderer.drawString(s9, k5 - 16, j + 4, this.secondaryColor.getColorInt());
                     GL11.glScaled(1.0526315789473684D, 1.0526315789473684D, 1.0D);
                  }
               }
            }
         } else {
            int j5 = k5 + l4 * 2;
            Gui.drawRect(l, i1, j5, k1, this.backgroundColor.getColorInt());
            j = l4 - 2;
            label0:
            switch($SWITCH_TABLE$net$badlion$client$mods$render$ModOrientation()[this.modOrientation.ordinal()]) {
            case 1:
               int i6 = 0;
               ColorUtil.bindColor(this.primaryColor.getColor());
               Wrapper.getInstance().getBadlionFontRenderer().drawString(l4, j, "X:", 12, BadlionFontRenderer.FontType.TITLE);
               int j6 = j + 12;
               Wrapper.getInstance().getBadlionFontRenderer().drawString(l4, j6, "Y:", 12, BadlionFontRenderer.FontType.TITLE);
               j6 = j6 + 12;
               Wrapper.getInstance().getBadlionFontRenderer().drawString(l4, j6, "Z:", 12, BadlionFontRenderer.FontType.TITLE);
               ColorUtil.bindColor(this.secondaryColor.getColor());
               int k6 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth("X: ", 12, BadlionFontRenderer.FontType.TITLE);
               Wrapper.getInstance().getBadlionFontRenderer().drawString(l4 + k6, j, s1, 12, BadlionFontRenderer.FontType.TITLE);
               j6 = j + 12;
               int i4 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth("Y: ", 12, BadlionFontRenderer.FontType.TITLE);
               Wrapper.getInstance().getBadlionFontRenderer().drawString(l4 + i4, j6, s2, 12, BadlionFontRenderer.FontType.TITLE);
               j6 = j6 + 12;
               int l6 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth("Z: ", 12, BadlionFontRenderer.FontType.TITLE);
               Wrapper.getInstance().getBadlionFontRenderer().drawString(l4 + l6, j6, s3, 12, BadlionFontRenderer.FontType.TITLE);
               if(this.biomeEnabled.isTrue()) {
                  j6 += 12;
                  ColorUtil.bindColor(this.biomeColor.getColor());
                  Wrapper.getInstance().getBadlionFontRenderer().drawString(l4, j6, s4, 12, BadlionFontRenderer.FontType.TITLE);
               }

               if(this.directionEnabled.isTrue()) {
                  ShowDirection.Direction showdirection$direction2 = ShowDirection.Direction.values()[MathHelper.floor_double((double)(this.gameInstance.thePlayer.rotationYaw * 4.0F / 180.0F) + 0.5D) & 7];
                  ColorUtil.bindColor(this.primaryColor.getColor());
                  Wrapper.getInstance().getBadlionFontRenderer().drawString(k5 - 12, j6, showdirection$direction2.name(), 12, BadlionFontRenderer.FontType.TITLE);
                  ColorUtil.bindColor(this.secondaryColor.getColor());
                  switch($SWITCH_TABLE$net$badlion$client$mods$render$ShowDirection$Direction()[showdirection$direction2.ordinal()]) {
                  case 1:
                     Wrapper.getInstance().getBadlionFontRenderer().drawString(k5 - 12, j6, "++", 12, BadlionFontRenderer.FontType.TITLE);
                     break label0;
                  case 2:
                     Wrapper.getInstance().getBadlionFontRenderer().drawString(k5 - 12, j6, "  +", 12, BadlionFontRenderer.FontType.TITLE);
                     Wrapper.getInstance().getBadlionFontRenderer().drawString(k5 - 12, i6, "  -", 12, BadlionFontRenderer.FontType.TITLE);
                     break label0;
                  case 3:
                     Wrapper.getInstance().getBadlionFontRenderer().drawString(k5 - 12, i6, "--", 12, BadlionFontRenderer.FontType.TITLE);
                     break label0;
                  case 4:
                     Wrapper.getInstance().getBadlionFontRenderer().drawString(k5 - 12, i6, "  -", 12, BadlionFontRenderer.FontType.TITLE);
                     Wrapper.getInstance().getBadlionFontRenderer().drawString(k5 - 12, j6, "  -", 12, BadlionFontRenderer.FontType.TITLE);
                     break label0;
                  case 5:
                     Wrapper.getInstance().getBadlionFontRenderer().drawString(k5 - 12, j6, "--", 12, BadlionFontRenderer.FontType.TITLE);
                     break label0;
                  case 6:
                     Wrapper.getInstance().getBadlionFontRenderer().drawString(k5 - 12, j6, "  -", 12, BadlionFontRenderer.FontType.TITLE);
                     Wrapper.getInstance().getBadlionFontRenderer().drawString(k5 - 12, i6, "  +", 12, BadlionFontRenderer.FontType.TITLE);
                     break label0;
                  case 7:
                     Wrapper.getInstance().getBadlionFontRenderer().drawString(k5 - 12, i6, "++", 12, BadlionFontRenderer.FontType.TITLE);
                     break label0;
                  case 8:
                     Wrapper.getInstance().getBadlionFontRenderer().drawString(k5 - 12, i6, "  +", 12, BadlionFontRenderer.FontType.TITLE);
                     Wrapper.getInstance().getBadlionFontRenderer().drawString(k5 - 12, j6, "  +", 12, BadlionFontRenderer.FontType.TITLE);
                  }
               }
               break;
            case 2:
               String s10 = "X:";
               String s11 = "Y:";
               String s12 = "Z:";
               StringBuilder stringbuilder1 = new StringBuilder();
               ColorUtil.bindColor(this.primaryColor.getColor());
               Wrapper.getInstance().getBadlionFontRenderer().drawString(l4, j, s10, 12, BadlionFontRenderer.FontType.TITLE);
               stringbuilder1.append(s10);
               ColorUtil.bindColor(this.secondaryColor.getColor());
               Wrapper.getInstance().getBadlionFontRenderer().drawString(l4 + Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(stringbuilder1.toString(), 12, BadlionFontRenderer.FontType.TITLE), j, " " + s1, 12, BadlionFontRenderer.FontType.TITLE);
               stringbuilder1.append(" ").append(s1);
               ColorUtil.bindColor(this.primaryColor.getColor());
               Wrapper.getInstance().getBadlionFontRenderer().drawString(l4 + Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(stringbuilder1.toString(), 12, BadlionFontRenderer.FontType.TITLE), j, " " + s11, 12, BadlionFontRenderer.FontType.TITLE);
               stringbuilder1.append(" ").append(s11);
               ColorUtil.bindColor(this.secondaryColor.getColor());
               Wrapper.getInstance().getBadlionFontRenderer().drawString(l4 + Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(stringbuilder1.toString(), 12, BadlionFontRenderer.FontType.TITLE), j, " " + s2, 12, BadlionFontRenderer.FontType.TITLE);
               stringbuilder1.append(" ").append(s2);
               ColorUtil.bindColor(this.primaryColor.getColor());
               Wrapper.getInstance().getBadlionFontRenderer().drawString(l4 + Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(stringbuilder1.toString(), 12, BadlionFontRenderer.FontType.TITLE), j, " " + s12, 12, BadlionFontRenderer.FontType.TITLE);
               stringbuilder1.append(" ").append(s12);
               ColorUtil.bindColor(this.secondaryColor.getColor());
               Wrapper.getInstance().getBadlionFontRenderer().drawString(l4 + Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(stringbuilder1.toString(), 12, BadlionFontRenderer.FontType.TITLE), j, " " + s3, 12, BadlionFontRenderer.FontType.TITLE);
               stringbuilder1.append(" ").append(s3);
               ColorUtil.bindColor(this.biomeColor.getColor());
               Wrapper.getInstance().getBadlionFontRenderer().drawString(l4 + Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(stringbuilder1.toString(), 12, BadlionFontRenderer.FontType.TITLE), j, "  " + s4, 12, BadlionFontRenderer.FontType.TITLE);
               stringbuilder1.append("  ").append(s4);
               if(this.directionEnabled.isTrue()) {
                  ColorUtil.bindColor(this.primaryColor.getColor());
                  ShowDirection.Direction showdirection$direction3 = ShowDirection.Direction.values()[MathHelper.floor_double((double)(this.gameInstance.thePlayer.rotationYaw * 4.0F / 180.0F) + 0.5D) & 7];
                  Wrapper.getInstance().getBadlionFontRenderer().drawString(k5 - 12, j, showdirection$direction3.name(), 12, BadlionFontRenderer.FontType.TITLE);
                  ColorUtil.bindColor(this.secondaryColor.getColor());
                  String s13 = "";
                  String s14 = "";
                  switch($SWITCH_TABLE$net$badlion$client$mods$render$ShowDirection$Direction()[showdirection$direction3.ordinal()]) {
                  case 1:
                     s14 = "++";
                     break;
                  case 2:
                     s13 = "-";
                     s14 = "+";
                     break;
                  case 3:
                     s13 = "--";
                     break;
                  case 4:
                     s14 = "-";
                     s13 = "-";
                     break;
                  case 5:
                     s14 = "--";
                     break;
                  case 6:
                     s13 = "+";
                     s14 = "-";
                     break;
                  case 7:
                     s13 = "++";
                     break;
                  case 8:
                     s14 = "+";
                     s13 = "+";
                  }

                  if(!s13.isEmpty()) {
                     Wrapper.getInstance().getBadlionFontRenderer().drawString(k5 - 24, j - 3, s13, 12, BadlionFontRenderer.FontType.TITLE);
                  }

                  if(!s14.isEmpty()) {
                     Wrapper.getInstance().getBadlionFontRenderer().drawString(k5 - 24, j + 2, s14, 12, BadlionFontRenderer.FontType.TITLE);
                  }
               }
            }
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.endRender();
      }

      super.onEvent(e);
   }

   private void updateSize() {
      int i;
      int j;
      if(this.modOrientation == ModOrientation.VERTICAL) {
         if(this.biomeEnabled.isTrue()) {
            i = 113;
            j = 48;
            if(this.useFancyFont.isTrue()) {
               i -= 13;
            }
         } else {
            if(this.useFancyFont.isTrue()) {
               i = 31;
            } else {
               i = 40;
            }

            if(this.roundLocation.isFalse()) {
               if(this.useFancyFont.isTrue()) {
                  i += 7;
               } else {
                  i += 8;
               }
            }

            if(this.directionEnabled.isTrue()) {
               if(this.useFancyFont.isTrue()) {
                  i += 17;
               } else {
                  i += 14;
               }
            }

            j = 36;
         }
      } else {
         if(this.useFancyFont.isTrue()) {
            i = 99;
         } else {
            i = 126;
         }

         if(this.roundLocation.isTrue()) {
            if(this.useFancyFont.isTrue()) {
               i -= 21;
            } else {
               i -= 24;
            }
         }

         if(this.biomeEnabled.isTrue()) {
            if(this.useFancyFont.isTrue()) {
               i += 98;
            } else {
               i += 113;
            }
         }

         if(this.directionEnabled.isTrue()) {
            if(this.useFancyFont.isTrue()) {
               i += 32;
            } else {
               i += 32;
            }
         }

         j = 12;
      }

      double d0 = this.getScaleX();
      double d1 = this.getScaleY();
      this.defaultSizeX = i;
      this.defaultSizeY = j;
      this.sizeX = (double)this.defaultSizeX * d0;
      this.sizeY = (double)this.defaultSizeY * d1;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$badlion$client$mods$render$ShowDirection$Direction() {
      int[] var10000 = $SWITCH_TABLE$net$badlion$client$mods$render$ShowDirection$Direction;
      if($SWITCH_TABLE$net$badlion$client$mods$render$ShowDirection$Direction != null) {
         return var10000;
      } else {
         int[] var0 = new int[ShowDirection.Direction.values().length];

         try {
            var0[ShowDirection.Direction.E.ordinal()] = 7;
         } catch (NoSuchFieldError var8) {
            ;
         }

         try {
            var0[ShowDirection.Direction.N.ordinal()] = 5;
         } catch (NoSuchFieldError var7) {
            ;
         }

         try {
            var0[ShowDirection.Direction.NE.ordinal()] = 6;
         } catch (NoSuchFieldError var6) {
            ;
         }

         try {
            var0[ShowDirection.Direction.NW.ordinal()] = 4;
         } catch (NoSuchFieldError var5) {
            ;
         }

         try {
            var0[ShowDirection.Direction.S.ordinal()] = 1;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            var0[ShowDirection.Direction.SE.ordinal()] = 8;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[ShowDirection.Direction.SW.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[ShowDirection.Direction.W.ordinal()] = 3;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$badlion$client$mods$render$ShowDirection$Direction = var0;
         return var0;
      }
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$badlion$client$mods$render$ModOrientation() {
      int[] var10000 = $SWITCH_TABLE$net$badlion$client$mods$render$ModOrientation;
      if($SWITCH_TABLE$net$badlion$client$mods$render$ModOrientation != null) {
         return var10000;
      } else {
         int[] var0 = new int[ModOrientation.values().length];

         try {
            var0[ModOrientation.HORIZONTAL.ordinal()] = 2;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[ModOrientation.VERTICAL.ordinal()] = 1;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$badlion$client$mods$render$ModOrientation = var0;
         return var0;
      }
   }
}
