package com.ibm.icu.util;

import com.ibm.icu.text.UTF16;
import com.ibm.icu.util.BytesTrie;
import java.io.IOException;
import java.util.ArrayList;
import java.util.NoSuchElementException;

public final class CharsTrie implements Cloneable, Iterable {
   private static BytesTrie.Result[] valueResults_ = new BytesTrie.Result[]{BytesTrie.Result.INTERMEDIATE_VALUE, BytesTrie.Result.FINAL_VALUE};
   static final int kMaxBranchLinearSubNodeLength = 5;
   static final int kMinLinearMatch = 48;
   static final int kMaxLinearMatchLength = 16;
   static final int kMinValueLead = 64;
   static final int kNodeTypeMask = 63;
   static final int kValueIsFinal = 32768;
   static final int kMaxOneUnitValue = 16383;
   static final int kMinTwoUnitValueLead = 16384;
   static final int kThreeUnitValueLead = 32767;
   static final int kMaxTwoUnitValue = 1073676287;
   static final int kMaxOneUnitNodeValue = 255;
   static final int kMinTwoUnitNodeValueLead = 16448;
   static final int kThreeUnitNodeValueLead = 32704;
   static final int kMaxTwoUnitNodeValue = 16646143;
   static final int kMaxOneUnitDelta = 64511;
   static final int kMinTwoUnitDeltaLead = 64512;
   static final int kThreeUnitDeltaLead = 65535;
   static final int kMaxTwoUnitDelta = 67043327;
   private CharSequence chars_;
   private int root_;
   private int pos_;
   private int remainingMatchLength_;

   public CharsTrie(CharSequence trieChars, int offset) {
      this.chars_ = trieChars;
      this.pos_ = this.root_ = offset;
      this.remainingMatchLength_ = -1;
   }

   public Object clone() throws CloneNotSupportedException {
      return super.clone();
   }

   public CharsTrie reset() {
      this.pos_ = this.root_;
      this.remainingMatchLength_ = -1;
      return this;
   }

   public CharsTrie saveState(CharsTrie.State state) {
      state.chars = this.chars_;
      state.root = this.root_;
      state.pos = this.pos_;
      state.remainingMatchLength = this.remainingMatchLength_;
      return this;
   }

   public CharsTrie resetToState(CharsTrie.State state) {
      if(this.chars_ == state.chars && this.chars_ != null && this.root_ == state.root) {
         this.pos_ = state.pos;
         this.remainingMatchLength_ = state.remainingMatchLength;
         return this;
      } else {
         throw new IllegalArgumentException("incompatible trie state");
      }
   }

   public BytesTrie.Result current() {
      int pos = this.pos_;
      int node;
      return pos < 0?BytesTrie.Result.NO_MATCH:(this.remainingMatchLength_ < 0 && (node = this.chars_.charAt(pos)) >= 64?valueResults_[node >> 15]:BytesTrie.Result.NO_VALUE);
   }

   public BytesTrie.Result first(int inUnit) {
      this.remainingMatchLength_ = -1;
      return this.nextImpl(this.root_, inUnit);
   }

   public BytesTrie.Result firstForCodePoint(int cp) {
      return cp <= '\uffff'?this.first(cp):(this.first(UTF16.getLeadSurrogate(cp)).hasNext()?this.next(UTF16.getTrailSurrogate(cp)):BytesTrie.Result.NO_MATCH);
   }

   public BytesTrie.Result next(int inUnit) {
      int pos = this.pos_;
      if(pos < 0) {
         return BytesTrie.Result.NO_MATCH;
      } else {
         int length = this.remainingMatchLength_;
         if(length >= 0) {
            if(inUnit != this.chars_.charAt(pos++)) {
               this.stop();
               return BytesTrie.Result.NO_MATCH;
            } else {
               --length;
               this.remainingMatchLength_ = length;
               this.pos_ = pos;
               int node;
               return length < 0 && (node = this.chars_.charAt(pos)) >= 64?valueResults_[node >> 15]:BytesTrie.Result.NO_VALUE;
            }
         } else {
            return this.nextImpl(pos, inUnit);
         }
      }
   }

