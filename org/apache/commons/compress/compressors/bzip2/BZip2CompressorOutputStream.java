package org.apache.commons.compress.compressors.bzip2;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.compress.compressors.CompressorOutputStream;
import org.apache.commons.compress.compressors.bzip2.BZip2Constants;
import org.apache.commons.compress.compressors.bzip2.BlockSort;
import org.apache.commons.compress.compressors.bzip2.CRC;

public class BZip2CompressorOutputStream extends CompressorOutputStream implements BZip2Constants {
   public static final int MIN_BLOCKSIZE = 1;
   public static final int MAX_BLOCKSIZE = 9;
   private static final int GREATER_ICOST = 15;
   private static final int LESSER_ICOST = 0;
   private int last;
   private final int blockSize100k;
   private int bsBuff;
   private int bsLive;
   private final CRC crc;
   private int nInUse;
   private int nMTF;
   private int currentChar;
   private int runLength;
   private int blockCRC;
   private int combinedCRC;
   private final int allowableBlockSize;
   private BZip2CompressorOutputStream.Data data;
   private BlockSort blockSorter;
   private OutputStream out;

   private static void hbMakeCodeLengths(byte[] len, int[] freq, BZip2CompressorOutputStream.Data dat, int alphaSize, int maxLen) {
      int[] heap = dat.heap;
      int[] weight = dat.weight;
      int[] parent = dat.parent;
      int i = alphaSize;

      while(true) {
         --i;
         if(i < 0) {
            i = 1;

            while(i) {
               i = 0;
               int nNodes = alphaSize;
               int nHeap = 0;
               heap[0] = 0;
               weight[0] = 0;
               parent[0] = -2;

               for(int i = 1; i <= alphaSize; ++i) {
                  parent[i] = -1;
                  ++nHeap;
                  heap[nHeap] = i;
                  int zz = nHeap;

                  int tmp;
                  for(tmp = heap[nHeap]; weight[tmp] < weight[heap[zz >> 1]]; zz >>= 1) {
                     heap[zz] = heap[zz >> 1];
                  }

                  heap[zz] = tmp;
               }

               while(nHeap > 1) {
                  int n1 = heap[1];
                  heap[1] = heap[nHeap];
                  --nHeap;
                  int yy = 0;
                  int zz = 1;
                  int tmp = heap[1];

                  while(true) {
                     yy = zz << 1;
                     if(yy > nHeap) {
                        break;
                     }

                     if(yy < nHeap && weight[heap[yy + 1]] < weight[heap[yy]]) {
                        ++yy;
                     }

                     if(weight[tmp] < weight[heap[yy]]) {
                        break;
                     }

                     heap[zz] = heap[yy];
                     zz = yy;
                  }

                  heap[zz] = tmp;
                  int n2 = heap[1];
                  heap[1] = heap[nHeap];
                  --nHeap;
                  yy = 0;
                  zz = 1;
                  tmp = heap[1];

                  while(true) {
                     yy = zz << 1;
                     if(yy > nHeap) {
                        break;
                     }

                     if(yy < nHeap && weight[heap[yy + 1]] < weight[heap[yy]]) {
                        ++yy;
                     }

                     if(weight[tmp] < weight[heap[yy]]) {
                        break;
                     }

                     heap[zz] = heap[yy];
                     zz = yy;
                  }

                  heap[zz] = tmp;
                  ++nNodes;
                  parent[n1] = parent[n2] = nNodes;
                  int weight_n1 = weight[n1];
                  int weight_n2 = weight[n2];
                  weight[nNodes] = (weight_n1 & -256) + (weight_n2 & -256) | 1 + ((weight_n1 & 255) > (weight_n2 & 255)?weight_n1 & 255:weight_n2 & 255);
                  parent[nNodes] = -1;
                  ++nHeap;
                  heap[nHeap] = nNodes;
                  tmp = 0;
                  zz = nHeap;
                  tmp = heap[nHeap];

                  for(int weight_tmp = weight[tmp]; weight_tmp < weight[heap[zz >> 1]]; zz >>= 1) {
                     heap[zz] = heap[zz >> 1];
                  }

                  heap[zz] = tmp;
               }

               for(int i = 1; i <= alphaSize; ++i) {
                  int j = 0;

                  int parent_k;
                  for(int k = i; (parent_k = parent[k]) >= 0; ++j) {
                     k = parent_k;
                  }

                  len[i - 1] = (byte)j;
                  if(j > maxLen) {
                     i = 1;
                  }
               }

               if(i) {
                  for(int i = 1; i < alphaSize; ++i) {
                     int j = weight[i] >> 8;
                     j = 1 + (j >> 1);
                     weight[i] = j << 8;
                  }
               }
            }

            return;
         }

         weight[i + 1] = (freq[i] == 0?1:freq[i]) << 8;
      }
   }

