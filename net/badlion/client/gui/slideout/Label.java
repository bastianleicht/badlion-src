package net.badlion.client.gui.slideout;

import net.badlion.client.Wrapper;
import net.badlion.client.gui.BadlionFontRenderer;
import net.badlion.client.gui.slideout.RenderElement;
import net.badlion.client.gui.slideout.fontrenderer.CustomFontRenderer;
import net.badlion.client.util.ColorUtil;
import org.lwjgl.opengl.GL11;

public class Label extends RenderElement {
   private CustomFontRenderer fontRenderer;
   private String text;
   private int color;
   private boolean lineBreak;
   private int fontSize;
   private BadlionFontRenderer.FontType fontType;

   public Label(String text, int color, int fontSize, BadlionFontRenderer.FontType fontType) {
      this(text, color, fontSize, fontType, false);
   }

   public Label(String text, int color, int fontSize, BadlionFontRenderer.FontType fontType, boolean lineBreak) {
      this.lineBreak = false;
      this.fontSize = 12;
      this.text = text;
      this.color = color;
      this.fontSize = fontSize;
      this.lineBreak = lineBreak;
      this.fontType = fontType;
   }

   public String getText() {
      return this.text;
   }

   public int getColor() {
      return this.color;
   }

   public boolean isLineBreak() {
      return this.lineBreak;
   }

   public void init() {
      this.fontRenderer = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getFontRenderer();
      super.init();
   }

   public void render() {
      int i = Wrapper.getInstance().getActiveModProfile().getSlideoutAccess().getSlideoutInstance().getSlideoutWidth();
      if(this.lineBreak) {
         this.fontRenderer.drawString(this.text, this.getX() + 49, this.getY(), this.fontSize, this.fontType, this.color);
         GL11.glDisable(3553);
         GL11.glLineWidth(1.0F);
         GL11.glBegin(1);
         ColorUtil.bindHexColorRGBA(this.color);
         GL11.glVertex2f((float)(this.getX() - 49 + 49), (float)(this.getY() + this.getHeight() / 2 + 1));
         GL11.glVertex2f((float)(this.getX() - 5 + 49), (float)(this.getY() + this.getHeight() / 2 + 1));
         GL11.glVertex2f((float)(this.getX() + 5 + 49 + this.getLocalWidth()), (float)(this.getY() + this.getHeight() / 2 + 1));
         GL11.glVertex2f((float)(this.getX() + this.getLocalWidth() + 5 + 45 + 49), (float)(this.getY() + this.getHeight() / 2 + 1));
         GL11.glEnd();
         GL11.glEnable(3553);
      } else {
         this.fontRenderer.drawString(this.text, this.getX(), this.getY(), this.fontSize, this.fontType, this.color);
      }

   }

   private int getLocalWidth() {
      return (int)((double)this.fontRenderer.getStringWidth(this.text, this.fontSize, this.fontType));
   }

   public void update(int mx, int my) {
      super.update(mx, my);
   }

   public int getWidth() {
      return this.fontRenderer == null?0:(this.lineBreak?this.getX() + this.getLocalWidth() + 5 + 45 + 49 - this.getX():(int)((double)this.fontRenderer.getStringWidth(this.text, this.fontSize, this.fontType)));
   }

   public int getHeight() {
      return 10;
   }
}
