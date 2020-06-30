package net.badlion.client.mods.render;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.ClickMouse;
import net.badlion.client.events.event.GUIClickMouse;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.events.event.RenderGame;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.ModPreviewRenderer;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.TextButton;
import net.badlion.client.gui.slideout.elements.ColorPicker;
import net.badlion.client.mods.render.RenderMod;
import net.badlion.client.mods.render.color.ModColor;
import net.badlion.client.mods.render.gui.BoxedCoord;
import net.badlion.client.util.ColorUtil;
import net.badlion.client.util.ImageDimension;
import net.minecraft.client.gui.Gui;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class Keystroke extends RenderMod {
   private MutableBoolean fancyFont = new MutableBoolean(false);
   private transient TextButton defaultFontButton;
   private ModColor keystrokeBackgroundColor = new ModColor(-1289213133);
   private ModColor keystrokeColorClicked = new ModColor(1728053247);
   private ModColor fontColor = new ModColor(-1);
   private ModColor fontColorClicked = new ModColor(-12084809);
   private MutableBoolean showCPS = new MutableBoolean(true);
   private MutableBoolean showMouse = new MutableBoolean(true);
   private MutableBoolean showSpacebar = new MutableBoolean(true);
   private MutableBoolean showWASD = new MutableBoolean(true);
   private transient TextButton showCPSButton;
   private transient TextButton showMouseButton;
   private transient TextButton showSpaceBarButton;
   private transient TextButton showWASDButton;
   private transient List delaysLMB = new ArrayList();
   private transient List delaysRMB = new ArrayList();
   private MutableBoolean changed = new MutableBoolean(true);

   public Keystroke() {
      super("Keystroke", -35, -112, 47, 63);
      this.iconDimension = new ImageDimension(114, 61);
      this.defaultTopLeftBox = new BoxedCoord(30, 0, 0.75D, 0.0D);
      this.defaultCenterBox = new BoxedCoord(31, 2, 0.65D, 0.11851851851851852D);
      this.defaultBottomRightBox = new BoxedCoord(32, 4, 0.55D, 0.2962962962962963D);
   }

   public void init() {
      this.registerEvent(EventType.RENDER_GAME);
      this.registerEvent(EventType.MOTION_UPDATE);
      this.registerEvent(EventType.CLICK_MOUSE);
      this.setFontOffset(0.079D);
      this.offsetX = -2;
      super.init();
   }

   public void reset() {
      this.defaultFontButton.setEnabled(false);
      this.fancyFont.setValue(false);
      this.keystrokeBackgroundColor = new ModColor(-1289213133);
      this.keystrokeColorClicked = new ModColor(1728053247);
      this.fontColor = new ModColor(-1);
      this.fontColorClicked = new ModColor(-12084809);
      this.showCPSButton.setEnabled(true);
      this.showMouseButton.setEnabled(true);
      this.showSpaceBarButton.setEnabled(true);
      this.showWASDButton.setEnabled(true);
      this.updateSize();
      super.reset();
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 8));
      this.slideCogMenu.addElement(new ModPreviewRenderer(this, 0, 0, true, 5));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 4));
      this.slideCogMenu.addElement(new Label("Settings", -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));
      this.slideCogMenu.addElement(new ColorPicker("Back Color", this.keystrokeBackgroundColor, 0.13D, true));
      this.slideCogMenu.addElement(new ColorPicker("Back Color Clicked", this.keystrokeColorClicked, 0.13D, true));
      this.slideCogMenu.addElement(new ColorPicker("Font Color", this.fontColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Font Color Clicked", this.fontColorClicked, 0.13D));
      this.slideCogMenu.addElement(this.showCPSButton = new TextButton("Show CPS", this.showCPS, 1.0D));
      this.slideCogMenu.addElement(this.showMouseButton = new TextButton("Show Mouse", this.showMouse, 1.0D));
      this.slideCogMenu.addElement(this.showSpaceBarButton = new TextButton("Show Space", this.showSpacebar, 1.0D));
      this.slideCogMenu.addElement(this.showWASDButton = new TextButton("Show WASD", this.showWASD, 1.0D));
      this.defaultFontButton = new TextButton("Fancy Font", this.fancyFont, 1.0D);
      this.slideCogMenu.addElement(this.defaultFontButton);
      this.keystrokeColorClicked.init();
      this.keystrokeBackgroundColor.init();
      this.fontColor.init();
      this.fontColorClicked.init();
      super.createCogMenu();
      this.updateSize();
   }

   private String getKeyName(int keyCode) {
      return keyCode < 0?(keyCode == -100?"LMB":(keyCode == -99?"RMB":(keyCode == -98?"MMB":"MB" + String.valueOf(keyCode - -99)))):Keyboard.getKeyName(keyCode);
   }

   public void onEvent(Event e) {
      if(e instanceof MotionUpdate) {
         this.keystrokeBackgroundColor.tickColor();
         this.keystrokeColorClicked.tickColor();
         this.fontColor.tickColor();
         this.fontColorClicked.tickColor();
         List<Long> newDelaysLMB = new ArrayList();
         Iterator var5 = this.delaysLMB.iterator();

         while(var5.hasNext()) {
            long l = ((Long)var5.next()).longValue();
            if(System.currentTimeMillis() - l < 1000L) {
               newDelaysLMB.add(Long.valueOf(l));
            }
         }

         this.delaysLMB = newDelaysLMB;
         List<Long> newDelaysRMB = new ArrayList();
         Iterator var6 = this.delaysRMB.iterator();

         while(var6.hasNext()) {
            long i = ((Long)var6.next()).longValue();
            if(System.currentTimeMillis() - i < 1000L) {
               newDelaysRMB.add(Long.valueOf(i));
            }
         }

         this.delaysRMB = newDelaysRMB;
      }

      if(e instanceof GUIClickMouse && ((GUIClickMouse)e).getMouseButton() == 0) {
         int k = Wrapper.getInstance().getMouseX();
         int i1 = Wrapper.getInstance().getMouseY();
         if(k > this.showMouseButton.getX() && k < this.showMouseButton.getX() + this.showMouseButton.getWidth() && i1 > this.showMouseButton.getY() && i1 < this.showMouseButton.getY() + this.showMouseButton.getHeight()) {
            this.changed.setValue(true);
         }

         if(k > this.showSpaceBarButton.getX() && k < this.showSpaceBarButton.getX() + this.showSpaceBarButton.getWidth() && i1 > this.showSpaceBarButton.getY() && i1 < this.showSpaceBarButton.getY() + this.showSpaceBarButton.getHeight()) {
            this.changed.setValue(true);
         }

         if(k > this.showWASDButton.getX() && k < this.showWASDButton.getX() + this.showWASDButton.getWidth() && i1 > this.showWASDButton.getY() && i1 < this.showWASDButton.getY() + this.showWASDButton.getHeight()) {
            this.changed.setValue(true);
         }

         if(k > this.defaultFontButton.getX() && k < this.defaultFontButton.getX() + this.defaultFontButton.getWidth() && i1 > this.defaultFontButton.getY() && i1 < this.defaultFontButton.getY() + this.defaultFontButton.getHeight()) {
            this.changed.setValue(true);
         }
      }

      if(e instanceof RenderGame && this.isEnabled()) {
         int l = 0;
         int j1 = 0;
         if(this.changed.isTrue()) {
            this.updateSize();
            this.changed.setValue(false);
         }

         this.beginRender();
         if(this.showWASD.booleanValue()) {
            this.renderWasd(l, j1);
            j1 += 32;
         }

         if(this.showMouse.booleanValue()) {
            this.renderMouse(l, j1);
            j1 += 16;
         }

         if(this.showSpacebar.booleanValue()) {
            this.renderSpace(l, j1);
         }

         this.endRender();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      if(e instanceof ClickMouse) {
         ClickMouse clickmouse = (ClickMouse)e;
         if(clickmouse.getClickType() == 0) {
            this.delaysLMB.add(Long.valueOf(System.currentTimeMillis()));
         }

         if(clickmouse.getClickType() == 1) {
            this.delaysRMB.add(Long.valueOf(System.currentTimeMillis()));
         }
      }

      super.onEvent(e);
   }

   public void renderWasd(int xStart, int yStart) {
      boolean flag = this.gameInstance.gameSettings.keyBindLeft.isKeyDown();
      boolean flag1 = this.gameInstance.gameSettings.keyBindBack.isKeyDown();
      boolean flag2 = this.gameInstance.gameSettings.keyBindRight.isKeyDown();
      boolean flag3 = this.gameInstance.gameSettings.keyBindJump.isKeyDown();
      xStart = xStart + 16;
      this.renderChar(xStart, yStart, this.getKeyName(this.gameInstance.gameSettings.keyBindLeft.getKeyCode()), flag);
      xStart = xStart - 16;
      yStart = yStart + 16;
      this.renderChar(xStart, yStart, this.getKeyName(this.gameInstance.gameSettings.keyBindBack.getKeyCode()), flag1);
      xStart = xStart + 16;
      this.renderChar(xStart, yStart, this.getKeyName(this.gameInstance.gameSettings.keyBindRight.getKeyCode()), flag2);
      xStart = xStart + 16;
      this.renderChar(xStart, yStart, this.getKeyName(this.gameInstance.gameSettings.keyBindJump.getKeyCode()), flag3);
   }

   public void renderChar(int x, int y, String keyName, boolean keyDown) {
      Gui.drawRect(x, y, x + 15, y + 15, keyDown?this.keystrokeColorClicked.getColorInt():this.keystrokeBackgroundColor.getColorInt());
      if(!this.fancyFont.getValue().booleanValue()) {
         int i = this.gameInstance.fontRendererObj.getCharWidth(keyName.charAt(0));
         this.gameInstance.fontRendererObj.drawString(keyName, x + (15 - i) / 2 + 1, y + 4, keyDown?this.fontColorClicked.getColorInt():this.fontColor.getColorInt());
      } else {
         ColorUtil.bindColor(keyDown?this.fontColorClicked.getColor():this.fontColor.getColor());
         int j = Wrapper.getInstance().getBadlionFontRenderer().getCharWidth(keyName.charAt(0), 12, BadlionFontRenderer.FontType.TITLE);
         Wrapper.getInstance().getBadlionFontRenderer().drawString(x + (15 - j) / 2, y + 2, keyName, 12, BadlionFontRenderer.FontType.TITLE);
      }

   }

   public void renderMouse(int xStart, int yStart) {
      boolean flag = this.gameInstance.gameSettings.keyBindPickBlock.isKeyDown();
      boolean flag1 = this.gameInstance.gameSettings.keyBindDrop.isKeyDown();
      Gui.drawRect(xStart, yStart, xStart + 23, yStart + 15, flag?this.keystrokeColorClicked.getColorInt():this.keystrokeBackgroundColor.getColorInt());
      String s = this.getKeyName(this.gameInstance.gameSettings.keyBindPickBlock.getKeyCode());
      boolean flag2 = this.delaysLMB.size() > 0;
      boolean flag3 = this.delaysRMB.size() > 0;
      double d0 = flag2 && this.showCPS.booleanValue()?0.65D:1.0D;
      double d1 = flag3 && this.showCPS.booleanValue()?0.65D:1.0D;
      if(flag2 && this.showCPS.booleanValue()) {
         s = this.delaysLMB.size() + " CPS";
      }

      GL11.glScaled(d0, d0, d0);
      if(!this.fancyFont.getValue().booleanValue()) {
         int i = this.gameInstance.fontRendererObj.getStringWidth(s);
         int j = xStart + (23 - i) / 2 + 1;
         int k = yStart + 4;
         j = (int)((double)j / d0);
         k = (int)((double)k / d0);
         if(flag2 && this.showCPS.booleanValue()) {
            j += 6;
            k += 2;
         }

         this.gameInstance.fontRendererObj.drawString(s, j, k, flag?this.fontColorClicked.getColorInt():this.fontColor.getColorInt());
      } else {
         ColorUtil.bindColor(flag?this.fontColorClicked.getColor():this.fontColor.getColor());
         int l = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(s, 10, BadlionFontRenderer.FontType.TITLE);
         int k1 = xStart + (23 - l) / 2;
         int j2 = yStart + 3;
         k1 = (int)((double)k1 / d0);
         j2 = (int)((double)j2 / d0);
         if(flag2 && this.showCPS.booleanValue()) {
            k1 += 6;
            j2 += 2;
         }

         Wrapper.getInstance().getBadlionFontRenderer().drawString(k1, j2, s, 10, BadlionFontRenderer.FontType.TITLE);
      }

      GL11.glScaled(1.0D / d0, 1.0D / d0, 1.0D / d0);
      xStart = xStart + 24;
      Gui.drawRect(xStart, yStart, xStart + 23, yStart + 15, flag1?this.keystrokeColorClicked.getColorInt():this.keystrokeBackgroundColor.getColorInt());
      s = this.getKeyName(this.gameInstance.gameSettings.keyBindDrop.getKeyCode());
      if(flag3 && this.showCPS.booleanValue()) {
         s = this.delaysRMB.size() + " CPS";
      }

      GL11.glScaled(d1, d1, d1);
      if(!this.fancyFont.getValue().booleanValue()) {
         int i1 = this.gameInstance.fontRendererObj.getStringWidth(s);
         int l1 = xStart + (23 - i1) / 2 + 1;
         int k2 = yStart + 4;
         l1 = (int)((double)l1 / d1);
         k2 = (int)((double)k2 / d1);
         if(flag3 && this.showCPS.booleanValue()) {
            l1 += 6;
            k2 += 2;
         }

         this.gameInstance.fontRendererObj.drawString(s, l1, k2, flag1?this.fontColorClicked.getColorInt():this.fontColor.getColorInt());
      } else {
         ColorUtil.bindColor(flag1?this.fontColorClicked.getColor():this.fontColor.getColor());
         int j1 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(s, 10, BadlionFontRenderer.FontType.TITLE);
         int i2 = xStart + (23 - j1) / 2;
         int l2 = yStart + 3;
         i2 = (int)((double)i2 / d1);
         l2 = (int)((double)l2 / d1);
         if(flag3 && this.showCPS.booleanValue()) {
            i2 += 6;
            l2 += 2;
         }

         Wrapper.getInstance().getBadlionFontRenderer().drawString(i2, l2, s, 10, BadlionFontRenderer.FontType.TITLE);
      }

      GL11.glScaled(1.0D / d1, 1.0D / d1, 1.0D / d1);
   }

   public void renderSpace(int xStart, int yStart) {
      boolean flag = this.gameInstance.gameSettings.keyBindSneak.isKeyDown();
      Gui.drawRect(xStart, yStart, xStart + 47, yStart + 15, flag?this.keystrokeColorClicked.getColorInt():this.keystrokeBackgroundColor.getColorInt());
      Gui.drawRect(xStart + 10, yStart + 7, xStart + 47 - 10, yStart + 9, flag?this.fontColorClicked.getColorInt():this.fontColor.getColorInt());
   }

   private void updateSize() {
      int i = 47;
      int j = 63;
      if(this.showSpacebar.isFalse()) {
         j -= 16;
      }

      if(this.showMouse.isFalse()) {
         j -= 16;
      }

      if(this.showWASD.isFalse()) {
         j -= 32;
      }

      double d0 = this.getScaleX();
      double d1 = this.getScaleY();
      this.defaultSizeX = i;
      this.defaultSizeY = j;
      this.sizeX = (double)this.defaultSizeX * d0;
      this.sizeY = (double)this.defaultSizeY * d1;
   }
}
