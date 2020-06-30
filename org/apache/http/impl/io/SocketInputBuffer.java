package org.apache.http.impl.io;

import java.io.IOException;
import java.net.Socket;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.impl.io.AbstractSessionInputBuffer;
import org.apache.http.io.EofSensor;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
@NotThreadSafe
public class SocketInputBuffer extends AbstractSessionInputBuffer implements EofSensor {
   private final Socket socket;
   private boolean eof;

   public SocketInputBuffer(Socket socket, int buffersize, HttpParams params) throws IOException {
      Args.notNull(socket, "Socket");
      this.socket = socket;
      this.eof = false;
      int n = buffersize;
      if(buffersize < 0) {
         n = socket.getReceiveBufferSize();
      }

      if(n < 1024) {
         n = 1024;
      }

      this.init(socket.getInputStream(), n, params);
   }

   protected int fillBuffer() throws IOException {
      int i = super.fillBuffer();
      this.eof = i == -1;
      return i;
   }

   public boolean isDataAvailable(int timeout) throws IOException {
      boolean result = this.hasBufferedData();
      if(!result) {
         int oldtimeout = this.socket.getSoTimeout();

         try {
            this.socket.setSoTimeout(timeout);
            this.fillBuffer();
            result = this.hasBufferedData();
         } finally {
            this.socket.setSoTimeout(oldtimeout);
         }
      }

      return result;
   }

   public boolean isEof() {
      return this.eof;
   }
}
