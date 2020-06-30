package org.apache.http.message;

import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolVersion;
import org.apache.http.ReasonPhraseCatalog;
import org.apache.http.StatusLine;
import org.apache.http.annotation.NotThreadSafe;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.util.Args;

@NotThreadSafe
public class BasicHttpResponse extends AbstractHttpMessage implements HttpResponse {
   private StatusLine statusline;
   private ProtocolVersion ver;
   private int code;
   private String reasonPhrase;
   private HttpEntity entity;
   private final ReasonPhraseCatalog reasonCatalog;
   private Locale locale;

   public BasicHttpResponse(StatusLine statusline, ReasonPhraseCatalog catalog, Locale locale) {
      this.statusline = (StatusLine)Args.notNull(statusline, "Status line");
      this.ver = statusline.getProtocolVersion();
      this.code = statusline.getStatusCode();
      this.reasonPhrase = statusline.getReasonPhrase();
      this.reasonCatalog = catalog;
      this.locale = locale;
   }

   public BasicHttpResponse(StatusLine statusline) {
      this.statusline = (StatusLine)Args.notNull(statusline, "Status line");
      this.ver = statusline.getProtocolVersion();
      this.code = statusline.getStatusCode();
      this.reasonPhrase = statusline.getReasonPhrase();
      this.reasonCatalog = null;
      this.locale = null;
   }

   public BasicHttpResponse(ProtocolVersion ver, int code, String reason) {
      Args.notNegative(code, "Status code");
      this.statusline = null;
      this.ver = ver;
      this.code = code;
      this.reasonPhrase = reason;
      this.reasonCatalog = null;
      this.locale = null;
   }

   public ProtocolVersion getProtocolVersion() {
      return this.ver;
   }

   public StatusLine getStatusLine() {
      if(this.statusline == null) {
         this.statusline = new BasicStatusLine((ProtocolVersion)(this.ver != null?this.ver:HttpVersion.HTTP_1_1), this.code, this.reasonPhrase != null?this.reasonPhrase:this.getReason(this.code));
      }

      return this.statusline;
   }

   public HttpEntity getEntity() {
      return this.entity;
   }

   public Locale getLocale() {
      return this.locale;
   }

   public void setStatusLine(StatusLine statusline) {
      this.statusline = (StatusLine)Args.notNull(statusline, "Status line");
      this.ver = statusline.getProtocolVersion();
      this.code = statusline.getStatusCode();
      this.reasonPhrase = statusline.getReasonPhrase();
   }

   public void setStatusLine(ProtocolVersion ver, int code) {
      Args.notNegative(code, "Status code");
      this.statusline = null;
      this.ver = ver;
      this.code = code;
      this.reasonPhrase = null;
   }

   public void setStatusLine(ProtocolVersion ver, int code, String reason) {
      Args.notNegative(code, "Status code");
      this.statusline = null;
      this.ver = ver;
      this.code = code;
      this.reasonPhrase = reason;
   }

   public void setStatusCode(int code) {
      Args.notNegative(code, "Status code");
      this.statusline = null;
      this.code = code;
      this.reasonPhrase = null;
   }

   public void setReasonPhrase(String reason) {
      this.statusline = null;
      this.reasonPhrase = reason;
   }

   public void setEntity(HttpEntity entity) {
      this.entity = entity;
   }

   public void setLocale(Locale locale) {
      this.locale = (Locale)Args.notNull(locale, "Locale");
      this.statusline = null;
   }

   protected String getReason(int code) {
      return this.reasonCatalog != null?this.reasonCatalog.getReason(code, this.locale != null?this.locale:Locale.getDefault()):null;
   }

   public String toString() {
      return this.getStatusLine() + " " + this.headergroup;
   }
}
