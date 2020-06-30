package net.badlion.client.mods.render.gui;

import net.badlion.client.Wrapper;
import org.apache.logging.log4j.LogManager;

public class BoxedCoord implements Cloneable {
   private int xBox;
   private int yBox;
   private double xOffset;
   private double yOffset;

   public BoxedCoord(BoxedCoord boxedCoord) {
      this.xBox = boxedCoord.xBox;
      this.yBox = boxedCoord.yBox;
      this.xOffset = boxedCoord.xOffset;
      this.yOffset = boxedCoord.yOffset;
   }

   public BoxedCoord(int xBox, int yBox, double xOffset, double yOffset) {
      this.xBox = xBox;
      this.yBox = yBox;
      this.xOffset = xOffset;
      this.yOffset = yOffset;
   }

   public int getxBox() {
      return this.xBox;
   }

   public void setxBox(int xBox) {
      this.xBox = xBox;
   }

   public int getyBox() {
      return this.yBox;
   }

   public void setyBox(int yBox) {
      this.yBox = yBox;
   }

   public double getxOffset() {
      return this.xOffset;
   }

   public void setxOffset(double xOffset) {
      this.xOffset = xOffset;
   }

   public double getyOffset() {
      return this.yOffset;
   }

   public void setyOffset(double yOffset) {
      this.yOffset = yOffset;
   }

   public void setPosition(int xBox, int yBox, double xOffset, double yOffset) {
      this.xBox = xBox;
      this.yBox = yBox;
      this.xOffset = xOffset;
      this.yOffset = yOffset;
   }

   public int toXPos() {
      double d0 = Wrapper.getInstance().getActiveModProfile().getModConfigurator().getBoxWidth();
      return (int)((double)this.xBox * d0 + d0 * this.xOffset);
   }

   public int toYPos() {
      double d0 = Wrapper.getInstance().getActiveModProfile().getModConfigurator().getBoxHeight();
      return (int)((double)this.yBox * d0 + d0 * this.yOffset);
   }

   public int toXPos(double boxWidth) {
      return (int)((double)this.xBox * boxWidth + boxWidth * this.xOffset);
   }

   public int toYPos(double boxHeight) {
      return (int)((double)this.yBox * boxHeight + boxHeight * this.yOffset);
   }

   public BoxedCoord clone() {
      try {
         return (BoxedCoord)super.clone();
      } catch (CloneNotSupportedException var2) {
         LogManager.getLogger().catching(var2);
         return new BoxedCoord(this);
      }
   }
}
