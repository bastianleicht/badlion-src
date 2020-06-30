package org.apache.http.conn;

import org.apache.http.HttpHost;
import org.apache.http.conn.UnsupportedSchemeException;

public interface SchemePortResolver {
   int resolve(HttpHost var1) throws UnsupportedSchemeException;
}
