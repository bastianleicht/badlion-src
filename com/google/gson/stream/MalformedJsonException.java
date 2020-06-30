package com.google.gson.stream;

import java.io.IOException;

public final class MalformedJsonException extends IOException {
   private static final long serialVersionUID = 1L;

   public MalformedJsonException(String msg) {
      super(msg);
   }

   public MalformedJsonException(String msg, Throwable throwable) {
      super(msg);
      this.initCause(throwable);
   }

   public MalformedJsonException(Throwable throwable) {
      this.initCause(throwable);
   }
}
