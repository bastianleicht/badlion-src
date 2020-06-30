package org.apache.http.cookie;

import java.util.Date;
import org.apache.http.cookie.Cookie;

public interface SetCookie extends Cookie {
   void setValue(String var1);

   void setComment(String var1);

   void setExpiryDate(Date var1);

   void setDomain(String var1);

   void setPath(String var1);

   void setSecure(boolean var1);

   void setVersion(int var1);
}