   public static int chooseBlockSize(long inputLength) {
      return inputLength > 0L?(int)Math.min(inputLength / 132000L + 1L, 9L):9;
   }

   public BZip2CompressorOutputStream(OutputStream out) throws IOException {
      this(out, 9);
   }

   public BZip2CompressorOutputStream(OutputStream out, int blockSize) throws IOException {
      this.crc = new CRC();
      this.currentChar = -1;
      this.runLength = 0;
      if(blockSize < 1) {
         throw new IllegalArgumentException("blockSize(" + blockSize + ") < 1");
      } else if(blockSize > 9) {
         throw new IllegalArgumentException("blockSize(" + blockSize + ") > 9");
      } else {
         this.blockSize100k = blockSize;
         this.out = out;
         this.allowableBlockSize = this.blockSize100k * 100000 - 20;
         this.init();
      }
   }

   public void write(int b) throws IOException {
      if(this.out != null) {
         this.write0(b);
      } else {
         throw new IOException("closed");
      }
   }

   private void writeRun() throws IOException {
      int lastShadow = this.last;
      if(lastShadow < this.allowableBlockSize) {
         int currentCharShadow = this.currentChar;
         BZip2CompressorOutputStream.Data dataShadow = this.data;
         dataShadow.inUse[currentCharShadow] = true;
         byte ch = (byte)currentCharShadow;
         int runLengthShadow = this.runLength;
         this.crc.updateCRC(currentCharShadow, runLengthShadow);
         switch(runLengthShadow) {
         case 1:
            dataShadow.block[lastShadow + 2] = ch;
            this.last = lastShadow + 1;
            break;
         case 2:
            dataShadow.block[lastShadow + 2] = ch;
            dataShadow.block[lastShadow + 3] = ch;
            this.last = lastShadow + 2;
            break;
         case 3:
            byte[] block = dataShadow.block;
            block[lastShadow + 2] = ch;
            block[lastShadow + 3] = ch;
            block[lastShadow + 4] = ch;
            this.last = lastShadow + 3;
            break;
         default:
            runLengthShadow = runLengthShadow - 4;
            dataShadow.inUse[runLengthShadow] = true;
            byte[] block = dataShadow.block;
            block[lastShadow + 2] = ch;
            block[lastShadow + 3] = ch;
            block[lastShadow + 4] = ch;
            block[lastShadow + 5] = ch;
            block[lastShadow + 6] = (byte)runLengthShadow;
            this.last = lastShadow + 5;
         }
      } else {
         this.endBlock();
         this.initBlock();
         this.writeRun();
      }

   }

   protected void finalize() throws Throwable {
      this.finish();
      super.finalize();
   }

   public void finish() throws IOException {
      if(this.out != null) {
         try {
            if(this.runLength > 0) {
               this.writeRun();
            }

            this.currentChar = -1;
            this.endBlock();
            this.endCompression();
         } finally {
            this.out = null;
            this.data = null;
            this.blockSorter = null;
         }
      }

   }

   public void close() throws IOException {
      if(this.out != null) {
         OutputStream outShadow = this.out;
         this.finish();
         outShadow.close();
      }

   }

   public void flush() throws IOException {
      OutputStream outShadow = this.out;
      if(outShadow != null) {
         outShadow.flush();
      }

   }

   private void init() throws IOException {
      this.bsPutUByte(66);
      this.bsPutUByte(90);
      this.data = new BZip2CompressorOutputStream.Data(this.blockSize100k);
      this.blockSorter = new BlockSort(this.data);
      this.bsPutUByte(104);
      this.bsPutUByte(48 + this.blockSize100k);
      this.combinedCRC = 0;
      this.initBlock();
   }

