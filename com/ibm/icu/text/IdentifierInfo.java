package com.ibm.icu.text;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.lang.UScript;
import com.ibm.icu.text.SpoofChecker;
import com.ibm.icu.text.UnicodeSet;
import java.util.BitSet;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/** @deprecated */
public class IdentifierInfo {
   private static final UnicodeSet ASCII = (new UnicodeSet(0, 127)).freeze();
   private String identifier;
   private final BitSet requiredScripts = new BitSet();
   private final Set scriptSetSet = new HashSet();
   private final BitSet commonAmongAlternates = new BitSet();
   private final UnicodeSet numerics = new UnicodeSet();
   private final UnicodeSet identifierProfile = new UnicodeSet(0, 1114111);
   private static final BitSet JAPANESE = set(new BitSet(), new int[]{25, 17, 20, 22});
   private static final BitSet CHINESE = set(new BitSet(), new int[]{25, 17, 5});
   private static final BitSet KOREAN = set(new BitSet(), new int[]{25, 17, 18});
   private static final BitSet CONFUSABLE_WITH_LATIN = set(new BitSet(), new int[]{8, 14, 6});
   /** @deprecated */
   public static final Comparator BITSET_COMPARATOR = new Comparator() {
      public int compare(BitSet arg0, BitSet arg1) {
         int diff = arg0.cardinality() - arg1.cardinality();
         if(diff != 0) {
            return diff;
         } else {
            int i0 = arg0.nextSetBit(0);

            for(int i1 = arg1.nextSetBit(0); (diff = i0 - i1) == 0 && i0 > 0; i1 = arg1.nextSetBit(i1 + 1)) {
               i0 = arg0.nextSetBit(i0 + 1);
            }

            return diff;
         }
      }
   };

   private IdentifierInfo clear() {
      this.requiredScripts.clear();
      this.scriptSetSet.clear();
      this.numerics.clear();
      this.commonAmongAlternates.clear();
      return this;
   }

   /** @deprecated */
   public IdentifierInfo setIdentifierProfile(UnicodeSet identifierProfile) {
      this.identifierProfile.set(identifierProfile);
      return this;
   }

   /** @deprecated */
   public UnicodeSet getIdentifierProfile() {
      return new UnicodeSet(this.identifierProfile);
   }

   /** @deprecated */
   public IdentifierInfo setIdentifier(String identifier) {
      this.identifier = identifier;
      this.clear();
      BitSet scriptsForCP = new BitSet();

      for(int i = 0; i < identifier.length(); i += Character.charCount(i)) {
         int cp = Character.codePointAt(identifier, i);
         if(UCharacter.getType(cp) == 9) {
            this.numerics.add(cp - UCharacter.getNumericValue(cp));
         }

         UScript.getScriptExtensions(cp, scriptsForCP);
         scriptsForCP.clear(0);
         scriptsForCP.clear(1);
         switch(scriptsForCP.cardinality()) {
         case 0:
            break;
         case 1:
            this.requiredScripts.or(scriptsForCP);
            break;
         default:
            if(!this.requiredScripts.intersects(scriptsForCP) && this.scriptSetSet.add(scriptsForCP)) {
               scriptsForCP = new BitSet();
            }
         }
      }

      if(this.scriptSetSet.size() > 0) {
         this.commonAmongAlternates.set(0, 159);
         Iterator<BitSet> it = this.scriptSetSet.iterator();

         label211:
         while(it.hasNext()) {
            BitSet next = (BitSet)it.next();
            if(this.requiredScripts.intersects(next)) {
               it.remove();
            } else {
               this.commonAmongAlternates.and(next);
               Iterator i$ = this.scriptSetSet.iterator();

               while(true) {
                  if(!i$.hasNext()) {
                     continue label211;
                  }

                  BitSet other = (BitSet)i$.next();
                  if(next != other && contains(next, other)) {
                     break;
                  }
               }

               it.remove();
            }
         }
      }

      if(this.scriptSetSet.size() == 0) {
         this.commonAmongAlternates.clear();
      }

      return this;
   }

   /** @deprecated */
   public String getIdentifier() {
      return this.identifier;
   }

   /** @deprecated */
   public BitSet getScripts() {
      return (BitSet)this.requiredScripts.clone();
   }

   /** @deprecated */
   public Set getAlternates() {
      Set<BitSet> result = new HashSet();

      for(BitSet item : this.scriptSetSet) {
         result.add((BitSet)item.clone());
      }

      return result;
   }

