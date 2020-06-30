package net.badlion.client.mods.render.gui;

import net.badlion.client.gui.BadlionGuiScreen;
import net.badlion.client.mods.render.gui.IComponent;
import net.badlion.client.mods.render.gui.elements.Panel;
import net.minecraft.client.gui.GuiIngame;

public class Component implements IComponent {
   protected boolean visible = true;
   protected String name;
   protected boolean relative;
   protected int x;
   protected int y;
   protected BadlionGuiScreen optionalRenderScreen;

   public void setOptionalRenderScreen(BadlionGuiScreen optionalRenderScreen) {
      this.optionalRenderScreen = optionalRenderScreen;
   }

   public Component(String name, int x, int y) {
      this.x = x;
      this.y = y;
      this.name = name;
   }

   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   public boolean isVisible() {
      return this.visible;
   }

   public String getName() {
      return this.name;
   }

   public void setRelative(boolean relative) {
      this.relative = relative;
   }

   public boolean isRelative() {
      return this.relative;
   }

   public void render(GuiIngame gameRenderer, int x, int y) {
   }

   public void init() {
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int[] getPosition() {
      return new int[]{this.x, this.y};
   }

   public void setPosition(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void setX(int x) {
      this.setPosition(x, this.y);
   }

   public void setY(int y) {
      this.setPosition(this.x, y);
   }

   public int getSizeX() {
      return 0;
   }

   public int getSizeY() {
      return 0;
   }

   public void update(Panel parent, int mouseX, int mouseY, boolean dragging) {
   }

   public BadlionGuiScreen getOptionalRenderScreen() {
      return this.optionalRenderScreen;
   }
}