   private void initBlock() {
      this.crc.initialiseCRC();
      this.last = -1;
      boolean[] inUse = this.data.inUse;
      int i = 256;

      while(true) {
         --i;
         if(i < 0) {
            return;
         }

         inUse[i] = false;
      }
   }

   private void endBlock() throws IOException {
      this.blockCRC = this.crc.getFinalCRC();
      this.combinedCRC = this.combinedCRC << 1 | this.combinedCRC >>> 31;
      this.combinedCRC ^= this.blockCRC;
      if(this.last != -1) {
         this.blockSort();
         this.bsPutUByte(49);
         this.bsPutUByte(65);
         this.bsPutUByte(89);
         this.bsPutUByte(38);
         this.bsPutUByte(83);
         this.bsPutUByte(89);
         this.bsPutInt(this.blockCRC);
         this.bsW(1, 0);
         this.moveToFrontCodeAndSend();
      }
   }

   private void endCompression() throws IOException {
      this.bsPutUByte(23);
      this.bsPutUByte(114);
      this.bsPutUByte(69);
      this.bsPutUByte(56);
      this.bsPutUByte(80);
      this.bsPutUByte(144);
      this.bsPutInt(this.combinedCRC);
      this.bsFinishedWithStream();
   }

   public final int getBlockSize() {
      return this.blockSize100k;
   }

   public void write(byte[] buf, int offs, int len) throws IOException {
      if(offs < 0) {
         throw new IndexOutOfBoundsException("offs(" + offs + ") < 0.");
      } else if(len < 0) {
         throw new IndexOutOfBoundsException("len(" + len + ") < 0.");
      } else if(offs + len > buf.length) {
         throw new IndexOutOfBoundsException("offs(" + offs + ") + len(" + len + ") > buf.length(" + buf.length + ").");
      } else if(this.out == null) {
         throw new IOException("stream closed");
      } else {
         int hi = offs + len;

         while(offs < hi) {
            this.write0(buf[offs++]);
         }

      }
   }

   private void write0(int b) throws IOException {
      if(this.currentChar != -1) {
         b = b & 255;
         if(this.currentChar == b) {
            if(++this.runLength > 254) {
               this.writeRun();
               this.currentChar = -1;
               this.runLength = 0;
            }
         } else {
            this.writeRun();
            this.runLength = 1;
            this.currentChar = b;
         }
      } else {
         this.currentChar = b & 255;
         ++this.runLength;
      }

   }

   private static void hbAssignCodes(int[] code, byte[] length, int minLen, int maxLen, int alphaSize) {
      int vec = 0;

      for(int n = minLen; n <= maxLen; ++n) {
         for(int i = 0; i < alphaSize; ++i) {
            if((length[i] & 255) == n) {
               code[i] = vec++;
            }
         }

         vec <<= 1;
      }

   }

   private void bsFinishedWithStream() throws IOException {
      while(this.bsLive > 0) {
         int ch = this.bsBuff >> 24;
         this.out.write(ch);
         this.bsBuff <<= 8;
         this.bsLive -= 8;
      }

   }

   private void bsW(int n, int v) throws IOException {
      OutputStream outShadow = this.out;
      int bsLiveShadow = this.bsLive;

      int bsBuffShadow;
      for(bsBuffShadow = this.bsBuff; bsLiveShadow >= 8; bsLiveShadow -= 8) {
         outShadow.write(bsBuffShadow >> 24);
         bsBuffShadow <<= 8;
      }

      this.bsBuff = bsBuffShadow | v << 32 - bsLiveShadow - n;
      this.bsLive = bsLiveShadow + n;
   }

   private void bsPutUByte(int c) throws IOException {
      this.bsW(8, c);
   }

   private void bsPutInt(int u) throws IOException {
      this.bsW(8, u >> 24 & 255);
      this.bsW(8, u >> 16 & 255);
      this.bsW(8, u >> 8 & 255);
      this.bsW(8, u & 255);
   }

