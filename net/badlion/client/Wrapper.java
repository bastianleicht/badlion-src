package net.badlion.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JOptionPane;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.BaseUIRenderer;
import net.badlion.client.manager.CapeManager;
import net.badlion.client.manager.ModProfileManager;
import net.badlion.client.mods.Mod;
import net.badlion.client.mods.ModProfile;
import net.badlion.client.mods.misc.LegacyAnimations;
import net.badlion.client.mods.render.RenderMod;
import net.badlion.client.util.BlTextureManager;
import net.badlion.client.util.CTOSWorker;
import net.badlion.client.util.ColorHandler;
import net.badlion.client.util.EnumAdapterFactory;
import net.badlion.client.util.HashIndex;
import net.badlion.client.util.PingWorker;
import net.badlion.client.util.STOCWorker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.network.NetworkManager;
import org.lwjgl.input.Mouse;

public class Wrapper {
   private static Wrapper instance;
   private boolean disableDebug = true;
   private boolean wasConnected;
   private LegacyAnimations legacyAnimations;
   private boolean globalMouseButtonHandle;
   public String currentPremiumConnection;
   public NetworkManager networkManager;
   private boolean loadedSettings;
   private boolean connecting;
   private int connectPort;
   public int mouse_wheel;
   private boolean loaded;
   private Gson gson;
   private Gson gsonNonPretty;
   private BlTextureManager blTextureManager;
   private int tabTipBACTicks;
   private boolean fancyGraphics;
   private boolean clearGlass;
   public String currentIp;
   private ColorHandler colorHandler;
   private BadlionFontRenderer badlionFontRenderer;
   private boolean debugEnabled = false;
   private String currentConnection;
   private boolean edit;
   private RenderMod editMod;
   private float beforeGammaSetting = -1.0F;
   public static String hwidHash;
   public static int xorKey;
   public static String key;
   private Map disallowedMods;
   private List workers = new ArrayList();
   private PingWorker na;
   private PingWorker sa;
   private PingWorker eu;
   private Wrapper.Region region;
   private static final String gt = "1.2.0b live client";
   private static String version = "2.0.0-v-beta";
   private static int jsonVersion = 15;
   private ModProfileManager modProfileManager;
   private CapeManager capeManager;
   public boolean premium;
   private CTOSWorker ctosWorker;
   private STOCWorker stocWorker;
   public boolean flags;
   private int lastDisplayX;
   private int lastDisplayY;
   private boolean connected;
   private int failSafe;
   private BaseUIRenderer UIRenderer;
   private static boolean failed;

   public boolean shouldDisableDebug() {
      return this.disableDebug;
   }

   public Wrapper.Region getRegion() {
      return this.region;
   }

   public void releaseMouse() {
      this.globalMouseButtonHandle = false;
   }

   public boolean getGlobalMouseState() {
      return this.globalMouseButtonHandle;
   }

   public void toggleButtonStatus() {
      this.globalMouseButtonHandle = true;
   }

   public ModProfileManager getModProfileManager() {
      return this.modProfileManager;
   }

   public ModProfile getActiveModProfile() {
      return this.modProfileManager.getActiveModProfile();
   }

   public boolean isPremium() {
      return this.premium;
   }

   public boolean hasLoadedSettings() {
      return this.loadedSettings;
   }

   public int getMouseX() {
      int i = Mouse.getX();
      if((double)getInstance().getRealScaleFactor() <= 2.0D) {
         i = (int)((double)i / 2.0D);
      } else {
         i = i / getInstance().getRealScaleFactor();
      }

      return i;
   }

   public int getMouseY() {
      int i = Minecraft.getMinecraft().displayHeight - Mouse.getY();
      if((double)getInstance().getRealScaleFactor() <= 2.0D) {
         i = (int)((double)i / 2.0D);
      } else {
         i = i / getInstance().getRealScaleFactor();
      }

      return i;
   }

   public void setCurrentConnection(String connection) {
      this.currentConnection = connection;
   }

   public String getCurrentConnection() {
      return this.currentConnection;
   }

