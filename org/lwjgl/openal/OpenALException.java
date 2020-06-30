package org.lwjgl.openal;

import org.lwjgl.openal.AL10;

public class OpenALException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public OpenALException() {
   }

   public OpenALException(int error_code) {
      super("OpenAL error: " + AL10.alGetString(error_code) + " (" + error_code + ")");
   }

   public OpenALException(String message) {
      super(message);
   }

   public OpenALException(String message, Throwable cause) {
      super(message, cause);
   }

   public OpenALException(Throwable cause) {
      super(cause);
   }
}
