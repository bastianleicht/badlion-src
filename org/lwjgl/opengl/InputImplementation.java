package org.lwjgl.opengl;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import org.lwjgl.LWJGLException;

public interface InputImplementation {
   boolean hasWheel();

   int getButtonCount();

   void createMouse() throws LWJGLException;

   void destroyMouse();

   void pollMouse(IntBuffer var1, ByteBuffer var2);

   void readMouse(ByteBuffer var1);

   void grabMouse(boolean var1);

   int getNativeCursorCapabilities();

   void setCursorPosition(int var1, int var2);

   void setNativeCursor(Object var1) throws LWJGLException;

   int getMinCursorSize();

   int getMaxCursorSize();

   void createKeyboard() throws LWJGLException;

   void destroyKeyboard();

   void pollKeyboard(ByteBuffer var1);

   void readKeyboard(ByteBuffer var1);

   Object createCursor(int var1, int var2, int var3, int var4, int var5, IntBuffer var6, IntBuffer var7) throws LWJGLException;

   void destroyCursor(Object var1);

   int getWidth();

   int getHeight();

   boolean isInsideWindow();
}
