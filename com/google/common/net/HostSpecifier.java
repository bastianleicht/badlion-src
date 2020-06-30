package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.net.HostAndPort;
import com.google.common.net.InetAddresses;
import com.google.common.net.InternetDomainName;
import java.net.InetAddress;
import java.text.ParseException;
import javax.annotation.Nullable;

@Beta
public final class HostSpecifier {
   private final String canonicalForm;

   private HostSpecifier(String canonicalForm) {
      this.canonicalForm = canonicalForm;
   }

   public static HostSpecifier fromValid(String specifier) {
      HostAndPort parsedHost = HostAndPort.fromString(specifier);
      Preconditions.checkArgument(!parsedHost.hasPort());
      String host = parsedHost.getHostText();
      InetAddress addr = null;

      try {
         addr = InetAddresses.forString(host);
      } catch (IllegalArgumentException var5) {
         ;
      }

      if(addr != null) {
         return new HostSpecifier(InetAddresses.toUriString(addr));
      } else {
         InternetDomainName domain = InternetDomainName.from(host);
         if(domain.hasPublicSuffix()) {
            return new HostSpecifier(domain.toString());
         } else {
            throw new IllegalArgumentException("Domain name does not have a recognized public suffix: " + host);
         }
      }
   }

   public static HostSpecifier from(String specifier) throws ParseException {
      try {
         return fromValid(specifier);
      } catch (IllegalArgumentException var3) {
         ParseException parseException = new ParseException("Invalid host specifier: " + specifier, 0);
         parseException.initCause(var3);
         throw parseException;
      }
   }

   public static boolean isValid(String specifier) {
      try {
         fromValid(specifier);
         return true;
      } catch (IllegalArgumentException var2) {
         return false;
      }
   }

   public boolean equals(@Nullable Object other) {
      if(this == other) {
         return true;
      } else if(other instanceof HostSpecifier) {
         HostSpecifier that = (HostSpecifier)other;
         return this.canonicalForm.equals(that.canonicalForm);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.canonicalForm.hashCode();
   }

   public String toString() {
      return this.canonicalForm;
   }
}
