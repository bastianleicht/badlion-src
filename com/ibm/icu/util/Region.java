package com.ibm.icu.util;

import com.ibm.icu.impl.ICUResourceBundle;
import com.ibm.icu.util.UResourceBundle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Region implements Comparable {
   public static final int UNDEFINED_NUMERIC_CODE = -1;
   private String id;
   private int code;
   private Region.RegionType type;
   private Region containingRegion = null;
   private Set containedRegions = new TreeSet();
   private List preferredValues = null;
   private static boolean regionDataIsLoaded = false;
   private static Map regionIDMap = null;
   private static Map numericCodeMap = null;
   private static Map regionAliases = null;
   private static ArrayList regions = null;
   private static ArrayList availableRegions = null;
   private static final String UNKNOWN_REGION_ID = "ZZ";
   private static final String OUTLYING_OCEANIA_REGION_ID = "QO";
   private static final String WORLD_ID = "001";

   private static synchronized void loadRegionData() {
      if(!regionDataIsLoaded) {
         regionAliases = new HashMap();
         regionIDMap = new HashMap();
         numericCodeMap = new HashMap();
         availableRegions = new ArrayList(Region.RegionType.values().length);
         UResourceBundle regionCodes = null;
         UResourceBundle territoryAlias = null;
         UResourceBundle codeMappings = null;
         UResourceBundle worldContainment = null;
         UResourceBundle territoryContainment = null;
         UResourceBundle groupingContainment = null;
         UResourceBundle rb = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "metadata", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
         regionCodes = rb.get("regionCodes");
         territoryAlias = rb.get("territoryAlias");
         UResourceBundle rb2 = UResourceBundle.getBundleInstance("com/ibm/icu/impl/data/icudt51b", "supplementalData", ICUResourceBundle.ICU_DATA_CLASS_LOADER);
         codeMappings = rb2.get("codeMappings");
         territoryContainment = rb2.get("territoryContainment");
         worldContainment = territoryContainment.get("001");
         groupingContainment = territoryContainment.get("grouping");
         String[] continentsArr = worldContainment.getStringArray();
         List<String> continents = Arrays.asList(continentsArr);
         String[] groupingArr = groupingContainment.getStringArray();
         List<String> groupings = Arrays.asList(groupingArr);
         int regionCodeSize = regionCodes.getSize();
         regions = new ArrayList(regionCodeSize);

         for(int i = 0; i < regionCodeSize; ++i) {
            Region r = new Region();
            String id = regionCodes.getString(i);
            r.id = id;
            r.type = Region.RegionType.TERRITORY;
            regionIDMap.put(id, r);
            if(id.matches("[0-9]{3}")) {
               r.code = Integer.valueOf(id).intValue();
               numericCodeMap.put(Integer.valueOf(r.code), r);
               r.type = Region.RegionType.SUBCONTINENT;
            } else {
               r.code = -1;
            }

            regions.add(r);
         }

         for(int i = 0; i < territoryAlias.getSize(); ++i) {
            UResourceBundle res = territoryAlias.get(i);
            String aliasFrom = res.getKey();
            String aliasTo = res.getString();
            if(regionIDMap.containsKey(aliasTo) && !regionIDMap.containsKey(aliasFrom)) {
               regionAliases.put(aliasFrom, regionIDMap.get(aliasTo));
            } else {
               Region r;
               if(regionIDMap.containsKey(aliasFrom)) {
                  r = (Region)regionIDMap.get(aliasFrom);
               } else {
                  r = new Region();
                  r.id = aliasFrom;
                  regionIDMap.put(aliasFrom, r);
                  if(aliasFrom.matches("[0-9]{3}")) {
                     r.code = Integer.valueOf(aliasFrom).intValue();
                     numericCodeMap.put(Integer.valueOf(r.code), r);
                  } else {
                     r.code = -1;
                  }

                  regions.add(r);
               }

               r.type = Region.RegionType.DEPRECATED;
               List<String> aliasToRegionStrings = Arrays.asList(aliasTo.split(" "));
               r.preferredValues = new ArrayList();

               for(String s : aliasToRegionStrings) {
                  if(regionIDMap.containsKey(s)) {
                     r.preferredValues.add(regionIDMap.get(s));
                  }
               }
            }
         }

         for(int i = 0; i < codeMappings.getSize(); ++i) {
            UResourceBundle mapping = codeMappings.get(i);
            if(mapping.getType() == 8) {
               String[] codeMappingStrings = mapping.getStringArray();
               String codeMappingID = codeMappingStrings[0];
               Integer codeMappingNumber = Integer.valueOf(codeMappingStrings[1]);
               String codeMapping3Letter = codeMappingStrings[2];
               if(regionIDMap.containsKey(codeMappingID)) {
                  Region r = (Region)regionIDMap.get(codeMappingID);
                  r.code = codeMappingNumber.intValue();
                  numericCodeMap.put(Integer.valueOf(r.code), r);
                  regionAliases.put(codeMapping3Letter, r);
               }
            }
         }

         if(regionIDMap.containsKey("001")) {
            Region r = (Region)regionIDMap.get("001");
            r.type = Region.RegionType.WORLD;
         }

         if(regionIDMap.containsKey("ZZ")) {
            Region r = (Region)regionIDMap.get("ZZ");
            r.type = Region.RegionType.UNKNOWN;
         }

         for(String continent : continents) {
            if(regionIDMap.containsKey(continent)) {
               Region r = (Region)regionIDMap.get(continent);
               r.type = Region.RegionType.CONTINENT;
            }
         }

         for(String grouping : groupings) {
            if(regionIDMap.containsKey(grouping)) {
               Region r = (Region)regionIDMap.get(grouping);
               r.type = Region.RegionType.GROUPING;
            }
         }

         if(regionIDMap.containsKey("QO")) {
            Region r = (Region)regionIDMap.get("QO");
            r.type = Region.RegionType.SUBCONTINENT;
         }

         for(int i = 0; i < territoryContainment.getSize(); ++i) {
            UResourceBundle mapping = territoryContainment.get(i);
            String parent = mapping.getKey();
            Region parentRegion = (Region)regionIDMap.get(parent);

            for(int j = 0; j < mapping.getSize(); ++j) {
               String child = mapping.getString(j);
               Region childRegion = (Region)regionIDMap.get(child);
               if(parentRegion != null && childRegion != null) {
                  parentRegion.containedRegions.add(childRegion);
                  if(parentRegion.getType() != Region.RegionType.GROUPING) {
                     childRegion.containingRegion = parentRegion;
                  }
               }
            }
         }

         for(int i = 0; i < Region.RegionType.values().length; ++i) {
            availableRegions.add(new TreeSet());
         }

         for(Region ar : regions) {
            Set<Region> currentSet = (Set)availableRegions.get(ar.type.ordinal());
            currentSet.add(ar);
            availableRegions.set(ar.type.ordinal(), currentSet);
         }

         regionDataIsLoaded = true;
      }
   }

   public static Region getInstance(String id) {
      if(id == null) {
         throw new NullPointerException();
      } else {
         loadRegionData();
         Region r = (Region)regionIDMap.get(id);
         if(r == null) {
            r = (Region)regionAliases.get(id);
         }

         if(r == null) {
            throw new IllegalArgumentException("Unknown region id: " + id);
         } else {
            if(r.type == Region.RegionType.DEPRECATED && r.preferredValues.size() == 1) {
               r = (Region)r.preferredValues.get(0);
            }

            return r;
         }
      }
   }

   public static Region getInstance(int code) {
      loadRegionData();
      Region r = (Region)numericCodeMap.get(Integer.valueOf(code));
      if(r == null) {
         String pad = "";
         if(code < 10) {
            pad = "00";
         } else if(code < 100) {
            pad = "0";
         }

         String id = pad + Integer.toString(code);
         r = (Region)regionAliases.get(id);
      }

      if(r == null) {
         throw new IllegalArgumentException("Unknown region code: " + code);
      } else {
         if(r.type == Region.RegionType.DEPRECATED && r.preferredValues.size() == 1) {
            r = (Region)r.preferredValues.get(0);
         }

         return r;
      }
   }

   public static Set getAvailable(Region.RegionType type) {
      loadRegionData();
      return Collections.unmodifiableSet((Set)availableRegions.get(type.ordinal()));
   }

   public Region getContainingRegion() {
      loadRegionData();
      return this.containingRegion;
   }

   public Region getContainingRegion(Region.RegionType type) {
      loadRegionData();
      return this.containingRegion == null?null:(this.containingRegion.type.equals(type)?this.containingRegion:this.containingRegion.getContainingRegion(type));
   }

   public Set getContainedRegions() {
      loadRegionData();
      return Collections.unmodifiableSet(this.containedRegions);
   }

   public Set getContainedRegions(Region.RegionType type) {
      loadRegionData();
      Set<Region> result = new TreeSet();

      for(Region r : this.getContainedRegions()) {
         if(r.getType() == type) {
            result.add(r);
         } else {
            result.addAll(r.getContainedRegions(type));
         }
      }

      return Collections.unmodifiableSet(result);
   }

   public List getPreferredValues() {
      loadRegionData();
      return this.type == Region.RegionType.DEPRECATED?Collections.unmodifiableList(this.preferredValues):null;
   }

   public boolean contains(Region other) {
      loadRegionData();
      if(this.containedRegions.contains(other)) {
         return true;
      } else {
         for(Region cr : this.containedRegions) {
            if(cr.contains(other)) {
               return true;
            }
         }

         return false;
      }
   }

   public String toString() {
      return this.id;
   }

   public int getNumericCode() {
      return this.code;
   }

   public Region.RegionType getType() {
      return this.type;
   }

   public int compareTo(Region other) {
      return this.id.compareTo(other.id);
   }

   public static enum RegionType {
      UNKNOWN,
      TERRITORY,
      WORLD,
      CONTINENT,
      SUBCONTINENT,
      GROUPING,
      DEPRECATED;
   }
}
