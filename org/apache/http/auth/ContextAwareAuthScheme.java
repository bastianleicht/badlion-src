package org.apache.http.auth;

import org.apache.http.Header;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.protocol.HttpContext;

public interface ContextAwareAuthScheme extends AuthScheme {
   Header authenticate(Credentials var1, HttpRequest var2, HttpContext var3) throws AuthenticationException;
}
