package oshi.software.os.linux.proc;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import oshi.hardware.Processor;
import oshi.util.FormatUtil;

public class CentralProcessor implements Processor {
   private String _vendor;
   private String _name;
   private String _identifier = null;
   private String _stepping;
   private String _model;
   private String _family;
   private boolean _cpu64;

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
      if(this._identifier == null) {
         StringBuilder sb = new StringBuilder();
         if(this.getVendor().contentEquals("GenuineIntel")) {
            sb.append(this.isCpu64bit()?"Intel64":"x86");
         } else {
            sb.append(this.getVendor());
         }

         sb.append(" Family ");
         sb.append(this.getFamily());
         sb.append(" Model ");
         sb.append(this.getModel());
         sb.append(" Stepping ");
         sb.append(this.getStepping());
         this._identifier = sb.toString();
      }

      return this._identifier;
   }

   public void setIdentifier(String identifier) {
      this._identifier = identifier;
   }

   public boolean isCpu64bit() {
      return this._cpu64;
   }

   public void setCpu64(boolean cpu64) {
      this._cpu64 = cpu64;
   }

   public String getStepping() {
      return this._stepping;
   }

   public void setStepping(String _stepping) {
      this._stepping = _stepping;
   }

   public String getModel() {
      return this._model;
   }

   public void setModel(String _model) {
      this._model = _model;
   }

   public String getFamily() {
      return this._family;
   }

   public void setFamily(String _family) {
      this._family = _family;
   }

   public float getLoad() {
      Scanner in = null;

      try {
         in = new Scanner(new FileReader("/proc/stat"));
      } catch (FileNotFoundException var8) {
         System.err.println("Problem with: /proc/stat");
         System.err.println(var8.getMessage());
         return -1.0F;
      }

      in.useDelimiter("\n");
      String[] result = in.next().split(" ");
      ArrayList<Float> loads = new ArrayList();

      for(String load : result) {
         if(load.matches("-?\\d+(\\.\\d+)?")) {
            loads.add(Float.valueOf(load));
         }
      }

      float totalCpuLoad = (((Float)loads.get(0)).floatValue() + ((Float)loads.get(2)).floatValue()) * 100.0F / (((Float)loads.get(0)).floatValue() + ((Float)loads.get(2)).floatValue() + ((Float)loads.get(3)).floatValue());
      return FormatUtil.round(totalCpuLoad, 2);
   }

   public String toString() {
      return this.getName();
   }
}
