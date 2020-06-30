package org.apache.http.impl.io;

import org.apache.http.annotation.Immutable;
import org.apache.http.impl.io.DefaultHttpResponseWriter;
import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.HttpMessageWriterFactory;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.message.BasicLineFormatter;
import org.apache.http.message.LineFormatter;

@Immutable
public class DefaultHttpResponseWriterFactory implements HttpMessageWriterFactory {
   public static final DefaultHttpResponseWriterFactory INSTANCE = new DefaultHttpResponseWriterFactory();
   private final LineFormatter lineFormatter;

   public DefaultHttpResponseWriterFactory(LineFormatter lineFormatter) {
      this.lineFormatter = (LineFormatter)(lineFormatter != null?lineFormatter:BasicLineFormatter.INSTANCE);
   }

   public DefaultHttpResponseWriterFactory() {
      this((LineFormatter)null);
   }

   public HttpMessageWriter create(SessionOutputBuffer buffer) {
      return new DefaultHttpResponseWriter(buffer, this.lineFormatter);
   }
}
