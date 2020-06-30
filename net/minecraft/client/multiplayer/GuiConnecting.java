package net.minecraft.client.multiplayer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;
import net.badlion.client.Wrapper;
import net.badlion.client.util.ColorUtil;
import net.badlion.client.util.ConnectWorker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

public class GuiConnecting extends GuiScreen {
   private static final AtomicInteger CONNECTION_ID = new AtomicInteger(0);
   private static final Logger logger = LogManager.getLogger();
   private NetworkManager networkManager;
   private boolean cancel;
   public final GuiScreen previousGuiScreen;
   private String message;
   private int dots;
   private int frameTimer;
   private static final ResourceLocation badlionLogo = new ResourceLocation("textures/gui/badlion-logo-large.png");
   private float start1 = 0.0F;
   private float end1 = 0.0F;
   private float end2 = 0.0F;
   private int index = 0;
   private float incrAmount = 0.1F;
   private float length = 1.5F;
   private boolean error;
   private String ip;
   private int port;
   private boolean connecting;
   private ConnectWorker worker;
   private GuiScreen screenError;

   public GuiConnecting(GuiScreen p_i1181_1_, Minecraft mcIn, ServerData p_i1181_3_) {
      this.mc = mcIn;
      this.previousGuiScreen = p_i1181_1_;
      ServerAddress serveraddress = ServerAddress.func_78860_a(p_i1181_3_.serverIP);
      mcIn.loadWorld((WorldClient)null);
      mcIn.setServerData(p_i1181_3_);
      this.worker = new ConnectWorker();
      (new Thread(this.worker)).start();
      this.connect(serveraddress.getIP(), serveraddress.getPort());
   }

   public GuiConnecting(GuiScreen p_i1182_1_, Minecraft mcIn, String hostName, int port) {
      this.mc = mcIn;
      this.previousGuiScreen = p_i1182_1_;
      mcIn.loadWorld((WorldClient)null);
      this.worker = new ConnectWorker();
      (new Thread(this.worker)).start();
      this.connect(hostName, port);
   }

