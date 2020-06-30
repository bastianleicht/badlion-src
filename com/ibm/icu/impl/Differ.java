package com.ibm.icu.impl;

public final class Differ {
   private int STACKSIZE;
   private int EQUALSIZE;
   private Object[] a;
   private Object[] b;
   private Object last = null;
   private Object next = null;
   private int aCount = 0;
   private int bCount = 0;
   private int aLine = 1;
   private int bLine = 1;
   private int maxSame = 0;
   private int aTop = 0;
   private int bTop = 0;

   public Differ(int stackSize, int matchCount) {
      this.STACKSIZE = stackSize;
      this.EQUALSIZE = matchCount;
      this.a = (Object[])(new Object[stackSize + matchCount]);
      this.b = (Object[])(new Object[stackSize + matchCount]);
   }

   public void add(Object aStr, Object bStr) {
      this.addA(aStr);
      this.addB(bStr);
   }

   public void addA(Object aStr) {
      this.flush();
      this.a[this.aCount++] = aStr;
   }

   public void addB(Object bStr) {
      this.flush();
      this.b[this.bCount++] = bStr;
   }

   public int getALine(int offset) {
      return this.aLine + this.maxSame + offset;
   }

   public Object getA(int offset) {
      return offset < 0?this.last:(offset > this.aTop - this.maxSame?this.next:this.a[offset]);
   }

   public int getACount() {
      return this.aTop - this.maxSame;
   }

   public int getBCount() {
      return this.bTop - this.maxSame;
   }

   public int getBLine(int offset) {
      return this.bLine + this.maxSame + offset;
   }

   public Object getB(int offset) {
      return offset < 0?this.last:(offset > this.bTop - this.maxSame?this.next:this.b[offset]);
   }

   public void checkMatch(boolean finalPass) {
      int max = this.aCount;
      if(max > this.bCount) {
         max = this.bCount;
      }

      int i;
      for(i = 0; i < max && this.a[i].equals(this.b[i]); ++i) {
         ;
      }

      this.maxSame = i;
      this.aTop = this.bTop = this.maxSame;
      if(this.maxSame > 0) {
         this.last = this.a[this.maxSame - 1];
      }

      this.next = null;
      if(finalPass) {
         this.aTop = this.aCount;
         this.bTop = this.bCount;
         this.next = null;
      } else if(this.aCount - this.maxSame >= this.EQUALSIZE && this.bCount - this.maxSame >= this.EQUALSIZE) {
         int match = this.find(this.a, this.aCount - this.EQUALSIZE, this.aCount, this.b, this.maxSame, this.bCount);
         if(match != -1) {
            this.aTop = this.aCount - this.EQUALSIZE;
            this.bTop = match;
            this.next = this.a[this.aTop];
         } else {
            match = this.find(this.b, this.bCount - this.EQUALSIZE, this.bCount, this.a, this.maxSame, this.aCount);
            if(match != -1) {
               this.bTop = this.bCount - this.EQUALSIZE;
               this.aTop = match;
               this.next = this.b[this.bTop];
            } else {
               if(this.aCount >= this.STACKSIZE || this.bCount >= this.STACKSIZE) {
                  this.aCount = (this.aCount + this.maxSame) / 2;
                  this.bCount = (this.bCount + this.maxSame) / 2;
                  this.next = null;
               }

            }
         }
      }
   }

   public int find(Object[] aArr, int aStart, int aEnd, Object[] bArr, int bStart, int bEnd) {
      int len = aEnd - aStart;
      int bEndMinus = bEnd - len;

      for(int i = bStart; i <= bEndMinus; ++i) {
         int j = 0;

         while(true) {
            if(j >= len) {
               return i;
            }

            if(!bArr[i + j].equals(aArr[aStart + j])) {
               break;
            }

            ++j;
         }
      }

      return -1;
   }

   private void flush() {
      if(this.aTop != 0) {
         int newCount = this.aCount - this.aTop;
         System.arraycopy(this.a, this.aTop, this.a, 0, newCount);
         this.aCount = newCount;
         this.aLine += this.aTop;
         this.aTop = 0;
      }

      if(this.bTop != 0) {
         int newCount = this.bCount - this.bTop;
         System.arraycopy(this.b, this.bTop, this.b, 0, newCount);
         this.bCount = newCount;
         this.bLine += this.bTop;
         this.bTop = 0;
      }

   }
}
