package io.netty.handler.codec.http;

import io.netty.handler.codec.http.Cookie;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class DefaultCookie implements Cookie {
   private final String name;
   private String value;
   private String domain;
   private String path;
   private String comment;
   private String commentUrl;
   private boolean discard;
   private Set ports;
   private Set unmodifiablePorts;
   private long maxAge;
   private int version;
   private boolean secure;
   private boolean httpOnly;

   public DefaultCookie(String param1, String param2) {
      // $FF: Couldn't be decompiled
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      if(value == null) {
         throw new NullPointerException("value");
      } else {
         this.value = value;
      }
   }

   public String getDomain() {
      return this.domain;
   }

   public void setDomain(String domain) {
      this.domain = validateValue("domain", domain);
   }

   public String getPath() {
      return this.path;
   }

   public void setPath(String path) {
      this.path = validateValue("path", path);
   }

   public String getComment() {
      return this.comment;
   }

   public void setComment(String comment) {
      this.comment = validateValue("comment", comment);
   }

   public String getCommentUrl() {
      return this.commentUrl;
   }

   public void setCommentUrl(String commentUrl) {
      this.commentUrl = validateValue("commentUrl", commentUrl);
   }

   public boolean isDiscard() {
      return this.discard;
   }

   public void setDiscard(boolean discard) {
      this.discard = discard;
   }

   public Set getPorts() {
      if(this.unmodifiablePorts == null) {
         this.unmodifiablePorts = Collections.unmodifiableSet(this.ports);
      }

      return this.unmodifiablePorts;
   }

   public void setPorts(int... ports) {
      if(ports == null) {
         throw new NullPointerException("ports");
      } else {
         int[] portsCopy = (int[])ports.clone();
         if(portsCopy.length == 0) {
            this.unmodifiablePorts = this.ports = Collections.emptySet();
         } else {
            Set<Integer> newPorts = new TreeSet();

            for(int p : portsCopy) {
               if(p <= 0 || p > '\uffff') {
                  throw new IllegalArgumentException("port out of range: " + p);
               }

               newPorts.add(Integer.valueOf(p));
            }

            this.ports = newPorts;
            this.unmodifiablePorts = null;
         }

      }
   }

   public void setPorts(Iterable ports) {
      Set<Integer> newPorts = new TreeSet();
      Iterator i$ = ports.iterator();

      while(i$.hasNext()) {
         int p = ((Integer)i$.next()).intValue();
         if(p <= 0 || p > '\uffff') {
            throw new IllegalArgumentException("port out of range: " + p);
         }

         newPorts.add(Integer.valueOf(p));
      }

      if(newPorts.isEmpty()) {
         this.unmodifiablePorts = this.ports = Collections.emptySet();
      } else {
         this.ports = newPorts;
         this.unmodifiablePorts = null;
      }

   }

   public long getMaxAge() {
      return this.maxAge;
   }

   public void setMaxAge(long maxAge) {
      this.maxAge = maxAge;
   }

   public int getVersion() {
      return this.version;
   }

   public void setVersion(int version) {
      this.version = version;
   }

   public boolean isSecure() {
      return this.secure;
   }

   public void setSecure(boolean secure) {
      this.secure = secure;
   }

   public boolean isHttpOnly() {
      return this.httpOnly;
   }

   public void setHttpOnly(boolean httpOnly) {
      this.httpOnly = httpOnly;
   }

   public int hashCode() {
      return this.getName().hashCode();
   }

   public boolean equals(Object o) {
      if(!(o instanceof Cookie)) {
         return false;
      } else {
         Cookie that = (Cookie)o;
         if(!this.getName().equalsIgnoreCase(that.getName())) {
            return false;
         } else {
            if(this.getPath() == null) {
               if(that.getPath() != null) {
                  return false;
               }
            } else {
               if(that.getPath() == null) {
                  return false;
               }

               if(!this.getPath().equals(that.getPath())) {
                  return false;
               }
            }

            return this.getDomain() == null?that.getDomain() == null:(that.getDomain() == null?false:this.getDomain().equalsIgnoreCase(that.getDomain()));
         }
      }
   }

   public int compareTo(Cookie c) {
      int v = this.getName().compareToIgnoreCase(c.getName());
      if(v != 0) {
         return v;
      } else {
         if(this.getPath() == null) {
            if(c.getPath() != null) {
               return -1;
            }
         } else {
            if(c.getPath() == null) {
               return 1;
            }

            v = this.getPath().compareTo(c.getPath());
            if(v != 0) {
               return v;
            }
         }

         if(this.getDomain() == null) {
            return c.getDomain() != null?-1:0;
         } else if(c.getDomain() == null) {
            return 1;
         } else {
            v = this.getDomain().compareToIgnoreCase(c.getDomain());
            return v;
         }
      }
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      buf.append(this.getName());
      buf.append('=');
      buf.append(this.getValue());
      if(this.getDomain() != null) {
         buf.append(", domain=");
         buf.append(this.getDomain());
      }

      if(this.getPath() != null) {
         buf.append(", path=");
         buf.append(this.getPath());
      }

      if(this.getComment() != null) {
         buf.append(", comment=");
         buf.append(this.getComment());
      }

      if(this.getMaxAge() >= 0L) {
         buf.append(", maxAge=");
         buf.append(this.getMaxAge());
         buf.append('s');
      }

      if(this.isSecure()) {
         buf.append(", secure");
      }

      if(this.isHttpOnly()) {
         buf.append(", HTTPOnly");
      }

      return buf.toString();
   }

   private static String validateValue(String param0, String param1) {
      // $FF: Couldn't be decompiled
   }
}
