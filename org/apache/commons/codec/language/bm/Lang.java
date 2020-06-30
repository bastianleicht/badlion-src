package org.apache.commons.codec.language.bm;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.commons.codec.language.bm.Languages;
import org.apache.commons.codec.language.bm.NameType;

public class Lang {
   private static final Map Langs = new EnumMap(NameType.class);
   private static final String LANGUAGE_RULES_RN = "org/apache/commons/codec/language/bm/lang.txt";
   private final Languages languages;
   private final List rules;

   public static Lang instance(NameType nameType) {
      return (Lang)Langs.get(nameType);
   }

   public static Lang loadFromResource(String languageRulesResourceName, Languages languages) {
      List<Lang.LangRule> rules = new ArrayList();
      InputStream lRulesIS = Lang.class.getClassLoader().getResourceAsStream(languageRulesResourceName);
      if(lRulesIS == null) {
         throw new IllegalStateException("Unable to resolve required resource:org/apache/commons/codec/language/bm/lang.txt");
      } else {
         Scanner scanner = new Scanner(lRulesIS, "UTF-8");

         try {
            boolean inExtendedComment = false;

            while(scanner.hasNextLine()) {
               String rawLine = scanner.nextLine();
               String line = rawLine;
               if(inExtendedComment) {
                  if(rawLine.endsWith("*/")) {
                     inExtendedComment = false;
                  }
               } else if(rawLine.startsWith("/*")) {
                  inExtendedComment = true;
               } else {
                  int cmtI = rawLine.indexOf("//");
                  if(cmtI >= 0) {
                     line = rawLine.substring(0, cmtI);
                  }

                  line = line.trim();
                  if(line.length() != 0) {
                     String[] parts = line.split("\\s+");
                     if(parts.length != 3) {
                        throw new IllegalArgumentException("Malformed line \'" + rawLine + "\' in language resource \'" + languageRulesResourceName + "\'");
                     }

                     Pattern pattern = Pattern.compile(parts[0]);
                     String[] langs = parts[1].split("\\+");
                     boolean accept = parts[2].equals("true");
                     rules.add(new Lang.LangRule(pattern, new HashSet(Arrays.asList(langs)), accept));
                  }
               }
            }
         } finally {
            scanner.close();
         }

         return new Lang(rules, languages);
      }
   }

   private Lang(List rules, Languages languages) {
      this.rules = Collections.unmodifiableList(rules);
      this.languages = languages;
   }

   public String guessLanguage(String text) {
      Languages.LanguageSet ls = this.guessLanguages(text);
      return ls.isSingleton()?ls.getAny():"any";
   }

   public Languages.LanguageSet guessLanguages(String input) {
      String text = input.toLowerCase(Locale.ENGLISH);
      Set<String> langs = new HashSet(this.languages.getLanguages());

      for(Lang.LangRule rule : this.rules) {
         if(rule.matches(text)) {
            if(rule.acceptOnMatch) {
               langs.retainAll(rule.languages);
            } else {
               langs.removeAll(rule.languages);
            }
         }
      }

      Languages.LanguageSet ls = Languages.LanguageSet.from(langs);
      return ls.equals(Languages.NO_LANGUAGES)?Languages.ANY_LANGUAGE:ls;
   }

   static {
      for(NameType s : NameType.values()) {
         Langs.put(s, loadFromResource("org/apache/commons/codec/language/bm/lang.txt", Languages.getInstance(s)));
      }

   }

   private static final class LangRule {
      private final boolean acceptOnMatch;
      private final Set languages;
      private final Pattern pattern;

      private LangRule(Pattern pattern, Set languages, boolean acceptOnMatch) {
         this.pattern = pattern;
         this.languages = languages;
         this.acceptOnMatch = acceptOnMatch;
      }

      public boolean matches(String txt) {
         return this.pattern.matcher(txt).find();
      }
   }
}
