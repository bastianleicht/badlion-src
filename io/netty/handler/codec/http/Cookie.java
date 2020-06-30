package io.netty.handler.codec.http;

import java.util.Set;

public interface Cookie extends Comparable {
   String getName();

   String getValue();

   void setValue(String var1);

   String getDomain();

   void setDomain(String var1);

   String getPath();

   void setPath(String var1);

   String getComment();

   void setComment(String var1);

   long getMaxAge();

   void setMaxAge(long var1);

   int getVersion();

   void setVersion(int var1);

   boolean isSecure();

   void setSecure(boolean var1);

   boolean isHttpOnly();

   void setHttpOnly(boolean var1);

   String getCommentUrl();

   void setCommentUrl(String var1);

   boolean isDiscard();

   void setDiscard(boolean var1);

   Set getPorts();

   void setPorts(int... var1);

   void setPorts(Iterable var1);
}
