package org.lwjgl.util;

import java.io.Serializable;
import java.nio.ByteBuffer;
import org.lwjgl.util.ReadableColor;
import org.lwjgl.util.WritableColor;

public final class Color implements ReadableColor, Serializable, WritableColor {
   static final long serialVersionUID = 1L;
   private byte red;
   private byte green;
   private byte blue;
   private byte alpha;

   public Color() {
      this((int)0, (int)0, (int)0, (int)255);
   }

   public Color(int r, int g, int b) {
      this((int)r, (int)g, (int)b, (int)255);
   }

   public Color(byte r, byte g, byte b) {
      this(r, g, b, (byte)-1);
   }

   public Color(int r, int g, int b, int a) {
      this.set(r, g, b, a);
   }

   public Color(byte r, byte g, byte b, byte a) {
      this.set(r, g, b, a);
   }

   public Color(ReadableColor c) {
      this.setColor(c);
   }

   public void set(int r, int g, int b, int a) {
      this.red = (byte)r;
      this.green = (byte)g;
      this.blue = (byte)b;
      this.alpha = (byte)a;
   }

   public void set(byte r, byte g, byte b, byte a) {
      this.red = r;
      this.green = g;
      this.blue = b;
      this.alpha = a;
   }

   public void set(int r, int g, int b) {
      this.set((int)r, (int)g, (int)b, (int)255);
   }

   public void set(byte r, byte g, byte b) {
      this.set(r, g, b, (byte)-1);
   }

   public int getRed() {
      return this.red & 255;
   }

   public int getGreen() {
      return this.green & 255;
   }

   public int getBlue() {
      return this.blue & 255;
   }

   public int getAlpha() {
      return this.alpha & 255;
   }

   public void setRed(int red) {
      this.red = (byte)red;
   }

   public void setGreen(int green) {
      this.green = (byte)green;
   }

   public void setBlue(int blue) {
      this.blue = (byte)blue;
   }

   public void setAlpha(int alpha) {
      this.alpha = (byte)alpha;
   }

   public void setRed(byte red) {
      this.red = red;
   }

   public void setGreen(byte green) {
      this.green = green;
   }

   public void setBlue(byte blue) {
      this.blue = blue;
   }

   public void setAlpha(byte alpha) {
      this.alpha = alpha;
   }

   public String toString() {
      return "Color [" + this.getRed() + ", " + this.getGreen() + ", " + this.getBlue() + ", " + this.getAlpha() + "]";
   }

   public boolean equals(Object o) {
      return o != null && o instanceof ReadableColor && ((ReadableColor)o).getRed() == this.getRed() && ((ReadableColor)o).getGreen() == this.getGreen() && ((ReadableColor)o).getBlue() == this.getBlue() && ((ReadableColor)o).getAlpha() == this.getAlpha();
   }

   public int hashCode() {
      return this.red << 24 | this.green << 16 | this.blue << 8 | this.alpha;
   }

   public byte getAlphaByte() {
      return this.alpha;
   }

   public byte getBlueByte() {
      return this.blue;
   }

   public byte getGreenByte() {
      return this.green;
   }

   public byte getRedByte() {
      return this.red;
   }

   public void writeRGBA(ByteBuffer dest) {
      dest.put(this.red);
      dest.put(this.green);
      dest.put(this.blue);
      dest.put(this.alpha);
   }

   public void writeRGB(ByteBuffer dest) {
      dest.put(this.red);
      dest.put(this.green);
      dest.put(this.blue);
   }

   public void writeABGR(ByteBuffer dest) {
      dest.put(this.alpha);
      dest.put(this.blue);
      dest.put(this.green);
      dest.put(this.red);
   }

   public void writeARGB(ByteBuffer dest) {
      dest.put(this.alpha);
      dest.put(this.red);
      dest.put(this.green);
      dest.put(this.blue);
   }

   public void writeBGR(ByteBuffer dest) {
      dest.put(this.blue);
      dest.put(this.green);
      dest.put(this.red);
   }

   public void writeBGRA(ByteBuffer dest) {
      dest.put(this.blue);
      dest.put(this.green);
      dest.put(this.red);
      dest.put(this.alpha);
   }

   public void readRGBA(ByteBuffer src) {
      this.red = src.get();
      this.green = src.get();
      this.blue = src.get();
      this.alpha = src.get();
   }

