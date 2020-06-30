package org.apache.http.auth;

import org.apache.http.auth.AuthScheme;
import org.apache.http.protocol.HttpContext;

public interface AuthSchemeProvider {
   AuthScheme create(HttpContext var1);
}
