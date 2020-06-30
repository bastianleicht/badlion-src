package org.apache.http;

import org.apache.http.HttpMessage;
import org.apache.http.RequestLine;

public interface HttpRequest extends HttpMessage {
   RequestLine getRequestLine();
}
