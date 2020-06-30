package net.badlion.client.mods.render;

import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.events.event.RenderGame;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.BadlionGuiScreen;
import net.badlion.client.gui.slideout.Dropdown;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.ModPreviewRenderer;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.elements.ColorPicker;
import net.badlion.client.mods.render.RenderMod;
import net.badlion.client.mods.render.color.ModColor;
import net.badlion.client.mods.render.gui.BoxedCoord;
import net.badlion.client.util.ImageDimension;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import org.lwjgl.opengl.GL11;

public class Saturation extends RenderMod {
   private ModColor textColor = new ModColor(-1);
   private ModColor backgroundColor = new ModColor(-5592321);
   private Saturation.DisplayMode displayMode = Saturation.DisplayMode.ICON;
   private transient Dropdown typeDropdown;

   public Saturation() {
      super("Saturation", 9, 71, 80, 8, false);
      this.iconDimension = new ImageDimension(114, 114);
      this.defaultTopLeftBox = new BoxedCoord(23, 29, 0.2294820717131474D, 0.21092278719397364D);
      this.defaultCenterBox = new BoxedCoord(24, 29, 0.4589641434262948D, 0.4218455743879473D);
      this.defaultBottomRightBox = new BoxedCoord(25, 29, 0.6799468791500664D, 0.647834274952919D);
   }

   public void reset() {
      super.reset();
      this.textColor.setColor(-1);
      this.textColor.setMode(ModColor.DynamicColorMode.STATIC);
      this.backgroundColor.setColor(-5592321);
      this.backgroundColor.setEnabled(true);
      this.backgroundColor.setMode(ModColor.DynamicColorMode.STATIC);
      this.displayMode = Saturation.DisplayMode.ICON;
      this.defaultSizeX = this.displayMode.x;
      this.defaultSizeY = this.displayMode.y;
   }

   public void init() {
      this.setDisplayName("Saturation");
      this.setFontOffset(0.016D);
      this.registerEvent(EventType.RENDER_GAME);
      super.init();
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 12));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new ModPreviewRenderer(this, 2, 1));
      this.slideCogMenu.addElement(new Label("Settings", -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));
      this.slideCogMenu.addElement(this.typeDropdown = new Dropdown(new String[]{"Icon", "Text"}, this.displayMode.equals(Saturation.DisplayMode.ICON)?0:1, 0.19D));
      this.slideCogMenu.addElement(new ColorPicker("Text Color", this.textColor, 0.13D));
      this.slideCogMenu.addElement(new ColorPicker("Background Color", this.backgroundColor, 0.13D, true));
      this.textColor.init();
      this.backgroundColor.init();
      this.defaultSizeY = this.displayMode.y;
      this.defaultSizeX = this.displayMode.x;
      super.createCogMenu();
   }

   public void onEvent(Event event) {
      if(event instanceof MotionUpdate) {
         Saturation.DisplayMode saturation$displaymode = this.typeDropdown.getValue().equals("Icon")?Saturation.DisplayMode.ICON:Saturation.DisplayMode.NUMBER;
         if(saturation$displaymode != this.displayMode) {
            this.displayMode = saturation$displaymode;
            int i = this.defaultSizeX;
            int j = this.defaultSizeY;
            this.defaultSizeX = this.displayMode.x;
            this.defaultSizeY = this.displayMode.y;
            this.sizeX = (double)((int)(this.sizeX / (double)i * (double)this.defaultSizeX));
            this.sizeY = (double)((int)(this.sizeY / (double)j * (double)this.defaultSizeY));
            Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().initPages();
         }

         this.backgroundColor.tickColor();
         this.textColor.tickColor();
      }

      if(event instanceof RenderGame && this.isEnabled()) {
         int j1 = 0;
         int k1 = 0;
         this.beginRender();
         if(this.displayMode == Saturation.DisplayMode.ICON) {
            this.gameInstance.getTextureManager().bindTexture(Gui.icons);
            int i2 = (int)this.gameInstance.thePlayer.getFoodStats().getSaturationLevel();
            if(Wrapper.getInstance().getActiveModProfile().getModConfigurator().isInEditingMode()) {
               i2 = 20;
            }

            if(i2 > 1) {
               int j2 = i2 / 2;
               int l = j1 + 8 * j2 + 3;
               if(l > this.defaultSizeX) {
                  l = this.defaultSizeX;
               }

               GuiIngame.drawRect(j1, k1, l, this.defaultSizeY, this.backgroundColor.getColorInt());

               for(int i1 = 0; i1 < j2; ++i1) {
                  GL11.glColor4f(0.65F, 0.85F, 0.85F, 1.0F);
                  BadlionGuiScreen.drawTexturedModalRect(j1 + 1 + 8 * i1, k1 + 1, 16, 27, 9, 9, 1);
                  BadlionGuiScreen.drawTexturedModalRect(j1 + 1 + 8 * i1, k1 + 1, 52, 27, 9, 9, 1);
               }
            }
         } else {
            int l1 = (int)this.gameInstance.thePlayer.getFoodStats().getSaturationLevel();
            if(Wrapper.getInstance().getActiveModProfile().getModConfigurator().isInEditingMode()) {
               l1 = 20;
            }

            int k = this.gameInstance.fontRendererObj.getStringWidth(String.valueOf(l1)) + 2;
            if(k > this.defaultSizeX) {
               k = this.defaultSizeX;
            }

            GuiIngame.drawRect(j1, k1, k, this.defaultSizeY, this.backgroundColor.getColorInt());
            this.gameInstance.fontRendererObj.drawStringWithShadow(String.valueOf(l1), 1.0F, 1.0F, this.textColor.getColorInt());
         }

         this.endRender();
      }

      super.onEvent(event);
   }

   public static enum DisplayMode {
      ICON(83, 11),
      NUMBER(14, 10);

      public int x;
      public int y;

      private DisplayMode(int x, int y) {
         this.x = x;
         this.y = y;
      }
   }
}
