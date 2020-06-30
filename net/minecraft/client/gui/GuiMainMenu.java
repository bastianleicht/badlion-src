package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import net.badlion.client.Wrapper;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.InputField;
import net.badlion.client.gui.mainmenu.CapeMenu;
import net.badlion.client.gui.mainmenu.GuiAccountList;
import net.badlion.client.gui.mainmenu.GuiAccountLogin;
import net.badlion.client.gui.mainmenu.GuiModProfiles;
import net.badlion.client.gui.mainmenu.GuiTopBar;
import net.badlion.client.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButtonLanguage;
import net.minecraft.client.gui.GuiConfirmOpenLink;
import net.minecraft.client.gui.GuiLanguage;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.realms.RealmsBridge;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.io.Charsets;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;

public class GuiMainMenu extends GuiScreen implements GuiYesNoCallback {
   private static final AtomicInteger field_175373_f = new AtomicInteger(0);
   private static final Logger logger = LogManager.getLogger();
   private static final Random RANDOM = new Random();
   private float updateCounter;
   private String splashText;
   private GuiButton buttonResetDemo;
   private boolean field_175375_v = true;
   private final Object threadLock = new Object();
   private String openGLWarning1;
   private String openGLWarning2;
   private String openGLWarningLink;
   private static final ResourceLocation minecraftTitleTextureHd = new ResourceLocation("textures/menu/home/minecraft-logo.png");
   private static final ResourceLocation splashTexts = new ResourceLocation("texts/splashes.txt");
   private static final ResourceLocation badlionClientHeader = new ResourceLocation("textures/menu/home/bl-client-logo.png");
   private static final ResourceLocation badlionClientLogo = new ResourceLocation("textures/gui/badlion-client-logo.png");
   public static final ResourceLocation badlionLogo = new ResourceLocation("textures/gui/badlion-logo-large.png");
   public static final String field_96138_a = "Please click " + EnumChatFormatting.UNDERLINE + "here" + EnumChatFormatting.RESET + " for more information.";
   private int field_92024_r;
   private int field_92023_s;
   private int field_92022_t;
   private int field_92021_u;
   private int field_92020_v;
   private int field_92019_w;
   private GuiButton realmsButton;
   private boolean L;
   private GuiScreen M;
   private final int topBarHeight = 25;
   private GuiTopBar guiTopBar;
   private GuiModProfiles guiModProfiles;
   private boolean userBoxOpen = false;
   private boolean loginPageOpen = false;
   private GuiAccountList guiAccountList;
   private GuiAccountLogin guiAccountLogin;
   private InputField emailField;
   private InputField passwordField;
   private CapeMenu capeMenu;

   public GuiMainMenu() {
      this.openGLWarning2 = field_96138_a;
      this.L = false;
      this.splashText = "missingno";
      BufferedReader bufferedreader = null;

      try {
         List<String> list = Lists.newArrayList();
         bufferedreader = new BufferedReader(new InputStreamReader(Minecraft.getMinecraft().getResourceManager().getResource(splashTexts).getInputStream(), Charsets.UTF_8));

         String s;
         while((s = bufferedreader.readLine()) != null) {
            s = s.trim();
            if(!s.isEmpty()) {
               list.add(s);
            }
         }

         if(!list.isEmpty()) {
            while(true) {
               this.splashText = (String)list.get(RANDOM.nextInt(list.size()));
               if(this.splashText.hashCode() != 125780783) {
                  break;
               }
            }
         }
      } catch (IOException var12) {
         ;
      } finally {
         if(bufferedreader != null) {
            try {
               bufferedreader.close();
            } catch (IOException var11) {
               ;
            }
         }

      }

      this.updateCounter = RANDOM.nextFloat();
      this.openGLWarning1 = "";
      if(!GLContext.getCapabilities().OpenGL20 && !OpenGlHelper.areShadersSupported()) {
         this.openGLWarning1 = I18n.format("title.oldgl1", new Object[0]);
         this.openGLWarning2 = I18n.format("title.oldgl2", new Object[0]);
         this.openGLWarningLink = "https://help.mojang.com/customer/portal/articles/325948?ref=game";
      }

   }