   public BytesTrie.Result nextForCodePoint(int cp) {
      return cp <= '\uffff'?this.next(cp):(this.next(UTF16.getLeadSurrogate(cp)).hasNext()?this.next(UTF16.getTrailSurrogate(cp)):BytesTrie.Result.NO_MATCH);
   }

   public BytesTrie.Result next(CharSequence s, int sIndex, int sLimit) {
      if(sIndex >= sLimit) {
         return this.current();
      } else {
         int pos = this.pos_;
         if(pos < 0) {
            return BytesTrie.Result.NO_MATCH;
         } else {
            int length = this.remainingMatchLength_;

            label88:
            while(sIndex != sLimit) {
               char inUnit = s.charAt(sIndex++);
               if(length < 0) {
                  this.remainingMatchLength_ = length;
                  int node = this.chars_.charAt(pos++);

                  while(true) {
                     while(node >= 48) {
                        if(node < 64) {
                           length = node - 48;
                           if(inUnit != this.chars_.charAt(pos)) {
                              this.stop();
                              return BytesTrie.Result.NO_MATCH;
                           }

                           ++pos;
                           --length;
                           continue label88;
                        }

                        if((node & '耀') != 0) {
                           this.stop();
                           return BytesTrie.Result.NO_MATCH;
                        }

                        pos = skipNodeValue(pos, node);
                        node &= 63;
                     }

                     BytesTrie.Result result = this.branchNext(pos, node, inUnit);
                     if(result == BytesTrie.Result.NO_MATCH) {
                        return BytesTrie.Result.NO_MATCH;
                     }

                     if(sIndex == sLimit) {
                        return result;
                     }

                     if(result == BytesTrie.Result.FINAL_VALUE) {
                        this.stop();
                        return BytesTrie.Result.NO_MATCH;
                     }

                     inUnit = s.charAt(sIndex++);
                     pos = this.pos_;
                     node = this.chars_.charAt(pos++);
                  }
               } else {
                  if(inUnit != this.chars_.charAt(pos)) {
                     this.stop();
                     return BytesTrie.Result.NO_MATCH;
                  }

                  ++pos;
                  --length;
               }
            }

            this.remainingMatchLength_ = length;
            this.pos_ = pos;
            int node;
            return length < 0 && (node = this.chars_.charAt(pos)) >= 64?valueResults_[node >> 15]:BytesTrie.Result.NO_VALUE;
         }
      }
   }

   public int getValue() {
      int pos = this.pos_;
      int leadUnit = this.chars_.charAt(pos++);

      assert leadUnit >= 64;

      return (leadUnit & '耀') != 0?readValue(this.chars_, pos, leadUnit & 32767):readNodeValue(this.chars_, pos, leadUnit);
   }

   public long getUniqueValue() {
      int pos = this.pos_;
      if(pos < 0) {
         return 0L;
      } else {
         long uniqueValue = findUniqueValue(this.chars_, pos + this.remainingMatchLength_ + 1, 0L);
         return uniqueValue << 31 >> 31;
      }
   }

   public int getNextChars(Appendable out) {
      int pos = this.pos_;
      if(pos < 0) {
         return 0;
      } else if(this.remainingMatchLength_ >= 0) {
         append(out, this.chars_.charAt(pos));
         return 1;
      } else {
         int node = this.chars_.charAt(pos++);
         if(node >= 64) {
            if((node & '耀') != 0) {
               return 0;
            }

            pos = skipNodeValue(pos, node);
            node &= 63;
         }

         if(node < 48) {
            if(node == 0) {
               node = this.chars_.charAt(pos++);
            }

            ++node;
            getNextBranchChars(this.chars_, pos, node, out);
            return node;
         } else {
            append(out, this.chars_.charAt(pos));
            return 1;
         }
      }
   }

   public CharsTrie.Iterator iterator() {
      return new CharsTrie.Iterator(this.chars_, this.pos_, this.remainingMatchLength_, 0);
   }

   public CharsTrie.Iterator iterator(int maxStringLength) {
      return new CharsTrie.Iterator(this.chars_, this.pos_, this.remainingMatchLength_, maxStringLength);
   }

   public static CharsTrie.Iterator iterator(CharSequence trieChars, int offset, int maxStringLength) {
      return new CharsTrie.Iterator(trieChars, offset, -1, maxStringLength);
   }

