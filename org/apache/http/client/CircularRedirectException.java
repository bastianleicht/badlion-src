package org.apache.http.client;

import org.apache.http.annotation.Immutable;
import org.apache.http.client.RedirectException;

@Immutable
public class CircularRedirectException extends RedirectException {
   private static final long serialVersionUID = 6830063487001091803L;

   public CircularRedirectException() {
   }

   public CircularRedirectException(String message) {
      super(message);
   }

   public CircularRedirectException(String message, Throwable cause) {
      super(message, cause);
   }
}
