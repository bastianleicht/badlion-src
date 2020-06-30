package net.badlion.client.gui.slideout;

public class RenderElement {
   protected int x;
   protected int y;
   private int optionalOffsetX;
   private int optionalOffsetY;
   protected int zLevel = -1;
   protected boolean ignoreAutoPos;
   protected int ignoreAutoPosPage;

   public void ignoreAutoPos(int page) {
      this.ignoreAutoPos = true;
      this.ignoreAutoPosPage = page;
   }

   public boolean isAutoPosIgnored() {
      return this.ignoreAutoPos;
   }

   public void update(int mouseX, int mouseY) {
   }

   public void render() {
   }

   public void init() {
   }

   public void setPosition(int x, int y) {
      this.x = x;
      this.y = y;
   }

   public void setOptionalOffset(int xO, int yO) {
      this.optionalOffsetX = xO;
      this.optionalOffsetY = yO;
   }

   public boolean onClick(int mouseButton) {
      return false;
   }

   public void keyTyped(char character, int keyCode) {
   }

   public int getX() {
      return this.x + this.optionalOffsetX;
   }

   public int getY() {
      return this.y + this.optionalOffsetY;
   }

   public int getWidth() {
      return -1;
   }

   public int getHeight() {
      return -1;
   }

   public int getZLevel() {
      return this.zLevel;
   }
}
