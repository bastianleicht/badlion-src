package org.apache.http.impl.cookie;

import java.util.Date;
import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.impl.cookie.AbstractCookieAttributeHandler;
import org.apache.http.util.Args;

@Immutable
public class BasicMaxAgeHandler extends AbstractCookieAttributeHandler {
   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
      Args.notNull(cookie, "Cookie");
      if(value == null) {
         throw new MalformedCookieException("Missing value for max-age attribute");
      } else {
         int age;
         try {
            age = Integer.parseInt(value);
         } catch (NumberFormatException var5) {
            throw new MalformedCookieException("Invalid max-age attribute: " + value);
         }

         if(age < 0) {
            throw new MalformedCookieException("Negative max-age attribute: " + value);
         } else {
            cookie.setExpiryDate(new Date(System.currentTimeMillis() + (long)age * 1000L));
         }
      }
   }
}
