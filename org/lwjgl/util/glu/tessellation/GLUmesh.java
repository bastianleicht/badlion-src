package org.lwjgl.util.glu.tessellation;

import org.lwjgl.util.glu.tessellation.GLUface;
import org.lwjgl.util.glu.tessellation.GLUhalfEdge;
import org.lwjgl.util.glu.tessellation.GLUvertex;

class GLUmesh {
   GLUvertex vHead = new GLUvertex();
   GLUface fHead = new GLUface();
   GLUhalfEdge eHead = new GLUhalfEdge(true);
   GLUhalfEdge eHeadSym = new GLUhalfEdge(false);
}
