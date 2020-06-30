package org.apache.http.client.params;

/** @deprecated */
@Deprecated
public interface ClientPNames {
   String CONNECTION_MANAGER_FACTORY_CLASS_NAME = "http.connection-manager.factory-class-name";
   String HANDLE_REDIRECTS = "http.protocol.handle-redirects";
   String REJECT_RELATIVE_REDIRECT = "http.protocol.reject-relative-redirect";
   String MAX_REDIRECTS = "http.protocol.max-redirects";
   String ALLOW_CIRCULAR_REDIRECTS = "http.protocol.allow-circular-redirects";
   String HANDLE_AUTHENTICATION = "http.protocol.handle-authentication";
   String COOKIE_POLICY = "http.protocol.cookie-policy";
   String VIRTUAL_HOST = "http.virtual-host";
   String DEFAULT_HEADERS = "http.default-headers";
   String DEFAULT_HOST = "http.default-host";
   String CONN_MANAGER_TIMEOUT = "http.conn-manager.timeout";
}
