package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.HttpRequest;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.impl.io.AbstractMessageWriter;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.LineFormatter;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
@NotThreadSafe
public class HttpRequestWriter extends AbstractMessageWriter {
   public HttpRequestWriter(SessionOutputBuffer buffer, LineFormatter formatter, HttpParams params) {
      super(buffer, formatter, params);
   }

   protected void writeHeadLine(HttpRequest message) throws IOException {
      this.lineFormatter.formatRequestLine(this.lineBuf, message.getRequestLine());
      this.sessionBuffer.writeLine(this.lineBuf);
   }
}
