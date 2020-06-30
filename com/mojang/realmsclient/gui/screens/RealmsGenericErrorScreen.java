package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.exception.RealmsServiceException;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;

public class RealmsGenericErrorScreen extends RealmsScreen {
   private final RealmsScreen nextScreen;
   private static final int OK_BUTTON_ID = 10;
   private String line1;
   private String line2;

   public RealmsGenericErrorScreen(RealmsServiceException realmsServiceException, RealmsScreen nextScreen) {
      this.nextScreen = nextScreen;
      this.errorMessage(realmsServiceException);
   }

   public RealmsGenericErrorScreen(String message, RealmsScreen nextScreen) {
      this.nextScreen = nextScreen;
      this.errorMessage(message);
   }

   public RealmsGenericErrorScreen(String title, String message, RealmsScreen nextScreen) {
      this.nextScreen = nextScreen;
      this.errorMessage(title, message);
   }

   private void errorMessage(RealmsServiceException realmsServiceException) {
      if(realmsServiceException.errorCode != -1) {
         this.line1 = "Realms (" + realmsServiceException.errorCode + "):";
         String translationKey = "mco.errorMessage." + realmsServiceException.errorCode;
         String translated = getLocalizedString(translationKey);
         this.line2 = translated.equals(translationKey)?realmsServiceException.errorMsg:translated;
      } else {
         this.line1 = "An error occurred (" + realmsServiceException.httpResultCode + "):";
         this.line2 = realmsServiceException.httpResponseContent;
      }

   }

   private void errorMessage(String message) {
      this.line1 = "An error occurred: ";
      this.line2 = message;
   }

   private void errorMessage(String title, String message) {
      this.line1 = title;
      this.line2 = message;
   }

   public void init() {
      this.buttonsClear();
      this.buttonsAdd(newButton(10, this.width() / 2 - 100, this.height() - 52, 200, 20, "Ok"));
   }

   public void tick() {
      super.tick();
   }

   public void buttonClicked(RealmsButton button) {
      if(button.id() == 10) {
         Realms.setScreen(this.nextScreen);
      }

   }

   public void render(int xm, int ym, float a) {
      this.renderBackground();
      this.drawCenteredString(this.line1, this.width() / 2, 80, 16777215);
      this.drawCenteredString(this.line2, this.width() / 2, 100, 16711680);
      super.render(xm, ym, a);
   }
}
