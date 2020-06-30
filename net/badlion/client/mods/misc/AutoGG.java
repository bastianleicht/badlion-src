package net.badlion.client.mods.misc;

import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.ChatReceivedEvent;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.Slider;
import net.badlion.client.mods.Mod;
import net.badlion.client.util.ImageDimension;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;

public class AutoGG extends Mod {
   private double delayS = 0.0D;
   private int delay = 0;
   private transient double currentDelay = -1.0D;
   private transient boolean hypixel = false;
   private transient Slider timeSpeedSlider;
   private String[] triggers = new String[]{"1st Killer - ", "1st Place - ", "Winner: ", " - Damage Dealt - ", "Winning Team -", "1st - ", "Winners: ", "Winner: ", "Winning Team: ", " won the game!", "Top Seeker: ", "1st Place: ", "Last team standing!", "Winner #1 (", "Top Survivors", "Winners - "};

   public AutoGG() {
      super("AutoGG", false);
      this.iconDimension = new ImageDimension(100, 58);
   }

   public void init() {
      this.registerEvent(EventType.MOTION_UPDATE);
      this.registerEvent(EventType.CHAT_RECEIVED);
      super.init();
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label("Settings", -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 3));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 3));
      this.timeSpeedSlider = new Slider("Send after:", 0.0D, 1.0D, this.delayS, 0.18D);
      this.timeSpeedSlider.setDisplayText(new String[]{"0s", "1s", "2s", "3s", "4s", "5s"});
      this.timeSpeedSlider.init();
      this.slideCogMenu.addElement(this.timeSpeedSlider);
      super.createCogMenu();
   }

   public void onEvent(Event e) {
      if(e instanceof MotionUpdate && this.isEnabled()) {
         try {
            this.delayS = this.timeSpeedSlider.getValue();
            this.delay = Integer.parseInt(this.timeSpeedSlider.getCurrentDisplayText().substring(0, this.timeSpeedSlider.getCurrentDisplayText().length() - 1));
         } catch (Exception var3) {
            ;
         }

         if(this.currentDelay != -1.0D) {
            this.currentDelay += 0.05D;
            if(this.currentDelay >= (double)this.delay) {
               Minecraft.getMinecraft().thePlayer.sendChatMessage("gg");
               this.currentDelay = -1.0D;
            }
         }
      }

      if(e instanceof ChatReceivedEvent) {
         this.handleChat(((ChatReceivedEvent)e).getComponent());
      }

      super.onEvent(e);
   }

   public void handleJoin(String hostName) {
      this.hypixel = hostName.toLowerCase().contains("hypixel.net");
   }

   private void handleChat(IChatComponent chatComponent) {
      if(this.isEnabled() && this.hypixel) {
         String s = EnumChatFormatting.getTextWithoutFormattingCodes(chatComponent.getUnformattedText());
         if(s != null && s.startsWith(" ")) {
            for(String s1 : this.triggers) {
               if(s.contains(s1)) {
                  this.currentDelay = 0.0D;
                  break;
               }
            }
         }
      }

   }
}
