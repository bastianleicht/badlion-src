package com.ibm.icu.impl.duration.impl;

import com.ibm.icu.impl.duration.impl.RecordReader;
import com.ibm.icu.impl.duration.impl.RecordWriter;
import java.util.ArrayList;
import java.util.List;

public class DataRecord {
   byte pl;
   String[][] pluralNames;
   byte[] genders;
   String[] singularNames;
   String[] halfNames;
   String[] numberNames;
   String[] mediumNames;
   String[] shortNames;
   String[] measures;
   String[] rqdSuffixes;
   String[] optSuffixes;
   String[] halves;
   byte[] halfPlacements;
   byte[] halfSupport;
   String fifteenMinutes;
   String fiveMinutes;
   boolean requiresDigitSeparator;
   String digitPrefix;
   String countSep;
   String shortUnitSep;
   String[] unitSep;
   boolean[] unitSepRequiresDP;
   boolean[] requiresSkipMarker;
   byte numberSystem;
   char zero;
   char decimalSep;
   boolean omitSingularCount;
   boolean omitDualCount;
   byte zeroHandling;
   byte decimalHandling;
   byte fractionHandling;
   String skippedUnitMarker;
   boolean allowZero;
   boolean weeksAloneOnly;
   byte useMilliseconds;
   DataRecord.ScopeData[] scopeData;

   public static DataRecord read(String ln, RecordReader in) {
      if(!in.open("DataRecord")) {
         throw new InternalError("did not find DataRecord while reading " + ln);
      } else {
         DataRecord record = new DataRecord();
         record.pl = in.namedIndex("pl", DataRecord.EPluralization.names);
         record.pluralNames = in.stringTable("pluralName");
         record.genders = in.namedIndexArray("gender", DataRecord.EGender.names);
         record.singularNames = in.stringArray("singularName");
         record.halfNames = in.stringArray("halfName");
         record.numberNames = in.stringArray("numberName");
         record.mediumNames = in.stringArray("mediumName");
         record.shortNames = in.stringArray("shortName");
         record.measures = in.stringArray("measure");
         record.rqdSuffixes = in.stringArray("rqdSuffix");
         record.optSuffixes = in.stringArray("optSuffix");
         record.halves = in.stringArray("halves");
         record.halfPlacements = in.namedIndexArray("halfPlacement", DataRecord.EHalfPlacement.names);
         record.halfSupport = in.namedIndexArray("halfSupport", DataRecord.EHalfSupport.names);
         record.fifteenMinutes = in.string("fifteenMinutes");
         record.fiveMinutes = in.string("fiveMinutes");
         record.requiresDigitSeparator = in.bool("requiresDigitSeparator");
         record.digitPrefix = in.string("digitPrefix");
         record.countSep = in.string("countSep");
         record.shortUnitSep = in.string("shortUnitSep");
         record.unitSep = in.stringArray("unitSep");
         record.unitSepRequiresDP = in.boolArray("unitSepRequiresDP");
         record.requiresSkipMarker = in.boolArray("requiresSkipMarker");
         record.numberSystem = in.namedIndex("numberSystem", DataRecord.ENumberSystem.names);
         record.zero = in.character("zero");
         record.decimalSep = in.character("decimalSep");
         record.omitSingularCount = in.bool("omitSingularCount");
         record.omitDualCount = in.bool("omitDualCount");
         record.zeroHandling = in.namedIndex("zeroHandling", DataRecord.EZeroHandling.names);
         record.decimalHandling = in.namedIndex("decimalHandling", DataRecord.EDecimalHandling.names);
         record.fractionHandling = in.namedIndex("fractionHandling", DataRecord.EFractionHandling.names);
         record.skippedUnitMarker = in.string("skippedUnitMarker");
         record.allowZero = in.bool("allowZero");
         record.weeksAloneOnly = in.bool("weeksAloneOnly");
         record.useMilliseconds = in.namedIndex("useMilliseconds", DataRecord.EMilliSupport.names);
         if(in.open("ScopeDataList")) {
            List<DataRecord.ScopeData> list = new ArrayList();

            DataRecord.ScopeData data;
            while(null != (data = DataRecord.ScopeData.read(in))) {
               list.add(data);
            }

            if(in.close()) {
               record.scopeData = (DataRecord.ScopeData[])list.toArray(new DataRecord.ScopeData[list.size()]);
            }
         }

         if(in.close()) {
            return record;
         } else {
            throw new InternalError("null data read while reading " + ln);
         }
      }
   }

