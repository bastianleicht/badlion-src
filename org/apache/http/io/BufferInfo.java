package org.apache.http.io;

public interface BufferInfo {
   int length();

   int capacity();

   int available();
}