   private void sendMTFValues() throws IOException {
      byte[][] len = this.data.sendMTFValues_len;
      int alphaSize = this.nInUse + 2;
      int t = 6;

      while(true) {
         --t;
         if(t < 0) {
            t = this.nMTF < 200?2:(this.nMTF < 600?3:(this.nMTF < 1200?4:(this.nMTF < 2400?5:6)));
            this.sendMTFValues0(t, alphaSize);
            int nSelectors = this.sendMTFValues1(t, alphaSize);
            this.sendMTFValues2(t, nSelectors);
            this.sendMTFValues3(t, alphaSize);
            this.sendMTFValues4();
            this.sendMTFValues5(t, nSelectors);
            this.sendMTFValues6(t, alphaSize);
            this.sendMTFValues7();
            return;
         }

         byte[] len_t = len[t];
         int v = alphaSize;

         while(true) {
            --v;
            if(v < 0) {
               break;
            }

            len_t[v] = 15;
         }
      }
   }

   private void sendMTFValues0(int nGroups, int alphaSize) {
      byte[][] len = this.data.sendMTFValues_len;
      int[] mtfFreq = this.data.mtfFreq;
      int remF = this.nMTF;
      int gs = 0;

      for(int nPart = nGroups; nPart > 0; --nPart) {
         int tFreq = remF / nPart;
         int ge = gs - 1;
         int aFreq = 0;

         for(int a = alphaSize - 1; aFreq < tFreq && ge < a; aFreq += mtfFreq[ge]) {
            ++ge;
         }

         if(ge > gs && nPart != nGroups && nPart != 1 && (nGroups - nPart & 1) != 0) {
            aFreq -= mtfFreq[ge--];
         }

         byte[] len_np = len[nPart - 1];
         int v = alphaSize;

         while(true) {
            --v;
            if(v < 0) {
               gs = ge + 1;
               remF -= aFreq;
               break;
            }

            if(v >= gs && v <= ge) {
               len_np[v] = 0;
            } else {
               len_np[v] = 15;
            }
         }
      }

   }

   private int sendMTFValues1(int nGroups, int alphaSize) {
      BZip2CompressorOutputStream.Data dataShadow = this.data;
      int[][] rfreq = dataShadow.sendMTFValues_rfreq;
      int[] fave = dataShadow.sendMTFValues_fave;
      short[] cost = dataShadow.sendMTFValues_cost;
      char[] sfmap = dataShadow.sfmap;
      byte[] selector = dataShadow.selector;
      byte[][] len = dataShadow.sendMTFValues_len;
      byte[] len_0 = len[0];
      byte[] len_1 = len[1];
      byte[] len_2 = len[2];
      byte[] len_3 = len[3];
      byte[] len_4 = len[4];
      byte[] len_5 = len[5];
      int nMTFShadow = this.nMTF;
      int nSelectors = 0;

      for(int iter = 0; iter < 4; ++iter) {
         int t = nGroups;

         while(true) {
            --t;
            if(t < 0) {
               nSelectors = 0;

               int ge;
               for(t = 0; t < this.nMTF; t = ge + 1) {
                  ge = Math.min(t + 50 - 1, nMTFShadow - 1);
                  if(nGroups == 6) {
                     short cost0 = 0;
                     short cost1 = 0;
                     short cost2 = 0;
                     short cost3 = 0;
                     short cost4 = 0;
                     short cost5 = 0;

                     for(int i = t; i <= ge; ++i) {
                        int icv = sfmap[i];
                        cost0 = (short)(cost0 + (len_0[icv] & 255));
                        cost1 = (short)(cost1 + (len_1[icv] & 255));
                        cost2 = (short)(cost2 + (len_2[icv] & 255));
                        cost3 = (short)(cost3 + (len_3[icv] & 255));
                        cost4 = (short)(cost4 + (len_4[icv] & 255));
                        cost5 = (short)(cost5 + (len_5[icv] & 255));
                     }

                     cost[0] = cost0;
                     cost[1] = cost1;
                     cost[2] = cost2;
                     cost[3] = cost3;
                     cost[4] = cost4;
                     cost[5] = cost5;
                  } else {
                     int t = nGroups;

                     while(true) {
                        --t;
                        if(t < 0) {
                           for(t = t; t <= ge; ++t) {
                              int icv = sfmap[t];
                              int t = nGroups;

                              while(true) {
                                 --t;
                                 if(t < 0) {
                                    break;
                                 }

                                 cost[t] = (short)(cost[t] + (len[t][icv] & 255));
                              }
                           }
                           break;
                        }

                        cost[t] = 0;
                     }
                  }

                  int bt = -1;
                  int t = nGroups;
                  int bc = 999999999;

                  while(true) {
                     --t;
                     if(t < 0) {
                        ++fave[bt];
                        selector[nSelectors] = (byte)bt;
                        ++nSelectors;
                        int[] rfreq_bt = rfreq[bt];

                        for(bc = t; bc <= ge; ++bc) {
                           ++rfreq_bt[sfmap[bc]];
                        }
                        break;
                     }

                     int cost_t = cost[t];
                     if(cost_t < bc) {
                        bc = cost_t;
                        bt = t;
                     }
                  }
               }

               for(t = 0; t < nGroups; ++t) {
                  hbMakeCodeLengths(len[t], rfreq[t], this.data, alphaSize, 20);
               }
               break;
            }

            fave[t] = 0;
            int[] rfreqt = rfreq[t];
            int i = alphaSize;

            while(true) {
               --i;
               if(i < 0) {
                  break;
               }

               rfreqt[i] = 0;
            }
         }
      }

      return nSelectors;
   }

