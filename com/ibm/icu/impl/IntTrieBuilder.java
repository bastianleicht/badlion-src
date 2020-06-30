package com.ibm.icu.impl;

import com.ibm.icu.impl.IntTrie;
import com.ibm.icu.impl.Trie;
import com.ibm.icu.impl.TrieBuilder;
import com.ibm.icu.text.UTF16;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

public class IntTrieBuilder extends TrieBuilder {
   protected int[] m_data_;
   protected int m_initialValue_;
   private int m_leadUnitValue_;

   public IntTrieBuilder(IntTrieBuilder table) {
      super(table);
      this.m_data_ = new int[this.m_dataCapacity_];
      System.arraycopy(table.m_data_, 0, this.m_data_, 0, this.m_dataLength_);
      this.m_initialValue_ = table.m_initialValue_;
      this.m_leadUnitValue_ = table.m_leadUnitValue_;
   }

   public IntTrieBuilder(int[] aliasdata, int maxdatalength, int initialvalue, int leadunitvalue, boolean latin1linear) {
      if(maxdatalength >= 32 && (!latin1linear || maxdatalength >= 1024)) {
         if(aliasdata != null) {
            this.m_data_ = aliasdata;
         } else {
            this.m_data_ = new int[maxdatalength];
         }

         int j = 32;
         if(latin1linear) {
            int i = 0;

            while(true) {
               this.m_index_[i++] = j;
               j += 32;
               if(i >= 8) {
                  break;
               }
            }
         }

         this.m_dataLength_ = j;
         Arrays.fill(this.m_data_, 0, this.m_dataLength_, initialvalue);
         this.m_initialValue_ = initialvalue;
         this.m_leadUnitValue_ = leadunitvalue;
         this.m_dataCapacity_ = maxdatalength;
         this.m_isLatin1Linear_ = latin1linear;
         this.m_isCompacted_ = false;
      } else {
         throw new IllegalArgumentException("Argument maxdatalength is too small");
      }
   }

   public int getValue(int ch) {
      if(!this.m_isCompacted_ && ch <= 1114111 && ch >= 0) {
         int block = this.m_index_[ch >> 5];
         return this.m_data_[Math.abs(block) + (ch & 31)];
      } else {
         return 0;
      }
   }

   public int getValue(int ch, boolean[] inBlockZero) {
      if(!this.m_isCompacted_ && ch <= 1114111 && ch >= 0) {
         int block = this.m_index_[ch >> 5];
         if(inBlockZero != null) {
            inBlockZero[0] = block == 0;
         }

         return this.m_data_[Math.abs(block) + (ch & 31)];
      } else {
         if(inBlockZero != null) {
            inBlockZero[0] = true;
         }

         return 0;
      }
   }

   public boolean setValue(int ch, int value) {
      if(!this.m_isCompacted_ && ch <= 1114111 && ch >= 0) {
         int block = this.getDataBlock(ch);
         if(block < 0) {
            return false;
         } else {
            this.m_data_[block + (ch & 31)] = value;
            return true;
         }
      } else {
         return false;
      }
   }

   public IntTrie serialize(TrieBuilder.DataManipulate datamanipulate, Trie.DataManipulate triedatamanipulate) {
      if(datamanipulate == null) {
         throw new IllegalArgumentException("Parameters can not be null");
      } else {
         if(!this.m_isCompacted_) {
            this.compact(false);
            this.fold(datamanipulate);
            this.compact(true);
            this.m_isCompacted_ = true;
         }

         if(this.m_dataLength_ >= 262144) {
            throw new ArrayIndexOutOfBoundsException("Data length too small");
         } else {
            char[] index = new char[this.m_indexLength_];
            int[] data = new int[this.m_dataLength_];

            for(int i = 0; i < this.m_indexLength_; ++i) {
               index[i] = (char)(this.m_index_[i] >>> 2);
            }

            System.arraycopy(this.m_data_, 0, data, 0, this.m_dataLength_);
            int options = 37;
            options = options | 256;
            if(this.m_isLatin1Linear_) {
               options |= 512;
            }

            return new IntTrie(index, data, this.m_initialValue_, options, triedatamanipulate);
         }
      }
   }

