package net.badlion.client.gui.mainmenu;

import java.util.ArrayList;
import java.util.List;
import net.badlion.client.Wrapper;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.BadlionGuiScreen;
import net.badlion.client.gui.InputField;
import net.badlion.client.mods.ModProfile;
import net.badlion.client.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class GuiModProfiles {
   private static final ResourceLocation infoButton = new ResourceLocation("textures/menu/home/info.png");
   private static final ResourceLocation folderButton = new ResourceLocation("textures/menu/home/folder.png");
   private static final ResourceLocation backButton = new ResourceLocation("textures/menu/home/white-back-button.svg_large.png");
   private static final ResourceLocation forwardButton = new ResourceLocation("textures/menu/home/white-forward-button.svg_large.png");
   private static final ResourceLocation textEditButton = new ResourceLocation("textures/menu/home/edit.png");
   private boolean boxOpen = false;
   private boolean confirmationBoxOpen = false;
   private boolean infoBoxOpen = false;
   private InputField modProfileEditInputField = null;
   private ModProfile modProfileBeingEdited = null;
   private int currentPage = 1;
   private int boxWidth = 172;
   private int boxHeight = -1;
   private int boxXOffset;
   private int boxYOffset;
   private float elementWidthProportion = 0.9F;
   private String titleText = "Your Profiles";
   private int titleTextFontHeight = 13;
   private int titleTextTopPadding = 5;
   private int titleTextBottomPadding = 5;
   private int infoButtonWidth = 15;
   private int infoButtonHeight = 15;
   private int folderButtonWidth = 15;
   private int folderButtonHeight = 15;
   private int backForwardButtonWidth = 15;
   private int backForwardButtonHeight = 15;
   private int backForwardButtonHorizontalPadding = 10;
   private String pageTextPrefix = "Page ";
   private int pageTextFontHeight = 10;
   private int numberOfProfilesPerPage = 5;
   private int profileBoxTopPadding = 8;
   private int profileBoxBottomPadding = 8;
   private int profileEntryHeight = 20;
   private int profileTextFontHeight = 12;
   private int profileTextHorizontalPadding = 20;
   private int profileTextEditButtonWidth = 10;
   private int profileTextEditButtonHeight = 10;
   private int bottomButtonHeight = 10;
   private int bottomButtonFontHeight = 8;
   private int bottomButtonBottomPadding = 7;
   private int bottomButtonHorizontalPadding = 5;
   private List bottomButtons = new ArrayList();
   private int confirmationBoxWidth = 102;
   private int confirmationBoxHeight = -1;
   private String confirmationText = "Are you sure?";
   private int confirmationTextFontHeight = 10;
   private int confirmationTextTopPadding = 15;
   private int confirmationTextBottomPadding = 10;
   private float confirmationBoxElementWidthProportion = 0.6F;
   private int confirmationBoxButtonFontHeight = 8;
   private int confirmationBoxButtonHeight = 10;
   private int confirmationBoxButtonBottomPadding = 10;
   private int confirmationBoxButtonHorizontalPadding = 6;
   private List confirmationBoxButtons = new ArrayList();
   private int infoTextFontHeight = 12;
   private int infoTextTopPadding = 6;
   private int infoTextVerticalPadding = 4;
   private int infoTextInternalVerticalPadding = 3;
   private List informationStrings = new ArrayList();

   public GuiModProfiles() {
      this.calculateHeight();
      this.calculateConfirmationBoxHeight();
      this.calculateCenteredBoxOffsets();
      this.initialize();
   }

   public GuiModProfiles(int boxXOffset, int boxYOffset) {
      this.calculateHeight();
      this.calculateConfirmationBoxHeight();
      this.boxXOffset = boxXOffset;
      this.boxYOffset = boxYOffset;
      this.initialize();
   }

   private void initialize() {
      this.bottomButtons.add(GuiModProfiles.BottomButtonType.NEW);
      this.bottomButtons.add(GuiModProfiles.BottomButtonType.PRESETS);
      this.bottomButtons.add(GuiModProfiles.BottomButtonType.CLONE);
      this.bottomButtons.add(GuiModProfiles.BottomButtonType.DELETE);
      this.confirmationBoxButtons.add(GuiModProfiles.BottomButtonType.YES);
      this.confirmationBoxButtons.add(GuiModProfiles.BottomButtonType.NO);
      this.informationStrings.add("You can view your profile files by clicking on the folder button");
      this.informationStrings.add("Share profiles with your friends by sending them your profile files and placing them in the mod profiles folder");
      this.informationStrings.add("Hold SHIFT to delete profiles without the confirmation prompt");
      this.informationStrings.add("Select a profile and use the UP/DOWN arrow keys to move the profile up or down in the ordering");
   }

   private void calculateHeight() {
      this.boxHeight = this.titleTextTopPadding;
      this.boxHeight += this.titleTextFontHeight + this.titleTextBottomPadding;
      this.boxHeight += this.backForwardButtonHeight + this.profileBoxTopPadding;
      this.boxHeight += this.profileEntryHeight * this.numberOfProfilesPerPage;
      this.boxHeight += this.profileBoxBottomPadding;
      this.boxHeight += this.bottomButtonHeight + this.bottomButtonBottomPadding;
   }

   private void calculateConfirmationBoxHeight() {
      this.confirmationBoxHeight = this.confirmationTextTopPadding;
      this.confirmationBoxHeight += this.confirmationTextFontHeight + this.confirmationTextBottomPadding;
      this.confirmationBoxHeight += this.confirmationBoxButtonHeight + this.confirmationBoxButtonBottomPadding;
   }

   public void calculateCenteredBoxOffsets() {
      int i = Minecraft.getMinecraft().displayWidth / Wrapper.getInstance().getRealScaleFactor();
      int j = Minecraft.getMinecraft().displayHeight / Wrapper.getInstance().getRealScaleFactor();
      this.boxXOffset = (int)((float)(i - this.boxWidth) / 2.0F);
      this.boxYOffset = (int)((float)(j - this.boxHeight) / 2.0F);
   }

   public void render(int mouseX, int mouseY) {
      if(this.infoBoxOpen) {
         this.renderInfoBox(mouseX, mouseY);
      } else {
         this.renderMainBox(mouseX, mouseY);
         if(this.confirmationBoxOpen) {
            this.renderConfirmationBox(mouseX, mouseY);
         }
      }

   }

   private void renderConfirmationBox(int mouseX, int mouseY) {
      int i = this.boxXOffset + (int)((float)(this.boxWidth - this.confirmationBoxWidth) / 2.0F);
      int j = this.boxYOffset + (int)((float)(this.boxHeight - this.confirmationBoxHeight) / 2.0F);
      BadlionGuiScreen.drawOutlinedBox(i, j, this.confirmationBoxWidth, this.confirmationBoxHeight, 0.95F);
      GL11.glEnable(3553);
      GL11.glEnable(3042);
      int k = j + this.confirmationTextTopPadding;
      int l = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(this.confirmationText, this.confirmationTextFontHeight, BadlionFontRenderer.FontType.TITLE);
      ColorUtil.bindHexColorRGBA(-2119633);
      Wrapper.getInstance().getBadlionFontRenderer().drawString(i + (int)((float)this.confirmationBoxWidth / 2.0F) - (int)((float)l / 2.0F), k, this.confirmationText, this.confirmationTextFontHeight, BadlionFontRenderer.FontType.TITLE, true);
      k = k + this.confirmationTextFontHeight + this.confirmationTextBottomPadding;
      int i1 = (int)(this.confirmationBoxElementWidthProportion * (float)this.confirmationBoxWidth);
      int j1 = (int)((float)(this.confirmationBoxWidth - i1) / 2.0F);
      int k1 = i + j1;
      int l1 = (int)((float)(i1 - this.bottomButtonHorizontalPadding * (this.confirmationBoxButtons.size() - 1)) / (float)this.confirmationBoxButtons.size());

      for(int i2 = 0; i2 < this.confirmationBoxButtons.size(); ++i2) {
         GuiModProfiles.BottomButtonType guimodprofiles$bottombuttontype = (GuiModProfiles.BottomButtonType)this.confirmationBoxButtons.get(i2);
         String s = guimodprofiles$bottombuttontype.getText();
         BadlionGuiScreen.drawButton(k1 + i2 * (l1 + this.confirmationBoxButtonHorizontalPadding), k, l1, this.confirmationBoxButtonHeight, this.isMouseOverConfirmationBoxButton(mouseX, mouseY, guimodprofiles$bottombuttontype), s, this.confirmationBoxButtonFontHeight);
      }

      k = k + this.confirmationBoxButtonHeight + this.confirmationBoxButtonBottomPadding;
      this.confirmationBoxHeight = k - j;
   }

   private void renderInfoBox(int mouseX, int mouseY) {
      this.infoTextFontHeight = 10;
      if(this.informationStrings.contains("key1")) {
         this.informationStrings.clear();
         this.informationStrings.add("You can view your profile files by clicking on the folder button");
         this.informationStrings.add("Share profiles with your friends by sending them your profile files and placing them in the mod profiles folder");
         this.informationStrings.add("Hold SHIFT to delete profiles without the confirmation prompt");
         this.informationStrings.add("Select a profile and use the UP/DOWN arrow keys to move the profile up or down in the ordering");
      }

      int i = this.boxXOffset;
      int j = this.boxYOffset;
      BadlionGuiScreen.drawOutlinedBox(i, j, this.boxWidth, this.boxHeight);
      GL11.glEnable(3553);
      GL11.glEnable(3042);
      int k = i + this.backForwardButtonHorizontalPadding;
      int l = j + this.titleTextTopPadding;
      if(this.isMouseOverInfoBoxBackButton(mouseX, mouseY)) {
         ColorUtil.bindHexColorRGBA(-11755587);
      } else {
         ColorUtil.bindHexColorRGBA(-1);
      }

      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(backButton);
      Gui.drawScaledCustomSizeModalRect(i + this.backForwardButtonHorizontalPadding, l, 0.0F, 0.0F, 92, 92, this.backForwardButtonWidth, this.backForwardButtonHeight, 92.0F, 92.0F);
      l = l + this.backForwardButtonHeight + this.infoTextTopPadding;
      ColorUtil.bindHexColorRGBA(-1);
      int i1 = this.boxWidth - 2 * this.backForwardButtonHorizontalPadding;

      for(String s : this.informationStrings) {
         String[] astring = s.split(" ");
         int j1 = 0;

         for(String s1 = "- "; j1 < astring.length; ++j1) {
            String s2 = s1 + " " + astring[j1];
            int k1 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(s2, this.infoTextFontHeight, BadlionFontRenderer.FontType.TITLE);
            if(k1 > i1) {
               Wrapper.getInstance().getBadlionFontRenderer().drawString(k, l, s1, this.infoTextFontHeight, BadlionFontRenderer.FontType.TITLE, true);
               l += this.infoTextFontHeight + this.infoTextInternalVerticalPadding;
               s1 = astring[j1];
            } else if(j1 == astring.length - 1) {
               Wrapper.getInstance().getBadlionFontRenderer().drawString(k, l, s2, this.infoTextFontHeight, BadlionFontRenderer.FontType.TITLE, true);
               l += this.infoTextFontHeight;
            } else {
               s1 = s2;
            }
         }

         l += this.infoTextVerticalPadding;
      }

   }

   private void renderMainBox(int mouseX, int mouseY) {
      if(this.modProfileBeingEdited != null) {
         this.checkModProfileBeingEdited();
      }

      int i = this.boxXOffset;
      int j = this.boxYOffset;
      BadlionGuiScreen.drawOutlinedBox(i, j, this.boxWidth, this.boxHeight);
      GL11.glEnable(3553);
      GL11.glEnable(3042);
      int k = (int)(this.elementWidthProportion * (float)this.boxWidth);
      int l = (int)((float)(this.boxWidth - k) / 2.0F);
      int i1 = i + l;
      int j1 = j + this.titleTextTopPadding;
      int k1 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(this.titleText, this.titleTextFontHeight, BadlionFontRenderer.FontType.TITLE);
      ColorUtil.bindHexColorRGBA(-2119633);
      Wrapper.getInstance().getBadlionFontRenderer().drawString(i + (int)((float)this.boxWidth / 2.0F) - (int)((float)k1 / 2.0F), j1, this.titleText, this.titleTextFontHeight, BadlionFontRenderer.FontType.TITLE, true);
      if(this.isMouseOverInfoButton(mouseX, mouseY)) {
         ColorUtil.bindHexColorRGBA(-11755587);
      } else {
         ColorUtil.bindHexColorRGBA(-1);
      }

      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(infoButton);
      Gui.drawScaledCustomSizeModalRect(i + this.backForwardButtonHorizontalPadding, j1, 0.0F, 0.0F, 92, 92, this.infoButtonWidth, this.infoButtonHeight, 92.0F, 92.0F);
      if(this.isMouseOverOpenProfilesFolderButton(mouseX, mouseY)) {
         ColorUtil.bindHexColorRGBA(-11755587);
      } else {
         ColorUtil.bindHexColorRGBA(-1);
      }

      Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(folderButton);
      Gui.drawScaledCustomSizeModalRect(i + this.boxWidth - this.backForwardButtonHorizontalPadding - this.folderButtonWidth, j1, 0.0F, 0.0F, 92, 92, this.folderButtonWidth, this.folderButtonHeight, 92.0F, 92.0F);
      j1 = j1 + this.titleTextFontHeight + this.titleTextBottomPadding;
      if(this.currentPage != 1) {
         if(this.isMouseOverBackPageButton(mouseX, mouseY)) {
            ColorUtil.bindHexColorRGBA(-11755587);
         } else {
            ColorUtil.bindHexColorRGBA(-1);
         }

         Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(backButton);
         Gui.drawScaledCustomSizeModalRect(i + this.backForwardButtonHorizontalPadding, j1, 0.0F, 0.0F, 92, 92, this.backForwardButtonWidth, this.backForwardButtonHeight, 92.0F, 92.0F);
      }

      if(Wrapper.getInstance().getModProfileManager().getModProfiles().size() > this.currentPage * this.numberOfProfilesPerPage) {
         if(this.isMouseOverForwardPageButton(mouseX, mouseY)) {
            ColorUtil.bindHexColorRGBA(-11755587);
         } else {
            ColorUtil.bindHexColorRGBA(-1);
         }

         Wrapper.getInstance().getBlTextureManager().bindTextureMipmapped(forwardButton);
         Gui.drawScaledCustomSizeModalRect(i + this.boxWidth - this.backForwardButtonHorizontalPadding - this.backForwardButtonWidth, j1, 0.0F, 0.0F, 92, 92, this.backForwardButtonWidth, this.backForwardButtonHeight, 92.0F, 92.0F);
      }

      ColorUtil.bindHexColorRGBA(-1);
      int l1 = Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(this.pageTextPrefix + this.currentPage, this.pageTextFontHeight, BadlionFontRenderer.FontType.TITLE);
      Wrapper.getInstance().getBadlionFontRenderer().drawString(i + (int)((float)this.boxWidth / 2.0F) - (int)((float)l1 / 2.0F), j1 + (int)((float)(this.backForwardButtonHeight - this.pageTextFontHeight) / 2.0F), this.pageTextPrefix + this.currentPage, this.pageTextFontHeight, BadlionFontRenderer.FontType.TITLE, true);
      j1 = j1 + this.backForwardButtonHeight + this.profileBoxTopPadding;

      for(int i2 = 0; i2 < this.numberOfProfilesPerPage; ++i2) {
         int j2 = i2 + (this.currentPage - 1) * this.numberOfProfilesPerPage;
         ModProfile modprofile = null;
         if(j2 < Wrapper.getInstance().getModProfileManager().getModProfiles().size()) {
            modprofile = (ModProfile)Wrapper.getInstance().getModProfileManager().getModProfiles().get(j2);
         }

         GL11.glDisable(3553);
         Pair<ModProfile, Boolean> pair = this.isMouseOverModProfile(mouseX, mouseY);
         if(j2 == Wrapper.getInstance().getActiveModProfile().getSortIndex()) {
            BadlionGuiScreen.drawRoundedRect(i1, j1, i1 + k, j1 + this.profileEntryHeight, 2.0F, -12545617);
         } else if(modprofile != null && pair != null && modprofile == pair.getLeft() && !this.confirmationBoxOpen) {
            BadlionGuiScreen.drawRoundedRect(i1, j1, i1 + k, j1 + this.profileEntryHeight, 2.0F, 1620679065);
         }

         if(modprofile != null) {
            if(this.modProfileBeingEdited != null && modprofile == this.modProfileBeingEdited) {
               GL11.glEnable(3553);
               GL11.glEnable(3042);
               GL11.glBlendFunc(770, 771);
               this.modProfileEditInputField.render();
            } else {
               ColorUtil.bindHexColorRGBA(-1);
               Wrapper.getInstance().getBadlionFontRenderer().drawString(i1 + this.profileTextHorizontalPadding, j1 + (int)((float)(this.profileEntryHeight - this.profileTextFontHeight) / 2.0F), modprofile.getProfileName(), this.profileTextFontHeight, BadlionFontRenderer.FontType.TITLE, true);
               if(pair != null && modprofile == pair.getLeft() && ((Boolean)pair.getRight()).booleanValue() && !this.confirmationBoxOpen) {
                  ColorUtil.bindHexColorRGBA(-2119633);
               }

               Minecraft.getMinecraft().getTextureManager().bindTexture(textEditButton);
               Gui.drawScaledCustomSizeModalRect(i1 + k - this.profileTextHorizontalPadding - this.profileTextEditButtonWidth, j1 + (int)((float)(this.profileEntryHeight - this.profileTextEditButtonHeight) / 2.0F), 0.0F, 0.0F, 128, 128, this.profileTextEditButtonWidth, this.profileTextEditButtonHeight, 128.0F, 128.0F);
            }
         } else {
            ColorUtil.bindHexColorRGBA(-1);
            Wrapper.getInstance().getBadlionFontRenderer().drawItalicizedString(i1 + this.profileTextHorizontalPadding, j1 + (int)((float)(this.profileEntryHeight - this.profileTextFontHeight) / 2.0F), "Empty", this.profileTextFontHeight, BadlionFontRenderer.FontType.TITLE, true);
         }

         j1 += this.profileEntryHeight;
      }

      j1 = j1 + this.profileBoxBottomPadding;
      int k2 = (int)((float)(k - this.bottomButtonHorizontalPadding * (this.bottomButtons.size() - 1)) / (float)this.bottomButtons.size());

      for(int l2 = 0; l2 < this.bottomButtons.size(); ++l2) {
         GuiModProfiles.BottomButtonType guimodprofiles$bottombuttontype = (GuiModProfiles.BottomButtonType)this.bottomButtons.get(l2);
         String s = guimodprofiles$bottombuttontype.getText();
         if(guimodprofiles$bottombuttontype == GuiModProfiles.BottomButtonType.PRESETS) {
            BadlionGuiScreen.drawButton(i1 + l2 * (k2 + this.bottomButtonHorizontalPadding), j1, k2, this.bottomButtonHeight, !this.confirmationBoxOpen && this.isMouseOverBottomButton(mouseX, mouseY, guimodprofiles$bottombuttontype), s, this.bottomButtonFontHeight, -65536);
         } else {
            BadlionGuiScreen.drawButton(i1 + l2 * (k2 + this.bottomButtonHorizontalPadding), j1, k2, this.bottomButtonHeight, !this.confirmationBoxOpen && this.isMouseOverBottomButton(mouseX, mouseY, guimodprofiles$bottombuttontype), s, this.bottomButtonFontHeight);
         }
      }

      j1 = j1 + this.bottomButtonHeight + this.bottomButtonBottomPadding;
      this.boxHeight = j1 - j;
   }

   public void keyTyped(char character, int keyCode) {
      if(keyCode == 1) {
         if(this.confirmationBoxOpen) {
            this.confirmationBoxOpen = false;
         } else {
            this.setBoxOpen(false);
         }
      } else if(this.modProfileEditInputField != null && this.modProfileEditInputField.isFocused()) {
         this.modProfileEditInputField.keyTyped(character, keyCode);
      } else if(keyCode == 200) {
         Wrapper.getInstance().getModProfileManager().moveActiveModProfileUp();
         this.currentPage = Wrapper.getInstance().getActiveModProfile().getSortIndex() / this.numberOfProfilesPerPage + 1;
      } else if(keyCode == 208) {
         Wrapper.getInstance().getModProfileManager().moveActiveModProfileDown();
         this.currentPage = Wrapper.getInstance().getActiveModProfile().getSortIndex() / this.numberOfProfilesPerPage + 1;
      }

   }

   public boolean onClick(int mouseX, int mouseY, int mouseButton) {
      if(this.confirmationBoxOpen) {
         if(this.isMouseOverConfirmationBox(mouseX, mouseY)) {
            if(this.isMouseOverConfirmationBoxButton(mouseX, mouseY, GuiModProfiles.BottomButtonType.YES)) {
               Wrapper.getInstance().getModProfileManager().deleteActiveModProfile();
               this.currentPage = Wrapper.getInstance().getActiveModProfile().getSortIndex() / this.numberOfProfilesPerPage + 1;
               this.confirmationBoxOpen = false;
            } else if(this.isMouseOverConfirmationBoxButton(mouseX, mouseY, GuiModProfiles.BottomButtonType.NO)) {
               this.confirmationBoxOpen = false;
            }

            return true;
         }

         this.confirmationBoxOpen = false;
      } else if(this.infoBoxOpen) {
         if(this.isMouseOverBox(mouseX, mouseY)) {
            if(this.isMouseOverInfoBoxBackButton(mouseX, mouseY)) {
               this.infoBoxOpen = false;
            }

            return true;
         }

         this.setBoxOpen(false);
      } else {
         if(this.isMouseOverBox(mouseX, mouseY)) {
            if(this.modProfileEditInputField != null) {
               this.modProfileEditInputField.onClick(mouseButton);
            }

            Pair<ModProfile, Boolean> pair = this.isMouseOverModProfile(mouseX, mouseY);
            if(pair != null) {
               if(((Boolean)pair.getRight()).booleanValue()) {
                  this.setModProfileBeingEdited((ModProfile)pair.getLeft());
               } else if(pair.getLeft() != Wrapper.getInstance().getActiveModProfile()) {
                  Wrapper.getInstance().getModProfileManager().activateModProfile((ModProfile)pair.getLeft());
               }
            } else if(this.isMouseOverInfoButton(mouseX, mouseY)) {
               this.infoBoxOpen = true;
            } else if(this.isMouseOverOpenProfilesFolderButton(mouseX, mouseY)) {
               Wrapper.getInstance().getModProfileManager().openModProfilesSystemFolder();
            } else if(this.isMouseOverBackPageButton(mouseX, mouseY)) {
               --this.currentPage;
            } else if(this.isMouseOverForwardPageButton(mouseX, mouseY)) {
               ++this.currentPage;
            } else if(this.isMouseOverBottomButton(mouseX, mouseY, GuiModProfiles.BottomButtonType.NEW)) {
               Wrapper.getInstance().getModProfileManager().createNewModProfile();
               this.currentPage = Wrapper.getInstance().getActiveModProfile().getSortIndex() / this.numberOfProfilesPerPage + 1;
            } else if(!this.isMouseOverBottomButton(mouseX, mouseY, GuiModProfiles.BottomButtonType.PRESETS)) {
               if(this.isMouseOverBottomButton(mouseX, mouseY, GuiModProfiles.BottomButtonType.CLONE)) {
                  Wrapper.getInstance().getModProfileManager().cloneActiveModProfile();
                  this.currentPage = Wrapper.getInstance().getActiveModProfile().getSortIndex() / this.numberOfProfilesPerPage + 1;
               } else if(this.isMouseOverBottomButton(mouseX, mouseY, GuiModProfiles.BottomButtonType.DELETE)) {
                  int i = Keyboard.getEventKeyState()?Keyboard.getEventKey():-1;
                  if(i != 42 && i != 54) {
                     this.confirmationBoxOpen = true;
                  } else {
                     Wrapper.getInstance().getModProfileManager().deleteActiveModProfile();
                     this.currentPage = Wrapper.getInstance().getActiveModProfile().getSortIndex() / this.numberOfProfilesPerPage + 1;
                  }
               }
            }

            return true;
         }

         this.setBoxOpen(false);
      }

      return false;
   }

   private void checkModProfileBeingEdited() {
      if(this.modProfileEditInputField != null && (!this.modProfileEditInputField.isFocused() || !this.boxOpen)) {
         if(!this.modProfileEditInputField.getText().isEmpty()) {
            Wrapper.getInstance().getModProfileManager().renameModProfile(this.modProfileBeingEdited, this.modProfileEditInputField.getText());
         }

         this.modProfileBeingEdited = null;
         this.modProfileEditInputField = null;
      }

   }

   public boolean isMouseOverBox(int mouseX, int mouseY) {
      int i = this.boxXOffset;
      int j = this.boxYOffset;
      return mouseX >= i && mouseX < i + this.boxWidth && mouseY >= j && mouseY < j + this.boxHeight;
   }

   public boolean isMouseOverConfirmationBox(int mouseX, int mouseY) {
      int i = this.boxXOffset + (int)((float)(this.boxWidth - this.confirmationBoxWidth) / 2.0F);
      int j = this.boxYOffset + (int)((float)(this.boxHeight - this.confirmationBoxHeight) / 2.0F);
      return mouseX >= i && mouseX < i + this.confirmationBoxWidth && mouseY >= j && mouseY < j + this.confirmationBoxHeight;
   }

   public boolean isMouseOverInfoButton(int mouseX, int mouseY) {
      int i = this.boxXOffset;
      int j = this.boxYOffset;
      int k = i + this.backForwardButtonHorizontalPadding;
      int l = j + this.titleTextTopPadding;
      return mouseX >= k && mouseX < k + this.backForwardButtonWidth && mouseY >= l && mouseY < l + this.backForwardButtonHeight;
   }

   public boolean isMouseOverInfoBoxBackButton(int mouseX, int mouseY) {
      int i = this.boxXOffset;
      int j = this.boxYOffset;
      int k = i + this.backForwardButtonHorizontalPadding;
      int l = j + this.titleTextTopPadding;
      return mouseX >= k && mouseX < k + this.backForwardButtonWidth && mouseY >= l && mouseY < l + this.backForwardButtonHeight;
   }

   public boolean isMouseOverOpenProfilesFolderButton(int mouseX, int mouseY) {
      int i = this.boxXOffset;
      int j = this.boxYOffset;
      int k = i + this.boxWidth - this.backForwardButtonHorizontalPadding - this.backForwardButtonWidth;
      int l = j + this.titleTextTopPadding;
      return mouseX >= k && mouseX < k + this.backForwardButtonWidth && mouseY >= l && mouseY < l + this.backForwardButtonHeight;
   }

   public boolean isMouseOverBackPageButton(int mouseX, int mouseY) {
      if(this.currentPage == 1) {
         return false;
      } else {
         int i = this.boxXOffset;
         int j = this.boxYOffset;
         int k = i + this.backForwardButtonHorizontalPadding;
         int l = j + this.titleTextTopPadding + this.titleTextFontHeight + this.titleTextBottomPadding;
         return mouseX >= k && mouseX < k + this.backForwardButtonWidth && mouseY >= l && mouseY < l + this.backForwardButtonHeight;
      }
   }

   public boolean isMouseOverForwardPageButton(int mouseX, int mouseY) {
      if(Wrapper.getInstance().getModProfileManager().getModProfiles().size() <= this.currentPage * this.numberOfProfilesPerPage) {
         return false;
      } else {
         int i = this.boxXOffset;
         int j = this.boxYOffset;
         int k = i + this.boxWidth - this.backForwardButtonHorizontalPadding - this.backForwardButtonWidth;
         int l = j + this.titleTextTopPadding + this.titleTextFontHeight + this.titleTextBottomPadding;
         return mouseX >= k && mouseX < k + this.backForwardButtonWidth && mouseY >= l && mouseY < l + this.backForwardButtonHeight;
      }
   }

   public Pair isMouseOverModProfile(int mouseX, int mouseY) {
      int i = this.boxXOffset;
      int j = (int)(this.elementWidthProportion * (float)this.boxWidth);
      int k = (int)((float)(this.boxWidth - j) / 2.0F);
      int l = i + k;
      int i1 = this.boxYOffset + this.titleTextTopPadding + this.titleTextFontHeight + this.titleTextBottomPadding + this.backForwardButtonHeight + this.profileBoxTopPadding;
      if(mouseX >= l && mouseX < l + j && mouseY > i1 && mouseY < i1 + this.profileEntryHeight * this.numberOfProfilesPerPage) {
         int j1 = (int)Math.ceil((double)((float)(mouseY - i1) / (float)this.profileEntryHeight)) - 1 + (this.currentPage - 1) * this.numberOfProfilesPerPage;
         if(j1 < Wrapper.getInstance().getModProfileManager().getModProfiles().size()) {
            ModProfile modprofile = (ModProfile)Wrapper.getInstance().getModProfileManager().getModProfiles().get(j1);
            boolean flag = false;
            int k1 = l + j - this.profileTextHorizontalPadding - this.profileTextEditButtonWidth;
            int l1 = i1 + j1 % this.numberOfProfilesPerPage * this.profileEntryHeight + (int)((float)(this.profileEntryHeight - this.profileTextEditButtonHeight) / 2.0F);
            if(mouseX >= k1 && mouseX < k1 + this.profileTextEditButtonWidth && mouseY > l1 && mouseY < l1 + this.profileTextEditButtonHeight) {
               flag = true;
            }

            return Pair.of(modprofile, Boolean.valueOf(flag));
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public boolean isMouseOverBottomButton(int mouseX, int mouseY, GuiModProfiles.BottomButtonType bottomButtonType) {
      int i = this.boxXOffset;
      int j = (int)(this.elementWidthProportion * (float)this.boxWidth);
      int k = (int)((float)(this.boxWidth - j) / 2.0F);
      int l = i + k;
      int i1 = (int)((float)(j - this.bottomButtonHorizontalPadding * (this.bottomButtons.size() - 1)) / (float)this.bottomButtons.size());
      int j1 = this.boxYOffset + this.titleTextTopPadding + this.titleTextFontHeight + this.titleTextBottomPadding + this.backForwardButtonHeight + this.profileBoxTopPadding + this.profileEntryHeight * this.numberOfProfilesPerPage + this.profileBoxBottomPadding;
      int k1 = this.bottomButtons.indexOf(bottomButtonType);
      return k1 == -1?false:(mouseX >= l + k1 * (i1 + this.bottomButtonHorizontalPadding) && mouseX < l + k1 * (i1 + this.bottomButtonHorizontalPadding) + i1 && mouseY >= j1 && mouseY < j1 + this.bottomButtonHeight?bottomButtonType != GuiModProfiles.BottomButtonType.PRESETS:false);
   }

   public boolean isMouseOverConfirmationBoxButton(int mouseX, int mouseY, GuiModProfiles.BottomButtonType bottomButtonType) {
      int i = this.boxXOffset + (int)((float)(this.boxWidth - this.confirmationBoxWidth) / 2.0F);
      int j = this.boxYOffset + (int)((float)(this.boxHeight - this.confirmationBoxHeight) / 2.0F);
      int k = (int)(this.confirmationBoxElementWidthProportion * (float)this.confirmationBoxWidth);
      int l = (int)((float)(this.confirmationBoxWidth - k) / 2.0F);
      int i1 = i + l;
      int j1 = (int)((float)(k - this.confirmationBoxButtonHorizontalPadding * (this.confirmationBoxButtons.size() - 1)) / (float)this.confirmationBoxButtons.size());
      int k1 = j + this.confirmationTextTopPadding + this.confirmationTextFontHeight + this.confirmationTextBottomPadding;
      int l1 = this.confirmationBoxButtons.indexOf(bottomButtonType);
      return l1 == -1?false:mouseX >= i1 + l1 * (j1 + this.confirmationBoxButtonHorizontalPadding) && mouseX < i1 + l1 * (j1 + this.confirmationBoxButtonHorizontalPadding) + j1 && mouseY >= k1 && mouseY < k1 + this.confirmationBoxButtonHeight;
   }

   public boolean isBoxOpen() {
      return this.boxOpen;
   }

   public void setBoxOpen(boolean boxOpen) {
      this.boxOpen = boxOpen;
      if(!boxOpen) {
         this.confirmationBoxOpen = false;
         this.infoBoxOpen = false;
         this.checkModProfileBeingEdited();
      }

   }

   public boolean isConfirmationBoxOpen() {
      return this.confirmationBoxOpen;
   }

   public void setModProfileBeingEdited(ModProfile modProfile) {
      this.modProfileBeingEdited = modProfile;
      if(modProfile != null) {
         int i = this.boxXOffset;
         int j = (int)(this.elementWidthProportion * (float)this.boxWidth);
         int k = (int)((float)(this.boxWidth - j) / 2.0F);
         int l = i + k;
         int i1 = this.boxYOffset + this.titleTextTopPadding + this.titleTextFontHeight + this.titleTextBottomPadding + this.backForwardButtonHeight + this.profileBoxTopPadding;
         int j1 = l + this.profileTextHorizontalPadding;
         int k1 = i1 + modProfile.getSortIndex() % this.numberOfProfilesPerPage * this.profileEntryHeight;
         this.modProfileEditInputField = new InputField(j1 - 5, k1 + 4, j - 2 * this.profileTextHorizontalPadding, this.profileTextFontHeight, false, modProfile.getProfileName(), "", 24, InputField.InputFlavor.MOD_PROFILE_NAME);
         this.modProfileEditInputField.setFocused(true);
      }

   }

   public static enum BottomButtonType {
      NEW("NEW"),
      PRESETS("PRESETS"),
      CLONE("CLONE"),
      DELETE("DELETE"),
      YES("YES"),
      NO("NO");

      private String text;

      private BottomButtonType(String text) {
         this.text = text;
      }

      public String getText() {
         return this.text;
      }
   }
}
