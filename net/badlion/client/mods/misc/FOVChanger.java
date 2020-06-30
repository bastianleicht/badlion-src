package net.badlion.client.mods.misc;

import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.Slider;
import net.badlion.client.gui.slideout.TextButton;
import net.badlion.client.mods.render.RenderMod;
import net.badlion.client.mods.render.gui.BoxedCoord;
import net.badlion.client.util.DisplayUtil;
import net.badlion.client.util.ImageDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class FOVChanger extends RenderMod {
   private MutableBoolean dynamicSwiftness = new MutableBoolean(true);
   private transient Slider flyingSlider;
   private double flyingS;
   private int flying;
   private transient Slider slownessSlider;
   private double slownessS;
   private int slowness;
   private transient Slider sprintingSlider;
   private double sprintingS;
   private int sprinting;
   private transient Slider swiftnessSlider;
   private double swiftnessS;
   private int swiftness;
   private transient Slider defaultFovModifier;
   private double defaultFovS;
   private int defaultFov;
   private static transient float stepSize = -1.0F;
   private static transient float currentFov = -1.0F;
   private static transient float targetFov = -1.0F;

   public void reset() {
      this.defaultFov = 70;
      this.defaultFovModifier.setValue(this.defaultFovS = (double)(this.defaultFov - 30) / 80.0D);
      this.slowness = 60;
      this.slownessSlider.setValue(this.slownessS = (double)(this.slowness - 30) / 80.0D);
      this.sprinting = 71;
      this.sprintingSlider.setValue(this.sprintingS = (double)(this.sprinting - 30) / 80.0D);
      this.swiftness = 77;
      this.swiftnessSlider.setValue(this.swiftnessS = (double)(this.swiftness - 30) / 80.0D);
      this.flying = 77;
      this.flyingSlider.setValue(this.flyingS = (double)(this.flying - 30) / 80.0D);
      this.dynamicSwiftness.setValue(true);
      super.reset();
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(0, 3));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(0, 26));
      this.slideCogMenu.addElement(new Label("Settings", -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));
      String[] astring = new String[81];

      for(int i = 0; i < 81; ++i) {
         astring[i] = String.valueOf(i + 30);
      }

      this.slideCogMenu.addElement(this.sprintingSlider = new Slider("Running FOV: ", 0.0D, 1.0D, this.sprintingS, 0.2D));
      this.sprintingSlider.setDisplayText(astring);
      this.sprintingSlider.init();
      this.slideCogMenu.addElement(this.swiftnessSlider = new Slider("Speed FOV: ", 0.0D, 1.0D, this.swiftnessS, 0.2D));
      this.swiftnessSlider.setDisplayText(astring);
      this.swiftnessSlider.init();
      this.slideCogMenu.addElement(this.slownessSlider = new Slider("Slowness FOV: ", 0.0D, 1.0D, this.slownessS, 0.2D));
      this.slownessSlider.setDisplayText(astring);
      this.slownessSlider.init();
      this.slideCogMenu.addElement(this.flyingSlider = new Slider("Flying FOV: ", 0.0D, 1.0D, this.flyingS, 0.2D));
      this.flyingSlider.setDisplayText(astring);
      this.flyingSlider.init();
      this.slideCogMenu.addElement(this.defaultFovModifier = new Slider("Default FOV: ", 0.0D, 1.0D, this.defaultFovS, 0.2D));
      this.defaultFovModifier.setDisplayText(astring);
      this.defaultFovModifier.init();
      TextButton textbutton = new TextButton("Dynamic swiftness", this.dynamicSwiftness, 1.0D);
      textbutton.setToolTipText("When toggled on, your swiftness FOV will increase per swiftness level.");
      this.slideCogMenu.addElement(textbutton);
      super.createCogMenu();
      if(this.flying == 0 && this.slowness == 0 && this.swiftness == 0 && this.sprinting == 0) {
         this.reset();
      }

   }

   public FOVChanger() {
      super("FOV Changer", DisplayUtil.getCenterX(), DisplayUtil.getCenterY(), 0, 0, false);
      this.iconDimension = new ImageDimension(76, 76);
      this.defaultTopLeftBox = new BoxedCoord(0, 0, 0.0D, 0.0D);
      this.defaultCenterBox = new BoxedCoord(0, 0, 0.0D, 0.0D);
      this.defaultBottomRightBox = new BoxedCoord(0, 0, 0.0D, 0.0D);
   }

   public void init() {
      this.registerEvent(EventType.RENDER_GAME);
      this.registerEvent(EventType.MOTION_UPDATE);
      this.disableGuiEditing();
      this.setFontOffset(0.017D);
      super.init();
   }

   public void onEvent(Event event) {
      if(event instanceof MotionUpdate && this.loadedCogMenu) {
         this.flyingS = this.flyingSlider.getValue();
         this.slownessS = this.slownessSlider.getValue();
         this.swiftnessS = this.swiftnessSlider.getValue();
         this.sprintingS = this.sprintingSlider.getValue();
         this.defaultFovS = this.defaultFovModifier.getValue();
         this.flying = Integer.parseInt(this.flyingSlider.getCurrentDisplayText());
         this.slowness = Integer.parseInt(this.slownessSlider.getCurrentDisplayText());
         this.swiftness = Integer.parseInt(this.swiftnessSlider.getCurrentDisplayText());
         this.sprinting = Integer.parseInt(this.sprintingSlider.getCurrentDisplayText());
         this.defaultFov = Integer.parseInt(this.defaultFovModifier.getCurrentDisplayText());
      }

      super.onEvent(event);
   }

   public int getFlying() {
      return this.flying;
   }

   public int getDefaultFov() {
      return this.defaultFov;
   }

   public int getSlowness() {
      return this.slowness;
   }

   public int getSwiftness() {
      if(this.dynamicSwiftness.isFalse()) {
         return this.swiftness;
      } else {
         int i = 0;

         for(PotionEffect potioneffect : Minecraft.getMinecraft().thePlayer.getActivePotionEffects()) {
            if(potioneffect.getPotionID() == Potion.moveSpeed.getId()) {
               i = potioneffect.getAmplifier();
               break;
            }
         }

         ++i;
         return (int)((double)this.defaultFov + (double)this.defaultFov / 70.0D * 7.0D * (double)i);
      }
   }

   public int getSprinting() {
      return this.sprinting;
   }

   public static float getFov(float var4) {
      FOVChanger fovchanger = Wrapper.getInstance().getActiveModProfile().getFovChanger();
      if(!fovchanger.isEnabled()) {
         return var4;
      } else {
         var4 = (float)fovchanger.getDefaultFov();
         if(Minecraft.getMinecraft().thePlayer.isSprinting()) {
            var4 += (float)(fovchanger.getSprinting() - fovchanger.getDefaultFov());
         }

         if(Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSpeed)) {
            var4 += (float)(fovchanger.getSwiftness() - fovchanger.getDefaultFov());
         }

         if(Minecraft.getMinecraft().thePlayer.isPotionActive(Potion.moveSlowdown)) {
            var4 += (float)(fovchanger.getSlowness() - fovchanger.getDefaultFov());
         }

         if(Minecraft.getMinecraft().thePlayer.capabilities.isFlying) {
            var4 += (float)(fovchanger.getFlying() - fovchanger.getDefaultFov());
         }

         if(currentFov == -1.0F) {
            currentFov = var4;
         }

         if(targetFov != var4) {
            stepSize = -1.0F;
         }

         if(stepSize == -1.0F) {
            stepSize = var4 - currentFov;
            targetFov = var4;
         }

         currentFov = (float)((double)currentFov + (double)(stepSize / 15.0F) * (60.0D / (double)Minecraft.getDebugFPS()));
         if(stepSize < 0.0F) {
            if(currentFov < var4) {
               currentFov = var4;
            }
         } else if(currentFov > var4) {
            currentFov = var4;
         }

         return currentFov >= 30.0F && currentFov <= 110.0F?currentFov:var4;
      }
   }
}
