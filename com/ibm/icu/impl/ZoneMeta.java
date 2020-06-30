package com.ibm.icu.impl;

import com.ibm.icu.impl.ICUCache;
import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.impl.OlsonTimeZone;
import com.ibm.icu.impl.SimpleCache;
import com.ibm.icu.impl.SoftCache;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.util.Output;
import com.ibm.icu.util.SimpleTimeZone;
import com.ibm.icu.util.TimeZone;
import com.ibm.icu.util.UResourceBundle;
import java.lang.ref.SoftReference;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Set;
import java.util.TreeSet;

public final class ZoneMeta {
   private static final boolean ASSERT = false;
   private static final String ZONEINFORESNAME = "zoneinfo64";
   private static final String kREGIONS = "Regions";
   private static final String kZONES = "Zones";
   private static final String kNAMES = "Names";
   private static final String kGMT_ID = "GMT";
   private static final String kCUSTOM_TZ_PREFIX = "GMT";
   private static final String kWorld = "001";
   private static SoftReference REF_SYSTEM_ZONES;
   private static SoftReference REF_CANONICAL_SYSTEM_ZONES;
   private static SoftReference REF_CANONICAL_SYSTEM_LOCATION_ZONES;
   private static String[] ZONEIDS = null;
   private static ICUCache CANONICAL_ID_CACHE = new SimpleCache();
   private static ICUCache REGION_CACHE = new SimpleCache();
   private static ICUCache SINGLE_COUNTRY_CACHE = new SimpleCache();
   private static final ZoneMeta.SystemTimeZoneCache SYSTEM_ZONE_CACHE = new ZoneMeta.SystemTimeZoneCache();
   private static final int kMAX_CUSTOM_HOUR = 23;
   private static final int kMAX_CUSTOM_MIN = 59;
   private static final int kMAX_CUSTOM_SEC = 59;
   private static final ZoneMeta.CustomTimeZoneCache CUSTOM_ZONE_CACHE = new ZoneMeta.CustomTimeZoneCache();

   private static synchronized Set getSystemZIDs() {
      Set<String> systemZones = null;
      if(REF_SYSTEM_ZONES != null) {
         systemZones = (Set)REF_SYSTEM_ZONES.get();
      }

      if(systemZones == null) {
         Set<String> systemIDs = new TreeSet();
         String[] allIDs = getZoneIDs();

         for(String id : allIDs) {
            if(!id.equals("Etc/Unknown")) {
               systemIDs.add(id);
            }
         }

         systemZones = Collections.unmodifiableSet(systemIDs);
         REF_SYSTEM_ZONES = new SoftReference(systemZones);
      }

      return systemZones;
   }

   private static synchronized Set getCanonicalSystemZIDs() {
      Set<String> canonicalSystemZones = null;
      if(REF_CANONICAL_SYSTEM_ZONES != null) {
         canonicalSystemZones = (Set)REF_CANONICAL_SYSTEM_ZONES.get();
      }

      if(canonicalSystemZones == null) {
         Set<String> canonicalSystemIDs = new TreeSet();
         String[] allIDs = getZoneIDs();

         for(String id : allIDs) {
            if(!id.equals("Etc/Unknown")) {
               String canonicalID = getCanonicalCLDRID(id);
               if(id.equals(canonicalID)) {
                  canonicalSystemIDs.add(id);
               }
            }
         }

         canonicalSystemZones = Collections.unmodifiableSet(canonicalSystemIDs);
         REF_CANONICAL_SYSTEM_ZONES = new SoftReference(canonicalSystemZones);
      }

      return canonicalSystemZones;
   }

