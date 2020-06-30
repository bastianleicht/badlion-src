package com.ibm.icu.text;

import com.ibm.icu.impl.ICUConfig;
import com.ibm.icu.impl.SoftCache;
import com.ibm.icu.impl.TimeZoneNamesImpl;
import com.ibm.icu.util.ULocale;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public abstract class TimeZoneNames implements Serializable {
   private static final long serialVersionUID = -9180227029248969153L;
   private static TimeZoneNames.Cache TZNAMES_CACHE = new TimeZoneNames.Cache();
   private static final TimeZoneNames.Factory TZNAMES_FACTORY;
   private static final String FACTORY_NAME_PROP = "com.ibm.icu.text.TimeZoneNames.Factory.impl";
   private static final String DEFAULT_FACTORY_CLASS = "com.ibm.icu.impl.TimeZoneNamesFactoryImpl";

   public static TimeZoneNames getInstance(ULocale locale) {
      String key = locale.getBaseName();
      return (TimeZoneNames)TZNAMES_CACHE.getInstance(key, locale);
   }

   public abstract Set getAvailableMetaZoneIDs();

   public abstract Set getAvailableMetaZoneIDs(String var1);

   public abstract String getMetaZoneID(String var1, long var2);

   public abstract String getReferenceZoneID(String var1, String var2);

   public abstract String getMetaZoneDisplayName(String var1, TimeZoneNames.NameType var2);

   public final String getDisplayName(String tzID, TimeZoneNames.NameType type, long date) {
      String name = this.getTimeZoneDisplayName(tzID, type);
      if(name == null) {
         String mzID = this.getMetaZoneID(tzID, date);
         name = this.getMetaZoneDisplayName(mzID, type);
      }

      return name;
   }

   public abstract String getTimeZoneDisplayName(String var1, TimeZoneNames.NameType var2);

   public String getExemplarLocationName(String tzID) {
      return TimeZoneNamesImpl.getDefaultExemplarLocationName(tzID);
   }

   public Collection find(CharSequence text, int start, EnumSet types) {
      throw new UnsupportedOperationException("The method is not implemented in TimeZoneNames base class.");
   }

   static {
      TimeZoneNames.Factory factory = null;
      String classname = ICUConfig.get("com.ibm.icu.text.TimeZoneNames.Factory.impl", "com.ibm.icu.impl.TimeZoneNamesFactoryImpl");

      while(true) {
         try {
            factory = (TimeZoneNames.Factory)Class.forName(classname).newInstance();
            break;
         } catch (ClassNotFoundException var3) {
            ;
         } catch (IllegalAccessException var4) {
            ;
         } catch (InstantiationException var5) {
            ;
         }

         if(classname.equals("com.ibm.icu.impl.TimeZoneNamesFactoryImpl")) {
            break;
         }

         classname = "com.ibm.icu.impl.TimeZoneNamesFactoryImpl";
      }

      if(factory == null) {
         factory = new TimeZoneNames.DefaultTimeZoneNames.FactoryImpl();
      }

      TZNAMES_FACTORY = factory;
   }

   private static class Cache extends SoftCache {
      private Cache() {
      }

      protected TimeZoneNames createInstance(String key, ULocale data) {
         return TimeZoneNames.TZNAMES_FACTORY.getTimeZoneNames(data);
      }
   }

   private static class DefaultTimeZoneNames extends TimeZoneNames {
      private static final long serialVersionUID = -995672072494349071L;
      public static final TimeZoneNames.DefaultTimeZoneNames INSTANCE = new TimeZoneNames.DefaultTimeZoneNames();

      public Set getAvailableMetaZoneIDs() {
         return Collections.emptySet();
      }

      public Set getAvailableMetaZoneIDs(String tzID) {
         return Collections.emptySet();
      }

      public String getMetaZoneID(String tzID, long date) {
         return null;
      }

      public String getReferenceZoneID(String mzID, String region) {
         return null;
      }

      public String getMetaZoneDisplayName(String mzID, TimeZoneNames.NameType type) {
         return null;
      }

      public String getTimeZoneDisplayName(String tzID, TimeZoneNames.NameType type) {
         return null;
      }

      public Collection find(CharSequence text, int start, EnumSet nameTypes) {
         return Collections.emptyList();
      }

      public static class FactoryImpl extends TimeZoneNames.Factory {
         public TimeZoneNames getTimeZoneNames(ULocale locale) {
            return TimeZoneNames.DefaultTimeZoneNames.INSTANCE;
         }
      }
   }

   public abstract static class Factory {
      public abstract TimeZoneNames getTimeZoneNames(ULocale var1);
   }

   public static class MatchInfo {
      private TimeZoneNames.NameType _nameType;
      private String _tzID;
      private String _mzID;
      private int _matchLength;

      public MatchInfo(TimeZoneNames.NameType nameType, String tzID, String mzID, int matchLength) {
         if(nameType == null) {
            throw new IllegalArgumentException("nameType is null");
         } else if(tzID == null && mzID == null) {
            throw new IllegalArgumentException("Either tzID or mzID must be available");
         } else if(matchLength <= 0) {
            throw new IllegalArgumentException("matchLength must be positive value");
         } else {
            this._nameType = nameType;
            this._tzID = tzID;
            this._mzID = mzID;
            this._matchLength = matchLength;
         }
      }

      public String tzID() {
         return this._tzID;
      }

      public String mzID() {
         return this._mzID;
      }

      public TimeZoneNames.NameType nameType() {
         return this._nameType;
      }

      public int matchLength() {
         return this._matchLength;
      }
   }

   public static enum NameType {
      LONG_GENERIC,
      LONG_STANDARD,
      LONG_DAYLIGHT,
      SHORT_GENERIC,
      SHORT_STANDARD,
      SHORT_DAYLIGHT,
      EXEMPLAR_LOCATION;
   }
}
