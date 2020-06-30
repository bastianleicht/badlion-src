package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.impl.io.AbstractMessageWriter;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.LineFormatter;
import org.apache.http.params.HttpParams;

/** @deprecated */
@Deprecated
@NotThreadSafe
public class HttpResponseWriter extends AbstractMessageWriter {
   public HttpResponseWriter(SessionOutputBuffer buffer, LineFormatter formatter, HttpParams params) {
      super(buffer, formatter, params);
   }

   protected void writeHeadLine(HttpResponse message) throws IOException {
      this.lineFormatter.formatStatusLine(this.lineBuf, message.getStatusLine());
      this.sessionBuffer.writeLine(this.lineBuf);
   }
}
