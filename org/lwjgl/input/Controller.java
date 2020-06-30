package org.lwjgl.input;

public interface Controller {
   String getName();

   int getIndex();

   int getButtonCount();

   String getButtonName(int var1);

   boolean isButtonPressed(int var1);

   void poll();

   float getPovX();

   float getPovY();

   float getDeadZone(int var1);

   void setDeadZone(int var1, float var2);

   int getAxisCount();

   String getAxisName(int var1);

   float getAxisValue(int var1);

   float getXAxisValue();

   float getXAxisDeadZone();

   void setXAxisDeadZone(float var1);

   float getYAxisValue();

   float getYAxisDeadZone();

   void setYAxisDeadZone(float var1);

   float getZAxisValue();

   float getZAxisDeadZone();

   void setZAxisDeadZone(float var1);

   float getRXAxisValue();

   float getRXAxisDeadZone();

   void setRXAxisDeadZone(float var1);

   float getRYAxisValue();

   float getRYAxisDeadZone();

   void setRYAxisDeadZone(float var1);

   float getRZAxisValue();

   float getRZAxisDeadZone();

   void setRZAxisDeadZone(float var1);

   int getRumblerCount();

   String getRumblerName(int var1);

   void setRumblerStrength(int var1, float var2);
}
