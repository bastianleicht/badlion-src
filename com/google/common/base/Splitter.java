package com.google.common.base;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.base.AbstractIterator;
import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.CheckReturnValue;

@GwtCompatible(
   emulated = true
)
public final class Splitter {
   private final CharMatcher trimmer;
   private final boolean omitEmptyStrings;
   private final Splitter.Strategy strategy;
   private final int limit;

   private Splitter(Splitter.Strategy strategy) {
      this(strategy, false, CharMatcher.NONE, Integer.MAX_VALUE);
   }

   private Splitter(Splitter.Strategy strategy, boolean omitEmptyStrings, CharMatcher trimmer, int limit) {
      this.strategy = strategy;
      this.omitEmptyStrings = omitEmptyStrings;
      this.trimmer = trimmer;
      this.limit = limit;
   }

   public static Splitter on(char separator) {
      return on(CharMatcher.is(separator));
   }

   public static Splitter on(final CharMatcher separatorMatcher) {
      Preconditions.checkNotNull(separatorMatcher);
      return new Splitter(new Splitter.Strategy() {
         public Splitter.SplittingIterator iterator(final Splitter splitter, final CharSequence toSplit) {
            return new Splitter.SplittingIterator(splitter, toSplit) {
               int separatorStart(int start) {
                  return separatorMatcher.indexIn(this.toSplit, start);
               }

               int separatorEnd(int separatorPosition) {
                  return separatorPosition + 1;
               }
            };
         }
      });
   }

   public static Splitter on(final String separator) {
      Preconditions.checkArgument(separator.length() != 0, "The separator may not be the empty string.");
      return new Splitter(new Splitter.Strategy() {
         public Splitter.SplittingIterator iterator(final Splitter splitter, final CharSequence toSplit) {
            return new Splitter.SplittingIterator(splitter, toSplit) {
               public int separatorStart(int start) {
                  int separatorLength = separator.length();
                  int p = start;

                  for(int last = this.toSplit.length() - separatorLength; p <= last; ++p) {
                     int i = 0;

                     while(true) {
                        if(i >= separatorLength) {
                           return p;
                        }

                        if(this.toSplit.charAt(i + p) != separator.charAt(i)) {
                           break;
                        }

                        ++i;
                     }
                  }

                  return -1;
               }

               public int separatorEnd(int separatorPosition) {
                  return separatorPosition + separator.length();
               }
            };
         }
      });
   }

   @GwtIncompatible("java.util.regex")
   public static Splitter on(final Pattern separatorPattern) {
      Preconditions.checkNotNull(separatorPattern);
      Preconditions.checkArgument(!separatorPattern.matcher("").matches(), "The pattern may not match the empty string: %s", new Object[]{separatorPattern});
      return new Splitter(new Splitter.Strategy() {
         public Splitter.SplittingIterator iterator(final Splitter splitter, final CharSequence toSplit) {
            final Matcher matcher = separatorPattern.matcher(toSplit);
            return new Splitter.SplittingIterator(splitter, toSplit) {
               public int separatorStart(int start) {
                  return matcher.find(start)?matcher.start():-1;
               }

               public int separatorEnd(int separatorPosition) {
                  return matcher.end();
               }
            };
         }
      });
   }

   @GwtIncompatible("java.util.regex")
   public static Splitter onPattern(String separatorPattern) {
      return on(Pattern.compile(separatorPattern));
   }

   public static Splitter fixedLength(final int length) {
      Preconditions.checkArgument(length > 0, "The length may not be less than 1");
      return new Splitter(new Splitter.Strategy() {
         public Splitter.SplittingIterator iterator(final Splitter splitter, final CharSequence toSplit) {
            return new Splitter.SplittingIterator(splitter, toSplit) {
               public int separatorStart(int start) {
                  int nextChunkStart = start + length;
                  return nextChunkStart < this.toSplit.length()?nextChunkStart:-1;
               }

               public int separatorEnd(int separatorPosition) {
                  return separatorPosition;
               }
            };
         }
      });
   }

   @CheckReturnValue
   public Splitter omitEmptyStrings() {
      return new Splitter(this.strategy, true, this.trimmer, this.limit);
   }

   @CheckReturnValue
   public Splitter limit(int limit) {
      Preconditions.checkArgument(limit > 0, "must be greater than zero: %s", new Object[]{Integer.valueOf(limit)});
      return new Splitter(this.strategy, this.omitEmptyStrings, this.trimmer, limit);
   }

   @CheckReturnValue
   public Splitter trimResults() {
      return this.trimResults(CharMatcher.WHITESPACE);
   }

   @CheckReturnValue
   public Splitter trimResults(CharMatcher trimmer) {
      Preconditions.checkNotNull(trimmer);
      return new Splitter(this.strategy, this.omitEmptyStrings, trimmer, this.limit);
   }

