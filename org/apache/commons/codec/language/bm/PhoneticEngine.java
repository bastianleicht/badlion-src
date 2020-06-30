package org.apache.commons.codec.language.bm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.apache.commons.codec.language.bm.Lang;
import org.apache.commons.codec.language.bm.Languages;
import org.apache.commons.codec.language.bm.NameType;
import org.apache.commons.codec.language.bm.Rule;
import org.apache.commons.codec.language.bm.RuleType;

public class PhoneticEngine {
   private static final Map NAME_PREFIXES = new EnumMap(NameType.class);
   private static final int DEFAULT_MAX_PHONEMES = 20;
   private final Lang lang;
   private final NameType nameType;
   private final RuleType ruleType;
   private final boolean concat;
   private final int maxPhonemes;

   private static String join(Iterable strings, String sep) {
      StringBuilder sb = new StringBuilder();
      Iterator<String> si = strings.iterator();
      if(si.hasNext()) {
         sb.append((String)si.next());
      }

      while(si.hasNext()) {
         sb.append(sep).append((String)si.next());
      }

      return sb.toString();
   }

   public PhoneticEngine(NameType nameType, RuleType ruleType, boolean concat) {
      this(nameType, ruleType, concat, 20);
   }

   public PhoneticEngine(NameType nameType, RuleType ruleType, boolean concat, int maxPhonemes) {
      if(ruleType == RuleType.RULES) {
         throw new IllegalArgumentException("ruleType must not be " + RuleType.RULES);
      } else {
         this.nameType = nameType;
         this.ruleType = ruleType;
         this.concat = concat;
         this.lang = Lang.instance(nameType);
         this.maxPhonemes = maxPhonemes;
      }
   }

   private PhoneticEngine.PhonemeBuilder applyFinalRules(PhoneticEngine.PhonemeBuilder phonemeBuilder, Map finalRules) {
      if(finalRules == null) {
         throw new NullPointerException("finalRules can not be null");
      } else if(finalRules.isEmpty()) {
         return phonemeBuilder;
      } else {
         Set<Rule.Phoneme> phonemes = new TreeSet(Rule.Phoneme.COMPARATOR);

         for(Rule.Phoneme phoneme : phonemeBuilder.getPhonemes()) {
            PhoneticEngine.PhonemeBuilder subBuilder = PhoneticEngine.PhonemeBuilder.empty(phoneme.getLanguages());
            String phonemeText = phoneme.getPhonemeText().toString();

            PhoneticEngine.RulesApplication rulesApplication;
            for(int i = 0; i < phonemeText.length(); i = rulesApplication.getI()) {
               rulesApplication = (new PhoneticEngine.RulesApplication(finalRules, phonemeText, subBuilder, i, this.maxPhonemes)).invoke();
               boolean found = rulesApplication.isFound();
               subBuilder = rulesApplication.getPhonemeBuilder();
               if(!found) {
                  subBuilder.append(phonemeText.subSequence(i, i + 1));
               }
            }

            phonemes.addAll(subBuilder.getPhonemes());
         }

         return new PhoneticEngine.PhonemeBuilder(phonemes);
      }
   }

   public String encode(String input) {
      Languages.LanguageSet languageSet = this.lang.guessLanguages(input);
      return this.encode(input, languageSet);
   }

   public String encode(String input, Languages.LanguageSet languageSet) {
      Map<String, List<Rule>> rules = Rule.getInstanceMap(this.nameType, RuleType.RULES, languageSet);
      Map<String, List<Rule>> finalRules1 = Rule.getInstanceMap(this.nameType, this.ruleType, "common");
      Map<String, List<Rule>> finalRules2 = Rule.getInstanceMap(this.nameType, this.ruleType, languageSet);
      input = input.toLowerCase(Locale.ENGLISH).replace('-', ' ').trim();
      if(this.nameType == NameType.GENERIC) {
         if(input.length() >= 2 && input.substring(0, 2).equals("d\'")) {
            String remainder = input.substring(2);
            String combined = "d" + remainder;
            return "(" + this.encode(remainder) + ")-(" + this.encode(combined) + ")";
         }

         for(String l : (Set)NAME_PREFIXES.get(this.nameType)) {
            if(input.startsWith(l + " ")) {
               String remainder = input.substring(l.length() + 1);
               String combined = l + remainder;
               return "(" + this.encode(remainder) + ")-(" + this.encode(combined) + ")";
            }
         }
      }

      List<String> words = Arrays.asList(input.split("\\s+"));
      List<String> words2 = new ArrayList();
      switch(this.nameType) {
      case SEPHARDIC:
         for(String aWord : words) {
            String[] parts = aWord.split("\'");
            String lastPart = parts[parts.length - 1];
            words2.add(lastPart);
         }

         words2.removeAll((Collection)NAME_PREFIXES.get(this.nameType));
         break;
      case ASHKENAZI:
         words2.addAll(words);
         words2.removeAll((Collection)NAME_PREFIXES.get(this.nameType));
         break;
      case GENERIC:
         words2.addAll(words);
         break;
      default:
         throw new IllegalStateException("Unreachable case: " + this.nameType);
      }

      if(this.concat) {
         input = join(words2, " ");
      } else {
         if(words2.size() != 1) {
            StringBuilder result = new StringBuilder();

            for(String word : words2) {
               result.append("-").append(this.encode(word));
            }

            return result.substring(1);
         }

         input = (String)words.iterator().next();
      }

      PhoneticEngine.PhonemeBuilder phonemeBuilder = PhoneticEngine.PhonemeBuilder.empty(languageSet);

      PhoneticEngine.RulesApplication rulesApplication;
      for(int i = 0; i < input.length(); phonemeBuilder = rulesApplication.getPhonemeBuilder()) {
         rulesApplication = (new PhoneticEngine.RulesApplication(rules, input, phonemeBuilder, i, this.maxPhonemes)).invoke();
         i = rulesApplication.getI();
      }

      phonemeBuilder = this.applyFinalRules(phonemeBuilder, finalRules1);
      phonemeBuilder = this.applyFinalRules(phonemeBuilder, finalRules2);
      return phonemeBuilder.makeString();
   }

