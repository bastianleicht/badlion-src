package org.apache.http;

import java.util.Locale;
import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;

public interface HttpResponse extends HttpMessage {
   StatusLine getStatusLine();

   void setStatusLine(StatusLine var1);

   void setStatusLine(ProtocolVersion var1, int var2);

   void setStatusLine(ProtocolVersion var1, int var2, String var3);

   void setStatusCode(int var1) throws IllegalStateException;

   void setReasonPhrase(String var1) throws IllegalStateException;

   HttpEntity getEntity();

   void setEntity(HttpEntity var1);

   Locale getLocale();

   void setLocale(Locale var1);
}
