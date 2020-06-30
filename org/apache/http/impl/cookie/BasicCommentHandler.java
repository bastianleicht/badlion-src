package org.apache.http.impl.cookie;

import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.impl.cookie.AbstractCookieAttributeHandler;
import org.apache.http.util.Args;

@Immutable
public class BasicCommentHandler extends AbstractCookieAttributeHandler {
   public void parse(SetCookie cookie, String value) throws MalformedCookieException {
      Args.notNull(cookie, "Cookie");
      cookie.setComment(value);
   }
}
