package net.badlion.client.mods.render;

import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.events.event.RenderGame;
import net.badlion.client.gui.BadlionFontRenderer;
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
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ShowDirection extends RenderMod {
   private ModColor textColor = new ModColor(-1);
   private ModColor backgroundColor = new ModColor(-1289213133);
   private ShowDirection.DirectionType directionType = ShowDirection.DirectionType.COMPASS;
   private boolean drawBackground = false;
   private transient Dropdown typeDropdown;
   private transient ResourceLocation hudLocation;
   protected static final transient ResourceLocation compassNoBackground = new ResourceLocation("textures/gui/compass_with_background.png");
   protected static final transient ResourceLocation compassWithBackground = new ResourceLocation("textures/gui/compass_with_background.png");
   protected static final transient ResourceLocation compassTriangle = new ResourceLocation("textures/gui/compass_triangle.png");

   public void reset() {
      this.textColor = new ModColor(-1);
      this.backgroundColor = new ModColor(-1289213133);
      this.directionType = ShowDirection.DirectionType.COMPASS;
      this.defaultSizeX = 150;
      this.defaultSizeY = 18;
      super.reset();
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 8));
      if(this.directionType.equals(ShowDirection.DirectionType.COMPASS)) {
         this.slideCogMenu.addElement(new ModPreviewRenderer(this, -8, 1));
      } else {
         this.slideCogMenu.addElement(new ModPreviewRenderer(this, 0, 0));
      }

      this.slideCogMenu.addElement(new Label("Settings", -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));
      if(this.directionType == ShowDirection.DirectionType.SIMPLE) {
         this.slideCogMenu.addElement(new ColorPicker("Text Color", this.textColor, 0.13D));
         this.slideCogMenu.addElement(new ColorPicker("Background Color", this.backgroundColor, 0.13D, true));
      }

      this.slideCogMenu.addElement(this.typeDropdown = new Dropdown(new String[]{"Compass", "Simple"}, this.directionType.equals(ShowDirection.DirectionType.COMPASS)?0:1, 0.19D));
      this.textColor.init();
      this.backgroundColor.init();
      if(this.directionType.equals(ShowDirection.DirectionType.COMPASS)) {
         this.defaultSizeX = 150;
         this.defaultSizeY = 18;
      } else {
         this.defaultSizeX = 17;
         this.defaultSizeY = 18;
      }

      super.createCogMenu();
   }

   public ShowDirection() {
      super("ShowDirection", 206, -126, 150, 18);
      this.iconDimension = new ImageDimension(77, 77);
      this.defaultTopLeftBox = new BoxedCoord(12, 0, 0.8833333333333333D, 0.0D);
      this.defaultCenterBox = new BoxedCoord(15, 0, 0.9666666666666667D, 0.6518518518518519D);
      this.defaultBottomRightBox = new BoxedCoord(19, 1, 0.05D, 0.2962962962962963D);
   }

   public void init() {
      this.offsetX = 1;
      this.setFontOffset(0.0D);
      this.setDisplayName("ShowDirection");
      this.registerEvent(EventType.RENDER_GAME);
      this.registerEvent(EventType.MOTION_UPDATE);
      super.init();
   }

   public void onEvent(Event e) {
      if(e instanceof MotionUpdate) {
         ShowDirection.DirectionType showdirection$directiontype = this.typeDropdown.getValue().equals("Compass")?ShowDirection.DirectionType.COMPASS:ShowDirection.DirectionType.SIMPLE;
         if(!showdirection$directiontype.equals(this.directionType)) {
            this.directionType = showdirection$directiontype;
            int i = this.defaultSizeX;
            int j = this.defaultSizeY;
            if(this.directionType.equals(ShowDirection.DirectionType.COMPASS)) {
               this.defaultSizeX = 150;
               this.defaultSizeY = 18;
            } else {
               this.defaultSizeX = 17;
               this.defaultSizeY = 18;
            }

            this.sizeX = (double)((int)(this.sizeX / (double)i * (double)this.defaultSizeX));
            this.sizeY = (double)((int)(this.sizeY / (double)j * (double)this.defaultSizeY));
            Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().initPages();
         }

         this.textColor.tickColor();
         this.backgroundColor.tickColor();
      }

      if(e instanceof RenderGame && this.shouldDisplayDirection()) {
         RenderGame rendergame = (RenderGame)e;
         int k1 = 0;
         int l1 = 0;
         GL11.glPushMatrix();
         GL11.glScaled(2.0D / (double)Wrapper.getInstance().getScaleFactor(), 2.0D / (double)Wrapper.getInstance().getScaleFactor(), 1.0D);
         GL11.glScaled(0.5D, 0.5D, 1.0D);
         GL11.glScaled(this.getScaleX(), this.getScaleY(), 1.0D);
         GL11.glTranslatef((float)((double)this.getX() / this.getScaleX()), (float)((double)this.getY() / this.getScaleY()), 0.0F);
         if(this.directionType.equals(ShowDirection.DirectionType.SIMPLE)) {
            Gui.drawRect((int)((double)k1 / this.getScaleX()), (int)((double)l1 / this.getScaleY()), (int)((double)k1 / this.getScaleX()) + 17, (int)((double)l1 / this.getScaleY()) + 18, this.backgroundColor.getColorInt());
            String s = ShowDirection.Direction.values()[MathHelper.floor_double((double)(this.gameInstance.thePlayer.rotationYaw * 4.0F / 180.0F) + 0.5D) & 7].name();
            double d0 = s.length() == 1?1.52D:0.9D;
            int l = d0 == 1.52D?0:1;
            int i1 = d0 == 1.52D?0:4;
            GL11.glScaled(d0, d0, d0);
            this.gameInstance.fontRendererObj.drawString(s, (int)((double)k1 / d0 / this.getScaleX() + 3.0D) + l, i1 + (int)(2.0D + (double)l1 / d0 / this.getScaleY()), this.textColor.getColorInt());
            GL11.glScaled(1.0D / d0, 1.0D / d0, 1.0D / d0);
         } else if(this.directionType.equals(ShowDirection.DirectionType.COMPASS)) {
            GL11.glEnable(3042);
            this.defaultSizeX = 150;
            this.defaultSizeY = 18;
            int i2 = (int)((double)k1 / this.getScaleX());
            int j2 = (int)((double)l1 / this.getScaleY());
            int k = 150;
            int k2 = 37;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if(this.drawBackground) {
               Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(compassWithBackground);
            } else {
               Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(compassNoBackground);
            }

            float f = this.gameInstance.thePlayer.rotationYaw + 180.0F;
            Gui.drawModalRectWithCustomSizedTexture(i2, j2, f - (float)(k / 2), (float)(k2 / 2), k, k2 / 2, 360.0F, (float)(k2 / 2));
            int j1 = 13;
            Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(compassTriangle);
            Gui.drawModalRectWithCustomSizedTexture(i2 + k / 2 - j1 / 2, j2 - 5, (float)j1, 12.0F, j1, 12, 13.0F, 12.0F);
            GL11.glDisable(3042);
         }

         GL11.glPopMatrix();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      super.onEvent(e);
   }

   public boolean shouldDisplayDirection() {
      return this.isEnabled();
   }

   public static enum Direction {
      S,
      SW,
      W,
      NW,
      N,
      NE,
      E,
      SE;
   }

   private static enum DirectionType {
      COMPASS,
      SIMPLE;
   }
}