   public void init() {
      instance = this;
      this.legacyAnimations = new LegacyAnimations();
      this.blTextureManager = new BlTextureManager(Minecraft.getMinecraft().getResourceManager());
      this.gson = (new GsonBuilder()).registerTypeAdapterFactory(new EnumAdapterFactory()).setPrettyPrinting().enableComplexMapKeySerialization().create();
      this.gsonNonPretty = (new GsonBuilder()).registerTypeAdapterFactory(new EnumAdapterFactory()).enableComplexMapKeySerialization().create();

      try {
         this.badlionFontRenderer = new BadlionFontRenderer();
      } catch (Throwable var3) {
         var3.printStackTrace();
      }

      this.modProfileManager = new ModProfileManager();

      try {
         this.modProfileManager.loadModProfiles();
      } catch (Throwable var2) {
         var2.printStackTrace();
      }

      this.capeManager = new CapeManager();
      this.loadedSettings = true;
      this.ctosWorker = new CTOSWorker();
      this.stocWorker = new STOCWorker();
      this.colorHandler = new ColorHandler();
      (new Thread(this.ctosWorker)).start();
      (new Thread(this.stocWorker)).start();
      this.na = new PingWorker("na.badlion.net:25565");
      this.eu = new PingWorker("eu.badlion.net:25565");
      this.sa = new PingWorker("sa.badlion.net:25565");
      this.na.setRunOnce(true);
      this.eu.setRunOnce(true);
      this.sa.setRunOnce(true);
      (new Thread(this.na)).start();
      (new Thread(this.eu)).start();
      (new Thread(this.sa)).start();
      this.workers.add(this.na);
      this.workers.add(this.sa);
      this.workers.add(this.eu);
   }

   public void showBACTabTip() {
      this.tabTipBACTicks += 2;
      if((double)this.tabTipBACTicks > (double)Minecraft.getDebugFPS() * 0.5D) {
         this.tabTipBACTicks = (int)((double)Minecraft.getDebugFPS() * 0.5D);
      }

   }

   public void setEditing(boolean edit, RenderMod editMod) {
      this.edit = edit;
      this.editMod = editMod;
   }

   public boolean isEditing() {
      return this.edit && this.editMod != null;
   }

   public RenderMod getEditMod() {
      return this.editMod;
   }

   public void destroy() {
      if(this.beforeGammaSetting != -1.0F) {
         Minecraft.getMinecraft().gameSettings.saturation = this.beforeGammaSetting;
         Minecraft.getMinecraft().gameSettings.saveOptions();
      }

   }

   public static Wrapper getInstance() {
      return instance;
   }

   public boolean checkVerify() {
      return true;
   }

   public native void sendClientToServerComparisonHash(String var1, int var2);

   public native void sendServerToClientComparisonHash(String var1, int var2);

   public short isConnectedToPremiumGame() {
      boolean flag = this.premium;
      boolean flag1 = Minecraft.getMinecraft().getNetHandler() != null;
      boolean flag2;
      boolean flag3;
      if(flag1) {
         flag2 = Minecraft.getMinecraft().getNetHandler().getNetworkManager() != null;
         if(flag2) {
            flag3 = Minecraft.getMinecraft().getNetHandler().getNetworkManager().isChannelOpen();
         } else {
            flag3 = false;
         }
      } else {
         flag2 = false;
         flag3 = false;
      }

      short short1 = 0;
      if(flag) {
         short1 = (short)(short1 | 1);
      } else {
         short1 = (short)(short1 & -2);
      }

      if(flag1) {
         short1 = (short)(short1 | 2);
      } else {
         short1 = (short)(short1 & -3);
      }

      if(flag2) {
         short1 = (short)(short1 | 4);
      } else {
         short1 = (short)(short1 & -5);
      }

      if(flag3) {
         short1 = (short)(short1 | 8);
      } else {
         short1 = (short)(short1 & -9);
      }

      return short1;
   }

   public native int doesServerHaveBACEnabled(String var1, int var2);

   public native String sendInitialHeartbeat(String var1, int var2);

   public int isServerBACEnabled(String ip, int port) {
      return this.doesServerHaveBACEnabled(ip, port);
   }

   public String getPlayerUUID() {
      return Minecraft.getMinecraft().getSession() != null && Minecraft.getMinecraft().getSession().getPlayerID() != null?Minecraft.getMinecraft().getSession().getPlayerID():"Error";
   }

   public static native String CalculateHash();

   public int getRealScaleFactor() {
      return Math.max(2, (new ScaledResolution(Minecraft.getMinecraft())).getScaleFactor());
   }

   public int getScaleFactor() {
      return (new ScaledResolution(Minecraft.getMinecraft())).getScaleFactor();
   }

   public static int getJsonVersion() {
      return jsonVersion;
   }

   public static String getVersion() {
      return version;
   }

   public void sendClientToServer(String hash, int currentIndex) {
      this.ctosWorker.addToQueue(new HashIndex(hash, currentIndex));
   }

