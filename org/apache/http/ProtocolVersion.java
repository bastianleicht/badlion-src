package org.apache.http;

import java.io.Serializable;
import org.apache.http.annotation.Immutable;
import org.apache.http.util.Args;

@Immutable
public class ProtocolVersion implements Serializable, Cloneable {
   private static final long serialVersionUID = 8950662842175091068L;
   protected final String protocol;
   protected final int major;
   protected final int minor;

   public ProtocolVersion(String protocol, int major, int minor) {
      this.protocol = (String)Args.notNull(protocol, "Protocol name");
      this.major = Args.notNegative(major, "Protocol minor version");
      this.minor = Args.notNegative(minor, "Protocol minor version");
   }

   public final String getProtocol() {
      return this.protocol;
   }

   public final int getMajor() {
      return this.major;
   }

   public final int getMinor() {
      return this.minor;
   }

   public ProtocolVersion forVersion(int major, int minor) {
      return major == this.major && minor == this.minor?this:new ProtocolVersion(this.protocol, major, minor);
   }

   public final int hashCode() {
      return this.protocol.hashCode() ^ this.major * 100000 ^ this.minor;
   }

   public final boolean equals(Object obj) {
      if(this == obj) {
         return true;
      } else if(!(obj instanceof ProtocolVersion)) {
         return false;
      } else {
         ProtocolVersion that = (ProtocolVersion)obj;
         return this.protocol.equals(that.protocol) && this.major == that.major && this.minor == that.minor;
      }
   }

   public boolean isComparable(ProtocolVersion that) {
      return that != null && this.protocol.equals(that.protocol);
   }

   public int compareToVersion(ProtocolVersion that) {
      Args.notNull(that, "Protocol version");
      Args.check(this.protocol.equals(that.protocol), "Versions for different protocols cannot be compared: %s %s", new Object[]{this, that});
      int delta = this.getMajor() - that.getMajor();
      if(delta == 0) {
         delta = this.getMinor() - that.getMinor();
      }

      return delta;
   }

   public final boolean greaterEquals(ProtocolVersion version) {
      return this.isComparable(version) && this.compareToVersion(version) >= 0;
   }

   public final boolean lessEquals(ProtocolVersion version) {
      return this.isComparable(version) && this.compareToVersion(version) <= 0;
   }

   public String toString() {
      StringBuilder buffer = new StringBuilder();
      buffer.append(this.protocol);
      buffer.append('/');
      buffer.append(Integer.toString(this.major));
      buffer.append('.');
      buffer.append(Integer.toString(this.minor));
      return buffer.toString();
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }
}
