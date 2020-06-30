package org.apache.http.client.params;

import java.net.InetAddress;
import java.util.Collection;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
public final class HttpClientParamConfig {
   public static RequestConfig getRequestConfig(HttpParams params) {
      return RequestConfig.custom().setSocketTimeout(params.getIntParameter("http.socket.timeout", 0)).setStaleConnectionCheckEnabled(params.getBooleanParameter("http.connection.stalecheck", true)).setConnectTimeout(params.getIntParameter("http.connection.timeout", 0)).setExpectContinueEnabled(params.getBooleanParameter("http.protocol.expect-continue", false)).setProxy((HttpHost)params.getParameter("http.route.default-proxy")).setLocalAddress((InetAddress)params.getParameter("http.route.local-address")).setProxyPreferredAuthSchemes((Collection)params.getParameter("http.auth.proxy-scheme-pref")).setTargetPreferredAuthSchemes((Collection)params.getParameter("http.auth.target-scheme-pref")).setAuthenticationEnabled(params.getBooleanParameter("http.protocol.handle-authentication", true)).setCircularRedirectsAllowed(params.getBooleanParameter("http.protocol.allow-circular-redirects", false)).setConnectionRequestTimeout((int)params.getLongParameter("http.conn-manager.timeout", 0L)).setCookieSpec((String)params.getParameter("http.protocol.cookie-policy")).setMaxRedirects(params.getIntParameter("http.protocol.max-redirects", 50)).setRedirectsEnabled(params.getBooleanParameter("http.protocol.handle-redirects", true)).setRelativeRedirectsAllowed(!params.getBooleanParameter("http.protocol.reject-relative-redirect", false)).build();
   }
}
