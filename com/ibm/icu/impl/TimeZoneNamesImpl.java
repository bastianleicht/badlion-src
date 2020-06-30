package com.ibm.icu.impl;

import com.ibm.icu.impl.Grego;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.SoftCache;
import com.ibm.icu.impl.TextTrieMap;
import com.ibm.icu.impl.ZoneMeta;
import com.ibm.icu.text.TimeZoneNames;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.ULocale;
import com.ibm.icu.util.UResourceBundle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public class TimeZoneNamesImpl extends TimeZoneNames {
   private static final long serialVersionUID = -2179814848495897472L;
   private static final String ZONE_STRINGS_BUNDLE = "zoneStrings";
   private static final String MZ_PREFIX = "meta:";
   private static Set METAZONE_IDS;
   private static final TimeZoneNamesImpl.TZ2MZsCache TZ_TO_MZS_CACHE = new TimeZoneNamesImpl.TZ2MZsCache();
   private static final TimeZoneNamesImpl.MZ2TZsCache MZ_TO_TZS_CACHE = new TimeZoneNamesImpl.MZ2TZsCache();
   private transient ICUResourceBundle _zoneStrings;
   private transient ConcurrentHashMap _mzNamesMap;
   private transient ConcurrentHashMap _tzNamesMap;
   private transient TextTrieMap _namesTrie;
   private transient boolean _namesTrieFullyLoaded;
   private static final Pattern LOC_EXCLUSION_PATTERN = Pattern.compile("Etc/.*|SystemV/.*|.*/Riyadh8[7-9]");

   public TimeZoneNamesImpl(ULocale locale) {
      this.initialize(locale);
   }

   public synchronized Set getAvailableMetaZoneIDs() {
      if(METAZONE_IDS == null) {
         UResourceBundle bundle = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "metaZones");
         UResourceBundle mapTimezones = bundle.get("mapTimezones");
         Set<String> keys = mapTimezones.keySet();
         METAZONE_IDS = Collections.unmodifiableSet(keys);
      }

      return METAZONE_IDS;
   }

   public Set getAvailableMetaZoneIDs(String tzID) {
      if(tzID != null && tzID.length() != 0) {
         List<TimeZoneNamesImpl.MZMapEntry> maps = (List)TZ_TO_MZS_CACHE.getInstance(tzID, tzID);
         if(maps.isEmpty()) {
            return Collections.emptySet();
         } else {
            Set<String> mzIDs = new HashSet(maps.size());

            for(TimeZoneNamesImpl.MZMapEntry map : maps) {
               mzIDs.add(map.mzID());
            }

            return Collections.unmodifiableSet(mzIDs);
         }
      } else {
         return Collections.emptySet();
      }
   }

   public String getMetaZoneID(String tzID, long date) {
      if(tzID != null && tzID.length() != 0) {
         String mzID = null;

         for(TimeZoneNamesImpl.MZMapEntry map : (List)TZ_TO_MZS_CACHE.getInstance(tzID, tzID)) {
            if(date >= map.from() && date < map.to()) {
               mzID = map.mzID();
               break;
            }
         }

         return mzID;
      } else {
         return null;
      }
   }

   public String getReferenceZoneID(String mzID, String region) {
      if(mzID != null && mzID.length() != 0) {
         String refID = null;
         Map<String, String> regionTzMap = (Map)MZ_TO_TZS_CACHE.getInstance(mzID, mzID);
         if(!regionTzMap.isEmpty()) {
            refID = (String)regionTzMap.get(region);
            if(refID == null) {
               refID = (String)regionTzMap.get("001");
            }
         }

         return refID;
      } else {
         return null;
      }
   }

   public String getMetaZoneDisplayName(String mzID, TimeZoneNames.NameType type) {
      return mzID != null && mzID.length() != 0?this.loadMetaZoneNames(mzID).getName(type):null;
   }

   public String getTimeZoneDisplayName(String tzID, TimeZoneNames.NameType type) {
      return tzID != null && tzID.length() != 0?this.loadTimeZoneNames(tzID).getName(type):null;
   }

   public String getExemplarLocationName(String tzID) {
      if(tzID != null && tzID.length() != 0) {
         String locName = this.loadTimeZoneNames(tzID).getName(TimeZoneNames.NameType.EXEMPLAR_LOCATION);
         return locName;
      } else {
         return null;
      }
   }

   public synchronized Collection find(CharSequence text, int start, EnumSet nameTypes) {
      if(text != null && text.length() != 0 && start >= 0 && start < text.length()) {
         TimeZoneNamesImpl.NameSearchHandler handler = new TimeZoneNamesImpl.NameSearchHandler(nameTypes);
         this._namesTrie.find(text, start, handler);
         if(handler.getMaxMatchLen() != text.length() - start && !this._namesTrieFullyLoaded) {
            for(String tzID : TimeZone.getAvailableIDs(TimeZone.SystemTimeZoneType.CANONICAL, (String)null, (Integer)null)) {
               this.loadTimeZoneNames(tzID);
            }

            for(String mzID : this.getAvailableMetaZoneIDs()) {
               this.loadMetaZoneNames(mzID);
            }

            this._namesTrieFullyLoaded = true;
            handler.resetResults();
            this._namesTrie.find(text, start, handler);
            return handler.getMatches();
         } else {
            return handler.getMatches();
         }
      } else {
         throw new IllegalArgumentException("bad input text or range");
      }
   }

   private void initialize(ULocale locale) {
      ICUResourceBundle bundle = (ICUResourceBundle)ICUResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b/zone", locale);
      this._zoneStrings = (ICUResourceBundle)bundle.get("zoneStrings");
      this._tzNamesMap = new ConcurrentHashMap();
      this._mzNamesMap = new ConcurrentHashMap();
      this._namesTrie = new TextTrieMap(true);
      this._namesTrieFullyLoaded = false;
      TimeZone tz = TimeZone.getDefault();
      String tzCanonicalID = ZoneMeta.getCanonicalCLDRID(tz);
      if(tzCanonicalID != null) {
         this.loadStrings(tzCanonicalID);
      }

   }

   private synchronized void loadStrings(String tzCanonicalID) {
      if(tzCanonicalID != null && tzCanonicalID.length() != 0) {
         this.loadTimeZoneNames(tzCanonicalID);

         for(String mzID : this.getAvailableMetaZoneIDs(tzCanonicalID)) {
            this.loadMetaZoneNames(mzID);
         }

      }
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      ULocale locale = this._zoneStrings.getULocale();
      out.writeObject(locale);
   }

   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
      ULocale locale = (ULocale)in.readObject();
      this.initialize(locale);
   }

   private synchronized TimeZoneNamesImpl.ZNames loadMetaZoneNames(String mzID) {
      TimeZoneNamesImpl.ZNames znames = (TimeZoneNamesImpl.ZNames)this._mzNamesMap.get(mzID);
      if(znames == null) {
         znames = TimeZoneNamesImpl.ZNames.getInstance(this._zoneStrings, "meta:" + mzID);
         mzID = mzID.intern();

         for(TimeZoneNames.NameType t : TimeZoneNames.NameType.values()) {
            String name = znames.getName(t);
            if(name != null) {
               TimeZoneNamesImpl.NameInfo info = new TimeZoneNamesImpl.NameInfo();
               info.mzID = mzID;
               info.type = t;
               this._namesTrie.put(name, info);
            }
         }

         TimeZoneNamesImpl.ZNames tmpZnames = (TimeZoneNamesImpl.ZNames)this._mzNamesMap.putIfAbsent(mzID, znames);
         znames = tmpZnames == null?znames:tmpZnames;
      }

      return znames;
   }

   private synchronized TimeZoneNamesImpl.TZNames loadTimeZoneNames(String tzID) {
      TimeZoneNamesImpl.TZNames tznames = (TimeZoneNamesImpl.TZNames)this._tzNamesMap.get(tzID);
      if(tznames == null) {
         tznames = TimeZoneNamesImpl.TZNames.getInstance(this._zoneStrings, tzID.replace('/', ':'), tzID);
         tzID = tzID.intern();

         for(TimeZoneNames.NameType t : TimeZoneNames.NameType.values()) {
            String name = tznames.getName(t);
            if(name != null) {
               TimeZoneNamesImpl.NameInfo info = new TimeZoneNamesImpl.NameInfo();
               info.tzID = tzID;
               info.type = t;
               this._namesTrie.put(name, info);
            }
         }

         TimeZoneNamesImpl.TZNames tmpTznames = (TimeZoneNamesImpl.TZNames)this._tzNamesMap.putIfAbsent(tzID, tznames);
         tznames = tmpTznames == null?tznames:tmpTznames;
      }

      return tznames;
   }

   public static String getDefaultExemplarLocationName(String tzID) {
      if(tzID != null && tzID.length() != 0 && !LOC_EXCLUSION_PATTERN.matcher(tzID).matches()) {
         String location = null;
         int sep = tzID.lastIndexOf(47);
         if(sep > 0 && sep + 1 < tzID.length()) {
            location = tzID.substring(sep + 1).replace('_', ' ');
         }

         return location;
      } else {
         return null;
      }
   }

   private static class MZ2TZsCache extends SoftCache {
      private MZ2TZsCache() {
      }

      protected Map createInstance(String key, String data) {
         Map<String, String> map = null;
         UResourceBundle bundle = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "metaZones");
         UResourceBundle mapTimezones = bundle.get("mapTimezones");

         try {
            UResourceBundle regionMap = mapTimezones.get(key);
            Set<String> regions = regionMap.keySet();
            map = new HashMap(regions.size());

            for(String region : regions) {
               String tzID = regionMap.getString(region).intern();
               map.put(region.intern(), tzID);
            }
         } catch (MissingResourceException var11) {
            map = Collections.emptyMap();
         }

         return map;
      }
   }

   private static class MZMapEntry {
      private String _mzID;
      private long _from;
      private long _to;

      MZMapEntry(String mzID, long from, long to) {
         this._mzID = mzID;
         this._from = from;
         this._to = to;
      }

      String mzID() {
         return this._mzID;
      }

      long from() {
         return this._from;
      }

      long to() {
         return this._to;
      }
   }

   private static class NameInfo {
      String tzID;
      String mzID;
      TimeZoneNames.NameType type;

      private NameInfo() {
      }
   }

   private static class NameSearchHandler implements TextTrieMap.ResultHandler {
      private EnumSet _nameTypes;
      private Collection _matches;
      private int _maxMatchLen;

      NameSearchHandler(EnumSet nameTypes) {
         this._nameTypes = nameTypes;
      }

      public boolean handlePrefixMatch(int matchLength, Iterator values) {
         while(values.hasNext()) {
            TimeZoneNamesImpl.NameInfo ninfo = (TimeZoneNamesImpl.NameInfo)values.next();
            if(this._nameTypes == null || this._nameTypes.contains(ninfo.type)) {
               TimeZoneNames.MatchInfo minfo;
               if(ninfo.tzID != null) {
                  minfo = new TimeZoneNames.MatchInfo(ninfo.type, ninfo.tzID, (String)null, matchLength);
               } else {
                  assert ninfo.mzID != null;

                  minfo = new TimeZoneNames.MatchInfo(ninfo.type, (String)null, ninfo.mzID, matchLength);
               }

               if(this._matches == null) {
                  this._matches = new LinkedList();
               }

               this._matches.add(minfo);
               if(matchLength > this._maxMatchLen) {
                  this._maxMatchLen = matchLength;
               }
            }
         }

         return true;
      }

      public Collection getMatches() {
         return (Collection)(this._matches == null?Collections.emptyList():this._matches);
      }

      public int getMaxMatchLen() {
         return this._maxMatchLen;
      }

      public void resetResults() {
         this._matches = null;
         this._maxMatchLen = 0;
      }
   }

   private static class TZ2MZsCache extends SoftCache {
      private TZ2MZsCache() {
      }

      protected List createInstance(String key, String data) {
         List<TimeZoneNamesImpl.MZMapEntry> mzMaps = null;
         UResourceBundle bundle = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "metaZones");
         UResourceBundle metazoneInfoBundle = bundle.get("metazoneInfo");
         String tzkey = data.replace('/', ':');

         try {
            UResourceBundle zoneBundle = metazoneInfoBundle.get(tzkey);
            mzMaps = new ArrayList(zoneBundle.getSize());

            for(int idx = 0; idx < zoneBundle.getSize(); ++idx) {
               UResourceBundle mz = zoneBundle.get(idx);
               String mzid = mz.getString(0);
               String fromStr = "1970-01-01 00:00";
               String toStr = "9999-12-31 23:59";
               if(mz.getSize() == 3) {
                  fromStr = mz.getString(1);
                  toStr = mz.getString(2);
               }

               long from = parseDate(fromStr);
               long to = parseDate(toStr);
               mzMaps.add(new TimeZoneNamesImpl.MZMapEntry(mzid, from, to));
            }
         } catch (MissingResourceException var17) {
            mzMaps = Collections.emptyList();
         }

         return mzMaps;
      }

      private static long parseDate(String text) {
         int year = 0;
         int month = 0;
         int day = 0;
         int hour = 0;
         int min = 0;

         for(int idx = 0; idx <= 3; ++idx) {
            int n = text.charAt(idx) - 48;
            if(n < 0 || n >= 10) {
               throw new IllegalArgumentException("Bad year");
            }

            year = 10 * year + n;
         }

         for(int var10 = 5; var10 <= 6; ++var10) {
            int n = text.charAt(var10) - 48;
            if(n < 0 || n >= 10) {
               throw new IllegalArgumentException("Bad month");
            }

            month = 10 * month + n;
         }

         for(int var11 = 8; var11 <= 9; ++var11) {
            int n = text.charAt(var11) - 48;
            if(n < 0 || n >= 10) {
               throw new IllegalArgumentException("Bad day");
            }

            day = 10 * day + n;
         }

         for(int var12 = 11; var12 <= 12; ++var12) {
            int n = text.charAt(var12) - 48;
            if(n < 0 || n >= 10) {
               throw new IllegalArgumentException("Bad hour");
            }

            hour = 10 * hour + n;
         }

         for(int var13 = 14; var13 <= 15; ++var13) {
            int n = text.charAt(var13) - 48;
            if(n < 0 || n >= 10) {
               throw new IllegalArgumentException("Bad minute");
            }

            min = 10 * min + n;
         }

         long date = Grego.fieldsToDay(year, month - 1, day) * 86400000L + (long)hour * 3600000L + (long)min * 60000L;
         return date;
      }
   }

   private static class TZNames extends TimeZoneNamesImpl.ZNames {
      private String _locationName;
      private static final TimeZoneNamesImpl.TZNames EMPTY_TZNAMES = new TimeZoneNamesImpl.TZNames((String[])null, (String)null);

      public static TimeZoneNamesImpl.TZNames getInstance(ICUResourceBundle zoneStrings, String key, String tzID) {
         if(zoneStrings != null && key != null && key.length() != 0) {
            String[] names = loadData(zoneStrings, key);
            String locationName = null;
            ICUResourceBundle table = null;

            try {
               table = zoneStrings.getWithFallback(key);
               locationName = table.getStringWithFallback("ec");
            } catch (MissingResourceException var7) {
               ;
            }

            if(locationName == null) {
               locationName = TimeZoneNamesImpl.getDefaultExemplarLocationName(tzID);
            }

            return locationName == null && names == null?EMPTY_TZNAMES:new TimeZoneNamesImpl.TZNames(names, locationName);
         } else {
            return EMPTY_TZNAMES;
         }
      }

      public String getName(TimeZoneNames.NameType type) {
         return type == TimeZoneNames.NameType.EXEMPLAR_LOCATION?this._locationName:super.getName(type);
      }

      private TZNames(String[] names, String locationName) {
         super(names);
         this._locationName = locationName;
      }
   }

   private static class ZNames {
      private static final TimeZoneNamesImpl.ZNames EMPTY_ZNAMES = new TimeZoneNamesImpl.ZNames((String[])null);
      private String[] _names;
      private static final String[] KEYS = new String[]{"lg", "ls", "ld", "sg", "ss", "sd"};

      protected ZNames(String[] names) {
         this._names = names;
      }

      public static TimeZoneNamesImpl.ZNames getInstance(ICUResourceBundle zoneStrings, String key) {
         String[] names = loadData(zoneStrings, key);
         return names == null?EMPTY_ZNAMES:new TimeZoneNamesImpl.ZNames(names);
      }

      public String getName(TimeZoneNames.NameType type) {
         if(this._names == null) {
            return null;
         } else {
            String name = null;
            switch(type) {
            case LONG_GENERIC:
               name = this._names[0];
               break;
            case LONG_STANDARD:
               name = this._names[1];
               break;
            case LONG_DAYLIGHT:
               name = this._names[2];
               break;
            case SHORT_GENERIC:
               name = this._names[3];
               break;
            case SHORT_STANDARD:
               name = this._names[4];
               break;
            case SHORT_DAYLIGHT:
               name = this._names[5];
               break;
            case EXEMPLAR_LOCATION:
               name = null;
            }

            return name;
         }
      }

      protected static String[] loadData(ICUResourceBundle zoneStrings, String key) {
         if(zoneStrings != null && key != null && key.length() != 0) {
            ICUResourceBundle table = null;

            try {
               table = zoneStrings.getWithFallback(key);
            } catch (MissingResourceException var8) {
               return null;
            }

            boolean isEmpty = true;
            String[] names = new String[KEYS.length];

            for(int i = 0; i < names.length; ++i) {
               try {
                  names[i] = table.getStringWithFallback(KEYS[i]);
                  isEmpty = false;
               } catch (MissingResourceException var7) {
                  names[i] = null;
               }
            }

            return isEmpty?null:names;
         } else {
            return null;
         }
      }
   }
}