   private void sendMTFValues2(int nGroups, int nSelectors) {
      BZip2CompressorOutputStream.Data dataShadow = this.data;
      byte[] pos = dataShadow.sendMTFValues2_pos;
      int i = nGroups;

      while(true) {
         --i;
         if(i < 0) {
            for(i = 0; i < nSelectors; ++i) {
               byte ll_i = dataShadow.selector[i];
               byte tmp = pos[0];

               int j;
               byte tmp2;
               for(j = 0; ll_i != tmp; pos[j] = tmp2) {
                  ++j;
                  tmp2 = tmp;
                  tmp = pos[j];
               }

               pos[0] = tmp;
               dataShadow.selectorMtf[i] = (byte)j;
            }

            return;
         }

         pos[i] = (byte)i;
      }
   }

   private void sendMTFValues3(int nGroups, int alphaSize) {
      int[][] code = this.data.sendMTFValues_code;
      byte[][] len = this.data.sendMTFValues_len;

      for(int t = 0; t < nGroups; ++t) {
         int minLen = 32;
         int maxLen = 0;
         byte[] len_t = len[t];
         int i = alphaSize;

         while(true) {
            --i;
            if(i < 0) {
               hbAssignCodes(code[t], len[t], minLen, maxLen, alphaSize);
               break;
            }

            int l = len_t[i] & 255;
            if(l > maxLen) {
               maxLen = l;
            }

            if(l < minLen) {
               minLen = l;
            }
         }
      }

   }

   private void sendMTFValues4() throws IOException {
      boolean[] inUse = this.data.inUse;
      boolean[] inUse16 = this.data.sentMTFValues4_inUse16;
      int i = 16;

      while(true) {
         --i;
         if(i < 0) {
            for(i = 0; i < 16; ++i) {
               this.bsW(1, inUse16[i]?1:0);
            }

            OutputStream outShadow = this.out;
            int bsLiveShadow = this.bsLive;
            int bsBuffShadow = this.bsBuff;

            for(int i = 0; i < 16; ++i) {
               if(inUse16[i]) {
                  int i16 = i * 16;

                  for(int j = 0; j < 16; ++j) {
                     while(bsLiveShadow >= 8) {
                        outShadow.write(bsBuffShadow >> 24);
                        bsBuffShadow <<= 8;
                        bsLiveShadow -= 8;
                     }

                     if(inUse[i16 + j]) {
                        bsBuffShadow |= 1 << 32 - bsLiveShadow - 1;
                     }

                     ++bsLiveShadow;
                  }
               }
            }

            this.bsBuff = bsBuffShadow;
            this.bsLive = bsLiveShadow;
            return;
         }

         inUse16[i] = false;
         int i16 = i * 16;
         int j = 16;

         while(true) {
            --j;
            if(j < 0) {
               break;
            }

            if(inUse[i16 + j]) {
               inUse16[i] = true;
            }
         }
      }
   }

