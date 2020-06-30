package org.apache.http.params;

import java.nio.charset.CodingErrorAction;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.Args;

/** @deprecated */
@Deprecated
public final class HttpProtocolParams implements CoreProtocolPNames {
   public static String getHttpElementCharset(HttpParams params) {
      Args.notNull(params, "HTTP parameters");
      String charset = (String)params.getParameter("http.protocol.element-charset");
      if(charset == null) {
         charset = HTTP.DEF_PROTOCOL_CHARSET.name();
      }

      return charset;
   }

   public static void setHttpElementCharset(HttpParams params, String charset) {
      Args.notNull(params, "HTTP parameters");
      params.setParameter("http.protocol.element-charset", charset);
   }

   public static String getContentCharset(HttpParams params) {
      Args.notNull(params, "HTTP parameters");
      String charset = (String)params.getParameter("http.protocol.content-charset");
      if(charset == null) {
         charset = HTTP.DEF_CONTENT_CHARSET.name();
      }

      return charset;
   }

   public static void setContentCharset(HttpParams params, String charset) {
      Args.notNull(params, "HTTP parameters");
      params.setParameter("http.protocol.content-charset", charset);
   }

   public static ProtocolVersion getVersion(HttpParams params) {
      Args.notNull(params, "HTTP parameters");
      Object param = params.getParameter("http.protocol.version");
      return (ProtocolVersion)(param == null?HttpVersion.HTTP_1_1:(ProtocolVersion)param);
   }

   public static void setVersion(HttpParams params, ProtocolVersion version) {
      Args.notNull(params, "HTTP parameters");
      params.setParameter("http.protocol.version", version);
   }

   public static String getUserAgent(HttpParams params) {
      Args.notNull(params, "HTTP parameters");
      return (String)params.getParameter("http.useragent");
   }

   public static void setUserAgent(HttpParams params, String useragent) {
      Args.notNull(params, "HTTP parameters");
      params.setParameter("http.useragent", useragent);
   }

   public static boolean useExpectContinue(HttpParams params) {
      Args.notNull(params, "HTTP parameters");
      return params.getBooleanParameter("http.protocol.expect-continue", false);
   }

   public static void setUseExpectContinue(HttpParams params, boolean b) {
      Args.notNull(params, "HTTP parameters");
      params.setBooleanParameter("http.protocol.expect-continue", b);
   }

   public static CodingErrorAction getMalformedInputAction(HttpParams params) {
      Args.notNull(params, "HTTP parameters");
      Object param = params.getParameter("http.malformed.input.action");
      return param == null?CodingErrorAction.REPORT:(CodingErrorAction)param;
   }

   public static void setMalformedInputAction(HttpParams params, CodingErrorAction action) {
      Args.notNull(params, "HTTP parameters");
      params.setParameter("http.malformed.input.action", action);
   }

   public static CodingErrorAction getUnmappableInputAction(HttpParams params) {
      Args.notNull(params, "HTTP parameters");
      Object param = params.getParameter("http.unmappable.input.action");
      return param == null?CodingErrorAction.REPORT:(CodingErrorAction)param;
   }

   public static void setUnmappableInputAction(HttpParams params, CodingErrorAction action) {
      Args.notNull(params, "HTTP parameters");
      params.setParameter("http.unmappable.input.action", action);
   }
}
