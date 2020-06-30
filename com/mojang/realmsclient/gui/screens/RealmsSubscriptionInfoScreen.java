package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.Subscription;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.util.RealmsUtil;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class RealmsSubscriptionInfoScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private final RealmsScreen lastScreen;
   private final RealmsServer serverData;
   private final RealmsScreen mainScreen;
   private final int BUTTON_BACK_ID = 0;
   private final int BUTTON_DELETE_ID = 1;
   private int daysLeft;
   private String startDate;
   private Subscription.SubscriptionType type;
   private final String PURCHASE_LINK = "https://account.mojang.com/buy/realms";
   private boolean onLink;

   public RealmsSubscriptionInfoScreen(RealmsScreen lastScreen, RealmsServer serverData, RealmsScreen mainScreen) {
      this.lastScreen = lastScreen;
      this.serverData = serverData;
      this.mainScreen = mainScreen;
   }

   public void init() {
      this.getSubscription(this.serverData.id);
      Keyboard.enableRepeatEvents(true);
      this.buttonsAdd(newButton(0, this.width() / 2 - 100, RealmsConstants.row(12), getLocalizedString("gui.back")));
      if(this.serverData.expired) {
         this.buttonsAdd(newButton(1, this.width() / 2 - 100, RealmsConstants.row(10), getLocalizedString("mco.configure.world.delete.button")));
      }

   }

   private void getSubscription(long worldId) {
      RealmsClient client = RealmsClient.createRealmsClient();

      try {
         Subscription subscription = client.subscriptionFor(worldId);
         this.daysLeft = subscription.daysLeft;
         this.startDate = this.localPresentation(subscription.startDate);
         this.type = subscription.type;
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn\'t get subscription");
         Realms.setScreen(new RealmsGenericErrorScreen(var5, this.lastScreen));
      } catch (IOException var6) {
         LOGGER.error("Couldn\'t parse response subscribing");
      }

   }

   public void confirmResult(boolean result, int id) {
      if(id == 1 && result) {
         (new Thread("Realms-delete-realm") {
            public void run() {
               try {
                  RealmsClient client = RealmsClient.createRealmsClient();
                  client.deleteWorld(RealmsSubscriptionInfoScreen.this.serverData.id);
               } catch (RealmsServiceException var2) {
                  RealmsSubscriptionInfoScreen.LOGGER.error("Couldn\'t delete world");
                  RealmsSubscriptionInfoScreen.LOGGER.error((Object)var2);
               } catch (IOException var3) {
                  RealmsSubscriptionInfoScreen.LOGGER.error("Couldn\'t delete world");
                  var3.printStackTrace();
               }

               Realms.setScreen(RealmsSubscriptionInfoScreen.this.mainScreen);
            }
         }).start();
      }

      Realms.setScreen(this);
   }

   private String localPresentation(long cetTime) {
      Calendar cal = new GregorianCalendar(TimeZone.getDefault());
      cal.setTimeInMillis(cetTime);
      return SimpleDateFormat.getDateTimeInstance().format(cal.getTime());
   }

   public void removed() {
      Keyboard.enableRepeatEvents(false);
   }

   public void buttonClicked(RealmsButton button) {
      if(button.active()) {
         if(button.id() == 0) {
            Realms.setScreen(this.lastScreen);
         } else if(button.id() == 1) {
            String line2 = getLocalizedString("mco.configure.world.delete.question.line1");
            String line3 = getLocalizedString("mco.configure.world.delete.question.line2");
            Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Warning, line2, line3, true, 1));
         }

      }
   }

   public void keyPressed(char ch, int eventKey) {
      if(eventKey == 1) {
         Realms.setScreen(this.lastScreen);
      }

   }

   public void mouseClicked(int x, int y, int buttonNum) {
      super.mouseClicked(x, y, buttonNum);
      if(this.onLink) {
         String extensionUrl = "https://account.mojang.com/buy/realms?sid=" + this.serverData.remoteSubscriptionId + "&pid=" + Realms.getUUID();
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(new StringSelection(extensionUrl), (ClipboardOwner)null);
         RealmsUtil.browseTo(extensionUrl);
      }

   }

   public void render(int xm, int ym, float a) {
      this.renderBackground();
      int center = this.width() / 2 - 100;
      this.drawCenteredString(getLocalizedString("mco.configure.world.subscription.title"), this.width() / 2, 17, 16777215);
      this.drawString(getLocalizedString("mco.configure.world.subscription.start"), center, RealmsConstants.row(0), 10526880);
      this.drawString(this.startDate, center, RealmsConstants.row(1), 16777215);
      if(this.type == Subscription.SubscriptionType.NORMAL) {
         this.drawString(getLocalizedString("mco.configure.world.subscription.timeleft"), center, RealmsConstants.row(3), 10526880);
      } else if(this.type == Subscription.SubscriptionType.RECURRING) {
         this.drawString(getLocalizedString("mco.configure.world.subscription.recurring.daysleft"), center, RealmsConstants.row(3), 10526880);
      }

      this.drawString(this.daysLeftPresentation(this.daysLeft), center, RealmsConstants.row(4), 16777215);
      this.drawString(getLocalizedString("mco.configure.world.subscription.extendHere"), center, RealmsConstants.row(6), 10526880);
      int height = RealmsConstants.row(7);
      int textWidth = this.fontWidth("https://account.mojang.com/buy/realms");
      int x1 = this.width() / 2 - textWidth / 2 - 1;
      int y1 = height - 1;
      int x2 = x1 + textWidth + 1;
      int y2 = height + 1 + this.fontLineHeight();
      if(x1 <= xm && xm <= x2 && y1 <= ym && ym <= y2) {
         this.onLink = true;
         this.drawString("https://account.mojang.com/buy/realms", this.width() / 2 - textWidth / 2, height, 7107012);
      } else {
         this.onLink = false;
         this.drawString("https://account.mojang.com/buy/realms", this.width() / 2 - textWidth / 2, height, 3368635);
      }

      super.render(xm, ym, a);
   }

   private String daysLeftPresentation(int daysLeft) {
      if(daysLeft == -1) {
         return "Expired";
      } else if(daysLeft <= 1) {
         return getLocalizedString("mco.configure.world.subscription.less_than_a_day");
      } else {
         int months = daysLeft / 30;
         int days = daysLeft % 30;
         StringBuilder sb = new StringBuilder();
         if(months > 0) {
            sb.append(months).append(" ");
            if(months == 1) {
               sb.append(getLocalizedString("mco.configure.world.subscription.month").toLowerCase());
            } else {
               sb.append(getLocalizedString("mco.configure.world.subscription.months").toLowerCase());
            }
         }

         if(days > 0) {
            if(sb.length() > 0) {
               sb.append(", ");
            }

            sb.append(days).append(" ");
            if(days == 1) {
               sb.append(getLocalizedString("mco.configure.world.subscription.day").toLowerCase());
            } else {
               sb.append(getLocalizedString("mco.configure.world.subscription.days").toLowerCase());
            }
         }

         return sb.toString();
      }
   }
}
