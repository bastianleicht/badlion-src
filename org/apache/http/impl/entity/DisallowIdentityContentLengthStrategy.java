package org.apache.http.impl.entity;

import org.apache.http.HttpException;
import org.apache.http.HttpMessage;
import org.apache.http.ProtocolException;
import org.apache.http.annotation.Immutable;
import org.apache.http.entity.ContentLengthStrategy;
import org.apache.http.impl.entity.LaxContentLengthStrategy;

@Immutable
public class DisallowIdentityContentLengthStrategy implements ContentLengthStrategy {
   public static final DisallowIdentityContentLengthStrategy INSTANCE = new DisallowIdentityContentLengthStrategy(new LaxContentLengthStrategy(0));
   private final ContentLengthStrategy contentLengthStrategy;

   public DisallowIdentityContentLengthStrategy(ContentLengthStrategy contentLengthStrategy) {
      this.contentLengthStrategy = contentLengthStrategy;
   }

   public long determineLength(HttpMessage message) throws HttpException {
      long result = this.contentLengthStrategy.determineLength(message);
      if(result == -1L) {
         throw new ProtocolException("Identity transfer encoding cannot be used");
      } else {
         return result;
      }
   }
}
