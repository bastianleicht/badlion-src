package org.apache.http.conn.params;

/** @deprecated */
@Deprecated
public interface ConnManagerPNames {
   String TIMEOUT = "http.conn-manager.timeout";
   String MAX_CONNECTIONS_PER_ROUTE = "http.conn-manager.max-per-route";
   String MAX_TOTAL_CONNECTIONS = "http.conn-manager.max-total";
}