   private void sendMTFValues5(int nGroups, int nSelectors) throws IOException {
      this.bsW(3, nGroups);
      this.bsW(15, nSelectors);
      OutputStream outShadow = this.out;
      byte[] selectorMtf = this.data.selectorMtf;
      int bsLiveShadow = this.bsLive;
      int bsBuffShadow = this.bsBuff;

      for(int i = 0; i < nSelectors; ++i) {
         int j = 0;

         for(int hj = selectorMtf[i] & 255; j < hj; ++j) {
            while(bsLiveShadow >= 8) {
               outShadow.write(bsBuffShadow >> 24);
               bsBuffShadow <<= 8;
               bsLiveShadow -= 8;
            }

            bsBuffShadow |= 1 << 32 - bsLiveShadow - 1;
            ++bsLiveShadow;
         }

         while(bsLiveShadow >= 8) {
            outShadow.write(bsBuffShadow >> 24);
            bsBuffShadow <<= 8;
            bsLiveShadow -= 8;
         }

         ++bsLiveShadow;
      }

      this.bsBuff = bsBuffShadow;
      this.bsLive = bsLiveShadow;
   }

   private void sendMTFValues6(int nGroups, int alphaSize) throws IOException {
      byte[][] len = this.data.sendMTFValues_len;
      OutputStream outShadow = this.out;
      int bsLiveShadow = this.bsLive;
      int bsBuffShadow = this.bsBuff;

      for(int t = 0; t < nGroups; ++t) {
         byte[] len_t = len[t];

         int curr;
         for(curr = len_t[0] & 255; bsLiveShadow >= 8; bsLiveShadow -= 8) {
            outShadow.write(bsBuffShadow >> 24);
            bsBuffShadow <<= 8;
         }

         bsBuffShadow |= curr << 32 - bsLiveShadow - 5;
         bsLiveShadow += 5;

         for(int i = 0; i < alphaSize; ++i) {
            int lti;
            for(lti = len_t[i] & 255; curr < lti; ++curr) {
               while(bsLiveShadow >= 8) {
                  outShadow.write(bsBuffShadow >> 24);
                  bsBuffShadow <<= 8;
                  bsLiveShadow -= 8;
               }

               bsBuffShadow |= 2 << 32 - bsLiveShadow - 2;
               bsLiveShadow += 2;
            }

            while(curr > lti) {
               while(bsLiveShadow >= 8) {
                  outShadow.write(bsBuffShadow >> 24);
                  bsBuffShadow <<= 8;
                  bsLiveShadow -= 8;
               }

               bsBuffShadow |= 3 << 32 - bsLiveShadow - 2;
               bsLiveShadow += 2;
               --curr;
            }

            while(bsLiveShadow >= 8) {
               outShadow.write(bsBuffShadow >> 24);
               bsBuffShadow <<= 8;
               bsLiveShadow -= 8;
            }

            ++bsLiveShadow;
         }
      }

      this.bsBuff = bsBuffShadow;
      this.bsLive = bsLiveShadow;
   }

   private void sendMTFValues7() throws IOException {
      BZip2CompressorOutputStream.Data dataShadow = this.data;
      byte[][] len = dataShadow.sendMTFValues_len;
      int[][] code = dataShadow.sendMTFValues_code;
      OutputStream outShadow = this.out;
      byte[] selector = dataShadow.selector;
      char[] sfmap = dataShadow.sfmap;
      int nMTFShadow = this.nMTF;
      int selCtr = 0;
      int bsLiveShadow = this.bsLive;
      int bsBuffShadow = this.bsBuff;

      for(int gs = 0; gs < nMTFShadow; ++selCtr) {
         int ge = Math.min(gs + 50 - 1, nMTFShadow - 1);
         int selector_selCtr = selector[selCtr] & 255;
         int[] code_selCtr = code[selector_selCtr];

         for(byte[] len_selCtr = len[selector_selCtr]; gs <= ge; ++gs) {
            int sfmap_i;
            for(sfmap_i = sfmap[gs]; bsLiveShadow >= 8; bsLiveShadow -= 8) {
               outShadow.write(bsBuffShadow >> 24);
               bsBuffShadow <<= 8;
            }

            int n = len_selCtr[sfmap_i] & 255;
            bsBuffShadow |= code_selCtr[sfmap_i] << 32 - bsLiveShadow - n;
            bsLiveShadow += n;
         }

         gs = ge + 1;
      }

      this.bsBuff = bsBuffShadow;
      this.bsLive = bsLiveShadow;
   }

