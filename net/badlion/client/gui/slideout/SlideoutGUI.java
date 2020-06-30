package net.badlion.client.gui.slideout;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.event.GUIClickMouse;
import net.badlion.client.events.event.GUIKeyPress;
import net.badlion.client.events.event.KeyPress;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.events.event.RenderGame;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.GuiScreenEditing;
import net.badlion.client.gui.InputField;
import net.badlion.client.gui.slideout.Dropdown;
import net.badlion.client.gui.slideout.GuiScreenSlideout;
import net.badlion.client.gui.slideout.ImageButton;
import net.badlion.client.gui.slideout.KeyBindElement;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.ModButton;
import net.badlion.client.gui.slideout.ModProfilesButton;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.RenderElement;
import net.badlion.client.gui.slideout.SearchField;
import net.badlion.client.gui.slideout.SimpleButton;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.TextButton;
import net.badlion.client.gui.slideout.elements.ColorPicker;
import net.badlion.client.gui.slideout.fontrenderer.CustomFontRenderer;
import net.badlion.client.mods.Mod;
import net.badlion.client.mods.misc.LegacyAnimations;
import net.badlion.client.mods.misc.SlideoutAccess;
import net.badlion.client.mods.render.ChangeColorMod;
import net.badlion.client.mods.render.ModConfigurator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.LogManager;
import org.lwjgl.opengl.GL11;

public class SlideoutGUI {
   public static final transient String MODPAGE = "modPage";
   public static final transient String BETTERFRAMESPAGE = "betterframesPage";
   public static final transient String SLIDEOUTSETTINGSPAGE = "slideoutSettings";
   private HashMap pages = new HashMap();
   ResourceLocation backButtonRes = new ResourceLocation("textures/slideout/cogwheel/back-button.svg_large.png");
   ResourceLocation forwardButtonRes = new ResourceLocation("textures/slideout/cogwheel/forward-button.svg_large.png");
   private ResourceLocation badlionLogo = new ResourceLocation("textures/slideout/mods/BL-menu-header.svg_large_sizable.png");
   private ResourceLocation modsLogo = new ResourceLocation("textures/slideout/mods/MODS.svg_large.png");
   private ResourceLocation betterframesLogo = new ResourceLocation("textures/slideout/betterframes/BETTERFRAMES.svg_large.png");
   private ResourceLocation mul = new ResourceLocation("textures/slideout/mods/blue-underline.svg_large.png");
   private ResourceLocation bful = new ResourceLocation("textures/slideout/betterframes/blue-underline.svg_large.png");
   private ResourceLocation cogWheel = new ResourceLocation("textures/slideout/mods/cog.svg_large.png");
   private CustomFontRenderer fontRenderer;
   private SlideoutAccess access = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess();
   private Minecraft mc = Minecraft.getMinecraft();
   private long visibleTime;
   private boolean visible;
   private String search;
   private ImageButton cogSetting;
   private ImageButton backCogButton;
   private ModProfilesButton modProfilesButton;
   private SimpleButton guiEnable;
   private MutableBoolean quickSlideout = new MutableBoolean(false);
   private transient String selectedPage = "modPage";
   public transient double slide;
   private TextButton itemHeld;
   private TextButton blockHit;
   private TextButton heartAnimation;
   private TextButton damageAnimation;
   private TextButton sneakingAnimation;
   private Timer timer;
   private boolean blur;
   private boolean wasVisible;
   private int lastDisplayWidth;
   private int lastDisplayHeight;
   private static final ResourceLocation[] shaderResourceLocations = new ResourceLocation[]{new ResourceLocation("shaders/post/notch.json"), new ResourceLocation("shaders/post/fxaa.json"), new ResourceLocation("shaders/post/art.json"), new ResourceLocation("shaders/post/bumpy.json"), new ResourceLocation("shaders/post/blobs2.json"), new ResourceLocation("shaders/post/pencil.json"), new ResourceLocation("shaders/post/color_convolve.json"), new ResourceLocation("shaders/post/deconverge.json"), new ResourceLocation("shaders/post/flip.json"), new ResourceLocation("shaders/post/invert.json"), new ResourceLocation("shaders/post/ntsc.json"), new ResourceLocation("shaders/post/outline.json"), new ResourceLocation("shaders/post/phosphor.json"), new ResourceLocation("shaders/post/scan_pincushion.json"), new ResourceLocation("shaders/post/sobel.json"), new ResourceLocation("shaders/post/bits.json"), new ResourceLocation("shaders/post/desaturate.json"), new ResourceLocation("shaders/post/green.json"), new ResourceLocation("shaders/post/blur.json"), new ResourceLocation("shaders/post/wobble.json"), new ResourceLocation("shaders/post/blobs.json"), new ResourceLocation("shaders/post/antialias.json")};
   private int currentPageIndex = 2;

