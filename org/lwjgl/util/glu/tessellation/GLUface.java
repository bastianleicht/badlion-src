package org.lwjgl.util.glu.tessellation;

import org.lwjgl.util.glu.tessellation.GLUhalfEdge;

class GLUface {
   public GLUface next;
   public GLUface prev;
   public GLUhalfEdge anEdge;
   public Object data;
   public GLUface trail;
   public boolean marked;
   public boolean inside;
}
