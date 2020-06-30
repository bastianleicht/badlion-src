package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlotFancy;
import net.minecraft.client.gui.ServerListEntryLanDetected;
import net.minecraft.client.gui.ServerListEntryLanScan;
import net.minecraft.client.gui.ServerListEntryNormal;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerDetector;

public class ServerSelectionList extends GuiSlotFancy {
   private final GuiMultiplayer owner;
   private final List field_148198_l = Lists.newArrayList();
   private final List field_148199_m = Lists.newArrayList();
   private final GuiListExtended.IGuiListEntry lanScanEntry = new ServerListEntryLanScan();
   private int selectedSlotIndex = -1;

   public ServerSelectionList(GuiMultiplayer p_i10_1_, Minecraft p_i10_2_, int p_i10_3_, int p_i10_4_, int p_i10_5_, int p_i10_6_, int p_i10_7_, GuiScreen p_i10_8_) {
      super(p_i10_2_, p_i10_3_, p_i10_4_, p_i10_5_, p_i10_6_, p_i10_7_, p_i10_8_);
      this.owner = p_i10_1_;
   }

   public GuiListExtended.IGuiListEntry getListEntry(int index) {
      if(index < this.field_148198_l.size()) {
         return (GuiListExtended.IGuiListEntry)this.field_148198_l.get(index);
      } else {
         index = index - this.field_148198_l.size();
         if(index == 0) {
            return this.lanScanEntry;
         } else {
            --index;
            return (GuiListExtended.IGuiListEntry)this.field_148199_m.get(index);
         }
      }
   }

   protected int getSize0() {
      return this.field_148198_l.size() + 1 + this.field_148199_m.size();
   }

   protected void elementClicked0(int p_elementClicked0_1_, boolean p_elementClicked0_2_, int p_elementClicked0_3_, int p_elementClicked0_4_) {
   }

   public void setSelectedSlotIndex(int selectedSlotIndexIn) {
      this.selectedSlotIndex = selectedSlotIndexIn;
   }

   protected boolean isSelected0(int p_isSelected0_1_) {
      return p_isSelected0_1_ == this.selectedSlotIndex;
   }

   protected void drawBackground0() {
   }

   protected void drawSlot0(int p_drawSlot0_1_, int p_drawSlot0_2_, int p_drawSlot0_3_, int p_drawSlot0_4_, int p_drawSlot0_5_, int p_drawSlot0_6_) {
      this.getListEntry(p_drawSlot0_1_).drawEntry(p_drawSlot0_1_, p_drawSlot0_2_, p_drawSlot0_3_, this.getListWidth0(), p_drawSlot0_4_, p_drawSlot0_5_, p_drawSlot0_6_, this.getSlotIndexFromScreenCoords(p_drawSlot0_5_, p_drawSlot0_6_) == p_drawSlot0_1_);
   }

   public int func_148193_k() {
      return this.selectedSlotIndex;
   }

   public void func_148195_a(ServerList p_148195_1_) {
      this.field_148198_l.clear();

      for(int i = 0; i < p_148195_1_.countServers(); ++i) {
         this.field_148198_l.add(new ServerListEntryNormal(this.owner, p_148195_1_.getServerData(i)));
      }

   }

   public void func_148194_a(List p_148194_1_) {
      this.field_148199_m.clear();

      for(LanServerDetector.LanServer lanserverdetector$lanserver : p_148194_1_) {
         this.field_148199_m.add(new ServerListEntryLanDetected(this.owner, lanserverdetector$lanserver));
      }

   }

   public boolean mouseClicked(int p_mouseClicked_1_, int p_mouseClicked_2_, int p_mouseClicked_3_) {
      if(this.isMouseYWithinSlotBounds(p_mouseClicked_2_)) {
         int i = this.getSlotIndexFromScreenCoords(p_mouseClicked_1_, p_mouseClicked_2_);
         if(i >= 0) {
            int j = this.left + this.width / 2 - this.getListWidth0() / 2 + 2;
            int k = this.top + 4 - this.getAmountScrolled() + i * this.slotHeight + this.headerPadding;
            int l = p_mouseClicked_1_ - j;
            int i1 = p_mouseClicked_2_ - k;
            if(this.getListEntry(i).mousePressed(i, p_mouseClicked_1_, p_mouseClicked_2_, p_mouseClicked_3_, l, i1)) {
               this.setEnabled(false);
               return true;
            }
         }
      }

      return false;
   }

   public boolean mouseReleased(int p_mouseReleased_1_, int p_mouseReleased_2_, int p_mouseReleased_3_) {
      for(int i = 0; i < this.getSize0(); ++i) {
         int j = this.left + this.width / 2 - this.getListWidth0() / 2 + 2;
         int k = this.top + 4 - this.getAmountScrolled() + i * this.slotHeight + this.headerPadding;
         int l = p_mouseReleased_1_ - j;
         int i1 = p_mouseReleased_2_ - k;
         this.getListEntry(i).mouseReleased(i, p_mouseReleased_1_, p_mouseReleased_2_, p_mouseReleased_3_, l, i1);
      }

      this.setEnabled(true);
      return false;
   }

   protected int getScrollBarX0() {
      return super.getScrollBarX0() + 30;
   }

   public int getListWidth0() {
      return super.getListWidth0() + 85;
   }
}
