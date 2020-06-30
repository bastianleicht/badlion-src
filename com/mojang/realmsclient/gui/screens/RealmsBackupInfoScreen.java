package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.dto.Backup;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.realms.Tezzelator;
import org.lwjgl.input.Keyboard;

public class RealmsBackupInfoScreen extends RealmsScreen {
   private final RealmsScreen lastScreen;
   private final int BUTTON_BACK_ID = 0;
   private final Backup backup;
   private List keys = new ArrayList();
   private RealmsBackupInfoScreen.BackupInfoList backupInfoList;
   String[] difficulties = new String[]{getLocalizedString("options.difficulty.peaceful"), getLocalizedString("options.difficulty.easy"), getLocalizedString("options.difficulty.normal"), getLocalizedString("options.difficulty.hard")};
   String[] gameModes = new String[]{getLocalizedString("selectWorld.gameMode.survival"), getLocalizedString("selectWorld.gameMode.creative"), getLocalizedString("selectWorld.gameMode.adventure")};

   public RealmsBackupInfoScreen(RealmsScreen lastScreen, Backup backup) {
      this.lastScreen = lastScreen;
      this.backup = backup;
      if(backup.changeList != null) {
         for(Entry<String, String> entry : backup.changeList.entrySet()) {
            this.keys.add(entry.getKey());
         }
      }

   }

   public void mouseEvent() {
      super.mouseEvent();
      this.backupInfoList.mouseEvent();
   }

   public void tick() {
   }

   public void init() {
      Keyboard.enableRepeatEvents(true);
      this.buttonsAdd(newButton(0, this.width() / 2 - 100, this.height() / 4 + 120 + 24, getLocalizedString("gui.back")));
      this.backupInfoList = new RealmsBackupInfoScreen.BackupInfoList();
   }

   public void removed() {
      Keyboard.enableRepeatEvents(false);
   }

   public void buttonClicked(RealmsButton button) {
      if(button.active()) {
         if(button.id() == 0) {
            Realms.setScreen(this.lastScreen);
         }

      }
   }

   public void keyPressed(char ch, int eventKey) {
      if(eventKey == 1) {
         Realms.setScreen(this.lastScreen);
      }

   }

   public void render(int xm, int ym, float a) {
      this.renderBackground();
      this.drawCenteredString("Changes from last backup", this.width() / 2, 10, 16777215);
      this.backupInfoList.render(xm, ym, a);
      super.render(xm, ym, a);
   }

   private String checkForSpecificMetadata(String key, String value) {
      String k = key.toLowerCase();
      return k.contains("game") && k.contains("mode")?this.gameModeMetadata(value):(k.contains("game") && k.contains("difficulty")?this.gameDifficultyMetadata(value):value);
   }

   private String gameDifficultyMetadata(String value) {
      try {
         return this.difficulties[Integer.parseInt(value)];
      } catch (Exception var3) {
         return "UNKNOWN";
      }
   }

   private String gameModeMetadata(String value) {
      try {
         return this.gameModes[Integer.parseInt(value)];
      } catch (Exception var3) {
         return "UNKNOWN";
      }
   }

   private class BackupInfoList extends RealmsSimpleScrolledSelectionList {
      public BackupInfoList() {
         super(RealmsBackupInfoScreen.this.width(), RealmsBackupInfoScreen.this.height(), 32, RealmsBackupInfoScreen.this.height() - 64, 36);
      }

      public int getItemCount() {
         return RealmsBackupInfoScreen.this.backup.changeList.size();
      }

      public void selectItem(int item, boolean doubleClick, int xMouse, int yMouse) {
      }

      public boolean isSelectedItem(int item) {
         return false;
      }

      public int getMaxPosition() {
         return this.getItemCount() * 36;
      }

      public void renderBackground() {
      }

      protected void renderItem(int i, int x, int y, int h, Tezzelator t, int mouseX, int mouseY) {
         String key = (String)RealmsBackupInfoScreen.this.keys.get(i);
         RealmsBackupInfoScreen.this.drawString(key, this.width() / 2 - 40, y, 10526880);
         String metadataValue = (String)RealmsBackupInfoScreen.this.backup.changeList.get(key);
         RealmsBackupInfoScreen.this.drawString(RealmsBackupInfoScreen.this.checkForSpecificMetadata(key, metadataValue), this.width() / 2 - 40, y + 12, 16777215);
      }
   }
}