   /** @deprecated */
   public UnicodeSet getNumerics() {
      return new UnicodeSet(this.numerics);
   }

   /** @deprecated */
   public BitSet getCommonAmongAlternates() {
      return (BitSet)this.commonAmongAlternates.clone();
   }

   /** @deprecated */
   public SpoofChecker.RestrictionLevel getRestrictionLevel() {
      if(this.identifierProfile.containsAll(this.identifier) && this.getNumerics().size() <= 1) {
         if(ASCII.containsAll(this.identifier)) {
            return SpoofChecker.RestrictionLevel.ASCII;
         } else {
            int cardinalityPlus = this.requiredScripts.cardinality() + (this.commonAmongAlternates.cardinality() == 0?this.scriptSetSet.size():1);
            return cardinalityPlus < 2?SpoofChecker.RestrictionLevel.HIGHLY_RESTRICTIVE:(!this.containsWithAlternates(JAPANESE, this.requiredScripts) && !this.containsWithAlternates(CHINESE, this.requiredScripts) && !this.containsWithAlternates(KOREAN, this.requiredScripts)?(cardinalityPlus == 2 && this.requiredScripts.get(25) && !this.requiredScripts.intersects(CONFUSABLE_WITH_LATIN)?SpoofChecker.RestrictionLevel.MODERATELY_RESTRICTIVE:SpoofChecker.RestrictionLevel.MINIMALLY_RESTRICTIVE):SpoofChecker.RestrictionLevel.HIGHLY_RESTRICTIVE);
         }
      } else {
         return SpoofChecker.RestrictionLevel.UNRESTRICTIVE;
      }
   }

   /** @deprecated */
   public int getScriptCount() {
      int count = this.requiredScripts.cardinality() + (this.commonAmongAlternates.cardinality() == 0?this.scriptSetSet.size():1);
      return count;
   }

   /** @deprecated */
   public String toString() {
      return this.identifier + ", " + this.identifierProfile.toPattern(false) + ", " + this.getRestrictionLevel() + ", " + displayScripts(this.requiredScripts) + ", " + displayAlternates(this.scriptSetSet) + ", " + this.numerics.toPattern(false);
   }

   private boolean containsWithAlternates(BitSet container, BitSet containee) {
      if(!contains(container, containee)) {
         return false;
      } else {
         for(BitSet alternatives : this.scriptSetSet) {
            if(!container.intersects(alternatives)) {
               return false;
            }
         }

         return true;
      }
   }

   /** @deprecated */
   public static String displayAlternates(Set alternates) {
      if(alternates.size() == 0) {
         return "";
      } else {
         StringBuilder result = new StringBuilder();
         Set<BitSet> sorted = new TreeSet(BITSET_COMPARATOR);
         sorted.addAll(alternates);

         for(BitSet item : sorted) {
            if(result.length() != 0) {
               result.append("; ");
            }

            result.append(displayScripts(item));
         }

         return result.toString();
      }
   }

   /** @deprecated */
   public static String displayScripts(BitSet scripts) {
      StringBuilder result = new StringBuilder();

      for(int i = scripts.nextSetBit(0); i >= 0; i = scripts.nextSetBit(i + 1)) {
         if(result.length() != 0) {
            result.append(' ');
         }

         result.append(UScript.getShortName(i));
      }

      return result.toString();
   }

   /** @deprecated */
   public static BitSet parseScripts(String scriptsString) {
      BitSet result = new BitSet();

      for(String item : scriptsString.trim().split(",?\\s+")) {
         if(item.length() != 0) {
            result.set(UScript.getCodeFromName(item));
         }
      }

      return result;
   }

   /** @deprecated */
   public static Set parseAlternates(String scriptsSetString) {
      Set<BitSet> result = new HashSet();

      for(String item : scriptsSetString.trim().split("\\s*;\\s*")) {
         if(item.length() != 0) {
            result.add(parseScripts(item));
         }
      }

      return result;
   }

   /** @deprecated */
   public static final boolean contains(BitSet container, BitSet containee) {
      for(int i = containee.nextSetBit(0); i >= 0; i = containee.nextSetBit(i + 1)) {
         if(!container.get(i)) {
            return false;
         }
      }

      return true;
   }

   /** @deprecated */
   public static final BitSet set(BitSet bitset, int... values) {
      for(int value : values) {
         bitset.set(value);
      }

      return bitset;
   }
}
