package net.badlion.client.mods;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.badlion.client.Wrapper;
import net.badlion.client.config.BetterframesConfig;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.RenderGame;
import net.badlion.client.mods.Mod;
import net.badlion.client.mods.misc.AutoGG;
import net.badlion.client.mods.misc.BlockOverlay;
import net.badlion.client.mods.misc.EnchantGlint;
import net.badlion.client.mods.misc.FOVChanger;
import net.badlion.client.mods.misc.SlideoutAccess;
import net.badlion.client.mods.misc.TcpNoDelay;
import net.badlion.client.mods.misc.TimeChanger;
import net.badlion.client.mods.movement.ToggleSneak;
import net.badlion.client.mods.render.ArmorStatus;
import net.badlion.client.mods.render.ChangeColorMod;
import net.badlion.client.mods.render.Chat;
import net.badlion.client.mods.render.Coordinates;
import net.badlion.client.mods.render.Crosshair;
import net.badlion.client.mods.render.Fullbright;
import net.badlion.client.mods.render.Hitboxes;
import net.badlion.client.mods.render.Keystroke;
import net.badlion.client.mods.render.ModConfigurator;
import net.badlion.client.mods.render.PotionStatus;
import net.badlion.client.mods.render.Saturation;
import net.badlion.client.mods.render.Scoreboard;
import net.badlion.client.mods.render.ShowArrows;
import net.badlion.client.mods.render.ShowCPS;
import net.badlion.client.mods.render.ShowDirection;
import net.badlion.client.mods.render.ShowFPS;
import net.badlion.client.mods.render.ShowPing;
import net.badlion.client.mods.render.ShowPotions;
import net.badlion.client.mods.render.minimap.MiniMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;

public class ModProfile {
   public static final int PROFILE_VERSION = 2;
   private transient String profileName;
   private boolean active = false;
   private int sortIndex;
   private Integer version = Integer.valueOf(2);
   private int slideoutKey = 54;
   private Coordinates coordinates = new Coordinates();
   private ToggleSneak toggleSneak = new ToggleSneak();
   private ArmorStatus armorStatus = new ArmorStatus();
   private PotionStatus potionStatus = new PotionStatus();
   private ShowFPS showFPS = new ShowFPS();
   private ShowDirection showDirection = new ShowDirection();
   private ShowCPS showCPS = new ShowCPS();
   private ShowPing showPing = new ShowPing();
   private Keystroke keyStroke = new Keystroke();
   private ShowArrows showArrows = new ShowArrows();
   private Fullbright fullbright = new Fullbright();
   private Hitboxes hitboxes = new Hitboxes();
   private TcpNoDelay tcpNoDelay = new TcpNoDelay();
   private EnchantGlint enchantGlint = new EnchantGlint();
   private Chat chat = new Chat();
   private Saturation saturation = new Saturation();
   private TimeChanger timeChanger = new TimeChanger();
   private Scoreboard scoreboard = new Scoreboard();
   private MiniMap newMiniMap = new MiniMap();
   private Crosshair crosshair = new Crosshair();
   private BlockOverlay blockOverlay = new BlockOverlay();
   private FOVChanger fovChanger = new FOVChanger();
   private ShowPotions showPotions = new ShowPotions();
   private AutoGG autoGG = new AutoGG();
   private BetterframesConfig betterframesConfig = new BetterframesConfig();
   private transient List allMods;
   private transient Map registeredEvents;
   private transient ModConfigurator modConfigurator;
   private transient SlideoutAccess slideoutAccess;
   private transient ChangeColorMod changeColorMod;

   public ModProfile(String profileName) {
      this.profileName = profileName;
      this.sortIndex = Wrapper.getInstance().getModProfileManager().getModProfiles().size();
      this.allMods = new ArrayList();
      this.registeredEvents = new HashMap();
      this.modConfigurator = new ModConfigurator();
      this.slideoutAccess = new SlideoutAccess();
      this.changeColorMod = new ChangeColorMod();
   }

