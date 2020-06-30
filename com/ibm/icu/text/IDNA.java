package com.ibm.icu.text;

import com.ibm.icu.impl.IDNA2003;
import com.ibm.icu.impl.UTS46;
import com.ibm.icu.text.StringPrepParseException;
import com.ibm.icu.text.UCharacterIterator;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public abstract class IDNA {
   public static final int DEFAULT = 0;
   public static final int ALLOW_UNASSIGNED = 1;
   public static final int USE_STD3_RULES = 2;
   public static final int CHECK_BIDI = 4;
   public static final int CHECK_CONTEXTJ = 8;
   public static final int NONTRANSITIONAL_TO_ASCII = 16;
   public static final int NONTRANSITIONAL_TO_UNICODE = 32;
   public static final int CHECK_CONTEXTO = 64;

   public static IDNA getUTS46Instance(int options) {
      return new UTS46(options);
   }

   public abstract StringBuilder labelToASCII(CharSequence var1, StringBuilder var2, IDNA.Info var3);

   public abstract StringBuilder labelToUnicode(CharSequence var1, StringBuilder var2, IDNA.Info var3);

   public abstract StringBuilder nameToASCII(CharSequence var1, StringBuilder var2, IDNA.Info var3);

   public abstract StringBuilder nameToUnicode(CharSequence var1, StringBuilder var2, IDNA.Info var3);

   /** @deprecated */
   protected static void resetInfo(IDNA.Info info) {
      info.reset();
   }

   /** @deprecated */
   protected static boolean hasCertainErrors(IDNA.Info info, EnumSet errors) {
      return !info.errors.isEmpty() && !Collections.disjoint(info.errors, errors);
   }

   /** @deprecated */
   protected static boolean hasCertainLabelErrors(IDNA.Info info, EnumSet errors) {
      return !info.labelErrors.isEmpty() && !Collections.disjoint(info.labelErrors, errors);
   }

   /** @deprecated */
   protected static void addLabelError(IDNA.Info info, IDNA.Error error) {
      info.labelErrors.add(error);
   }

   /** @deprecated */
   protected static void promoteAndResetLabelErrors(IDNA.Info info) {
      if(!info.labelErrors.isEmpty()) {
         info.errors.addAll(info.labelErrors);
         info.labelErrors.clear();
      }

   }

   /** @deprecated */
   protected static void addError(IDNA.Info info, IDNA.Error error) {
      info.errors.add(error);
   }

   /** @deprecated */
   protected static void setTransitionalDifferent(IDNA.Info info) {
      info.isTransDiff = true;
   }

   /** @deprecated */
   protected static void setBiDi(IDNA.Info info) {
      info.isBiDi = true;
   }

   /** @deprecated */
   protected static boolean isBiDi(IDNA.Info info) {
      return info.isBiDi;
   }

   /** @deprecated */
   protected static void setNotOkBiDi(IDNA.Info info) {
      info.isOkBiDi = false;
   }

   /** @deprecated */
   protected static boolean isOkBiDi(IDNA.Info info) {
      return info.isOkBiDi;
   }

   public static StringBuffer convertToASCII(String src, int options) throws StringPrepParseException {
      UCharacterIterator iter = UCharacterIterator.getInstance(src);
      return convertToASCII(iter, options);
   }

   public static StringBuffer convertToASCII(StringBuffer src, int options) throws StringPrepParseException {
      UCharacterIterator iter = UCharacterIterator.getInstance(src);
      return convertToASCII(iter, options);
   }

   public static StringBuffer convertToASCII(UCharacterIterator src, int options) throws StringPrepParseException {
      return IDNA2003.convertToASCII(src, options);
   }

   public static StringBuffer convertIDNToASCII(UCharacterIterator src, int options) throws StringPrepParseException {
      return convertIDNToASCII(src.getText(), options);
   }

   public static StringBuffer convertIDNToASCII(StringBuffer src, int options) throws StringPrepParseException {
      return convertIDNToASCII(src.toString(), options);
   }

   public static StringBuffer convertIDNToASCII(String src, int options) throws StringPrepParseException {
      return IDNA2003.convertIDNToASCII(src, options);
   }

   public static StringBuffer convertToUnicode(String src, int options) throws StringPrepParseException {
      UCharacterIterator iter = UCharacterIterator.getInstance(src);
      return convertToUnicode(iter, options);
   }

   public static StringBuffer convertToUnicode(StringBuffer src, int options) throws StringPrepParseException {
      UCharacterIterator iter = UCharacterIterator.getInstance(src);
      return convertToUnicode(iter, options);
   }

   public static StringBuffer convertToUnicode(UCharacterIterator src, int options) throws StringPrepParseException {
      return IDNA2003.convertToUnicode(src, options);
   }

   public static StringBuffer convertIDNToUnicode(UCharacterIterator src, int options) throws StringPrepParseException {
      return convertIDNToUnicode(src.getText(), options);
   }

   public static StringBuffer convertIDNToUnicode(StringBuffer src, int options) throws StringPrepParseException {
      return convertIDNToUnicode(src.toString(), options);
   }

   public static StringBuffer convertIDNToUnicode(String src, int options) throws StringPrepParseException {
      return IDNA2003.convertIDNToUnicode(src, options);
   }

   public static int compare(StringBuffer s1, StringBuffer s2, int options) throws StringPrepParseException {
      if(s1 != null && s2 != null) {
         return IDNA2003.compare(s1.toString(), s2.toString(), options);
      } else {
         throw new IllegalArgumentException("One of the source buffers is null");
      }
   }

   public static int compare(String s1, String s2, int options) throws StringPrepParseException {
      if(s1 != null && s2 != null) {
         return IDNA2003.compare(s1, s2, options);
      } else {
         throw new IllegalArgumentException("One of the source buffers is null");
      }
   }

   public static int compare(UCharacterIterator s1, UCharacterIterator s2, int options) throws StringPrepParseException {
      if(s1 != null && s2 != null) {
         return IDNA2003.compare(s1.getText(), s2.getText(), options);
      } else {
         throw new IllegalArgumentException("One of the source buffers is null");
      }
   }

   public static enum Error {
      EMPTY_LABEL,
      LABEL_TOO_LONG,
      DOMAIN_NAME_TOO_LONG,
      LEADING_HYPHEN,
      TRAILING_HYPHEN,
      HYPHEN_3_4,
      LEADING_COMBINING_MARK,
      DISALLOWED,
      PUNYCODE,
      LABEL_HAS_DOT,
      INVALID_ACE_LABEL,
      BIDI,
      CONTEXTJ,
      CONTEXTO_PUNCTUATION,
      CONTEXTO_DIGITS;
   }

   public static final class Info {
      private EnumSet errors = EnumSet.noneOf(IDNA.Error.class);
      private EnumSet labelErrors = EnumSet.noneOf(IDNA.Error.class);
      private boolean isTransDiff = false;
      private boolean isBiDi = false;
      private boolean isOkBiDi = true;

      public boolean hasErrors() {
         return !this.errors.isEmpty();
      }

      public Set getErrors() {
         return this.errors;
      }

      public boolean isTransitionalDifferent() {
         return this.isTransDiff;
      }

      private void reset() {
         this.errors.clear();
         this.labelErrors.clear();
         this.isTransDiff = false;
         this.isBiDi = false;
         this.isOkBiDi = true;
      }
   }
}