   public void write(RecordWriter out) {
      out.open("DataRecord");
      out.namedIndex("pl", DataRecord.EPluralization.names, this.pl);
      out.stringTable("pluralName", this.pluralNames);
      out.namedIndexArray("gender", DataRecord.EGender.names, this.genders);
      out.stringArray("singularName", this.singularNames);
      out.stringArray("halfName", this.halfNames);
      out.stringArray("numberName", this.numberNames);
      out.stringArray("mediumName", this.mediumNames);
      out.stringArray("shortName", this.shortNames);
      out.stringArray("measure", this.measures);
      out.stringArray("rqdSuffix", this.rqdSuffixes);
      out.stringArray("optSuffix", this.optSuffixes);
      out.stringArray("halves", this.halves);
      out.namedIndexArray("halfPlacement", DataRecord.EHalfPlacement.names, this.halfPlacements);
      out.namedIndexArray("halfSupport", DataRecord.EHalfSupport.names, this.halfSupport);
      out.string("fifteenMinutes", this.fifteenMinutes);
      out.string("fiveMinutes", this.fiveMinutes);
      out.bool("requiresDigitSeparator", this.requiresDigitSeparator);
      out.string("digitPrefix", this.digitPrefix);
      out.string("countSep", this.countSep);
      out.string("shortUnitSep", this.shortUnitSep);
      out.stringArray("unitSep", this.unitSep);
      out.boolArray("unitSepRequiresDP", this.unitSepRequiresDP);
      out.boolArray("requiresSkipMarker", this.requiresSkipMarker);
      out.namedIndex("numberSystem", DataRecord.ENumberSystem.names, this.numberSystem);
      out.character("zero", this.zero);
      out.character("decimalSep", this.decimalSep);
      out.bool("omitSingularCount", this.omitSingularCount);
      out.bool("omitDualCount", this.omitDualCount);
      out.namedIndex("zeroHandling", DataRecord.EZeroHandling.names, this.zeroHandling);
      out.namedIndex("decimalHandling", DataRecord.EDecimalHandling.names, this.decimalHandling);
      out.namedIndex("fractionHandling", DataRecord.EFractionHandling.names, this.fractionHandling);
      out.string("skippedUnitMarker", this.skippedUnitMarker);
      out.bool("allowZero", this.allowZero);
      out.bool("weeksAloneOnly", this.weeksAloneOnly);
      out.namedIndex("useMilliseconds", DataRecord.EMilliSupport.names, this.useMilliseconds);
      if(this.scopeData != null) {
         out.open("ScopeDataList");

         for(int i = 0; i < this.scopeData.length; ++i) {
            this.scopeData[i].write(out);
         }

         out.close();
      }

      out.close();
   }

   public interface ECountVariant {
      byte INTEGER = 0;
      byte INTEGER_CUSTOM = 1;
      byte HALF_FRACTION = 2;
      byte DECIMAL1 = 3;
      byte DECIMAL2 = 4;
      byte DECIMAL3 = 5;
      String[] names = new String[]{"INTEGER", "INTEGER_CUSTOM", "HALF_FRACTION", "DECIMAL1", "DECIMAL2", "DECIMAL3"};
   }

   public interface EDecimalHandling {
      byte DPLURAL = 0;
      byte DSINGULAR = 1;
      byte DSINGULAR_SUBONE = 2;
      byte DPAUCAL = 3;
      String[] names = new String[]{"DPLURAL", "DSINGULAR", "DSINGULAR_SUBONE", "DPAUCAL"};
   }

