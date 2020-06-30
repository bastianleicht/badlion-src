package oshi.hardware;

public interface Processor {
   String getVendor();

   void setVendor(String var1);

   String getName();

   void setName(String var1);

   String getIdentifier();

   void setIdentifier(String var1);

   boolean isCpu64bit();

   void setCpu64(boolean var1);

   String getStepping();

   void setStepping(String var1);

   String getModel();

   void setModel(String var1);

   String getFamily();

   void setFamily(String var1);

   float getLoad();
}
