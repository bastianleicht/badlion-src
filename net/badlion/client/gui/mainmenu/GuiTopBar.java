package net.badlion.client.gui.mainmenu;

import net.badlion.client.Wrapper;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.manager.AccountManager;
import net.badlion.client.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiTopBar {
   private static final ResourceLocation profilesIcon = new ResourceLocation("textures/menu/home/profiles.png");
   private int modProfilesBoxPadding = 5;
   private int modProfilesIconWidth = 14;
   private int modProfilesIconHeight = 16;
   private int usernameBoxPadding = 5;

   public void render(int mouseX, int mouseY, int topBarHeight, int width, boolean isUserBoxOpen, boolean isModProfilesBoxOpen) {
      GL11.glDisable(3553);
      ColorUtil.bindHexColorRGBA(-14144717);
      GL11.glBegin(7);
      GL11.glVertex2f(0.0F, 0.0F);
      GL11.glVertex2f(0.0F, (float)topBarHeight);
      GL11.glVertex2f((float)width, (float)topBarHeight);
      GL11.glVertex2f((float)width, 0.0F);
      GL11.glEnd();
      this.renderModProfileBox(mouseX, mouseY, 10, 0, topBarHeight, isModProfilesBoxOpen);
      int i = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(Minecraft.getMinecraft().getSession().getUsername(), 12, BadlionFontRenderer.FontType.TITLE);
      int j = this.usernameBoxPadding + 16 + this.usernameBoxPadding + i + this.usernameBoxPadding + 10;
      boolean flag = isUserBoxOpen || this.isMouseOverUsernameBox(mouseX, mouseY, topBarHeight, width);
      if(flag) {
         ColorUtil.bindHexColorRGBA(-12545617);
         GL11.glBegin(7);
         GL11.glVertex2f((float)(width - j - 10), 0.0F);
         GL11.glVertex2f((float)(width - j - 10), (float)topBarHeight);
         GL11.glVertex2f((float)(width - 10), (float)topBarHeight);
         GL11.glVertex2f((float)(width - 10), 0.0F);
         GL11.glEnd();
      }

      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glBegin(4);
      GL11.glVertex2f((float)(width - 15), (float)(topBarHeight / 2));
      GL11.glVertex2f((float)(width - 21), (float)(topBarHeight / 2));
      GL11.glVertex2f((float)(width - 18), (float)(topBarHeight / 2 + 3));
      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Wrapper.getInstance().getBadlionFontRenderer().drawString(width - 10 - 15 - i, topBarHeight - topBarHeight / 2 - 6, Minecraft.getMinecraft().getSession().getUsername(), 12, BadlionFontRenderer.FontType.TITLE, true);
      ResourceLocation resourcelocation = AccountManager.locationStevePng;
      if(Wrapper.getInstance().getModProfileManager().getAccountManager().getCachedSkinResources().containsKey(Minecraft.getMinecraft().getSession().getPlayerUUID())) {
         Minecraft.getMinecraft().getTextureManager().bindTexture((ResourceLocation)Wrapper.getInstance().getModProfileManager().getAccountManager().getCachedSkinResources().get(Minecraft.getMinecraft().getSession().getPlayerUUID()));
      } else {
         Minecraft.getMinecraft().getTextureManager().bindTexture(resourcelocation);
      }

      GL11.glColor3d(1.0D, 1.0D, 1.0D);
      int k = width - j - 5;
      int l = topBarHeight / 2 - 8;
      Gui.drawScaledCustomSizeModalRect(k, l, 16.0F, 16.0F, 16, 16, 16, 16, 128.0F, 128.0F);
      Gui.drawScaledCustomSizeModalRect(k, l, 80.0F, 16.0F, 16, 16, 16, 16, 128.0F, 128.0F);
      GL11.glDisable(2929);
      GL11.glEnable(3042);
      OpenGlHelper.glBlendFunc(770, 771, 0, 1);
      GL11.glDisable(3008);
      GL11.glShadeModel(7425);
      GL11.glDisable(3553);
      GL11.glShadeModel(7424);
      GL11.glEnable(3008);
      GL11.glDisable(3042);
      GL11.glEnable(3553);
      int i1 = 20;
      int j1 = 20;
      GL11.glEnable(3042);
      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(GuiMainMenu.badlionLogo);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Gui.drawModalRectWithCustomSizedTexture(width / 2 - i1 / 2, 3, 0.0F, 0.0F, i1, j1, (float)i1, (float)j1);
      GL11.glDisable(3553);
   }

   public void renderModProfileBox(int mouseX, int mouseY, int xOffset, int yOffset, int height, boolean isModProfilesBoxOpen) {
      int i = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth("Mod Profiles", 12, BadlionFontRenderer.FontType.TITLE);
      int j = this.modProfilesBoxPadding + this.modProfilesIconWidth + this.modProfilesBoxPadding + i + this.modProfilesBoxPadding + 6 + this.modProfilesBoxPadding;
      boolean flag = isModProfilesBoxOpen || this.isMouseOverModProfilesBox(mouseX, mouseY, xOffset, yOffset, height);
      if(flag) {
         ColorUtil.bindHexColorRGBA(-12545617);
         GL11.glDisable(3553);
         GL11.glBegin(7);
         GL11.glVertex2f((float)xOffset, (float)yOffset);
         GL11.glVertex2f((float)xOffset, (float)(yOffset + height));
         GL11.glVertex2f((float)(xOffset + j), (float)(yOffset + height));
         GL11.glVertex2f((float)(xOffset + j), (float)yOffset);
         GL11.glEnd();
      }

      GL11.glEnable(3553);
      ColorUtil.bindHexColorRGBA(-1);
      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(profilesIcon);
      Gui.drawScaledCustomSizeModalRect(xOffset + this.modProfilesBoxPadding, yOffset + (int)((float)(height - this.modProfilesIconHeight) / 2.0F), 0.0F, 0.0F, 64, 64, this.modProfilesIconWidth, this.modProfilesIconHeight, 64.0F, 64.0F);
      GL11.glDisable(3553);
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glBegin(4);
      GL11.glVertex2f((float)(xOffset + this.modProfilesBoxPadding + this.modProfilesIconWidth + this.modProfilesBoxPadding + i + this.modProfilesBoxPadding + 6), (float)(yOffset + height / 2));
      GL11.glVertex2f((float)(xOffset + this.modProfilesBoxPadding + this.modProfilesIconWidth + this.modProfilesBoxPadding + i + this.modProfilesBoxPadding), (float)(yOffset + height / 2));
      GL11.glVertex2f((float)(xOffset + this.modProfilesBoxPadding + this.modProfilesIconWidth + this.modProfilesBoxPadding + i + this.modProfilesBoxPadding + 3), (float)(yOffset + height / 2 + 3));
      GL11.glEnd();
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      Wrapper.getInstance().getBadlionFontRenderer().drawString(xOffset + this.modProfilesBoxPadding + this.modProfilesIconWidth + this.modProfilesBoxPadding, yOffset + height - height / 2 - 6, "Mod Profiles", 12, BadlionFontRenderer.FontType.TITLE, true);
   }

   public boolean isMouseOverModProfilesBox(int mouseX, int mouseY, int xOffset, int yOffset, int height) {
      int i = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth("Mod Profiles", 12, BadlionFontRenderer.FontType.TITLE);
      int j = this.modProfilesBoxPadding + this.modProfilesIconWidth + this.modProfilesBoxPadding + i + this.modProfilesBoxPadding + 6 + this.modProfilesBoxPadding;
      return mouseX >= xOffset && mouseX < j + xOffset && mouseY >= yOffset && mouseY < yOffset + height;
   }

   public boolean isMouseOverUsernameBox(int mouseX, int mouseY, int topBarHeight, int width) {
      int i = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(Minecraft.getMinecraft().getSession().getUsername(), 12, BadlionFontRenderer.FontType.TITLE);
      int j = this.usernameBoxPadding + 16 + this.usernameBoxPadding + i + this.usernameBoxPadding + 10;
      return mouseX >= width - j - 10 && mouseX < width - 10 && mouseY >= 0 && mouseY < topBarHeight;
   }
}
