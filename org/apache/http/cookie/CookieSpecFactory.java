package org.apache.http.cookie;

import org.apache.http.cookie.CookieSpec;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
public interface CookieSpecFactory {
   CookieSpec newInstance(HttpParams var1);
}
