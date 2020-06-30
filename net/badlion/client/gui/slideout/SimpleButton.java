package net.badlion.client.gui.slideout;

import net.badlion.client.Wrapper;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.BadlionGuiScreen;
import net.badlion.client.gui.slideout.RenderElement;
import net.badlion.client.util.ColorUtil;

public class SimpleButton extends RenderElement {
   private String text;
   private int textColor;
   private int color;
   private int hoverColor;
   private boolean rounded;
   private double textScale;
   private int sizeX;
   private int sizeY;
   private boolean selected;
   private boolean hover;
   private int offsetY;
   private int fontSize;

   public SimpleButton(String text, int color, int hoverColor, int textColor) {
      this(text, color, hoverColor, textColor, true, 12);
   }

   public SimpleButton(String text, int color, int hoverColor, int textColor, boolean rounded, int fontSize) {
      this.text = text;
      this.color = color;
      this.hoverColor = hoverColor;
      this.textColor = textColor;
      this.rounded = rounded;
      this.fontSize = fontSize;
   }

   public void init() {
      super.init();
   }

   public void setTextScale(double textScale) {
      this.textScale = textScale;
   }

   public String getText() {
      return this.text;
   }

   public int getColor() {
      return this.color;
   }

   public int getTextColor() {
      return this.textColor;
   }

   public int getHoverColor() {
      return this.hoverColor;
   }

   public boolean isSelected() {
      return this.selected;
   }

   public void render() {
      int i = this.getWidth();
      BadlionGuiScreen.drawRoundedRect(this.getX(), this.getY(), this.getX() + i, this.getY() + this.getHeight(), 2.0F, this.hover?this.getHoverColor():this.getColor());
      if(!this.getText().isEmpty()) {
         ColorUtil.bindHexColorRGBA(this.getTextColor());
         Wrapper.getInstance().getBadlionFontRenderer().drawString(this.getX() + 4, this.getY() + this.getHeight() / 2 - this.fontSize / 2, this.getText(), this.fontSize, BadlionFontRenderer.FontType.TITLE);
      }

   }

   public int getWidth() {
      return Wrapper.getInstance().getBadlionFontRenderer().getStringWidth(this.getText(), this.fontSize, BadlionFontRenderer.FontType.TITLE) + 8;
   }

   public int getHeight() {
      return this.sizeY != 0?this.sizeY:14;
   }

   public void setSize(int sizeX, int sizeY) {
      this.sizeX = sizeX;
      this.sizeY = sizeY;
   }

   public boolean onClick(int mouseButton) {
      if(this.hover && mouseButton == 0) {
         this.selected = true;
      }

      return false;
   }

   public void setOffsetY(int offsetY) {
      this.offsetY = offsetY;
   }

   public void update(int mX, int mY) {
      this.hover = false;
      this.selected = false;
      if(mX > this.getX() && mX < this.getX() + this.getWidth() && mY > this.getY() && mY < this.getY() + this.getHeight()) {
         this.hover = true;
      }

   }

   public void setColor(int color) {
      this.color = color;
   }

   public void setHoverColor(int hoverColor) {
      this.hoverColor = hoverColor;
   }

   public void setText(String text) {
      this.text = text;
   }
}
