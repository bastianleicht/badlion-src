package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.multipart.HttpData;
import java.io.IOException;

public interface Attribute extends HttpData {
   String getValue() throws IOException;

   void setValue(String var1) throws IOException;

   Attribute copy();

   Attribute duplicate();

   Attribute retain();

   Attribute retain(int var1);
}