   private void stop() {
      this.pos_ = -1;
   }

   private static int readValue(CharSequence chars, int pos, int leadUnit) {
      int value;
      if(leadUnit < 16384) {
         value = leadUnit;
      } else if(leadUnit < 32767) {
         value = leadUnit - 16384 << 16 | chars.charAt(pos);
      } else {
         value = chars.charAt(pos) << 16 | chars.charAt(pos + 1);
      }

      return value;
   }

   private static int skipValue(int pos, int leadUnit) {
      if(leadUnit >= 16384) {
         if(leadUnit < 32767) {
            ++pos;
         } else {
            pos += 2;
         }
      }

      return pos;
   }

   private static int skipValue(CharSequence chars, int pos) {
      int leadUnit = chars.charAt(pos++);
      return skipValue(pos, leadUnit & 32767);
   }

   private static int readNodeValue(CharSequence chars, int pos, int leadUnit) {
      if($assertionsDisabled || 64 <= leadUnit && leadUnit < '耀') {
         int value;
         if(leadUnit < 16448) {
            value = (leadUnit >> 6) - 1;
         } else if(leadUnit < 32704) {
            value = (leadUnit & 32704) - 16448 << 10 | chars.charAt(pos);
         } else {
            value = chars.charAt(pos) << 16 | chars.charAt(pos + 1);
         }

         return value;
      } else {
         throw new AssertionError();
      }
   }

   private static int skipNodeValue(int pos, int leadUnit) {
      if($assertionsDisabled || 64 <= leadUnit && leadUnit < '耀') {
         if(leadUnit >= 16448) {
            if(leadUnit < 32704) {
               ++pos;
            } else {
               pos += 2;
            }
         }

         return pos;
      } else {
         throw new AssertionError();
      }
   }

   private static int jumpByDelta(CharSequence chars, int pos) {
      int delta = chars.charAt(pos++);
      if(delta >= 'ﰀ') {
         if(delta == '\uffff') {
            delta = chars.charAt(pos) << 16 | chars.charAt(pos + 1);
            pos += 2;
         } else {
            delta = delta - 'ﰀ' << 16 | chars.charAt(pos++);
         }
      }

      return pos + delta;
   }

   private static int skipDelta(CharSequence chars, int pos) {
      int delta = chars.charAt(pos++);
      if(delta >= 'ﰀ') {
         if(delta == '\uffff') {
            pos += 2;
         } else {
            ++pos;
         }
      }

      return pos;
   }

   private BytesTrie.Result branchNext(int pos, int length, int inUnit) {
      if(length == 0) {
         length = this.chars_.charAt(pos++);
      }

      ++length;

      while(length > 5) {
         if(inUnit < this.chars_.charAt(pos++)) {
            length >>= 1;
            pos = jumpByDelta(this.chars_, pos);
         } else {
            length -= length >> 1;
            pos = skipDelta(this.chars_, pos);
         }
      }

      while(inUnit != this.chars_.charAt(pos++)) {
         --length;
         pos = skipValue(this.chars_, pos);
         if(length <= 1) {
            if(inUnit == this.chars_.charAt(pos++)) {
               this.pos_ = pos;
               int node = this.chars_.charAt(pos);
               return node >= 64?valueResults_[node >> 15]:BytesTrie.Result.NO_VALUE;
            }

            this.stop();
            return BytesTrie.Result.NO_MATCH;
         }
      }

      int node = this.chars_.charAt(pos);
      BytesTrie.Result result;
      if((node & '耀') != 0) {
         result = BytesTrie.Result.FINAL_VALUE;
      } else {
         ++pos;
         int delta;
         if(node < 16384) {
            delta = node;
         } else if(node < 32767) {
            delta = node - 16384 << 16 | this.chars_.charAt(pos++);
         } else {
            delta = this.chars_.charAt(pos) << 16 | this.chars_.charAt(pos + 1);
            pos += 2;
         }

         pos += delta;
         node = this.chars_.charAt(pos);
         result = node >= 64?valueResults_[node >> 15]:BytesTrie.Result.NO_VALUE;
      }

      this.pos_ = pos;
      return result;
   }

