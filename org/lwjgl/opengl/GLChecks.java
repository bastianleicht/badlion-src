package org.lwjgl.opengl;

import java.nio.Buffer;
import java.nio.FloatBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.StateTracker;

class GLChecks {
   static void ensureArrayVBOdisabled(ContextCapabilities caps) {
      if(LWJGLUtil.CHECKS && StateTracker.getReferences(caps).arrayBuffer != 0) {
         throw new OpenGLException("Cannot use Buffers when Array Buffer Object is enabled");
      }
   }

   static void ensureArrayVBOenabled(ContextCapabilities caps) {
      if(LWJGLUtil.CHECKS && StateTracker.getReferences(caps).arrayBuffer == 0) {
         throw new OpenGLException("Cannot use offsets when Array Buffer Object is disabled");
      }
   }

   static void ensureElementVBOdisabled(ContextCapabilities caps) {
      if(LWJGLUtil.CHECKS && StateTracker.getElementArrayBufferBound(caps) != 0) {
         throw new OpenGLException("Cannot use Buffers when Element Array Buffer Object is enabled");
      }
   }

   static void ensureElementVBOenabled(ContextCapabilities caps) {
      if(LWJGLUtil.CHECKS && StateTracker.getElementArrayBufferBound(caps) == 0) {
         throw new OpenGLException("Cannot use offsets when Element Array Buffer Object is disabled");
      }
   }

   static void ensureIndirectBOdisabled(ContextCapabilities caps) {
      if(LWJGLUtil.CHECKS && StateTracker.getReferences(caps).indirectBuffer != 0) {
         throw new OpenGLException("Cannot use Buffers when Draw Indirect Object is enabled");
      }
   }

   static void ensureIndirectBOenabled(ContextCapabilities caps) {
      if(LWJGLUtil.CHECKS && StateTracker.getReferences(caps).indirectBuffer == 0) {
         throw new OpenGLException("Cannot use offsets when Draw Indirect Object is disabled");
      }
   }

   static void ensurePackPBOdisabled(ContextCapabilities caps) {
      if(LWJGLUtil.CHECKS && StateTracker.getReferences(caps).pixelPackBuffer != 0) {
         throw new OpenGLException("Cannot use Buffers when Pixel Pack Buffer Object is enabled");
      }
   }

   static void ensurePackPBOenabled(ContextCapabilities caps) {
      if(LWJGLUtil.CHECKS && StateTracker.getReferences(caps).pixelPackBuffer == 0) {
         throw new OpenGLException("Cannot use offsets when Pixel Pack Buffer Object is disabled");
      }
   }

   static void ensureUnpackPBOdisabled(ContextCapabilities caps) {
      if(LWJGLUtil.CHECKS && StateTracker.getReferences(caps).pixelUnpackBuffer != 0) {
         throw new OpenGLException("Cannot use Buffers when Pixel Unpack Buffer Object is enabled");
      }
   }

   static void ensureUnpackPBOenabled(ContextCapabilities caps) {
      if(LWJGLUtil.CHECKS && StateTracker.getReferences(caps).pixelUnpackBuffer == 0) {
         throw new OpenGLException("Cannot use offsets when Pixel Unpack Buffer Object is disabled");
      }
   }

   static int calculateImageStorage(Buffer buffer, int format, int type, int width, int height, int depth) {
      return LWJGLUtil.CHECKS?calculateImageStorage(format, type, width, height, depth) >> BufferUtils.getElementSizeExponent(buffer):0;
   }

   static int calculateTexImage1DStorage(Buffer buffer, int format, int type, int width) {
      return LWJGLUtil.CHECKS?calculateTexImage1DStorage(format, type, width) >> BufferUtils.getElementSizeExponent(buffer):0;
   }

   static int calculateTexImage2DStorage(Buffer buffer, int format, int type, int width, int height) {
      return LWJGLUtil.CHECKS?calculateTexImage2DStorage(format, type, width, height) >> BufferUtils.getElementSizeExponent(buffer):0;
   }

   static int calculateTexImage3DStorage(Buffer buffer, int format, int type, int width, int height, int depth) {
      return LWJGLUtil.CHECKS?calculateTexImage3DStorage(format, type, width, height, depth) >> BufferUtils.getElementSizeExponent(buffer):0;
   }

