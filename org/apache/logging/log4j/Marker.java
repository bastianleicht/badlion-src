package org.apache.logging.log4j;

import java.io.Serializable;

public interface Marker extends Serializable {
   String getName();

   Marker getParent();

   boolean isInstanceOf(Marker var1);

   boolean isInstanceOf(String var1);
}
