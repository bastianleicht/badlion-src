package org.lwjgl.util.glu;

import java.nio.ByteBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.PixelStoreState;
import org.lwjgl.util.glu.Util;

public class MipMap extends Util {
   public static int gluBuild2DMipmaps(int target, int components, int width, int height, int format, int type, ByteBuffer data) {
      if(width >= 1 && height >= 1) {
         int bpp = bytesPerPixel(format, type);
         if(bpp == 0) {
            return 100900;
         } else {
            int maxSize = glGetIntegerv(3379);
            int w = nearestPower(width);
            if(w > maxSize) {
               w = maxSize;
            }

            int h = nearestPower(height);
            if(h > maxSize) {
               h = maxSize;
            }

            PixelStoreState pss = new PixelStoreState();
            GL11.glPixelStorei(3330, 0);
            GL11.glPixelStorei(3333, 1);
            GL11.glPixelStorei(3331, 0);
            GL11.glPixelStorei(3332, 0);
            int retVal = 0;
            boolean done = false;
            ByteBuffer image;
            if(w == width && h == height) {
               image = data;
            } else {
               image = BufferUtils.createByteBuffer((w + 4) * h * bpp);
               int error = gluScaleImage(format, width, height, type, data, w, h, type, image);
               if(error != 0) {
                  retVal = error;
                  done = true;
               }

               GL11.glPixelStorei(3314, 0);
               GL11.glPixelStorei(3317, 1);
               GL11.glPixelStorei(3315, 0);
               GL11.glPixelStorei(3316, 0);
            }

            ByteBuffer bufferA = null;
            ByteBuffer bufferB = null;

            for(int level = 0; !done; ++level) {
               if(image != data) {
                  GL11.glPixelStorei(3314, 0);
                  GL11.glPixelStorei(3317, 1);
                  GL11.glPixelStorei(3315, 0);
                  GL11.glPixelStorei(3316, 0);
               }

               GL11.glTexImage2D(target, level, components, w, h, 0, format, type, (ByteBuffer)image);
               if(w == 1 && h == 1) {
                  break;
               }

               int newW = w < 2?1:w >> 1;
               int newH = h < 2?1:h >> 1;
               ByteBuffer newImage;
               if(bufferA == null) {
                  newImage = bufferA = BufferUtils.createByteBuffer((newW + 4) * newH * bpp);
               } else if(bufferB == null) {
                  newImage = bufferB = BufferUtils.createByteBuffer((newW + 4) * newH * bpp);
               } else {
                  newImage = bufferB;
               }

               int error = gluScaleImage(format, w, h, type, image, newW, newH, type, newImage);
               if(error != 0) {
                  retVal = error;
                  done = true;
               }

               image = newImage;
               if(bufferB != null) {
                  bufferB = bufferA;
               }

               w = newW;
               h = newH;
            }

            pss.save();
            return retVal;
         }
      } else {
         return 100901;
      }
   }