   public void sendServerToClient(String hash, int currentIndex) {
      this.stocWorker.addToQueue(new HashIndex(hash, currentIndex));
   }

   public boolean wasAntiLauncherSet() {
      return hwidHash != null && xorKey != 0 && this.loaded;
   }

   public int sendHeartBeat(String ip, int port) {
      Wrapper.HbResponse wrapper$hbresponse = (Wrapper.HbResponse)getInstance().gsonNonPretty.fromJson(this.sendInitialHeartbeat(ip, port), Wrapper.HbResponse.class);
      if(wrapper$hbresponse.getDisallowedMods() != null && !wrapper$hbresponse.getDisallowedMods().isEmpty()) {
         this.disallowedMods = wrapper$hbresponse.getDisallowedMods();

         for(Entry<String, Wrapper.DisallowedMods> entry : this.disallowedMods.entrySet()) {
            for(Mod mod : this.getActiveModProfile().getAllMods()) {
               if(mod.getName().equals(entry.getKey())) {
                  mod.handleDisallowedMods((Wrapper.DisallowedMods)entry.getValue());
               }
            }
         }
      } else {
         this.disallowedMods = null;
      }

      return wrapper$hbresponse.getHb();
   }

   public void killMinecraft() {
      System.exit(0);
   }

   public native void updateConnectionStatus(boolean var1);

   public void updateConnection(boolean connected) {
      if(!connected) {
         this.connected = false;
         if(this.disallowedMods != null) {
            this.disallowedMods = null;

            for(Mod mod : this.getActiveModProfile().getAllMods()) {
               mod.handleDisallowedMods((Wrapper.DisallowedMods)null);
            }
         }
      }

      this.updateConnectionStatus(connected);
   }

   public boolean tick() {
      if(this.region == null) {
         int i = Integer.MAX_VALUE;
         PingWorker pingworker = null;

         for(PingWorker pingworker1 : this.workers) {
            if(pingworker1.getPing() < i) {
               i = pingworker1.getPing();
               pingworker = pingworker1;
            }
         }

         if(i > 0) {
            if(this.na.equals(pingworker)) {
               this.region = Wrapper.Region.NA;
            }

            if(this.sa.equals(pingworker)) {
               this.region = Wrapper.Region.SA;
            }

            if(this.eu.equals(pingworker)) {
               this.region = Wrapper.Region.EU;
            }
         }
      }

      if(Minecraft.getMinecraft().gameSettings.fancyGraphics != this.fancyGraphics) {
         Minecraft minecraft = Minecraft.getMinecraft();
         this.fancyGraphics = minecraft.gameSettings.fancyGraphics;
         minecraft.refreshResources();
         minecraft.renderGlobal.loadRenderers();
      }

      this.getLegacyAnimations().update();

      for(int j = 0; j < this.colorHandler.getRainbowSpeed(); ++j) {
         this.colorHandler.tickRainbow();
      }

      if(Minecraft.getMinecraft().theWorld == null && !(Minecraft.getMinecraft().currentScreen instanceof GuiConnecting)) {
         this.currentConnection = null;
      }

      Minecraft.getMinecraft().getRenderManager().debugBoundingBox = this.getActiveModProfile().getHitboxes().isEnabled();
      if(this.beforeGammaSetting == -1.0F) {
         this.beforeGammaSetting = Minecraft.getMinecraft().gameSettings.saturation;
      }

      if(this.getActiveModProfile().getFullbright().isEnabled()) {
         Minecraft.getMinecraft().gameSettings.saturation = 10.0F;
      } else {
         if((double)this.beforeGammaSetting != -1.0D) {
            Minecraft.getMinecraft().gameSettings.saturation = this.beforeGammaSetting;
         }

         this.beforeGammaSetting = -1.0F;
      }

      try {
         if(!this.loaded) {
            CalculateHash();
         }

         if(!this.wasAntiLauncherSet()) {
            return false;
         }

         if(!getInstance().checkVerify()) {
            JOptionPane.showMessageDialog((Component)null, "Please use the Badlion launcher!  If you are seeing this error constantly, please fully restart the Badlion Client and launcher.", "Badlion Client", 0);
            System.exit(0);
         }
      } catch (UnsatisfiedLinkError var5) {
         ;
      }

      if(Minecraft.getMinecraft().displayWidth != this.lastDisplayX || Minecraft.getMinecraft().displayHeight != this.lastDisplayY) {
         this.lastDisplayX = Minecraft.getMinecraft().displayWidth;
         this.lastDisplayY = Minecraft.getMinecraft().displayHeight;
         this.getModProfileManager().saveActiveModProfile();
      }

      if(Minecraft.getMinecraft().gameSettings.keyBindPickBlock.getKeyCode() != -100 && Minecraft.getMinecraft().gameSettings.keyBindPickBlock.getKeyCode() != -99) {
         Minecraft.getMinecraft().gameSettings.keyBindPickBlock.setKeyCode(-100);
      }

      boolean flag = this.connected && this.premium && Minecraft.getMinecraft().getNetHandler() != null;
      if(flag != this.wasConnected) {
         this.wasConnected = flag;
         if(flag) {
            this.updateConnection(true);
         }
      }

      if(this.connected && this.premium && Minecraft.getMinecraft().getNetHandler() == null) {
         this.updateConnection(false);
      }

      if(!this.connected && Minecraft.getMinecraft().getNetHandler() != null && Minecraft.getMinecraft().thePlayer != null && Minecraft.getMinecraft().theWorld != null) {
         this.connected = true;
      }

      if(Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiConnecting) {
         GuiConnecting guiconnecting = (GuiConnecting)Minecraft.getMinecraft().currentScreen;
         if(guiconnecting.getIP() != null && guiconnecting.getPort() != 0 && !guiconnecting.isConnecting()) {
            guiconnecting.connect(guiconnecting.getIP(), guiconnecting.getPort());
         }
      }

      if(!getInstance().wasAntiLauncherSet()) {
         return false;
      } else {
         this.mouse_wheel = Mouse.getDWheel();
         if(this.mouse_wheel > 0) {
            this.getActiveModProfile().getSlideoutAccess().getSlideoutInstance().onClick(-2);
         } else if(this.mouse_wheel < 0) {
            this.getActiveModProfile().getSlideoutAccess().getSlideoutInstance().onClick(-1);
         }

         return true;
      }
   }

