package org.apache.http.client;

import java.util.Map;
import java.util.Queue;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.protocol.HttpContext;

public interface AuthenticationStrategy {
   boolean isAuthenticationRequested(HttpHost var1, HttpResponse var2, HttpContext var3);

   Map getChallenges(HttpHost var1, HttpResponse var2, HttpContext var3) throws MalformedChallengeException;

   Queue select(Map var1, HttpHost var2, HttpResponse var3, HttpContext var4) throws MalformedChallengeException;

   void authSucceeded(HttpHost var1, AuthScheme var2, HttpContext var3);

   void authFailed(HttpHost var1, AuthScheme var2, HttpContext var3);
}
