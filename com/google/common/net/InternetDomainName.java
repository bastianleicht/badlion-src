package com.google.common.net;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Ascii;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.thirdparty.publicsuffix.PublicSuffixPatterns;
import java.util.List;
import javax.annotation.Nullable;

@Beta
@GwtCompatible
public final class InternetDomainName {
   private static final CharMatcher DOTS_MATCHER = CharMatcher.anyOf(".。．｡");
   private static final Splitter DOT_SPLITTER = Splitter.on('.');
   private static final Joiner DOT_JOINER = Joiner.on('.');
   private static final int NO_PUBLIC_SUFFIX_FOUND = -1;
   private static final String DOT_REGEX = "\\.";
   private static final int MAX_PARTS = 127;
   private static final int MAX_LENGTH = 253;
   private static final int MAX_DOMAIN_PART_LENGTH = 63;
   private final String name;
   private final ImmutableList parts;
   private final int publicSuffixIndex;
   private static final CharMatcher DASH_MATCHER = CharMatcher.anyOf("-_");
   private static final CharMatcher PART_CHAR_MATCHER = CharMatcher.JAVA_LETTER_OR_DIGIT.or(DASH_MATCHER);

   InternetDomainName(String name) {
      name = Ascii.toLowerCase(DOTS_MATCHER.replaceFrom(name, '.'));
      if(name.endsWith(".")) {
         name = name.substring(0, name.length() - 1);
      }

      Preconditions.checkArgument(name.length() <= 253, "Domain name too long: \'%s\':", new Object[]{name});
      this.name = name;
      this.parts = ImmutableList.copyOf(DOT_SPLITTER.split(name));
      Preconditions.checkArgument(this.parts.size() <= 127, "Domain has too many parts: \'%s\'", new Object[]{name});
      Preconditions.checkArgument(validateSyntax(this.parts), "Not a valid domain name: \'%s\'", new Object[]{name});
      this.publicSuffixIndex = this.findPublicSuffix();
   }

   private int findPublicSuffix() {
      int partsSize = this.parts.size();

      for(int i = 0; i < partsSize; ++i) {
         String ancestorName = DOT_JOINER.join((Iterable)this.parts.subList(i, partsSize));
         if(PublicSuffixPatterns.EXACT.containsKey(ancestorName)) {
            return i;
         }

         if(PublicSuffixPatterns.EXCLUDED.containsKey(ancestorName)) {
            return i + 1;
         }

         if(matchesWildcardPublicSuffix(ancestorName)) {
            return i;
         }
      }

      return -1;
   }

   public static InternetDomainName from(String domain) {
      return new InternetDomainName((String)Preconditions.checkNotNull(domain));
   }

   private static boolean validateSyntax(List parts) {
      int lastIndex = parts.size() - 1;
      if(!validatePart((String)parts.get(lastIndex), true)) {
         return false;
      } else {
         for(int i = 0; i < lastIndex; ++i) {
            String part = (String)parts.get(i);
            if(!validatePart(part, false)) {
               return false;
            }
         }

         return true;
      }
   }

   private static boolean validatePart(String part, boolean isFinalPart) {
      if(part.length() >= 1 && part.length() <= 63) {
         String asciiChars = CharMatcher.ASCII.retainFrom(part);
         return !PART_CHAR_MATCHER.matchesAllOf(asciiChars)?false:(!DASH_MATCHER.matches(part.charAt(0)) && !DASH_MATCHER.matches(part.charAt(part.length() - 1))?!isFinalPart || !CharMatcher.DIGIT.matches(part.charAt(0)):false);
      } else {
         return false;
      }
   }

   public ImmutableList parts() {
      return this.parts;
   }

   public boolean isPublicSuffix() {
      return this.publicSuffixIndex == 0;
   }

   public boolean hasPublicSuffix() {
      return this.publicSuffixIndex != -1;
   }

   public InternetDomainName publicSuffix() {
      return this.hasPublicSuffix()?this.ancestor(this.publicSuffixIndex):null;
   }

   public boolean isUnderPublicSuffix() {
      return this.publicSuffixIndex > 0;
   }

   public boolean isTopPrivateDomain() {
      return this.publicSuffixIndex == 1;
   }

   public InternetDomainName topPrivateDomain() {
      if(this.isTopPrivateDomain()) {
         return this;
      } else {
         Preconditions.checkState(this.isUnderPublicSuffix(), "Not under a public suffix: %s", new Object[]{this.name});
         return this.ancestor(this.publicSuffixIndex - 1);
      }
   }

   public boolean hasParent() {
      return this.parts.size() > 1;
   }

   public InternetDomainName parent() {
      Preconditions.checkState(this.hasParent(), "Domain \'%s\' has no parent", new Object[]{this.name});
      return this.ancestor(1);
   }

   private InternetDomainName ancestor(int levels) {
      return from(DOT_JOINER.join((Iterable)this.parts.subList(levels, this.parts.size())));
   }

   public InternetDomainName child(String leftParts) {
      return from((String)Preconditions.checkNotNull(leftParts) + "." + this.name);
   }

   public static boolean isValid(String name) {
      try {
         from(name);
         return true;
      } catch (IllegalArgumentException var2) {
         return false;
      }
   }

   private static boolean matchesWildcardPublicSuffix(String domain) {
      String[] pieces = domain.split("\\.", 2);
      return pieces.length == 2 && PublicSuffixPatterns.UNDER.containsKey(pieces[1]);
   }

   public String toString() {
      return this.name;
   }

   public boolean equals(@Nullable Object object) {
      if(object == this) {
         return true;
      } else if(object instanceof InternetDomainName) {
         InternetDomainName that = (InternetDomainName)object;
         return this.name.equals(that.name);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.name.hashCode();
   }
}