   private static synchronized Set getCanonicalSystemLocationZIDs() {
      Set<String> canonicalSystemLocationZones = null;
      if(REF_CANONICAL_SYSTEM_LOCATION_ZONES != null) {
         canonicalSystemLocationZones = (Set)REF_CANONICAL_SYSTEM_LOCATION_ZONES.get();
      }

      if(canonicalSystemLocationZones == null) {
         Set<String> canonicalSystemLocationIDs = new TreeSet();
         String[] allIDs = getZoneIDs();

         for(String id : allIDs) {
            if(!id.equals("Etc/Unknown")) {
               String canonicalID = getCanonicalCLDRID(id);
               if(id.equals(canonicalID)) {
                  String region = getRegion(id);
                  if(region != null && !region.equals("001")) {
                     canonicalSystemLocationIDs.add(id);
                  }
               }
            }
         }

         canonicalSystemLocationZones = Collections.unmodifiableSet(canonicalSystemLocationIDs);
         REF_CANONICAL_SYSTEM_LOCATION_ZONES = new SoftReference(canonicalSystemLocationZones);
      }

      return canonicalSystemLocationZones;
   }

   public static Set getAvailableIDs(TimeZone.SystemTimeZoneType type, String region, Integer rawOffset) {
      Set<String> baseSet = null;
      switch(type) {
      case ANY:
         baseSet = getSystemZIDs();
         break;
      case CANONICAL:
         baseSet = getCanonicalSystemZIDs();
         break;
      case CANONICAL_LOCATION:
         baseSet = getCanonicalSystemLocationZIDs();
         break;
      default:
         throw new IllegalArgumentException("Unknown SystemTimeZoneType");
      }

      if(region == null && rawOffset == null) {
         return baseSet;
      } else {
         if(region != null) {
            region = region.toUpperCase(Locale.ENGLISH);
         }

         Set<String> result = new TreeSet();
         Iterator i$ = baseSet.iterator();

         while(true) {
            String id;
            while(true) {
               while(true) {
                  if(!i$.hasNext()) {
                     if(result.isEmpty()) {
                        return Collections.emptySet();
                     }

                     return Collections.unmodifiableSet(result);
                  }

                  id = (String)i$.next();
                  if(region == null) {
                     break;
                  }

                  String r = getRegion(id);
                  if(region.equals(r)) {
                     break;
                  }
               }

               if(rawOffset == null) {
                  break;
               }

               TimeZone z = getSystemTimeZone(id);
               if(z != null && rawOffset.equals(Integer.valueOf(z.getRawOffset()))) {
                  break;
               }
            }

            result.add(id);
         }
      }
   }

   public static synchronized int countEquivalentIDs(String id) {
      int count = 0;
      UResourceBundle res = openOlsonResource((UResourceBundle)null, id);
      if(res != null) {
         try {
            UResourceBundle links = res.get("links");
            int[] v = links.getIntVector();
            count = v.length;
         } catch (MissingResourceException var5) {
            ;
         }
      }

      return count;
   }

   public static synchronized String getEquivalentID(String id, int index) {
      String result = "";
      if(index >= 0) {
         UResourceBundle res = openOlsonResource((UResourceBundle)null, id);
         if(res != null) {
            int zoneIdx = -1;

            try {
               UResourceBundle links = res.get("links");
               int[] zones = links.getIntVector();
               if(index < zones.length) {
                  zoneIdx = zones[index];
               }
            } catch (MissingResourceException var7) {
               ;
            }

            if(zoneIdx >= 0) {
               String tmp = getZoneID(zoneIdx);
               if(tmp != null) {
                  result = tmp;
               }
            }
         }
      }

      return result;
   }

   private static synchronized String[] getZoneIDs() {
      if(ZONEIDS == null) {
         try {
            UResourceBundle top = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "zoneinfo64", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
            UResourceBundle names = top.get("Names");
            ZONEIDS = names.getStringArray();
         } catch (MissingResourceException var2) {
            ;
         }
      }

      if(ZONEIDS == null) {
         ZONEIDS = new String[0];
      }

      return ZONEIDS;
   }

   private static String getZoneID(int idx) {
      if(idx >= 0) {
         String[] ids = getZoneIDs();
         if(idx < ids.length) {
            return ids[idx];
         }
      }

      return null;
   }