   public void connect(final String ip, final int port) {
      logger.info("Connecting to " + ip + ", " + port);
      (new Thread("Server Connector #" + CONNECTION_ID.incrementAndGet()) {
         private static final String __OBFID = "CL_00000686";

         public void run() {
            InetAddress var1 = null;

            try {
               if(GuiConnecting.this.cancel) {
                  return;
               }

               var1 = InetAddress.getByName(ip);
               GuiConnecting.this.networkManager = NetworkManager.func_181124_a(var1, port, GuiConnecting.this.mc.gameSettings.func_181148_f());
               GuiConnecting.this.networkManager.setNetHandler(new NetHandlerLoginClient(GuiConnecting.this.networkManager, GuiConnecting.this.mc, GuiConnecting.this.previousGuiScreen));
               GuiConnecting.this.networkManager.sendPacket(new C00Handshake(47, ip, port, EnumConnectionState.LOGIN));
               GuiConnecting.this.networkManager.sendPacket(new C00PacketLoginStart(GuiConnecting.this.mc.getSession().getProfile()));
            } catch (UnknownHostException var5) {
               if(GuiConnecting.this.cancel) {
                  return;
               }

               GuiConnecting.logger.error((String)"Couldn\'t connect to server", (Throwable)var5);
               GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[]{"Unknown host"})));
            } catch (Exception var6) {
               if(GuiConnecting.this.cancel) {
                  return;
               }

               GuiConnecting.logger.error((String)"Couldn\'t connect to server", (Throwable)var6);
               String var3 = var6.toString();
               if(var1 != null) {
                  String var4 = var1.toString() + ":" + port;
                  var3 = var3.replaceAll(var4, "");
               }

               GuiConnecting.this.mc.displayGuiScreen(new GuiDisconnected(GuiConnecting.this.previousGuiScreen, "connect.failed", new ChatComponentTranslation("disconnect.genericReason", new Object[]{var3})));
            }

         }
      }).start();
   }

   public void updateScreen() {
      this.tickLoadingIcon();
      if(this.networkManager != null) {
         if(this.networkManager.isChannelOpen()) {
            this.networkManager.processReceivedPackets();
         } else {
            this.networkManager.checkDisconnected();
         }
      }

      if(this.screenError != null) {
         Minecraft.getMinecraft().displayGuiScreen(this.screenError);
      }

   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + 50, I18n.format("gui.cancel", new Object[0])));
   }

   protected void actionPerformed(GuiButton button) throws IOException {
      if(button.id == 0) {
         this.cancel = true;
         if(this.networkManager != null) {
            this.networkManager.closeChannel(new ChatComponentText("Aborted"));
         }

         Wrapper wrapper = Wrapper.getInstance();
         wrapper.premium = false;
         wrapper.currentPremiumConnection = null;
         wrapper.currentIp = null;
         wrapper.updateConnection(false);
         this.mc.displayGuiScreen(this.previousGuiScreen);
      }

   }

   public void setError(GuiDisconnected p_setError_1_) {
      this.message = "Problem when connecting to server";
      this.screenError = p_setError_1_;
   }

   public void setMessage(String p_setMessage_1_) {
      this.message = p_setMessage_1_;
   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      int i = this.width / 2 - 32;
      int j = this.height / 2 - 60;
      GlStateManager.pushMatrix();
      GlStateManager.enableBlend();
      GL11.glEnable(3042);
      GL11.glEnable(2848);
      GL11.glEnable(2881);
      GL11.glHint(3154, 4354);
      GL11.glHint(3155, 4354);
      GlStateManager.disableTexture2D();
      ColorUtil.bindHexColorRGBA(-15762023);
      GL11.glBegin(7);
      GL11.glVertex2f(0.0F, 0.0F);
      GL11.glVertex2f(0.0F, (float)this.height);
      GL11.glVertex2f((float)((double)this.width * 0.3574D), (float)this.height);
      GL11.glVertex2f((float)this.width * 0.0378F, 0.0F);
      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glBegin(7);
      GL11.glVertex2f((float)this.width * 0.0378F, 0.0F);
      GL11.glVertex2f((float)((double)this.width * 0.3574D), (float)this.height);
      GL11.glVertex2f((float)((double)this.width * 0.4141D), (float)this.height);
      GL11.glVertex2f((float)((double)this.width * 0.0946D), 0.0F);
      GL11.glEnd();
      ColorUtil.bindHexColorRGBA(-1988048);
      GL11.glBegin(7);
      GL11.glVertex2f((float)((double)this.width * 0.0946D), 0.0F);
      GL11.glVertex2f((float)((double)this.width * 0.4141D), (float)this.height);
      GL11.glVertex2f((float)((double)this.width * 0.6069D), (float)this.height);
      GL11.glVertex2f((float)((double)this.width * 0.2839D), 0.0F);
      GL11.glEnd();
      ColorUtil.bindHexColorRGBA(-14144717);
      GL11.glBegin(7);
      GL11.glVertex2f((float)((double)this.width * 0.2839D), 0.0F);
      GL11.glVertex2f((float)((double)this.width * 0.6069D), (float)this.height);
      GL11.glVertex2f((float)this.width, (float)this.height);
      GL11.glVertex2f((float)this.width, 0.0F);
      GL11.glEnd();
      GL11.glDisable(2929);
      OpenGlHelper.glBlendFunc(770, 771, 0, 1);
      GL11.glDisable(3008);
      GL11.glShadeModel(7425);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldrenderer.pos((double)((float)((double)this.width * 0.0946D)), 0.0D, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      worldrenderer.pos((double)((float)((double)this.width * 0.4141D)), (double)this.height, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      worldrenderer.pos((double)((float)((double)this.width * 0.4641D)), (double)this.height, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 0).endVertex();
      worldrenderer.pos((double)((float)((double)this.width * 0.1446D)), 0.0D, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 0).endVertex();
      tessellator.draw();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
      worldrenderer.pos((double)((float)((double)this.width * 0.2839D)), 0.0D, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      worldrenderer.pos((double)((float)((double)this.width * 0.6069D)), (double)this.height, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      worldrenderer.pos((double)((float)((double)this.width * 0.6569D)), (double)this.height, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 0).endVertex();
      worldrenderer.pos((double)((float)((double)this.width * 0.3339D)), 0.0D, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 0).endVertex();
      tessellator.draw();
      GL11.glShadeModel(7424);
      GL11.glEnable(3008);
      GL11.glDisable(2848);
      GL11.glDisable(2881);
      GlStateManager.enableAlpha();
      int k = 120;
      int l = 90;
      GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.5F);
      GL11.glBegin(7);
      GL11.glVertex2f((float)(this.width / 2 - k), (float)(this.height / 2 - l));
      GL11.glVertex2f((float)(this.width / 2 - k), (float)(this.height / 2 + l));
      GL11.glVertex2f((float)(this.width / 2 + k), (float)(this.height / 2 + l));
      GL11.glVertex2f((float)(this.width / 2 + k), (float)(this.height / 2 - l));
      GL11.glEnd();
      GL11.glDisable(3042);
      GlStateManager.enableTexture2D();
      GlStateManager.enableAlpha();
      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(badlionLogo);
      GL11.glColor3d(1.0D, 1.0D, 1.0D);
      Gui.drawModalRectWithCustomSizedTexture(this.width / 2 - 32, j, 64.0F, 64.0F, 64, 64, 64.0F, 64.0F);
      GlStateManager.disableAlpha();
      GlStateManager.disableTexture2D();

      for(int i1 = 0; i1 < 6; ++i1) {
         this.renderSideNormal(i1, i, j);
      }

      this.renderHexagonSide(this.index, this.start1, this.end1, i, j);
      if(this.end2 != 0.0F) {
         this.renderHexagonSide(this.index == 0?5:this.index - 1, 0.0F, this.end2, i, j);
      }

      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
      GlStateManager.popMatrix();
      if(this.message != null) {
         String s = "";

         for(int j1 = 0; j1 < this.dots; ++j1) {
            s = s + ".";
         }

         if(this.frameTimer++ > Minecraft.getDebugFPS() / 3) {
            if(++this.dots > 4) {
               this.dots = 0;
            }

            this.frameTimer = 0;
         }

         this.buttonList.clear();
         this.drawCenteredString(this.fontRendererObj, this.message + s, this.width / 2, this.height / 2 + 20, -1);
      } else {
         if(this.buttonList.size() == 0) {
            this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + 50, I18n.format("gui.cancel", new Object[0])));
         }

         if(this.networkManager == null) {
            this.drawCenteredString(this.fontRendererObj, I18n.format("connect.connecting", new Object[0]), this.width / 2, this.height / 2 + 20, 16777215);
         } else {
            this.drawCenteredString(this.fontRendererObj, I18n.format("connect.authorizing", new Object[0]), this.width / 2, this.height / 2 + 20, 16777215);
         }
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public void tickLoadingIcon() {
      this.incrAmount = 0.2F;
      if(this.end1 < 1.0F) {
         this.end1 += this.incrAmount;
      }

      if(this.end1 == 1.0F && this.start1 < 1.0F) {
         this.start1 += this.incrAmount;
         this.end2 += this.incrAmount;
      }

      if(this.end1 - this.start1 > this.length) {
         this.start1 += this.incrAmount;
      }

      if(this.start1 >= 1.0F) {
         if(this.index == 0) {
            this.index = 5;
         } else {
            --this.index;
         }

         this.end1 = this.end2;
         this.end2 = 0.0F;
         this.start1 = 0.0F;
      }

      if(this.end1 > 1.0F) {
         this.end2 = 0.0F;
         this.end1 = 1.0F;
      }

   }

   public void renderSideNormal(int p_renderSideNormal_1_, int p_renderSideNormal_2_, int p_renderSideNormal_3_) {
      int i = p_renderSideNormal_1_ == 5?0:p_renderSideNormal_1_ + 1;
      int j = p_renderSideNormal_2_ + 32;
      int k = p_renderSideNormal_3_ + 32;
      double d0 = 10.0D;
      double d1 = 32.0D + d0;
      double d2 = Math.sin((double)p_renderSideNormal_1_ / 6.0D * 2.0D * 3.141592653589793D) * d1 + (double)j;
      double d3 = Math.cos((double)p_renderSideNormal_1_ / 6.0D * 2.0D * 3.141592653589793D) * d1 + (double)k;
      double d4 = Math.sin((double)i / 6.0D * 2.0D * 3.141592653589793D) * d1 + (double)j;
      double d5 = Math.cos((double)i / 6.0D * 2.0D * 3.141592653589793D) * d1 + (double)k;
      double d6 = Math.sin((double)p_renderSideNormal_1_ / 6.0D * 2.0D * 3.141592653589793D) * (d1 - d0) + (double)j;
      double d7 = Math.cos((double)p_renderSideNormal_1_ / 6.0D * 2.0D * 3.141592653589793D) * (d1 - d0) + (double)k;
      double d8 = Math.sin((double)i / 6.0D * 2.0D * 3.141592653589793D) * (d1 - d0) + (double)j;
      double d9 = Math.cos((double)i / 6.0D * 2.0D * 3.141592653589793D) * (d1 - d0) + (double)k;
      GL11.glBegin(7);
      GlStateManager.color(0.20392157F, 0.21568628F, 0.24705882F, 1.0F);
      GL11.glVertex2d(d6, d7);
      GL11.glVertex2d(d2, d3);
      GL11.glVertex2d(d4, d5);
      GL11.glVertex2d(d8, d9);
      GL11.glEnd();
   }

   public void renderHexagonSide(int p_renderHexagonSide_1_, float p_renderHexagonSide_2_, float p_renderHexagonSide_3_, int p_renderHexagonSide_4_, int p_renderHexagonSide_5_) {
      int i = p_renderHexagonSide_1_ == 0?5:p_renderHexagonSide_1_ - 1;
      int j = p_renderHexagonSide_4_ + 32;
      int k = p_renderHexagonSide_5_ + 32;
      double d0 = 10.0D;
      double d1 = 32.0D + d0;
      double d2 = Math.sin((double)p_renderHexagonSide_1_ / 6.0D * 2.0D * 3.141592653589793D) * d1 + (double)j;
      double d3 = Math.cos((double)p_renderHexagonSide_1_ / 6.0D * 2.0D * 3.141592653589793D) * d1 + (double)k;
      double d4 = Math.sin((double)i / 6.0D * 2.0D * 3.141592653589793D) * d1 + (double)j;
      double d5 = Math.cos((double)i / 6.0D * 2.0D * 3.141592653589793D) * d1 + (double)k;
      double d6 = (d5 - d3) / (d4 - d2);
      double d7 = (d4 - d2) * (double)p_renderHexagonSide_3_;
      double d8 = (d4 - d2) * (double)p_renderHexagonSide_2_;
      if(p_renderHexagonSide_1_ != 2 && p_renderHexagonSide_1_ != 5) {
         d4 = d2 + d8;
         d5 = d6 * (d2 + d8 - d2) + d3;
         d2 += d7;
         d3 = d6 * (d2 + d7 - d2) + d3;
      } else {
         d5 = d3 + (d5 - d3) * (double)p_renderHexagonSide_3_;
         d3 = d3 + (d5 - d3) * (double)p_renderHexagonSide_2_;
      }

      double d9 = Math.sin((double)p_renderHexagonSide_1_ / 6.0D * 2.0D * 3.141592653589793D) * (d1 - d0) + (double)j;
      double d10 = Math.cos((double)p_renderHexagonSide_1_ / 6.0D * 2.0D * 3.141592653589793D) * (d1 - d0) + (double)k;
      double d11 = Math.sin((double)i / 6.0D * 2.0D * 3.141592653589793D) * (d1 - d0) + (double)j;
      double d12 = Math.cos((double)i / 6.0D * 2.0D * 3.141592653589793D) * (d1 - d0) + (double)k;
      double d13 = (d12 - d10) / (d11 - d9);
      double d14 = (d11 - d9) * (double)p_renderHexagonSide_3_;
      double d15 = (d11 - d9) * (double)p_renderHexagonSide_2_;
      if(p_renderHexagonSide_1_ != 2 && p_renderHexagonSide_1_ != 5) {
         d11 = d9 + d15;
         d12 = d13 * (d9 + d15 - d9) + d10;
         d9 += d14;
         d10 = d13 * (d9 + d14 - d9) + d10;
      } else {
         d12 = d10 + (d12 - d10) * (double)p_renderHexagonSide_3_;
         d10 = d10 + (d12 - d10) * (double)p_renderHexagonSide_2_;
      }

      GL11.glBegin(7);
      GlStateManager.color(1.0F, 0.8F, 0.32941177F, 1.0F);
      if(p_renderHexagonSide_1_ != 2 && p_renderHexagonSide_1_ != 5) {
         GL11.glVertex2d(d2, d3);
         GL11.glVertex2d(d4, d5);
         GL11.glVertex2d(d11, d12);
         GL11.glVertex2d(d9, d10);
      } else {
         GL11.glVertex2d(d2, d3);
         GL11.glVertex2d(d9, d10);
         GL11.glVertex2d(d11, d12);
         GL11.glVertex2d(d4, d5);
      }

      GL11.glEnd();
   }

   public String getIP() {
      return this.ip;
   }

   public int getPort() {
      return this.port;
   }

   public boolean isConnecting() {
      return this.connecting;
   }
}
