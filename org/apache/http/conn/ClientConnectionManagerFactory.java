package org.apache.http.conn;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
public interface ClientConnectionManagerFactory {
   ClientConnectionManager newInstance(HttpParams var1, SchemeRegistry var2);
}