   public void activate() {
      this.allMods = new ArrayList();
      this.registeredEvents = new HashMap();
      this.modConfigurator = new ModConfigurator();
      this.slideoutAccess = new SlideoutAccess();
      this.changeColorMod = new ChangeColorMod();
      this.active = true;
      if(this.modConfigurator == null) {
         this.modConfigurator = new ModConfigurator();
      }

      if(this.armorStatus == null) {
         this.armorStatus = new ArmorStatus();
      }

      if(this.coordinates == null) {
         this.coordinates = new Coordinates();
      }

      if(this.fullbright == null) {
         this.fullbright = new Fullbright();
      }

      if(this.hitboxes == null) {
         this.hitboxes = new Hitboxes();
      }

      if(this.keyStroke == null) {
         this.keyStroke = new Keystroke();
      }

      if(this.newMiniMap == null) {
         this.newMiniMap = new MiniMap();
      }

      if(this.potionStatus == null) {
         this.potionStatus = new PotionStatus();
      }

      if(this.showArrows == null) {
         this.showArrows = new ShowArrows();
      }

      if(this.showCPS == null) {
         this.showCPS = new ShowCPS();
      }

      if(this.showDirection == null) {
         this.showDirection = new ShowDirection();
      }

      if(this.showFPS == null) {
         this.showFPS = new ShowFPS();
      }

      if(this.showPing == null) {
         this.showPing = new ShowPing();
      }

      if(this.toggleSneak == null) {
         this.toggleSneak = new ToggleSneak();
      }

      if(this.tcpNoDelay == null) {
         this.tcpNoDelay = new TcpNoDelay();
      }

      if(this.enchantGlint == null) {
         this.enchantGlint = new EnchantGlint();
      }

      if(this.scoreboard == null) {
         this.scoreboard = new Scoreboard();
      }

      if(this.timeChanger == null) {
         this.timeChanger = new TimeChanger();
      }

      if(this.chat == null) {
         this.chat = new Chat();
      }

      if(this.saturation == null) {
         this.saturation = new Saturation();
      }

      if(this.crosshair == null) {
         this.crosshair = new Crosshair();
      }

      if(this.changeColorMod == null) {
         this.changeColorMod = new ChangeColorMod();
      }

      if(this.blockOverlay == null) {
         this.blockOverlay = new BlockOverlay();
      }

      if(this.fovChanger == null) {
         this.fovChanger = new FOVChanger();
      }

      if(this.showPotions == null) {
         this.showPotions = new ShowPotions();
      }

      if(this.autoGG == null) {
         this.autoGG = new AutoGG();
      }

      this.allMods.add(this.modConfigurator);
      this.allMods.add(this.armorStatus);
      this.allMods.add(this.coordinates);
      this.allMods.add(this.fullbright);
      this.allMods.add(this.hitboxes);
      this.allMods.add(this.keyStroke);
      this.allMods.add(this.newMiniMap);
      this.allMods.add(this.potionStatus);
      this.allMods.add(this.showArrows);
      this.allMods.add(this.showCPS);
      this.allMods.add(this.showDirection);
      this.allMods.add(this.showFPS);
      this.allMods.add(this.showPing);
      this.allMods.add(this.toggleSneak);
      this.allMods.add(this.tcpNoDelay);
      this.allMods.add(this.enchantGlint);
      this.allMods.add(this.scoreboard);
      this.allMods.add(this.timeChanger);
      this.allMods.add(this.chat);
      this.allMods.add(this.saturation);
      this.allMods.add(this.crosshair);
      this.allMods.add(this.changeColorMod);
      this.allMods.add(this.blockOverlay);
      this.allMods.add(this.fovChanger);
      this.allMods.add(this.showPotions);
      this.allMods.add(this.autoGG);
      this.allMods.add(this.slideoutAccess);

      for(Mod mod : this.allMods) {
         try {
            mod.init();
         } catch (Exception var5) {
            LogManager.getLogger().info("ERROR LOADING MOD init(): " + mod.getDisplayName() + " - " + var5.getMessage());
            LogManager.getLogger().catching(var5);
         }
      }

      this.sortMods();
      if(Minecraft.getMinecraft().getNetHandler() != null && Minecraft.getMinecraft().getNetHandler().getNetworkManager() != null && Minecraft.getMinecraft().getNetHandler().getNetworkManager().isChannelOpen()) {
         this.slideoutAccess.getSlideoutInstance().initPages();
         this.slideoutAccess.getSlideoutInstance().setVisible(true, false);
         this.slideoutAccess.getSlideoutInstance().getModProfilesButton().setBoxOpen(true);
      }

      if(Wrapper.getInstance().getDisallowedMods() != null) {
         for(Entry<String, Wrapper.DisallowedMods> entry : Wrapper.getInstance().getDisallowedMods().entrySet()) {
            for(Mod mod1 : this.allMods) {
               if(mod1.getName().equals(entry.getKey())) {
                  mod1.handleDisallowedMods((Wrapper.DisallowedMods)entry.getValue());
               }
            }
         }
      }

   }

   public void deactivate() {
      this.active = false;
      this.slideoutAccess.getSlideoutInstance().getTimer().cancel();
      if(this.slideoutAccess.getSlideoutInstance().isOpen()) {
         Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
      }

   }

   public void registerEvent(Mod mod, EventType eventType) {
      if(!this.registeredEvents.containsKey(eventType)) {
         this.registeredEvents.put(eventType, new ArrayList());
      }

      List<Mod> list = (List)this.registeredEvents.get(eventType);
      if(!list.contains(mod)) {
         list.add(mod);
      }

   }

