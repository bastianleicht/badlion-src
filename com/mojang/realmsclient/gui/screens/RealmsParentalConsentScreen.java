package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.util.RealmsUtil;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsScreen;
import org.lwjgl.opengl.GL11;

public class RealmsParentalConsentScreen extends RealmsScreen {
   private final RealmsScreen nextScreen;
   private static final int BUTTON_BACK_ID = 0;
   private static final int BUTTON_OK_ID = 1;
   private final String line1 = "Recently, Mojang was acquired by Microsoft. Microsoft implements";
   private final String line2 = "certain procedures to help protect children and their privacy,";
   private final String line3 = "including complying with the Children’s Online Privacy Protection Act (COPPA)";
   private final String line4 = "You may need to obtain parental consent before accessing your Realms account.";
   private boolean onLink = false;

   public RealmsParentalConsentScreen(RealmsScreen nextScreen) {
      this.nextScreen = nextScreen;
   }

   public void init() {
      this.buttonsClear();
      this.buttonsAdd(newButton(1, this.width() / 2 - 100, RealmsConstants.row(11), 200, 20, "Go to accounts page"));
      this.buttonsAdd(newButton(0, this.width() / 2 - 100, RealmsConstants.row(13), 200, 20, "Back"));
   }

   public void tick() {
      super.tick();
   }

   public void buttonClicked(RealmsButton button) {
      switch(button.id()) {
      case 0:
         Realms.setScreen(this.nextScreen);
         break;
      case 1:
         RealmsUtil.browseTo("https://accounts.mojang.com/me/verify/" + Realms.getUUID());
         break;
      default:
         return;
      }

   }

   public void mouseClicked(int x, int y, int buttonNum) {
      if(this.onLink) {
         RealmsUtil.browseTo("http://www.ftc.gov/enforcement/rules/rulemaking-regulatory-reform-proceedings/childrens-online-privacy-protection-rule");
      }

   }

   public void render(int xm, int ym, float a) {
      this.renderBackground();
      this.drawCenteredString("Recently, Mojang was acquired by Microsoft. Microsoft implements", this.width() / 2, 30, 16777215);
      this.drawCenteredString("certain procedures to help protect children and their privacy,", this.width() / 2, 45, 16777215);
      this.drawCenteredString("including complying with the Children’s Online Privacy Protection Act (COPPA)", this.width() / 2, 60, 16777215);
      this.drawCenteredString("You may need to obtain parental consent before accessing your Realms account.", this.width() / 2, 120, 16777215);
      this.renderLink(xm, ym);
      super.render(xm, ym, a);
   }

   private void renderLink(int xm, int ym) {
      String text = getLocalizedString("Read more about COPPA");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      int textWidth = this.fontWidth(text);
      int leftPadding = this.width() / 2 - textWidth / 2;
      int topPadding = 75;
      int x2 = leftPadding + textWidth + 1;
      int y2 = topPadding + this.fontLineHeight();
      GL11.glTranslatef((float)leftPadding, (float)topPadding, 0.0F);
      if(leftPadding <= xm && xm <= x2 && topPadding <= ym && ym <= y2) {
         this.onLink = true;
         this.drawString(text, 0, 0, 7107012);
      } else {
         this.onLink = false;
         this.drawString(text, 0, 0, 3368635);
      }

      GL11.glPopMatrix();
   }
}