   public int serialize(OutputStream os, boolean reduceTo16Bits, TrieBuilder.DataManipulate datamanipulate) throws IOException {
      if(datamanipulate == null) {
         throw new IllegalArgumentException("Parameters can not be null");
      } else {
         if(!this.m_isCompacted_) {
            this.compact(false);
            this.fold(datamanipulate);
            this.compact(true);
            this.m_isCompacted_ = true;
         }

         int length;
         if(reduceTo16Bits) {
            length = this.m_dataLength_ + this.m_indexLength_;
         } else {
            length = this.m_dataLength_;
         }

         if(length >= 262144) {
            throw new ArrayIndexOutOfBoundsException("Data length too small");
         } else {
            length = 16 + 2 * this.m_indexLength_;
            if(reduceTo16Bits) {
               length = length + 2 * this.m_dataLength_;
            } else {
               length = length + 4 * this.m_dataLength_;
            }

            if(os == null) {
               return length;
            } else {
               DataOutputStream dos = new DataOutputStream(os);
               dos.writeInt(1416784229);
               int options = 37;
               if(!reduceTo16Bits) {
                  options |= 256;
               }

               if(this.m_isLatin1Linear_) {
                  options |= 512;
               }

               dos.writeInt(options);
               dos.writeInt(this.m_indexLength_);
               dos.writeInt(this.m_dataLength_);
               if(reduceTo16Bits) {
                  for(int i = 0; i < this.m_indexLength_; ++i) {
                     int v = this.m_index_[i] + this.m_indexLength_ >>> 2;
                     dos.writeChar(v);
                  }

                  for(int i = 0; i < this.m_dataLength_; ++i) {
                     int v = this.m_data_[i] & '\uffff';
                     dos.writeChar(v);
                  }
               } else {
                  for(int i = 0; i < this.m_indexLength_; ++i) {
                     int v = this.m_index_[i] >>> 2;
                     dos.writeChar(v);
                  }

                  for(int i = 0; i < this.m_dataLength_; ++i) {
                     dos.writeInt(this.m_data_[i]);
                  }
               }

               return length;
            }
         }
      }
   }

   public boolean setRange(int start, int limit, int value, boolean overwrite) {
      if(!this.m_isCompacted_ && start >= 0 && start <= 1114111 && limit >= 0 && limit <= 1114112 && start <= limit) {
         if(start == limit) {
            return true;
         } else {
            if((start & 31) != 0) {
               int block = this.getDataBlock(start);
               if(block < 0) {
                  return false;
               }

               int nextStart = start + 32 & -32;
               if(nextStart > limit) {
                  this.fillBlock(block, start & 31, limit & 31, value, overwrite);
                  return true;
               }

               this.fillBlock(block, start & 31, 32, value, overwrite);
               start = nextStart;
            }

            int rest = limit & 31;
            limit = limit & -32;
            int repeatBlock = 0;
            if(value != this.m_initialValue_) {
               repeatBlock = -1;
            }

            for(; start < limit; start += 32) {
               int block = this.m_index_[start >> 5];
               if(block > 0) {
                  this.fillBlock(block, 0, 32, value, overwrite);
               } else if(this.m_data_[-block] != value && (block == 0 || overwrite)) {
                  if(repeatBlock >= 0) {
                     this.m_index_[start >> 5] = -repeatBlock;
                  } else {
                     repeatBlock = this.getDataBlock(start);
                     if(repeatBlock < 0) {
                        return false;
                     }

                     this.m_index_[start >> 5] = -repeatBlock;
                     this.fillBlock(repeatBlock, 0, 32, value, true);
                  }
               }
            }

            if(rest > 0) {
               int block = this.getDataBlock(start);
               if(block < 0) {
                  return false;
               }

               this.fillBlock(block, 0, rest, value, overwrite);
            }

            return true;
         }
      } else {
         return false;
      }
   }

   private int allocDataBlock() {
      int newBlock = this.m_dataLength_;
      int newTop = newBlock + 32;
      if(newTop > this.m_dataCapacity_) {
         return -1;
      } else {
         this.m_dataLength_ = newTop;
         return newBlock;
      }
   }

   private int getDataBlock(int ch) {
      ch = ch >> 5;
      int indexValue = this.m_index_[ch];
      if(indexValue > 0) {
         return indexValue;
      } else {
         int newBlock = this.allocDataBlock();
         if(newBlock < 0) {
            return -1;
         } else {
            this.m_index_[ch] = newBlock;
            System.arraycopy(this.m_data_, Math.abs(indexValue), this.m_data_, newBlock, 128);
            return newBlock;
         }
      }
   }

