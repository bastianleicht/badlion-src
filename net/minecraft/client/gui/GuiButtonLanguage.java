package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import org.lwjgl.opengl.GL11;

public class GuiButtonLanguage extends GuiButton {
   public GuiButtonLanguage(int buttonID, int xPos, int yPos) {
      super(buttonID, xPos, yPos, 18, 18, "");
   }

   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if(this.visible) {
         mc.getTextureManager().bindTexture(GuiButton.earthIcon);
         GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
         boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
         if(flag) {
            GL11.glColor4f(0.7F, 0.7F, 0.7F, 1.0F);
         }

         Gui.drawModalRectWithCustomSizedTexture(this.xPosition, this.yPosition, 0.0F, 0.0F, this.width, this.height, (float)this.width, (float)this.height);
      }

   }
}
