package org.apache.http.auth;

import java.io.Serializable;
import java.security.Principal;
import org.apache.http.annotation.Immutable;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Immutable
public final class BasicUserPrincipal implements Principal, Serializable {
   private static final long serialVersionUID = -2266305184969850467L;
   private final String username;

   public BasicUserPrincipal(String username) {
      Args.notNull(username, "User name");
      this.username = username;
   }

   public String getName() {
      return this.username;
   }

   public int hashCode() {
      int hash = 17;
      hash = LangUtils.hashCode(hash, this.username);
      return hash;
   }

   public boolean equals(Object o) {
      if(this == o) {
         return true;
      } else {
         if(o instanceof BasicUserPrincipal) {
            BasicUserPrincipal that = (BasicUserPrincipal)o;
            if(LangUtils.equals((Object)this.username, (Object)that.username)) {
               return true;
            }
         }

         return false;
      }
   }

   public String toString() {
      StringBuilder buffer = new StringBuilder();
      buffer.append("[principal: ");
      buffer.append(this.username);
      buffer.append("]");
      return buffer.toString();
   }
}