   private BytesTrie.Result nextImpl(int pos, int inUnit) {
      int node = this.chars_.charAt(pos++);

      while(true) {
         if(node < 48) {
            return this.branchNext(pos, node, inUnit);
         }

         if(node < 64) {
            int length = node - 48;
            if(inUnit == this.chars_.charAt(pos++)) {
               --length;
               this.remainingMatchLength_ = length;
               this.pos_ = pos;
               return length < 0 && (node = this.chars_.charAt(pos)) >= 64?valueResults_[node >> 15]:BytesTrie.Result.NO_VALUE;
            }
            break;
         }

         if((node & '耀') != 0) {
            break;
         }

         pos = skipNodeValue(pos, node);
         node &= 63;
      }

      this.stop();
      return BytesTrie.Result.NO_MATCH;
   }

   private static long findUniqueValueFromBranch(CharSequence chars, int pos, int length, long uniqueValue) {
      while(length > 5) {
         ++pos;
         uniqueValue = findUniqueValueFromBranch(chars, jumpByDelta(chars, pos), length >> 1, uniqueValue);
         if(uniqueValue == 0L) {
            return 0L;
         }

         length -= length >> 1;
         pos = skipDelta(chars, pos);
      }

      while(true) {
         ++pos;
         int node = chars.charAt(pos++);
         boolean isFinal = (node & '耀') != 0;
         node = node & 32767;
         int value = readValue(chars, pos, node);
         pos = skipValue(pos, node);
         if(isFinal) {
            if(uniqueValue != 0L) {
               if(value != (int)(uniqueValue >> 1)) {
                  return 0L;
               }
            } else {
               uniqueValue = (long)value << 1 | 1L;
            }
         } else {
            uniqueValue = findUniqueValue(chars, pos + value, uniqueValue);
            if(uniqueValue == 0L) {
               return 0L;
            }
         }

         --length;
         if(length <= 1) {
            break;
         }
      }

      return (long)(pos + 1) << 33 | uniqueValue & 8589934591L;
   }

   private static long findUniqueValue(CharSequence param0, int param1, long param2) {
      // $FF: Couldn't be decompiled
   }

   private static void getNextBranchChars(CharSequence chars, int pos, int length, Appendable out) {
      while(length > 5) {
         ++pos;
         getNextBranchChars(chars, jumpByDelta(chars, pos), length >> 1, out);
         length -= length >> 1;
         pos = skipDelta(chars, pos);
      }

      while(true) {
         append(out, chars.charAt(pos++));
         pos = skipValue(chars, pos);
         --length;
         if(length <= 1) {
            break;
         }
      }

      append(out, chars.charAt(pos));
   }

   private static void append(Appendable out, int c) {
      try {
         out.append((char)c);
      } catch (IOException var3) {
         throw new RuntimeException(var3);
      }
   }

   public static final class Entry {
      public CharSequence chars;
      public int value;

      private Entry() {
      }
   }

   public static final class Iterator implements java.util.Iterator {
      private CharSequence chars_;
      private int pos_;
      private int initialPos_;
      private int remainingMatchLength_;
      private int initialRemainingMatchLength_;
      private boolean skipValue_;
      private StringBuilder str_;
      private int maxLength_;
      private CharsTrie.Entry entry_;
      private ArrayList stack_;

      private Iterator(CharSequence trieChars, int offset, int remainingMatchLength, int maxStringLength) {
         this.str_ = new StringBuilder();
         this.entry_ = new CharsTrie.Entry();
         this.stack_ = new ArrayList();
         this.chars_ = trieChars;
         this.pos_ = this.initialPos_ = offset;
         this.remainingMatchLength_ = this.initialRemainingMatchLength_ = remainingMatchLength;
         this.maxLength_ = maxStringLength;
         int length = this.remainingMatchLength_;
         if(length >= 0) {
            ++length;
            if(this.maxLength_ > 0 && length > this.maxLength_) {
               length = this.maxLength_;
            }

            this.str_.append(this.chars_, this.pos_, this.pos_ + length);
            this.pos_ += length;
            this.remainingMatchLength_ -= length;
         }

      }

