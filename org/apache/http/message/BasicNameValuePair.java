package org.apache.http.message;

import java.io.Serializable;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.Immutable;
import org.apache.http.util.Args;
import org.apache.http.util.LangUtils;

@Immutable
public class BasicNameValuePair implements NameValuePair, Cloneable, Serializable {
   private static final long serialVersionUID = -6437800749411518984L;
   private final String name;
   private final String value;

   public BasicNameValuePair(String name, String value) {
      this.name = (String)Args.notNull(name, "Name");
      this.value = value;
   }

   public String getName() {
      return this.name;
   }

   public String getValue() {
      return this.value;
   }

   public String toString() {
      if(this.value == null) {
         return this.name;
      } else {
         int len = this.name.length() + 1 + this.value.length();
         StringBuilder buffer = new StringBuilder(len);
         buffer.append(this.name);
         buffer.append("=");
         buffer.append(this.value);
         return buffer.toString();
      }
   }

   public boolean equals(Object object) {
      if(this == object) {
         return true;
      } else if(!(object instanceof NameValuePair)) {
         return false;
      } else {
         BasicNameValuePair that = (BasicNameValuePair)object;
         return this.name.equals(that.name) && LangUtils.equals((Object)this.value, (Object)that.value);
      }
   }

   public int hashCode() {
      int hash = 17;
      hash = LangUtils.hashCode(hash, this.name);
      hash = LangUtils.hashCode(hash, this.value);
      return hash;
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
