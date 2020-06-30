package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.NoHttpResponseException;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.config.MessageConstraints;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.io.AbstractMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@NotThreadSafe
public class DefaultHttpResponseParser extends AbstractMessageParser {
   private final HttpResponseFactory responseFactory;
   private final CharArrayBuffer lineBuf;

   /** @deprecated */
   @Deprecated
   public DefaultHttpResponseParser(SessionInputBuffer buffer, LineParser lineParser, HttpResponseFactory responseFactory, HttpParams params) {
      super(buffer, lineParser, params);
      this.responseFactory = (HttpResponseFactory)Args.notNull(responseFactory, "Response factory");
      this.lineBuf = new CharArrayBuffer(128);
   }

   public DefaultHttpResponseParser(SessionInputBuffer buffer, LineParser lineParser, HttpResponseFactory responseFactory, MessageConstraints constraints) {
      super(buffer, lineParser, constraints);
      this.responseFactory = (HttpResponseFactory)(responseFactory != null?responseFactory:DefaultHttpResponseFactory.INSTANCE);
      this.lineBuf = new CharArrayBuffer(128);
   }

   public DefaultHttpResponseParser(SessionInputBuffer buffer, MessageConstraints constraints) {
      this(buffer, (LineParser)null, (HttpResponseFactory)null, (MessageConstraints)constraints);
   }

   public DefaultHttpResponseParser(SessionInputBuffer buffer) {
      this(buffer, (LineParser)null, (HttpResponseFactory)null, (MessageConstraints)MessageConstraints.DEFAULT);
   }

   protected HttpResponse parseHead(SessionInputBuffer sessionBuffer) throws IOException, HttpException, ParseException {
      this.lineBuf.clear();
      int i = sessionBuffer.readLine(this.lineBuf);
      if(i == -1) {
         throw new NoHttpResponseException("The target server failed to respond");
      } else {
         ParserCursor cursor = new ParserCursor(0, this.lineBuf.length());
         StatusLine statusline = this.lineParser.parseStatusLine(this.lineBuf, cursor);
         return this.responseFactory.newHttpResponse(statusline, (HttpContext)null);
      }
   }
}
