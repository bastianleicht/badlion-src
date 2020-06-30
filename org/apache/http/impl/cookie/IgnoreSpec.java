package org.apache.http.impl.cookie;

import java.util.Collections;
import java.util.List;
import org.apache.http.Header;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.cookie.CookieSpecBase;

@NotThreadSafe
public class IgnoreSpec extends CookieSpecBase {
   public int getVersion() {
      return 0;
   }

   public List parse(Header header, CookieOrigin origin) throws MalformedCookieException {
      return Collections.emptyList();
   }

   public List formatCookies(List cookies) {
      return Collections.emptyList();
   }

   public Header getVersionHeader() {
      return null;
   }
}
