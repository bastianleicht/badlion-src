package com.ibm.icu.impl.locale;

import com.ibm.icu.impl.locale.AsciiUtil;
import com.ibm.icu.impl.locale.Extension;
import java.util.Collections;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

public class UnicodeLocaleExtension extends Extension {
   public static final char SINGLETON = 'u';
   private static final SortedSet EMPTY_SORTED_SET = new TreeSet();
   private static final SortedMap EMPTY_SORTED_MAP = new TreeMap();
   private SortedSet _attributes;
   private SortedMap _keywords;
   public static final UnicodeLocaleExtension CA_JAPANESE = new UnicodeLocaleExtension();
   public static final UnicodeLocaleExtension NU_THAI = new UnicodeLocaleExtension();

   private UnicodeLocaleExtension() {
      super('u');
      this._attributes = EMPTY_SORTED_SET;
      this._keywords = EMPTY_SORTED_MAP;
   }

   UnicodeLocaleExtension(SortedSet attributes, SortedMap keywords) {
      this();
      if(attributes != null && attributes.size() > 0) {
         this._attributes = attributes;
      }

      if(keywords != null && keywords.size() > 0) {
         this._keywords = keywords;
      }

      if(this._attributes.size() > 0 || this._keywords.size() > 0) {
         StringBuilder sb = new StringBuilder();

         for(String attribute : this._attributes) {
            sb.append("-").append(attribute);
         }

         for(Entry<String, String> keyword : this._keywords.entrySet()) {
            String key = (String)keyword.getKey();
            String value = (String)keyword.getValue();
            sb.append("-").append(key);
            if(value.length() > 0) {
               sb.append("-").append(value);
            }
         }

         this._value = sb.substring(1);
      }

   }

   public Set getUnicodeLocaleAttributes() {
      return Collections.unmodifiableSet(this._attributes);
   }

   public Set getUnicodeLocaleKeys() {
      return Collections.unmodifiableSet(this._keywords.keySet());
   }

   public String getUnicodeLocaleType(String unicodeLocaleKey) {
      return (String)this._keywords.get(unicodeLocaleKey);
   }

   public static boolean isSingletonChar(char c) {
      return 117 == AsciiUtil.toLower(c);
   }

   public static boolean isAttribute(String s) {
      return s.length() >= 3 && s.length() <= 8 && AsciiUtil.isAlphaNumericString(s);
   }

   public static boolean isKey(String s) {
      return s.length() == 2 && AsciiUtil.isAlphaNumericString(s);
   }

   public static boolean isTypeSubtag(String s) {
      return s.length() >= 3 && s.length() <= 8 && AsciiUtil.isAlphaNumericString(s);
   }

   static {
      CA_JAPANESE._keywords = new TreeMap();
      CA_JAPANESE._keywords.put("ca", "japanese");
      CA_JAPANESE._value = "ca-japanese";
      NU_THAI._keywords = new TreeMap();
      NU_THAI._keywords.put("nu", "thai");
      NU_THAI._value = "nu-thai";
   }
}
