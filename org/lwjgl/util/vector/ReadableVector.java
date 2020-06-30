package org.lwjgl.util.vector;

import java.nio.FloatBuffer;
import org.lwjgl.util.vector.Vector;

public interface ReadableVector {
   float length();

   float lengthSquared();

   Vector store(FloatBuffer var1);
}