   public String getSelectedPage() {
      return this.selectedPage;
   }

   public void setPage(String pageName) {
      for(RenderElement renderelement : ((SlidePage)this.pages.get(this.selectedPage)).getElementList()) {
         if(renderelement instanceof InputField) {
            ((InputField)renderelement).setFocused(false);
         } else if(renderelement instanceof Dropdown) {
            if(((Dropdown)renderelement).isOpen()) {
               ((Dropdown)renderelement).setOpen(false);
            }
         } else if(renderelement instanceof ColorPicker) {
            ((ColorPicker)renderelement).setModalOpen(false);
         }
      }

      if(!pageName.equals("modPage")) {
         ((SlidePage)this.pages.get(pageName)).setPage(0);
      }

      this.selectedPage = pageName;
      Wrapper.getInstance().getModProfileManager().saveActiveModProfile();
   }

   public int getSlideoutWidth() {
      return 165;
   }

   public void passEvent(Event event) {
      if(this.visible) {
         this.visibleTime = System.currentTimeMillis();
      }

      if(event instanceof MotionUpdate) {
         Wrapper.getInstance().getLegacyAnimations().setItemHeld(this.itemHeld.isEnabled()?LegacyAnimations.AnimationMode.LEGACY:LegacyAnimations.AnimationMode.CURRENT);
         Wrapper.getInstance().getLegacyAnimations().setBlockHit(this.blockHit.isEnabled()?LegacyAnimations.AnimationMode.LEGACY:LegacyAnimations.AnimationMode.CURRENT);
         Wrapper.getInstance().getLegacyAnimations().setHeartAnimation(this.heartAnimation.isEnabled()?LegacyAnimations.AnimationMode.LEGACY:LegacyAnimations.AnimationMode.CURRENT);
         Wrapper.getInstance().getLegacyAnimations().setDamageAnimation(this.damageAnimation.isEnabled()?LegacyAnimations.AnimationMode.LEGACY:LegacyAnimations.AnimationMode.CURRENT);
         Wrapper.getInstance().getLegacyAnimations().setSneakingAnimation(this.sneakingAnimation.isEnabled()?LegacyAnimations.AnimationMode.LEGACY:LegacyAnimations.AnimationMode.CURRENT);
      }

      if(event instanceof RenderGame && (this.visible || this.slide > 0.0D)) {
         int i = (new ScaledResolution(Minecraft.getMinecraft())).getScaleFactor();
         if(i < 2) {
            GL11.glPushMatrix();
            GL11.glScaled(2.0D, 2.0D, 2.0D);
         }

         this.render();
         if(i < 2) {
            GL11.glPopMatrix();
         }
      }

      if(event instanceof GUIKeyPress) {
         this.keyTyped(((GUIKeyPress)event).getCharacter(), ((GUIKeyPress)event).getKeyID(), event);
      }

      if(event instanceof KeyPress && ((KeyPress)event).isPressed()) {
         this.keyTyped('\u0000', ((KeyPress)event).getKeyID(), event);
      }

      if(event instanceof GUIClickMouse && this.isOpen() && !event.isCancelled()) {
         this.onClick(((GUIClickMouse)event).getMouseButton());
      }

   }

