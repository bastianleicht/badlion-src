package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsState;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.util.RealmsUtil;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;

public class RealmsBuyRealmsScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private RealmsScreen lastScreen;
   private static int BUTTON_BACK_ID = 0;
   private volatile RealmsState realmsStatus;
   private boolean onLink = false;

   public RealmsBuyRealmsScreen(RealmsScreen lastScreen) {
      this.lastScreen = lastScreen;
   }

   public void tick() {
      super.tick();
   }

   public void init() {
      Keyboard.enableRepeatEvents(true);
      this.buttonsClear();
      int buttonLength = 212;
      this.buttonsAdd(newButton(BUTTON_BACK_ID, this.width() / 2 - buttonLength / 2, RealmsConstants.row(12), buttonLength, 20, getLocalizedString("gui.back")));
      this.fetchMessage();
   }

   private void fetchMessage() {
      final RealmsClient client = RealmsClient.createRealmsClient();
      (new Thread("Realms-stat-message") {
         public void run() {
            try {
               RealmsBuyRealmsScreen.this.realmsStatus = client.fetchRealmsState();
            } catch (RealmsServiceException var2) {
               RealmsBuyRealmsScreen.LOGGER.error("Could not get state");
               Realms.setScreen(new RealmsGenericErrorScreen(var2, RealmsBuyRealmsScreen.this.lastScreen));
            }

         }
      }).start();
   }

   public void removed() {
      Keyboard.enableRepeatEvents(false);
   }

   public void buttonClicked(RealmsButton button) {
      if(button.active()) {
         if(button.id() == BUTTON_BACK_ID) {
            Realms.setScreen(this.lastScreen);
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
         Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
         clipboard.setContents(new StringSelection(this.realmsStatus.getBuyLink()), (ClipboardOwner)null);
         RealmsUtil.browseTo(this.realmsStatus.getBuyLink());
      }

   }

   public void render(int xm, int ym, float a) {
      this.renderBackground();
      this.drawCenteredString(getLocalizedString("mco.buy.realms.title"), this.width() / 2, 17, 16777215);
      if(this.realmsStatus != null) {
         String[] lines = this.realmsStatus.getStatusMessage().split("\n");
         int i = 1;

         for(String line : lines) {
            this.drawCenteredString(line, this.width() / 2, RealmsConstants.row(i), 10526880);
            i += 2;
         }

         if(this.realmsStatus.getBuyLink() != null) {
            String buyLink = this.realmsStatus.getBuyLink();
            int height = RealmsConstants.row(i + 1);
            int textWidth = this.fontWidth(buyLink);
            int x1 = this.width() / 2 - textWidth / 2 - 1;
            int y1 = height - 1;
            int x2 = x1 + textWidth + 1;
            int y2 = height + 1 + this.fontLineHeight();
            if(x1 <= xm && xm <= x2 && y1 <= ym && ym <= y2) {
               this.onLink = true;
               this.drawString(buyLink, this.width() / 2 - textWidth / 2, height, 7107012);
            } else {
               this.onLink = false;
               this.drawString(buyLink, this.width() / 2 - textWidth / 2, height, 3368635);
            }
         }

         super.render(xm, ym, a);
      }
   }
}
