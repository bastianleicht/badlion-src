package net.badlion.client.gui.mainmenu;

import net.badlion.client.Wrapper;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.BadlionGuiScreen;
import net.badlion.client.manager.AccountManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.lwjgl.opengl.GL11;

public class GuiAccountList {
   private int boxWidth;
   private int boxHeight;
   private int boxXOffset = 11;
   private int boxYOffset = 8;
   private float elementWidthProportion = 0.9F;
   private int addAccountButtonTopPadding = 6;
   private int addAccountButtonBottomPadding = 6;
   private int addAccountButtonHeight = 14;
   private int accountRowHeight = 20;
   private final GuiMainMenu guiMainMenu;

   public GuiAccountList(GuiMainMenu guiMainMenu) {
      this.guiMainMenu = guiMainMenu;
   }

   public void render(int mouseX, int mouseY, int topBarHeight) {
      int i = this.guiMainMenu.getWidth();
      Minecraft minecraft = this.guiMainMenu.getMinecraft();
      int j = 0;
      int k = Wrapper.getInstance().getModProfileManager().getAccountManager().getSessionMap().size();

      for(String s : Wrapper.getInstance().getModProfileManager().getAccountManager().getSessionMap().keySet()) {
         j = Math.max(Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(s, 16, BadlionFontRenderer.FontType.TITLE), j);
      }

      this.boxWidth = Math.max(j + 10 + 10 + 16 + 10, 100);
      this.boxHeight = this.accountRowHeight * k + this.addAccountButtonHeight + this.addAccountButtonTopPadding + this.addAccountButtonBottomPadding;
      int j2 = i - this.boxWidth - this.boxXOffset;
      int k2 = topBarHeight + this.boxYOffset;
      BadlionGuiScreen.drawOutlinedBox(j2, k2, this.boxWidth, this.boxHeight);
      GL11.glEnable(3042);
      GL11.glEnable(3553);
      int l = (int)(this.elementWidthProportion * (float)this.boxWidth);
      int i1 = (int)((float)(this.boxWidth - l) / 2.0F);
      int j1 = j2 + i1;
      BadlionGuiScreen.drawButton(j1, k2 + this.accountRowHeight * k + this.addAccountButtonTopPadding, l, this.addAccountButtonHeight, this.isMouseOverAddAccountButton(mouseX, mouseY, topBarHeight), "Add Account", 10);
      GL11.glDisable(3553);
      int k1 = k2;

      for(int l1 = 0; l1 < Wrapper.getInstance().getModProfileManager().getAccountManager().getSortedUsernames().size(); ++l1) {
         String s1 = (String)Wrapper.getInstance().getModProfileManager().getAccountManager().getSortedUsernames().get(l1);
         Session session = Wrapper.getInstance().getModProfileManager().getAccountManager().getSession(s1);
         boolean flag = minecraft.getSession().getUsername().equals(s1);
         if(minecraft.getSession().getUsername().equals(s1)) {
            BadlionGuiScreen.drawRoundedRect(j1, k1 + 4, j1 + l, k1 + 20, 2.0F, -12545617);
         }

         if(!flag) {
            int i2 = this.isMouseOverAccount(mouseX, mouseY, topBarHeight);
            if(i2 != -1 && i2 == l1) {
               BadlionGuiScreen.drawRoundedRect(j1, k1 + 4, j1 + l, k1 + 20, 2.0F, 1620679065);
            }
         }

         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         int l2 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(s1, 14, BadlionFontRenderer.FontType.TITLE);
         Wrapper.getInstance().getBadlionFontRenderer().drawString(j1 + 16 + 5, k1 + 4, s1, 14, BadlionFontRenderer.FontType.TITLE, true);
         ResourceLocation resourcelocation = AccountManager.locationStevePng;
         if(Wrapper.getInstance().getModProfileManager().getAccountManager().getCachedSkinResources().containsKey(session.getPlayerUUID())) {
            minecraft.getTextureManager().bindTexture((ResourceLocation)Wrapper.getInstance().getModProfileManager().getAccountManager().getCachedSkinResources().get(session.getPlayerUUID()));
         } else {
            minecraft.getTextureManager().bindTexture(resourcelocation);
         }

         GL11.glColor3d(1.0D, 1.0D, 1.0D);
         Gui.drawScaledCustomSizeModalRect(j1, k1 + 4, 16.0F, 16.0F, 16, 16, 16, 16, 128.0F, 128.0F);
         Gui.drawScaledCustomSizeModalRect(j1, k1 + 4, 80.0F, 16.0F, 16, 16, 16, 16, 128.0F, 128.0F);
         GL11.glDisable(3553);
         k1 += this.accountRowHeight;
      }

   }

   public int isMouseOverAccount(int mouseX, int mouseY, int topBarHeight) {
      int i = this.guiMainMenu.getWidth() - this.boxWidth - this.boxXOffset;
      int j = topBarHeight + this.boxYOffset;
      int k = i + 5;
      int l = (int)(this.elementWidthProportion * (float)this.boxWidth);
      if(mouseX >= k && mouseX < k + l && mouseY >= j + 4 && mouseY < j + 20 * Wrapper.getInstance().getModProfileManager().getAccountManager().getSessionMap().size()) {
         int i1 = mouseY - j;
         return (int)Math.ceil((double)((float)i1 / (float)this.accountRowHeight)) - 1;
      } else {
         return -1;
      }
   }

   public boolean isMouseOverBox(int mouseX, int mouseY, int topBarHeight) {
      int i = this.guiMainMenu.getWidth() - this.boxWidth - this.boxXOffset;
      int j = topBarHeight + this.boxYOffset;
      return mouseX >= i && mouseX < i + this.boxWidth && mouseY >= j && mouseY < j + this.boxHeight;
   }

   public boolean isMouseOverAddAccountButton(int mouseX, int mouseY, int topBarHeight) {
      int i = this.guiMainMenu.getWidth() - this.boxWidth - this.boxXOffset;
      int j = topBarHeight + this.boxYOffset;
      int k = (int)(this.elementWidthProportion * (float)this.boxWidth);
      int l = (int)((float)(this.boxWidth - k) / 2.0F);
      int i1 = i + l;
      int j1 = Wrapper.getInstance().getModProfileManager().getAccountManager().getSessionMap().size();
      int k1 = j + this.accountRowHeight * j1 + this.addAccountButtonTopPadding;
      return mouseX >= i1 && mouseX < i1 + k && mouseY >= k1 && mouseY < k1 + this.addAccountButtonHeight;
   }
}
