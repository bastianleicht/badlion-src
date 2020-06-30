package com.ibm.icu.impl.locale;

import com.ibm.icu.impl.locale.AsciiUtil;
import com.ibm.icu.impl.locale.Extension;
import com.ibm.icu.impl.locale.InternalLocaleBuilder;
import com.ibm.icu.impl.locale.LanguageTag;
import com.ibm.icu.impl.locale.UnicodeLocaleExtension;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

public class LocaleExtensions {
   private SortedMap _map;
   private String _id;
   private static final SortedMap EMPTY_MAP = Collections.unmodifiableSortedMap(new TreeMap());
   public static final LocaleExtensions EMPTY_EXTENSIONS = new LocaleExtensions();
   public static final LocaleExtensions CALENDAR_JAPANESE = new LocaleExtensions();
   public static final LocaleExtensions NUMBER_THAI = new LocaleExtensions();

   private LocaleExtensions() {
   }

   LocaleExtensions(Map extensions, Set uattributes, Map ukeywords) {
      boolean hasExtension = extensions != null && extensions.size() > 0;
      boolean hasUAttributes = uattributes != null && uattributes.size() > 0;
      boolean hasUKeywords = ukeywords != null && ukeywords.size() > 0;
      if(!hasExtension && !hasUAttributes && !hasUKeywords) {
         this._map = EMPTY_MAP;
         this._id = "";
      } else {
         this._map = new TreeMap();
         if(hasExtension) {
            Iterator i$ = extensions.entrySet().iterator();

            label122:
            while(true) {
               char key;
               String value;
               while(true) {
                  if(!i$.hasNext()) {
                     break label122;
                  }

                  Entry<InternalLocaleBuilder.CaseInsensitiveChar, String> ext = (Entry)i$.next();
                  key = AsciiUtil.toLower(((InternalLocaleBuilder.CaseInsensitiveChar)ext.getKey()).value());
                  value = (String)ext.getValue();
                  if(!LanguageTag.isPrivateusePrefixChar(key)) {
                     break;
                  }

                  value = InternalLocaleBuilder.removePrivateuseVariant(value);
                  if(value != null) {
                     break;
                  }
               }

               Extension e = new Extension(key, AsciiUtil.toLowerString(value));
               this._map.put(Character.valueOf(key), e);
            }
         }

         if(hasUAttributes || hasUKeywords) {
            TreeSet<String> uaset = null;
            TreeMap<String, String> ukmap = null;
            if(hasUAttributes) {
               uaset = new TreeSet();

               for(InternalLocaleBuilder.CaseInsensitiveString cis : uattributes) {
                  uaset.add(AsciiUtil.toLowerString(cis.value()));
               }
            }

            if(hasUKeywords) {
               ukmap = new TreeMap();

               for(Entry<InternalLocaleBuilder.CaseInsensitiveString, String> kwd : ukeywords.entrySet()) {
                  String key = AsciiUtil.toLowerString(((InternalLocaleBuilder.CaseInsensitiveString)kwd.getKey()).value());
                  String type = AsciiUtil.toLowerString((String)kwd.getValue());
                  ukmap.put(key, type);
               }
            }

            UnicodeLocaleExtension ule = new UnicodeLocaleExtension(uaset, ukmap);
            this._map.put(Character.valueOf('u'), ule);
         }

         if(this._map.size() == 0) {
            this._map = EMPTY_MAP;
            this._id = "";
         } else {
            this._id = toID(this._map);
         }

      }
   }

   public Set getKeys() {
      return Collections.unmodifiableSet(this._map.keySet());
   }

   public Extension getExtension(Character key) {
      return (Extension)this._map.get(Character.valueOf(AsciiUtil.toLower(key.charValue())));
   }

   public String getExtensionValue(Character key) {
      Extension ext = (Extension)this._map.get(Character.valueOf(AsciiUtil.toLower(key.charValue())));
      return ext == null?null:ext.getValue();
   }

   public Set getUnicodeLocaleAttributes() {
      Extension ext = (Extension)this._map.get(Character.valueOf('u'));
      if(ext == null) {
         return Collections.emptySet();
      } else {
         assert ext instanceof UnicodeLocaleExtension;

         return ((UnicodeLocaleExtension)ext).getUnicodeLocaleAttributes();
      }
   }

   public Set getUnicodeLocaleKeys() {
      Extension ext = (Extension)this._map.get(Character.valueOf('u'));
      if(ext == null) {
         return Collections.emptySet();
      } else {
         assert ext instanceof UnicodeLocaleExtension;

         return ((UnicodeLocaleExtension)ext).getUnicodeLocaleKeys();
      }
   }

   public String getUnicodeLocaleType(String unicodeLocaleKey) {
      Extension ext = (Extension)this._map.get(Character.valueOf('u'));
      if(ext == null) {
         return null;
      } else {
         assert ext instanceof UnicodeLocaleExtension;

         return ((UnicodeLocaleExtension)ext).getUnicodeLocaleType(AsciiUtil.toLowerString(unicodeLocaleKey));
      }
   }

   public boolean isEmpty() {
      return this._map.isEmpty();
   }

   public static boolean isValidKey(char c) {
      return LanguageTag.isExtensionSingletonChar(c) || LanguageTag.isPrivateusePrefixChar(c);
   }

   public static boolean isValidUnicodeLocaleKey(String ukey) {
      return UnicodeLocaleExtension.isKey(ukey);
   }

   private static String toID(SortedMap map) {
      StringBuilder buf = new StringBuilder();
      Extension privuse = null;

      for(Entry<Character, Extension> entry : map.entrySet()) {
         char singleton = ((Character)entry.getKey()).charValue();
         Extension extension = (Extension)entry.getValue();
         if(LanguageTag.isPrivateusePrefixChar(singleton)) {
            privuse = extension;
         } else {
            if(buf.length() > 0) {
               buf.append("-");
            }

            buf.append(extension);
         }
      }

      if(privuse != null) {
         if(buf.length() > 0) {
            buf.append("-");
         }

         buf.append(privuse);
      }

      return buf.toString();
   }

   public String toString() {
      return this._id;
   }

   public String getID() {
      return this._id;
   }

   public int hashCode() {
      return this._id.hashCode();
   }

   public boolean equals(Object other) {
      return this == other?true:(!(other instanceof LocaleExtensions)?false:this._id.equals(((LocaleExtensions)other)._id));
   }

   static {
      EMPTY_EXTENSIONS._id = "";
      EMPTY_EXTENSIONS._map = EMPTY_MAP;
      CALENDAR_JAPANESE._id = "u-ca-japanese";
      CALENDAR_JAPANESE._map = new TreeMap();
      CALENDAR_JAPANESE._map.put(Character.valueOf('u'), UnicodeLocaleExtension.CA_JAPANESE);
      NUMBER_THAI._id = "u-nu-thai";
      NUMBER_THAI._map = new TreeMap();
      NUMBER_THAI._map.put(Character.valueOf('u'), UnicodeLocaleExtension.NU_THAI);
   }
}
