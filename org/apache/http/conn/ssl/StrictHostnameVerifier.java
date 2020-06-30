package org.apache.http.conn.ssl;

import javax.net.ssl.SSLException;
import org.apache.http.annotation.Immutable;
import org.apache.http.conn.ssl.AbstractVerifier;

@Immutable
public class StrictHostnameVerifier extends AbstractVerifier {
   public final void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
      this.verify(host, cns, subjectAlts, true);
   }

   public final String toString() {
      return "STRICT";
   }
}
