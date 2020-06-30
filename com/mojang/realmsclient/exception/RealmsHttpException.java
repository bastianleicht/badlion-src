package com.mojang.realmsclient.exception;

public class RealmsHttpException extends RuntimeException {
   public RealmsHttpException(String s, Exception e) {
      super(s, e);
   }
}
