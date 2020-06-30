package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.HttpRequestFactory;
import org.apache.http.ParseException;
import org.apache.http.RequestLine;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.impl.io.AbstractMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

/** @deprecated */
@Deprecated
@NotThreadSafe
public class HttpRequestParser extends AbstractMessageParser {
   private final HttpRequestFactory requestFactory;
   private final CharArrayBuffer lineBuf;

   public HttpRequestParser(SessionInputBuffer buffer, LineParser parser, HttpRequestFactory requestFactory, HttpParams params) {
      super(buffer, parser, params);
      this.requestFactory = (HttpRequestFactory)Args.notNull(requestFactory, "Request factory");
      this.lineBuf = new CharArrayBuffer(128);
   }

   protected HttpMessage parseHead(SessionInputBuffer sessionBuffer) throws IOException, HttpException, ParseException {
      this.lineBuf.clear();
      int i = sessionBuffer.readLine(this.lineBuf);
      if(i == -1) {
         throw new ConnectionClosedException("Client closed connection");
      } else {
         ParserCursor cursor = new ParserCursor(0, this.lineBuf.length());
         RequestLine requestline = this.lineParser.parseRequestLine(this.lineBuf, cursor);
         return this.requestFactory.newHttpRequest(requestline);
      }
   }
}
