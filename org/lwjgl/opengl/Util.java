package org.lwjgl.opengl;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.OpenGLException;

public final class Util {
   public static void checkGLError() throws OpenGLException {
      int err = GL11.glGetError();
      if(err != 0) {
         throw new OpenGLException(err);
      }
   }

   public static String translateGLErrorString(int error_code) {
      switch(error_code) {
      case 0:
         return "No error";
      case 1280:
         return "Invalid enum";
      case 1281:
         return "Invalid value";
      case 1282:
         return "Invalid operation";
      case 1283:
         return "Stack overflow";
      case 1284:
         return "Stack underflow";
      case 1285:
         return "Out of memory";
      case 1286:
         return "Invalid framebuffer operation";
      case 32817:
         return "Table too large";
      default:
         return null;
      }
   }
}
