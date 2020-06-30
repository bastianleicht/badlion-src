package com.ibm.icu.impl;

import com.ibm.icu.impl.IntTrieBuilder;
import com.ibm.icu.impl.PropsVectors;

public class PVecToTrieCompactHandler implements PropsVectors.CompactHandler {
   public IntTrieBuilder builder;
   public int initialValue;

   public void setRowIndexForErrorValue(int rowIndex) {
   }

   public void setRowIndexForInitialValue(int rowIndex) {
      this.initialValue = rowIndex;
   }

   public void setRowIndexForRange(int start, int end, int rowIndex) {
      this.builder.setRange(start, end + 1, rowIndex, true);
   }

   public void startRealValues(int rowIndex) {
      if(rowIndex > '\uffff') {
         throw new IndexOutOfBoundsException();
      } else {
         this.builder = new IntTrieBuilder((int[])null, 100000, this.initialValue, this.initialValue, false);
      }
   }
}