   private void compact(boolean overlap) {
      if(!this.m_isCompacted_) {
         this.findUnusedBlocks();
         int overlapStart = 32;
         if(this.m_isLatin1Linear_) {
            overlapStart += 256;
         }

         int newStart = 32;
         int start = newStart;

         while(start < this.m_dataLength_) {
            if(this.m_map_[start >>> 5] < 0) {
               start += 32;
            } else {
               if(start >= overlapStart) {
                  int i = findSameDataBlock(this.m_data_, newStart, start, overlap?4:32);
                  if(i >= 0) {
                     this.m_map_[start >>> 5] = i;
                     start += 32;
                     continue;
                  }
               }

               int i;
               if(overlap && start >= overlapStart) {
                  for(i = 28; i > 0 && !equal_int(this.m_data_, newStart - i, start, i); i -= 4) {
                     ;
                  }
               } else {
                  i = 0;
               }

               if(i > 0) {
                  this.m_map_[start >>> 5] = newStart - i;
                  start += i;

                  for(i = 32 - i; i > 0; --i) {
                     this.m_data_[newStart++] = this.m_data_[start++];
                  }
               } else if(newStart < start) {
                  this.m_map_[start >>> 5] = newStart;

                  for(i = 32; i > 0; --i) {
                     this.m_data_[newStart++] = this.m_data_[start++];
                  }
               } else {
                  this.m_map_[start >>> 5] = start;
                  newStart += 32;
                  start = newStart;
               }
            }
         }

         for(int i = 0; i < this.m_indexLength_; ++i) {
            this.m_index_[i] = this.m_map_[Math.abs(this.m_index_[i]) >>> 5];
         }

         this.m_dataLength_ = newStart;
      }
   }

   private static final int findSameDataBlock(int[] data, int dataLength, int otherBlock, int step) {
      dataLength = dataLength - 32;

      for(int block = 0; block <= dataLength; block += step) {
         if(equal_int(data, block, otherBlock, 32)) {
            return block;
         }
      }

      return -1;
   }

   private final void fold(TrieBuilder.DataManipulate manipulate) {
      int[] leadIndexes = new int[32];
      int[] index = this.m_index_;
      System.arraycopy(index, 1728, leadIndexes, 0, 32);
      int block = 0;
      if(this.m_leadUnitValue_ != this.m_initialValue_) {
         block = this.allocDataBlock();
         if(block < 0) {
            throw new IllegalStateException("Internal error: Out of memory space");
         }

         this.fillBlock(block, 0, 32, this.m_leadUnitValue_, true);
         block = -block;
      }

      for(int c = 1728; c < 1760; ++c) {
         this.m_index_[c] = block;
      }

      int indexLength = 2048;
      int c = 65536;

      while(c < 1114112) {
         if(index[c >> 5] != 0) {
            c = c & -1024;
            block = findSameIndexBlock(index, indexLength, c >> 5);
            int value = manipulate.getFoldedValue(c, block + 32);
            if(value != this.getValue(UTF16.getLeadSurrogate(c))) {
               if(!this.setValue(UTF16.getLeadSurrogate(c), value)) {
                  throw new ArrayIndexOutOfBoundsException("Data table overflow");
               }

               if(block == indexLength) {
                  System.arraycopy(index, c >> 5, index, indexLength, 32);
                  indexLength += 32;
               }
            }

            c = c + 1024;
         } else {
            c += 32;
         }
      }

      if(indexLength >= '蠀') {
         throw new ArrayIndexOutOfBoundsException("Index table overflow");
      } else {
         System.arraycopy(index, 2048, index, 2080, indexLength - 2048);
         System.arraycopy(leadIndexes, 0, index, 2048, 32);
         indexLength = indexLength + 32;
         this.m_indexLength_ = indexLength;
      }
   }

   private void fillBlock(int block, int start, int limit, int value, boolean overwrite) {
      limit = limit + block;
      block = block + start;
      if(overwrite) {
         while(block < limit) {
            this.m_data_[block++] = value;
         }
      } else {
         for(; block < limit; ++block) {
            if(this.m_data_[block] == this.m_initialValue_) {
               this.m_data_[block] = value;
            }
         }
      }

   }
}
