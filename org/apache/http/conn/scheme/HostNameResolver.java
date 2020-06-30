package org.apache.http.conn.scheme;

import java.io.IOException;
import java.net.InetAddress;

/** @deprecated */
@Deprecated
public interface HostNameResolver {
   InetAddress resolve(String var1) throws IOException;
}
