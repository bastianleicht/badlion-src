package org.lwjgl;

interface SysImplementation {
   int getRequiredJNIVersion();

   int getJNIVersion();

   int getPointerSize();

   void setDebug(boolean var1);

   long getTimerResolution();

   long getTime();

   void alert(String var1, String var2);

   boolean openURL(String var1);

   String getClipboard();

   boolean has64Bit();
}
