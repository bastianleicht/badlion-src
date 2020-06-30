package com.mojang.realmsclient.exception;

import com.mojang.realmsclient.exception.RealmsServiceException;

public class RetryCallException extends RealmsServiceException {
   public static final int DEFAULT_DELAY = 5;
   public final int delaySeconds;

   public RetryCallException(int delaySeconds) {
      super(503, "Retry operation", -1, "");
      if(delaySeconds >= 0 && delaySeconds <= 120) {
         this.delaySeconds = delaySeconds;
      } else {
         this.delaySeconds = 5;
      }

   }
}