      public CharsTrie.Iterator reset() {
         this.pos_ = this.initialPos_;
         this.remainingMatchLength_ = this.initialRemainingMatchLength_;
         this.skipValue_ = false;
         int length = this.remainingMatchLength_ + 1;
         if(this.maxLength_ > 0 && length > this.maxLength_) {
            length = this.maxLength_;
         }

         this.str_.setLength(length);
         this.pos_ += length;
         this.remainingMatchLength_ -= length;
         this.stack_.clear();
         return this;
      }

      public boolean hasNext() {
         return this.pos_ >= 0 || !this.stack_.isEmpty();
      }

      public CharsTrie.Entry next() {
         int pos = this.pos_;
         if(pos < 0) {
            if(this.stack_.isEmpty()) {
               throw new NoSuchElementException();
            }

            long top = ((Long)this.stack_.remove(this.stack_.size() - 1)).longValue();
            int length = (int)top;
            pos = (int)(top >> 32);
            this.str_.setLength(length & '\uffff');
            length = length >>> 16;
            if(length > 1) {
               pos = this.branchNext(pos, length);
               if(pos < 0) {
                  return this.entry_;
               }
            } else {
               this.str_.append(this.chars_.charAt(pos++));
            }
         }

         if(this.remainingMatchLength_ >= 0) {
            return this.truncateAndStop();
         } else {
            while(true) {
               int node = this.chars_.charAt(pos++);
               if(node >= 64) {
                  if(!this.skipValue_) {
                     boolean isFinal = (node & '耀') != 0;
                     if(isFinal) {
                        this.entry_.value = CharsTrie.readValue(this.chars_, pos, node & 32767);
                     } else {
                        this.entry_.value = CharsTrie.readNodeValue(this.chars_, pos, node);
                     }

                     if(!isFinal && (this.maxLength_ <= 0 || this.str_.length() != this.maxLength_)) {
                        this.pos_ = pos - 1;
                        this.skipValue_ = true;
                     } else {
                        this.pos_ = -1;
                     }

                     this.entry_.chars = this.str_;
                     return this.entry_;
                  }

                  pos = CharsTrie.skipNodeValue(pos, node);
                  node &= 63;
                  this.skipValue_ = false;
               }

               if(this.maxLength_ > 0 && this.str_.length() == this.maxLength_) {
                  return this.truncateAndStop();
               }

               if(node < 48) {
                  if(node == 0) {
                     node = this.chars_.charAt(pos++);
                  }

                  pos = this.branchNext(pos, node + 1);
                  if(pos < 0) {
                     return this.entry_;
                  }
               } else {
                  int length = node - 48 + 1;
                  if(this.maxLength_ > 0 && this.str_.length() + length > this.maxLength_) {
                     this.str_.append(this.chars_, pos, pos + this.maxLength_ - this.str_.length());
                     return this.truncateAndStop();
                  }

                  this.str_.append(this.chars_, pos, pos + length);
                  pos += length;
               }
            }
         }
      }

      public void remove() {
         throw new UnsupportedOperationException();
      }

      private CharsTrie.Entry truncateAndStop() {
         this.pos_ = -1;
         this.entry_.chars = this.str_;
         this.entry_.value = -1;
         return this.entry_;
      }

      private int branchNext(int pos, int length) {
         while(length > 5) {
            ++pos;
            this.stack_.add(Long.valueOf((long)CharsTrie.skipDelta(this.chars_, pos) << 32 | (long)(length - (length >> 1) << 16) | (long)this.str_.length()));
            length >>= 1;
            pos = CharsTrie.jumpByDelta(this.chars_, pos);
         }

         char trieUnit = this.chars_.charAt(pos++);
         int node = this.chars_.charAt(pos++);
         boolean isFinal = (node & '耀') != 0;
         int value = CharsTrie.readValue(this.chars_, pos, node = node & 32767);
         pos = CharsTrie.skipValue(pos, node);
         this.stack_.add(Long.valueOf((long)pos << 32 | (long)(length - 1 << 16) | (long)this.str_.length()));
         this.str_.append(trieUnit);
         if(isFinal) {
            this.pos_ = -1;
            this.entry_.chars = this.str_;
            this.entry_.value = value;
            return -1;
         } else {
            return pos + value;
         }
      }
   }

   public static final class State {
      private CharSequence chars;
      private int root;
      private int pos;
      private int remainingMatchLength;
   }
}