   public void keyTyped(char character, int keyCode, Event event) {
      boolean flag = false;

      for(RenderElement renderelement : ((SlidePage)this.pages.get(this.selectedPage)).getElementList()) {
         if(renderelement instanceof SearchField) {
            if(!this.modProfilesButton.isBoxOpen()) {
               renderelement.keyTyped(character, keyCode);
               String s = ((SearchField)renderelement).getText();
               if(this.search == null || !this.search.equals(s)) {
                  this.search = s;
                  flag = true;
               }
            }
         } else {
            renderelement.keyTyped(character, keyCode);
         }
      }

      if(flag) {
         this.initPages();

         for(RenderElement renderelement1 : ((SlidePage)this.pages.get(this.selectedPage)).getElementList()) {
            if(renderelement1 instanceof SearchField) {
               ((SearchField)renderelement1).setText(this.search);
            }
         }
      }

      if(this.pages.size() > 0) {
         int i = ((KeyBindElement)((SlidePage)this.pages.get("slideoutSettings")).getElementList().get(2)).getKey();
         if(this.access.getKey() != i) {
            this.access.setKey(i);
         } else if(this.isOpen() && keyCode == 1 || keyCode == this.access.getKey()) {
            this.toggle(event);
         }
      }

   }

   public void render() {
      int i = this.getSlideoutWidth();
      int j = this.mc.displayHeight / Wrapper.getInstance().getRealScaleFactor();
      if(this.visible) {
         ;
      }

      Gui.drawRect((int)this.slide - i, 0, (int)this.slide, j, -1155061959);
      int k = 61;
      int l = 24;
      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(this.badlionLogo);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture((int)this.slide - i, 0, (float)k, (float)l, k, l, (float)k, (float)l);
      Gui.drawRect((int)this.slide - i + k, 0, (int)this.slide, l, -14144717);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      int i1 = 7;
      int j1 = 15;
      int k1 = 110;
      int l1 = 75;
      int i2 = -11184811;
      int j2 = -11184811;
      if(this.selectedPage.equals("modPage")) {
         i2 = -1;
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         renderImage(this.mul, (int)this.slide - k1, j1, 216, 44, 0.145D);
      } else if(this.selectedPage.equals("betterframesPage")) {
         j2 = -1;
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         renderImage(this.bful, (int)this.slide - l1, j1, 458, 44, 0.145D);
      }

      double d0 = 0.95D;
      int k2 = 12;
      double d1 = -0.5D;
      int l2 = this.fontRenderer.getStringWidth("MODS");
      GL11.glScaled(d0, d0, d0);
      GL11.glColor4f((float)i2, (float)i2, (float)i2, 1.0F);
      this.fontRenderer.drawString("MODS", (int)((this.slide - (double)i + (double)k - 3.0D) / d0), (int)((double)i1 + d1 / d0), i2);
      GL11.glColor4f((float)j2, (float)j2, (float)j2, 1.0F);
      this.fontRenderer.drawString("BETTERFRAMES", (int)((this.slide - (double)i + (double)k - 5.0D + (double)l2 + (double)k2) / d0), (int)((double)i1 + d1 / d0), j2);
      GL11.glScaled(1.0D / d0, 1.0D / d0, 1.0D / d0);
      if(this.pages.get(this.selectedPage) != null) {
         ((SlidePage)this.pages.get(this.selectedPage)).render(this.slide);
      }

      if(this.visible && !this.blur) {
         this.blur = true;
      } else if(!this.visible && this.blur) {
         this.blur = false;
      }

   }

