package com.mojang.realmsclient.gui.screens;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.Ops;
import com.mojang.realmsclient.dto.PlayerInfo;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.gui.RealmsConstants;
import com.mojang.realmsclient.gui.screens.RealmsActivityScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfirmScreen;
import com.mojang.realmsclient.gui.screens.RealmsInviteScreen;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsClickableScrolledSelectionList;
import net.minecraft.realms.RealmsDefaultVertexFormat;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.realms.Tezzelator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class RealmsPlayerScreen extends RealmsScreen {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final String OP_ICON_LOCATION = "realms:textures/gui/realms/op_icon.png";
   private static final String USER_ICON_LOCATION = "realms:textures/gui/realms/user_icon.png";
   private static final String CROSS_ICON_LOCATION = "realms:textures/gui/realms/cross_icon.png";
   private String toolTip;
   private final RealmsConfigureWorldScreen lastScreen;
   private RealmsServer serverData;
   private RealmsPlayerScreen.InvitedSelectionList invitedSelectionList;
   private int column1_x;
   private int column_width;
   private int column2_x;
   private static final int BUTTON_BACK_ID = 0;
   private static final int BUTTON_INVITE_ID = 1;
   private static final int BUTTON_UNINVITE_ID = 2;
   private static final int BUTTON_ACTIVITY_ID = 3;
   private RealmsButton inviteButton;
   private RealmsButton activityButton;
   private int selectedInvitedIndex = -1;
   private String selectedInvited;
   private boolean stateChanged;

   public RealmsPlayerScreen(RealmsConfigureWorldScreen lastScreen, RealmsServer serverData) {
      this.lastScreen = lastScreen;
      this.serverData = serverData;
   }

   public void mouseEvent() {
      super.mouseEvent();
      if(this.invitedSelectionList != null) {
         this.invitedSelectionList.mouseEvent();
      }

   }

   public void tick() {
      super.tick();
   }

   public void init() {
      this.column1_x = this.width() / 2 - 160;
      this.column_width = 150;
      this.column2_x = this.width() / 2 + 12;
      Keyboard.enableRepeatEvents(true);
      this.buttonsClear();
      this.buttonsAdd(this.inviteButton = newButton(1, this.column2_x, RealmsConstants.row(1), this.column_width + 10, 20, getLocalizedString("mco.configure.world.buttons.invite")));
      this.buttonsAdd(this.activityButton = newButton(3, this.column2_x, RealmsConstants.row(3), this.column_width + 10, 20, getLocalizedString("mco.configure.world.buttons.activity")));
      this.buttonsAdd(newButton(0, this.column2_x + this.column_width / 2 + 2, RealmsConstants.row(12), this.column_width / 2 + 10 - 2, 20, getLocalizedString("gui.back")));
      this.invitedSelectionList = new RealmsPlayerScreen.InvitedSelectionList();
      this.invitedSelectionList.setLeftPos(this.column1_x);
      this.inviteButton.active(false);
   }

   public void removed() {
      Keyboard.enableRepeatEvents(false);
   }

   public void buttonClicked(RealmsButton button) {
      if(button.active()) {
         switch(button.id()) {
         case 0:
            this.backButtonClicked();
            break;
         case 1:
            Realms.setScreen(new RealmsInviteScreen(this.lastScreen, this, this.serverData));
            break;
         case 2:
         default:
            return;
         case 3:
            Realms.setScreen(new RealmsActivityScreen(this, this.serverData));
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
         Realms.setScreen(this.lastScreen.getNewScreen());
      } else {
         Realms.setScreen(this.lastScreen);
      }

   }

   private void op(int index) {
      RealmsClient client = RealmsClient.createRealmsClient();
      String selectedInvite = ((PlayerInfo)this.serverData.players.get(index)).getName();

      try {
         this.updateOps(client.op(this.serverData.id, selectedInvite));
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn\'t op the user");
      }

   }

   private void deop(int index) {
      RealmsClient client = RealmsClient.createRealmsClient();
      String selectedInvite = ((PlayerInfo)this.serverData.players.get(index)).getName();

      try {
         this.updateOps(client.deop(this.serverData.id, selectedInvite));
      } catch (RealmsServiceException var5) {
         LOGGER.error("Couldn\'t deop the user");
      }

   }

   private void updateOps(Ops ops) {
      for(PlayerInfo playerInfo : this.serverData.players) {
         playerInfo.setOperator(ops.ops.contains(playerInfo.getName()));
      }

   }

   private void uninvite(int index) {
      if(index >= 0 && index < this.serverData.players.size()) {
         PlayerInfo playerInfo = (PlayerInfo)this.serverData.players.get(index);
         this.selectedInvited = playerInfo.getUuid();
         this.selectedInvitedIndex = index;
         RealmsConfirmScreen confirmScreen = new RealmsConfirmScreen(this, "Question", getLocalizedString("mco.configure.world.uninvite.question") + " \'" + playerInfo.getName() + "\' ?", 2);
         Realms.setScreen(confirmScreen);
      }

   }

   public void confirmResult(boolean result, int id) {
      if(id == 2) {
         if(result) {
            RealmsClient client = RealmsClient.createRealmsClient();

            try {
               client.uninvite(this.serverData.id, this.selectedInvited);
            } catch (RealmsServiceException var5) {
               LOGGER.error("Couldn\'t uninvite user");
            }

            this.deleteFromInvitedList(this.selectedInvitedIndex);
         }

         this.stateChanged = true;
         Realms.setScreen(this);
      }

   }

   private void deleteFromInvitedList(int selectedInvitedIndex) {
      this.serverData.players.remove(selectedInvitedIndex);
   }

   public void render(int xm, int ym, float a) {
      this.toolTip = null;
      this.renderBackground();
      if(this.invitedSelectionList != null) {
         this.invitedSelectionList.render(xm, ym, a);
      }

      int bottom_border = RealmsConstants.row(12) + 20;
      GL11.glDisable(2896);
      GL11.glDisable(2912);
      Tezzelator t = Tezzelator.instance;
      bind("textures/gui/options_background.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      float s = 32.0F;
      t.begin(7, RealmsDefaultVertexFormat.POSITION_TEX_COLOR);
      t.vertex(0.0D, (double)this.height(), 0.0D).tex(0.0D, (double)((float)(this.height() - bottom_border) / 32.0F + 0.0F)).color(64, 64, 64, 255).endVertex();
      t.vertex((double)this.width(), (double)this.height(), 0.0D).tex((double)((float)this.width() / 32.0F), (double)((float)(this.height() - bottom_border) / 32.0F + 0.0F)).color(64, 64, 64, 255).endVertex();
      t.vertex((double)this.width(), (double)bottom_border, 0.0D).tex((double)((float)this.width() / 32.0F), 0.0D).color(64, 64, 64, 255).endVertex();
      t.vertex(0.0D, (double)bottom_border, 0.0D).tex(0.0D, 0.0D).color(64, 64, 64, 255).endVertex();
      t.end();
      this.drawCenteredString(getLocalizedString("mco.configure.world.players.title"), this.width() / 2, 17, 16777215);
      if(this.serverData != null && this.serverData.players != null) {
         this.drawString(getLocalizedString("mco.configure.world.invited") + " (" + this.serverData.players.size() + ")", this.column1_x, RealmsConstants.row(0), 10526880);
         this.inviteButton.active(this.serverData.players.size() < 200);
      } else {
         this.drawString(getLocalizedString("mco.configure.world.invited"), this.column1_x, RealmsConstants.row(0), 10526880);
         this.inviteButton.active(false);
      }

      super.render(xm, ym, a);
      if(this.serverData != null) {
         if(this.toolTip != null) {
            this.renderMousehoverTooltip(this.toolTip, xm, ym);
         }

      }
   }

   protected void renderMousehoverTooltip(String msg, int x, int y) {
      if(msg != null) {
         int rx = x + 12;
         int ry = y - 12;
         int width = this.fontWidth(msg);
         this.fillGradient(rx - 3, ry - 3, rx + width + 3, ry + 8 + 3, -1073741824, -1073741824);
         this.fontDrawShadow(msg, rx, ry, 16777215);
      }
   }

   private void drawRemoveIcon(int x, int y, int xm, int ym) {
      boolean hovered = xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9 && ym < this.height() - 25 && ym > RealmsConstants.row(1);
      bind("realms:textures/gui/realms/cross_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      RealmsScreen.blit(x, y, 0.0F, hovered?7.0F:0.0F, 8, 7, 8.0F, 14.0F);
      GL11.glPopMatrix();
      if(hovered) {
         this.toolTip = getLocalizedString("mco.configure.world.invites.remove.tooltip");
      }

   }

   private void drawOpped(int x, int y, int xm, int ym) {
      boolean hovered = xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9 && ym < this.height() - 25 && ym > RealmsConstants.row(1);
      bind("realms:textures/gui/realms/op_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      RealmsScreen.blit(x, y, 0.0F, hovered?8.0F:0.0F, 8, 8, 8.0F, 16.0F);
      GL11.glPopMatrix();
      if(hovered) {
         this.toolTip = getLocalizedString("mco.configure.world.invites.ops.tooltip");
      }

   }

   private void drawNormal(int x, int y, int xm, int ym) {
      boolean hovered = xm >= x && xm <= x + 9 && ym >= y && ym <= y + 9;
      bind("realms:textures/gui/realms/user_icon.png");
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      GL11.glPushMatrix();
      RealmsScreen.blit(x, y, 0.0F, hovered?8.0F:0.0F, 8, 8, 8.0F, 16.0F);
      GL11.glPopMatrix();
      if(hovered) {
         this.toolTip = getLocalizedString("mco.configure.world.invites.normal.tooltip");
      }

   }

   private class InvitedSelectionList extends RealmsClickableScrolledSelectionList {
      public InvitedSelectionList() {
         super(RealmsPlayerScreen.this.column_width + 10, RealmsConstants.row(12) + 20, RealmsConstants.row(1), RealmsConstants.row(12) + 20, 13);
      }

      public void customMouseEvent(int y0, int y1, int headerHeight, float yo, int itemHeight) {
         if(Mouse.isButtonDown(0) && this.ym() >= y0 && this.ym() <= y1) {
            int x0 = RealmsPlayerScreen.this.column1_x;
            int x1 = RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width;
            int clickSlotPos = this.ym() - y0 - headerHeight + (int)yo - 4;
            int slot = clickSlotPos / itemHeight;
            if(this.xm() >= x0 && this.xm() <= x1 && slot >= 0 && clickSlotPos >= 0 && slot < this.getItemCount()) {
               this.itemClicked(clickSlotPos, slot, this.xm(), this.ym(), this.width());
            }
         }

      }

      public void itemClicked(int clickSlotPos, int slot, int xm, int ym, int width) {
         if(slot >= 0 && slot <= RealmsPlayerScreen.this.serverData.players.size() && RealmsPlayerScreen.this.toolTip != null) {
            if(!RealmsPlayerScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.ops.tooltip")) && !RealmsPlayerScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.normal.tooltip"))) {
               if(RealmsPlayerScreen.this.toolTip.equals(RealmsScreen.getLocalizedString("mco.configure.world.invites.remove.tooltip"))) {
                  RealmsPlayerScreen.this.uninvite(slot);
               }
            } else if(((PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(slot)).isOperator()) {
               RealmsPlayerScreen.this.deop(slot);
            } else {
               RealmsPlayerScreen.this.op(slot);
            }

         }
      }

      public void renderBackground() {
         RealmsPlayerScreen.this.renderBackground();
      }

      public int getScrollbarPosition() {
         return RealmsPlayerScreen.this.column1_x + this.width() - 5;
      }

      public int getItemCount() {
         return RealmsPlayerScreen.this.serverData == null?1:RealmsPlayerScreen.this.serverData.players.size();
      }

      public int getMaxPosition() {
         return this.getItemCount() * 13;
      }

      protected void renderItem(int i, int x, int y, int h, Tezzelator t, int mouseX, int mouseY) {
         if(RealmsPlayerScreen.this.serverData != null) {
            if(i < RealmsPlayerScreen.this.serverData.players.size()) {
               this.renderInvitedItem(i, x, y, h);
            }

         }
      }

      private void renderInvitedItem(int i, int x, int y, int h) {
         PlayerInfo invited = (PlayerInfo)RealmsPlayerScreen.this.serverData.players.get(i);
         RealmsPlayerScreen.this.drawString(invited.getName(), RealmsPlayerScreen.this.column1_x + 3 + 12, y + 1, invited.getAccepted()?16777215:10526880);
         if(invited.isOperator()) {
            RealmsPlayerScreen.this.drawOpped(RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 10, y + 1, this.xm(), this.ym());
         } else {
            RealmsPlayerScreen.this.drawNormal(RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 10, y + 1, this.xm(), this.ym());
         }

         RealmsPlayerScreen.this.drawRemoveIcon(RealmsPlayerScreen.this.column1_x + RealmsPlayerScreen.this.column_width - 22, y + 2, this.xm(), this.ym());
         RealmsScreen.bindFace(invited.getUuid(), invited.getName());
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         RealmsScreen.blit(RealmsPlayerScreen.this.column1_x + 2 + 2, y + 1, 8.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
         RealmsScreen.blit(RealmsPlayerScreen.this.column1_x + 2 + 2, y + 1, 40.0F, 8.0F, 8, 8, 8, 8, 64.0F, 64.0F);
      }
   }
}
