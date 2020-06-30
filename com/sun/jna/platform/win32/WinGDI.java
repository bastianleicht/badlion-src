package com.sun.jna.platform.win32;

import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.StdCallLibrary;

public interface WinGDI extends StdCallLibrary {
   int RDH_RECTANGLES = 1;
   int RGN_AND = 1;
   int RGN_OR = 2;
   int RGN_XOR = 3;
   int RGN_DIFF = 4;
   int RGN_COPY = 5;
   int ERROR = 0;
   int NULLREGION = 1;
   int SIMPLEREGION = 2;
   int COMPLEXREGION = 3;
   int ALTERNATE = 1;
   int WINDING = 2;
   int BI_RGB = 0;
   int BI_RLE8 = 1;
   int BI_RLE4 = 2;
   int BI_BITFIELDS = 3;
   int BI_JPEG = 4;
   int BI_PNG = 5;
   int DIB_RGB_COLORS = 0;
   int DIB_PAL_COLORS = 1;

   public static class BITMAPINFO extends Structure {
      public WinGDI.BITMAPINFOHEADER bmiHeader;
      public WinGDI.RGBQUAD[] bmiColors;

      public BITMAPINFO() {
         this(1);
      }

      public BITMAPINFO(int size) {
         this.bmiHeader = new WinGDI.BITMAPINFOHEADER();
         this.bmiColors = new WinGDI.RGBQUAD[1];
         this.bmiColors = new WinGDI.RGBQUAD[size];
      }
   }

   public static class BITMAPINFOHEADER extends Structure {
      public int biSize = this.size();
      public int biWidth;
      public int biHeight;
      public short biPlanes;
      public short biBitCount;
      public int biCompression;
      public int biSizeImage;
      public int biXPelsPerMeter;
      public int biYPelsPerMeter;
      public int biClrUsed;
      public int biClrImportant;
   }

   public static class RGBQUAD extends Structure {
      public byte rgbBlue;
      public byte rgbGreen;
      public byte rgbRed;
      public byte rgbReserved = 0;
   }

   public static class RGNDATA extends Structure {
      public WinGDI.RGNDATAHEADER rdh;
      public byte[] Buffer;

      public RGNDATA(int bufferSize) {
         this.Buffer = new byte[bufferSize];
         this.allocateMemory();
      }
   }

   public static class RGNDATAHEADER extends Structure {
      public int dwSize = this.size();
      public int iType = 1;
      public int nCount;
      public int nRgnSize;
      public WinDef.RECT rcBound;
   }
}
