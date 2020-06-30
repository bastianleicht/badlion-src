package org.apache.http.client.entity;

import java.io.File;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.entity.GzipCompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.SerializableEntity;
import org.apache.http.entity.StringEntity;

@NotThreadSafe
public class EntityBuilder {
   private String text;
   private byte[] binary;
   private InputStream stream;
   private List parameters;
   private Serializable serializable;
   private File file;
   private ContentType contentType;
   private String contentEncoding;
   private boolean chunked;
   private boolean gzipCompress;

   public static EntityBuilder create() {
      return new EntityBuilder();
   }

   private void clearContent() {
      this.text = null;
      this.binary = null;
      this.stream = null;
      this.parameters = null;
      this.serializable = null;
      this.file = null;
   }

   public String getText() {
      return this.text;
   }

   public EntityBuilder setText(String text) {
      this.clearContent();
      this.text = text;
      return this;
   }

   public byte[] getBinary() {
      return this.binary;
   }

   public EntityBuilder setBinary(byte[] binary) {
      this.clearContent();
      this.binary = binary;
      return this;
   }

   public InputStream getStream() {
      return this.stream;
   }

   public EntityBuilder setStream(InputStream stream) {
      this.clearContent();
      this.stream = stream;
      return this;
   }

   public List getParameters() {
      return this.parameters;
   }

   public EntityBuilder setParameters(List parameters) {
      this.clearContent();
      this.parameters = parameters;
      return this;
   }

   public EntityBuilder setParameters(NameValuePair... parameters) {
      return this.setParameters(Arrays.asList(parameters));
   }

   public Serializable getSerializable() {
      return this.serializable;
   }

   public EntityBuilder setSerializable(Serializable serializable) {
      this.clearContent();
      this.serializable = serializable;
      return this;
   }

   public File getFile() {
      return this.file;
   }

   public EntityBuilder setFile(File file) {
      this.clearContent();
      this.file = file;
      return this;
   }

   public ContentType getContentType() {
      return this.contentType;
   }

   public EntityBuilder setContentType(ContentType contentType) {
      this.contentType = contentType;
      return this;
   }

   public String getContentEncoding() {
      return this.contentEncoding;
   }

   public EntityBuilder setContentEncoding(String contentEncoding) {
      this.contentEncoding = contentEncoding;
      return this;
   }

   public boolean isChunked() {
      return this.chunked;
   }

   public EntityBuilder chunked() {
      this.chunked = true;
      return this;
   }

   public boolean isGzipCompress() {
      return this.gzipCompress;
   }

   public EntityBuilder gzipCompress() {
      this.gzipCompress = true;
      return this;
   }

   private ContentType getContentOrDefault(ContentType def) {
      return this.contentType != null?this.contentType:def;
   }

   public HttpEntity build() {
      AbstractHttpEntity e;
      if(this.text != null) {
         e = new StringEntity(this.text, this.getContentOrDefault(ContentType.DEFAULT_TEXT));
      } else if(this.binary != null) {
         e = new ByteArrayEntity(this.binary, this.getContentOrDefault(ContentType.DEFAULT_BINARY));
      } else if(this.stream != null) {
         e = new InputStreamEntity(this.stream, 1L, this.getContentOrDefault(ContentType.DEFAULT_BINARY));
      } else if(this.parameters != null) {
         e = new UrlEncodedFormEntity(this.parameters, this.contentType != null?this.contentType.getCharset():null);
      } else if(this.serializable != null) {
         e = new SerializableEntity(this.serializable);
         e.setContentType(ContentType.DEFAULT_BINARY.toString());
      } else if(this.file != null) {
         e = new FileEntity(this.file, this.getContentOrDefault(ContentType.DEFAULT_BINARY));
      } else {
         e = new BasicHttpEntity();
      }

      if(e.getContentType() != null && this.contentType != null) {
         e.setContentType(this.contentType.toString());
      }

      e.setContentEncoding(this.contentEncoding);
      e.setChunked(this.chunked);
      return (HttpEntity)(this.gzipCompress?new GzipCompressingEntity(e):e);
   }
}
