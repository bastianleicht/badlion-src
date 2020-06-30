package io.netty.handler.codec.http.cors;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.util.internal.StringUtil;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

public final class CorsConfig {
   private final Set origins;
   private final boolean anyOrigin;
   private final boolean enabled;
   private final Set exposeHeaders;
   private final boolean allowCredentials;
   private final long maxAge;
   private final Set allowedRequestMethods;
   private final Set allowedRequestHeaders;
   private final boolean allowNullOrigin;
   private final Map preflightHeaders;
   private final boolean shortCurcuit;

   private CorsConfig(CorsConfig.Builder builder) {
      this.origins = new LinkedHashSet(builder.origins);
      this.anyOrigin = builder.anyOrigin;
      this.enabled = builder.enabled;
      this.exposeHeaders = builder.exposeHeaders;
      this.allowCredentials = builder.allowCredentials;
      this.maxAge = builder.maxAge;
      this.allowedRequestMethods = builder.requestMethods;
      this.allowedRequestHeaders = builder.requestHeaders;
      this.allowNullOrigin = builder.allowNullOrigin;
      this.preflightHeaders = builder.preflightHeaders;
      this.shortCurcuit = builder.shortCurcuit;
   }

   public boolean isCorsSupportEnabled() {
      return this.enabled;
   }

   public boolean isAnyOriginSupported() {
      return this.anyOrigin;
   }

   public String origin() {
      return this.origins.isEmpty()?"*":(String)this.origins.iterator().next();
   }

   public Set origins() {
      return this.origins;
   }

   public boolean isNullOriginAllowed() {
      return this.allowNullOrigin;
   }

   public Set exposedHeaders() {
      return Collections.unmodifiableSet(this.exposeHeaders);
   }

   public boolean isCredentialsAllowed() {
      return this.allowCredentials;
   }

   public long maxAge() {
      return this.maxAge;
   }

   public Set allowedRequestMethods() {
      return Collections.unmodifiableSet(this.allowedRequestMethods);
   }

   public Set allowedRequestHeaders() {
      return Collections.unmodifiableSet(this.allowedRequestHeaders);
   }

   public HttpHeaders preflightResponseHeaders() {
      if(this.preflightHeaders.isEmpty()) {
         return HttpHeaders.EMPTY_HEADERS;
      } else {
         HttpHeaders preflightHeaders = new DefaultHttpHeaders();

         for(Entry<CharSequence, Callable<?>> entry : this.preflightHeaders.entrySet()) {
            Object value = getValue((Callable)entry.getValue());
            if(value instanceof Iterable) {
               preflightHeaders.add((CharSequence)entry.getKey(), (Iterable)value);
            } else {
               preflightHeaders.add((CharSequence)entry.getKey(), value);
            }
         }

         return preflightHeaders;
      }
   }

   public boolean isShortCurcuit() {
      return this.shortCurcuit;
   }

   private static Object getValue(Callable callable) {
      try {
         return callable.call();
      } catch (Exception var2) {
         throw new IllegalStateException("Could not generate value for callable [" + callable + ']', var2);
      }
   }

   public String toString() {
      return StringUtil.simpleClassName((Object)this) + "[enabled=" + this.enabled + ", origins=" + this.origins + ", anyOrigin=" + this.anyOrigin + ", exposedHeaders=" + this.exposeHeaders + ", isCredentialsAllowed=" + this.allowCredentials + ", maxAge=" + this.maxAge + ", allowedRequestMethods=" + this.allowedRequestMethods + ", allowedRequestHeaders=" + this.allowedRequestHeaders + ", preflightHeaders=" + this.preflightHeaders + ']';
   }

   public static CorsConfig.Builder withAnyOrigin() {
      return new CorsConfig.Builder();
   }

   public static CorsConfig.Builder withOrigin(String origin) {
      return "*".equals(origin)?new CorsConfig.Builder():new CorsConfig.Builder(new String[]{origin});
   }

   public static CorsConfig.Builder withOrigins(String... origins) {
      return new CorsConfig.Builder(origins);
   }

   public static class Builder {
      private final Set origins;
      private final boolean anyOrigin;
      private boolean allowNullOrigin;
      private boolean enabled = true;
      private boolean allowCredentials;
      private final Set exposeHeaders = new HashSet();
      private long maxAge;
      private final Set requestMethods = new HashSet();
      private final Set requestHeaders = new HashSet();
      private final Map preflightHeaders = new HashMap();
      private boolean noPreflightHeaders;
      private boolean shortCurcuit;

      public Builder(String... origins) {
         this.origins = new LinkedHashSet(Arrays.asList(origins));
         this.anyOrigin = false;
      }

      public Builder() {
         this.anyOrigin = true;
         this.origins = Collections.emptySet();
      }

      public CorsConfig.Builder allowNullOrigin() {
         this.allowNullOrigin = true;
         return this;
      }

      public CorsConfig.Builder disable() {
         this.enabled = false;
         return this;
      }

      public CorsConfig.Builder exposeHeaders(String... headers) {
         this.exposeHeaders.addAll(Arrays.asList(headers));
         return this;
      }

      public CorsConfig.Builder allowCredentials() {
         this.allowCredentials = true;
         return this;
      }

      public CorsConfig.Builder maxAge(long max) {
         this.maxAge = max;
         return this;
      }

      public CorsConfig.Builder allowedRequestMethods(HttpMethod... methods) {
         this.requestMethods.addAll(Arrays.asList(methods));
         return this;
      }

      public CorsConfig.Builder allowedRequestHeaders(String... headers) {
         this.requestHeaders.addAll(Arrays.asList(headers));
         return this;
      }

      public CorsConfig.Builder preflightResponseHeader(CharSequence name, Object... values) {
         if(values.length == 1) {
            this.preflightHeaders.put(name, new CorsConfig.ConstantValueGenerator(values[0]));
         } else {
            this.preflightResponseHeader((CharSequence)name, (Iterable)Arrays.asList(values));
         }

         return this;
      }

      public CorsConfig.Builder preflightResponseHeader(CharSequence name, Iterable value) {
         this.preflightHeaders.put(name, new CorsConfig.ConstantValueGenerator(value));
         return this;
      }

      public CorsConfig.Builder preflightResponseHeader(String name, Callable valueGenerator) {
         this.preflightHeaders.put(name, valueGenerator);
         return this;
      }

      public CorsConfig.Builder noPreflightResponseHeaders() {
         this.noPreflightHeaders = true;
         return this;
      }

      public CorsConfig build() {
         if(this.preflightHeaders.isEmpty() && !this.noPreflightHeaders) {
            this.preflightHeaders.put("Date", new CorsConfig.DateValueGenerator());
            this.preflightHeaders.put("Content-Length", new CorsConfig.ConstantValueGenerator("0"));
         }

         return new CorsConfig(this);
      }

      public CorsConfig.Builder shortCurcuit() {
         this.shortCurcuit = true;
         return this;
      }
   }

   private static final class ConstantValueGenerator implements Callable {
      private final Object value;

      private ConstantValueGenerator(Object value) {
         if(value == null) {
            throw new IllegalArgumentException("value must not be null");
         } else {
            this.value = value;
         }
      }

      public Object call() {
         return this.value;
      }
   }

   public static final class DateValueGenerator implements Callable {
      public Date call() throws Exception {
         return new Date();
      }
   }
}
