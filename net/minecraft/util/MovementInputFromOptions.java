package net.minecraft.util;

import net.badlion.client.Wrapper;
import net.badlion.client.events.event.SneakEvent;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MovementInput;

public class MovementInputFromOptions extends MovementInput {
   private final GameSettings gameSettings;

   public MovementInputFromOptions(GameSettings gameSettingsIn) {
      this.gameSettings = gameSettingsIn;
   }

   public void updatePlayerMoveState() {
      this.moveStrafe = 0.0F;
      this.moveForward = 0.0F;
      if(this.gameSettings.keyBindLeft.isKeyDown()) {
         ++this.moveForward;
      }

      if(this.gameSettings.keyBindRight.isKeyDown()) {
         --this.moveForward;
      }

      if(this.gameSettings.keyBindBack.isKeyDown()) {
         ++this.moveStrafe;
      }

      if(this.gameSettings.keyBindJump.isKeyDown()) {
         --this.moveStrafe;
      }

      this.jump = this.gameSettings.keyBindSneak.isKeyDown();
      SneakEvent sneakevent = new SneakEvent(this.gameSettings.keyBindSprint.isKeyDown());
      Wrapper.getInstance().getActiveModProfile().passEvent(sneakevent);
      this.sneak = sneakevent.isSneaking();
      if(this.sneak) {
         this.moveStrafe = (float)((double)this.moveStrafe * 0.3D);
         this.moveForward = (float)((double)this.moveForward * 0.3D);
      }

   }
}
