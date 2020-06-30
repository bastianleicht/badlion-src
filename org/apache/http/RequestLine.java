package org.apache.http;

import org.apache.http.ProtocolVersion;

public interface RequestLine {
   String getMethod();

   ProtocolVersion getProtocolVersion();

   String getUri();
}
