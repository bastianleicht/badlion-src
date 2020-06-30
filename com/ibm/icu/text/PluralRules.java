package com.ibm.icu.text;

import com.ibm.icu.impl.PatternProps;
import com.ibm.icu.impl.PluralRulesLoader;
import com.ibm.icu.impl.Utility;
import com.ibm.icu.util.Output;
import com.ibm.icu.util.ULocale;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class PluralRules implements Serializable {
   private static final long serialVersionUID = 1L;
   private final PluralRules.RuleList rules;
   private final Set keywords;
   private int repeatLimit;
   private transient int hashCode;
   private transient Map _keySamplesMap;
   private transient Map _keyLimitedMap;
   public static final String KEYWORD_ZERO = "zero";
   public static final String KEYWORD_ONE = "one";
   public static final String KEYWORD_TWO = "two";
   public static final String KEYWORD_FEW = "few";
   public static final String KEYWORD_MANY = "many";
   public static final String KEYWORD_OTHER = "other";
   public static final double NO_UNIQUE_VALUE = -0.00123456777D;
   private static final PluralRules.Constraint NO_CONSTRAINT = new PluralRules.Constraint() {
      private static final long serialVersionUID = 9163464945387899416L;

      public boolean isFulfilled(double n) {
         return true;
      }

      public boolean isLimited() {
         return false;
      }

      public String toString() {
         return "n is any";
      }

      public int updateRepeatLimit(int limit) {
         return limit;
      }
   };
   private static final PluralRules.Rule DEFAULT_RULE = new PluralRules.Rule() {
      private static final long serialVersionUID = -5677499073940822149L;

      public String getKeyword() {
         return "other";
      }

      public boolean appliesTo(double n) {
         return true;
      }

      public boolean isLimited() {
         return false;
      }

      public String toString() {
         return "(other)";
      }

      public int updateRepeatLimit(int limit) {
         return limit;
      }
   };
   public static final PluralRules DEFAULT = new PluralRules(new PluralRules.RuleChain(DEFAULT_RULE));

   public static PluralRules parseDescription(String description) throws ParseException {
      description = description.trim();
      return description.length() == 0?DEFAULT:new PluralRules(parseRuleChain(description));
   }

   public static PluralRules createRules(String description) {
      try {
         return parseDescription(description);
      } catch (ParseException var2) {
         return null;
      }
   }

   private static PluralRules.Constraint parseConstraint(String description) throws ParseException {
      description = description.trim().toLowerCase(Locale.ENGLISH);
      PluralRules.Constraint result = null;
      String[] or_together = Utility.splitString(description, "or");

      for(int i = 0; i < or_together.length; ++i) {
         PluralRules.Constraint andConstraint = null;
         String[] and_together = Utility.splitString(or_together[i], "and");

         for(int j = 0; j < and_together.length; ++j) {
            PluralRules.Constraint newConstraint = NO_CONSTRAINT;
            String condition = and_together[j].trim();
            String[] tokens = Utility.splitWhitespace(condition);
            int mod = 0;
            boolean inRange = true;
            boolean integersOnly = true;
            long lowBound = Long.MAX_VALUE;
            long highBound = Long.MIN_VALUE;
            long[] vals = null;
            boolean isRange = false;
            int x = 0;
            String t = tokens[x++];
            if(!"n".equals(t)) {
               throw unexpected(t, condition);
            }

            if(x < tokens.length) {
               t = tokens[x++];
               if("mod".equals(t)) {
                  mod = Integer.parseInt(tokens[x++]);
                  t = nextToken(tokens, x++, condition);
               }

               if("is".equals(t)) {
                  t = nextToken(tokens, x++, condition);
                  if("not".equals(t)) {
                     inRange = false;
                     t = nextToken(tokens, x++, condition);
                  }
               } else {
                  isRange = true;
                  if("not".equals(t)) {
                     inRange = false;
                     t = nextToken(tokens, x++, condition);
                  }

                  if("in".equals(t)) {
                     t = nextToken(tokens, x++, condition);
                  } else {
                     if(!"within".equals(t)) {
                        throw unexpected(t, condition);
                     }

                     integersOnly = false;
                     t = nextToken(tokens, x++, condition);
                  }
               }

               if(isRange) {
                  String[] range_list = Utility.splitString(t, ",");
                  vals = new long[range_list.length * 2];
                  int k1 = 0;

                  for(int k2 = 0; k1 < range_list.length; k2 += 2) {
                     String range = range_list[k1];
                     String[] pair = Utility.splitString(range, "..");
                     long low;
                     long high;
                     if(pair.length == 2) {
                        low = Long.parseLong(pair[0]);
                        high = Long.parseLong(pair[1]);
                        if(low > high) {
                           throw unexpected(range, condition);
                        }
                     } else {
                        if(pair.length != 1) {
                           throw unexpected(range, condition);
                        }

                        low = high = Long.parseLong(pair[0]);
                     }

                     vals[k2] = low;
                     vals[k2 + 1] = high;
                     lowBound = Math.min(lowBound, low);
                     highBound = Math.max(highBound, high);
                     ++k1;
                  }

                  if(vals.length == 2) {
                     vals = null;
                  }
               } else {
                  lowBound = highBound = Long.parseLong(t);
               }

               if(x != tokens.length) {
                  throw unexpected(tokens[x], condition);
               }

               newConstraint = new PluralRules.RangeConstraint(mod, inRange, integersOnly, lowBound, highBound, vals);
            }

            if(andConstraint == null) {
               andConstraint = newConstraint;
            } else {
               andConstraint = new PluralRules.AndConstraint(andConstraint, newConstraint);
            }
         }

         if(result == null) {
            result = andConstraint;
         } else {
            result = new PluralRules.OrConstraint(result, andConstraint);
         }
      }

      return result;
   }

   private static ParseException unexpected(String token, String context) {
      return new ParseException("unexpected token \'" + token + "\' in \'" + context + "\'", -1);
   }

   private static String nextToken(String[] tokens, int x, String context) throws ParseException {
      if(x < tokens.length) {
         return tokens[x];
      } else {
         throw new ParseException("missing token at end of \'" + context + "\'", -1);
      }
   }

   private static PluralRules.Rule parseRule(String description) throws ParseException {
      int x = description.indexOf(58);
      if(x == -1) {
         throw new ParseException("missing \':\' in rule description \'" + description + "\'", 0);
      } else {
         String keyword = description.substring(0, x).trim();
         if(!isValidKeyword(keyword)) {
            throw new ParseException("keyword \'" + keyword + " is not valid", 0);
         } else {
            description = description.substring(x + 1).trim();
            if(description.length() == 0) {
               throw new ParseException("missing constraint in \'" + description + "\'", x + 1);
            } else {
               PluralRules.Constraint constraint = parseConstraint(description);
               PluralRules.Rule rule = new PluralRules.ConstrainedRule(keyword, constraint);
               return rule;
            }
         }
      }
   }

   private static PluralRules.RuleChain parseRuleChain(String description) throws ParseException {
      PluralRules.RuleChain rc = null;
      String[] rules = Utility.split(description, ';');

      for(int i = 0; i < rules.length; ++i) {
         PluralRules.Rule r = parseRule(rules[i].trim());
         if(rc == null) {
            rc = new PluralRules.RuleChain(r);
         } else {
            rc = rc.addRule(r);
         }
      }

      return rc;
   }

   public static PluralRules forLocale(ULocale locale) {
      return PluralRulesLoader.loader.forLocale(locale, PluralRules.PluralType.CARDINAL);
   }

   public static PluralRules forLocale(ULocale locale, PluralRules.PluralType type) {
      return PluralRulesLoader.loader.forLocale(locale, type);
   }

   private static boolean isValidKeyword(String token) {
      return PatternProps.isIdentifier(token);
   }

   private PluralRules(PluralRules.RuleList rules) {
      this.rules = rules;
      this.keywords = Collections.unmodifiableSet(rules.getKeywords());
   }

   public String select(double number) {
      return this.rules.select(number);
   }

   public Set getKeywords() {
      return this.keywords;
   }

   public double getUniqueKeywordValue(String keyword) {
      Collection<Double> values = this.getAllKeywordValues(keyword);
      return values != null && values.size() == 1?((Double)values.iterator().next()).doubleValue():-0.00123456777D;
   }

   public Collection getAllKeywordValues(String keyword) {
      if(!this.keywords.contains(keyword)) {
         return Collections.emptyList();
      } else {
         Collection<Double> result = (Collection)this.getKeySamplesMap().get(keyword);
         return result.size() > 2 && !((Boolean)this.getKeyLimitedMap().get(keyword)).booleanValue()?null:result;
      }
   }

   public Collection getSamples(String keyword) {
      return !this.keywords.contains(keyword)?null:(Collection)this.getKeySamplesMap().get(keyword);
   }

   private Map getKeyLimitedMap() {
      this.initKeyMaps();
      return this._keyLimitedMap;
   }

   private Map getKeySamplesMap() {
      this.initKeyMaps();
      return this._keySamplesMap;
   }

   private synchronized void initKeyMaps() {
      if(this._keySamplesMap == null) {
         int MAX_SAMPLES = 3;
         Map<String, Boolean> temp = new HashMap();

         for(String k : this.keywords) {
            temp.put(k, Boolean.valueOf(this.rules.isLimited(k)));
         }

         this._keyLimitedMap = temp;
         Map<String, List<Double>> sampleMap = new HashMap();
         int keywordsRemaining = this.keywords.size();
         int limit = Math.max(5, this.getRepeatLimit() * 3) * 2;

         for(int i = 0; keywordsRemaining > 0 && i < limit; ++i) {
            double val = (double)i / 2.0D;
            String keyword = this.select(val);
            boolean keyIsLimited = ((Boolean)this._keyLimitedMap.get(keyword)).booleanValue();
            List<Double> list = (List)sampleMap.get(keyword);
            if(list == null) {
               list = new ArrayList(3);
               sampleMap.put(keyword, list);
            } else if(!keyIsLimited && list.size() == 3) {
               continue;
            }

            list.add(Double.valueOf(val));
            if(!keyIsLimited && list.size() == 3) {
               --keywordsRemaining;
            }
         }

         if(keywordsRemaining > 0) {
            for(String k : this.keywords) {
               if(!sampleMap.containsKey(k)) {
                  sampleMap.put(k, Collections.emptyList());
                  --keywordsRemaining;
                  if(keywordsRemaining == 0) {
                     break;
                  }
               }
            }
         }

         for(Entry<String, List<Double>> entry : sampleMap.entrySet()) {
            sampleMap.put(entry.getKey(), Collections.unmodifiableList((List)entry.getValue()));
         }

         this._keySamplesMap = sampleMap;
      }

   }

   public static ULocale[] getAvailableULocales() {
      return PluralRulesLoader.loader.getAvailableULocales();
   }

   public static ULocale getFunctionalEquivalent(ULocale locale, boolean[] isAvailable) {
      return PluralRulesLoader.loader.getFunctionalEquivalent(locale, isAvailable);
   }

   public String toString() {
      return "keywords: " + this.keywords + " limit: " + this.getRepeatLimit() + " rules: " + this.rules.toString();
   }

   public int hashCode() {
      if(this.hashCode == 0) {
         int newHashCode = this.keywords.hashCode();

         for(int i = 0; i < 12; ++i) {
            newHashCode = newHashCode * 31 + this.select((double)i).hashCode();
         }

         if(newHashCode == 0) {
            newHashCode = 1;
         }

         this.hashCode = newHashCode;
      }

      return this.hashCode;
   }

   public boolean equals(Object rhs) {
      return rhs instanceof PluralRules && this.equals((PluralRules)rhs);
   }

   public boolean equals(PluralRules rhs) {
      if(rhs == null) {
         return false;
      } else if(rhs == this) {
         return true;
      } else if(this.hashCode() != rhs.hashCode()) {
         return false;
      } else if(!rhs.getKeywords().equals(this.keywords)) {
         return false;
      } else {
         int limit = Math.max(this.getRepeatLimit(), rhs.getRepeatLimit());

         for(int i = 0; i < limit * 2; ++i) {
            if(!this.select((double)i).equals(rhs.select((double)i))) {
               return false;
            }
         }

         return true;
      }
   }

   private int getRepeatLimit() {
      if(this.repeatLimit == 0) {
         this.repeatLimit = this.rules.getRepeatLimit() + 1;
      }

      return this.repeatLimit;
   }

   public PluralRules.KeywordStatus getKeywordStatus(String keyword, int offset, Set explicits, Output uniqueValue) {
      if(uniqueValue != null) {
         uniqueValue.value = null;
      }

      if(!this.rules.getKeywords().contains(keyword)) {
         return PluralRules.KeywordStatus.INVALID;
      } else {
         Collection<Double> values = this.getAllKeywordValues(keyword);
         if(values == null) {
            return PluralRules.KeywordStatus.UNBOUNDED;
         } else {
            int originalSize = values.size();
            if(explicits == null) {
               explicits = Collections.emptySet();
            }

            if(originalSize > explicits.size()) {
               if(originalSize == 1) {
                  if(uniqueValue != null) {
                     uniqueValue.value = values.iterator().next();
                  }

                  return PluralRules.KeywordStatus.UNIQUE;
               } else {
                  return PluralRules.KeywordStatus.BOUNDED;
               }
            } else {
               HashSet<Double> subtractedSet = new HashSet(values);

               for(Double explicit : explicits) {
                  subtractedSet.remove(Double.valueOf(explicit.doubleValue() - (double)offset));
               }

               if(subtractedSet.size() == 0) {
                  return PluralRules.KeywordStatus.SUPPRESSED;
               } else {
                  if(uniqueValue != null && subtractedSet.size() == 1) {
                     uniqueValue.value = subtractedSet.iterator().next();
                  }

                  return originalSize == 1?PluralRules.KeywordStatus.UNIQUE:PluralRules.KeywordStatus.BOUNDED;
               }
            }
         }
      }
   }

   private static class AndConstraint extends PluralRules.BinaryConstraint {
      private static final long serialVersionUID = 7766999779862263523L;

      AndConstraint(PluralRules.Constraint a, PluralRules.Constraint b) {
         super(a, b, " && ");
      }

      public boolean isFulfilled(double n) {
         return this.a.isFulfilled(n) && this.b.isFulfilled(n);
      }

      public boolean isLimited() {
         return this.a.isLimited() || this.b.isLimited();
      }
   }

   private abstract static class BinaryConstraint implements PluralRules.Constraint, Serializable {
      private static final long serialVersionUID = 1L;
      protected final PluralRules.Constraint a;
      protected final PluralRules.Constraint b;
      private final String conjunction;

      protected BinaryConstraint(PluralRules.Constraint a, PluralRules.Constraint b, String c) {
         this.a = a;
         this.b = b;
         this.conjunction = c;
      }

      public int updateRepeatLimit(int limit) {
         return this.a.updateRepeatLimit(this.b.updateRepeatLimit(limit));
      }

      public String toString() {
         return this.a.toString() + this.conjunction + this.b.toString();
      }
   }

   private static class ConstrainedRule implements PluralRules.Rule, Serializable {
      private static final long serialVersionUID = 1L;
      private final String keyword;
      private final PluralRules.Constraint constraint;

      public ConstrainedRule(String keyword, PluralRules.Constraint constraint) {
         this.keyword = keyword;
         this.constraint = constraint;
      }

      public PluralRules.Rule and(PluralRules.Constraint c) {
         return new PluralRules.ConstrainedRule(this.keyword, new PluralRules.AndConstraint(this.constraint, c));
      }

      public PluralRules.Rule or(PluralRules.Constraint c) {
         return new PluralRules.ConstrainedRule(this.keyword, new PluralRules.OrConstraint(this.constraint, c));
      }

      public String getKeyword() {
         return this.keyword;
      }

      public boolean appliesTo(double n) {
         return this.constraint.isFulfilled(n);
      }

      public int updateRepeatLimit(int limit) {
         return this.constraint.updateRepeatLimit(limit);
      }

      public boolean isLimited() {
         return this.constraint.isLimited();
      }

      public String toString() {
         return this.keyword + ": " + this.constraint;
      }
   }

   private interface Constraint extends Serializable {
      boolean isFulfilled(double var1);

      boolean isLimited();

      int updateRepeatLimit(int var1);
   }

   public static enum KeywordStatus {
      INVALID,
      SUPPRESSED,
      UNIQUE,
      BOUNDED,
      UNBOUNDED;
   }

   private static class OrConstraint extends PluralRules.BinaryConstraint {
      private static final long serialVersionUID = 1405488568664762222L;

      OrConstraint(PluralRules.Constraint a, PluralRules.Constraint b) {
         super(a, b, " || ");
      }

      public boolean isFulfilled(double n) {
         return this.a.isFulfilled(n) || this.b.isFulfilled(n);
      }

      public boolean isLimited() {
         return this.a.isLimited() && this.b.isLimited();
      }
   }

   public static enum PluralType {
      CARDINAL,
      ORDINAL;
   }

   private static class RangeConstraint implements PluralRules.Constraint, Serializable {
      private static final long serialVersionUID = 1L;
      private int mod;
      private boolean inRange;
      private boolean integersOnly;
      private long lowerBound;
      private long upperBound;
      private long[] range_list;

      RangeConstraint(int mod, boolean inRange, boolean integersOnly, long lowerBound, long upperBound, long[] range_list) {
         this.mod = mod;
         this.inRange = inRange;
         this.integersOnly = integersOnly;
         this.lowerBound = lowerBound;
         this.upperBound = upperBound;
         this.range_list = range_list;
      }

      public boolean isFulfilled(double n) {
         if(this.integersOnly && n - (double)((long)n) != 0.0D) {
            return !this.inRange;
         } else {
            if(this.mod != 0) {
               n %= (double)this.mod;
            }

            boolean test = n >= (double)this.lowerBound && n <= (double)this.upperBound;
            if(test && this.range_list != null) {
               test = false;

               for(int i = 0; !test && i < this.range_list.length; i += 2) {
                  test = n >= (double)this.range_list[i] && n <= (double)this.range_list[i + 1];
               }
            }

            return this.inRange == test;
         }
      }

      public boolean isLimited() {
         return this.integersOnly && this.inRange && this.mod == 0;
      }

      public int updateRepeatLimit(int limit) {
         int mylimit = this.mod == 0?(int)this.upperBound:this.mod;
         return Math.max(mylimit, limit);
      }

      public String toString() {
         class ListBuilder {
            StringBuilder sb = new StringBuilder("[");

            ListBuilder add(String s) {
               return this.add(s, (Object)null);
            }

            ListBuilder add(String s, Object o) {
               if(this.sb.length() > 1) {
                  this.sb.append(", ");
               }

               this.sb.append(s);
               if(o != null) {
                  this.sb.append(": ").append(o.toString());
               }

               return this;
            }

            public String toString() {
               String s = this.sb.append(']').toString();
               this.sb = null;
               return s;
            }
         }

         ListBuilder lb = new ListBuilder();
         if(this.mod > 1) {
            lb.add("mod", Integer.valueOf(this.mod));
         }

         if(this.inRange) {
            lb.add("in");
         } else {
            lb.add("except");
         }

         if(this.integersOnly) {
            lb.add("ints");
         }

         if(this.lowerBound == this.upperBound) {
            lb.add(String.valueOf(this.lowerBound));
         } else {
            lb.add(this.lowerBound + "-" + this.upperBound);
         }

         if(this.range_list != null) {
            lb.add(Arrays.toString(this.range_list));
         }

         return lb.toString();
      }
   }

   private interface Rule extends Serializable {
      String getKeyword();

      boolean appliesTo(double var1);

      boolean isLimited();

      int updateRepeatLimit(int var1);
   }

   private static class RuleChain implements PluralRules.RuleList, Serializable {
      private static final long serialVersionUID = 1L;
      private final PluralRules.Rule rule;
      private final PluralRules.RuleChain next;

      public RuleChain(PluralRules.Rule rule) {
         this(rule, (PluralRules.RuleChain)null);
      }

      private RuleChain(PluralRules.Rule rule, PluralRules.RuleChain next) {
         this.rule = rule;
         this.next = next;
      }

      public PluralRules.RuleChain addRule(PluralRules.Rule nextRule) {
         return new PluralRules.RuleChain(nextRule, this);
      }

      private PluralRules.Rule selectRule(double n) {
         PluralRules.Rule r = null;
         if(this.next != null) {
            r = this.next.selectRule(n);
         }

         if(r == null && this.rule.appliesTo(n)) {
            r = this.rule;
         }

         return r;
      }

      public String select(double n) {
         PluralRules.Rule r = this.selectRule(n);
         return r == null?"other":r.getKeyword();
      }

      public Set getKeywords() {
         Set<String> result = new HashSet();
         result.add("other");

         for(PluralRules.RuleChain rc = this; rc != null; rc = rc.next) {
            result.add(rc.rule.getKeyword());
         }

         return result;
      }

      public boolean isLimited(String keyword) {
         PluralRules.RuleChain rc = this;

         boolean result;
         for(result = false; rc != null; rc = rc.next) {
            if(keyword.equals(rc.rule.getKeyword())) {
               if(!rc.rule.isLimited()) {
                  return false;
               }

               result = true;
            }
         }

         return result;
      }

      public int getRepeatLimit() {
         int result = 0;

         for(PluralRules.RuleChain rc = this; rc != null; rc = rc.next) {
            result = rc.rule.updateRepeatLimit(result);
         }

         return result;
      }

      public String toString() {
         String s = this.rule.toString();
         if(this.next != null) {
            s = this.next.toString() + "; " + s;
         }

         return s;
      }
   }

   private interface RuleList extends Serializable {
      String select(double var1);

      Set getKeywords();

      int getRepeatLimit();

      boolean isLimited(String var1);
   }
}