   public boolean isClearGlass() {
      return this.clearGlass;
   }

   public void setClearGlass(boolean clearGlass) {
      this.clearGlass = clearGlass;
   }

   public boolean isDebugEnabled() {
      return this.debugEnabled;
   }

   public Gson getGson() {
      return this.gson;
   }

   public Gson getGsonNonPretty() {
      return this.gsonNonPretty;
   }

   public BadlionFontRenderer getBadlionFontRenderer() {
      return this.badlionFontRenderer;
   }

   public BaseUIRenderer getUIRenderer() {
      return this.UIRenderer;
   }

   public BlTextureManager getBlTextureManager() {
      return this.blTextureManager;
   }

   public LegacyAnimations getLegacyAnimations() {
      return this.legacyAnimations;
   }

   public native String getAvailableProfiles(String var1);

   public native String refreshProfile(String var1, String var2);

   public native String loginProfile(String var1, String var2, String var3);

   public native String saveProfile(String var1, String var2, String var3);

   public native String disableCape(String var1);

   public native String enableCape(String var1);

   public native String getAvailableCapes(String var1);

   public native String checkCapesBulk(String var1);

   public native String checkCape(String var1);

   public native boolean CacheJNIInformation();

   public ColorHandler getColorHandler() {
      return this.colorHandler;
   }

   public CapeManager getCapeManager() {
      return this.capeManager;
   }

   public void hideBACTooltip() {
      this.tabTipBACTicks = 0;
   }

   public boolean isBACTooltipShowing() {
      if(this.tabTipBACTicks-- < 0) {
         this.tabTipBACTicks = 0;
      }

      return (double)this.tabTipBACTicks > 0.25D * (double)Minecraft.getDebugFPS();
   }

   public Map getDisallowedMods() {
      return this.disallowedMods;
   }

   public class DisallowedMods {
      private boolean disabled;
      private JsonObject extraData;

      public DisallowedMods(boolean disabled) {
         this.disabled = disabled;
      }

      public void setExtraData(JsonObject extraData) {
         this.extraData = extraData;
      }

      public JsonObject getExtraData() {
         return this.extraData;
      }

      public boolean isDisabled() {
         return this.disabled;
      }
   }

   public class HbResponse {
      private Map modsDisallowed;
      private int hb;

      public int getHb() {
         return this.hb;
      }

      public Map getDisallowedMods() {
         return this.modsDisallowed;
      }
   }

   public static enum Region {
      NA,
      SA,
      EU;
   }
}
