package org.lwjgl.openal;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.ALC10;
import org.lwjgl.openal.ALCcontext;
import org.lwjgl.openal.ALCdevice;
import org.lwjgl.openal.OpenALException;

public final class Util {
   public static void checkALCError(ALCdevice device) {
      int err = ALC10.alcGetError(device);
      if(err != 0) {
         throw new OpenALException(ALC10.alcGetString(AL.getDevice(), err));
      }
   }

   public static void checkALError() {
      int err = AL10.alGetError();
      if(err != 0) {
         throw new OpenALException(err);
      }
   }

   public static void checkALCValidDevice(ALCdevice device) {
      if(!device.isValid()) {
         throw new OpenALException("Invalid device: " + device);
      }
   }

   public static void checkALCValidContext(ALCcontext context) {
      if(!context.isValid()) {
         throw new OpenALException("Invalid context: " + context);
      }
   }
}
