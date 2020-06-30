package org.lwjgl.util.glu;

import org.lwjgl.opengl.GL11;

public class Quadric {
   protected int drawStyle = 100012;
   protected int orientation = 100020;
   protected boolean textureFlag = false;
   protected int normals = 100000;

   protected void normal3f(float x, float y, float z) {
      float mag = (float)Math.sqrt((double)(x * x + y * y + z * z));
      if(mag > 1.0E-5F) {
         x /= mag;
         y /= mag;
         z /= mag;
      }

      GL11.glNormal3f(x, y, z);
   }

   public void setDrawStyle(int drawStyle) {
      this.drawStyle = drawStyle;
   }

   public void setNormals(int normals) {
      this.normals = normals;
   }

   public void setOrientation(int orientation) {
      this.orientation = orientation;
   }

   public void setTextureFlag(boolean textureFlag) {
      this.textureFlag = textureFlag;
   }

   public int getDrawStyle() {
      return this.drawStyle;
   }

   public int getNormals() {
      return this.normals;
   }

   public int getOrientation() {
      return this.orientation;
   }

   public boolean getTextureFlag() {
      return this.textureFlag;
   }

   protected void TXTR_COORD(float x, float y) {
      if(this.textureFlag) {
         GL11.glTexCoord2f(x, y);
      }

   }

   protected float sin(float r) {
      return (float)Math.sin((double)r);
   }

   protected float cos(float r) {
      return (float)Math.cos((double)r);
   }
}
