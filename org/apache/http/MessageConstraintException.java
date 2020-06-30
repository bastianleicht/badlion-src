package org.apache.http;

import java.io.IOException;

public class MessageConstraintException extends IOException {
   private static final long serialVersionUID = 6077207720446368695L;

   public MessageConstraintException(String message) {
      super(message);
   }
}
