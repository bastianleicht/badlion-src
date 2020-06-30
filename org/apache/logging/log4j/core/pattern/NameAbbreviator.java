package org.apache.logging.log4j.core.pattern;

import java.util.ArrayList;
import java.util.List;

public abstract class NameAbbreviator {
   private static final NameAbbreviator DEFAULT = new NameAbbreviator.NOPAbbreviator();

   public static NameAbbreviator getAbbreviator(String pattern) {
      if(pattern.length() <= 0) {
         return DEFAULT;
      } else {
         String trimmed = pattern.trim();
         if(trimmed.isEmpty()) {
            return DEFAULT;
         } else {
            int i;
            for(i = 0; i < trimmed.length() && trimmed.charAt(i) >= 48 && trimmed.charAt(i) <= 57; ++i) {
               ;
            }

            if(i == trimmed.length()) {
               return new NameAbbreviator.MaxElementAbbreviator(Integer.parseInt(trimmed));
            } else {
               ArrayList<NameAbbreviator.PatternAbbreviatorFragment> fragments = new ArrayList(5);

               for(int pos = 0; pos < trimmed.length() && pos >= 0; ++pos) {
                  int ellipsisPos = pos;
                  int charCount;
                  if(trimmed.charAt(pos) == 42) {
                     charCount = Integer.MAX_VALUE;
                     ellipsisPos = pos + 1;
                  } else if(trimmed.charAt(pos) >= 48 && trimmed.charAt(pos) <= 57) {
                     charCount = trimmed.charAt(pos) - 48;
                     ellipsisPos = pos + 1;
                  } else {
                     charCount = 0;
                  }

                  char ellipsis = 0;
                  if(ellipsisPos < trimmed.length()) {
                     ellipsis = trimmed.charAt(ellipsisPos);
                     if(ellipsis == 46) {
                        ellipsis = 0;
                     }
                  }

                  fragments.add(new NameAbbreviator.PatternAbbreviatorFragment(charCount, ellipsis));
                  pos = trimmed.indexOf(46, pos);
                  if(pos == -1) {
                     break;
                  }
               }

               return new NameAbbreviator.PatternAbbreviator(fragments);
            }
         }
      }
   }

   public static NameAbbreviator getDefaultAbbreviator() {
      return DEFAULT;
   }

   public abstract String abbreviate(String var1);

   private static class MaxElementAbbreviator extends NameAbbreviator {
      private final int count;

      public MaxElementAbbreviator(int count) {
         this.count = count < 1?1:count;
      }

      public String abbreviate(String buf) {
         int end = buf.length() - 1;

         for(int i = this.count; i > 0; --i) {
            end = buf.lastIndexOf(46, end - 1);
            if(end == -1) {
               return buf;
            }
         }

         return buf.substring(end + 1);
      }
   }

   private static class NOPAbbreviator extends NameAbbreviator {
      public String abbreviate(String buf) {
         return buf;
      }
   }

   private static class PatternAbbreviator extends NameAbbreviator {
      private final NameAbbreviator.PatternAbbreviatorFragment[] fragments;

      public PatternAbbreviator(List fragments) {
         if(fragments.size() == 0) {
            throw new IllegalArgumentException("fragments must have at least one element");
         } else {
            this.fragments = new NameAbbreviator.PatternAbbreviatorFragment[fragments.size()];
            fragments.toArray(this.fragments);
         }
      }

      public String abbreviate(String buf) {
         int pos = 0;
         StringBuilder sb = new StringBuilder(buf);

         for(int i = 0; i < this.fragments.length - 1 && pos < buf.length(); ++i) {
            pos = this.fragments[i].abbreviate(sb, pos);
         }

         for(NameAbbreviator.PatternAbbreviatorFragment terminalFragment = this.fragments[this.fragments.length - 1]; pos < buf.length() && pos >= 0; pos = terminalFragment.abbreviate(sb, pos)) {
            ;
         }

         return sb.toString();
      }
   }

   private static class PatternAbbreviatorFragment {
      private final int charCount;
      private final char ellipsis;

      public PatternAbbreviatorFragment(int charCount, char ellipsis) {
         this.charCount = charCount;
         this.ellipsis = ellipsis;
      }

      public int abbreviate(StringBuilder buf, int startPos) {
         int nextDot = buf.toString().indexOf(46, startPos);
         if(nextDot != -1) {
            if(nextDot - startPos > this.charCount) {
               buf.delete(startPos + this.charCount, nextDot);
               nextDot = startPos + this.charCount;
               if(this.ellipsis != 0) {
                  buf.insert(nextDot, this.ellipsis);
                  ++nextDot;
               }
            }

            ++nextDot;
         }

         return nextDot;
      }
   }
}
