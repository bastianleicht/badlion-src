package org.apache.http.io;

import org.apache.http.io.HttpMessageWriter;
import org.apache.http.io.SessionOutputBuffer;

public interface HttpMessageWriterFactory {
   HttpMessageWriter create(SessionOutputBuffer var1);
}
