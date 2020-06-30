package org.apache.http.conn.ssl;

import java.net.Socket;
import java.util.Map;

public interface PrivateKeyStrategy {
   String chooseAlias(Map var1, Socket var2);
}