   public void onClick(int mouseButton) {
      if(mouseButton == 0) {
         Minecraft minecraft = Minecraft.getMinecraft();
         int i = Wrapper.getInstance().getMouseX();
         int j = Wrapper.getInstance().getMouseY();
         int k = this.getSlideoutWidth();
         int l = 7;
         int i1 = 61;
         int j1 = 176;
         int k1 = 472;
         int l1 = 62;
         if(i > (int)(this.slide - (double)k + (double)i1 - 3.0D) && i < (int)(this.slide - (double)k + (double)i1 - 3.0D) + (int)((double)j1 * 0.159D) && j > l && j < l + (int)((double)l1 * 0.159D)) {
            this.selectedPage = "modPage";
         }

         if(i > (int)(this.slide - (double)k + (double)i1 - 5.0D + (double)j1 * 0.159D + 5.0D) && i < (int)(this.slide - (double)k + (double)i1 - 5.0D + (double)j1 * 0.159D + 5.0D) + (int)((double)k1 * 0.159D) && j > l && j < l + (int)((double)l1 * 0.159D)) {
            this.selectedPage = "betterframesPage";
         }

         SlidePage slidepage = (SlidePage)this.pages.get(this.selectedPage);
         if(slidepage != null) {
            slidepage.onClick(mouseButton);
         }
      }

      List<RenderElement> list = ((SlidePage)this.pages.get(this.selectedPage)).getElementList();
      int i2 = -1;

      for(RenderElement renderelement : list) {
         if(renderelement.getZLevel() > i2) {
            i2 = renderelement.getZLevel();
         }
      }

      if(mouseButton == 0) {
         int j2 = 0;

         for(RenderElement renderelement2 : list) {
            if(renderelement2 instanceof InputField) {
               ((InputField)renderelement2).setFocused(false);
            }

            if(renderelement2 instanceof Dropdown && ((Dropdown)renderelement2).isOpen()) {
               ++j2;
            }
         }

         if(j2 > 1) {
            for(RenderElement renderelement3 : list) {
               if(renderelement3 instanceof Dropdown) {
                  ((Dropdown)renderelement3).setOpen(false);
               }
            }
         }
      }

      for(RenderElement renderelement1 : list) {
         if(renderelement1.onClick(mouseButton)) {
            if(!(renderelement1 instanceof ModProfilesButton)) {
               this.modProfilesButton.setBoxOpen(false);
            }
            break;
         }
      }

      if(this.guiEnable.isSelected()) {
         this.toggle();
         ModConfigurator modconfigurator = Wrapper.getInstance().getActiveModProfile().getModConfigurator();
         if(modconfigurator != null) {
            modconfigurator.setEditing(!modconfigurator.isInEditingMode());
         }

         this.mc.displayGuiScreen(new GuiScreenEditing((GuiScreen)null));
      }

   }

   public static void renderImage(ResourceLocation resourceLocation, int x, int y, int sizeX, int sizeY, double scale) {
      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(resourceLocation);
      GL11.glEnable(3042);
      GL11.glBlendFunc(770, 771);
      int i = (int)((double)sizeX * scale);
      int j = (int)((double)sizeY * scale);
      Gui.drawModalRectWithCustomSizedTexture(x, y, (float)i, (float)j, i, j, (float)i, (float)j);
   }

   public void update() {
      Minecraft minecraft = Minecraft.getMinecraft();
      int i = Wrapper.getInstance().getMouseX();
      int j = Wrapper.getInstance().getMouseY();
      if(this.mc.displayWidth != this.lastDisplayWidth || this.mc.displayHeight != this.lastDisplayHeight || this.visible != this.wasVisible) {
         this.lastDisplayWidth = this.mc.displayWidth;
         this.lastDisplayHeight = this.mc.displayHeight;
         this.wasVisible = this.visible;
         this.initPages();
      }

      int k = this.getSlideoutWidth();
      if(this.quickSlideout.getValue().booleanValue()) {
         if(!this.visible) {
            this.slide = 0.0D;
         } else {
            this.slide = (double)k;
         }
      } else if(!this.visible) {
         if(this.slide > 0.0D) {
            this.slide /= 1.06D;
            --this.slide;
            if(this.slide < 0.0D) {
               this.slide = 0.0D;
            }
         }

         if(System.currentTimeMillis() - this.visibleTime > 3000L && this.slide > -50.0D) {
            --this.slide;
         }
      } else {
         if(this.slide < 0.0D) {
            this.slide = 0.0D;
         }

         if(this.slide < (double)k) {
            ++this.slide;
            this.slide *= 1.03D;
         }

         if(this.slide > (double)k) {
            this.slide = (double)k;
         }
      }

      for(SlidePage slidepage : this.pages.values()) {
         slidepage.update(i, j);

         for(RenderElement renderelement : slidepage.getElementList()) {
            if(renderelement instanceof TextButton) {
               TextButton textbutton = (TextButton)renderelement;
               String s = textbutton.getText();
               byte b0 = -1;
               switch(s.hashCode()) {
               case -1461285227:
                  if(s.equals("Experimental Improvements")) {
                     b0 = 2;
                  }
                  break;
               case -1406873644:
                  if(s.equals("Weather")) {
                     b0 = 0;
                  }
                  break;
               case -112688939:
                  if(s.equals("Reduced Chunk Updates")) {
                     b0 = 1;
                  }
                  break;
               case 124959013:
                  if(s.equals("Dirt Screen")) {
                     b0 = 3;
                  }
               }

               switch(b0) {
               case 0:
                  Wrapper.getInstance().getActiveModProfile().getBetterframesConfig().setWeather(textbutton.isEnabled());
                  break;
               case 1:
                  Wrapper.getInstance().getActiveModProfile().getBetterframesConfig().setReducedChunkUpdates(textbutton.isEnabled());
                  break;
               case 2:
                  Wrapper.getInstance().getActiveModProfile().getBetterframesConfig().setRenderImprovements(textbutton.isEnabled());
                  break;
               case 3:
                  Wrapper.getInstance().getActiveModProfile().getBetterframesConfig().setWorldSwitchDirtScreen(textbutton.isEnabled());
               }
            }
         }
      }

      if(this.cogSetting.isSelected()) {
         this.setPage("slideoutSettings");
      }

      if(this.backCogButton.isSelected()) {
         this.setPage("modPage");
      }

   }

