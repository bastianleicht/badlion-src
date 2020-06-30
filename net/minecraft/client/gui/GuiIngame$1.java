package net.minecraft.client.gui;

import com.google.common.base.Predicate;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.scoreboard.Score;

class GuiIngame$1 implements Predicate {
   private GuiIngame field_178904_a;

   GuiIngame$1(GuiIngame p_i45544_1_) {
      this.field_178904_a = p_i45544_1_;
   }

   public boolean apply(Score p_apply_1_) {
      return p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#");
   }
}
