package org.apache.http.client.entity;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

@NotThreadSafe
public class UrlEncodedFormEntity extends StringEntity {
   public UrlEncodedFormEntity(List parameters, String charset) throws UnsupportedEncodingException {
      super(URLEncodedUtils.format(parameters, charset != null?charset:HTTP.DEF_CONTENT_CHARSET.name()), ContentType.create("application/x-www-form-urlencoded", charset));
   }

   public UrlEncodedFormEntity(Iterable parameters, Charset charset) {
      super(URLEncodedUtils.format(parameters, charset != null?charset:HTTP.DEF_CONTENT_CHARSET), ContentType.create("application/x-www-form-urlencoded", charset));
   }

   public UrlEncodedFormEntity(List parameters) throws UnsupportedEncodingException {
      this((Iterable)parameters, (Charset)((Charset)null));
   }

   public UrlEncodedFormEntity(Iterable parameters) {
      this((Iterable)parameters, (Charset)null);
   }
}
