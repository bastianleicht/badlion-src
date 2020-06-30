package com.mojang.authlib.exceptions;

import com.mojang.authlib.exceptions.AuthenticationException;

public class AuthenticationUnavailableException extends AuthenticationException {
   public AuthenticationUnavailableException() {
   }

   public AuthenticationUnavailableException(String message) {
      super(message);
   }

   public AuthenticationUnavailableException(String message, Throwable cause) {
      super(message, cause);
   }

   public AuthenticationUnavailableException(Throwable cause) {
      super(cause);
   }
}
