package joptsimple.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

public class InetAddressConverter implements ValueConverter {
   public InetAddress convert(String value) {
      try {
         return InetAddress.getByName(value);
      } catch (UnknownHostException var3) {
         throw new ValueConversionException("Cannot convert value [" + value + " into an InetAddress", var3);
      }
   }

   public Class valueType() {
      return InetAddress.class;
   }

   public String valuePattern() {
      return null;
   }
}