   public interface EFractionHandling {
      byte FPLURAL = 0;
      byte FSINGULAR_PLURAL = 1;
      byte FSINGULAR_PLURAL_ANDAHALF = 2;
      byte FPAUCAL = 3;
      String[] names = new String[]{"FPLURAL", "FSINGULAR_PLURAL", "FSINGULAR_PLURAL_ANDAHALF", "FPAUCAL"};
   }

   public interface EGender {
      byte M = 0;
      byte F = 1;
      byte N = 2;
      String[] names = new String[]{"M", "F", "N"};
   }

   public interface EHalfPlacement {
      byte PREFIX = 0;
      byte AFTER_FIRST = 1;
      byte LAST = 2;
      String[] names = new String[]{"PREFIX", "AFTER_FIRST", "LAST"};
   }

   public interface EHalfSupport {
      byte YES = 0;
      byte NO = 1;
      byte ONE_PLUS = 2;
      String[] names = new String[]{"YES", "NO", "ONE_PLUS"};
   }

   public interface EMilliSupport {
      byte YES = 0;
      byte NO = 1;
      byte WITH_SECONDS = 2;
      String[] names = new String[]{"YES", "NO", "WITH_SECONDS"};
   }

   public interface ENumberSystem {
      byte DEFAULT = 0;
      byte CHINESE_TRADITIONAL = 1;
      byte CHINESE_SIMPLIFIED = 2;
      byte KOREAN = 3;
      String[] names = new String[]{"DEFAULT", "CHINESE_TRADITIONAL", "CHINESE_SIMPLIFIED", "KOREAN"};
   }

   public interface EPluralization {
      byte NONE = 0;
      byte PLURAL = 1;
      byte DUAL = 2;
      byte PAUCAL = 3;
      byte HEBREW = 4;
      byte ARABIC = 5;
      String[] names = new String[]{"NONE", "PLURAL", "DUAL", "PAUCAL", "HEBREW", "ARABIC"};
   }

   public interface ESeparatorVariant {
      byte NONE = 0;
      byte SHORT = 1;
      byte FULL = 2;
      String[] names = new String[]{"NONE", "SHORT", "FULL"};
   }

   public interface ETimeDirection {
      byte NODIRECTION = 0;
      byte PAST = 1;
      byte FUTURE = 2;
      String[] names = new String[]{"NODIRECTION", "PAST", "FUTURE"};
   }

   public interface ETimeLimit {
      byte NOLIMIT = 0;
      byte LT = 1;
      byte MT = 2;
      String[] names = new String[]{"NOLIMIT", "LT", "MT"};
   }

   public interface EUnitVariant {
      byte PLURALIZED = 0;
      byte MEDIUM = 1;
      byte SHORT = 2;
      String[] names = new String[]{"PLURALIZED", "MEDIUM", "SHORT"};
   }

   public interface EZeroHandling {
      byte ZPLURAL = 0;
      byte ZSINGULAR = 1;
      String[] names = new String[]{"ZPLURAL", "ZSINGULAR"};
   }

   public static class ScopeData {
      String prefix;
      boolean requiresDigitPrefix;
      String suffix;

      public void write(RecordWriter out) {
         out.open("ScopeData");
         out.string("prefix", this.prefix);
         out.bool("requiresDigitPrefix", this.requiresDigitPrefix);
         out.string("suffix", this.suffix);
         out.close();
      }

      public static DataRecord.ScopeData read(RecordReader in) {
         if(in.open("ScopeData")) {
            DataRecord.ScopeData scope = new DataRecord.ScopeData();
            scope.prefix = in.string("prefix");
            scope.requiresDigitPrefix = in.bool("requiresDigitPrefix");
            scope.suffix = in.string("suffix");
            if(in.close()) {
               return scope;
            }
         }

         return null;
      }
   }
}