   public void readRGB(ByteBuffer src) {
      this.red = src.get();
      this.green = src.get();
      this.blue = src.get();
   }

   public void readARGB(ByteBuffer src) {
      this.alpha = src.get();
      this.red = src.get();
      this.green = src.get();
      this.blue = src.get();
   }

   public void readBGRA(ByteBuffer src) {
      this.blue = src.get();
      this.green = src.get();
      this.red = src.get();
      this.alpha = src.get();
   }

   public void readBGR(ByteBuffer src) {
      this.blue = src.get();
      this.green = src.get();
      this.red = src.get();
   }

   public void readABGR(ByteBuffer src) {
      this.alpha = src.get();
      this.blue = src.get();
      this.green = src.get();
      this.red = src.get();
   }

   public void setColor(ReadableColor src) {
      this.red = src.getRedByte();
      this.green = src.getGreenByte();
      this.blue = src.getBlueByte();
      this.alpha = src.getAlphaByte();
   }

   public void fromHSB(float hue, float saturation, float brightness) {
      if(saturation == 0.0F) {
         this.red = this.green = this.blue = (byte)((int)(brightness * 255.0F + 0.5F));
      } else {
         float f3 = (hue - (float)Math.floor((double)hue)) * 6.0F;
         float f4 = f3 - (float)Math.floor((double)f3);
         float f5 = brightness * (1.0F - saturation);
         float f6 = brightness * (1.0F - saturation * f4);
         float f7 = brightness * (1.0F - saturation * (1.0F - f4));
         switch((int)f3) {
         case 0:
            this.red = (byte)((int)(brightness * 255.0F + 0.5F));
            this.green = (byte)((int)(f7 * 255.0F + 0.5F));
            this.blue = (byte)((int)(f5 * 255.0F + 0.5F));
            break;
         case 1:
            this.red = (byte)((int)(f6 * 255.0F + 0.5F));
            this.green = (byte)((int)(brightness * 255.0F + 0.5F));
            this.blue = (byte)((int)(f5 * 255.0F + 0.5F));
            break;
         case 2:
            this.red = (byte)((int)(f5 * 255.0F + 0.5F));
            this.green = (byte)((int)(brightness * 255.0F + 0.5F));
            this.blue = (byte)((int)(f7 * 255.0F + 0.5F));
            break;
         case 3:
            this.red = (byte)((int)(f5 * 255.0F + 0.5F));
            this.green = (byte)((int)(f6 * 255.0F + 0.5F));
            this.blue = (byte)((int)(brightness * 255.0F + 0.5F));
            break;
         case 4:
            this.red = (byte)((int)(f7 * 255.0F + 0.5F));
            this.green = (byte)((int)(f5 * 255.0F + 0.5F));
            this.blue = (byte)((int)(brightness * 255.0F + 0.5F));
            break;
         case 5:
            this.red = (byte)((int)(brightness * 255.0F + 0.5F));
            this.green = (byte)((int)(f5 * 255.0F + 0.5F));
            this.blue = (byte)((int)(f6 * 255.0F + 0.5F));
         }
      }

   }

   public float[] toHSB(float[] dest) {
      int r = this.getRed();
      int g = this.getGreen();
      int b = this.getBlue();
      if(dest == null) {
         dest = new float[3];
      }

      int l = r <= g?g:r;
      if(b > l) {
         l = b;
      }

      int i1 = r >= g?g:r;
      if(b < i1) {
         i1 = b;
      }

      float brightness = (float)l / 255.0F;
      float saturation;
      if(l != 0) {
         saturation = (float)(l - i1) / (float)l;
      } else {
         saturation = 0.0F;
      }

      float hue;
      if(saturation == 0.0F) {
         hue = 0.0F;
      } else {
         float f3 = (float)(l - r) / (float)(l - i1);
         float f4 = (float)(l - g) / (float)(l - i1);
         float f5 = (float)(l - b) / (float)(l - i1);
         if(r == l) {
            hue = f5 - f4;
         } else if(g == l) {
            hue = 2.0F + f3 - f5;
         } else {
            hue = 4.0F + f4 - f3;
         }

         hue = hue / 6.0F;
         if(hue < 0.0F) {
            ++hue;
         }
      }

      dest[0] = hue;
      dest[1] = saturation;
      dest[2] = brightness;
      return dest;
   }
}
