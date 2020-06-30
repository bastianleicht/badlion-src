package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.Header;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.BasicLineFormatter;
import org.apache.http.message.LineFormatter;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@NotThreadSafe
public abstract class AbstractMessageWriter implements HttpMessageWriter {
   protected final SessionOutputBuffer sessionBuffer;
   protected final CharArrayBuffer lineBuf;
   protected final LineFormatter lineFormatter;

   /** @deprecated */
   @Deprecated
   public AbstractMessageWriter(SessionOutputBuffer buffer, LineFormatter formatter, HttpParams params) {
      Args.notNull(buffer, "Session input buffer");
      this.sessionBuffer = buffer;
      this.lineBuf = new CharArrayBuffer(128);
      this.lineFormatter = (LineFormatter)(formatter != null?formatter:BasicLineFormatter.INSTANCE);
   }

   public AbstractMessageWriter(SessionOutputBuffer buffer, LineFormatter formatter) {
      this.sessionBuffer = (SessionOutputBuffer)Args.notNull(buffer, "Session input buffer");
      this.lineFormatter = (LineFormatter)(formatter != null?formatter:BasicLineFormatter.INSTANCE);
      this.lineBuf = new CharArrayBuffer(128);
   }

   protected abstract void writeHeadLine(HttpMessage var1) throws IOException;

   public void write(HttpMessage message) throws IOException, HttpException {
      Args.notNull(message, "HTTP message");
      this.writeHeadLine(message);
      HeaderIterator it = message.headerIterator();

      while(it.hasNext()) {
         Header header = it.nextHeader();
         this.sessionBuffer.writeLine(this.lineFormatter.formatHeader(this.lineBuf, header));
      }

      this.lineBuf.clear();
      this.sessionBuffer.writeLine(this.lineBuf);
   }
}