   public Lang getLang() {
      return this.lang;
   }

   public NameType getNameType() {
      return this.nameType;
   }

   public RuleType getRuleType() {
      return this.ruleType;
   }

   public boolean isConcat() {
      return this.concat;
   }

   public int getMaxPhonemes() {
      return this.maxPhonemes;
   }

   static {
      NAME_PREFIXES.put(NameType.ASHKENAZI, Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[]{"bar", "ben", "da", "de", "van", "von"}))));
      NAME_PREFIXES.put(NameType.SEPHARDIC, Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[]{"al", "el", "da", "dal", "de", "del", "dela", "de la", "della", "des", "di", "do", "dos", "du", "van", "von"}))));
      NAME_PREFIXES.put(NameType.GENERIC, Collections.unmodifiableSet(new HashSet(Arrays.asList(new String[]{"da", "dal", "de", "del", "dela", "de la", "della", "des", "di", "do", "dos", "du", "van", "von"}))));
   }

   static final class PhonemeBuilder {
      private final Set phonemes;

      public static PhoneticEngine.PhonemeBuilder empty(Languages.LanguageSet languages) {
         return new PhoneticEngine.PhonemeBuilder(new Rule.Phoneme("", languages));
      }

      private PhonemeBuilder(Rule.Phoneme phoneme) {
         this.phonemes = new LinkedHashSet();
         this.phonemes.add(phoneme);
      }

      private PhonemeBuilder(Set phonemes) {
         this.phonemes = phonemes;
      }

      public void append(CharSequence str) {
         for(Rule.Phoneme ph : this.phonemes) {
            ph.append(str);
         }

      }

      public void apply(Rule.PhonemeExpr phonemeExpr, int maxPhonemes) {
         Set<Rule.Phoneme> newPhonemes = new LinkedHashSet(maxPhonemes);

         label65:
         for(Rule.Phoneme left : this.phonemes) {
            for(Rule.Phoneme right : phonemeExpr.getPhonemes()) {
               Languages.LanguageSet languages = left.getLanguages().restrictTo(right.getLanguages());
               if(!languages.isEmpty()) {
                  Rule.Phoneme join = new Rule.Phoneme(left, right, languages);
                  if(newPhonemes.size() < maxPhonemes) {
                     newPhonemes.add(join);
                     if(newPhonemes.size() >= maxPhonemes) {
                        break label65;
                     }
                  }
               }
            }
         }

         this.phonemes.clear();
         this.phonemes.addAll(newPhonemes);
      }

      public Set getPhonemes() {
         return this.phonemes;
      }

      public String makeString() {
         StringBuilder sb = new StringBuilder();

         for(Rule.Phoneme ph : this.phonemes) {
            if(sb.length() > 0) {
               sb.append("|");
            }

            sb.append(ph.getPhonemeText());
         }

         return sb.toString();
      }
   }

   private static final class RulesApplication {
      private final Map finalRules;
      private final CharSequence input;
      private PhoneticEngine.PhonemeBuilder phonemeBuilder;
      private int i;
      private final int maxPhonemes;
      private boolean found;

      public RulesApplication(Map finalRules, CharSequence input, PhoneticEngine.PhonemeBuilder phonemeBuilder, int i, int maxPhonemes) {
         if(finalRules == null) {
            throw new NullPointerException("The finalRules argument must not be null");
         } else {
            this.finalRules = finalRules;
            this.phonemeBuilder = phonemeBuilder;
            this.input = input;
            this.i = i;
            this.maxPhonemes = maxPhonemes;
         }
      }

      public int getI() {
         return this.i;
      }

      public PhoneticEngine.PhonemeBuilder getPhonemeBuilder() {
         return this.phonemeBuilder;
      }

      public PhoneticEngine.RulesApplication invoke() {
         this.found = false;
         int patternLength = 1;
         List<Rule> rules = (List)this.finalRules.get(this.input.subSequence(this.i, this.i + patternLength));
         if(rules != null) {
            for(Rule rule : rules) {
               String pattern = rule.getPattern();
               patternLength = pattern.length();
               if(rule.patternAndContextMatches(this.input, this.i)) {
                  this.phonemeBuilder.apply(rule.getPhoneme(), this.maxPhonemes);
                  this.found = true;
                  break;
               }
            }
         }

         if(!this.found) {
            patternLength = 1;
         }

         this.i += patternLength;
         return this;
      }

      public boolean isFound() {
         return this.found;
      }
   }
}