   private void moveToFrontCodeAndSend() throws IOException {
      this.bsW(24, this.data.origPtr);
      this.generateMTFValues();
      this.sendMTFValues();
   }

   private void blockSort() {
      this.blockSorter.blockSort(this.data, this.last);
   }

   private void generateMTFValues() {
      int lastShadow = this.last;
      BZip2CompressorOutputStream.Data dataShadow = this.data;
      boolean[] inUse = dataShadow.inUse;
      byte[] block = dataShadow.block;
      int[] fmap = dataShadow.fmap;
      char[] sfmap = dataShadow.sfmap;
      int[] mtfFreq = dataShadow.mtfFreq;
      byte[] unseqToSeq = dataShadow.unseqToSeq;
      byte[] yy = dataShadow.generateMTFValues_yy;
      int nInUseShadow = 0;

      for(int i = 0; i < 256; ++i) {
         if(inUse[i]) {
            unseqToSeq[i] = (byte)nInUseShadow;
            ++nInUseShadow;
         }
      }

      this.nInUse = nInUseShadow;
      int eob = nInUseShadow + 1;

      for(int i = eob; i >= 0; --i) {
         mtfFreq[i] = 0;
      }

      int i = nInUseShadow;

      while(true) {
         --i;
         if(i < 0) {
            i = 0;
            int zPend = 0;

            for(int i = 0; i <= lastShadow; ++i) {
               byte ll_i = unseqToSeq[block[fmap[i]] & 255];
               byte tmp = yy[0];

               int j;
               byte tmp2;
               for(j = 0; ll_i != tmp; yy[j] = tmp2) {
                  ++j;
                  tmp2 = tmp;
                  tmp = yy[j];
               }

               yy[0] = tmp;
               if(j == 0) {
                  ++zPend;
               } else {
                  if(zPend > 0) {
                     --zPend;

                     while(true) {
                        if((zPend & 1) == 0) {
                           sfmap[i] = 0;
                           ++i;
                           ++mtfFreq[0];
                        } else {
                           sfmap[i] = 1;
                           ++i;
                           ++mtfFreq[1];
                        }

                        if(zPend < 2) {
                           zPend = 0;
                           break;
                        }

                        zPend = zPend - 2 >> 1;
                     }
                  }

                  sfmap[i] = (char)(j + 1);
                  ++i;
                  ++mtfFreq[j + 1];
               }
            }

            if(zPend > 0) {
               --zPend;

               while(true) {
                  if((zPend & 1) == 0) {
                     sfmap[i] = 0;
                     ++i;
                     ++mtfFreq[0];
                  } else {
                     sfmap[i] = 1;
                     ++i;
                     ++mtfFreq[1];
                  }

                  if(zPend < 2) {
                     break;
                  }

                  zPend = zPend - 2 >> 1;
               }
            }

            sfmap[i] = (char)eob;
            ++mtfFreq[eob];
            this.nMTF = i + 1;
            return;
         }

         yy[i] = (byte)i;
      }
   }

   static final class Data {
      final boolean[] inUse = new boolean[256];
      final byte[] unseqToSeq = new byte[256];
      final int[] mtfFreq = new int[258];
      final byte[] selector = new byte[18002];
      final byte[] selectorMtf = new byte[18002];
      final byte[] generateMTFValues_yy = new byte[256];
      final byte[][] sendMTFValues_len = new byte[6][258];
      final int[][] sendMTFValues_rfreq = new int[6][258];
      final int[] sendMTFValues_fave = new int[6];
      final short[] sendMTFValues_cost = new short[6];
      final int[][] sendMTFValues_code = new int[6][258];
      final byte[] sendMTFValues2_pos = new byte[6];
      final boolean[] sentMTFValues4_inUse16 = new boolean[16];
      final int[] heap = new int[260];
      final int[] weight = new int[516];
      final int[] parent = new int[516];
      final byte[] block;
      final int[] fmap;
      final char[] sfmap;
      int origPtr;

      Data(int blockSize100k) {
         int n = blockSize100k * 100000;
         this.block = new byte[n + 1 + 20];
         this.fmap = new int[n];
         this.sfmap = new char[2 * n];
      }
   }
}
