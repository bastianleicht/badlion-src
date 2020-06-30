package com.ibm.icu.impl;

import com.ibm.icu.impl.Grego;
import com.ibm.icu.util.TimeZone;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TreeSet;

public class JavaTimeZone extends TimeZone {
   private static final long serialVersionUID = 6977448185543929364L;
   private static final TreeSet AVAILABLESET = new TreeSet();
   private java.util.TimeZone javatz;
   private transient Calendar javacal;
   private static Method mObservesDaylightTime;
   private transient boolean isFrozen;

   public JavaTimeZone() {
      this(java.util.TimeZone.getDefault(), (String)null);
   }

   public JavaTimeZone(java.util.TimeZone jtz, String id) {
      this.isFrozen = false;
      if(id == null) {
         id = jtz.getID();
      }

      this.javatz = jtz;
      this.setID(id);
      this.javacal = new GregorianCalendar(this.javatz);
   }

   public static JavaTimeZone createTimeZone(String id) {
      java.util.TimeZone jtz = null;
      if(AVAILABLESET.contains(id)) {
         jtz = java.util.TimeZone.getTimeZone(id);
      }

      if(jtz == null) {
         boolean[] isSystemID = new boolean[1];
         String canonicalID = TimeZone.getCanonicalID(id, isSystemID);
         if(isSystemID[0] && AVAILABLESET.contains(canonicalID)) {
            jtz = java.util.TimeZone.getTimeZone(canonicalID);
         }
      }

      return jtz == null?null:new JavaTimeZone(jtz, id);
   }

   public int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds) {
      return this.javatz.getOffset(era, year, month, day, dayOfWeek, milliseconds);
   }

   public void getOffset(long date, boolean local, int[] offsets) {
      synchronized(this.javacal) {
         if(local) {
            int[] fields = new int[6];
            Grego.timeToFields(date, fields);
            int tmp = fields[5];
            int mil = tmp % 1000;
            tmp = tmp / 1000;
            int sec = tmp % 60;
            tmp = tmp / 60;
            int min = tmp % 60;
            int hour = tmp / 60;
            this.javacal.clear();
            this.javacal.set(fields[0], fields[1], fields[2], hour, min, sec);
            this.javacal.set(14, mil);
            int doy1 = this.javacal.get(6);
            int hour1 = this.javacal.get(11);
            int min1 = this.javacal.get(12);
            int sec1 = this.javacal.get(13);
            int mil1 = this.javacal.get(14);
            if(fields[4] != doy1 || hour != hour1 || min != min1 || sec != sec1 || mil != mil1) {
               int dayDelta = Math.abs(doy1 - fields[4]) > 1?1:doy1 - fields[4];
               int delta = (((dayDelta * 24 + hour1 - hour) * 60 + min1 - min) * 60 + sec1 - sec) * 1000 + mil1 - mil;
               this.javacal.setTimeInMillis(this.javacal.getTimeInMillis() - (long)delta - 1L);
            }
         } else {
            this.javacal.setTimeInMillis(date);
         }

         offsets[0] = this.javacal.get(15);
         offsets[1] = this.javacal.get(16);
      }
   }

   public int getRawOffset() {
      return this.javatz.getRawOffset();
   }

   public boolean inDaylightTime(Date date) {
      return this.javatz.inDaylightTime(date);
   }

   public void setRawOffset(int offsetMillis) {
      if(this.isFrozen()) {
         throw new UnsupportedOperationException("Attempt to modify a frozen JavaTimeZone instance.");
      } else {
         this.javatz.setRawOffset(offsetMillis);
      }
   }

   public boolean useDaylightTime() {
      return this.javatz.useDaylightTime();
   }

   public boolean observesDaylightTime() {
      if(mObservesDaylightTime != null) {
         try {
            return ((Boolean)mObservesDaylightTime.invoke(this.javatz, (Object[])null)).booleanValue();
         } catch (IllegalAccessException var2) {
            ;
         } catch (IllegalArgumentException var3) {
            ;
         } catch (InvocationTargetException var4) {
            ;
         }
      }

      return super.observesDaylightTime();
   }

   public int getDSTSavings() {
      return this.javatz.getDSTSavings();
   }

   public java.util.TimeZone unwrap() {
      return this.javatz;
   }

   public Object clone() {
      return this.isFrozen()?this:this.cloneAsThawed();
   }

   public int hashCode() {
      return super.hashCode() + this.javatz.hashCode();
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.javacal = new GregorianCalendar(this.javatz);
   }

   public boolean isFrozen() {
      return this.isFrozen;
   }

   public TimeZone freeze() {
      this.isFrozen = true;
      return this;
   }

   public TimeZone cloneAsThawed() {
      JavaTimeZone tz = (JavaTimeZone)super.cloneAsThawed();
      tz.javatz = (java.util.TimeZone)this.javatz.clone();
      tz.javacal = (GregorianCalendar)this.javacal.clone();
      tz.isFrozen = false;
      return tz;
   }

   static {
      String[] availableIds = java.util.TimeZone.getAvailableIDs();

      for(int i = 0; i < availableIds.length; ++i) {
         AVAILABLESET.add(availableIds[i]);
      }

      try {
         mObservesDaylightTime = java.util.TimeZone.class.getMethod("observesDaylightTime", (Class[])null);
      } catch (NoSuchMethodException var2) {
         ;
      } catch (SecurityException var3) {
         ;
      }

   }
}