   public void passEvent(Event event) {
      if(event instanceof RenderGame) {
         GL11.glPushAttrib(24640);
      }

      synchronized(Wrapper.getInstance().getModProfileManager()) {
         List<Mod> list = (List)this.registeredEvents.get(event.getEventType());
         if(list != null) {
            for(Mod mod : list) {
               if(!mod.isForceDisabled()) {
                  if(event instanceof RenderGame && mod instanceof SlideoutAccess) {
                     GL11.glPushMatrix();
                     GL11.glTranslatef(0.0F, 0.0F, 400.0F);
                  }

                  if(mod.getSlideCogMenu() != null && event.getEventType() == EventType.GUI_CLICK_MOUSE) {
                     if(mod.isPageOpen()) {
                        mod.onEvent(event);
                     }
                  } else {
                     mod.onEvent(event);
                  }

                  if(event instanceof RenderGame && mod instanceof SlideoutAccess) {
                     GL11.glPopMatrix();
                  }
               }
            }
         }
      }

      if(event instanceof RenderGame) {
         GL11.glPopAttrib();
         GlStateManager.disableBlend();
         GlStateManager.enableBlend();
         OpenGlHelper.glBlendFunc(770, 771, 1, 0);
         GlStateManager.disableAlpha();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

   }

   public boolean equals(Object obj) {
      if(!(obj instanceof ModProfile)) {
         return false;
      } else {
         ModProfile modprofile = (ModProfile)obj;
         return this.profileName.equalsIgnoreCase(modprofile.getProfileName());
      }
   }

   public String getProfileName() {
      return this.profileName;
   }

   public void setProfileName(String profileName) {
      this.profileName = profileName;
   }

   public boolean isActive() {
      return this.active;
   }

   public int getSortIndex() {
      return this.sortIndex;
   }

   public void setSortIndex(int sortIndex) {
      this.sortIndex = sortIndex;
   }

   public Integer getVersion() {
      return this.version;
   }

   public MiniMap getNewMiniMap() {
      return this.newMiniMap;
   }

   public Coordinates getCoordinates() {
      return this.coordinates;
   }

   public ToggleSneak getToggleSneak() {
      return this.toggleSneak;
   }

   public ArmorStatus getArmorStatus() {
      return this.armorStatus;
   }

   public PotionStatus getPotionStatus() {
      return this.potionStatus;
   }

   public ShowFPS getShowFPS() {
      return this.showFPS;
   }

   public ShowDirection getShowDirection() {
      return this.showDirection;
   }

   public ShowCPS getShowCPS() {
      return this.showCPS;
   }

   public Keystroke getKeyStroke() {
      return this.keyStroke;
   }

   public ShowArrows getShowArrows() {
      return this.showArrows;
   }

   public Fullbright getFullbright() {
      return this.fullbright;
   }

   public Hitboxes getHitboxes() {
      return this.hitboxes;
   }

   public TimeChanger getTimeChanger() {
      return this.timeChanger;
   }

   public ModConfigurator getModConfigurator() {
      return this.modConfigurator;
   }

   public SlideoutAccess getSlideoutAccess() {
      return this.slideoutAccess;
   }

   public List getAllMods() {
      return this.allMods;
   }

   public TcpNoDelay getTcpNoDelay() {
      return this.tcpNoDelay;
   }

   public ShowPing getShowPing() {
      return this.showPing;
   }

   public BetterframesConfig getBetterframesConfig() {
      return this.betterframesConfig;
   }

   public EnchantGlint getEnchantGlint() {
      return this.enchantGlint;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public Saturation getSaturation() {
      return this.saturation;
   }

   public Crosshair getCrosshair() {
      return this.crosshair;
   }

   public Chat getChat() {
      return this.chat;
   }

   public ChangeColorMod getChangeColorMod() {
      return this.changeColorMod;
   }

   public int getSlideoutKey() {
      return this.slideoutKey;
   }

   public void setSlideoutKey(int slideoutKey) {
      this.slideoutKey = slideoutKey;
   }

   public BlockOverlay getBlockOverlay() {
      return this.blockOverlay;
   }

   public FOVChanger getFovChanger() {
      return this.fovChanger;
   }

   public AutoGG getAutoGG() {
      return this.autoGG;
   }

   public void sortMods() {
      Collections.sort(this.allMods, new Comparator() {
         public int compare(Mod o1, Mod o2) {
            return o1.getDisplayName().compareTo(o2.getDisplayName());
         }
      });
      Collections.sort(this.allMods, new Comparator() {
         public int compare(Mod o1, Mod o2) {
            return o1.isFavorite()?-1:(o2.isFavorite()?1:0);
         }
      });
   }
}
