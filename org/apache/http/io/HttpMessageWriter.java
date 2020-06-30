package org.apache.http.io;

import java.io.IOException;
import org.apache.http.HttpException;
import org.apache.http.HttpMessage;

public interface HttpMessageWriter {
   void write(HttpMessage var1) throws IOException, HttpException;
}
