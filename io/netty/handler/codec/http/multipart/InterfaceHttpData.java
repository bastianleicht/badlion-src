package io.netty.handler.codec.http.multipart;

import io.netty.util.ReferenceCounted;

public interface InterfaceHttpData extends Comparable, ReferenceCounted {
   String getName();

   InterfaceHttpData.HttpDataType getHttpDataType();

   public static enum HttpDataType {
      Attribute,
      FileUpload,
      InternalAttribute;
   }
}
