package org.apache.http.auth;

import org.apache.http.auth.AuthScheme;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
public interface AuthSchemeFactory {
   AuthScheme newInstance(HttpParams var1);
}
