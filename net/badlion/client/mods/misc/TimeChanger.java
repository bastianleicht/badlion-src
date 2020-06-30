package net.badlion.client.mods.misc;

import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.Dropdown;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.Slider;
import net.badlion.client.gui.slideout.TextButton;
import net.badlion.client.mods.Mod;
import net.badlion.client.util.ImageDimension;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class TimeChanger extends Mod {
   private int currentTime = 0;
   private double timeMultiplier = 0.5D;
   private double timeSpeed = 1.0D;
   private boolean timeFrozen = false;
   private transient Slider timeSpeedSlider;
   private transient TextButton freezeTimeButton;
   private transient Dropdown presetSelectionDropdown;
   private int presetID;

   public TimeChanger() {
      super("TimeChanger", false);
      this.iconDimension = new ImageDimension(90, 90);
   }

   public boolean isTimeFrozen() {
      return this.timeFrozen;
   }

   public void init() {
      this.registerEvent(EventType.MOTION_UPDATE);
      super.init();
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label("Settings", -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));
      this.freezeTimeButton = new TextButton("Freeze Time", new MutableBoolean(this.timeFrozen), 1.0D);
      this.slideCogMenu.addElement(this.freezeTimeButton);
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 3));
      String[] astring = new String[]{"Always Day", "Always Night", "Always Midnight", "Always Sunset"};
      this.presetSelectionDropdown = new Dropdown(astring, this.presetID, 0.2D);
      this.slideCogMenu.addElement(this.presetSelectionDropdown);
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 3));
      this.timeSpeedSlider = new Slider("Time Speed", 0.0D, 1.0D, this.timeMultiplier, 0.18D);
      String[] astring1 = new String[]{"0.25x", "0.5x", "0.75x", "1.0x", "1.25x", "1.5x", "1.75x", "2.0x"};
      this.timeSpeedSlider.setDisplayText(astring1);
      this.slideCogMenu.addElement(this.timeSpeedSlider);
      super.createCogMenu();
   }

   public void onEvent(Event e) {
      if(e instanceof MotionUpdate && this.isEnabled()) {
         this.timeFrozen = this.freezeTimeButton.isEnabled();
         this.timeMultiplier = this.timeSpeedSlider.getValue();
         if(this.timeMultiplier >= 0.0D && this.timeMultiplier < 0.12D) {
            this.timeSpeed = 0.25D;
         } else if(this.timeMultiplier >= 0.12D && this.timeMultiplier < 0.25D) {
            this.timeSpeed = 0.5D;
         } else if(this.timeMultiplier >= 0.25D && this.timeMultiplier < 0.37D) {
            this.timeSpeed = 0.75D;
         } else if(this.timeMultiplier >= 0.37D && this.timeMultiplier < 0.5D) {
            this.timeSpeed = 1.0D;
         } else if(this.timeMultiplier >= 0.5D && this.timeMultiplier < 0.63D) {
            this.timeSpeed = 1.25D;
         } else if(this.timeMultiplier >= 0.63D && this.timeMultiplier < 0.75D) {
            this.timeSpeed = 1.5D;
         } else if(this.timeMultiplier >= 0.75D && this.timeMultiplier < 0.88D) {
            this.timeSpeed = 1.75D;
         } else {
            this.timeSpeed = 2.0D;
         }

         if(this.timeFrozen) {
            this.gameInstance.theWorld.setWorldTime(this.getCurrentWorldTimePreset());
         }
      }

      super.onEvent(e);
   }

   public long getCurrentWorldTimePreset() {
      long i = 0L;
      String s = this.presetSelectionDropdown.getValue();
      byte b0 = -1;
      switch(s.hashCode()) {
      case -2082005561:
         if(s.equals("Always Sunset")) {
            b0 = 3;
         }
         break;
      case -1734711609:
         if(s.equals("Always Night")) {
            b0 = 1;
         }
         break;
      case -1119132149:
         if(s.equals("Always Day")) {
            b0 = 0;
         }
         break;
      case 724547009:
         if(s.equals("Always Midnight")) {
            b0 = 2;
         }
      }

      switch(b0) {
      case 0:
         i = (long)TimeChanger.TimePreset.ALWAYS_DAY.getTime();
         this.presetID = 0;
         break;
      case 1:
         i = (long)TimeChanger.TimePreset.ALWAYS_NIGHT.getTime();
         this.presetID = 1;
         break;
      case 2:
         i = (long)TimeChanger.TimePreset.ALWAYS_MIDNIGHT.getTime();
         this.presetID = 2;
         break;
      case 3:
         i = (long)TimeChanger.TimePreset.ALWAYS_SUNSET.getTime();
         this.presetID = 3;
      }

      return i;
   }

   public double getTimeMultiplier() {
      return this.timeSpeed;
   }

   private static enum TimePreset {
      ALWAYS_DAY(0),
      ALWAYS_NIGHT(15000),
      ALWAYS_MIDNIGHT(18000),
      ALWAYS_SUNSET(13150);

      private int time;

      private TimePreset(int time) {
         this.time = time;
      }

      public int getTime() {
         return this.time;
      }
   }
}
