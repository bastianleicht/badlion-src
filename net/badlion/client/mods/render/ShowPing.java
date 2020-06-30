package net.badlion.client.mods.render;

import java.awt.Component;
import javax.swing.JOptionPane;
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
import net.badlion.client.util.ColorUtil;
import net.badlion.client.util.ImageDimension;
import net.badlion.client.util.PingWorker;
import net.badlion.client.util.PingerCallable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ShowPing extends RenderMod {
   private transient ResourceLocation pingTexture = new ResourceLocation("textures/slideout/mods/white_ping.png");
   private ModColor labelColor = new ModColor(-1);
   private ModColor backgroundColor = new ModColor(-1289213133);
   private ModColor foregroundColor = new ModColor(-16711903);
   private ShowPing.PingModMode mode = ShowPing.PingModMode.ICON;
   private transient int ping;
   private transient Thread workerThread;
   private transient PingWorker worker;
   private transient PingerCallable pinger;
   private transient Dropdown pingModModeDropdown;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$badlion$client$mods$render$ShowPing$PingModMode;

   public ShowPing() {
      super("ShowPing", 205, -101, 20, 15);
      this.iconDimension = new ImageDimension(83, 74);
      this.defaultTopLeftBox = new BoxedCoord(30, 7, 0.8D, 0.5037037037037037D);
      this.defaultCenterBox = new BoxedCoord(31, 8, 0.15D, 0.14814814814814814D);
      this.defaultBottomRightBox = new BoxedCoord(31, 8, 0.5D, 0.7703703703703704D);
   }

   public void init() {
      this.setDisplayName("Show Ping");
      this.setFontOffset(0.008D);
      this.worker = new PingWorker();
      this.workerThread = new Thread(this.worker);
      this.workerThread.start();
      this.registerEvent(EventType.RENDER_GAME);
      this.registerEvent(EventType.MOTION_UPDATE);
      super.init();
   }

   public void reset() {
      this.labelColor = new ModColor(-1);
      this.backgroundColor = new ModColor(-1289213133);
      this.foregroundColor = new ModColor(-16711903);
      this.mode = ShowPing.PingModMode.ICON;
      this.defaultSizeX = 15;
      this.defaultSizeY = 15;
      super.reset();
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
      this.slideCogMenu.addElement(new ColorPicker("Foreground Color", this.foregroundColor, 0.13D));
      this.slideCogMenu.addElement(this.pingModModeDropdown = new Dropdown(new String[]{"Icon", "Text"}, this.mode.equals(ShowPing.PingModMode.ICON)?0:1, 0.19D));
      this.labelColor.init();
      this.backgroundColor.init();
      this.defaultSizeX = this.mode.x;
      this.defaultSizeY = this.mode.y;
      super.createCogMenu();
   }

   private void drawPing(GuiIngame gameRenderer, int p_175245_1_, int xIn, int y) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(this.pingTexture);
      int i = 0;
      int k = this.ping;
      int j;
      if(k < 0) {
         j = 5;
      } else if(k < 150) {
         j = 0;
      } else if(k < 300) {
         j = 1;
      } else if(k < 600) {
         j = 2;
      } else if(k < 1000) {
         j = 3;
      } else {
         j = 4;
      }

      int l = xIn + p_175245_1_ - 12;
      ColorUtil.bindColor(this.foregroundColor.getColor());
      Gui.drawModalRectWithCustomSizedTexture(l, y, (float)(0 + i * 10), (float)(0 + j * 8), 10, 8, 10.0F, 40.0F);
   }

   public void onEvent(Event e) {
      if(e instanceof MotionUpdate) {
         if(!Wrapper.getInstance().checkVerify()) {
            JOptionPane.showMessageDialog((Component)null, "Please use the Badlion launcher!  If you are seeing this error constantly, please fully restart the Badlion Client and launcher.", "Badlion Client", 0);
            System.exit(0);
         }

         if(Wrapper.getInstance().getCurrentConnection() == null) {
            this.worker.setCallable((PingerCallable)null);
            this.worker.setCurrentConnection((String)null);
         }

         if(this.worker != null && this.worker.getCurrentConnection() == null && (this.worker.getCallable() == null && Wrapper.getInstance().getCurrentConnection() != null || this.worker.getCallable() != null && Wrapper.getInstance().getCurrentConnection() != null && !this.worker.getCallable().getAddress().toString().contains(Wrapper.getInstance().getCurrentConnection()))) {
            this.worker.setCurrentConnection(Wrapper.getInstance().getCurrentConnection());
         }

         if(!this.workerThread.isAlive()) {
            this.workerThread.start();
         }

         this.ping = this.worker.getPing();
         ShowPing.PingModMode showping$pingmodmode = this.pingModModeDropdown.getValue().equals("Icon")?ShowPing.PingModMode.ICON:ShowPing.PingModMode.TEXT;
         if(!showping$pingmodmode.equals(this.mode)) {
            this.mode = showping$pingmodmode;
            int i = this.defaultSizeX;
            int j = this.defaultSizeY;
            this.defaultSizeX = showping$pingmodmode.x;
            this.defaultSizeY = showping$pingmodmode.y;
            this.sizeX = (double)((int)(this.sizeX / (double)i * (double)this.defaultSizeX));
            this.sizeY = (double)((int)(this.sizeY / (double)j * (double)this.defaultSizeY));
            Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().initPages();
         }

         this.labelColor.tickColor();
         this.backgroundColor.tickColor();
         this.foregroundColor.tickColor();
      }

      if(e instanceof RenderGame && this.shouldDisplayPing()) {
         RenderGame rendergame = (RenderGame)e;
         int l2 = this.ping;
         if(Wrapper.getInstance().getActiveModProfile().getModConfigurator().isInEditingMode()) {
            l2 = 999;
         }

         switch($SWITCH_TABLE$net$badlion$client$mods$render$ShowPing$PingModMode()[this.mode.ordinal()]) {
         case 1:
            int i3 = 1;
            int k = 1;
            this.beginRender();
            int l = 0;
            int i1 = 0;
            int j1 = this.getDefaultSizeX();
            int k1 = this.getDefaultSizeY();
            Gui.drawRect(l, i1, j1, k1, this.backgroundColor.getColorInt());
            this.drawPing(rendergame.getGameRenderer(), 14, 3, 1);
            GL11.glScaled(0.5D, 0.5D, 1.0D);
            if(l2 < 0) {
               rendergame.getGameRenderer().drawString(this.gameInstance.fontRendererObj, "Unknown Ping", (int)((double)i3 / 0.5D) - "Unknown Ping".length(), (int)((double)k / 0.5D) + 19, this.labelColor.getColorInt());
            } else {
               rendergame.getGameRenderer().drawString(this.gameInstance.fontRendererObj, l2 + " ms", (int)((double)i3 / 0.5D) + (l2 + " ms").length() / 2 / 2, (int)((double)k / 0.5D + 18.0D), this.labelColor.getColorInt());
            }

            GL11.glScaled(1.0D / this.getScaleX(), 1.0D / this.getScaleY(), 1.0D);
            this.endRender();
            break;
         case 2:
            this.beginRender();
            int l1 = 0;
            int i2 = 0;
            int j2 = l1 + rendergame.getGameRenderer().getFontRenderer().getStringWidth("Ping: " + l2 + " ms ");
            int k2 = this.getDefaultSizeY();
            Gui.drawRect(l1, i2, j2, k2, this.backgroundColor.getColorInt());
            rendergame.getGameRenderer().drawString(this.gameInstance.fontRendererObj, "Ping: " + l2 + " ms", 2, 2, this.labelColor.getColorInt());
            this.endRender();
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      super.onEvent(e);
   }

   public boolean shouldDisplayPing() {
      return this.isEnabled();
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$badlion$client$mods$render$ShowPing$PingModMode() {
      int[] var10000 = $SWITCH_TABLE$net$badlion$client$mods$render$ShowPing$PingModMode;
      if($SWITCH_TABLE$net$badlion$client$mods$render$ShowPing$PingModMode != null) {
         return var10000;
      } else {
         int[] var0 = new int[ShowPing.PingModMode.values().length];

         try {
            var0[ShowPing.PingModMode.ICON.ordinal()] = 1;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[ShowPing.PingModMode.TEXT.ordinal()] = 2;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$badlion$client$mods$render$ShowPing$PingModMode = var0;
         return var0;
      }
   }

   private static enum PingModMode {
      ICON(20, 15),
      TEXT(64, 12);

      transient int x;
      transient int y;

      private PingModMode(int x, int y) {
         this.x = x;
         this.y = y;
      }
   }
}
