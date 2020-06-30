package net.badlion.client.mods.render.gui.elements;

import net.badlion.client.mods.render.gui.Component;
import net.badlion.client.mods.render.gui.SizeableComponent;
import net.badlion.client.mods.render.gui.elements.Panel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import org.lwjgl.input.Mouse;

public class Button extends SizeableComponent {
   private Panel background;
   protected int textColor;
   protected int backgroundColor;
   protected int selectionColor;
   protected String text;
   protected long renderTime;
   private boolean wasSelected;
   private boolean hovered;
   private boolean selected;
   private boolean held;

   public Button(String name, String text, int sizeX, int sizeY, int textColor, int backgroundColor, int selectionColor) {
      super(name, sizeX, sizeY);
      this.text = text;
      this.textColor = textColor;
      this.backgroundColor = backgroundColor;
      this.selectionColor = selectionColor;
   }

   public void init() {
      this.background = new Panel(this.name + "background", this.x, this.y, this.sizeX, this.sizeY, this.backgroundColor, true);
   }

   public void update(Panel parent, int mouseX, int mouseY, boolean dragging) {
      if(System.currentTimeMillis() - this.renderTime <= 500L) {
         boolean flag = Minecraft.getMinecraft().currentScreen != null;
         this.hovered = mouseX > this.x && mouseX < this.x + this.getSizeX() && mouseY > this.y && mouseY < this.y + this.getSizeY();
         if(this.isRelative()) {
            this.hovered = mouseX > parent.getX() + this.x && mouseX < parent.getX() + this.x + this.getSizeX() && mouseY > parent.getY() + this.y && mouseY < parent.getY() + this.y + this.getSizeY();
         }

         if(this.hovered) {
            int i = 0;

            for(int j = parent.getComponents().values().size(); j > 0; --j) {
               Component component = (Component)parent.getComponents().values().toArray()[j - 1];
               if(component.isRelative() && mouseX > parent.getX() + component.getX() && mouseX < parent.getX() + component.getX() + component.getSizeX() && mouseY > parent.getY() + component.getY() && mouseY < parent.getY() + component.getY() + component.getSizeY()) {
                  if(component.equals(this)) {
                     break;
                  }

                  this.hovered = false;
               }
            }
         }

         this.held = this.selected;
         this.wasSelected = this.selected;
         this.selected = flag && this.hovered && Mouse.isButtonDown(0) && !dragging;
         if(this.wasSelected != this.selected && this.selected && this.optionalRenderScreen != null) {
            this.optionalRenderScreen.onPanelButtonClick(this);
         }

         if(this.background != null) {
            this.background.setColor(this.hovered && flag?this.selectionColor:this.backgroundColor);
            if(this.selected) {
               this.background.setColor(this.backgroundColor);
            }
         }
      }

   }

   public boolean isSelected() {
      return this.selected && !this.held;
   }

   public boolean isHovered() {
      return this.hovered;
   }

   public void render(GuiIngame gameRenderer, int x, int y) {
      if(this.background != null && this.isVisible()) {
         FontRenderer fontrenderer = Minecraft.getMinecraft().fontRendererObj;
         this.background.setPosition(x, y);
         this.background.render(gameRenderer, x, y);
         int i = x + this.sizeX / 2 - fontrenderer.getStringWidth(this.getText()) / 2;
         int j = y + this.sizeY / 2 - 4;
         gameRenderer.drawString(fontrenderer, "", 0, 0, -1);
         gameRenderer.drawString(fontrenderer, this.getText(), i + 1, j + 1, this.textColor);
         this.renderTime = System.currentTimeMillis();
      }

   }

   public void setText(String text) {
      this.text = text;
   }

   public String getText() {
      return this.text;
   }

   public void deselect() {
      this.selected = false;
      this.hovered = false;
   }

   public void setBackgroundColor(int i) {
      this.background.setColor(i);
   }
}
