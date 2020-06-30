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
import org.lwjgl.opengl.GL11;

public class ShowCPS extends RenderMod {
   private transient List delays = new ArrayList();
   private ModColor labelColor = new ModColor(-1);
   private ModColor backgroundColor = new ModColor(-1289213133);
   private MutableBoolean fancyFont = new MutableBoolean(false);
   private transient TextButton defaultFontButton;
   private MutableBoolean changed = new MutableBoolean(true);

   public ShowCPS() {
      super("ShowCPS", -110, -97, 40, 12);
      this.iconDimension = new ImageDimension(52, 77);
      this.defaultTopLeftBox = new BoxedCoord(30, 6, 0.5166666666666667D, 0.08888888888888889D);
      this.defaultCenterBox = new BoxedCoord(31, 6, 0.15D, 0.4740740740740741D);
      this.defaultBottomRightBox = new BoxedCoord(31, 6, 0.8D, 0.8888888888888888D);
   }

   public void init() {
      this.setDisplayName("Show CPS");
      this.setFontOffset(0.016D);
      this.registerEvent(EventType.CLICK_MOUSE);
      this.registerEvent(EventType.RENDER_GAME);
      this.registerEvent(EventType.MOTION_UPDATE);
      super.init();
   }

   public void reset() {
      this.labelColor = new ModColor(-1);
      this.backgroundColor = new ModColor(-1289213133);
      this.defaultFontButton.setEnabled(false);
      this.fancyFont.setValue(false);
      this.updateSize();
      super.reset();
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 8));
      this.slideCogMenu.addElement(new ModPreviewRenderer(this, 0, 1));
      this.slideCogMenu.addElement(new Label("Settings", -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));
      this.slideCogMenu.addElement(new ColorPicker("Text Color", this.labelColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Background Color", this.backgroundColor, 0.13D, true));
      this.defaultFontButton = new TextButton("Fancy Font", this.fancyFont, 1.0D);
      this.slideCogMenu.addElement(this.defaultFontButton);
      this.labelColor.init();
      this.backgroundColor.init();
      super.createCogMenu();
      this.updateSize();
   }

   public void onEvent(Event event) {
      if(event instanceof MotionUpdate) {
         this.labelColor.tickColor();
         this.backgroundColor.tickColor();
         List<Long> list = new ArrayList();
         Iterator mY = this.delays.iterator();

         while(mY.hasNext()) {
            long i = ((Long)mY.next()).longValue();
            if(System.currentTimeMillis() - i < 1000L) {
               list.add(Long.valueOf(i));
            }
         }

         this.delays = list;
      }

      if(event instanceof GUIClickMouse && ((GUIClickMouse)event).getMouseButton() == 0) {
         int k1 = Wrapper.getInstance().getMouseX();
         int l1 = Wrapper.getInstance().getMouseY();
         if(k1 > this.defaultFontButton.getX() && k1 < this.defaultFontButton.getX() + this.defaultFontButton.getWidth() && l1 > this.defaultFontButton.getY() && l1 < this.defaultFontButton.getY() + this.defaultFontButton.getHeight()) {
            this.changed.setValue(true);
         }
      }

      if(event instanceof ClickMouse) {
         ClickMouse clickmouse = (ClickMouse)event;
         if(clickmouse.getClickType() == 0) {
            this.delays.add(Long.valueOf(System.currentTimeMillis()));
         }
      }

      if(event instanceof RenderGame && this.isEnabled()) {
         RenderGame rendergame = (RenderGame)event;
         int i2 = 2;
         int j2 = 0;
         int j = 0;
         if(this.changed.isTrue()) {
            this.updateSize();
            this.changed.setValue(false);
         }

         this.beginRender();
         int l = 8 + i2 * 2;
         int i1;
         if(Wrapper.getInstance().getActiveModProfile().getModConfigurator().isInEditingMode()) {
            i1 = 99;
         } else {
            i1 = this.getCPS();
         }

         if(!this.fancyFont.getValue().booleanValue()) {
            int j1 = rendergame.getGameRenderer().getFontRenderer().getStringWidth("CPS: " + i1);
            int k = j2 + j1 + i2 * 2;
            Gui.drawRect(j2, j, k, l, this.backgroundColor.getColorInt());
            rendergame.getGameRenderer().drawString(this.gameInstance.fontRendererObj, "CPS: " + i1, j2 + i2, j + i2, this.labelColor.getColorInt());
         } else {
            int l2 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth("CPS: " + i1, 12, BadlionFontRenderer.FontType.TITLE);
            int k2 = j2 + l2 + i2 * 2;
            Gui.drawRect(j2, j, k2, l, this.backgroundColor.getColorInt());
            ColorUtil.bindColor(this.labelColor.getColor());
            Wrapper.getInstance().getBadlionFontRenderer().drawString(j2 + i2, j + (i2 - 2), "CPS: " + i1, 12, BadlionFontRenderer.FontType.TITLE, true);
         }

         this.endRender();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      super.onEvent(event);
   }

   public int getCPS() {
      return this.delays.size();
   }

   private void updateSize() {
      int i = 40;
      int j = 12;
      if(this.fancyFont.isTrue()) {
         i -= 6;
      }

      double d0 = this.getScaleX();
      double d1 = this.getScaleY();
      this.defaultSizeX = i;
      this.defaultSizeY = j;
      this.sizeX = (double)this.defaultSizeX * d0;
      this.sizeY = (double)this.defaultSizeY * d1;
   }
}
