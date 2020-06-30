package org.apache.logging.log4j.core.net;

import java.io.OutputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.appender.OutputStreamManager;

public abstract class AbstractSocketManager extends OutputStreamManager {
   protected final InetAddress address;
   protected final String host;
   protected final int port;

   public AbstractSocketManager(String name, OutputStream os, InetAddress addr, String host, int port, Layout layout) {
      super(os, name, layout);
      this.address = addr;
      this.host = host;
      this.port = port;
   }

   public Map getContentFormat() {
      Map<String, String> result = new HashMap(super.getContentFormat());
      result.put("port", Integer.toString(this.port));
      result.put("address", this.address.getHostAddress());
      return result;
   }
}
