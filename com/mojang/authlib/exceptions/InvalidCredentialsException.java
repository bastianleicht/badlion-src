package com.mojang.authlib.exceptions;

import com.mojang.authlib.exceptions.AuthenticationException;

public class InvalidCredentialsException extends AuthenticationException {
   public InvalidCredentialsException() {
   }

   public InvalidCredentialsException(String message) {
      super(message);
   }

   public InvalidCredentialsException(String message, Throwable cause) {
      super(message, cause);
   }

   public InvalidCredentialsException(Throwable cause) {
      super(cause);
   }
}
