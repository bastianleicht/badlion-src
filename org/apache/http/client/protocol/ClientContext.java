package org.apache.http.client.protocol;

/** @deprecated */
@Deprecated
public interface ClientContext {
   String ROUTE = "http.route";
   /** @deprecated */
   @Deprecated
   String SCHEME_REGISTRY = "http.scheme-registry";
   String COOKIESPEC_REGISTRY = "http.cookiespec-registry";
   String COOKIE_SPEC = "http.cookie-spec";
   String COOKIE_ORIGIN = "http.cookie-origin";
   String COOKIE_STORE = "http.cookie-store";
   String CREDS_PROVIDER = "http.auth.credentials-provider";
   String AUTH_CACHE = "http.auth.auth-cache";
   String TARGET_AUTH_STATE = "http.auth.target-scope";
   String PROXY_AUTH_STATE = "http.auth.proxy-scope";
   /** @deprecated */
   @Deprecated
   String AUTH_SCHEME_PREF = "http.auth.scheme-pref";
   String USER_TOKEN = "http.user-token";
   String AUTHSCHEME_REGISTRY = "http.authscheme-registry";
   String SOCKET_FACTORY_REGISTRY = "http.socket-factory-registry";
   String REQUEST_CONFIG = "http.request-config";
}
