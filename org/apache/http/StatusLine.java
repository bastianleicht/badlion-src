package org.apache.http;

import org.apache.http.ProtocolVersion;

public interface StatusLine {
   ProtocolVersion getProtocolVersion();

   int getStatusCode();

   String getReasonPhrase();
}
