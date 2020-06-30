package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsOptions;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.WorldTemplate;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.RealmsHideableButton;
import com.mojang.realmsclient.gui.screens.RealmsBackupScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsPlayerScreen;
import com.mojang.realmsclient.gui.screens.RealmsResetWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsScreenWithCallback;
import com.mojang.realmsclient.gui.screens.RealmsSelectWorldTemplateScreen;
import com.mojang.realmsclient.gui.screens.RealmsSettingsScreen;
import com.mojang.realmsclient.gui.screens.RealmsSlotOptionsScreen;
import com.mojang.realmsclient.gui.screens.RealmsSubscriptionInfoScreen;
import com.mojang.realmsclient.util.RealmsTasks;
import com.mojang.realmsclient.util.RealmsTextureManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map.Entry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsMth;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSharedConstants;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class RealmsConfigureWorldScreen extends RealmsScreenWithCallback {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final String ON_ICON_LOCATION = "realms:textures/gui/realms/on_icon.png";
   private static final String OFF_ICON_LOCATION = "realms:textures/gui/realms/off_icon.png";
   private static final String EXPIRED_ICON_LOCATION = "realms:textures/gui/realms/expired_icon.png";
   private static final String SLOT_FRAME_LOCATION = "realms:textures/gui/realms/slot_frame.png";
   private static final String EMPTY_FRAME_LOCATION = "realms:textures/gui/realms/empty_frame.png";
   private String toolTip;
   private final RealmsScreen lastScreen;
   private RealmsServer serverData;
   private volatile long serverId;
   private int left_x;
   private int right_x;
   private int default_button_width = 80;
   private int default_button_offset = 5;
   private static final int BUTTON_BACK_ID = 0;
   private static final int BUTTON_PLAYERS_ID = 2;
   private static final int BUTTON_SETTINGS_ID = 3;
   private static final int BUTTON_SUBSCRIPTION_ID = 4;
   private static final int BUTTON_OPTIONS_ID = 5;
   private static final int BUTTON_BACKUP_ID = 6;
   private static final int BUTTON_RESET_WORLD_ID = 7;
   private static final int BUTTON_SWITCH_MINIGAME_ID = 8;
   private static final int SWITCH_SLOT_ID = 9;
   private static final int SWITCH_SLOT_ID_EMPTY = 10;
   private static final int SWITCH_SLOT_ID_RESULT = 11;
   private RealmsButton playersButton;
   private RealmsButton settingsButton;
   private RealmsButton subscriptionButton;
   private RealmsHideableButton optionsButton;
   private RealmsHideableButton backupButton;
   private RealmsHideableButton resetWorldButton;
   private RealmsHideableButton switchMinigameButton;
   private boolean stateChanged;
   private int hoveredSlot = -1;
   private int animTick;
   private int clicks = 0;
   private boolean hoveredActiveSlot = false;

   public RealmsConfigureWorldScreen(RealmsScreen lastScreen, long serverId) {
      this.lastScreen = lastScreen;
      this.serverId = serverId;
   }

   public void mouseEvent() {
      super.mouseEvent();
   }

   public void init() {
      if(this.serverData == null) {
         this.fetchServerData(this.serverId);
      }

      this.left_x = this.width() / 2 - 187;
      this.right_x = this.width() / 2 + 190;
      Keyboard.enableRepeatEvents(true);
      this.buttonsClear();
      this.buttonsAdd(this.playersButton = newButton(2, this.centerButton(0, 3), RealmsConstants.row(0), this.default_button_width, 20, getLocalizedString("mco.configure.world.buttons.players")));
      this.buttonsAdd(this.settingsButton = newButton(3, this.centerButton(1, 3), RealmsConstants.row(0), this.default_button_width, 20, getLocalizedString("mco.configure.world.buttons.settings")));
      this.buttonsAdd(this.subscriptionButton = newButton(4, this.centerButton(2, 3), RealmsConstants.row(0), this.default_button_width, 20, getLocalizedString("mco.configure.world.buttons.subscription")));
      this.buttonsAdd(this.optionsButton = new RealmsHideableButton(5, this.leftButton(0), RealmsConstants.row(13) - 5, this.default_button_width + 10, 20, getLocalizedString("mco.configure.world.buttons.options")));
      this.buttonsAdd(this.backupButton = new RealmsHideableButton(6, this.leftButton(1), RealmsConstants.row(13) - 5, this.default_button_width + 10, 20, getLocalizedString("mco.configure.world.backup")));
      this.buttonsAdd(this.resetWorldButton = new RealmsHideableButton(7, this.leftButton(2), RealmsConstants.row(13) - 5, this.default_button_width + 10, 20, getLocalizedString("mco.configure.world.buttons.resetworld")));
      this.buttonsAdd(this.switchMinigameButton = new RealmsHideableButton(8, this.leftButton(0), RealmsConstants.row(13) - 5, this.default_button_width + 20, 20, getLocalizedString("mco.configure.world.buttons.switchminigame")));
      this.buttonsAdd(newButton(0, this.right_x - this.default_button_width + 8, RealmsConstants.row(13) - 5, this.default_button_width - 10, 20, getLocalizedString("gui.back")));
      this.backupButton.active(true);
      if(this.serverData == null) {
         this.hideMinigameButtons();
         this.hideRegularButtons();
         this.playersButton.active(false);
         this.settingsButton.active(false);
         this.subscriptionButton.active(false);
      } else {
         this.disableButtons();
         if(this.isMinigame()) {
            this.hideRegularButtons();
         } else {
            this.hideMinigameButtons();
         }
      }

   }

   private int leftButton(int i) {
      return this.left_x + i * (this.default_button_width + 10 + this.default_button_offset);
   }

   private int centerButton(int i, int total) {
      return this.width() / 2 - (total * (this.default_button_width + this.default_button_offset) - this.default_button_offset) / 2 + i * (this.default_button_width + this.default_button_offset);
   }

   public void tick() {
      ++this.animTick;
      --this.clicks;
      if(this.clicks < 0) {
         this.clicks = 0;
      }

   }

   public void render(int xm, int ym, float a) {
      this.toolTip = null;
      this.hoveredActiveSlot = false;
      this.hoveredSlot = -1;
      this.renderBackground();
      this.drawCenteredString(getLocalizedString("mco.configure.worlds.title"), this.width() / 2, RealmsConstants.row(4), 16777215);
      super.render(xm, ym, a);
      if(this.serverData == null) {
         this.drawCenteredString(getLocalizedString("mco.configure.world.title"), this.width() / 2, 17, 16777215);
      } else {
         String name = this.serverData.getName();
         int nameWidth = this.fontWidth(name);
         int nameColor = this.serverData.state == RealmsServer.State.CLOSED?10526880:8388479;
         int titleWidth = this.fontWidth(getLocalizedString("mco.configure.world.title"));
         this.drawCenteredString(getLocalizedString("mco.configure.world.title"), this.width() / 2 - nameWidth / 2 - 2, 17, 16777215);
         this.drawCenteredString(name, this.width() / 2 + titleWidth / 2 + 2, 17, nameColor);
         int statusX = this.width() / 2 + nameWidth / 2 + titleWidth / 2 + 5;
         this.drawServerStatus(statusX, 17, xm, ym);

         for(Entry<Integer, RealmsOptions> entry : this.serverData.slots.entrySet()) {
            if(((RealmsOptions)entry.getValue()).templateImage != null && ((RealmsOptions)entry.getValue()).templateId != -1L) {
               this.drawSlotFrame(this.frame(((Integer)entry.getKey()).intValue()), RealmsConstants.row(5) + 5, xm, ym, this.serverData.activeSlot == ((Integer)entry.getKey()).intValue() && !this.isMinigame(), ((RealmsOptions)entry.getValue()).getSlotName(((Integer)entry.getKey()).intValue()), ((Integer)entry.getKey()).intValue(), ((RealmsOptions)entry.getValue()).templateId, ((RealmsOptions)entry.getValue()).templateImage, ((RealmsOptions)entry.getValue()).empty);
            } else {
               this.drawSlotFrame(this.frame(((Integer)entry.getKey()).intValue()), RealmsConstants.row(5) + 5, xm, ym, this.serverData.activeSlot == ((Integer)entry.getKey()).intValue() && !this.isMinigame(), ((RealmsOptions)entry.getValue()).getSlotName(((Integer)entry.getKey()).intValue()), ((Integer)entry.getKey()).intValue(), -1L, (String)null, ((RealmsOptions)entry.getValue()).empty);
            }
         }

         this.drawSlotFrame(this.frame(4), RealmsConstants.row(5) + 5, xm, ym, this.isMinigame(), "Minigame", 4, -1L, (String)null, false);
         if(this.isMinigame()) {
            this.drawString(getLocalizedString("mco.configure.current.minigame") + ": " + this.serverData.getMinigameName(), this.left_x + this.default_button_width + 20 + this.default_button_offset * 2, RealmsConstants.row(13), 16777215);
         }

         if(this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, xm, ym);
         }

      }
   }

   private int frame(int i) {
      return this.left_x + (i - 1) * 98;
   }

   public void removed() {
      Keyboard.enableRepeatEvents(false);
   }

   public void buttonClicked(RealmsButton button) {
      if(button.active()) {
         if(!(button instanceof RealmsHideableButton) || ((RealmsHideableButton)button).getVisible()) {
            switch(button.id()) {
            case 0:
               this.backButtonClicked();
               break;
            case 1:
            default:
               return;
            case 2:
               Realms.setScreen(new RealmsPlayerScreen(this, this.serverData));
               break;
            case 3:
               Realms.setScreen(new RealmsSettingsScreen(this, this.serverData.clone()));
               break;
            case 4:
               Realms.setScreen(new RealmsSubscriptionInfoScreen(this, this.serverData.clone(), this.lastScreen));
               break;
            case 5:
               Realms.setScreen(new RealmsSlotOptionsScreen(this, ((RealmsOptions)this.serverData.slots.get(Integer.valueOf(this.serverData.activeSlot))).clone(), this.serverData.worldType, this.serverData.activeSlot));
               break;
            case 6:
               Realms.setScreen(new RealmsBackupScreen(this, this.serverData.clone()));
               break;
            case 7:
               Realms.setScreen(new RealmsResetWorldScreen(this, this.serverData.clone(), this.getNewScreen()));
               break;
            case 8:
               Realms.setScreen(new RealmsSelectWorldTemplateScreen(this, (WorldTemplate)null, true));
            }

         }
      }
   }

   public void keyPressed(char ch, int eventKey) {
      if(eventKey == 1) {
         this.backButtonClicked();
      }

   }

   private void backButtonClicked() {
      if(this.stateChanged) {
         ((RealmsMainScreen)this.lastScreen).removeSelection();
      }

      Realms.setScreen(this.lastScreen);
   }

   private void fetchServerData(final long worldId) {
      (new Thread() {
         public void run() {
            RealmsClient client = RealmsClient.createRealmsClient();

            try {
               RealmsConfigureWorldScreen.this.serverData = client.getOwnWorld(worldId);
               RealmsConfigureWorldScreen.this.disableButtons();
               if(RealmsConfigureWorldScreen.this.isMinigame()) {
                  RealmsConfigureWorldScreen.this.showMinigameButtons();
               } else {
                  RealmsConfigureWorldScreen.this.showRegularButtons();
               }
            } catch (RealmsServiceException var3) {
               RealmsConfigureWorldScreen.LOGGER.error("Couldn\'t get own world");
               Realms.setScreen(new RealmsGenericErrorScreen(var3.getMessage(), RealmsConfigureWorldScreen.this.lastScreen));
            } catch (IOException var4) {
               RealmsConfigureWorldScreen.LOGGER.error("Couldn\'t parse response getting own world");
            }

         }
      }).start();
   }

   private void disableButtons() {
      this.playersButton.active(!this.serverData.expired);
      this.settingsButton.active(!this.serverData.expired);
      this.subscriptionButton.active(true);
      this.switchMinigameButton.active(!this.serverData.expired);
      this.optionsButton.active(!this.serverData.expired);
      this.resetWorldButton.active(!this.serverData.expired);
   }

   public void confirmResult(boolean result, int id) {
      switch(id) {
      case 9:
         if(result) {
            this.switchSlot();
         } else {
            Realms.setScreen(this);
         }
         break;
      case 10:
         if(result) {
            RealmsResetWorldScreen resetWorldScreen = new RealmsResetWorldScreen(this, this.serverData, this.getNewScreen(), getLocalizedString("mco.configure.world.switch.slot"), getLocalizedString("mco.configure.world.switch.slot.subtitle"), 10526880, getLocalizedString("gui.cancel"));
            resetWorldScreen.setSlot(this.hoveredSlot);
            resetWorldScreen.setResetTitle(getLocalizedString("mco.create.world.reset.title"));
            Realms.setScreen(resetWorldScreen);
         } else {
            Realms.setScreen(this);
         }
         break;
      case 11:
         Realms.setScreen(this);
      }

   }

   public void mouseClicked(int x, int y, int buttonNum) {
      if(buttonNum == 0) {
         this.clicks += RealmsSharedConstants.TICKS_PER_SECOND / 3 + 1;
         if(this.hoveredSlot != -1) {
            if(this.hoveredSlot < 4) {
               String line2 = getLocalizedString("mco.configure.world.slot.switch.question.line1");
               String line3 = getLocalizedString("mco.configure.world.slot.switch.question.line2");
               if(((RealmsOptions)this.serverData.slots.get(Integer.valueOf(this.hoveredSlot))).empty) {
                  Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, line2, line3, true, 10));
               } else {
                  Realms.setScreen(new RealmsLongConfirmationScreen(this, RealmsLongConfirmationScreen.Type.Info, line2, line3, true, 9));
               }
            } else if(!this.isMinigame() && !this.serverData.expired) {
               Realms.setScreen(new RealmsSelectWorldTemplateScreen(this, (WorldTemplate)null, true, true));
            }
         } else if(this.clicks >= RealmsSharedConstants.TICKS_PER_SECOND / 2 && this.hoveredActiveSlot && (this.serverData.state == RealmsServer.State.OPEN || this.serverData.state == RealmsServer.State.CLOSED)) {
            if(this.serverData.state == RealmsServer.State.OPEN) {
               ((RealmsMainScreen)this.lastScreen).play(this.serverData);
            } else {
               this.openTheWorld(true, this);
            }
         }

         super.mouseClicked(x, y, buttonNum);
      }
   }

   protected void renderMousehoverTooltip(String msg, int x, int y) {
      if(msg != null) {
         int rx = x + 12;
         int ry = y - 12;
         int width = this.fontWidth(msg);
         if(rx + width + 3 > this.right_x) {
            rx = rx - width - 20;
         }

         this.fillGradient(rx - 3, ry - 3, rx + width + 3, ry + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(msg, rx, ry, 16777215);
      }
   }

   private void drawServerStatus(int x, int y, int xm, int ym) {
      if(this.serverData.expired) {
         this.drawExpired(x, y, xm, ym);
      } else if(this.serverData.state == RealmsServer.State.ADMIN_LOCK) {
         this.drawLocked(x, y, xm, ym, false);
      } else if(this.serverData.state == RealmsServer.State.CLOSED) {
         this.drawLocked(x, y, xm, ym, true);
      } else if(this.serverData.state == RealmsServer.State.OPEN) {
         if(this.serverData.daysLeft < 7) {
            this.drawExpiring(x, y, xm, ym, this.serverData.daysLeft);
         } else {
            this.drawOpen(x, y, xm, ym);
         }
      }

   }

   private void drawExpiring(int x, int y, int xm, int ym, int daysLeft) {
      if(this.animTick % 20 < 10) {
         RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         GL11.glPushMatrix();
         GL11.glScalef(0.5F, 0.5F, 0.5F);
         RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
         GL11.glPopMatrix();
      }

      if(xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9) {
         if(daysLeft == 0) {
            this.toolTip = getLocalizedString("mco.selectServer.expires.soon");
         } else if(daysLeft == 1) {
            this.toolTip = getLocalizedString("mco.selectServer.expires.day");
         } else {
            this.toolTip = getLocalizedString("mco.selectServer.expires.days", new Object[]{Integer.valueOf(daysLeft)});
         }
      }

   }

   private void drawOpen(int x, int y, int xm, int ym) {
      RealmsScreen.bind("realms:textures/gui/realms/on_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
      GL11.glPopMatrix();
      if(xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9) {
         this.toolTip = getLocalizedString("mco.selectServer.open");
      }

   }

   private void drawExpired(int x, int y, int xm, int ym) {
      bind("realms:textures/gui/realms/expired_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
      GL11.glPopMatrix();
      if(xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9) {
         this.toolTip = getLocalizedString("mco.selectServer.expired");
      }

   }

   private void drawLocked(int x, int y, int xm, int ym, boolean closed) {
      bind("realms:textures/gui/realms/off_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      GL11.glScalef(0.5F, 0.5F, 0.5F);
      RealmsScreen.blit(x * 2, y * 2, 0.0F, 0.0F, 15, 15, 15.0F, 15.0F);
      GL11.glPopMatrix();
      if(closed && xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9) {
         this.toolTip = getLocalizedString("mco.selectServer.closed");
      }

   }

   private boolean isMinigame() {
      return this.serverData != null && this.serverData.worldType.equals(RealmsServer.WorldType.MINIGAME);
   }

   private void drawSlotFrame(int x, int y, int xm, int ym, boolean active, String text, int i, long imageId, String image, boolean empty) {
      if(xm >= x && xm <= x + 80 && ym >= y && ym <= y + 80 && (!this.isMinigame() && this.serverData.activeSlot != i || this.isMinigame() && i != 4) && (i != 4 || !this.serverData.expired)) {
         this.hoveredSlot = i;
         this.toolTip = i == 4?getLocalizedString("mco.configure.world.slot.tooltip.minigame"):getLocalizedString("mco.configure.world.slot.tooltip");
      }

      if(xm >= x && xm <= x + 80 && ym >= y && ym <= y + 80 && (!this.isMinigame() && this.serverData.activeSlot == i || this.isMinigame() && i == 4) && !this.serverData.expired && (this.serverData.state == RealmsServer.State.OPEN || this.serverData.state == RealmsServer.State.CLOSED)) {
         this.hoveredActiveSlot = true;
         this.toolTip = getLocalizedString("mco.configure.world.slot.tooltip.active");
      }

      if(empty) {
         bind("realms:textures/gui/realms/empty_frame.png");
      } else if(image != null && imageId != -1L) {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(imageId), image);
      } else if(i == 1) {
         bind("textures/gui/title/background/panorama_0.png");
      } else if(i == 2) {
         bind("textures/gui/title/background/panorama_2.png");
      } else if(i == 3) {
         bind("textures/gui/title/background/panorama_3.png");
      } else {
         RealmsTextureManager.bindWorldTemplate(String.valueOf(this.serverData.minigameId), this.serverData.minigameImage);
      }

      if(!active) {
         GL11.glColor4f(0.56F, 0.56F, 0.56F, 1.0F);
      } else if(active) {
         float c = 0.9F + 0.1F * RealmsMth.cos((float)this.animTick * 0.2F);
         GL11.glColor4f(c, c, c, 1.0F);
      }

      RealmsScreen.blit(x + 3, y + 3, 0.0F, 0.0F, 74, 74, 74.0F, 74.0F);
      bind("realms:textures/gui/realms/slot_frame.png");
      if(this.hoveredSlot == i) {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      } else if(!active) {
         GL11.glColor4f(0.56F, 0.56F, 0.56F, 1.0F);
      } else {
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      }

      RealmsScreen.blit(x, y, 0.0F, 0.0F, 80, 80, 80.0F, 80.0F);
      this.drawCenteredString(text, x + 40, y + 66, 16777215);
   }

   private void hideRegularButtons() {
      this.optionsButton.setVisible(false);
      this.backupButton.setVisible(false);
      this.resetWorldButton.setVisible(false);
   }

   private void hideMinigameButtons() {
      this.switchMinigameButton.setVisible(false);
   }

   private void showRegularButtons() {
      this.optionsButton.setVisible(true);
      this.backupButton.setVisible(true);
      this.resetWorldButton.setVisible(true);
   }

   private void showMinigameButtons() {
      this.switchMinigameButton.setVisible(true);
   }

   public void saveSlotSettings() {
      RealmsClient client = RealmsClient.createRealmsClient();

      try {
         client.updateSlot(this.serverData.id, (RealmsOptions)this.serverData.slots.get(Integer.valueOf(this.serverData.activeSlot)));
      } catch (RealmsServiceException var3) {
         LOGGER.error("Couldn\'t save slot settings");
         Realms.setScreen(new RealmsGenericErrorScreen(var3, this));
         return;
      } catch (UnsupportedEncodingException var4) {
         LOGGER.error("Couldn\'t save slot settings");
      }

      Realms.setScreen(this);
   }

   public void saveSlotSettings(RealmsOptions options) {
      RealmsOptions oldOptions = (RealmsOptions)this.serverData.slots.get(Integer.valueOf(this.serverData.activeSlot));
      options.templateId = oldOptions.templateId;
      options.templateImage = oldOptions.templateImage;
      this.serverData.slots.put(Integer.valueOf(this.serverData.activeSlot), options);
      this.saveSlotSettings();
   }

   public void saveServerData() {
      RealmsClient client = RealmsClient.createRealmsClient();

      try {
         client.update(this.serverData.id, this.serverData.getName(), this.serverData.getDescription());
      } catch (RealmsServiceException var3) {
         LOGGER.error("Couldn\'t save settings");
         Realms.setScreen(new RealmsGenericErrorScreen(var3, this));
         return;
      } catch (UnsupportedEncodingException var4) {
         LOGGER.error("Couldn\'t save settings");
      }

      Realms.setScreen(this);
   }

   public void saveSettings(String name, String desc) {
      String description = desc != null && !desc.trim().equals("")?desc:null;
      this.serverData.setName(name);
      this.serverData.setDescription(description);
      this.saveServerData();
   }

   public void openTheWorld(boolean join, RealmsScreen screenInCaseOfCancel) {
      RealmsTasks.OpenServerTask openServerTask = new RealmsTasks.OpenServerTask(this.serverData, this, this.lastScreen, join);
      RealmsLongRunningMcoTaskScreen openWorldLongRunningTaskScreen = new RealmsLongRunningMcoTaskScreen(screenInCaseOfCancel, openServerTask);
      openWorldLongRunningTaskScreen.start();
      Realms.setScreen(openWorldLongRunningTaskScreen);
   }

   public void closeTheWorld(RealmsScreen screenInCaseOfCancel) {
      RealmsTasks.CloseServerTask closeServerTask = new RealmsTasks.CloseServerTask(this.serverData, this);
      RealmsLongRunningMcoTaskScreen closeWorldLongRunningTaskScreen = new RealmsLongRunningMcoTaskScreen(screenInCaseOfCancel, closeServerTask);
      closeWorldLongRunningTaskScreen.start();
      Realms.setScreen(closeWorldLongRunningTaskScreen);
   }

   public void stateChanged() {
      this.stateChanged = true;
   }

   void callback(WorldTemplate worldTemplate) {
      if(worldTemplate != null) {
         if(worldTemplate.minigame) {
            this.switchMinigame(worldTemplate);
         }

      }
   }

   private void switchSlot() {
      RealmsTasks.SwitchSlotTask switchSlotTask = new RealmsTasks.SwitchSlotTask(this.serverData.id, this.hoveredSlot, this.getNewScreen(), 11);
      RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, switchSlotTask);
      longRunningMcoTaskScreen.start();
      Realms.setScreen(longRunningMcoTaskScreen);
   }

   private void switchMinigame(WorldTemplate selectedWorldTemplate) {
      RealmsTasks.SwitchMinigameTask startMinigameTask = new RealmsTasks.SwitchMinigameTask(this.serverData.id, selectedWorldTemplate, this.getNewScreen());
      RealmsLongRunningMcoTaskScreen longRunningMcoTaskScreen = new RealmsLongRunningMcoTaskScreen(this.lastScreen, startMinigameTask);
      longRunningMcoTaskScreen.start();
      Realms.setScreen(longRunningMcoTaskScreen);
   }

   public RealmsConfigureWorldScreen getNewScreen() {
      return new RealmsConfigureWorldScreen(this.lastScreen, this.serverId);
   }
}
