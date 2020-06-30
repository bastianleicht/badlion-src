package org.apache.http.io;

import org.apache.http.config.MessageConstraints;
import org.apache.http.io.HttpMessageParser;
import org.apache.http.io.SessionInputBuffer;

public interface HttpMessageParserFactory {
   HttpMessageParser create(SessionInputBuffer var1, MessageConstraints var2);
}