   public static int gluScaleImage(int format, int widthIn, int heightIn, int typein, ByteBuffer dataIn, int widthOut, int heightOut, int typeOut, ByteBuffer dataOut) {
      int components = compPerPix(format);
      if(components == -1) {
         return 100900;
      } else {
         float[] tempIn = new float[widthIn * heightIn * components];
         float[] tempOut = new float[widthOut * heightOut * components];
         int sizein;
         switch(typein) {
         case 5121:
            sizein = 1;
            break;
         case 5126:
            sizein = 4;
            break;
         default:
            return 1280;
         }

         int sizeout;
         switch(typeOut) {
         case 5121:
            sizeout = 1;
            break;
         case 5126:
            sizeout = 4;
            break;
         default:
            return 1280;
         }

         PixelStoreState pss = new PixelStoreState();
         int rowlen;
         if(pss.unpackRowLength > 0) {
            rowlen = pss.unpackRowLength;
         } else {
            rowlen = widthIn;
         }

         int rowstride;
         if(sizein >= pss.unpackAlignment) {
            rowstride = components * rowlen;
         } else {
            rowstride = pss.unpackAlignment / sizein * ceil(components * rowlen * sizein, pss.unpackAlignment);
         }

         label201:
         switch(typein) {
         case 5121:
            int k = 0;
            dataIn.rewind();
            int i = 0;

            while(true) {
               if(i >= heightIn) {
                  break label201;
               }

               int ubptr = i * rowstride + pss.unpackSkipRows * rowstride + pss.unpackSkipPixels * components;

               for(int j = 0; j < widthIn * components; ++j) {
                  tempIn[k++] = (float)(dataIn.get(ubptr++) & 255);
               }

               ++i;
            }
         case 5126:
            int k = 0;
            dataIn.rewind();
            int i = 0;

            while(true) {
               if(i >= heightIn) {
                  break label201;
               }

               int fptr = 4 * (i * rowstride + pss.unpackSkipRows * rowstride + pss.unpackSkipPixels * components);

               for(int j = 0; j < widthIn * components; ++j) {
                  tempIn[k++] = dataIn.getFloat(fptr);
                  fptr += 4;
               }

               ++i;
            }
         default:
            return 100900;
         }

         float sx = (float)widthIn / (float)widthOut;
         float sy = (float)heightIn / (float)heightOut;
         float[] c = new float[components];

         for(int iy = 0; iy < heightOut; ++iy) {
            for(int ix = 0; ix < widthOut; ++ix) {
               int x0 = (int)((float)ix * sx);
               int x1 = (int)((float)(ix + 1) * sx);
               int y0 = (int)((float)iy * sy);
               int y1 = (int)((float)(iy + 1) * sy);
               int readPix = 0;

               for(int ic = 0; ic < components; ++ic) {
                  c[ic] = 0.0F;
               }

               for(int ix0 = x0; ix0 < x1; ++ix0) {
                  for(int iy0 = y0; iy0 < y1; ++iy0) {
                     int src = (iy0 * widthIn + ix0) * components;

                     for(int ic = 0; ic < components; ++ic) {
                        c[ic] += tempIn[src + ic];
                     }

                     ++readPix;
                  }
               }

               int dst = (iy * widthOut + ix) * components;
               if(readPix == 0) {
                  int src = (y0 * widthIn + x0) * components;

                  for(int ic = 0; ic < components; ++ic) {
                     tempOut[dst++] = tempIn[src + ic];
                  }
               } else {
                  for(int var42 = 0; var42 < components; ++var42) {
                     tempOut[dst++] = c[var42] / (float)readPix;
                  }
               }
            }
         }

         if(pss.packRowLength > 0) {
            rowlen = pss.packRowLength;
         } else {
            rowlen = widthOut;
         }

         if(sizeout >= pss.packAlignment) {
            rowstride = components * rowlen;
         } else {
            rowstride = pss.packAlignment / sizeout * ceil(components * rowlen * sizeout, pss.packAlignment);
         }

         switch(typeOut) {
         case 5121:
            int var44 = 0;

            for(int var37 = 0; var37 < heightOut; ++var37) {
               int ubptr = var37 * rowstride + pss.packSkipRows * rowstride + pss.packSkipPixels * components;

               for(int var40 = 0; var40 < widthOut * components; ++var40) {
                  dataOut.put(ubptr++, (byte)((int)tempOut[var44++]));
               }
            }

            return 0;
         case 5126:
            int var43 = 0;

            for(int var36 = 0; var36 < heightOut; ++var36) {
               int fptr = 4 * (var36 * rowstride + pss.unpackSkipRows * rowstride + pss.unpackSkipPixels * components);

               for(int var39 = 0; var39 < widthOut * components; ++var39) {
                  dataOut.putFloat(fptr, tempOut[var43++]);
                  fptr += 4;
               }
            }

            return 0;
         default:
            return 100900;
         }
      }
   }
}