   private static int calculateImageStorage(int format, int type, int width, int height, int depth) {
      return calculateBytesPerPixel(format, type) * width * height * depth;
   }

   private static int calculateTexImage1DStorage(int format, int type, int width) {
      return calculateBytesPerPixel(format, type) * width;
   }

   private static int calculateTexImage2DStorage(int format, int type, int width, int height) {
      return calculateTexImage1DStorage(format, type, width) * height;
   }

   private static int calculateTexImage3DStorage(int format, int type, int width, int height, int depth) {
      return calculateTexImage2DStorage(format, type, width, height) * depth;
   }

   private static int calculateBytesPerPixel(int format, int type) {
      int bpe;
      switch(type) {
      case 5120:
      case 5121:
         bpe = 1;
         break;
      case 5122:
      case 5123:
         bpe = 2;
         break;
      case 5124:
      case 5125:
      case 5126:
         bpe = 4;
         break;
      default:
         return 0;
      }

      int epp;
      switch(format) {
      case 6406:
      case 6409:
         epp = 1;
         break;
      case 6407:
      case 32992:
         epp = 3;
         break;
      case 6408:
      case 32768:
      case 32993:
         epp = 4;
         break;
      case 6410:
         epp = 2;
         break;
      default:
         return 0;
      }

      return bpe * epp;
   }

   static int calculateBytesPerCharCode(int type) {
      switch(type) {
      case 5121:
      case 37018:
         return 1;
      case 5123:
      case 5127:
      case 37019:
         return 2;
      case 5128:
         return 3;
      case 5129:
         return 4;
      default:
         throw new IllegalArgumentException("Unsupported charcode type: " + type);
      }
   }

   static int calculateBytesPerPathName(int pathNameType) {
      switch(pathNameType) {
      case 5120:
      case 5121:
      case 37018:
         return 1;
      case 5122:
      case 5123:
      case 5127:
      case 37019:
         return 2;
      case 5124:
      case 5125:
      case 5126:
      case 5129:
         return 4;
      case 5128:
         return 3;
      default:
         throw new IllegalArgumentException("Unsupported path name type: " + pathNameType);
      }
   }

   static int calculateTransformPathValues(int transformType) {
      switch(transformType) {
      case 0:
         return 0;
      case 37006:
      case 37007:
         return 1;
      case 37008:
         return 2;
      case 37009:
         return 3;
      case 37010:
      case 37014:
         return 6;
      case 37012:
      case 37016:
         return 12;
      default:
         throw new IllegalArgumentException("Unsupported transform type: " + transformType);
      }
   }

   static int calculatePathColorGenCoeffsCount(int genMode, int colorFormat) {
      int coeffsPerComponent = calculatePathGenCoeffsPerComponent(genMode);
      switch(colorFormat) {
      case 6407:
         return 3 * coeffsPerComponent;
      case 6408:
         return 4 * coeffsPerComponent;
      default:
         return coeffsPerComponent;
      }
   }

   static int calculatePathTextGenCoeffsPerComponent(FloatBuffer coeffs, int genMode) {
      return genMode == 0?0:coeffs.remaining() / calculatePathGenCoeffsPerComponent(genMode);
   }

   private static int calculatePathGenCoeffsPerComponent(int genMode) {
      switch(genMode) {
      case 0:
         return 0;
      case 9216:
         return 4;
      case 9217:
      case 37002:
         return 3;
      default:
         throw new IllegalArgumentException("Unsupported gen mode: " + genMode);
      }
   }

   static int calculateMetricsSize(int metricQueryMask, int stride) {
      if(!LWJGLUtil.DEBUG || stride >= 0 && stride % 4 == 0) {
         int metrics = Integer.bitCount(metricQueryMask);
         if(LWJGLUtil.DEBUG && stride >> 2 < metrics) {
            throw new IllegalArgumentException("The queried metrics do not fit in the specified stride: " + stride);
         } else {
            return stride == 0?metrics:stride >> 2;
         }
      } else {
         throw new IllegalArgumentException("Invalid stride value: " + stride);
      }
   }
}
