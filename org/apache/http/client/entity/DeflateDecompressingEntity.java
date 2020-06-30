package org.apache.http.client.entity;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.DecompressingEntity;
import org.apache.http.client.entity.DeflateInputStream;

public class DeflateDecompressingEntity extends DecompressingEntity {
   public DeflateDecompressingEntity(HttpEntity entity) {
      super(entity);
   }

   InputStream decorate(InputStream wrapped) throws IOException {
      return new DeflateInputStream(wrapped);
   }

   public Header getContentEncoding() {
      return null;
   }

   public long getContentLength() {
      return -1L;
   }
}
