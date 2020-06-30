package com.ibm.icu.text;

import com.ibm.icu.impl.ICUDebug;
import com.ibm.icu.text.RuleBasedBreakIterator;
import com.ibm.icu.util.ULocale;
import java.lang.ref.SoftReference;
import java.text.CharacterIterator;
import java.util.Locale;
import java.util.MissingResourceException;

public abstract class BreakIterator implements Cloneable {
   private static final boolean DEBUG = ICUDebug.enabled("breakiterator");
   public static final int DONE = -1;
   public static final int KIND_CHARACTER = 0;
   public static final int KIND_WORD = 1;
   public static final int KIND_LINE = 2;
   public static final int KIND_SENTENCE = 3;
   public static final int KIND_TITLE = 4;
   private static final int KIND_COUNT = 5;
   private static final SoftReference[] iterCache = new SoftReference[5];
   private static BreakIterator.BreakIteratorServiceShim shim;
   private ULocale validLocale;
   private ULocale actualLocale;

   public Object clone() {
      try {
         return super.clone();
      } catch (CloneNotSupportedException var2) {
         throw new IllegalStateException();
      }
   }

   public abstract int first();

   public abstract int last();

   public abstract int next(int var1);

   public abstract int next();

   public abstract int previous();

   public abstract int following(int var1);

   public int preceding(int offset) {
      int pos;
      for(pos = this.following(offset); pos >= offset && pos != -1; pos = this.previous()) {
         ;
      }

      return pos;
   }

   public boolean isBoundary(int offset) {
      return offset == 0?true:this.following(offset - 1) == offset;
   }

   public abstract int current();

   public abstract CharacterIterator getText();

   public void setText(String newText) {
      this.setText((CharacterIterator)(new java.text.StringCharacterIterator(newText)));
   }

   public abstract void setText(CharacterIterator var1);

   public static BreakIterator getWordInstance() {
      return getWordInstance(ULocale.getDefault());
   }

   public static BreakIterator getWordInstance(Locale where) {
      return getBreakInstance(ULocale.forLocale(where), 1);
   }

   public static BreakIterator getWordInstance(ULocale where) {
      return getBreakInstance(where, 1);
   }

   public static BreakIterator getLineInstance() {
      return getLineInstance(ULocale.getDefault());
   }

   public static BreakIterator getLineInstance(Locale where) {
      return getBreakInstance(ULocale.forLocale(where), 2);
   }

   public static BreakIterator getLineInstance(ULocale where) {
      return getBreakInstance(where, 2);
   }

   public static BreakIterator getCharacterInstance() {
      return getCharacterInstance(ULocale.getDefault());
   }

   public static BreakIterator getCharacterInstance(Locale where) {
      return getBreakInstance(ULocale.forLocale(where), 0);
   }

   public static BreakIterator getCharacterInstance(ULocale where) {
      return getBreakInstance(where, 0);
   }

   public static BreakIterator getSentenceInstance() {
      return getSentenceInstance(ULocale.getDefault());
   }

   public static BreakIterator getSentenceInstance(Locale where) {
      return getBreakInstance(ULocale.forLocale(where), 3);
   }

   public static BreakIterator getSentenceInstance(ULocale where) {
      return getBreakInstance(where, 3);
   }

   public static BreakIterator getTitleInstance() {
      return getTitleInstance(ULocale.getDefault());
   }

   public static BreakIterator getTitleInstance(Locale where) {
      return getBreakInstance(ULocale.forLocale(where), 4);
   }

   public static BreakIterator getTitleInstance(ULocale where) {
      return getBreakInstance(where, 4);
   }

   public static Object registerInstance(BreakIterator iter, Locale locale, int kind) {
      return registerInstance(iter, ULocale.forLocale(locale), kind);
   }

   public static Object registerInstance(BreakIterator iter, ULocale locale, int kind) {
      if(iterCache[kind] != null) {
         BreakIterator.BreakIteratorCache cache = (BreakIterator.BreakIteratorCache)iterCache[kind].get();
         if(cache != null && cache.getLocale().equals(locale)) {
            iterCache[kind] = null;
         }
      }

      return getShim().registerInstance(iter, locale, kind);
   }

   public static boolean unregister(Object key) {
      if(key == null) {
         throw new IllegalArgumentException("registry key must not be null");
      } else if(shim == null) {
         return false;
      } else {
         for(int kind = 0; kind < 5; ++kind) {
            iterCache[kind] = null;
         }

         return shim.unregister(key);
      }
   }

   /** @deprecated */
   public static BreakIterator getBreakInstance(ULocale where, int kind) {
      if(iterCache[kind] != null) {
         BreakIterator.BreakIteratorCache cache = (BreakIterator.BreakIteratorCache)iterCache[kind].get();
         if(cache != null && cache.getLocale().equals(where)) {
            return cache.createBreakInstance();
         }
      }

      BreakIterator result = getShim().createBreakIterator(where, kind);
      BreakIterator.BreakIteratorCache cache = new BreakIterator.BreakIteratorCache(where, result);
      iterCache[kind] = new SoftReference(cache);
      if(result instanceof RuleBasedBreakIterator) {
         RuleBasedBreakIterator rbbi = (RuleBasedBreakIterator)result;
         rbbi.setBreakType(kind);
      }

      return result;
   }

   public static synchronized Locale[] getAvailableLocales() {
      return getShim().getAvailableLocales();
   }

   public static synchronized ULocale[] getAvailableULocales() {
      return getShim().getAvailableULocales();
   }

   private static BreakIterator.BreakIteratorServiceShim getShim() {
      if(shim == null) {
         try {
            Class<?> cls = Class.forName("com.ibm.icu.text.BreakIteratorFactory");
            shim = (BreakIterator.BreakIteratorServiceShim)cls.newInstance();
         } catch (MissingResourceException var1) {
            throw var1;
         } catch (Exception var2) {
            if(DEBUG) {
               var2.printStackTrace();
            }

            throw new RuntimeException(var2.getMessage());
         }
      }

      return shim;
   }

   public final ULocale getLocale(ULocale.Type type) {
      return type == ULocale.ACTUAL_LOCALE?this.actualLocale:this.validLocale;
   }

   final void setLocale(ULocale valid, ULocale actual) {
      if(valid == null != (actual == null)) {
         throw new IllegalArgumentException();
      } else {
         this.validLocale = valid;
         this.actualLocale = actual;
      }
   }

   private static final class BreakIteratorCache {
      private BreakIterator iter;
      private ULocale where;

      BreakIteratorCache(ULocale where, BreakIterator iter) {
         this.where = where;
         this.iter = (BreakIterator)iter.clone();
      }

      ULocale getLocale() {
         return this.where;
      }

      BreakIterator createBreakInstance() {
         return (BreakIterator)this.iter.clone();
      }
   }

   abstract static class BreakIteratorServiceShim {
      public abstract Object registerInstance(BreakIterator var1, ULocale var2, int var3);

      public abstract boolean unregister(Object var1);

      public abstract Locale[] getAvailableLocales();

      public abstract ULocale[] getAvailableULocales();

      public abstract BreakIterator createBreakIterator(ULocale var1, int var2);
   }
}
