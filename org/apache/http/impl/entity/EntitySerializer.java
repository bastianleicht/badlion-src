package org.apache.http.impl.entity;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.annotation.Immutable;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.io.ChunkedOutputStream;
import org.apache.http.impl.io.ContentLengthOutputStream;
import org.apache.http.impl.io.IdentityOutputStream;
import org.apache.http.io.SessionOutputBuffer;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
@Immutable
public class EntitySerializer {
   private final ContentLengthStrategy lenStrategy;

   public EntitySerializer(ContentLengthStrategy lenStrategy) {
      this.lenStrategy = (ContentLengthStrategy)Args.notNull(lenStrategy, "Content length strategy");
   }

   protected OutputStream doSerialize(SessionOutputBuffer outbuffer, HttpMessage message) throws HttpException, IOException {
      long len = this.lenStrategy.determineLength(message);
      return (OutputStream)(len == -2L?new ChunkedOutputStream(outbuffer):(len == -1L?new IdentityOutputStream(outbuffer):new ContentLengthOutputStream(outbuffer, len)));
   }

   public void serialize(SessionOutputBuffer outbuffer, HttpMessage message, HttpEntity entity) throws HttpException, IOException {
      Args.notNull(outbuffer, "Session output buffer");
      Args.notNull(message, "HTTP message");
      Args.notNull(entity, "HTTP entity");
      OutputStream outstream = this.doSerialize(outbuffer, message);
      entity.writeTo(outstream);
      outstream.close();
   }
}
