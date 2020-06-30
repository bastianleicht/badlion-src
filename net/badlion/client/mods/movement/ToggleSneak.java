package net.badlion.client.mods.movement;

import java.util.Locale;
import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.GUIClickMouse;
import net.badlion.client.events.event.KeyPress;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.events.event.RenderGame;
import net.badlion.client.events.event.SneakEvent;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.InputField;
import net.badlion.client.gui.slideout.Dropdown;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.ModPreviewRenderer;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.Slider;
import net.badlion.client.gui.slideout.TextButton;
import net.badlion.client.gui.slideout.elements.ColorPicker;
import net.badlion.client.gui.slideout.fontrenderer.CustomFontRenderer;
import net.badlion.client.mods.render.RenderMod;
import net.badlion.client.mods.render.color.ModColor;
import net.badlion.client.mods.render.gui.BoxedCoord;
import net.badlion.client.util.ColorUtil;
import net.badlion.client.util.ImageDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class ToggleSneak extends RenderMod {
   private transient CustomFontRenderer fontRenderer;
   private ToggleSneak.ToggleSneakMode toggleSneakMode = ToggleSneak.ToggleSneakMode.SPRINT;
   private ToggleSneak.ToggleSneakRenderMode toggleSneakRenderMode = ToggleSneak.ToggleSneakRenderMode.CLASSIC;
   private ModColor labelColor = new ModColor(-1);
   private boolean useDefaultFont = true;
   private MutableBoolean inventorySneak = new MutableBoolean(false);
   private MutableBoolean indicator = new MutableBoolean(true);
   private transient boolean overwriteInvSneak = false;
   private String sprintingText = "Sprinting";
   private String sneakingText = "Sneaking";
   private double flyModifier = 1.0D;
   private boolean warned = false;
   private transient boolean showWarning = false;
   private double flySlide = 0.0D;
   private transient Dropdown toggleSneakModeDropdown;
   private transient Dropdown toggleSneakRenderModeDropdown;
   private transient InputField sneakingTextBox;
   private transient InputField sprintingTextBox;
   private transient Slider flySpeedSlider;
   private transient TextButton inventorySneakButton;
   private transient TextButton showIndicatorButton;
   private transient boolean sprinting;
   private transient boolean sneaking;
   private transient int vanillaTicks;
   private transient long lastPressTime;
   private transient double lastFlySpeed = 1.0D;
   private transient boolean lastInventorySneak = false;
   private transient ResourceLocation sprintingIcon = new ResourceLocation("textures/gui/sprinting.png");
   private transient ResourceLocation sneakingIcon = new ResourceLocation("textures/gui/sneaking.png");
   private transient boolean sprintingVanilla;
   private transient boolean sneakingVanilla;

   public void reset() {
      this.toggleSneakMode = ToggleSneak.ToggleSneakMode.SPRINT;
      this.toggleSneakRenderMode = ToggleSneak.ToggleSneakRenderMode.CLASSIC;
      this.labelColor = new ModColor(-1);
      this.useDefaultFont = true;
      this.sprintingText = "Sprinting";
      this.sneakingText = "Sneaking";
      this.flyModifier = 1.0D;
      this.warned = false;
      this.flySlide = 0.0D;
      this.defaultSizeX = 110;
      this.defaultSizeY = 9;
      this.inventorySneakButton.setEnabled(false);
      this.showIndicatorButton.setEnabled(true);
      this.indicator.setValue(true);
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
      this.slideCogMenu.addElement(this.toggleSneakModeDropdown = new Dropdown(new String[]{"Sprint & Sneak", "Sprint", "Sneak"}, this.toggleSneakMode.ordinal(), 0.19D));
      this.slideCogMenu.addElement(this.toggleSneakRenderModeDropdown = new Dropdown(new String[]{"Classic", "Icons"}, this.toggleSneakRenderMode.ordinal(), 0.19D));
      this.slideCogMenu.addElement(this.sneakingTextBox = new InputField(110, 9, false, this.sneakingText, "", -1, InputField.InputFlavor.EMAIL));
      this.slideCogMenu.addElement(this.sprintingTextBox = new InputField(110, 9, false, this.sprintingText, "", -1, InputField.InputFlavor.EMAIL));
      this.slideCogMenu.addElement(this.flySpeedSlider = new Slider("Fly Speed", 0.0D, 1.0D, this.flySlide, 0.22D));
      this.flySpeedSlider.setDisplayText(new String[]{"1x", "2x", "4x", "8x"});
      this.labelColor.init();
      this.inventorySneakButton = new TextButton("Inventory Sneak", this.inventorySneak, 1.0D);
      this.inventorySneakButton.setToolTipText("Inventory sneaking may be considered an unfair advantage on some servers (not Badlion)!");
      this.slideCogMenu.addElement(this.inventorySneakButton);
      this.slideCogMenu.addElement(this.showIndicatorButton = new TextButton("Mod Indication", this.indicator, 1.0D));
      super.createCogMenu();
   }

   public ToggleSneak() {
      super("ToggleSneak", 42, -111, 110, 9);
      this.iconDimension = new ImageDimension(81, 48);
      this.defaultTopLeftBox = new BoxedCoord(26, 0, 0.016666666666666666D, 0.0D);
      this.defaultCenterBox = new BoxedCoord(27, 0, 0.7166666666666667D, 0.23703703703703705D);
      this.defaultBottomRightBox = new BoxedCoord(29, 0, 0.43333333333333335D, 0.5037037037037037D);
   }

   public void init() {
      this.fontRenderer = new CustomFontRenderer();
      this.offsetY = 1;
      if(this.toggleSneakRenderMode.equals(ToggleSneak.ToggleSneakRenderMode.ICONS)) {
         this.defaultSizeX = 38;
         this.defaultSizeY = 36;
      }

      this.registerEvent(EventType.RENDER_GAME);
      this.registerEvent(EventType.MOTION_UPDATE);
      this.registerEvent(EventType.CLICK_MOUSE);
      this.registerEvent(EventType.SNEAK_EVENT);
      this.registerEvent(EventType.KEY_PRESS);
      this.registerEvent(EventType.GUI_CLICK_MOUSE);
      this.setFontOffset(0.032D);
      super.init();
   }

   public void handleDisallowedMods(Wrapper.DisallowedMods disallowedMods) {
      super.handleDisallowedMods(disallowedMods);
      if(disallowedMods != null && disallowedMods.getExtraData() != null) {
         if(disallowedMods.getExtraData().has("invsneak")) {
            boolean flag = disallowedMods.getExtraData().get("invsneak").getAsBoolean();
            if(!flag) {
               this.overwriteInvSneak = true;
            }
         }
      } else {
         this.overwriteInvSneak = false;
      }

   }

   public void onEvent(Event event) {
      super.onEvent(event);
      KeyBinding keybinding = this.gameInstance.gameSettings.keyBindInventory;
      KeyBinding keybinding1 = this.gameInstance.gameSettings.keyBindSprint;
      if(event instanceof MotionUpdate && this.loadedCogMenu) {
         this.sneakingText = this.sneakingTextBox.getText();
         this.sprintingText = this.sprintingTextBox.getText();
         ToggleSneak.ToggleSneakRenderMode togglesneak$togglesneakrendermode = ToggleSneak.ToggleSneakRenderMode.valueOf(this.toggleSneakRenderModeDropdown.getValue().toUpperCase(Locale.US));
         if(!togglesneak$togglesneakrendermode.equals(this.toggleSneakRenderMode)) {
            int i = this.defaultSizeX;
            int j = this.defaultSizeY;
            if(togglesneak$togglesneakrendermode.equals(ToggleSneak.ToggleSneakRenderMode.CLASSIC)) {
               this.defaultSizeX = 110;
               this.defaultSizeY = 9;
            } else {
               this.defaultSizeX = 38;
               this.defaultSizeY = 36;
            }

            this.sizeX = (double)((int)(this.sizeX / (double)i * (double)this.defaultSizeX));
            this.sizeY = (double)((int)(this.sizeY / (double)j * (double)this.defaultSizeY));
            String mX = this.toggleSneakRenderModeDropdown.getValue();
            byte mY = -1;
            switch(mX.hashCode()) {
            case -1776693134:
               if(mX.equals("Classic")) {
                  mY = 1;
               }
               break;
            case 70476538:
               if(mX.equals("Icons")) {
                  mY = 2;
               }
            }

            switch(mY) {
            case 1:
            default:
               this.toggleSneakRenderMode = ToggleSneak.ToggleSneakRenderMode.CLASSIC;
               break;
            case 2:
               this.toggleSneakRenderMode = ToggleSneak.ToggleSneakRenderMode.ICONS;
            }

            Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().initPages();
         }

         String d = this.toggleSneakModeDropdown.getValue();
         byte startY = -1;
         switch(d.hashCode()) {
         case -1811812806:
            if(d.equals("Sprint")) {
               startY = 2;
            }
            break;
         case 80029428:
            if(d.equals("Sneak")) {
               startY = 3;
            }
            break;
         case 1265088148:
            if(d.equals("Sprint & Sneak")) {
               startY = 1;
            }
         }

         switch(startY) {
         case 1:
         default:
            this.toggleSneakMode = ToggleSneak.ToggleSneakMode.SPRINT_SNEAK;
            break;
         case 2:
            this.toggleSneakMode = ToggleSneak.ToggleSneakMode.SPRINT;
            if(this.sneaking) {
               this.sneaking = false;
            }
            break;
         case 3:
            this.toggleSneakMode = ToggleSneak.ToggleSneakMode.SNEAK;
         }

         String sneakText = this.flySpeedSlider.getCurrentDisplayText();
         byte sprintText = -1;
         switch(sneakText.hashCode()) {
         case 1670:
            if(sneakText.equals("2x")) {
               sprintText = 0;
            }
            break;
         case 1732:
            if(sneakText.equals("4x")) {
               sprintText = 1;
            }
            break;
         case 1856:
            if(sneakText.equals("8x")) {
               sprintText = 2;
            }
         }

         double d0;
         switch(sprintText) {
         case 0:
            d0 = 2.0D;
            break;
         case 1:
            d0 = 4.0D;
            break;
         case 2:
            d0 = 8.0D;
            break;
         default:
            d0 = 1.0D;
         }

         this.flySlide = this.flySpeedSlider.getValue();
         this.flyModifier = d0;
         if(this.flyModifier != this.lastFlySpeed && !this.warned) {
            this.warn();
         }

         if(this.inventorySneak.booleanValue() != this.lastInventorySneak && !this.warned) {
            this.warn();
         }

         this.gameInstance.thePlayer.capabilities.setFlySpeed((float)(0.05D * this.flyModifier));
         this.labelColor.tickColor();
      }

      if(this.isEnabled()) {
         if(event instanceof KeyPress && this.gameInstance != null && this.gameInstance.currentScreen == null) {
            KeyPress keypress = (KeyPress)event;
            if(Minecraft.getMinecraft().thePlayer.isRiding()) {
               return;
            }

            if(Minecraft.getMinecraft().currentScreen != null) {
               return;
            }

            if(keypress.isPressed()) {
               if((this.toggleSneakMode.equals(ToggleSneak.ToggleSneakMode.SPRINT_SNEAK) || this.toggleSneakMode.equals(ToggleSneak.ToggleSneakMode.SPRINT)) && keypress.getKeyID() == keybinding.getKeyCode()) {
                  this.sprinting = !this.sprinting;
                  if(!this.sprinting && Minecraft.getMinecraft().thePlayer.isSprinting()) {
                     Minecraft.getMinecraft().thePlayer.setSprinting(false);
                     keybinding.setPressed(false);
                     this.gameInstance.gameSettings.keyBindSprint.setPressed(false);
                     keypress.setPressed(false);
                     keypress.setCancelled(true);
                  }
               }

               if((this.toggleSneakMode.equals(ToggleSneak.ToggleSneakMode.SPRINT_SNEAK) || this.toggleSneakMode.equals(ToggleSneak.ToggleSneakMode.SNEAK)) && keypress.getKeyID() == keybinding1.getKeyCode()) {
                  this.sneaking = !this.sneaking;
               }
            } else if(keypress.getKeyID() == keybinding1.getKeyCode() && this.sneaking) {
               keypress.setPressed(true);
               keypress.setCancelled(true);
            }
         }

         if(event instanceof SneakEvent && this.sneaking && !((SneakEvent)event).isSneaking() && this.inventorySneak.booleanValue() && !this.overwriteInvSneak) {
            ((SneakEvent)event).setSneaking(true);
         }

         if(event instanceof MotionUpdate) {
            boolean flag2 = keybinding.isPressed();
            boolean flag3 = keybinding1.isPressed();
            if(this.gameInstance != null && this.gameInstance.currentScreen == null) {
               try {
                  flag2 = Keyboard.isKeyDown(keybinding.getKeyCode());
               } catch (IndexOutOfBoundsException var21) {
                  ;
               }

               try {
                  flag3 = Keyboard.isKeyDown(keybinding1.getKeyCode());
               } catch (IndexOutOfBoundsException var20) {
                  ;
               }
            }

            if(!flag2 && !flag3) {
               if(!this.gameInstance.thePlayer.isSprinting()) {
                  this.vanillaTicks = 0;
               }
            } else {
               ++this.vanillaTicks;
            }

            if(this.sprinting) {
               keybinding.setPressed(true);
            }

            if(this.sneaking && (this.gameInstance.currentScreen == null || this.inventorySneak.booleanValue() && !this.overwriteInvSneak)) {
               if(this.inventorySneakButton.isEnabled()) {
                  this.gameInstance.thePlayer.setSneaking(true);
               }

               keybinding1.setPressed(true);
            }

            if(this.gameInstance.thePlayer.capabilities.isFlying) {
               this.sprinting = this.sneaking = false;
            }

            if(this.sprintingVanilla && !this.gameInstance.thePlayer.isSprinting()) {
               this.sprintingVanilla = false;
            }

            if(this.sneakingVanilla && !this.gameInstance.thePlayer.isSneaking()) {
               this.sneakingVanilla = false;
            }
         }

         if(event instanceof GUIClickMouse) {
            ScaledResolution scaledresolution = new ScaledResolution(this.gameInstance);
            Minecraft minecraft = this.gameInstance;
            int j1 = scaledresolution.getScaledHeight() / 2 - 45;
            int l1 = Wrapper.getInstance().getMouseX();
            int i2 = Wrapper.getInstance().getMouseY();
            if(l1 > scaledresolution.getScaledWidth() / 2 - 18 && l1 < scaledresolution.getScaledWidth() / 2 + 16 && i2 > j1 + 75 && i2 < j1 + 87) {
               this.showWarning = false;
            }
         }

         if(event instanceof RenderGame) {
            RenderGame rendergame = (RenderGame)event;
            this.beginRender();
            int i1 = 0;
            int k1 = 0;
            if(this.toggleSneakRenderMode.equals(ToggleSneak.ToggleSneakRenderMode.CLASSIC)) {
               String s3 = this.sneakingText;
               String s5 = this.sprintingText;

               try {
                  boolean flag = keybinding.isPressed();
                  boolean flag1 = keybinding1.isPressed();

                  try {
                     flag = Keyboard.isKeyDown(keybinding.getKeyCode());
                  } catch (IndexOutOfBoundsException var18) {
                     ;
                  }

                  try {
                     flag1 = Keyboard.isKeyDown(keybinding1.getKeyCode());
                  } catch (IndexOutOfBoundsException var17) {
                     ;
                  }

                  if(!Wrapper.getInstance().getActiveModProfile().getModConfigurator().isInEditingMode()) {
                     if(!this.isVanilla()) {
                        if(this.sneaking) {
                           this.drawString(rendergame, i1, k1, "[" + s3 + "] (Toggled)");
                        } else if(this.sprinting) {
                           this.drawString(rendergame, i1, k1, "[" + s5 + "] (Toggled)");
                        }
                     } else if(flag) {
                        if(this.showIndicatorButton.isEnabled()) {
                           this.drawString(rendergame, i1, k1, "[" + s5 + "] (Key Held)");
                        }

                        if(this.gameInstance.currentScreen == null) {
                           this.sprinting = false;
                           this.sprintingVanilla = true;
                        }
                     } else if(flag1) {
                        this.drawString(rendergame, i1, k1, "[" + s3 + "] (Key Held)");
                        if(this.gameInstance.currentScreen == null) {
                           this.sneaking = false;
                           this.sneakingVanilla = true;
                        }
                     } else if(this.sprintingVanilla) {
                        this.drawString(rendergame, i1, k1, "[" + s5 + "] (Vanilla)");
                     }
                  } else if(this.toggleSneakMode == ToggleSneak.ToggleSneakMode.SNEAK) {
                     this.drawString(rendergame, i1, k1, "[" + s3 + "] (Key Held)");
                  } else {
                     this.drawString(rendergame, i1, k1, "[" + s5 + "] (Key Held)");
                  }
               } catch (Exception var19) {
                  this.drawString(rendergame, i1, k1, "[" + s5 + "] (Error #" + keybinding.getKeyCode() + ")");
               }
            } else {
               String s4 = this.sneakingText;
               String s6 = this.sprintingText;

               try {
                  boolean flag4 = keybinding.isPressed();
                  boolean flag5 = keybinding1.isPressed();

                  try {
                     flag4 = Keyboard.isKeyDown(keybinding.getKeyCode());
                  } catch (IndexOutOfBoundsException var15) {
                     ;
                  }

                  try {
                     flag5 = Keyboard.isKeyDown(keybinding1.getKeyCode());
                  } catch (IndexOutOfBoundsException var14) {
                     ;
                  }

                  if(!Wrapper.getInstance().getActiveModProfile().getModConfigurator().isInEditingMode()) {
                     if(!this.isVanilla()) {
                        if(this.sneaking) {
                           int k = 128;
                           int l = 128;
                           this.drawIcon(this.sneakingIcon, i1 + 10, k1, k, l);
                           this.drawString(rendergame, i1, k1 + 26, "[Toggled]");
                        } else if(this.sprinting) {
                           int l2 = 96;
                           int i4 = 96;
                           this.drawIcon(this.sprintingIcon, i1 + 13, k1 + 2, l2, i4);
                           this.drawString(rendergame, i1, k1 + 26, "[Toggled]");
                        }
                     } else if(flag4) {
                        int i3 = 96;
                        int j4 = 96;
                        this.drawIcon(this.sprintingIcon, i1 + 13, k1 + 2, i3, j4);
                        this.drawString(rendergame, i1 + 6, k1 + 26, "[Held]");
                        if(this.gameInstance.currentScreen == null) {
                           this.sprinting = false;
                           this.sprintingVanilla = true;
                        }
                     } else if(flag5) {
                        int j3 = 128;
                        int k4 = 128;
                        this.drawIcon(this.sneakingIcon, i1 + 10, k1, j3, k4);
                        this.drawString(rendergame, i1 + 6, k1 + 26, "[Held]");
                        if(this.gameInstance.currentScreen == null) {
                           this.sneaking = false;
                           this.sneakingVanilla = true;
                        }
                     } else if(this.sprintingVanilla) {
                        int k3 = 96;
                        int l4 = 96;
                        this.drawIcon(this.sprintingIcon, i1 + 13, k1 + 2, k3, l4);
                        this.drawString(rendergame, i1 + 1, k1 + 26, "[Vanilla]");
                     }
                  } else {
                     int l3 = 128;
                     int i5 = 128;
                     if(this.toggleSneakMode == ToggleSneak.ToggleSneakMode.SNEAK) {
                        this.drawIcon(this.sneakingIcon, i1 + 7, k1, l3, i5);
                     } else {
                        this.drawIcon(this.sprintingIcon, i1 + 7, k1, l3, i5);
                     }

                     this.drawString(rendergame, i1, k1 + 28, "[Editing]");
                  }
               } catch (Exception var16) {
                  var16.printStackTrace();
                  this.drawString(rendergame, i1, k1, "[" + s6 + "] (Error #" + keybinding.getKeyCode() + ")");
               }
            }

            this.endRender();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            if(this.showWarning && this.forceX == -1 && this.forceY == -1) {
               Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().setPage(this.getName());
               ScaledResolution scaledresolution1 = new ScaledResolution(this.gameInstance, Minecraft.getMinecraft().displayWidth, Minecraft.getMinecraft().displayHeight);
               Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().slide = 0.0D;
               this.setZIndex(300);
               int j2 = scaledresolution1.getScaledWidth() / 2 - 70;
               int k2 = scaledresolution1.getScaledHeight() / 2 - 45;
               Gui.drawRect(0, 0, scaledresolution1.getScaledWidth(), scaledresolution1.getScaledHeight(), -2013265920);
               Gui.drawRect(j2, k2, j2 + 140, k2 + 90, -13092808);
               double d1 = 1.25D;
               GL11.glScaled(d1, d1, 1.0D);
               this.fontRenderer.drawString("Anticheat Notice", (int)((double)(j2 + 5) / d1), (int)((double)(k2 + 2) / d1), -1);
               GL11.glScaled(1.0D / d1, 1.0D / d1, 1.0D);
               Gui.drawRect(j2, k2 + 18, j2 + 140, k2 + 19, -14540254);
               double d2 = 0.8D;
               GL11.glScaled(d2, d2, 1.0D);
               this.fontRenderer.drawString("These Options:", (int)((double)(j2 + 4) / d2), (int)((double)(k2 + 23) / d2), -1);
               this.fontRenderer.drawString("  *  Fly Speed", (int)((double)(j2 + 4) / d2), (int)((double)(k2 + 33) / d2), -1);
               this.fontRenderer.drawString("  *  Inventory Sneak", (int)((double)(j2 + 4) / d2), (int)((double)(k2 + 43) / d2), -1);
               this.fontRenderer.drawString("may be considered an ", (int)((double)(j2 + 4) / d2), (int)((double)(k2 + 53) / d2), -1);
               this.fontRenderer.drawString(" unfair advantage", (int)((double)(j2 + 77) / d2), (int)((double)(k2 + 53) / d2), -65536);
               this.fontRenderer.drawString("and may be punished! (Not on Badlion)", (int)((double)(j2 + 4) / d2), (int)((double)(k2 + 63) / d2), -1);
               GL11.glScaled(1.0D / d2, 1.0D / d2, 1.0D);
               Gui.drawRect(scaledresolution1.getScaledWidth() / 2 - 18, k2 + 75, scaledresolution1.getScaledWidth() / 2 + 16, k2 + 87, -14540254);
               this.fontRenderer.drawString("Okay", scaledresolution1.getScaledWidth() / 2 - 12, k2 + 74, -1);
            } else {
               this.setZIndex(100);
            }
         }
      }

   }

   private void drawIcon(ResourceLocation loc, int x, int y, int sizeX, int sizeY) {
      if(this.showIndicatorButton.isEnabled()) {
         GlStateManager.enableAlpha();
         GlStateManager.enableBlend();
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         SlideoutGUI.renderImage(loc, x, y, sizeX, sizeY, 0.2D);
      }

   }

   private void warn() {
      this.warned = true;
      this.showWarning = true;
   }

   public void drawString(RenderGame renderGame, int x, int y, String string) {
      if(this.showIndicatorButton.isEnabled()) {
         if(this.useDefaultFont) {
            renderGame.getGameRenderer().drawString(this.gameInstance.fontRendererObj, string, x, y, this.labelColor.getColorInt());
         } else {
            ColorUtil.bindColor(this.labelColor.getColor());
            Wrapper.getInstance().getBadlionFontRenderer().drawString(x, y, string, 10, BadlionFontRenderer.FontType.TITLE);
         }
      }

   }

   public boolean isVanilla() {
      return this.vanillaTicks > 7;
   }

   public boolean isSprinting() {
      return this.sprinting;
   }

   public boolean isSneaking() {
      return this.sneaking;
   }

   private static enum ToggleSneakMode {
      SPRINT_SNEAK,
      SPRINT,
      SNEAK;
   }

   private static enum ToggleSneakRenderMode {
      CLASSIC,
      ICONS;
   }
}
