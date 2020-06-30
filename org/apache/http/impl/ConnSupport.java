package org.apache.http.impl;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import org.apache.http.config.ConnectionConfig;

public final class ConnSupport {
   public static CharsetDecoder createDecoder(ConnectionConfig cconfig) {
      if(cconfig == null) {
         return null;
      } else {
         Charset charset = cconfig.getCharset();
         CodingErrorAction malformed = cconfig.getMalformedInputAction();
         CodingErrorAction unmappable = cconfig.getUnmappableInputAction();
         return charset != null?charset.newDecoder().onMalformedInput(malformed != null?malformed:CodingErrorAction.REPORT).onUnmappableCharacter(unmappable != null?unmappable:CodingErrorAction.REPORT):null;
      }
   }

   public static CharsetEncoder createEncoder(ConnectionConfig cconfig) {
      if(cconfig == null) {
         return null;
      } else {
         Charset charset = cconfig.getCharset();
         if(charset != null) {
            CodingErrorAction malformed = cconfig.getMalformedInputAction();
            CodingErrorAction unmappable = cconfig.getUnmappableInputAction();
            return charset.newEncoder().onMalformedInput(malformed != null?malformed:CodingErrorAction.REPORT).onUnmappableCharacter(unmappable != null?unmappable:CodingErrorAction.REPORT);
         } else {
            return null;
         }
      }
   }
}
