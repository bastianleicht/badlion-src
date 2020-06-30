package net.minecraft.client.gui;

import java.io.IOException;
import net.badlion.client.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiShareToLan;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiIngameMenu extends GuiScreen {
   private int field_146445_a;
   private int field_146444_f;
   private static final ResourceLocation menuHeader = new ResourceLocation("textures/menu/esc/menu-header.svg_large.png");
   private int hideTimer = -45;

   public void initGui() {
      this.field_146445_a = 0;
      this.buttonList.clear();
      byte b0 = -16;
      boolean flag = true;
      int i = this.width / 2 - 100;
      int j = this.width / 2 + 5;
      int k = this.height / 2 - 81 + 33;
      this.buttonList.add(new GuiButton(1, j, k, 95, 20, I18n.format("menu.returnToMenu", new Object[0]).substring(0, 13), GuiButton.ButtonType.RED));
      this.buttonList.add(new GuiButton(4, i, k, 95, 20, I18n.format("menu.returnToGame", new Object[0]), GuiButton.ButtonType.THICK_LINES));
      k = k + 30;
      if(!this.mc.isIntegratedServerRunning()) {
         ((GuiButton)this.buttonList.get(0)).displayString = I18n.format("menu.disconnect", new Object[0]);
      }

      this.buttonList.add(new GuiButton(10, i, k, "Badlion"));
      k = k + 25;
      this.buttonList.add(new GuiButton(11, i, k, "Server List"));
      k = k + 25;
      this.buttonList.add(new GuiButton(5, i, k, 95, 20, I18n.format("gui.achievements", new Object[0])));
      this.buttonList.add(new GuiButton(6, j, k, 95, 20, I18n.format("gui.stats", new Object[0])));
      k = k + 25;
      this.buttonList.add(new GuiButton(0, i, k, 95, 20, I18n.format("menu.options", new Object[0])));
      GuiButton guibutton;
      this.buttonList.add(guibutton = new GuiButton(7, j, k, 95, 20, I18n.format("menu.shareToLan", new Object[0])));
      this.buttonList.add(new GuiButton(12, 2, this.height - 22, 110, 20, "Reload Resources"));
      guibutton.enabled = this.mc.isSingleplayer() && !this.mc.getIntegratedServer().getPublic();
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      switch(button.id) {
      case 0:
         this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
         break;
      case 1:
         boolean flag = this.mc.isIntegratedServerRunning();
         boolean flag1 = this.mc.func_181540_al();
         button.enabled = false;
         this.mc.theWorld.sendQuittingDisconnectingPacket();
         this.mc.loadWorld((WorldClient)null);
         if(flag) {
            this.mc.displayGuiScreen(new GuiMainMenu());
         } else if(flag1) {
            RealmsBridge realmsbridge = new RealmsBridge();
            realmsbridge.switchToRealms(new GuiMainMenu());
         } else {
            this.mc.displayGuiScreen(new GuiMultiplayer(new GuiMainMenu()));
         }
      case 2:
      case 3:
      case 8:
      case 9:
      default:
         break;
      case 4:
         this.mc.displayGuiScreen((GuiScreen)null);
         this.mc.setIngameFocus();
         break;
      case 5:
         this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
         break;
      case 6:
         this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
         break;
      case 7:
         this.mc.displayGuiScreen(new GuiShareToLan(this));
         break;
      case 10:
         Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().toggle();
         break;
      case 11:
         this.mc.displayGuiScreen(new GuiMultiplayer(this));
         break;
      case 12:
         Minecraft minecraft = this.mc;
         this.hideTimer = Minecraft.getDebugFPS();
      }

   }

   public void updateScreen() {
      if(this.hideTimer <= -40) {
         super.updateScreen();
         ++this.field_146444_f;
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if(this.hideTimer-- > -40) {
         Gui.drawRect(0, 0, this.width, this.height, -16777216);
         this.drawDefaultBackground();
         if(this.hideTimer < 60 && this.hideTimer > 0) {
            this.mc.refreshResources();
            this.mc.renderGlobal.loadRenderers();
            this.hideTimer = 0;
         }

         this.drawCenteredString(this.fontRendererObj, "Reloading Resource Manager...", this.width / 2, this.height / 2 - 30, -1);
      } else {
         this.drawDefaultBackground();
         int i = 220;
         int j = 175;
         int k = this.width / 2 - i / 2;
         int l = this.height / 2 - j / 2;
         int i1 = this.width / 2 + i / 2;
         int j1 = l + j;
         Gui.drawRect(k, l + 33, i1, j1, -685233615);
         GL11.glEnable(3042);
         OpenGlHelper.glBlendFunc(770, 771, 0, 1);
         GL11.glDisable(3008);
         GL11.glShadeModel(7425);
         GL11.glDisable(3553);
         Tessellator tessellator = Tessellator.getInstance();
         WorldRenderer worldrenderer = tessellator.getWorldRenderer();
         worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
         worldrenderer.pos((double)k, (double)(l + 38), 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 0).endVertex();
         worldrenderer.pos((double)i1, (double)(l + 38), 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 0).endVertex();
         worldrenderer.pos((double)i1, (double)(l + 30), 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         worldrenderer.pos((double)k, (double)(l + 30), 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         tessellator.draw();
         GL11.glEnable(3553);
         GL11.glShadeModel(7424);
         GL11.glEnable(3008);
         GL11.glDisable(3042);
         Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(menuHeader);
         GL11.glColor3d(1.0D, 1.0D, 1.0D);
         Gui.drawModalRectWithCustomSizedTexture(k, l, 0.0F, 0.0F, i, 33, (float)i, 33.0F);
         this.drawString(this.mc.fontRendererObj, "v" + Wrapper.getVersion(), this.width - this.fontRendererObj.getStringWidth("v" + Wrapper.getVersion()), this.height - 10, -1);
         super.drawScreen(mouseX, mouseY, partialTicks);
      }

   }
}
