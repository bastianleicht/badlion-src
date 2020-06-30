package net.badlion.client.mods.render;

import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.lwjgl.opengl.GL11;

public class ShowFPS extends RenderMod {
   private ModColor labelColor = new ModColor(-1);
   private ModColor backgroundColor = new ModColor(-1289213133);
   private MutableBoolean fancyFont = new MutableBoolean(false);
   private transient TextButton defaultFontButton;
   private MutableBoolean changed = new MutableBoolean(true);

   public ShowFPS() {
      super("ShowFPS", -110, -111, 46, 12);
      this.iconDimension = new ImageDimension(114, 70);
      this.defaultTopLeftBox = new BoxedCoord(30, 4, 0.26666666666666666D, 0.8D);
      this.defaultCenterBox = new BoxedCoord(31, 5, 0.16666666666666666D, 0.2074074074074074D);
      this.defaultBottomRightBox = new BoxedCoord(32, 5, 0.06666666666666667D, 0.6222222222222222D);
   }

   public void reset() {
      this.labelColor = new ModColor(-1);
      this.backgroundColor = new ModColor(-1289213133);
      this.defaultFontButton.setEnabled(false);
      this.fancyFont.setValue(false);
      this.updateSize();
      super.reset();
   }

   public void init() {
      this.setDisplayName("Show FPS");
      this.setFontOffset(0.007D);
      this.registerEvent(EventType.RENDER_GAME);
      this.registerEvent(EventType.MOTION_UPDATE);
      super.init();
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 8));
      this.slideCogMenu.addElement(new ModPreviewRenderer(this, 0, 0));
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
      } else if(event instanceof GUIClickMouse) {
         if(((GUIClickMouse)event).getMouseButton() == 0) {
            int i = Wrapper.getInstance().getMouseX();
            int j = Wrapper.getInstance().getMouseY();
            if(i > this.defaultFontButton.getX() && i < this.defaultFontButton.getX() + this.defaultFontButton.getWidth() && j > this.defaultFontButton.getY() && j < this.defaultFontButton.getY() + this.defaultFontButton.getHeight()) {
               this.changed.setValue(true);
            }
         }
      } else if(event instanceof RenderGame && this.shouldDisplayFPS()) {
         RenderGame rendergame = (RenderGame)event;
         new ScaledResolution(this.gameInstance);
         int k = 2;
         if(this.changed.isTrue()) {
            this.updateSize();
            this.changed.setValue(false);
         }

         this.beginRender();
         int l = 0;
         int i1 = 0;
         int k1 = 8 + k * 2;
         int l1;
         if(Wrapper.getInstance().getActiveModProfile().getModConfigurator().isInEditingMode()) {
            l1 = 999;
         } else {
            l1 = Minecraft.getDebugFPS();
         }

         if(!this.fancyFont.getValue().booleanValue()) {
            int i2 = rendergame.getGameRenderer().getFontRenderer().getStringWidth("FPS: " + l1);
            int j1 = l + i2 + k * 2;
            Gui.drawRect(l, i1, j1, k1, this.backgroundColor.getColorInt());
            rendergame.getGameRenderer().drawString(this.gameInstance.fontRendererObj, "FPS: " + l1, k, k, this.labelColor.getColorInt());
         } else {
            int k2 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth("FPS: " + l1, 12, BadlionFontRenderer.FontType.TITLE);
            int j2 = k2 + k * 2;
            Gui.drawRect(l, i1, j2, k1, this.backgroundColor.getColorInt());
            ColorUtil.bindColor(this.labelColor.getColor());
            Wrapper.getInstance().getBadlionFontRenderer().drawString(k, k - 2, "FPS: " + l1, 12, BadlionFontRenderer.FontType.TITLE, true);
         }

         this.endRender();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      super.onEvent(event);
   }

   public boolean shouldDisplayFPS() {
      return this.isEnabled();
   }

   private void updateSize() {
      int i = 46;
      int j = 12;
      if(this.fancyFont.isTrue()) {
         i -= 8;
      }

      double d0 = this.getScaleX();
      double d1 = this.getScaleY();
      this.defaultSizeX = i;
      this.defaultSizeY = j;
      this.sizeX = (double)this.defaultSizeX * d0;
      this.sizeY = (double)this.defaultSizeY * d1;
   }
}