   public String getSearch() {
      return this.search;
   }

   public void init() {
      this.timer = new Timer();
      this.timer.schedule(new TimerTask() {
         public void run() {
            try {
               synchronized(Wrapper.getInstance().getModProfileManager()) {
                  SlideoutGUI.this.update();
               }
            } catch (Exception var3) {
               var3.printStackTrace();
            }

         }
      }, 0L, 9L);
      this.fontRenderer = new CustomFontRenderer();
   }

   public int getSlideoutHeight() {
      return this.mc.displayHeight / Wrapper.getInstance().getRealScaleFactor();
   }

   public void initPages() {
      this.pages.clear();
      int i = this.mc.displayWidth / Wrapper.getInstance().getRealScaleFactor();
      int j = this.mc.displayHeight / Wrapper.getInstance().getRealScaleFactor();
      SlidePage slidepage = new SlidePage("modPage", this.getSlideoutWidth(), j);
      int k = 12;
      int l = 33;
      double d0 = 0.128D;
      if(this.search == null) {
         this.search = "";
      }

      slidepage.addElement(new Padding(3, 5));
      SearchField searchfield;
      slidepage.addElement(searchfield = new SearchField(this.search, -1, -1, d0));
      ImageButton imagebutton = new ImageButton(this.cogWheel, 27, 29, 0.4D);
      imagebutton.zLevel = 999;
      imagebutton.setPosition(this.getSlideoutWidth() - k - 5, l);
      imagebutton.ignoreAutoPos(0);
      slidepage.addElement(imagebutton);
      slidepage.addElement(new Padding(0, 0));
      this.cogSetting = imagebutton;
      if(this.modProfilesButton == null) {
         this.modProfilesButton = new ModProfilesButton();
      }

      slidepage.addElement(this.modProfilesButton);

      for(Mod mod : Wrapper.getInstance().getActiveModProfile().getAllMods()) {
         try {
            mod.createCogMenu();
         } catch (Exception var23) {
            LogManager.getLogger().info("ERROR Creating Menu: " + mod.getDisplayName() + " - " + var23.getMessage());
            LogManager.getLogger().catching(var23);
         }
      }

      int i1 = 0;

      for(Mod mod1 : Wrapper.getInstance().getActiveModProfile().getAllMods()) {
         if(!(mod1 instanceof ModConfigurator) && !(mod1 instanceof SlideoutAccess) && !(mod1 instanceof ChangeColorMod) && (this.search == null || this.search.length() <= 0 || mod1.getName().toLowerCase().contains(this.search.toLowerCase()) || mod1.getDisplayName().toLowerCase().contains(this.search.toLowerCase()))) {
            ++i1;
            slidepage.addElement(new ModButton(mod1, 1.0D));
         }
      }

      if(i1 == 0) {
         for(Mod mod3 : Wrapper.getInstance().getActiveModProfile().getAllMods()) {
            if(!(mod3 instanceof ModConfigurator) && !(mod3 instanceof SlideoutAccess) && !(mod3 instanceof ChangeColorMod)) {
               slidepage.addElement(new ModButton(mod3, 1.0D));
            }
         }
      }

      searchfield.setResults(i1);
      slidepage.addElement(this.guiEnable = new SimpleButton("Enable GUI Editing", -14342875, -12303292, -1));
      this.guiEnable.init();
      this.guiEnable.ignoreAutoPos(0);
      int j1 = this.getSlideoutHeight();
      if(Wrapper.getInstance().getRealScaleFactor() < 2) {
         j1 /= 2;
      }

      this.guiEnable.setPosition(this.getSlideoutWidth() - this.guiEnable.getWidth() - 8, j1 - this.guiEnable.getHeight() - 8);
      slidepage.addElement(new Padding(this.getSlideoutWidth() - 25, 6));
      slidepage.addElement(this.itemHeld = new TextButton("Item Hold Animation", new MutableBoolean(Wrapper.getInstance().getLegacyAnimations().getItemHeld().equals(LegacyAnimations.AnimationMode.LEGACY)), 1.0D));
      slidepage.addElement(this.blockHit = new TextButton("Block Hit Animation", new MutableBoolean(Wrapper.getInstance().getLegacyAnimations().getBlockHit().equals(LegacyAnimations.AnimationMode.LEGACY)), 1.0D));
      slidepage.addElement(this.heartAnimation = new TextButton("Health Animation", new MutableBoolean(Wrapper.getInstance().getLegacyAnimations().getHeartAnimation().equals(LegacyAnimations.AnimationMode.LEGACY)), 1.0D));
      slidepage.addElement(this.damageAnimation = new TextButton("Damage Animation", new MutableBoolean(Wrapper.getInstance().getLegacyAnimations().getDamageAnimation().equals(LegacyAnimations.AnimationMode.LEGACY)), 1.0D));
      slidepage.addElement(this.sneakingAnimation = new TextButton("Sneaking Animation", new MutableBoolean(Wrapper.getInstance().getLegacyAnimations().getSneakingAnimation().equals(LegacyAnimations.AnimationMode.LEGACY)), 1.0D));
      this.blockHit.setLocked(true);
      this.sneakingAnimation.setLocked(true);
      this.itemHeld.setToolTipText("Off = 1.8     On = 1.7      Item holding position (Swords, Rods)");
      this.blockHit.setToolTipText("Off = 1.8     On = 1.7      Block hitting animation for swords");
      this.heartAnimation.setToolTipText("Off = 1.8     On = 1.7      Heart blinking animation");
      this.damageAnimation.setToolTipText("Off = 1.8     On = 1.7      Show red tint on armor");
      this.sneakingAnimation.setToolTipText("Off = 1.8     On = 1.7      Smooth sneaking from 1.7");
      slidepage.init();
      slidepage.position();
      SlidePage slidepage1 = new SlidePage("betterframesPage", this.getSlideoutWidth(), j);
      TextButton textbutton = new TextButton("Weather", new MutableBoolean(Wrapper.getInstance().getActiveModProfile().getBetterframesConfig().isWeather()), 1.0D);
      TextButton textbutton1 = new TextButton("Reduced Chunk Updates", new MutableBoolean(Wrapper.getInstance().getActiveModProfile().getBetterframesConfig().isReducedChunkUpdates()), 1.0D);
      TextButton textbutton2 = new TextButton("Experimental Improvements", new MutableBoolean(Wrapper.getInstance().getActiveModProfile().getBetterframesConfig().isRenderImprovements()), 1.0D);
      TextButton textbutton3 = new TextButton("Dirt Screen", new MutableBoolean(Wrapper.getInstance().getActiveModProfile().getBetterframesConfig().isWorldSwitchDirtScreen()), 1.0D);
      textbutton2.setLocked(true);
      textbutton1.setLocked(true);
      textbutton.setToggleable(true);
      textbutton1.setToggleable(false);
      textbutton2.setToggleable(false);
      textbutton3.setToggleable(true);
      textbutton.setToolTipText("(Disabled) Enable / Disable the weather");
      textbutton1.setToolTipText("(Disabled) Optimize and reduce chunk updating wherever possible.");
      textbutton2.setToolTipText("(Disabled) Enables a few experimental improvements to generally increase performance.");
      textbutton3.setToolTipText("Turn on/off the dirt screen you see when you change worlds.");
      slidepage1.addElement(new Padding(0, 1));
      slidepage1.addElement(new Label("NOTE:", -1, 12, BadlionFontRenderer.FontType.TITLE));
      slidepage1.addElement(new Padding(0, 1));
      slidepage1.addElement(new Label("One or more option may be", -1, 12, BadlionFontRenderer.FontType.TITLE));
      slidepage1.addElement(new Label("disabled due to conflictions.", -1, 12, BadlionFontRenderer.FontType.TITLE));
      slidepage1.addElement(new Padding(this.getSlideoutWidth() + 3, 8));
      slidepage1.addElement(textbutton1);
      slidepage1.addElement(textbutton2);
      slidepage1.addElement(textbutton3);
      slidepage1.addElement(textbutton);
      slidepage1.init();
      slidepage1.position();
      this.pages.put("modPage", slidepage);
      this.pages.put("betterframesPage", slidepage1);

      for(Mod mod2 : Wrapper.getInstance().getActiveModProfile().getAllMods()) {
         if(mod2.hasSlideCogMenu()) {
            this.pages.put(mod2.getName(), mod2.getSlideCogMenu());
         }
      }

      SlidePage slidepage2 = new SlidePage("slideoutSettings", this.getSlideoutWidth(), j);
      ImageButton imagebutton1 = new ImageButton(this.backButtonRes, 124, 124, 0.2D);
      this.backCogButton = imagebutton1;
      Label label = new Label("Slideout Config", -5592406, 14, BadlionFontRenderer.FontType.TITLE);
      Label label1 = new Label("Slideout Toggle Key: ", -1, 12, BadlionFontRenderer.FontType.TITLE);
      TextButton textbutton4 = new TextButton("Quick Slideout", this.quickSlideout, 1.0D);
      slidepage2.addElement(new Padding(0, 10));
      slidepage2.addElement(imagebutton1);
      slidepage2.addElement(new Padding(0, 0));
      slidepage2.addElement(label);
      label.ignoreAutoPos(0);
      label.setPosition(55, 33);
      slidepage2.addElement(label1);
      slidepage2.addElement(new Padding(0, 2));
      KeyBindElement keybindelement = new KeyBindElement(this.access.getKey());
      slidepage2.addElement(keybindelement);
      slidepage2.addElement(new Padding(0, 10));
      slidepage2.addElement(textbutton4);
      slidepage2.init();
      slidepage2.position();
      imagebutton1.setPosition(10, imagebutton1.getY());
      this.pages.put("slideoutSettings", slidepage2);
   }

