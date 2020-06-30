package org.lwjgl.util.glu.tessellation;

import org.lwjgl.util.glu.tessellation.DictNode;
import org.lwjgl.util.glu.tessellation.GLUhalfEdge;

class ActiveRegion {
   GLUhalfEdge eUp;
   DictNode nodeUp;
   int windingNumber;
   boolean inside;
   boolean sentinel;
   boolean dirty;
   boolean fixUpperEdge;
}
