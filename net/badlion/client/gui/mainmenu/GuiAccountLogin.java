package net.badlion.client.gui.mainmenu;

import net.badlion.client.Wrapper;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.BadlionGuiScreen;
import net.badlion.client.gui.InputField;
import net.minecraft.client.gui.GuiMainMenu;
import org.lwjgl.opengl.GL11;

public class GuiAccountLogin {
   private int boxWidth = 172;
   private int boxHeight = 122;
   private int boxXOffset = 11;
   private int boxYOffset = 8;
   private int backButtonWidth = 24;
   private int backButtonHeight = 10;
   private int backButtonYOffset = 6;
   private float elementWidthProportion = 0.9F;
   private int addAccountButtonYOffset = 24;
   private int addAccountButtonHeight = 16;
   private int inputFieldYPadding = 30;
   private int inputFieldYOffset = 40;
   private int inputFieldHeight = 18;
   private int inputFieldLabelYOffset = 13;
   private final GuiMainMenu guiMainMenu;

   public GuiAccountLogin(GuiMainMenu guiMainMenu) {
      this.guiMainMenu = guiMainMenu;
   }

   public void render(int mouseX, int mouseY, int topBarHeight) {
      int i = this.guiMainMenu.getWidth();
      int j = i - this.boxWidth - this.boxXOffset;
      int k = topBarHeight + this.boxYOffset;
      BadlionGuiScreen.drawOutlinedBox(j, k, this.boxWidth, this.boxHeight);
      GL11.glEnable(3553);
      GL11.glEnable(3042);
      int l = (int)(this.elementWidthProportion * (float)this.boxWidth);
      int i1 = (int)((float)(this.boxWidth - l) / 2.0F);
      int j1 = j + i1;
      BadlionGuiScreen.drawButton(j + this.boxWidth - this.backButtonWidth - i1, k + this.backButtonYOffset, this.backButtonWidth, this.backButtonHeight, this.isMouseOverBackButton(mouseX, mouseY, topBarHeight), "back", 10);
      BadlionGuiScreen.drawButton(j1, k + this.boxHeight - this.addAccountButtonYOffset, l, this.addAccountButtonHeight, this.isMouseOverAddAccountButton(mouseX, mouseY, topBarHeight), "Add Account", 12);
      int k1 = k + this.inputFieldYPadding;
      if(this.guiMainMenu.getEmailField() != null) {
         this.guiMainMenu.getEmailField().update(mouseX, mouseY);
         this.guiMainMenu.getEmailField().setPosition(j1, k1);
         this.guiMainMenu.getEmailField().render();
      } else {
         this.guiMainMenu.setEmailField(new InputField(j1, k1, l, this.inputFieldHeight, false, InputField.InputFlavor.EMAIL));
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Wrapper.getInstance().getBadlionFontRenderer().drawString(j1, k1 - this.inputFieldLabelYOffset, "Email/Username", 10, BadlionFontRenderer.FontType.TITLE, true);
      k1 = k1 + this.inputFieldYOffset;
      if(this.guiMainMenu.getPasswordField() != null) {
         this.guiMainMenu.getPasswordField().update(mouseX, mouseY);
         this.guiMainMenu.getPasswordField().setPosition(j1, k1);
         this.guiMainMenu.getPasswordField().render();
      } else {
         this.guiMainMenu.setPasswordField(new InputField(j1, k1, l, this.inputFieldHeight, true, InputField.InputFlavor.PASSWORD));
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Wrapper.getInstance().getBadlionFontRenderer().drawString(j1, k1 - this.inputFieldLabelYOffset, "Password", 10, BadlionFontRenderer.FontType.TITLE, true);
   }

   public boolean isMouseOverBox(int mouseX, int mouseY, int topBarHeight) {
      int i = this.guiMainMenu.getWidth() - this.boxWidth - this.boxXOffset;
      int j = topBarHeight + this.boxYOffset;
      return mouseX >= i && mouseX < i + this.boxWidth && mouseY >= j && mouseY < j + this.boxHeight;
   }

   public boolean isMouseOverBackButton(int mouseX, int mouseY, int topBarHeight) {
      int i = this.guiMainMenu.getWidth() - this.boxWidth - this.boxXOffset;
      int j = topBarHeight + this.boxYOffset;
      int k = (int)(this.elementWidthProportion * (float)this.boxWidth);
      int l = (int)((float)(this.boxWidth - k) / 2.0F);
      int i1 = i + this.boxWidth - this.backButtonWidth - l;
      int j1 = j + this.backButtonYOffset;
      return mouseX >= i1 && mouseX < i1 + this.backButtonWidth && mouseY >= j1 && mouseY < j1 + this.backButtonHeight;
   }

   public boolean isMouseOverAddAccountButton(int mouseX, int mouseY, int topBarHeight) {
      int i = this.guiMainMenu.getWidth() - this.boxWidth - this.boxXOffset;
      int j = topBarHeight + this.boxYOffset;
      int k = (int)(this.elementWidthProportion * (float)this.boxWidth);
      int l = (int)((float)(this.boxWidth - k) / 2.0F);
      int i1 = i + l;
      int j1 = j + this.boxHeight - this.addAccountButtonYOffset;
      return mouseX >= i1 && mouseX < i1 + k && mouseY >= j1 && mouseY < j1 + this.addAccountButtonHeight;
   }
}
