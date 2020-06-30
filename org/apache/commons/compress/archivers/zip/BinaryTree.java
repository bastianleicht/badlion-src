package org.apache.commons.compress.archivers.zip;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import org.apache.commons.compress.archivers.zip.BitStream;

class BinaryTree {
   private static final int UNDEFINED = -1;
   private static final int NODE = -2;
   private final int[] tree;

   public BinaryTree(int depth) {
      this.tree = new int[(1 << depth + 1) - 1];
      Arrays.fill(this.tree, -1);
   }

   public void addLeaf(int node, int path, int depth, int value) {
      if(depth == 0) {
         if(this.tree[node] != -1) {
            throw new IllegalArgumentException("Tree value at index " + node + " has already been assigned (" + this.tree[node] + ")");
         }

         this.tree[node] = value;
      } else {
         this.tree[node] = -2;
         int nextChild = 2 * node + 1 + (path & 1);
         this.addLeaf(nextChild, path >>> 1, depth - 1, value);
      }

   }

   public int read(BitStream stream) throws IOException {
      int currentIndex = 0;

      while(true) {
         int bit = stream.nextBit();
         if(bit == -1) {
            return -1;
         }

         int childIndex = 2 * currentIndex + 1 + bit;
         int value = this.tree[childIndex];
         if(value != -2) {
            if(value != -1) {
               return value;
            }

            throw new IOException("The child " + bit + " of node at index " + currentIndex + " is not defined");
         }

         currentIndex = childIndex;
      }
   }

   static BinaryTree decode(InputStream in, int totalNumberOfValues) throws IOException {
      int size = in.read() + 1;
      if(size == 0) {
         throw new IOException("Cannot read the size of the encoded tree, unexpected end of stream");
      } else {
         byte[] encodedTree = new byte[size];
         (new DataInputStream(in)).readFully(encodedTree);
         int maxLength = 0;
         int[] originalBitLengths = new int[totalNumberOfValues];
         int pos = 0;

         for(byte b : encodedTree) {
            int numberOfValues = ((b & 240) >> 4) + 1;
            int bitLength = (b & 15) + 1;

            for(int j = 0; j < numberOfValues; ++j) {
               originalBitLengths[pos++] = bitLength;
            }

            maxLength = Math.max(maxLength, bitLength);
         }

         int[] permutation = new int[originalBitLengths.length];

         for(int k = 0; k < permutation.length; permutation[k] = k++) {
            ;
         }

         int c = 0;
         int[] sortedBitLengths = new int[originalBitLengths.length];

         for(int k = 0; k < originalBitLengths.length; ++k) {
            for(int l = 0; l < originalBitLengths.length; ++l) {
               if(originalBitLengths[l] == k) {
                  sortedBitLengths[c] = k;
                  permutation[c] = l;
                  ++c;
               }
            }
         }

         int code = 0;
         int codeIncrement = 0;
         int lastBitLength = 0;
         int[] codes = new int[totalNumberOfValues];

         for(int i = totalNumberOfValues - 1; i >= 0; --i) {
            code += codeIncrement;
            if(sortedBitLengths[i] != lastBitLength) {
               lastBitLength = sortedBitLengths[i];
               codeIncrement = 1 << 16 - lastBitLength;
            }

            codes[permutation[i]] = code;
         }

         BinaryTree tree = new BinaryTree(maxLength);

         for(int k = 0; k < codes.length; ++k) {
            int bitLength = originalBitLengths[k];
            if(bitLength > 0) {
               tree.addLeaf(0, Integer.reverse(codes[k] << 16), bitLength, k);
            }
         }

         return tree;
      }
   }
}
