package net.minecraft.client.settings;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.stream.TwitchStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class GameSettings {
   private static final Logger gson = LogManager.getLogger();
   private static final Gson typeListString = new Gson();
   private static final ParameterizedType GUISCALES = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class};
      }

      public Type getRawType() {
         return List.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };
   private static final String[] PARTICLES = new String[]{"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
   private static final String[] AMBIENT_OCCLUSIONS = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
   private static final String[] STREAM_COMPRESSIONS = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
   private static final String[] STREAM_CHAT_MODES = new String[]{"options.stream.compression.low", "options.stream.compression.medium", "options.stream.compression.high"};
   private static final String[] STREAM_CHAT_FILTER_MODES = new String[]{"options.stream.chat.enabled.streaming", "options.stream.chat.enabled.always", "options.stream.chat.enabled.never"};
   private static final String[] STREAM_MIC_MODES = new String[]{"options.stream.chat.userFilter.all", "options.stream.chat.userFilter.subs", "options.stream.chat.userFilter.mods"};
   private static final String[] field_181149_aW = new String[]{"options.stream.mic_toggle.mute", "options.stream.mic_toggle.talk"};
   private static final String[] setModelParts = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
   public float mouseSensitivity = 0.5F;
   public boolean invertMouse;
   public int renderDistanceChunks = -1;
   public boolean viewBobbing = true;
   public boolean anaglyph;
   public boolean fboEnable = true;
   public int limitFramerate = 120;
   public int clouds = 2;
   public boolean fancyGraphics = true;
   public int ambientOcclusion = 2;
   public List resourcePacks = Lists.newArrayList();
   public List field_183018_l = Lists.newArrayList();
   public EntityPlayer.EnumChatVisibility chatVisibility = EntityPlayer.EnumChatVisibility.FULL;
   public boolean chatColours = true;
   public boolean chatLinks = true;
   public boolean chatLinksPrompt = true;
   public float chatOpacity = 1.0F;
   public boolean snooperEnabled = true;
   public boolean fullScreen;
   public boolean enableVsync = true;
   public boolean useVbo = false;
   public boolean allowBlockAlternatives = true;
   public boolean reducedDebugInfo = false;
   public boolean hideServerAddress;
   public boolean advancedItemTooltips;
   public boolean pauseOnLostFocus = true;
   private final Set mapSoundLevels = Sets.newHashSet((Object[])EnumPlayerModelParts.values());
   public boolean touchscreen;
   public int overrideWidth;
   public int overrideHeight;
   public boolean heldItemTooltips = true;
   public float chatScale = 1.0F;
   public float chatWidth = 1.0F;
   public float chatHeightUnfocused = 0.44366196F;
   public float chatHeightFocused = 1.0F;
   public boolean showInventoryAchievementHint = true;
   public int mipmapLevels = 4;
   private Map optionsFile = Maps.newEnumMap(SoundCategory.class);
   public float streamBytesPerPixel = 0.5F;
   public float streamMicVolume = 1.0F;
   public float streamGameVolume = 1.0F;
   public float streamKbps = 0.5412844F;
   public float streamFps = 0.31690142F;
   public int streamCompression = 1;
   public boolean streamSendMetadata = true;
   public String streamPreferredServer = "";
   public int streamChatEnabled = 0;
   public int streamChatUserFilter = 0;
   public int streamMicToggleBehavior = 0;
   public boolean field_181150_U = true;
   public boolean field_181151_V = true;
   public boolean keyBindForward = true;
   public KeyBinding keyBindLeft = new KeyBinding("key.forward", 17, "key.categories.movement");
   public KeyBinding keyBindBack = new KeyBinding("key.left", 30, "key.categories.movement");
   public KeyBinding keyBindRight = new KeyBinding("key.back", 31, "key.categories.movement");
   public KeyBinding keyBindJump = new KeyBinding("key.right", 32, "key.categories.movement");
   public KeyBinding keyBindSneak = new KeyBinding("key.jump", 57, "key.categories.movement");
   public KeyBinding keyBindSprint = new KeyBinding("key.sneak", 42, "key.categories.movement");
   public KeyBinding keyBindInventory = new KeyBinding("key.sprint", 29, "key.categories.movement");
   public KeyBinding keyBindUseItem = new KeyBinding("key.inventory", 18, "key.categories.inventory");
   public KeyBinding keyBindDrop = new KeyBinding("key.use", -99, "key.categories.gameplay");
   public KeyBinding keyBindAttack = new KeyBinding("key.drop", 16, "key.categories.gameplay");
   public KeyBinding keyBindPickBlock = new KeyBinding("key.attack", -100, "key.categories.gameplay");
   public KeyBinding keyBindChat = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
   public KeyBinding keyBindPlayerList = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
   public KeyBinding keyBindCommand = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
   public KeyBinding keyBindScreenshot = new KeyBinding("key.command", 53, "key.categories.multiplayer");
   public KeyBinding keyBindTogglePerspective = new KeyBinding("key.screenshot", 60, "key.categories.misc");
   public KeyBinding keyBindSmoothCamera = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
   public KeyBinding keyBindFullscreen = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
   public KeyBinding keyBindSpectatorOutlines = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
   public KeyBinding keyBindStreamStartStop = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
   public KeyBinding keyBindStreamPauseUnpause = new KeyBinding("key.streamStartStop", 64, "key.categories.stream");
   public KeyBinding keyBindStreamCommercials = new KeyBinding("key.streamPauseUnpause", 65, "key.categories.stream");
   public KeyBinding keyBindStreamToggleMic = new KeyBinding("key.streamCommercial", 0, "key.categories.stream");
   public KeyBinding keyBindsHotbar = new KeyBinding("key.streamToggleMic", 0, "key.categories.stream");
   public KeyBinding[] keyBindings = new KeyBinding[]{new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"), new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"), new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"), new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"), new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"), new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"), new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"), new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"), new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")};
   public KeyBinding[] mc;
   protected Minecraft difficulty;
   private File bc;
   public EnumDifficulty hideGUI;
   public boolean thirdPersonView;
   public int showDebugInfo;
   public boolean showDebugProfilerChart;
   public boolean field_181657_aC;
   public boolean lastServer;
   public String smoothCamera;
   public boolean debugCamEnable;
   public boolean fovSetting;
   public float gammaSetting;
   public float saturation;
   public float guiScale;
   public int particleSetting;
   public int language;
   public String forceUnicodeFont;
   public boolean logger;
   // $FF: synthetic field
   private static int[] $SWITCH_TABLE$net$minecraft$client$settings$GameSettings$Options;

   public GameSettings(Minecraft mcIn, File p_i46326_2_) {
      this.mc = (KeyBinding[])ArrayUtils.addAll((Object[])(new KeyBinding[]{this.keyBindPickBlock, this.keyBindDrop, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindInventory, this.keyBindAttack, this.keyBindUseItem, this.keyBindPlayerList, this.keyBindCommand, this.keyBindChat, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindFullscreen, this.keyBindStreamPauseUnpause, this.keyBindStreamCommercials, this.keyBindStreamToggleMic, this.keyBindsHotbar, this.keyBindSpectatorOutlines, this.keyBindStreamStartStop}), (Object[])this.keyBindings);
      this.hideGUI = EnumDifficulty.NORMAL;
      this.smoothCamera = "";
      this.gammaSetting = 70.0F;
      this.forceUnicodeFont = "en_US";
      this.logger = false;
      this.difficulty = mcIn;
      this.bc = new File(p_i46326_2_, "options.txt");
      if(mcIn.isJava64bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
         GameSettings.Options.RENDER_DISTANCE.setValueMax(32.0F);
      } else {
         GameSettings.Options.RENDER_DISTANCE.setValueMax(16.0F);
      }

      this.renderDistanceChunks = mcIn.isJava64bit()?12:8;
      this.loadOptions();
   }

   public GameSettings() {
      this.mc = (KeyBinding[])ArrayUtils.addAll((Object[])(new KeyBinding[]{this.keyBindPickBlock, this.keyBindDrop, this.keyBindLeft, this.keyBindBack, this.keyBindRight, this.keyBindJump, this.keyBindSneak, this.keyBindSprint, this.keyBindInventory, this.keyBindAttack, this.keyBindUseItem, this.keyBindPlayerList, this.keyBindCommand, this.keyBindChat, this.keyBindScreenshot, this.keyBindTogglePerspective, this.keyBindSmoothCamera, this.keyBindFullscreen, this.keyBindStreamPauseUnpause, this.keyBindStreamCommercials, this.keyBindStreamToggleMic, this.keyBindsHotbar, this.keyBindSpectatorOutlines, this.keyBindStreamStartStop}), (Object[])this.keyBindings);
      this.hideGUI = EnumDifficulty.NORMAL;
      this.smoothCamera = "";
      this.gammaSetting = 70.0F;
      this.forceUnicodeFont = "en_US";
      this.logger = false;
   }

   public static String getKeyDisplayString(int p_74298_0_) {
      return p_74298_0_ < 0?I18n.format("key.mouseButton", new Object[]{Integer.valueOf(p_74298_0_ + 101)}):(p_74298_0_ < 256?Keyboard.getKeyName(p_74298_0_):String.format("%c", new Object[]{Character.valueOf((char)(p_74298_0_ - 256))}).toUpperCase());
   }

   public static boolean isKeyDown(KeyBinding p_100015_0_) {
      return p_100015_0_.getKeyCode() == 0?false:(p_100015_0_.getKeyCode() < 0?Mouse.isButtonDown(p_100015_0_.getKeyCode() + 100):Keyboard.isKeyDown(p_100015_0_.getKeyCode()));
   }

   public void setOptionKeyBinding(KeyBinding p_151440_1_, int p_151440_2_) {
      p_151440_1_.setKeyCode(p_151440_2_);
      this.saveOptions();
   }

   public void setOptionFloatValue(GameSettings.Options p_74304_1_, float p_74304_2_) {
      if(p_74304_1_ == GameSettings.Options.SENSITIVITY) {
         this.mouseSensitivity = p_74304_2_;
      }

      if(p_74304_1_ == GameSettings.Options.FOV) {
         this.gammaSetting = p_74304_2_;
      }

      if(p_74304_1_ == GameSettings.Options.GAMMA) {
         this.saturation = p_74304_2_;
      }

      if(p_74304_1_ == GameSettings.Options.FRAMERATE_LIMIT) {
         this.limitFramerate = (int)p_74304_2_;
      }

      if(p_74304_1_ == GameSettings.Options.CHAT_OPACITY) {
         this.chatOpacity = p_74304_2_;
         this.difficulty.ingameGUI.getChatGUI().refreshChat();
      }

      if(p_74304_1_ == GameSettings.Options.CHAT_HEIGHT_FOCUSED) {
         this.chatHeightFocused = p_74304_2_;
         this.difficulty.ingameGUI.getChatGUI().refreshChat();
      }

      if(p_74304_1_ == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED) {
         this.chatHeightUnfocused = p_74304_2_;
         this.difficulty.ingameGUI.getChatGUI().refreshChat();
      }

      if(p_74304_1_ == GameSettings.Options.CHAT_WIDTH) {
         this.chatWidth = p_74304_2_;
         this.difficulty.ingameGUI.getChatGUI().refreshChat();
      }

      if(p_74304_1_ == GameSettings.Options.CHAT_SCALE) {
         this.chatScale = p_74304_2_;
         this.difficulty.ingameGUI.getChatGUI().refreshChat();
      }

      if(p_74304_1_ == GameSettings.Options.MIPMAP_LEVELS) {
         int i = this.mipmapLevels;
         this.mipmapLevels = (int)p_74304_2_;
         if((float)i != p_74304_2_) {
            this.difficulty.getTextureMapBlocks().setMipmapLevels(this.mipmapLevels);
            this.difficulty.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            this.difficulty.getTextureMapBlocks().setBlurMipmapDirect(false, this.mipmapLevels > 0);
            this.difficulty.scheduleResourcesRefresh();
         }
      }

      if(p_74304_1_ == GameSettings.Options.BLOCK_ALTERNATIVES) {
         this.allowBlockAlternatives = !this.allowBlockAlternatives;
         this.difficulty.renderGlobal.loadRenderers();
      }

      if(p_74304_1_ == GameSettings.Options.RENDER_DISTANCE) {
         this.renderDistanceChunks = (int)p_74304_2_;
         this.difficulty.renderGlobal.setDisplayListEntitiesDirty();
      }

      if(p_74304_1_ == GameSettings.Options.STREAM_BYTES_PER_PIXEL) {
         this.streamBytesPerPixel = p_74304_2_;
      }

      if(p_74304_1_ == GameSettings.Options.STREAM_VOLUME_MIC) {
         this.streamMicVolume = p_74304_2_;
         this.difficulty.getTwitchStream().updateStreamVolume();
      }

      if(p_74304_1_ == GameSettings.Options.STREAM_VOLUME_SYSTEM) {
         this.streamGameVolume = p_74304_2_;
         this.difficulty.getTwitchStream().updateStreamVolume();
      }

      if(p_74304_1_ == GameSettings.Options.STREAM_KBPS) {
         this.streamKbps = p_74304_2_;
      }

      if(p_74304_1_ == GameSettings.Options.STREAM_FPS) {
         this.streamFps = p_74304_2_;
      }

   }

   public void setOptionValue(GameSettings.Options p_74306_1_, int p_74306_2_) {
      if(p_74306_1_ == GameSettings.Options.INVERT_MOUSE) {
         this.invertMouse = !this.invertMouse;
      }

      if(p_74306_1_ == GameSettings.Options.GUI_SCALE) {
         this.particleSetting = this.particleSetting + p_74306_2_ & 3;
      }

      if(p_74306_1_ == GameSettings.Options.PARTICLES) {
         this.language = (this.language + p_74306_2_) % 3;
      }

      if(p_74306_1_ == GameSettings.Options.VIEW_BOBBING) {
         this.viewBobbing = !this.viewBobbing;
      }

      if(p_74306_1_ == GameSettings.Options.RENDER_CLOUDS) {
         this.clouds = (this.clouds + p_74306_2_) % 3;
      }

      if(p_74306_1_ == GameSettings.Options.FORCE_UNICODE_FONT) {
         this.logger = !this.logger;
         this.difficulty.fontRendererObj.setUnicodeFlag(this.difficulty.getLanguageManager().isCurrentLocaleUnicode() || this.logger);
      }

      if(p_74306_1_ == GameSettings.Options.FBO_ENABLE) {
         this.fboEnable = !this.fboEnable;
      }

      if(p_74306_1_ == GameSettings.Options.ANAGLYPH) {
         this.anaglyph = !this.anaglyph;
         this.difficulty.refreshResources();
      }

      if(p_74306_1_ == GameSettings.Options.GRAPHICS) {
         this.fancyGraphics = !this.fancyGraphics;
         this.difficulty.renderGlobal.loadRenderers();
      }

      if(p_74306_1_ == GameSettings.Options.AMBIENT_OCCLUSION) {
         this.ambientOcclusion = (this.ambientOcclusion + p_74306_2_) % 3;
         this.difficulty.renderGlobal.loadRenderers();
      }

      if(p_74306_1_ == GameSettings.Options.CHAT_VISIBILITY) {
         this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + p_74306_2_) % 3);
      }

      if(p_74306_1_ == GameSettings.Options.STREAM_COMPRESSION) {
         this.streamCompression = (this.streamCompression + p_74306_2_) % 3;
      }

      if(p_74306_1_ == GameSettings.Options.STREAM_SEND_METADATA) {
         this.streamSendMetadata = !this.streamSendMetadata;
      }

      if(p_74306_1_ == GameSettings.Options.STREAM_CHAT_ENABLED) {
         this.streamChatEnabled = (this.streamChatEnabled + p_74306_2_) % 3;
      }

      if(p_74306_1_ == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
         this.streamChatUserFilter = (this.streamChatUserFilter + p_74306_2_) % 3;
      }

      if(p_74306_1_ == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
         this.streamMicToggleBehavior = (this.streamMicToggleBehavior + p_74306_2_) % 2;
      }

      if(p_74306_1_ == GameSettings.Options.CHAT_COLOR) {
         this.chatColours = !this.chatColours;
      }

      if(p_74306_1_ == GameSettings.Options.CHAT_LINKS) {
         this.chatLinks = !this.chatLinks;
      }

      if(p_74306_1_ == GameSettings.Options.CHAT_LINKS_PROMPT) {
         this.chatLinksPrompt = !this.chatLinksPrompt;
      }

      if(p_74306_1_ == GameSettings.Options.SNOOPER_ENABLED) {
         this.snooperEnabled = !this.snooperEnabled;
      }

      if(p_74306_1_ == GameSettings.Options.TOUCHSCREEN) {
         this.touchscreen = !this.touchscreen;
      }

      if(p_74306_1_ == GameSettings.Options.USE_FULLSCREEN) {
         this.fullScreen = !this.fullScreen;
         if(this.difficulty.isFullScreen() != this.fullScreen) {
            this.difficulty.toggleFullscreen();
         }
      }

      if(p_74306_1_ == GameSettings.Options.ENABLE_VSYNC) {
         this.enableVsync = !this.enableVsync;
         Display.setVSyncEnabled(this.enableVsync);
      }

      if(p_74306_1_ == GameSettings.Options.USE_VBO) {
         this.useVbo = !this.useVbo;
         this.difficulty.renderGlobal.loadRenderers();
      }

      if(p_74306_1_ == GameSettings.Options.BLOCK_ALTERNATIVES) {
         this.allowBlockAlternatives = !this.allowBlockAlternatives;
         this.difficulty.renderGlobal.loadRenderers();
      }

      if(p_74306_1_ == GameSettings.Options.REDUCED_DEBUG_INFO) {
         this.reducedDebugInfo = !this.reducedDebugInfo;
      }

      if(p_74306_1_ == GameSettings.Options.ENTITY_SHADOWS) {
         this.field_181151_V = !this.field_181151_V;
      }

      if(p_74306_1_ == GameSettings.Options.enumFloat) {
         this.keyBindForward = !this.keyBindForward;
      }

      this.saveOptions();
   }

   public float getOptionFloatValue(GameSettings.Options p_74296_1_) {
      return p_74296_1_ == GameSettings.Options.FOV?this.gammaSetting:(p_74296_1_ == GameSettings.Options.GAMMA?this.saturation:(p_74296_1_ == GameSettings.Options.SATURATION?this.guiScale:(p_74296_1_ == GameSettings.Options.SENSITIVITY?this.mouseSensitivity:(p_74296_1_ == GameSettings.Options.CHAT_OPACITY?this.chatOpacity:(p_74296_1_ == GameSettings.Options.CHAT_HEIGHT_FOCUSED?this.chatHeightFocused:(p_74296_1_ == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED?this.chatHeightUnfocused:(p_74296_1_ == GameSettings.Options.CHAT_SCALE?this.chatScale:(p_74296_1_ == GameSettings.Options.CHAT_WIDTH?this.chatWidth:(p_74296_1_ == GameSettings.Options.FRAMERATE_LIMIT?(float)this.limitFramerate:(p_74296_1_ == GameSettings.Options.MIPMAP_LEVELS?(float)this.mipmapLevels:(p_74296_1_ == GameSettings.Options.RENDER_DISTANCE?(float)this.renderDistanceChunks:(p_74296_1_ == GameSettings.Options.STREAM_BYTES_PER_PIXEL?this.streamBytesPerPixel:(p_74296_1_ == GameSettings.Options.STREAM_VOLUME_MIC?this.streamMicVolume:(p_74296_1_ == GameSettings.Options.STREAM_VOLUME_SYSTEM?this.streamGameVolume:(p_74296_1_ == GameSettings.Options.STREAM_KBPS?this.streamKbps:(p_74296_1_ == GameSettings.Options.STREAM_FPS?this.streamFps:0.0F))))))))))))))));
   }

   public boolean getOptionOrdinalValue(GameSettings.Options p_74308_1_) {
      switch($SWITCH_TABLE$net$minecraft$client$settings$GameSettings$Options()[p_74308_1_.ordinal()]) {
      case 1:
         return this.invertMouse;
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 9:
      case 11:
      case 12:
      case 13:
      case 14:
      case 15:
      case 16:
      case 19:
      case 26:
      case 27:
      case 28:
      case 29:
      case 30:
      case 32:
      case 33:
      case 34:
      case 35:
      case 36:
      case 37:
      case 39:
      case 40:
      case 41:
      default:
         return false;
      case 7:
         return this.viewBobbing;
      case 8:
         return this.anaglyph;
      case 10:
         return this.fboEnable;
      case 17:
         return this.chatColours;
      case 18:
         return this.chatLinks;
      case 20:
         return this.chatLinksPrompt;
      case 21:
         return this.snooperEnabled;
      case 22:
         return this.fullScreen;
      case 23:
         return this.enableVsync;
      case 24:
         return this.useVbo;
      case 25:
         return this.touchscreen;
      case 31:
         return this.logger;
      case 38:
         return this.streamSendMetadata;
      case 42:
         return this.allowBlockAlternatives;
      case 43:
         return this.reducedDebugInfo;
      case 44:
         return this.field_181151_V;
      case 45:
         return this.keyBindForward;
      }
   }

   private static String getTranslation(String[] p_74299_0_, int p_74299_1_) {
      if(p_74299_1_ < 0 || p_74299_1_ >= p_74299_0_.length) {
         p_74299_1_ = 0;
      }

      return I18n.format(p_74299_0_[p_74299_1_], new Object[0]);
   }

   public String getKeyBinding(GameSettings.Options p_74297_1_) {
      String s = I18n.format(p_74297_1_.getEnumString(), new Object[0]) + ": ";
      if(p_74297_1_.getEnumFloat()) {
         float f1 = this.getOptionFloatValue(p_74297_1_);
         float f = p_74297_1_.normalizeValue(f1);
         return p_74297_1_ == GameSettings.Options.SENSITIVITY?(f == 0.0F?s + I18n.format("options.sensitivity.min", new Object[0]):(f == 1.0F?s + I18n.format("options.sensitivity.max", new Object[0]):s + (int)(f * 200.0F) + "%")):(p_74297_1_ == GameSettings.Options.FOV?(f1 == 70.0F?s + I18n.format("options.fov.min", new Object[0]):(f1 == 110.0F?s + I18n.format("options.fov.max", new Object[0]):s + (int)f1)):(p_74297_1_ == GameSettings.Options.FRAMERATE_LIMIT?(f1 == p_74297_1_.$VALUES?s + I18n.format("options.framerateLimit.max", new Object[0]):s + (int)f1 + " fps"):(p_74297_1_ == GameSettings.Options.RENDER_CLOUDS?(f1 == p_74297_1_.valueMax?s + I18n.format("options.cloudHeight.min", new Object[0]):s + ((int)f1 + 128)):(p_74297_1_ == GameSettings.Options.GAMMA?(f == 0.0F?s + I18n.format("options.gamma.min", new Object[0]):(f == 1.0F?s + I18n.format("options.gamma.max", new Object[0]):s + "+" + (int)(f * 100.0F) + "%")):(p_74297_1_ == GameSettings.Options.SATURATION?s + (int)(f * 400.0F) + "%":(p_74297_1_ == GameSettings.Options.CHAT_OPACITY?s + (int)(f * 90.0F + 10.0F) + "%":(p_74297_1_ == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED?s + GuiNewChat.calculateChatboxHeight(f) + "px":(p_74297_1_ == GameSettings.Options.CHAT_HEIGHT_FOCUSED?s + GuiNewChat.calculateChatboxHeight(f) + "px":(p_74297_1_ == GameSettings.Options.CHAT_WIDTH?s + GuiNewChat.calculateChatboxWidth(f) + "px":(p_74297_1_ == GameSettings.Options.RENDER_DISTANCE?s + (int)f1 + " chunks":(p_74297_1_ == GameSettings.Options.MIPMAP_LEVELS?(f1 == 0.0F?s + I18n.format("options.off", new Object[0]):s + (int)f1):(p_74297_1_ == GameSettings.Options.STREAM_FPS?s + TwitchStream.formatStreamFps(f) + " fps":(p_74297_1_ == GameSettings.Options.STREAM_KBPS?s + TwitchStream.formatStreamKbps(f) + " Kbps":(p_74297_1_ == GameSettings.Options.STREAM_BYTES_PER_PIXEL?s + String.format("%.3f bpp", new Object[]{Float.valueOf(TwitchStream.formatStreamBps(f))}):(f == 0.0F?s + I18n.format("options.off", new Object[0]):s + (int)(f * 100.0F) + "%")))))))))))))));
      } else if(p_74297_1_.getEnumBoolean()) {
         boolean flag = this.getOptionOrdinalValue(p_74297_1_);
         return flag?s + I18n.format("options.on", new Object[0]):s + I18n.format("options.off", new Object[0]);
      } else if(p_74297_1_ == GameSettings.Options.GUI_SCALE) {
         return s + getTranslation(PARTICLES, this.particleSetting);
      } else if(p_74297_1_ == GameSettings.Options.CHAT_VISIBILITY) {
         return s + I18n.format(this.chatVisibility.getResourceKey(), new Object[0]);
      } else if(p_74297_1_ == GameSettings.Options.PARTICLES) {
         return s + getTranslation(AMBIENT_OCCLUSIONS, this.language);
      } else if(p_74297_1_ == GameSettings.Options.AMBIENT_OCCLUSION) {
         return s + getTranslation(STREAM_COMPRESSIONS, this.ambientOcclusion);
      } else if(p_74297_1_ == GameSettings.Options.STREAM_COMPRESSION) {
         return s + getTranslation(STREAM_CHAT_MODES, this.streamCompression);
      } else if(p_74297_1_ == GameSettings.Options.STREAM_CHAT_ENABLED) {
         return s + getTranslation(STREAM_CHAT_FILTER_MODES, this.streamChatEnabled);
      } else if(p_74297_1_ == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
         return s + getTranslation(STREAM_MIC_MODES, this.streamChatUserFilter);
      } else if(p_74297_1_ == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
         return s + getTranslation(field_181149_aW, this.streamMicToggleBehavior);
      } else if(p_74297_1_ == GameSettings.Options.RENDER_CLOUDS) {
         return s + getTranslation(setModelParts, this.clouds);
      } else if(p_74297_1_ == GameSettings.Options.GRAPHICS) {
         if(this.fancyGraphics) {
            return s + I18n.format("options.graphics.fancy", new Object[0]);
         } else {
            String s1 = "options.graphics.fast";
            return s + I18n.format("options.graphics.fast", new Object[0]);
         }
      } else {
         return s;
      }
   }

   public void loadOptions() {
      try {
         if(!this.bc.exists()) {
            return;
         }

         BufferedReader bufferedreader = new BufferedReader(new FileReader(this.bc));
         String s = "";
         this.optionsFile.clear();

         while((s = bufferedreader.readLine()) != null) {
            try {
               String[] astring = s.split(":");
               if(astring[0].equals("mouseSensitivity")) {
                  this.mouseSensitivity = this.parseFloat(astring[1]);
               }

               if(astring[0].equals("fov")) {
                  this.gammaSetting = this.parseFloat(astring[1]) * 40.0F + 70.0F;
               }

               if(astring[0].equals("gamma")) {
                  this.saturation = this.parseFloat(astring[1]);
               }

               if(astring[0].equals("saturation")) {
                  this.guiScale = this.parseFloat(astring[1]);
               }

               if(astring[0].equals("invertYMouse")) {
                  this.invertMouse = astring[1].equals("true");
               }

               if(astring[0].equals("renderDistance")) {
                  this.renderDistanceChunks = Integer.parseInt(astring[1]);
               }

               if(astring[0].equals("guiScale")) {
                  this.particleSetting = Integer.parseInt(astring[1]);
               }

               if(astring[0].equals("particles")) {
                  this.language = Integer.parseInt(astring[1]);
               }

               if(astring[0].equals("bobView")) {
                  this.viewBobbing = astring[1].equals("true");
               }

               if(astring[0].equals("anaglyph3d")) {
                  this.anaglyph = astring[1].equals("true");
               }

               if(astring[0].equals("maxFps")) {
                  this.limitFramerate = Integer.parseInt(astring[1]);
               }

               if(astring[0].equals("fboEnable")) {
                  this.fboEnable = astring[1].equals("true");
               }

               if(astring[0].equals("difficulty")) {
                  this.hideGUI = EnumDifficulty.getDifficultyEnum(Integer.parseInt(astring[1]));
               }

               if(astring[0].equals("fancyGraphics")) {
                  this.fancyGraphics = astring[1].equals("true");
               }

               if(astring[0].equals("ao")) {
                  if(astring[1].equals("true")) {
                     this.ambientOcclusion = 2;
                  } else if(astring[1].equals("false")) {
                     this.ambientOcclusion = 0;
                  } else {
                     this.ambientOcclusion = Integer.parseInt(astring[1]);
                  }
               }

               if(astring[0].equals("renderClouds")) {
                  if(astring[1].equals("true")) {
                     this.clouds = 2;
                  } else if(astring[1].equals("false")) {
                     this.clouds = 0;
                  } else if(astring[1].equals("fast")) {
                     this.clouds = 1;
                  }
               }

               if(astring[0].equals("resourcePacks")) {
                  this.resourcePacks = (List)typeListString.fromJson((String)s.substring(s.indexOf(58) + 1), (Type)GUISCALES);
                  if(this.resourcePacks == null) {
                     this.resourcePacks = Lists.newArrayList();
                  }
               }

               if(astring[0].equals("incompatibleResourcePacks")) {
                  this.field_183018_l = (List)typeListString.fromJson((String)s.substring(s.indexOf(58) + 1), (Type)GUISCALES);
                  if(this.field_183018_l == null) {
                     this.field_183018_l = Lists.newArrayList();
                  }
               }

               if(astring[0].equals("lastServer") && astring.length >= 2) {
                  this.smoothCamera = s.substring(s.indexOf(58) + 1);
               }

               if(astring[0].equals("lang") && astring.length >= 2) {
                  this.forceUnicodeFont = astring[1];
               }

               if(astring[0].equals("chatVisibility")) {
                  this.chatVisibility = EntityPlayer.EnumChatVisibility.getEnumChatVisibility(Integer.parseInt(astring[1]));
               }

               if(astring[0].equals("chatColors")) {
                  this.chatColours = astring[1].equals("true");
               }

               if(astring[0].equals("chatLinks")) {
                  this.chatLinks = astring[1].equals("true");
               }

               if(astring[0].equals("chatLinksPrompt")) {
                  this.chatLinksPrompt = astring[1].equals("true");
               }

               if(astring[0].equals("chatOpacity")) {
                  this.chatOpacity = this.parseFloat(astring[1]);
               }

               if(astring[0].equals("snooperEnabled")) {
                  this.snooperEnabled = astring[1].equals("true");
               }

               if(astring[0].equals("fullscreen")) {
                  this.fullScreen = astring[1].equals("true");
               }

               if(astring[0].equals("enableVsync")) {
                  this.enableVsync = astring[1].equals("true");
               }

               if(astring[0].equals("useVbo")) {
                  this.useVbo = astring[1].equals("true");
               }

               if(astring[0].equals("hideServerAddress")) {
                  this.hideServerAddress = astring[1].equals("true");
               }

               if(astring[0].equals("advancedItemTooltips")) {
                  this.advancedItemTooltips = astring[1].equals("true");
               }

               if(astring[0].equals("pauseOnLostFocus")) {
                  this.pauseOnLostFocus = astring[1].equals("true");
               }

               if(astring[0].equals("touchscreen")) {
                  this.touchscreen = astring[1].equals("true");
               }

               if(astring[0].equals("overrideHeight")) {
                  this.overrideHeight = Integer.parseInt(astring[1]);
               }

               if(astring[0].equals("overrideWidth")) {
                  this.overrideWidth = Integer.parseInt(astring[1]);
               }

               if(astring[0].equals("heldItemTooltips")) {
                  this.heldItemTooltips = astring[1].equals("true");
               }

               if(astring[0].equals("chatHeightFocused")) {
                  this.chatHeightFocused = this.parseFloat(astring[1]);
               }

               if(astring[0].equals("chatHeightUnfocused")) {
                  this.chatHeightUnfocused = this.parseFloat(astring[1]);
               }

               if(astring[0].equals("chatScale")) {
                  this.chatScale = this.parseFloat(astring[1]);
               }

               if(astring[0].equals("chatWidth")) {
                  this.chatWidth = this.parseFloat(astring[1]);
               }

               if(astring[0].equals("showInventoryAchievementHint")) {
                  this.showInventoryAchievementHint = astring[1].equals("true");
               }

               if(astring[0].equals("mipmapLevels")) {
                  this.mipmapLevels = Integer.parseInt(astring[1]);
               }

               if(astring[0].equals("streamBytesPerPixel")) {
                  this.streamBytesPerPixel = this.parseFloat(astring[1]);
               }

               if(astring[0].equals("streamMicVolume")) {
                  this.streamMicVolume = this.parseFloat(astring[1]);
               }

               if(astring[0].equals("streamSystemVolume")) {
                  this.streamGameVolume = this.parseFloat(astring[1]);
               }

               if(astring[0].equals("streamKbps")) {
                  this.streamKbps = this.parseFloat(astring[1]);
               }

               if(astring[0].equals("streamFps")) {
                  this.streamFps = this.parseFloat(astring[1]);
               }

               if(astring[0].equals("streamCompression")) {
                  this.streamCompression = Integer.parseInt(astring[1]);
               }

               if(astring[0].equals("streamSendMetadata")) {
                  this.streamSendMetadata = astring[1].equals("true");
               }

               if(astring[0].equals("streamPreferredServer") && astring.length >= 2) {
                  this.streamPreferredServer = s.substring(s.indexOf(58) + 1);
               }

               if(astring[0].equals("streamChatEnabled")) {
                  this.streamChatEnabled = Integer.parseInt(astring[1]);
               }

               if(astring[0].equals("streamChatUserFilter")) {
                  this.streamChatUserFilter = Integer.parseInt(astring[1]);
               }

               if(astring[0].equals("streamMicToggleBehavior")) {
                  this.streamMicToggleBehavior = Integer.parseInt(astring[1]);
               }

               if(astring[0].equals("forceUnicodeFont")) {
                  this.logger = astring[1].equals("true");
               }

               if(astring[0].equals("allowBlockAlternatives")) {
                  this.allowBlockAlternatives = astring[1].equals("true");
               }

               if(astring[0].equals("reducedDebugInfo")) {
                  this.reducedDebugInfo = astring[1].equals("true");
               }

               if(astring[0].equals("useNativeTransport")) {
                  this.field_181150_U = astring[1].equals("true");
               }

               if(astring[0].equals("entityShadows")) {
                  this.field_181151_V = astring[1].equals("true");
               }

               if(astring[0].equals("realmsNotifications")) {
                  this.keyBindForward = astring[1].equals("true");
               }

               for(KeyBinding keybinding : this.mc) {
                  if(astring[0].equals("key_" + keybinding.getKeyDescription())) {
                     keybinding.setKeyCode(Integer.parseInt(astring[1]));
                  }
               }

               SoundCategory[] var17;
               for(SoundCategory soundcategory : var17 = SoundCategory.values()) {
                  if(astring[0].equals("soundCategory_" + soundcategory.getCategoryName())) {
                     this.optionsFile.put(soundcategory, Float.valueOf(this.parseFloat(astring[1])));
                  }
               }

               for(EnumPlayerModelParts enumplayermodelparts : var18 = EnumPlayerModelParts.values()) {
                  if(astring[0].equals("modelPart_" + enumplayermodelparts.getPartName())) {
                     this.setModelPartEnabled(enumplayermodelparts, astring[1].equals("true"));
                  }
               }
            } catch (Exception var8) {
               gson.warn("Skipping bad option: " + s);
            }
         }

         KeyBinding.resetKeyBindingArrayAndHash();
         bufferedreader.close();
      } catch (Exception var9) {
         gson.error((String)"Failed to load options", (Throwable)var9);
      }

   }

   private float parseFloat(String p_74305_1_) {
      return p_74305_1_.equals("true")?1.0F:(p_74305_1_.equals("false")?0.0F:Float.parseFloat(p_74305_1_));
   }

   public void saveOptions() {
      try {
         PrintWriter printwriter = new PrintWriter(new FileWriter(this.bc));
         printwriter.println("invertYMouse:" + this.invertMouse);
         printwriter.println("mouseSensitivity:" + this.mouseSensitivity);
         printwriter.println("fov:" + (this.gammaSetting - 70.0F) / 40.0F);
         printwriter.println("gamma:" + this.saturation);
         printwriter.println("saturation:" + this.guiScale);
         printwriter.println("renderDistance:" + this.renderDistanceChunks);
         printwriter.println("guiScale:" + this.particleSetting);
         printwriter.println("particles:" + this.language);
         printwriter.println("bobView:" + this.viewBobbing);
         printwriter.println("anaglyph3d:" + this.anaglyph);
         printwriter.println("maxFps:" + this.limitFramerate);
         printwriter.println("fboEnable:" + this.fboEnable);
         printwriter.println("difficulty:" + this.hideGUI.getDifficultyId());
         printwriter.println("fancyGraphics:" + this.fancyGraphics);
         printwriter.println("ao:" + this.ambientOcclusion);
         switch(this.clouds) {
         case 0:
            printwriter.println("renderClouds:false");
            break;
         case 1:
            printwriter.println("renderClouds:fast");
            break;
         case 2:
            printwriter.println("renderClouds:true");
         }

         printwriter.println("resourcePacks:" + typeListString.toJson((Object)this.resourcePacks));
         printwriter.println("incompatibleResourcePacks:" + typeListString.toJson((Object)this.field_183018_l));
         printwriter.println("lastServer:" + this.smoothCamera);
         printwriter.println("lang:" + this.forceUnicodeFont);
         printwriter.println("chatVisibility:" + this.chatVisibility.getChatVisibility());
         printwriter.println("chatColors:" + this.chatColours);
         printwriter.println("chatLinks:" + this.chatLinks);
         printwriter.println("chatLinksPrompt:" + this.chatLinksPrompt);
         printwriter.println("chatOpacity:" + this.chatOpacity);
         printwriter.println("snooperEnabled:" + this.snooperEnabled);
         printwriter.println("fullscreen:" + this.fullScreen);
         printwriter.println("enableVsync:" + this.enableVsync);
         printwriter.println("useVbo:" + this.useVbo);
         printwriter.println("hideServerAddress:" + this.hideServerAddress);
         printwriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
         printwriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
         printwriter.println("touchscreen:" + this.touchscreen);
         printwriter.println("overrideWidth:" + this.overrideWidth);
         printwriter.println("overrideHeight:" + this.overrideHeight);
         printwriter.println("heldItemTooltips:" + this.heldItemTooltips);
         printwriter.println("chatHeightFocused:" + this.chatHeightFocused);
         printwriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
         printwriter.println("chatScale:" + this.chatScale);
         printwriter.println("chatWidth:" + this.chatWidth);
         printwriter.println("showInventoryAchievementHint:" + this.showInventoryAchievementHint);
         printwriter.println("mipmapLevels:" + this.mipmapLevels);
         printwriter.println("streamBytesPerPixel:" + this.streamBytesPerPixel);
         printwriter.println("streamMicVolume:" + this.streamMicVolume);
         printwriter.println("streamSystemVolume:" + this.streamGameVolume);
         printwriter.println("streamKbps:" + this.streamKbps);
         printwriter.println("streamFps:" + this.streamFps);
         printwriter.println("streamCompression:" + this.streamCompression);
         printwriter.println("streamSendMetadata:" + this.streamSendMetadata);
         printwriter.println("streamPreferredServer:" + this.streamPreferredServer);
         printwriter.println("streamChatEnabled:" + this.streamChatEnabled);
         printwriter.println("streamChatUserFilter:" + this.streamChatUserFilter);
         printwriter.println("streamMicToggleBehavior:" + this.streamMicToggleBehavior);
         printwriter.println("forceUnicodeFont:" + this.logger);
         printwriter.println("allowBlockAlternatives:" + this.allowBlockAlternatives);
         printwriter.println("reducedDebugInfo:" + this.reducedDebugInfo);
         printwriter.println("useNativeTransport:" + this.field_181150_U);
         printwriter.println("entityShadows:" + this.field_181151_V);
         printwriter.println("realmsNotifications:" + this.keyBindForward);

         for(KeyBinding keybinding : this.mc) {
            printwriter.println("key_" + keybinding.getKeyDescription() + ":" + keybinding.getKeyCode());
         }

         SoundCategory[] var13;
         for(SoundCategory soundcategory : var13 = SoundCategory.values()) {
            printwriter.println("soundCategory_" + soundcategory.getCategoryName() + ":" + this.getSoundLevel(soundcategory));
         }

         for(EnumPlayerModelParts enumplayermodelparts : var14 = EnumPlayerModelParts.values()) {
            printwriter.println("modelPart_" + enumplayermodelparts.getPartName() + ":" + this.mapSoundLevels.contains(enumplayermodelparts));
         }

         printwriter.close();
      } catch (Exception var6) {
         gson.error((String)"Failed to save options", (Throwable)var6);
      }

      this.sendSettingsToServer();
   }

   public float getSoundLevel(SoundCategory p_151438_1_) {
      return this.optionsFile.containsKey(p_151438_1_)?((Float)this.optionsFile.get(p_151438_1_)).floatValue():1.0F;
   }

   public void setSoundLevel(SoundCategory p_151439_1_, float p_151439_2_) {
      this.difficulty.getSoundHandler().setSoundLevel(p_151439_1_, p_151439_2_);
      this.optionsFile.put(p_151439_1_, Float.valueOf(p_151439_2_));
   }

   public void sendSettingsToServer() {
      if(this.difficulty.thePlayer != null) {
         int i = 0;

         for(EnumPlayerModelParts enumplayermodelparts : this.mapSoundLevels) {
            i |= enumplayermodelparts.getPartMask();
         }

         this.difficulty.thePlayer.sendQueue.addToSendQueue(new C15PacketClientSettings(this.forceUnicodeFont, this.renderDistanceChunks, this.chatVisibility, this.chatColours, i));
      }

   }

   public Set getModelParts() {
      return ImmutableSet.copyOf((Collection)this.mapSoundLevels);
   }

   public void setModelPartEnabled(EnumPlayerModelParts p_178878_1_, boolean p_178878_2_) {
      if(p_178878_2_) {
         this.mapSoundLevels.add(p_178878_1_);
      } else {
         this.mapSoundLevels.remove(p_178878_1_);
      }

      this.sendSettingsToServer();
   }

   public void switchModelPartEnabled(EnumPlayerModelParts p_178877_1_) {
      if(!this.getModelParts().contains(p_178877_1_)) {
         this.mapSoundLevels.add(p_178877_1_);
      } else {
         this.mapSoundLevels.remove(p_178877_1_);
      }

      this.sendSettingsToServer();
   }

   public int func_181147_e() {
      return this.renderDistanceChunks >= 4?this.clouds:0;
   }

   public boolean func_181148_f() {
      return this.field_181150_U;
   }

   // $FF: synthetic method
   static int[] $SWITCH_TABLE$net$minecraft$client$settings$GameSettings$Options() {
      int[] var10000 = $SWITCH_TABLE$net$minecraft$client$settings$GameSettings$Options;
      if($SWITCH_TABLE$net$minecraft$client$settings$GameSettings$Options != null) {
         return var10000;
      } else {
         int[] var0 = new int[GameSettings.Options.values().length];

         try {
            var0[GameSettings.Options.AMBIENT_OCCLUSION.ordinal()] = 13;
         } catch (NoSuchFieldError var45) {
            ;
         }

         try {
            var0[GameSettings.Options.ANAGLYPH.ordinal()] = 8;
         } catch (NoSuchFieldError var44) {
            ;
         }

         try {
            var0[GameSettings.Options.BLOCK_ALTERNATIVES.ordinal()] = 42;
         } catch (NoSuchFieldError var43) {
            ;
         }

         try {
            var0[GameSettings.Options.CHAT_COLOR.ordinal()] = 17;
         } catch (NoSuchFieldError var42) {
            ;
         }

         try {
            var0[GameSettings.Options.CHAT_HEIGHT_FOCUSED.ordinal()] = 28;
         } catch (NoSuchFieldError var41) {
            ;
         }

         try {
            var0[GameSettings.Options.CHAT_HEIGHT_UNFOCUSED.ordinal()] = 29;
         } catch (NoSuchFieldError var40) {
            ;
         }

         try {
            var0[GameSettings.Options.CHAT_LINKS.ordinal()] = 18;
         } catch (NoSuchFieldError var39) {
            ;
         }

         try {
            var0[GameSettings.Options.CHAT_LINKS_PROMPT.ordinal()] = 20;
         } catch (NoSuchFieldError var38) {
            ;
         }

         try {
            var0[GameSettings.Options.CHAT_OPACITY.ordinal()] = 19;
         } catch (NoSuchFieldError var37) {
            ;
         }

         try {
            var0[GameSettings.Options.CHAT_SCALE.ordinal()] = 26;
         } catch (NoSuchFieldError var36) {
            ;
         }

         try {
            var0[GameSettings.Options.CHAT_VISIBILITY.ordinal()] = 16;
         } catch (NoSuchFieldError var35) {
            ;
         }

         try {
            var0[GameSettings.Options.CHAT_WIDTH.ordinal()] = 27;
         } catch (NoSuchFieldError var34) {
            ;
         }

         try {
            var0[GameSettings.Options.ENABLE_VSYNC.ordinal()] = 23;
         } catch (NoSuchFieldError var33) {
            ;
         }

         try {
            var0[GameSettings.Options.ENTITY_SHADOWS.ordinal()] = 44;
         } catch (NoSuchFieldError var32) {
            ;
         }

         try {
            var0[GameSettings.Options.FBO_ENABLE.ordinal()] = 10;
         } catch (NoSuchFieldError var31) {
            ;
         }

         try {
            var0[GameSettings.Options.FORCE_UNICODE_FONT.ordinal()] = 31;
         } catch (NoSuchFieldError var30) {
            ;
         }

         try {
            var0[GameSettings.Options.FOV.ordinal()] = 3;
         } catch (NoSuchFieldError var29) {
            ;
         }

         try {
            var0[GameSettings.Options.FRAMERATE_LIMIT.ordinal()] = 9;
         } catch (NoSuchFieldError var28) {
            ;
         }

         try {
            var0[GameSettings.Options.GAMMA.ordinal()] = 4;
         } catch (NoSuchFieldError var27) {
            ;
         }

         try {
            var0[GameSettings.Options.GRAPHICS.ordinal()] = 12;
         } catch (NoSuchFieldError var26) {
            ;
         }

         try {
            var0[GameSettings.Options.GUI_SCALE.ordinal()] = 14;
         } catch (NoSuchFieldError var25) {
            ;
         }

         try {
            var0[GameSettings.Options.INVERT_MOUSE.ordinal()] = 1;
         } catch (NoSuchFieldError var24) {
            ;
         }

         try {
            var0[GameSettings.Options.MIPMAP_LEVELS.ordinal()] = 30;
         } catch (NoSuchFieldError var23) {
            ;
         }

         try {
            var0[GameSettings.Options.PARTICLES.ordinal()] = 15;
         } catch (NoSuchFieldError var22) {
            ;
         }

         try {
            var0[GameSettings.Options.REDUCED_DEBUG_INFO.ordinal()] = 43;
         } catch (NoSuchFieldError var21) {
            ;
         }

         try {
            var0[GameSettings.Options.RENDER_CLOUDS.ordinal()] = 11;
         } catch (NoSuchFieldError var20) {
            ;
         }

         try {
            var0[GameSettings.Options.RENDER_DISTANCE.ordinal()] = 6;
         } catch (NoSuchFieldError var19) {
            ;
         }

         try {
            var0[GameSettings.Options.SATURATION.ordinal()] = 5;
         } catch (NoSuchFieldError var18) {
            ;
         }

         try {
            var0[GameSettings.Options.SENSITIVITY.ordinal()] = 2;
         } catch (NoSuchFieldError var17) {
            ;
         }

         try {
            var0[GameSettings.Options.SNOOPER_ENABLED.ordinal()] = 21;
         } catch (NoSuchFieldError var16) {
            ;
         }

         try {
            var0[GameSettings.Options.STREAM_BYTES_PER_PIXEL.ordinal()] = 32;
         } catch (NoSuchFieldError var15) {
            ;
         }

         try {
            var0[GameSettings.Options.STREAM_CHAT_ENABLED.ordinal()] = 39;
         } catch (NoSuchFieldError var14) {
            ;
         }

         try {
            var0[GameSettings.Options.STREAM_CHAT_USER_FILTER.ordinal()] = 40;
         } catch (NoSuchFieldError var13) {
            ;
         }

         try {
            var0[GameSettings.Options.STREAM_COMPRESSION.ordinal()] = 37;
         } catch (NoSuchFieldError var12) {
            ;
         }

         try {
            var0[GameSettings.Options.STREAM_FPS.ordinal()] = 36;
         } catch (NoSuchFieldError var11) {
            ;
         }

         try {
            var0[GameSettings.Options.STREAM_KBPS.ordinal()] = 35;
         } catch (NoSuchFieldError var10) {
            ;
         }

         try {
            var0[GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR.ordinal()] = 41;
         } catch (NoSuchFieldError var9) {
            ;
         }

         try {
            var0[GameSettings.Options.STREAM_SEND_METADATA.ordinal()] = 38;
         } catch (NoSuchFieldError var8) {
            ;
         }

         try {
            var0[GameSettings.Options.STREAM_VOLUME_MIC.ordinal()] = 33;
         } catch (NoSuchFieldError var7) {
            ;
         }

         try {
            var0[GameSettings.Options.STREAM_VOLUME_SYSTEM.ordinal()] = 34;
         } catch (NoSuchFieldError var6) {
            ;
         }

         try {
            var0[GameSettings.Options.TOUCHSCREEN.ordinal()] = 25;
         } catch (NoSuchFieldError var5) {
            ;
         }

         try {
            var0[GameSettings.Options.USE_FULLSCREEN.ordinal()] = 22;
         } catch (NoSuchFieldError var4) {
            ;
         }

         try {
            var0[GameSettings.Options.USE_VBO.ordinal()] = 24;
         } catch (NoSuchFieldError var3) {
            ;
         }

         try {
            var0[GameSettings.Options.VIEW_BOBBING.ordinal()] = 7;
         } catch (NoSuchFieldError var2) {
            ;
         }

         try {
            var0[GameSettings.Options.enumFloat.ordinal()] = 45;
         } catch (NoSuchFieldError var1) {
            ;
         }

         $SWITCH_TABLE$net$minecraft$client$settings$GameSettings$Options = var0;
         return var0;
      }
   }

   public static enum Options {
      INVERT_MOUSE("options.invertMouse", false, true),
      SENSITIVITY("options.sensitivity", true, false),
      FOV("options.fov", true, false, 30.0F, 110.0F, 1.0F),
      GAMMA("options.gamma", true, false),
      SATURATION("options.saturation", true, false),
      RENDER_DISTANCE("options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
      VIEW_BOBBING("options.viewBobbing", false, true),
      ANAGLYPH("options.anaglyph", false, true),
      FRAMERATE_LIMIT("options.framerateLimit", true, false, 10.0F, 260.0F, 10.0F),
      FBO_ENABLE("options.fboEnable", false, true),
      RENDER_CLOUDS("options.renderClouds", false, false),
      GRAPHICS("options.graphics", false, false),
      AMBIENT_OCCLUSION("options.ao", false, false),
      GUI_SCALE("options.guiScale", false, false),
      PARTICLES("options.particles", false, false),
      CHAT_VISIBILITY("options.chat.visibility", false, false),
      CHAT_COLOR("options.chat.color", false, true),
      CHAT_LINKS("options.chat.links", false, true),
      CHAT_OPACITY("options.chat.opacity", true, false),
      CHAT_LINKS_PROMPT("options.chat.links.prompt", false, true),
      SNOOPER_ENABLED("options.snooper", false, true),
      USE_FULLSCREEN("options.fullscreen", false, true),
      ENABLE_VSYNC("options.vsync", false, true),
      USE_VBO("options.vbo", false, true),
      TOUCHSCREEN("options.touchscreen", false, true),
      CHAT_SCALE("options.chat.scale", true, false),
      CHAT_WIDTH("options.chat.width", true, false),
      CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
      CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
      MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
      FORCE_UNICODE_FONT("options.forceUnicodeFont", false, true),
      STREAM_BYTES_PER_PIXEL("options.stream.bytesPerPixel", true, false),
      STREAM_VOLUME_MIC("options.stream.micVolumne", true, false),
      STREAM_VOLUME_SYSTEM("options.stream.systemVolume", true, false),
      STREAM_KBPS("options.stream.kbps", true, false),
      STREAM_FPS("options.stream.fps", true, false),
      STREAM_COMPRESSION("options.stream.compression", false, false),
      STREAM_SEND_METADATA("options.stream.sendMetadata", false, true),
      STREAM_CHAT_ENABLED("options.stream.chat.enabled", false, false),
      STREAM_CHAT_USER_FILTER("options.stream.chat.userFilter", false, false),
      STREAM_MIC_TOGGLE_BEHAVIOR("options.stream.micToggleBehavior", false, false),
      BLOCK_ALTERNATIVES("options.blockAlternatives", false, true),
      REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true),
      ENTITY_SHADOWS("options.entityShadows", false, true),
      enumFloat("options.realmsNotifications", false, true);

      private final boolean enumBoolean;
      private final boolean enumString;
      private final String valueStep;
      private final float valueMin;
      private float valueMax;
      private float $VALUES;

      public static GameSettings.Options getEnumOptions(int p_74379_0_) {
         GameSettings.Options[] var4;
         for(GameSettings.Options gamesettings$options : var4 = values()) {
            if(gamesettings$options.returnEnumOrdinal() == p_74379_0_) {
               return gamesettings$options;
            }
         }

         return null;
      }

      private Options(String p_i1015_3_, boolean p_i1015_4_, boolean p_i1015_5_) {
         this(p_i1015_3_, p_i1015_4_, p_i1015_5_, 0.0F, 1.0F, 0.0F);
      }

      private Options(String p_i45004_3_, boolean p_i45004_4_, boolean p_i45004_5_, float p_i45004_6_, float p_i45004_7_, float p_i45004_8_) {
         this.valueStep = p_i45004_3_;
         this.enumBoolean = p_i45004_4_;
         this.enumString = p_i45004_5_;
         this.valueMax = p_i45004_6_;
         this.$VALUES = p_i45004_7_;
         this.valueMin = p_i45004_8_;
      }

      public boolean getEnumFloat() {
         return this.enumBoolean;
      }

      public boolean getEnumBoolean() {
         return this.enumString;
      }

      public int returnEnumOrdinal() {
         return this.ordinal();
      }

      public String getEnumString() {
         return this.valueStep;
      }

      public float getValueMax() {
         return this.$VALUES;
      }

      public void setValueMax(float p_148263_1_) {
         this.$VALUES = p_148263_1_;
      }

      public float normalizeValue(float p_148266_1_) {
         return MathHelper.clamp_float((this.snapToStepClamp(p_148266_1_) - this.valueMax) / (this.$VALUES - this.valueMax), 0.0F, 1.0F);
      }

      public float denormalizeValue(float p_148262_1_) {
         return this.snapToStepClamp(this.valueMax + (this.$VALUES - this.valueMax) * MathHelper.clamp_float(p_148262_1_, 0.0F, 1.0F));
      }

      public float snapToStepClamp(float p_148268_1_) {
         p_148268_1_ = this.snapToStep(p_148268_1_);
         return MathHelper.clamp_float(p_148268_1_, this.valueMax, this.$VALUES);
      }

      protected float snapToStep(float p_148264_1_) {
         if(this.valueMin > 0.0F) {
            p_148264_1_ = this.valueMin * (float)Math.round(p_148264_1_ / this.valueMin);
         }

         return p_148264_1_;
      }
   }
}