   public void setVisible(boolean visible, boolean animation) {
      this.visible = visible;
      if(this.visible) {
         Minecraft.getMinecraft().displayGuiScreen(new GuiScreenSlideout());
         if(!animation) {
            this.slide = (double)this.getSlideoutWidth();
         }
      } else {
         Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
         if(!animation) {
            this.slide = 0.0D;
         }
      }

   }

   public void toggle() {
      this.toggle((Event)null);
   }

   public void toggle(Event event) {
      if(this.mc.theWorld != null) {
         this.visible = !this.visible;
         if(this.visible) {
            this.search = "";
            if(event != null) {
               int i = -1;
               if(event instanceof GUIKeyPress) {
                  i = ((GUIKeyPress)event).getKeyID();
               } else if(event instanceof KeyPress) {
                  i = ((KeyPress)event).getKeyID();
               }

               if(i != -1 && (i == Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode() || i == Minecraft.getMinecraft().gameSettings.keyBindJump.getKeyCode() || i == Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode() || i == Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode())) {
                  event.setCancelled(true);
               }
            }

            Minecraft.getMinecraft().displayGuiScreen(new GuiScreenSlideout());
         } else {
            Minecraft.getMinecraft().displayGuiScreen((GuiScreen)null);
            Wrapper.getInstance().getModProfileManager().saveActiveModProfile();
            this.modProfilesButton.setBoxOpen(false);
         }
      }

   }

   public CustomFontRenderer getFontRenderer() {
      return this.fontRenderer;
   }

   public int getNextPageIndex() {
      return this.currentPageIndex++;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public boolean isOpen() {
      return this.slide > 10.0D;
   }

   public Timer getTimer() {
      return this.timer;
   }

   public ModProfilesButton getModProfilesButton() {
      return this.modProfilesButton;
   }
}
