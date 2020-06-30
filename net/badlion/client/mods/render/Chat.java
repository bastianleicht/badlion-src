package net.badlion.client.mods.render;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import net.badlion.client.Wrapper;
import net.badlion.client.events.Event;
import net.badlion.client.events.EventType;
import net.badlion.client.events.event.ChatReceivedEvent;
import net.badlion.client.events.event.GUIClickMouse;
import net.badlion.client.events.event.MotionUpdate;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.Dropdown;
import net.badlion.client.gui.slideout.Label;
import net.badlion.client.gui.slideout.Padding;
import net.badlion.client.gui.slideout.SlidePage;
import net.badlion.client.gui.slideout.SlideoutGUI;
import net.badlion.client.gui.slideout.TextButton;
import net.badlion.client.gui.slideout.elements.ColorPicker;
import net.badlion.client.mods.Mod;
import net.badlion.client.mods.render.color.ModColor;
import net.badlion.client.util.ConvertUtil;
import net.badlion.client.util.ImageDimension;
import net.minecraft.client.gui.ChatLine;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class Chat extends Mod {
   private ModColor chatBackgroundColor = new ModColor(2130706432);
   private MutableBoolean textBackgroundShadow = new MutableBoolean(true);
   private MutableBoolean timeStamp24h = new MutableBoolean(true);
   private MutableBoolean timeStampBold = new MutableBoolean(false);
   private Chat.TimeStampType timeStampType = Chat.TimeStampType.NONE;
   private Chat.MCColor timeStampColor = Chat.MCColor.WHITE;
   private MutableBoolean antiSpam = new MutableBoolean(false);
   private transient Dropdown timeStampDropdown;
   private transient Dropdown timeStampColorDropdown;
   private transient TextButton textShadow;
   private transient TextButton timeStampButton;
   private transient SimpleDateFormat timestampFormat;

   private static String toTitleCase(String givenString) {
      String[] astring = givenString.split(" ");
      StringBuilder stringbuilder = new StringBuilder();

      for(String s : astring) {
         stringbuilder.append(Character.toUpperCase(s.charAt(0))).append(s.substring(1)).append(" ");
      }

      return stringbuilder.toString().trim();
   }

   public Chat() {
      super("Chat");
      this.iconDimension = new ImageDimension(92, 82);
   }

   public void init() {
      this.registerEvent(EventType.MOTION_UPDATE);
      this.registerEvent(EventType.CHAT_RECEIVED);
      this.setFontOffset(0.034D);
      if(this.timeStampType != Chat.TimeStampType.NONE) {
         this.timestampFormat = new SimpleDateFormat(this.timeStamp24h.isTrue()?this.timeStampType.format24:this.timeStampType.format12);
      }

      super.init();
   }

   public void createCogMenu() {
      SlideoutGUI slideoutgui = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance();
      this.slideCogMenu = new SlidePage(this.getName() + "_cog", slideoutgui.getSlideoutWidth(), slideoutgui.getSlideoutHeight());
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label(this.getName(), -1, 16, BadlionFontRenderer.FontType.TITLE, false));
      this.slideCogMenu.addElement(new Padding(slideoutgui.getSlideoutWidth() - 25, 6));
      this.slideCogMenu.addElement(new Label("Settings", -7894388, 12, BadlionFontRenderer.FontType.TITLE, true));
      this.slideCogMenu.addElement(this.textShadow = new TextButton("Text Shadow", this.textBackgroundShadow, 1.0D));
      this.textShadow.setToolTipText("Turn off/on text shadow. Can increase FPS if there is a lot of chat visible.");
      this.slideCogMenu.addElement(this.timeStampButton = new TextButton("24 Hour format", this.timeStamp24h, 1.0D));
      this.slideCogMenu.addElement(new TextButton("Bold format", this.timeStampBold, 1.0D));
      this.slideCogMenu.addElement(new ColorPicker("Back Color", this.chatBackgroundColor, 0.13D, true));
      this.slideCogMenu.addElement(new TextButton("Anti Spam", this.antiSpam, 1.0D));
      String[] astring = new String[Chat.TimeStampType.values().length];
      astring[0] = "None";

      for(int i = 1; i < astring.length; ++i) {
         astring[i] = Chat.TimeStampType.values()[i].getPreview(this.timeStamp24h.isTrue());
      }

      this.slideCogMenu.addElement(this.timeStampDropdown = new Dropdown(astring, this.timeStampType.ordinal(), 0.19D));
      astring = new String[Chat.MCColor.values().length];
      int j = 0;

      Chat.MCColor[] var7;
      for(Chat.MCColor chat$mccolor : var7 = Chat.MCColor.values()) {
         astring[j++] = chat$mccolor.getDisplayName();
      }

      this.slideCogMenu.addElement(this.timeStampColorDropdown = new Dropdown(astring, this.timeStampColor.ordinal(), 0.19D));
      this.chatBackgroundColor.init();
      super.createCogMenu();
   }

   public void onEvent(Event e) {
      if(e instanceof MotionUpdate) {
         Chat.TimeStampType chat$timestamptype = this.timeStampType;
         this.timeStampType = Chat.TimeStampType.values()[this.timeStampDropdown.getValueIndex()];
         if(chat$timestamptype != this.timeStampType) {
            this.updateFormat();
         }

         this.timeStampColor = Chat.MCColor.values()[this.timeStampColorDropdown.getValueIndex()];
      }

      if(e instanceof GUIClickMouse && ((GUIClickMouse)e).getMouseButton() == 0) {
         int j = Wrapper.getInstance().getMouseX();
         int i = Wrapper.getInstance().getMouseY();
         if(j > this.timeStampButton.getX() && (double)j < (double)this.timeStampButton.getX() + (double)this.timeStampButton.getWidth() * this.timeStampButton.getScale() && i > this.timeStampButton.getY() && (double)i < (double)this.timeStampButton.getY() + (double)this.timeStampButton.getHeight() * this.timeStampButton.getScale() && this.timeStampButton.isToggleable()) {
            this.updateFormat(this.timeStamp24h.isFalse());
            this.updateDropdown();
         }
      }

      if(e instanceof ChatReceivedEvent && this.antiSpam.isTrue()) {
         this.checkSpam((ChatReceivedEvent)e);
      }

      super.onEvent(e);
   }

   public void reset() {
      this.offsetX = 0;
      this.offsetY = 0;
      this.textShadow.setEnabled(true);
      this.chatBackgroundColor = new ModColor(2130706432);
      this.timeStamp24h.setValue(true);
      this.timeStampType = Chat.TimeStampType.NONE;
      this.timeStampBold.setValue(false);
      this.timeStampColor = Chat.MCColor.WHITE;
      super.reset();
   }

   public TextButton getTextShadow() {
      return this.textShadow;
   }

   public boolean isTextBackgroundShadow() {
      return this.textBackgroundShadow.booleanValue();
   }

   public ModColor getChatBackgroundColor() {
      return this.chatBackgroundColor;
   }

   public String getPrefix(Date date) {
      return this.isEnabled() && this.timeStampType != Chat.TimeStampType.NONE?this.timeStampColor.getColor() + (this.timeStampBold.isTrue()?"§l":"") + this.timestampFormat.format(date) + (this.timeStampBold.isTrue()?"§r":""):null;
   }

   private void updateDropdown() {
      String[] astring = new String[Chat.TimeStampType.values().length];
      astring[0] = "None";

      for(int i = 1; i < astring.length; ++i) {
         astring[i] = Chat.TimeStampType.values()[i].getPreview(this.timeStamp24h.isTrue());
      }

      this.timeStampDropdown.setList(astring);
   }

   private void updateFormat() {
      this.updateFormat(this.timeStamp24h.isTrue());
   }

   private void updateFormat(boolean value) {
      this.timestampFormat = new SimpleDateFormat(value?this.timeStampType.format24:this.timeStampType.format12);
   }

   private void checkSpam(ChatReceivedEvent event) {
      if(!event.getChatLines().isEmpty()) {
         String s = event.getComponent().getUnformattedText();
         int i = 1;
         Iterator<ChatLine> iterator = event.getChatLines().iterator();

         while(true) {
            while(true) {
               if(!iterator.hasNext()) {
                  if(i > 1) {
                     event.getComponent().appendText(" [x" + i + "]");
                  }

                  return;
               }

               String s1 = ((ChatLine)iterator.next()).getChatComponent().getUnformattedText();
               if(s1.startsWith(s)) {
                  if(s1.length() == s.length()) {
                     ++i;
                     break;
                  }

                  String s2 = s1.substring(s.length());
                  if(s2.startsWith(" [x") && s2.endsWith("]")) {
                     String s3 = s2.substring(3, s2.length() - 1);
                     if(ConvertUtil.isPositiveInteger(s3)) {
                        i += Integer.parseInt(s3);
                        break;
                     }
                  }
               }
            }

            iterator.remove();
         }
      }
   }

   static enum MCColor {
      BLACK('0'),
      DARK_BLUE('1'),
      DARK_GREEN('2'),
      DARK_AQUA('3'),
      DARK_RED('4'),
      DARK_PURPLE('5'),
      GOLD('6'),
      GRAY('7'),
      DARK_GRAY('8'),
      BLUE('9'),
      GREEN('a'),
      AQUA('b'),
      RED('c'),
      LIGHT_PURPLE('d'),
      YELLOW('e'),
      WHITE('f');

      private char colorCode;

      private MCColor(char colorCode) {
         this.colorCode = colorCode;
      }

      public String getColor() {
         return "§" + this.colorCode;
      }

      public String getDisplayName() {
         return Chat.toTitleCase(this.name().replaceAll("_", " "));
      }
   }

   static enum TimeStampType {
      NONE("", ""),
      YEAR_MONTH_DAY_HOUR_MINUTE_SECOND("[MMM d yyyy h:mm:ss a] ", "[MMM d yyyy HH:mm:ss] "),
      YEAR_MONTH_DAY_HOUR_MINUTE("[MMM d yyyy h:mm a] ", "[MMM d yyyy HH:mm] "),
      MONTH_DAY_HOUR_MINUTE_SECOND("[MMM d h:mm:ss a] ", "[MMM d HH:mm:ss] "),
      MONTH_DAY_HOUR_MINUTE("[MMM d h:mm a] ", "[MMM d HH:mm] "),
      DAY_HOUR_MINUTE_SECOND("[E h:mm:ss a] ", "[E HH:mm:ss] "),
      DAY_HOUR_MINUTE("[E h:mm a] ", "[E HH:mm] "),
      HOUR_MINUTE_SECOND("[h:mm:ss a] ", "[HH:mm:ss] "),
      HOUR_MINUTE("[h:mm a] ", "[HH:mm] ");

      private String format12;
      private String format24;

      private TimeStampType(String format12, String format24) {
         this.format12 = format12;
         this.format24 = format24;
      }

      public String getPreview(boolean twentyFour) {
         Date date = new Date();
         return (new SimpleDateFormat(twentyFour?this.format24:this.format12)).format(date);
      }
   }
}
