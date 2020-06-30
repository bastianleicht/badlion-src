package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.ErrorCallback;
import com.mojang.realmsclient.gui.LongRunningTask;
import com.mojang.realmsclient.gui.RealmsConstants;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

public class RealmsLongRunningMcoTaskScreen extends RealmsScreen implements ErrorCallback {
   private final int BUTTON_CANCEL_ID = 666;
   private final int BUTTON_BACK_ID = 667;
   private final RealmsScreen lastScreen;
   private final LongRunningTask taskThread;
   private volatile String title = "";
   private volatile boolean error;
   private volatile String errorMessage;
   private volatile boolean aborted;
   private int animTicks;
   private LongRunningTask task;
   private int buttonLength = 212;
   public static final String[] symbols = new String[]{"▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃", "_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄", "_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅", "_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆", "_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇", "_ _ _ _ _ ▃ ▄ ▅ ▆ ▇ █", "_ _ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇", "_ _ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆", "_ _ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅", "_ ▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄", "▃ ▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃", "▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _", "▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _", "▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _", "▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _", "█ ▇ ▆ ▅ ▄ ▃ _ _ _ _ _", "▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _ _", "▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _ _", "▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _ _", "▄ ▅ ▆ ▇ █ ▇ ▆ ▅ ▄ ▃ _"};

   public RealmsLongRunningMcoTaskScreen(RealmsScreen lastScreen, LongRunningTask task) {
      this.lastScreen = lastScreen;
      this.task = task;
      task.setScreen(this);
      this.taskThread = task;
   }

   public void start() {
      (new Thread(this.taskThread, "Realms-long-running-task")).start();
   }

   public void tick() {
      super.tick();
      ++this.animTicks;
      this.task.tick();
   }

   public void keyPressed(char eventCharacter, int eventKey) {
      if(eventKey == 1) {
         this.cancelOrBackButtonClicked();
      }

   }

   public void init() {
      this.task.init();
      this.buttonsAdd(newButton(666, this.width() / 2 - this.buttonLength / 2, RealmsConstants.row(12), this.buttonLength, 20, getLocalizedString("gui.cancel")));
   }

   public void buttonClicked(RealmsButton button) {
      if(button.id() == 666 || button.id() == 667) {
         this.cancelOrBackButtonClicked();
      }

      this.task.buttonClicked(button);
   }

   private void cancelOrBackButtonClicked() {
      this.aborted = true;
      this.task.abortTask();
      Realms.setScreen(this.lastScreen);
   }

   public void render(int xm, int ym, float a) {
      this.renderBackground();
      this.drawCenteredString(this.title, this.width() / 2, RealmsConstants.row(3), 16777215);
      if(!this.error) {
         this.drawCenteredString(symbols[this.animTicks % symbols.length], this.width() / 2, RealmsConstants.row(8), 8421504);
      }

      if(this.error) {
         this.drawCenteredString(this.errorMessage, this.width() / 2, RealmsConstants.row(8), 16711680);
      }

      super.render(xm, ym, a);
   }

   public void error(String errorMessage) {
      this.error = true;
      this.errorMessage = errorMessage;
      this.buttonsClear();
      this.buttonsAdd(newButton(667, this.width() / 2 - this.buttonLength / 2, this.height() / 4 + 120 + 12, getLocalizedString("gui.back")));
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public boolean aborted() {
      return this.aborted;
   }
}
