package oshi.software.os.windows.nt;

import oshi.hardware.Processor;

public class CentralProcessor implements Processor {
   private String _vendor;
   private String _name;
   private String _identifier;

   public String getVendor() {
      return this._vendor;
   }

   public void setVendor(String vendor) {
      this._vendor = vendor;
   }

   public String getName() {
      return this._name;
   }

   public void setName(String name) {
      this._name = name;
   }

   public String getIdentifier() {
      return this._identifier;
   }

   public void setIdentifier(String identifier) {
      this._identifier = identifier;
   }

   public boolean isCpu64bit() {
      throw new UnsupportedOperationException();
   }

   public void setCpu64(boolean cpu64) {
      throw new UnsupportedOperationException();
   }

   public String getStepping() {
      throw new UnsupportedOperationException();
   }

   public void setStepping(String _stepping) {
      throw new UnsupportedOperationException();
   }

   public String getModel() {
      throw new UnsupportedOperationException();
   }

   public void setModel(String _model) {
      throw new UnsupportedOperationException();
   }

   public String getFamily() {
      throw new UnsupportedOperationException();
   }

   public void setFamily(String _family) {
      throw new UnsupportedOperationException();
   }

   public float getLoad() {
      throw new UnsupportedOperationException();
   }

   public String toString() {
      return this._name;
   }
}