   private boolean a() {
      return Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.enumFloat) && this.M != null;
   }

   public void updateScreen() {
      Gui.backgroundPanorama.tickPanorama();
      if(this.a()) {
         this.M.updateScreen();
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }

   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if(this.guiModProfiles.isBoxOpen()) {
         this.guiModProfiles.keyTyped(typedChar, keyCode);
      } else if(keyCode == 1) {
         if(this.loginPageOpen) {
            this.loginPageOpen = false;
         } else if(this.userBoxOpen) {
            this.userBoxOpen = false;
         }
      } else if(keyCode == 15) {
         if(this.emailField != null && this.emailField.isFocused()) {
            this.emailField.setFocused(false);
            this.passwordField.setFocused(true);
         } else if(this.passwordField != null && this.passwordField.isFocused()) {
            this.emailField.setFocused(true);
            this.passwordField.setFocused(false);
         } else if(this.emailField != null) {
            this.emailField.setFocused(true);
         }
      } else if(this.emailField != null && this.emailField.isFocused()) {
         this.emailField.keyTyped(typedChar, keyCode);
      } else if(this.passwordField != null && this.passwordField.isFocused()) {
         this.passwordField.keyTyped(typedChar, keyCode);
      }

   }

   public void initGui() {
      Gui.backgroundPanorama.updateWidthHeight(this.width, this.height);
      Calendar calendar = Calendar.getInstance();
      calendar.setTime(new Date());
      if(calendar.get(2) + 1 == 12 && calendar.get(5) == 24) {
         this.splashText = "Merry X-mas!";
      } else if(calendar.get(2) + 1 == 1 && calendar.get(5) == 1) {
         this.splashText = "Happy new year!";
      } else if(calendar.get(2) + 1 == 10 && calendar.get(5) == 31) {
         this.splashText = "OOoooOOOoooo! Spooky!";
      }

      int i = 24;
      int j = this.height / 3 + 87;
      if(this.mc.isDemo()) {
         this.addDemoButtons(j, 5);
      } else {
         this.addSingleplayerMultiplayerButtons(j, 5);
      }

      String s = I18n.format("menu.options", new Object[0]);
      s = s.substring(0, s.length() - 3);
      this.buttonList.add(new GuiButton(0, this.width / 2 - 80, j + 25, 80, 20, s));
      this.buttonList.add(new GuiButton(4, this.width / 2 + 5, j + 25, 80, 20, I18n.format("menu.quit", new Object[0]), GuiButton.ButtonType.THICK_LINES));
      this.buttonList.add(new GuiButtonLanguage(5, this.width / 2 - 9, j + 56));
      synchronized(this.threadLock) {
         this.field_92023_s = this.fontRendererObj.getStringWidth(this.openGLWarning1);
         this.field_92024_r = this.fontRendererObj.getStringWidth(this.openGLWarning2);
         int k = Math.max(this.field_92023_s, this.field_92024_r);
         this.field_92022_t = (this.width - k) / 2;
         this.field_92021_u = ((GuiButton)this.buttonList.get(0)).yPosition - 24;
         this.field_92020_v = this.field_92022_t + k;
         this.field_92019_w = this.field_92021_u + 24;
      }

      this.mc.func_181537_a(false);
      if(Minecraft.getMinecraft().gameSettings.getOptionOrdinalValue(GameSettings.Options.enumFloat) && !this.L) {
         RealmsBridge realmsbridge = new RealmsBridge();
         this.M = realmsbridge.getNotificationScreen(this);
         this.L = true;
      }

      if(this.a()) {
         this.M.a(this.width, this.height);
         this.M.initGui();
      }

      this.guiTopBar = new GuiTopBar();
      this.getClass();
      this.guiModProfiles = new GuiModProfiles(11, 33);
      this.guiAccountList = new GuiAccountList(this);
      this.guiAccountLogin = new GuiAccountLogin(this);
      this.capeMenu = new CapeMenu(this);
   }

   private void addSingleplayerMultiplayerButtons(int p_73969_1_, int p_73969_2_) {
      this.buttonList.add(new GuiButton(1, this.width / 2 - 80, p_73969_1_, 80, 20, I18n.format("menu.singleplayer", new Object[0]), GuiButton.ButtonType.NORMAL, GuiButton.singleplayerIcon, 10, 9));
      this.buttonList.add(new GuiButton(2, this.width / 2 + p_73969_2_, p_73969_1_, 80, 20, I18n.format("menu.multiplayer", new Object[0]), GuiButton.ButtonType.NORMAL, GuiButton.multiplayerIcon, 13, 9));
   }

   private void addDemoButtons(int p_73972_1_, int p_73972_2_) {
      this.buttonList.add(new GuiButton(11, this.width / 2 - 100, p_73972_1_, I18n.format("menu.playdemo", new Object[0])));
      this.buttonList.add(this.buttonResetDemo = new GuiButton(12, this.width / 2 - 100, p_73972_1_ + p_73972_2_ * 1, I18n.format("menu.resetdemo", new Object[0])));
      ISaveFormat isaveformat = this.mc.getSaveLoader();
      WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
      if(worldinfo == null) {
         this.buttonResetDemo.enabled = false;
      }

   }

   protected void actionPerformed(GuiButton button) throws IOException {
      if(button.id == 0) {
         this.mc.displayGuiScreen(new GuiOptions(this, this.mc.gameSettings));
      }

      if(button.id == 5) {
         this.mc.displayGuiScreen(new GuiLanguage(this, this.mc.gameSettings, this.mc.getLanguageManager()));
      }

      if(button.id == 1) {
         this.mc.displayGuiScreen(new GuiSelectWorld(this));
      }

      if(button.id == 2) {
         this.mc.displayGuiScreen(new GuiMultiplayer(this));
      }

      if(button.id == 14 && this.realmsButton.visible) {
         this.f();
      }

      if(button.id == 4) {
         this.mc.shutdown();
      }

      if(button.id == 11) {
         this.mc.launchIntegratedServer("Demo_World", "Demo_World", DemoWorldServer.demoWorldSettings);
      }

      if(button.id == 12) {
         ISaveFormat isaveformat = this.mc.getSaveLoader();
         WorldInfo worldinfo = isaveformat.getWorldInfo("Demo_World");
         if(worldinfo != null) {
            GuiYesNo guiyesno = GuiSelectWorld.func_152129_a(this, worldinfo.getWorldName(), 12);
            this.mc.displayGuiScreen(guiyesno);
         }
      }

   }

   private void f() {
      RealmsBridge realmsbridge = new RealmsBridge();
      realmsbridge.switchToRealms(this);
   }

   public void confirmClicked(boolean result, int id) {
      if(result && id == 12) {
         ISaveFormat isaveformat = this.mc.getSaveLoader();
         isaveformat.flushCache();
         isaveformat.deleteWorldDirectory("Demo_World");
         this.mc.displayGuiScreen(this);
      } else if(id == 13) {
         if(result) {
            try {
               Class<?> oclass = Class.forName("java.awt.Desktop");
               Object object = oclass.getMethod("getDesktop", new Class[0]).invoke((Object)null, new Object[0]);
               oclass.getMethod("browse", new Class[]{URI.class}).invoke(object, new Object[]{new URI(this.openGLWarningLink)});
            } catch (Throwable var5) {
               logger.error("Couldn\'t open link", var5);
            }
         }

         this.mc.displayGuiScreen(this);
      }

   }

   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      int i = 196;
      int j = this.width / 2 - i / 2;
      int k = this.height / 3 + 8;
      int l = this.width / 2 + i / 2;
      int i1 = this.height / 3 + 188 + 8;
      GL11.glDisable(3008);
      Gui.backgroundPanorama.renderSkybox(mouseX, mouseY, partialTicks);
      GL11.glEnable(3008);
      short short1 = 179;
      int j1 = this.width / 2 - short1 / 2;
      int k1 = this.height / 6;
      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(minecraftTitleTextureHd);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture(j1, k1, 0.0F, 0.0F, short1, 29, (float)short1, 29.0F);
      GuiTopBar guitopbar = this.guiTopBar;
      this.getClass();
      guitopbar.render(mouseX, mouseY, 25, this.width, this.userBoxOpen, this.guiModProfiles.isBoxOpen());
      String s = "Minecraft 1.8.9";
      if(this.mc.isDemo()) {
         s = s + " Demo";
      }

      Gui.drawRect(j, k, l, i1, -265803215);
      Tessellator tessellator = Tessellator.getInstance();
      WorldRenderer worldrenderer = tessellator.getWorldRenderer();
      GL11.glEnable(3042);
      GL11.glDisable(3553);
      OpenGlHelper.glBlendFunc(770, 771, 1, 0);
      worldrenderer.begin(7, DefaultVertexFormats.POSITION);
      ColorUtil.bindHexColorRGBA(-11755587);
      worldrenderer.pos((double)j, (double)i1, 0.0D).endVertex();
      worldrenderer.pos((double)j, (double)(i1 + 2), 0.0D).endVertex();
      worldrenderer.pos((double)(j + i / 3 * 2), (double)(i1 + 2), 0.0D).endVertex();
      worldrenderer.pos((double)(j + i / 3 * 2 - 2), (double)i1, 0.0D).endVertex();
      tessellator.draw();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION);
      ColorUtil.bindHexColorRGBA(-1);
      worldrenderer.pos((double)(j + i / 3 * 2 - 2), (double)i1, 0.0D).endVertex();
      worldrenderer.pos((double)(j + i / 3 * 2), (double)(i1 + 2), 0.0D).endVertex();
      worldrenderer.pos((double)(j + i / 3 * 2 + 24), (double)(i1 + 2), 0.0D).endVertex();
      worldrenderer.pos((double)(j + i / 3 * 2 + 22), (double)i1, 0.0D).endVertex();
      tessellator.draw();
      worldrenderer.begin(7, DefaultVertexFormats.POSITION);
      ColorUtil.bindHexColorRGBA(-80570);
      worldrenderer.pos((double)(j + i / 3 * 2 + 22), (double)i1, 0.0D).endVertex();
      worldrenderer.pos((double)(j + i / 3 * 2 + 24), (double)(i1 + 2), 0.0D).endVertex();
      worldrenderer.pos((double)l, (double)(i1 + 2), 0.0D).endVertex();
      worldrenderer.pos((double)l, (double)i1, 0.0D).endVertex();
      tessellator.draw();
      GL11.glEnable(3553);
      GL11.glDisable(3042);
      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(badlionClientHeader);
      GL11.glColor3d(1.0D, 1.0D, 1.0D);
      Gui.drawModalRectWithCustomSizedTexture(j + (l - j) / 2 - 80, k + 18, 0.0F, 0.0F, 160, 43, 160.0F, 43.0F);
      GL11.glColor3d(0.5D, 0.5D, 0.5D);
      Wrapper.getInstance().getBadlionFontRenderer().drawString(j + i / 2 - Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(s, 12, BadlionFontRenderer.FontType.TITLE) / 2, i1 - 16, s, 12, BadlionFontRenderer.FontType.TITLE, true);
      if(this.openGLWarning1 != null && this.openGLWarning1.length() > 0) {
         drawRect(this.field_92022_t - 2, this.field_92021_u - 2, this.field_92020_v + 2, this.field_92019_w - 1, 1428160512);
         this.drawString(this.fontRendererObj, this.openGLWarning1, this.field_92022_t, this.field_92021_u, -1);
         this.drawString(this.fontRendererObj, this.openGLWarning2, (this.width - this.field_92024_r) / 2, ((GuiButton)this.buttonList.get(0)).yPosition - 12, -1);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
      if(this.a()) {
         this.M.drawScreen(mouseX, mouseY, partialTicks);
      }

      if(this.userBoxOpen) {
         if(this.loginPageOpen) {
            GuiAccountLogin guiaccountlogin = this.guiAccountLogin;
            this.getClass();
            guiaccountlogin.render(mouseX, mouseY, 25);
         } else {
            GuiAccountList guiaccountlist = this.guiAccountList;
            this.getClass();
            guiaccountlist.render(mouseX, mouseY, 25);
         }
      } else if(this.guiModProfiles.isBoxOpen()) {
         this.guiModProfiles.render(mouseX, mouseY);
      }

   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      synchronized(this.threadLock) {
         if(this.openGLWarning1.length() > 0 && mouseX >= this.field_92022_t && mouseX <= this.field_92020_v && mouseY >= this.field_92021_u && mouseY <= this.field_92019_w) {
            GuiConfirmOpenLink guiconfirmopenlink = new GuiConfirmOpenLink(this, this.openGLWarningLink, 13, true);
            guiconfirmopenlink.disableSecurityWarning();
            this.mc.displayGuiScreen(guiconfirmopenlink);
         }
      }

      if(this.a()) {
         this.M.mouseClicked(mouseX, mouseY, mouseButton);
      }

      GuiTopBar guitopbar = this.guiTopBar;
      this.getClass();
      if(guitopbar.isMouseOverModProfilesBox(mouseX, mouseY, 10, 0, 25)) {
         this.guiModProfiles.setBoxOpen(!this.guiModProfiles.isBoxOpen());
      } else if(this.guiModProfiles.isBoxOpen() && this.guiModProfiles.onClick(mouseX, mouseY, mouseButton)) {
         return;
      }

      guitopbar = this.guiTopBar;
      this.getClass();
      if(guitopbar.isMouseOverUsernameBox(mouseX, mouseY, 25, this.width)) {
         this.userBoxOpen = !this.userBoxOpen;
         Wrapper.getInstance().getModProfileManager().getAccountManager().reloadSessions();
      } else if(this.userBoxOpen) {
         if(this.loginPageOpen) {
            GuiAccountLogin guiaccountlogin = this.guiAccountLogin;
            this.getClass();
            if(guiaccountlogin.isMouseOverBox(mouseX, mouseY, 25)) {
               guiaccountlogin = this.guiAccountLogin;
               this.getClass();
               if(guiaccountlogin.isMouseOverAddAccountButton(mouseX, mouseY, 25)) {
                  if(this.emailField != null && !this.emailField.getText().isEmpty() && this.passwordField != null && !this.passwordField.getText().isEmpty()) {
                     boolean flag = Wrapper.getInstance().getModProfileManager().getAccountManager().loginProfile(this.emailField.getText(), this.passwordField.getText());
                     if(flag) {
                        this.emailField.reset();
                        this.passwordField.reset();
                        this.loginPageOpen = false;
                     }
                  }
               } else {
                  guiaccountlogin = this.guiAccountLogin;
                  this.getClass();
                  if(guiaccountlogin.isMouseOverBackButton(mouseX, mouseY, 25)) {
                     this.loginPageOpen = false;
                     if(this.emailField != null) {
                        this.emailField.reset();
                     }

                     if(this.passwordField != null) {
                        this.passwordField.reset();
                     }

                     return;
                  }
               }

               if(this.emailField != null) {
                  this.emailField.onClick(mouseButton);
               }

               if(this.passwordField != null) {
                  this.passwordField.onClick(mouseButton);
               }

               return;
            }

            this.userBoxOpen = false;
            this.loginPageOpen = false;
            if(this.emailField != null) {
               this.emailField.reset();
            }

            if(this.passwordField != null) {
               this.passwordField.reset();
            }
         } else {
            GuiAccountList guiaccountlist = this.guiAccountList;
            this.getClass();
            if(guiaccountlist.isMouseOverBox(mouseX, mouseY, 25)) {
               guiaccountlist = this.guiAccountList;
               this.getClass();
               int i = guiaccountlist.isMouseOverAccount(mouseX, mouseY, 25);
               if(i != -1) {
                  String s = (String)Wrapper.getInstance().getModProfileManager().getAccountManager().getSortedUsernames().get(i);
                  if(!this.mc.getSession().getUsername().equals(s)) {
                     Wrapper.getInstance().getModProfileManager().getAccountManager().switchToAccount(s);
                  }
               } else {
                  guiaccountlist = this.guiAccountList;
                  this.getClass();
                  if(guiaccountlist.isMouseOverAddAccountButton(mouseX, mouseY, 25)) {
                     this.loginPageOpen = true;
                  }
               }

               return;
            }

            this.userBoxOpen = false;
            this.loginPageOpen = false;
            if(this.emailField != null) {
               this.emailField.reset();
            }

            if(this.passwordField != null) {
               this.passwordField.reset();
            }
         }
      }

   }

   public void onGuiClosed() {
      if(this.M != null) {
         this.M.onGuiClosed();
      }

   }

   public Minecraft getMinecraft() {
      return this.mc;
   }

   public int getWidth() {
      return this.width;
   }

   public ResourceLocation getBadlionLogo() {
      return badlionLogo;
   }

   public InputField getEmailField() {
      return this.emailField;
   }

   public void setEmailField(InputField p_setEmailField_1_) {
      this.emailField = p_setEmailField_1_;
   }

   public InputField getPasswordField() {
      return this.passwordField;
   }

   public void setPasswordField(InputField p_setPasswordField_1_) {
      this.passwordField = p_setPasswordField_1_;
   }
}
