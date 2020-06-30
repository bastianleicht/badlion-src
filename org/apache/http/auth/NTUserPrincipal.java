package org.apache.http.auth;

import java.io.Serializable;
import java.security.Principal;
import java.util.Locale;
import org.apache.http.annotation.Immutable;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Immutable
public class NTUserPrincipal implements Principal, Serializable {
   private static final long serialVersionUID = -6870169797924406894L;
   private final String username;
   private final String domain;
   private final String ntname;

   public NTUserPrincipal(String domain, String username) {
      Args.notNull(username, "User name");
      this.username = username;
      if(domain != null) {
         this.domain = domain.toUpperCase(Locale.ENGLISH);
      } else {
         this.domain = null;
      }

      if(this.domain != null && this.domain.length() > 0) {
         StringBuilder buffer = new StringBuilder();
         buffer.append(this.domain);
         buffer.append('\\');
         buffer.append(this.username);
         this.ntname = buffer.toString();
      } else {
         this.ntname = this.username;
      }

   }

   public String getName() {
      return this.ntname;
   }

   public String getDomain() {
      return this.domain;
   }

   public String getUsername() {
      return this.username;
   }

   public int hashCode() {
      int hash = 17;
      hash = LangUtils.hashCode(hash, this.username);
      hash = LangUtils.hashCode(hash, this.domain);
      return hash;
   }

   public boolean equals(Object o) {
      if(this == o) {
         return true;
      } else {
         if(o instanceof NTUserPrincipal) {
            NTUserPrincipal that = (NTUserPrincipal)o;
            if(LangUtils.equals((Object)this.username, (Object)that.username) && LangUtils.equals((Object)this.domain, (Object)that.domain)) {
               return true;
            }
         }

         return false;
      }
   }

   public String toString() {
      return this.ntname;
   }
}
