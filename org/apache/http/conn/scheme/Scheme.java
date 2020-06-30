package org.apache.http.conn.scheme;

import java.util.Locale;
import org.apache.http.annotation.Immutable;
import org.apache.http.conn.scheme.LayeredSchemeSocketFactory;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.conn.scheme.LayeredSocketFactoryAdaptor;
import org.apache.http.conn.scheme.SchemeLayeredSocketFactory;
import org.apache.http.conn.scheme.SchemeLayeredSocketFactoryAdaptor;
import org.apache.http.conn.scheme.SchemeLayeredSocketFactoryAdaptor2;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.scheme.SchemeSocketFactoryAdaptor;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.scheme.SocketFactoryAdaptor;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

/** @deprecated */
@Deprecated
@Immutable
public final class Scheme {
   private final String name;
   private final SchemeSocketFactory socketFactory;
   private final int defaultPort;
   private final boolean layered;
   private String stringRep;

   public Scheme(String name, int port, SchemeSocketFactory factory) {
      Args.notNull(name, "Scheme name");
      Args.check(port > 0 && port <= '\uffff', "Port is invalid");
      Args.notNull(factory, "Socket factory");
      this.name = name.toLowerCase(Locale.ENGLISH);
      this.defaultPort = port;
      if(factory instanceof SchemeLayeredSocketFactory) {
         this.layered = true;
         this.socketFactory = factory;
      } else if(factory instanceof LayeredSchemeSocketFactory) {
         this.layered = true;
         this.socketFactory = new SchemeLayeredSocketFactoryAdaptor2((LayeredSchemeSocketFactory)factory);
      } else {
         this.layered = false;
         this.socketFactory = factory;
      }

   }

   /** @deprecated */
   @Deprecated
   public Scheme(String name, SocketFactory factory, int port) {
      Args.notNull(name, "Scheme name");
      Args.notNull(factory, "Socket factory");
      Args.check(port > 0 && port <= '\uffff', "Port is invalid");
      this.name = name.toLowerCase(Locale.ENGLISH);
      if(factory instanceof LayeredSocketFactory) {
         this.socketFactory = new SchemeLayeredSocketFactoryAdaptor((LayeredSocketFactory)factory);
         this.layered = true;
      } else {
         this.socketFactory = new SchemeSocketFactoryAdaptor(factory);
         this.layered = false;
      }

      this.defaultPort = port;
   }

   public final int getDefaultPort() {
      return this.defaultPort;
   }

   /** @deprecated */
   @Deprecated
   public final SocketFactory getSocketFactory() {
      return (SocketFactory)(this.socketFactory instanceof SchemeSocketFactoryAdaptor?((SchemeSocketFactoryAdaptor)this.socketFactory).getFactory():(this.layered?new LayeredSocketFactoryAdaptor((LayeredSchemeSocketFactory)this.socketFactory):new SocketFactoryAdaptor(this.socketFactory)));
   }

   public final SchemeSocketFactory getSchemeSocketFactory() {
      return this.socketFactory;
   }

   public final String getName() {
      return this.name;
   }

   public final boolean isLayered() {
      return this.layered;
   }

   public final int resolvePort(int port) {
      return port <= 0?this.defaultPort:port;
   }

   public final String toString() {
      if(this.stringRep == null) {
         StringBuilder buffer = new StringBuilder();
         buffer.append(this.name);
         buffer.append(':');
         buffer.append(Integer.toString(this.defaultPort));
         this.stringRep = buffer.toString();
      }

      return this.stringRep;
   }

   public final boolean equals(Object obj) {
      if(this == obj) {
         return true;
      } else if(!(obj instanceof Scheme)) {
         return false;
      } else {
         Scheme that = (Scheme)obj;
         return this.name.equals(that.name) && this.defaultPort == that.defaultPort && this.layered == that.layered;
      }
   }

   public int hashCode() {
      int hash = 17;
      hash = LangUtils.hashCode(hash, this.defaultPort);
      hash = LangUtils.hashCode(hash, this.name);
      hash = LangUtils.hashCode(hash, this.layered);
      return hash;
   }
}
