package net.badlion.client.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SmallAverageCollector {
   private List averages = new ArrayList();
   private int collection;

   public SmallAverageCollector(int collection) {
      this.collection = collection;
   }

   public boolean isFull() {
      return this.averages.size() >= this.collection;
   }

   public void clean() {
      if(this.averages.size() > 0) {
         List<Double> list = new ArrayList();
         double d0 = Double.MAX_VALUE;
         Iterator iterator = this.averages.iterator();

         while(iterator.hasNext()) {
            double d1 = ((Double)iterator.next()).doubleValue();
            if(d1 < d0) {
               d0 = d1;
            }
         }

         iterator = this.averages.iterator();

         while(iterator.hasNext()) {
            double d2 = ((Double)iterator.next()).doubleValue();
            if(d2 != d0) {
               list.add(Double.valueOf(d2));
            } else {
               d0 = 0.0D;
            }
         }

         this.averages = list;
      }

   }

   public void add(double d) {
      if(this.averages.size() >= this.collection) {
         List<Double> list = new ArrayList();
         list.add(Double.valueOf(d));

         for(int i = 1; i < this.averages.size(); ++i) {
            list.add((Double)this.averages.get(i - 1));
         }

         this.averages = list;
      } else {
         this.averages.add(Double.valueOf(d));
      }

   }

   public List getData() {
      return this.averages;
   }

   public double getAverage() {
      double d0 = 0.0D;

      double d1;
      for(Iterator iterator = this.averages.iterator(); iterator.hasNext(); d0 += d1) {
         d1 = ((Double)iterator.next()).doubleValue();
      }

      return d0 / (double)this.averages.size();
   }

   public double getMax() {
      double d0 = -1.7976931348623157E308D;
      Iterator iterator = this.averages.iterator();

      while(iterator.hasNext()) {
         double d1 = ((Double)iterator.next()).doubleValue();
         if(d1 > d0) {
            d0 = d1;
         }
      }

      return d0;
   }

   public double getTotal() {
      double d0 = 0.0D;

      double d1;
      for(Iterator iterator = this.averages.iterator(); iterator.hasNext(); d0 += d1) {
         d1 = ((Double)iterator.next()).doubleValue();
      }

      return d0;
   }

   public int size() {
      return this.averages.size();
   }

   public void clear() {
      this.averages.clear();
   }

   public double getMin() {
      double d0 = Double.MAX_VALUE;
      Iterator iterator = this.averages.iterator();

      while(iterator.hasNext()) {
         double d1 = ((Double)iterator.next()).doubleValue();
         if(d1 < d0) {
            d0 = d1;
         }
      }

      return d0;
   }
}