   private static int getZoneIndex(String zid) {
      int zoneIdx = -1;
      String[] all = getZoneIDs();
      if(all.length > 0) {
         int start = 0;
         int limit = all.length;
         int lastMid = Integer.MAX_VALUE;

         while(true) {
            int mid = (start + limit) / 2;
            if(lastMid == mid) {
               break;
            }

            lastMid = mid;
            int r = zid.compareTo(all[mid]);
            if(r == 0) {
               zoneIdx = mid;
               break;
            }

            if(r < 0) {
               limit = mid;
            } else {
               start = mid;
            }
         }
      }

      return zoneIdx;
   }

   public static String getCanonicalCLDRID(TimeZone tz) {
      return tz instanceof OlsonTimeZone?((OlsonTimeZone)tz).getCanonicalID():getCanonicalCLDRID(tz.getID());
   }

   public static String getCanonicalCLDRID(String tzid) {
      String canonical = (String)CANONICAL_ID_CACHE.get(tzid);
      if(canonical == null) {
         canonical = findCLDRCanonicalID(tzid);
         if(canonical == null) {
            try {
               int zoneIdx = getZoneIndex(tzid);
               if(zoneIdx >= 0) {
                  UResourceBundle top = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "zoneinfo64", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
                  UResourceBundle zones = top.get("Zones");
                  UResourceBundle zone = zones.get(zoneIdx);
                  if(zone.getType() == 7) {
                     tzid = getZoneID(zone.getInt());
                     canonical = findCLDRCanonicalID(tzid);
                  }

                  if(canonical == null) {
                     canonical = tzid;
                  }
               }
            } catch (MissingResourceException var6) {
               ;
            }
         }

         if(canonical != null) {
            CANONICAL_ID_CACHE.put(tzid, canonical);
         }
      }

      return canonical;
   }

   private static String findCLDRCanonicalID(String tzid) {
      String canonical = null;
      String tzidKey = tzid.replace('/', ':');

      try {
         UResourceBundle keyTypeData = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "keyTypeData", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
         UResourceBundle typeMap = keyTypeData.get("typeMap");
         UResourceBundle typeKeys = typeMap.get("timezone");

         try {
            typeKeys.get(tzidKey);
            canonical = tzid;
         } catch (MissingResourceException var8) {
            ;
         }

         if(canonical == null) {
            UResourceBundle typeAlias = keyTypeData.get("typeAlias");
            UResourceBundle aliasesForKey = typeAlias.get("timezone");
            canonical = aliasesForKey.getString(tzidKey);
         }
      } catch (MissingResourceException var9) {
         ;
      }

      return canonical;
   }

   public static String getRegion(String tzid) {
      String region = (String)REGION_CACHE.get(tzid);
      if(region == null) {
         int zoneIdx = getZoneIndex(tzid);
         if(zoneIdx >= 0) {
            try {
               UResourceBundle top = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "zoneinfo64", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
               UResourceBundle regions = top.get("Regions");
               if(zoneIdx < regions.getSize()) {
                  region = regions.getString(zoneIdx);
               }
            } catch (MissingResourceException var5) {
               ;
            }

            if(region != null) {
               REGION_CACHE.put(tzid, region);
            }
         }
      }

      return region;
   }

   public static String getCanonicalCountry(String tzid) {
      String country = getRegion(tzid);
      if(country != null && country.equals("001")) {
         country = null;
      }

      return country;
   }

