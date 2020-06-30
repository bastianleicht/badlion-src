package com.ibm.icu.text;

import com.ibm.icu.text.BreakIterator;
import java.text.CharacterIterator;

public abstract class SearchIterator {
   public static final int DONE = -1;
   protected BreakIterator breakIterator;
   protected CharacterIterator targetText;
   protected int matchLength;
   private boolean m_isForwardSearching_;
   private boolean m_isOverlap_;
   private boolean m_reset_;
   private int m_setOffset_;
   private int m_lastMatchStart_;

   public void setIndex(int position) {
      if(position >= this.targetText.getBeginIndex() && position <= this.targetText.getEndIndex()) {
         this.m_setOffset_ = position;
         this.m_reset_ = false;
         this.matchLength = 0;
      } else {
         throw new IndexOutOfBoundsException("setIndex(int) expected position to be between " + this.targetText.getBeginIndex() + " and " + this.targetText.getEndIndex());
      }
   }

   public void setOverlapping(boolean allowOverlap) {
      this.m_isOverlap_ = allowOverlap;
   }

   public void setBreakIterator(BreakIterator breakiter) {
      this.breakIterator = breakiter;
      if(this.breakIterator != null) {
         this.breakIterator.setText(this.targetText);
      }

   }

   public void setTarget(CharacterIterator text) {
      if(text != null && text.getEndIndex() != text.getIndex()) {
         this.targetText = text;
         this.targetText.setIndex(this.targetText.getBeginIndex());
         this.matchLength = 0;
         this.m_reset_ = true;
         this.m_isForwardSearching_ = true;
         if(this.breakIterator != null) {
            this.breakIterator.setText(this.targetText);
         }

      } else {
         throw new IllegalArgumentException("Illegal null or empty text");
      }
   }

   public int getMatchStart() {
      return this.m_lastMatchStart_;
   }

   public abstract int getIndex();

   public int getMatchLength() {
      return this.matchLength;
   }

   public BreakIterator getBreakIterator() {
      return this.breakIterator;
   }

   public CharacterIterator getTarget() {
      return this.targetText;
   }

   public String getMatchedText() {
      if(this.matchLength <= 0) {
         return null;
      } else {
         int limit = this.m_lastMatchStart_ + this.matchLength;
         StringBuilder result = new StringBuilder(this.matchLength);
         result.append(this.targetText.current());
         this.targetText.next();

         while(this.targetText.getIndex() < limit) {
            result.append(this.targetText.current());
            this.targetText.next();
         }

         this.targetText.setIndex(this.m_lastMatchStart_);
         return result.toString();
      }
   }

   public int next() {
      int start = this.targetText.getIndex();
      if(this.m_setOffset_ != -1) {
         start = this.m_setOffset_;
         this.m_setOffset_ = -1;
      }

      if(this.m_isForwardSearching_) {
         if(!this.m_reset_ && start + this.matchLength >= this.targetText.getEndIndex()) {
            this.matchLength = 0;
            this.targetText.setIndex(this.targetText.getEndIndex());
            this.m_lastMatchStart_ = -1;
            return -1;
         }

         this.m_reset_ = false;
      } else {
         this.m_isForwardSearching_ = true;
         if(start != -1) {
            return start;
         }
      }

      if(start == -1) {
         start = this.targetText.getBeginIndex();
      }

      if(this.matchLength > 0) {
         if(this.m_isOverlap_) {
            ++start;
         } else {
            start += this.matchLength;
         }
      }

      this.m_lastMatchStart_ = this.handleNext(start);
      return this.m_lastMatchStart_;
   }

   public int previous() {
      int start = this.targetText.getIndex();
      if(this.m_setOffset_ != -1) {
         start = this.m_setOffset_;
         this.m_setOffset_ = -1;
      }

      if(this.m_reset_) {
         this.m_isForwardSearching_ = false;
         this.m_reset_ = false;
         start = this.targetText.getEndIndex();
      }

      if(this.m_isForwardSearching_) {
         this.m_isForwardSearching_ = false;
         if(start != this.targetText.getEndIndex()) {
            return start;
         }
      } else if(start == this.targetText.getBeginIndex()) {
         this.matchLength = 0;
         this.targetText.setIndex(this.targetText.getBeginIndex());
         this.m_lastMatchStart_ = -1;
         return -1;
      }

      this.m_lastMatchStart_ = this.handlePrevious(start);
      return this.m_lastMatchStart_;
   }

   public boolean isOverlapping() {
      return this.m_isOverlap_;
   }

   public void reset() {
      this.matchLength = 0;
      this.setIndex(this.targetText.getBeginIndex());
      this.m_isOverlap_ = false;
      this.m_isForwardSearching_ = true;
      this.m_reset_ = true;
      this.m_setOffset_ = -1;
   }

   public final int first() {
      this.m_isForwardSearching_ = true;
      this.setIndex(this.targetText.getBeginIndex());
      return this.next();
   }

   public final int following(int position) {
      this.m_isForwardSearching_ = true;
      this.setIndex(position);
      return this.next();
   }

   public final int last() {
      this.m_isForwardSearching_ = false;
      this.setIndex(this.targetText.getEndIndex());
      return this.previous();
   }

   public final int preceding(int position) {
      this.m_isForwardSearching_ = false;
      this.setIndex(position);
      return this.previous();
   }

   protected SearchIterator(CharacterIterator target, BreakIterator breaker) {
      if(target != null && target.getEndIndex() - target.getBeginIndex() != 0) {
         this.targetText = target;
         this.breakIterator = breaker;
         if(this.breakIterator != null) {
            this.breakIterator.setText(target);
         }

         this.matchLength = 0;
         this.m_lastMatchStart_ = -1;
         this.m_isOverlap_ = false;
         this.m_isForwardSearching_ = true;
         this.m_reset_ = true;
         this.m_setOffset_ = -1;
      } else {
         throw new IllegalArgumentException("Illegal argument target.  Argument can not be null or of length 0");
      }
   }

   protected void setMatchLength(int length) {
      this.matchLength = length;
   }

   protected abstract int handleNext(int var1);

   protected abstract int handlePrevious(int var1);
}
