package org.apache.http.impl.io;

import java.io.IOException;
import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestFactory;
import org.apache.http.ParseException;
import org.apache.http.RequestLine;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.config.MessageConstraints;
import org.apache.http.impl.DefaultHttpRequestFactory;
import org.apache.http.impl.io.AbstractMessageParser;
import org.apache.http.io.SessionInputBuffer;
import org.apache.http.message.LineParser;
import org.apache.http.message.ParserCursor;
import org.apache.http.params.HttpParams;
import org.apache.http.util.Args;
import org.apache.http.util.CharArrayBuffer;

@NotThreadSafe
public class DefaultHttpRequestParser extends AbstractMessageParser {
   private final HttpRequestFactory requestFactory;
   private final CharArrayBuffer lineBuf;

   /** @deprecated */
   @Deprecated
   public DefaultHttpRequestParser(SessionInputBuffer buffer, LineParser lineParser, HttpRequestFactory requestFactory, HttpParams params) {
      super(buffer, lineParser, params);
      this.requestFactory = (HttpRequestFactory)Args.notNull(requestFactory, "Request factory");
      this.lineBuf = new CharArrayBuffer(128);
   }

   public DefaultHttpRequestParser(SessionInputBuffer buffer, LineParser lineParser, HttpRequestFactory requestFactory, MessageConstraints constraints) {
      super(buffer, lineParser, constraints);
      this.requestFactory = (HttpRequestFactory)(requestFactory != null?requestFactory:DefaultHttpRequestFactory.INSTANCE);
      this.lineBuf = new CharArrayBuffer(128);
   }

   public DefaultHttpRequestParser(SessionInputBuffer buffer, MessageConstraints constraints) {
      this(buffer, (LineParser)null, (HttpRequestFactory)null, (MessageConstraints)constraints);
   }

   public DefaultHttpRequestParser(SessionInputBuffer buffer) {
      this(buffer, (LineParser)null, (HttpRequestFactory)null, (MessageConstraints)MessageConstraints.DEFAULT);
   }

   protected HttpRequest parseHead(SessionInputBuffer sessionBuffer) throws IOException, HttpException, ParseException {
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