   public Iterable split(final CharSequence sequence) {
      Preconditions.checkNotNull(sequence);
      return new Iterable() {
         public Iterator iterator() {
            return Splitter.this.splittingIterator(sequence);
         }

         public String toString() {
            return Joiner.on(", ").appendTo((StringBuilder)(new StringBuilder()).append('['), (Iterable)this).append(']').toString();
         }
      };
   }

   private Iterator splittingIterator(CharSequence sequence) {
      return this.strategy.iterator(this, sequence);
   }

   @Beta
   public List splitToList(CharSequence sequence) {
      Preconditions.checkNotNull(sequence);
      Iterator<String> iterator = this.splittingIterator(sequence);
      List<String> result = new ArrayList();

      while(iterator.hasNext()) {
         result.add(iterator.next());
      }

      return Collections.unmodifiableList(result);
   }

   @CheckReturnValue
   @Beta
   public Splitter.MapSplitter withKeyValueSeparator(String separator) {
      return this.withKeyValueSeparator(on(separator));
   }

   @CheckReturnValue
   @Beta
   public Splitter.MapSplitter withKeyValueSeparator(char separator) {
      return this.withKeyValueSeparator(on(separator));
   }

   @CheckReturnValue
   @Beta
   public Splitter.MapSplitter withKeyValueSeparator(Splitter keyValueSplitter) {
      return new Splitter.MapSplitter(this, keyValueSplitter);
   }

   @Beta
   public static final class MapSplitter {
      private static final String INVALID_ENTRY_MESSAGE = "Chunk [%s] is not a valid entry";
      private final Splitter outerSplitter;
      private final Splitter entrySplitter;

      private MapSplitter(Splitter outerSplitter, Splitter entrySplitter) {
         this.outerSplitter = outerSplitter;
         this.entrySplitter = (Splitter)Preconditions.checkNotNull(entrySplitter);
      }

      public Map split(CharSequence sequence) {
         Map<String, String> map = new LinkedHashMap();

         for(String entry : this.outerSplitter.split(sequence)) {
            Iterator<String> entryFields = this.entrySplitter.splittingIterator(entry);
            Preconditions.checkArgument(entryFields.hasNext(), "Chunk [%s] is not a valid entry", new Object[]{entry});
            String key = (String)entryFields.next();
            Preconditions.checkArgument(!map.containsKey(key), "Duplicate key [%s] found.", new Object[]{key});
            Preconditions.checkArgument(entryFields.hasNext(), "Chunk [%s] is not a valid entry", new Object[]{entry});
            String value = (String)entryFields.next();
            map.put(key, value);
            Preconditions.checkArgument(!entryFields.hasNext(), "Chunk [%s] is not a valid entry", new Object[]{entry});
         }

         return Collections.unmodifiableMap(map);
      }
   }

   private abstract static class SplittingIterator extends AbstractIterator {
      final CharSequence toSplit;
      final CharMatcher trimmer;
      final boolean omitEmptyStrings;
      int offset = 0;
      int limit;

      abstract int separatorStart(int var1);

      abstract int separatorEnd(int var1);

      protected SplittingIterator(Splitter splitter, CharSequence toSplit) {
         this.trimmer = splitter.trimmer;
         this.omitEmptyStrings = splitter.omitEmptyStrings;
         this.limit = splitter.limit;
         this.toSplit = toSplit;
      }

      protected String computeNext() {
         int nextStart = this.offset;

         while(this.offset != -1) {
            int start = nextStart;
            int separatorPosition = this.separatorStart(this.offset);
            int end;
            if(separatorPosition == -1) {
               end = this.toSplit.length();
               this.offset = -1;
            } else {
               end = separatorPosition;
               this.offset = this.separatorEnd(separatorPosition);
            }

            if(this.offset != nextStart) {
               while(start < end && this.trimmer.matches(this.toSplit.charAt(start))) {
                  ++start;
               }

               while(end > start && this.trimmer.matches(this.toSplit.charAt(end - 1))) {
                  --end;
               }

               if(!this.omitEmptyStrings || start != end) {
                  if(this.limit == 1) {
                     end = this.toSplit.length();

                     for(this.offset = -1; end > start && this.trimmer.matches(this.toSplit.charAt(end - 1)); --end) {
                        ;
                     }
                  } else {
                     --this.limit;
                  }

                  return this.toSplit.subSequence(start, end).toString();
               }

               nextStart = this.offset;
            } else {
               ++this.offset;
               if(this.offset >= this.toSplit.length()) {
                  this.offset = -1;
               }
            }
         }

         return (String)this.endOfData();
      }
   }

   private interface Strategy {
      Iterator iterator(Splitter var1, CharSequence var2);
   }
}
