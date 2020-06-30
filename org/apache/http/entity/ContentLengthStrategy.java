package org.apache.http.entity;

import org.apache.http.HttpException;
import org.apache.http.HttpMessage;

public interface ContentLengthStrategy {
   int IDENTITY = -1;
   int CHUNKED = -2;

   long determineLength(HttpMessage var1) throws HttpException;
}
