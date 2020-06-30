package org.apache.http.cookie;

import org.apache.http.cookie.Cookie;

public interface ClientCookie extends Cookie {
   String VERSION_ATTR = "version";
   String PATH_ATTR = "path";
   String DOMAIN_ATTR = "domain";
   String MAX_AGE_ATTR = "max-age";
   String SECURE_ATTR = "secure";
   String COMMENT_ATTR = "comment";
   String EXPIRES_ATTR = "expires";
   String PORT_ATTR = "port";
   String COMMENTURL_ATTR = "commenturl";
   String DISCARD_ATTR = "discard";

   String getAttribute(String var1);

   boolean containsAttribute(String var1);
}
