package com.ibm.icu.text;

import com.ibm.icu.impl.ICUBinary;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.CharacterIterator;

class BreakCTDictionary {
   private BreakCTDictionary.CompactTrieHeader fData;
   private BreakCTDictionary.CompactTrieNodes[] nodes;
   private static final byte[] DATA_FORMAT_ID = new byte[]{(byte)84, (byte)114, (byte)68, (byte)99};

   private BreakCTDictionary.CompactTrieNodes getCompactTrieNode(int node) {
      return this.nodes[node];
   }

   public BreakCTDictionary(InputStream is) throws IOException {
      ICUBinary.readHeader(is, DATA_FORMAT_ID, (ICUBinary.Authenticate)null);
      DataInputStream in = new DataInputStream(is);
      this.fData = new BreakCTDictionary.CompactTrieHeader();
      this.fData.size = in.readInt();
      this.fData.magic = in.readInt();
      this.fData.nodeCount = in.readShort();
      this.fData.root = in.readShort();
      this.loadBreakCTDictionary(in);
   }

   private void loadBreakCTDictionary(DataInputStream in) throws IOException {
      for(int i = 0; i < this.fData.nodeCount; ++i) {
         in.readInt();
      }

      this.nodes = new BreakCTDictionary.CompactTrieNodes[this.fData.nodeCount];
      this.nodes[0] = new BreakCTDictionary.CompactTrieNodes();

      for(int j = 1; j < this.fData.nodeCount; ++j) {
         this.nodes[j] = new BreakCTDictionary.CompactTrieNodes();
         this.nodes[j].flagscount = in.readShort();
         int count = this.nodes[j].flagscount & 4095;
         if(count != 0) {
            boolean isVerticalNode = (this.nodes[j].flagscount & 4096) != 0;
            if(isVerticalNode) {
               this.nodes[j].vnode = new BreakCTDictionary.CompactTrieVerticalNode();
               this.nodes[j].vnode.equal = in.readShort();
               this.nodes[j].vnode.chars = new char[count];

               for(int l = 0; l < count; ++l) {
                  this.nodes[j].vnode.chars[l] = in.readChar();
               }
            } else {
               this.nodes[j].hnode = new BreakCTDictionary.CompactTrieHorizontalNode[count];

               for(int n = 0; n < count; ++n) {
                  this.nodes[j].hnode[n] = new BreakCTDictionary.CompactTrieHorizontalNode(in.readChar(), in.readShort());
               }
            }
         }
      }

   }

   public int matches(CharacterIterator text, int maxLength, int[] lengths, int[] count, int limit) {
      BreakCTDictionary.CompactTrieNodes node = this.getCompactTrieNode(this.fData.root);
      int mycount = 0;
      char uc = text.current();
      int i = 0;
      boolean exitFlag = false;

      while(node != null) {
         if(limit > 0 && (node.flagscount & 8192) != 0) {
            lengths[mycount++] = i;
            --limit;
         }

         if(i >= maxLength) {
            break;
         }

         int nodeCount = node.flagscount & 4095;
         if(nodeCount == 0) {
            break;
         }

         if((node.flagscount & 4096) == 0) {
            BreakCTDictionary.CompactTrieHorizontalNode[] hnode = node.hnode;
            int low = 0;
            int high = nodeCount - 1;
            node = null;

            while(high >= low) {
               int middle = high + low >>> 1;
               if(uc == hnode[middle].ch) {
                  node = this.getCompactTrieNode(hnode[middle].equal);
                  text.next();
                  uc = text.current();
                  ++i;
                  break;
               }

               if(uc < hnode[middle].ch) {
                  high = middle - 1;
               } else {
                  low = middle + 1;
               }
            }
         } else {
            BreakCTDictionary.CompactTrieVerticalNode vnode = node.vnode;

            for(int j = 0; j < nodeCount && i < maxLength; ++j) {
               if(uc != vnode.chars[j]) {
                  exitFlag = true;
                  break;
               }

               text.next();
               uc = text.current();
               ++i;
            }

            if(exitFlag) {
               break;
            }

            node = this.getCompactTrieNode(vnode.equal);
         }
      }

      count[0] = mycount;
      return i;
   }

   static class CompactTrieHeader {
      int size = 0;
      int magic = 0;
      int nodeCount = 0;
      int root = 0;
      int[] offset = null;
   }

   static class CompactTrieHorizontalNode {
      char ch;
      int equal;

      CompactTrieHorizontalNode(char newCh, int newEqual) {
         this.ch = newCh;
         this.equal = newEqual;
      }
   }

   static final class CompactTrieNodeFlags {
      static final int kVerticalNode = 4096;
      static final int kParentEndsWord = 8192;
      static final int kReservedFlag1 = 16384;
      static final int kReservedFlag2 = 32768;
      static final int kCountMask = 4095;
      static final int kFlagMask = 61440;
   }

   static class CompactTrieNodes {
      short flagscount = 0;
      BreakCTDictionary.CompactTrieHorizontalNode[] hnode = null;
      BreakCTDictionary.CompactTrieVerticalNode vnode = null;
   }

   static class CompactTrieVerticalNode {
      int equal = 0;
      char[] chars = null;
   }
}
