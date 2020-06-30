package io.netty.handler.codec.http.multipart;

import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import java.nio.charset.Charset;

public interface HttpDataFactory {
   Attribute createAttribute(HttpRequest var1, String var2);

   Attribute createAttribute(HttpRequest var1, String var2, String var3);

   FileUpload createFileUpload(HttpRequest var1, String var2, String var3, String var4, String var5, Charset var6, long var7);

   void removeHttpDataFromClean(HttpRequest var1, InterfaceHttpData var2);

   void cleanRequestHttpDatas(HttpRequest var1);

   void cleanAllHttpDatas();
}
