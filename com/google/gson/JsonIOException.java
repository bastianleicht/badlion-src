package com.google.gson;

import com.google.gson.JsonParseException;

public final class JsonIOException extends JsonParseException {
   private static final long serialVersionUID = 1L;

   public JsonIOException(String msg) {
      super(msg);
   }

   public JsonIOException(String msg, Throwable cause) {
      super(msg, cause);
   }

   public JsonIOException(Throwable cause) {
      super(cause);
   }
}
