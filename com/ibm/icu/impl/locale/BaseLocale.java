package com.ibm.icu.impl.locale;

import com.ibm.icu.impl.locale.AsciiUtil;
import com.ibm.icu.impl.locale.LocaleObjectCache;

public final class BaseLocale {
   private static final boolean JDKIMPL = false;
   public static final String SEP = "_";
   private static final BaseLocale.Cache CACHE = new BaseLocale.Cache();
   public static final BaseLocale ROOT = getInstance("", "", "", "");
   private String _language;
   private String _script;
   private String _region;
   private String _variant;
   private transient volatile int _hash;

   private BaseLocale(String language, String script, String region, String variant) {
      this._language = "";
      this._script = "";
      this._region = "";
      this._variant = "";
      this._hash = 0;
      if(language != null) {
         this._language = AsciiUtil.toLowerString(language).intern();
      }

      if(script != null) {
         this._script = AsciiUtil.toTitleString(script).intern();
      }

      if(region != null) {
         this._region = AsciiUtil.toUpperString(region).intern();
      }

      if(variant != null) {
         this._variant = AsciiUtil.toUpperString(variant).intern();
      }

   }

   public static BaseLocale getInstance(String language, String script, String region, String variant) {
      BaseLocale.Key key = new BaseLocale.Key(language, script, region, variant);
      BaseLocale baseLocale = (BaseLocale)CACHE.get(key);
      return baseLocale;
   }

   public String getLanguage() {
      return this._language;
   }

   public String getScript() {
      return this._script;
   }

   public String getRegion() {
      return this._region;
   }

   public String getVariant() {
      return this._variant;
   }

   public boolean equals(Object obj) {
      if(this == obj) {
         return true;
      } else if(!(obj instanceof BaseLocale)) {
         return false;
      } else {
         BaseLocale other = (BaseLocale)obj;
         return this.hashCode() == other.hashCode() && this._language.equals(other._language) && this._script.equals(other._script) && this._region.equals(other._region) && this._variant.equals(other._variant);
      }
   }

   public String toString() {
      StringBuilder buf = new StringBuilder();
      if(this._language.length() > 0) {
         buf.append("language=");
         buf.append(this._language);
      }

      if(this._script.length() > 0) {
         if(buf.length() > 0) {
            buf.append(", ");
         }

         buf.append("script=");
         buf.append(this._script);
      }

      if(this._region.length() > 0) {
         if(buf.length() > 0) {
            buf.append(", ");
         }

         buf.append("region=");
         buf.append(this._region);
      }

      if(this._variant.length() > 0) {
         if(buf.length() > 0) {
            buf.append(", ");
         }

         buf.append("variant=");
         buf.append(this._variant);
      }

      return buf.toString();
   }

   public int hashCode() {
      int h = this._hash;
      if(h == 0) {
         for(int i = 0; i < this._language.length(); ++i) {
            h = 31 * h + this._language.charAt(i);
         }

         for(int i = 0; i < this._script.length(); ++i) {
            h = 31 * h + this._script.charAt(i);
         }

         for(int i = 0; i < this._region.length(); ++i) {
            h = 31 * h + this._region.charAt(i);
         }

         for(int i = 0; i < this._variant.length(); ++i) {
            h = 31 * h + this._variant.charAt(i);
         }

         this._hash = h;
      }

      return h;
   }

   private static class Cache extends LocaleObjectCache {
      protected BaseLocale.Key normalizeKey(BaseLocale.Key key) {
         return BaseLocale.Key.normalize(key);
      }

      protected BaseLocale createObject(BaseLocale.Key key) {
         return new BaseLocale(key._lang, key._scrt, key._regn, key._vart);
      }
   }

   private static class Key implements Comparable {
      private String _lang = "";
      private String _scrt = "";
      private String _regn = "";
      private String _vart = "";
      private volatile int _hash;

      public Key(String language, String script, String region, String variant) {
         if(language != null) {
            this._lang = language;
         }

         if(script != null) {
            this._scrt = script;
         }

         if(region != null) {
            this._regn = region;
         }

         if(variant != null) {
            this._vart = variant;
         }

      }

      public boolean equals(Object obj) {
         return this == obj || obj instanceof BaseLocale.Key && AsciiUtil.caseIgnoreMatch(((BaseLocale.Key)obj)._lang, this._lang) && AsciiUtil.caseIgnoreMatch(((BaseLocale.Key)obj)._scrt, this._scrt) && AsciiUtil.caseIgnoreMatch(((BaseLocale.Key)obj)._regn, this._regn) && AsciiUtil.caseIgnoreMatch(((BaseLocale.Key)obj)._vart, this._vart);
      }

      public int compareTo(BaseLocale.Key other) {
         int res = AsciiUtil.caseIgnoreCompare(this._lang, other._lang);
         if(res == 0) {
            res = AsciiUtil.caseIgnoreCompare(this._scrt, other._scrt);
            if(res == 0) {
               res = AsciiUtil.caseIgnoreCompare(this._regn, other._regn);
               if(res == 0) {
                  res = AsciiUtil.caseIgnoreCompare(this._vart, other._vart);
               }
            }
         }

         return res;
      }

      public int hashCode() {
         int h = this._hash;
         if(h == 0) {
            for(int i = 0; i < this._lang.length(); ++i) {
               h = 31 * h + AsciiUtil.toLower(this._lang.charAt(i));
            }

            for(int i = 0; i < this._scrt.length(); ++i) {
               h = 31 * h + AsciiUtil.toLower(this._scrt.charAt(i));
            }

            for(int i = 0; i < this._regn.length(); ++i) {
               h = 31 * h + AsciiUtil.toLower(this._regn.charAt(i));
            }

            for(int i = 0; i < this._vart.length(); ++i) {
               h = 31 * h + AsciiUtil.toLower(this._vart.charAt(i));
            }

            this._hash = h;
         }

         return h;
      }

      public static BaseLocale.Key normalize(BaseLocale.Key key) {
         String lang = AsciiUtil.toLowerString(key._lang).intern();
         String scrt = AsciiUtil.toTitleString(key._scrt).intern();
         String regn = AsciiUtil.toUpperString(key._regn).intern();
         String vart = AsciiUtil.toUpperString(key._vart).intern();
         return new BaseLocale.Key(lang, scrt, regn, vart);
      }
   }
}