   public static String getCanonicalCountry(String tzid, Output isPrimary) {
      isPrimary.value = Boolean.FALSE;
      String country = getRegion(tzid);
      if(country != null && country.equals("001")) {
         return null;
      } else {
         Boolean singleZone = (Boolean)SINGLE_COUNTRY_CACHE.get(tzid);
         if(singleZone == null) {
            Set<String> ids = TimeZone.getAvailableIDs(TimeZone.SystemTimeZoneType.CANONICAL_LOCATION, country, (Integer)null);

            assert ids.size() >= 1;

            singleZone = Boolean.valueOf(ids.size() <= 1);
            SINGLE_COUNTRY_CACHE.put(tzid, singleZone);
         }

         if(singleZone.booleanValue()) {
            isPrimary.value = Boolean.TRUE;
         } else {
            try {
               UResourceBundle bundle = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "metaZones");
               UResourceBundle primaryZones = bundle.get("primaryZones");
               String primaryZone = primaryZones.getString(country);
               if(tzid.equals(primaryZone)) {
                  isPrimary.value = Boolean.TRUE;
               } else {
                  String canonicalID = getCanonicalCLDRID(tzid);
                  if(canonicalID != null && canonicalID.equals(primaryZone)) {
                     isPrimary.value = Boolean.TRUE;
                  }
               }
            } catch (MissingResourceException var8) {
               ;
            }
         }

         return country;
      }
   }

   public static UResourceBundle openOlsonResource(UResourceBundle top, String id) {
      UResourceBundle res = null;
      int zoneIdx = getZoneIndex(id);
      if(zoneIdx >= 0) {
         try {
            if(top == null) {
               top = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "zoneinfo64", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
            }

            UResourceBundle zones = top.get("Zones");
            UResourceBundle zone = zones.get(zoneIdx);
            if(zone.getType() == 7) {
               zone = zones.get(zone.getInt());
            }

            res = zone;
         } catch (MissingResourceException var6) {
            res = null;
         }
      }

      return res;
   }

   public static TimeZone getSystemTimeZone(String id) {
      return (TimeZone)SYSTEM_ZONE_CACHE.getInstance(id, id);
   }

   public static TimeZone getCustomTimeZone(String id) {
      int[] fields = new int[4];
      if(parseCustomID(id, fields)) {
         Integer key = Integer.valueOf(fields[0] * (fields[1] | fields[2] << 5 | fields[3] << 11));
         return (TimeZone)CUSTOM_ZONE_CACHE.getInstance(key, fields);
      } else {
         return null;
      }
   }

   public static String getCustomID(String id) {
      int[] fields = new int[4];
      return parseCustomID(id, fields)?formatCustomID(fields[1], fields[2], fields[3], fields[0] < 0):null;
   }

   static boolean parseCustomID(String id, int[] fields) {
      NumberFormat numberFormat = null;
      if(id != null && id.length() > "GMT".length() && id.toUpperCase(Locale.ENGLISH).startsWith("GMT")) {
         ParsePosition pos = new ParsePosition("GMT".length());
         int sign = 1;
         int hour = 0;
         int min = 0;
         int sec = 0;
         if(id.charAt(pos.getIndex()) == 45) {
            sign = -1;
         } else if(id.charAt(pos.getIndex()) != 43) {
            return false;
         }

         pos.setIndex(pos.getIndex() + 1);
         numberFormat = NumberFormat.getInstance();
         numberFormat.setParseIntegerOnly(true);
         int start = pos.getIndex();
         Number n = numberFormat.parse(id, pos);
         if(pos.getIndex() == start) {
            return false;
         }

         hour = n.intValue();
         if(pos.getIndex() < id.length()) {
            if(pos.getIndex() - start > 2 || id.charAt(pos.getIndex()) != 58) {
               return false;
            }

            pos.setIndex(pos.getIndex() + 1);
            int oldPos = pos.getIndex();
            n = numberFormat.parse(id, pos);
            if(pos.getIndex() - oldPos != 2) {
               return false;
            }

            min = n.intValue();
            if(pos.getIndex() < id.length()) {
               if(id.charAt(pos.getIndex()) != 58) {
                  return false;
               }

               pos.setIndex(pos.getIndex() + 1);
               oldPos = pos.getIndex();
               n = numberFormat.parse(id, pos);
               if(pos.getIndex() != id.length() || pos.getIndex() - oldPos != 2) {
                  return false;
               }

               sec = n.intValue();
            }
         } else {
            int length = pos.getIndex() - start;
            if(length <= 0 || 6 < length) {
               return false;
            }

            switch(length) {
            case 1:
            case 2:
            default:
               break;
            case 3:
            case 4:
               min = hour % 100;
               hour /= 100;
               break;
            case 5:
            case 6:
               sec = hour % 100;
               min = hour / 100 % 100;
               hour /= 10000;
            }
         }

         if(hour <= 23 && min <= 59 && sec <= 59) {
            if(fields != null) {
               if(fields.length >= 1) {
                  fields[0] = sign;
               }

               if(fields.length >= 2) {
                  fields[1] = hour;
               }

               if(fields.length >= 3) {
                  fields[2] = min;
               }

               if(fields.length >= 4) {
                  fields[3] = sec;
               }
            }

            return true;
         }
      }

      return false;
   }

   public static TimeZone getCustomTimeZone(int offset) {
      boolean negative = false;
      int tmp = offset;
      if(offset < 0) {
         negative = true;
         tmp = -offset;
      }

      tmp = tmp / 1000;
      int sec = tmp % 60;
      tmp = tmp / 60;
      int min = tmp % 60;
      int hour = tmp / 60;
      String zid = formatCustomID(hour, min, sec, negative);
      return new SimpleTimeZone(offset, zid);
   }

   static String formatCustomID(int hour, int min, int sec, boolean negative) {
      StringBuilder zid = new StringBuilder("GMT");
      if(hour != 0 || min != 0) {
         if(negative) {
            zid.append('-');
         } else {
            zid.append('+');
         }

         if(hour < 10) {
            zid.append('0');
         }

         zid.append(hour);
         zid.append(':');
         if(min < 10) {
            zid.append('0');
         }

         zid.append(min);
         if(sec != 0) {
            zid.append(':');
            if(sec < 10) {
               zid.append('0');
            }

            zid.append(sec);
         }
      }

      return zid.toString();
   }

   public static String getShortID(TimeZone tz) {
      String canonicalID = null;
      if(tz instanceof OlsonTimeZone) {
         canonicalID = ((OlsonTimeZone)tz).getCanonicalID();
      }

      canonicalID = getCanonicalCLDRID(tz.getID());
      return canonicalID == null?null:getShortIDFromCanonical(canonicalID);
   }

   public static String getShortID(String id) {
      String canonicalID = getCanonicalCLDRID(id);
      return canonicalID == null?null:getShortIDFromCanonical(canonicalID);
   }

   private static String getShortIDFromCanonical(String canonicalID) {
      String shortID = null;
      String tzidKey = canonicalID.replace('/', ':');

      try {
         UResourceBundle keyTypeData = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "keyTypeData", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
         UResourceBundle typeMap = keyTypeData.get("typeMap");
         UResourceBundle typeKeys = typeMap.get("timezone");
         shortID = typeKeys.getString(tzidKey);
      } catch (MissingResourceException var6) {
         ;
      }

      return shortID;
   }

   private static class CustomTimeZoneCache extends SoftCache {
      private CustomTimeZoneCache() {
      }

      protected SimpleTimeZone createInstance(Integer key, int[] data) {
         assert data.length == 4;

         assert data[0] == 1 || data[0] == -1;

         if($assertionsDisabled || data[1] >= 0 && data[1] <= 23) {
            if($assertionsDisabled || data[2] >= 0 && data[2] <= 59) {
               if($assertionsDisabled || data[3] >= 0 && data[3] <= 59) {
                  String id = ZoneMeta.formatCustomID(data[1], data[2], data[3], data[0] < 0);
                  int offset = data[0] * ((data[1] * 60 + data[2]) * 60 + data[3]) * 1000;
                  SimpleTimeZone tz = new SimpleTimeZone(offset, id);
                  tz.freeze();
                  return tz;
               } else {
                  throw new AssertionError();
               }
            } else {
               throw new AssertionError();
            }
         } else {
            throw new AssertionError();
         }
      }
   }

   private static class SystemTimeZoneCache extends SoftCache {
      private SystemTimeZoneCache() {
      }

      protected OlsonTimeZone createInstance(String key, String data) {
         OlsonTimeZone tz = null;

         try {
            UResourceBundle top = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "zoneinfo64", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
            UResourceBundle res = ZoneMeta.openOlsonResource(top, data);
            if(res != null) {
               tz = new OlsonTimeZone(top, res, data);
               tz.freeze();
            }
         } catch (MissingResourceException var6) {
            ;
         }

         return tz;
      }
   }
}
