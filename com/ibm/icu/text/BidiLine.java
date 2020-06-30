package com.ibm.icu.text;

import com.ibm.icu.text.Bidi;
import com.ibm.icu.text.BidiRun;
import java.util.Arrays;

final class BidiLine {
   static void setTrailingWSStart(Bidi bidi) {
      byte[] dirProps = bidi.dirProps;
      byte[] levels = bidi.levels;
      int start = bidi.length;
      byte paraLevel = bidi.paraLevel;
      if(Bidi.NoContextRTL(dirProps[start - 1]) == 7) {
         bidi.trailingWSStart = start;
      } else {
         while(start > 0 && (Bidi.DirPropFlagNC(dirProps[start - 1]) & Bidi.MASK_WS) != 0) {
            --start;
         }

         while(start > 0 && levels[start - 1] == paraLevel) {
            --start;
         }

         bidi.trailingWSStart = start;
      }
   }

   static Bidi setLine(Bidi paraBidi, int start, int limit) {
      Bidi lineBidi = new Bidi();
      int length = lineBidi.length = lineBidi.originalLength = lineBidi.resultLength = limit - start;
      lineBidi.text = new char[length];
      System.arraycopy(paraBidi.text, start, lineBidi.text, 0, length);
      lineBidi.paraLevel = paraBidi.GetParaLevelAt(start);
      lineBidi.paraCount = paraBidi.paraCount;
      lineBidi.runs = new BidiRun[0];
      lineBidi.reorderingMode = paraBidi.reorderingMode;
      lineBidi.reorderingOptions = paraBidi.reorderingOptions;
      if(paraBidi.controlCount > 0) {
         for(int j = start; j < limit; ++j) {
            if(Bidi.IsBidiControlChar(paraBidi.text[j])) {
               ++lineBidi.controlCount;
            }
         }

         lineBidi.resultLength -= lineBidi.controlCount;
      }

      lineBidi.getDirPropsMemory(length);
      lineBidi.dirProps = lineBidi.dirPropsMemory;
      System.arraycopy(paraBidi.dirProps, start, lineBidi.dirProps, 0, length);
      lineBidi.getLevelsMemory(length);
      lineBidi.levels = lineBidi.levelsMemory;
      System.arraycopy(paraBidi.levels, start, lineBidi.levels, 0, length);
      lineBidi.runCount = -1;
      if(paraBidi.direction != 2) {
         lineBidi.direction = paraBidi.direction;
         if(paraBidi.trailingWSStart <= start) {
            lineBidi.trailingWSStart = 0;
         } else if(paraBidi.trailingWSStart < limit) {
            lineBidi.trailingWSStart = paraBidi.trailingWSStart - start;
         } else {
            lineBidi.trailingWSStart = length;
         }
      } else {
         byte[] levels = lineBidi.levels;
         setTrailingWSStart(lineBidi);
         int trailingWSStart = lineBidi.trailingWSStart;
         if(trailingWSStart == 0) {
            lineBidi.direction = (byte)(lineBidi.paraLevel & 1);
         } else {
            byte level = (byte)(levels[0] & 1);
            if(trailingWSStart < length && (lineBidi.paraLevel & 1) != level) {
               lineBidi.direction = 2;
            } else {
               label380: {
                  for(int i = 1; i != trailingWSStart; ++i) {
                     if((levels[i] & 1) != level) {
                        lineBidi.direction = 2;
                        break label380;
                     }
                  }

                  lineBidi.direction = level;
               }
            }
         }

         switch(lineBidi.direction) {
         case 0:
            lineBidi.paraLevel = (byte)(lineBidi.paraLevel + 1 & -2);
            lineBidi.trailingWSStart = 0;
            break;
         case 1:
            lineBidi.paraLevel = (byte)(lineBidi.paraLevel | 1);
            lineBidi.trailingWSStart = 0;
         }
      }

      lineBidi.paraBidi = paraBidi;
      return lineBidi;
   }

   static byte getLevelAt(Bidi bidi, int charIndex) {
      return bidi.direction == 2 && charIndex < bidi.trailingWSStart?bidi.levels[charIndex]:bidi.GetParaLevelAt(charIndex);
   }

   static byte[] getLevels(Bidi bidi) {
      int start = bidi.trailingWSStart;
      int length = bidi.length;
      if(start != length) {
         Arrays.fill(bidi.levels, start, length, bidi.paraLevel);
         bidi.trailingWSStart = length;
      }

      if(length < bidi.levels.length) {
         byte[] levels = new byte[length];
         System.arraycopy(bidi.levels, 0, levels, 0, length);
         return levels;
      } else {
         return bidi.levels;
      }
   }

   static BidiRun getLogicalRun(Bidi bidi, int logicalPosition) {
      BidiRun newRun = new BidiRun();
      getRuns(bidi);
      int runCount = bidi.runCount;
      int visualStart = 0;
      int logicalLimit = 0;
      BidiRun iRun = bidi.runs[0];

      for(int i = 0; i < runCount; ++i) {
         iRun = bidi.runs[i];
         logicalLimit = iRun.start + iRun.limit - visualStart;
         if(logicalPosition >= iRun.start && logicalPosition < logicalLimit) {
            break;
         }

         visualStart = iRun.limit;
      }

      newRun.start = iRun.start;
      newRun.limit = logicalLimit;
      newRun.level = iRun.level;
      return newRun;
   }

   static BidiRun getVisualRun(Bidi bidi, int runIndex) {
      int start = bidi.runs[runIndex].start;
      byte level = bidi.runs[runIndex].level;
      int limit;
      if(runIndex > 0) {
         limit = start + bidi.runs[runIndex].limit - bidi.runs[runIndex - 1].limit;
      } else {
         limit = start + bidi.runs[0].limit;
      }

      return new BidiRun(start, limit, level);
   }

   static void getSingleRun(Bidi bidi, byte level) {
      bidi.runs = bidi.simpleRuns;
      bidi.runCount = 1;
      bidi.runs[0] = new BidiRun(0, bidi.length, level);
   }

   private static void reorderLine(Bidi bidi, byte minLevel, byte maxLevel) {
      if(maxLevel > (minLevel | 1)) {
         ++minLevel;
         BidiRun[] runs = bidi.runs;
         byte[] levels = bidi.levels;
         int runCount = bidi.runCount;
         if(bidi.trailingWSStart < bidi.length) {
            --runCount;
         }

         label44:
         while(true) {
            --maxLevel;
            if(maxLevel < minLevel) {
               if((minLevel & 1) == 0) {
                  int firstRun = 0;
                  if(bidi.trailingWSStart == bidi.length) {
                     --runCount;
                  }

                  while(firstRun < runCount) {
                     BidiRun tempRun = runs[firstRun];
                     runs[firstRun] = runs[runCount];
                     runs[runCount] = tempRun;
                     ++firstRun;
                     --runCount;
                  }
               }

               return;
            }

            int firstRun = 0;

            while(true) {
               while(firstRun >= runCount || levels[runs[firstRun].start] >= maxLevel) {
                  if(firstRun >= runCount) {
                     continue label44;
                  }

                  int limitRun = firstRun;

                  while(true) {
                     ++limitRun;
                     if(limitRun >= runCount || levels[runs[limitRun].start] < maxLevel) {
                        break;
                     }
                  }

                  for(int endRun = limitRun - 1; firstRun < endRun; --endRun) {
                     BidiRun tempRun = runs[firstRun];
                     runs[firstRun] = runs[endRun];
                     runs[endRun] = tempRun;
                     ++firstRun;
                  }

                  if(limitRun == runCount) {
                     continue label44;
                  }

                  firstRun = limitRun + 1;
               }

               ++firstRun;
            }
         }
      }
   }

   static int getRunFromLogicalIndex(Bidi bidi, int logicalIndex) {
      BidiRun[] runs = bidi.runs;
      int runCount = bidi.runCount;
      int visualStart = 0;

      for(int i = 0; i < runCount; ++i) {
         int length = runs[i].limit - visualStart;
         int logicalStart = runs[i].start;
         if(logicalIndex >= logicalStart && logicalIndex < logicalStart + length) {
            return i;
         }

         visualStart += length;
      }

      throw new IllegalStateException("Internal ICU error in getRunFromLogicalIndex");
   }

   static void getRuns(Bidi bidi) {
      if(bidi.runCount < 0) {
         if(bidi.direction != 2) {
            getSingleRun(bidi, bidi.paraLevel);
         } else {
            int length = bidi.length;
            byte[] levels = bidi.levels;
            byte level = 126;
            int limit = bidi.trailingWSStart;
            int runCount = 0;

            for(int i = 0; i < limit; ++i) {
               if(levels[i] != level) {
                  ++runCount;
                  level = levels[i];
               }
            }

            if(runCount == 1 && limit == length) {
               getSingleRun(bidi, levels[0]);
            } else {
               byte minLevel = 62;
               byte maxLevel = 0;
               if(limit < length) {
                  ++runCount;
               }

               bidi.getRunsMemory(runCount);
               BidiRun[] runs = bidi.runsMemory;
               int runIndex = 0;
               int var20 = 0;

               while(true) {
                  int start = var20;
                  level = levels[var20];
                  if(level < minLevel) {
                     minLevel = level;
                  }

                  if(level > maxLevel) {
                     maxLevel = level;
                  }

                  while(true) {
                     ++var20;
                     if(var20 >= limit || levels[var20] != level) {
                        break;
                     }
                  }

                  runs[runIndex] = new BidiRun(start, var20 - start, level);
                  ++runIndex;
                  if(var20 >= limit) {
                     break;
                  }
               }

               if(limit < length) {
                  runs[runIndex] = new BidiRun(limit, length - limit, bidi.paraLevel);
                  if(bidi.paraLevel < minLevel) {
                     minLevel = bidi.paraLevel;
                  }
               }

               bidi.runs = runs;
               bidi.runCount = runCount;
               reorderLine(bidi, minLevel, maxLevel);
               limit = 0;

               for(var20 = 0; var20 < runCount; ++var20) {
                  runs[var20].level = levels[runs[var20].start];
                  limit = runs[var20].limit += limit;
               }

               if(runIndex < runCount) {
                  int trailingRun = (bidi.paraLevel & 1) != 0?0:runIndex;
                  runs[trailingRun].level = bidi.paraLevel;
               }
            }
         }

         if(bidi.insertPoints.size > 0) {
            for(int ip = 0; ip < bidi.insertPoints.size; ++ip) {
               Bidi.Point point = bidi.insertPoints.points[ip];
               int runIndex = getRunFromLogicalIndex(bidi, point.pos);
               bidi.runs[runIndex].insertRemove |= point.flag;
            }
         }

         if(bidi.controlCount > 0) {
            for(int ic = 0; ic < bidi.length; ++ic) {
               char c = bidi.text[ic];
               if(Bidi.IsBidiControlChar(c)) {
                  int runIndex = getRunFromLogicalIndex(bidi, ic);
                  --bidi.runs[runIndex].insertRemove;
               }
            }
         }

      }
   }

   static int[] prepareReorder(byte[] levels, byte[] pMinLevel, byte[] pMaxLevel) {
      if(levels != null && levels.length > 0) {
         byte minLevel = 62;
         byte maxLevel = 0;
         int start = levels.length;

         while(start > 0) {
            --start;
            byte level = levels[start];
            if(level > 62) {
               return null;
            }

            if(level < minLevel) {
               minLevel = level;
            }

            if(level > maxLevel) {
               maxLevel = level;
            }
         }

         pMinLevel[0] = minLevel;
         pMaxLevel[0] = maxLevel;
         int[] indexMap = new int[levels.length];

         for(start = levels.length; start > 0; indexMap[start] = start) {
            --start;
         }

         return indexMap;
      } else {
         return null;
      }
   }

   static int[] reorderLogical(byte[] levels) {
      byte[] aMinLevel = new byte[1];
      byte[] aMaxLevel = new byte[1];
      int[] indexMap = prepareReorder(levels, aMinLevel, aMaxLevel);
      if(indexMap == null) {
         return null;
      } else {
         byte minLevel = aMinLevel[0];
         byte maxLevel = aMaxLevel[0];
         if(minLevel == maxLevel && (minLevel & 1) == 0) {
            return indexMap;
         } else {
            minLevel = (byte)(minLevel | 1);

            while(true) {
               int start = 0;

               label79:
               while(true) {
                  while(start >= levels.length || levels[start] >= maxLevel) {
                     if(start >= levels.length) {
                        break label79;
                     }

                     int limit = start;

                     while(true) {
                        ++limit;
                        if(limit >= levels.length || levels[limit] < maxLevel) {
                           break;
                        }
                     }

                     int sumOfSosEos = start + limit - 1;

                     while(true) {
                        indexMap[start] = sumOfSosEos - indexMap[start];
                        ++start;
                        if(start >= limit) {
                           break;
                        }
                     }

                     if(limit == levels.length) {
                        break label79;
                     }

                     start = limit + 1;
                  }

                  ++start;
               }

               --maxLevel;
               if(maxLevel < minLevel) {
                  break;
               }
            }

            return indexMap;
         }
      }
   }

   static int[] reorderVisual(byte[] levels) {
      byte[] aMinLevel = new byte[1];
      byte[] aMaxLevel = new byte[1];
      int[] indexMap = prepareReorder(levels, aMinLevel, aMaxLevel);
      if(indexMap == null) {
         return null;
      } else {
         byte minLevel = aMinLevel[0];
         byte maxLevel = aMaxLevel[0];
         if(minLevel == maxLevel && (minLevel & 1) == 0) {
            return indexMap;
         } else {
            minLevel = (byte)(minLevel | 1);

            while(true) {
               int start = 0;

               label79:
               while(true) {
                  while(start >= levels.length || levels[start] >= maxLevel) {
                     if(start >= levels.length) {
                        break label79;
                     }

                     int limit = start;

                     while(true) {
                        ++limit;
                        if(limit >= levels.length || levels[limit] < maxLevel) {
                           break;
                        }
                     }

                     for(int end = limit - 1; start < end; --end) {
                        int temp = indexMap[start];
                        indexMap[start] = indexMap[end];
                        indexMap[end] = temp;
                        ++start;
                     }

                     if(limit == levels.length) {
                        break label79;
                     }

                     start = limit + 1;
                  }

                  ++start;
               }

               --maxLevel;
               if(maxLevel < minLevel) {
                  break;
               }
            }

            return indexMap;
         }
      }
   }

   static int getVisualIndex(Bidi bidi, int logicalIndex) {
      int visualIndex = -1;
      switch(bidi.direction) {
      case 0:
         visualIndex = logicalIndex;
         break;
      case 1:
         visualIndex = bidi.length - logicalIndex - 1;
         break;
      default:
         getRuns(bidi);
         BidiRun[] runs = bidi.runs;
         int visualStart = 0;

         int i;
         for(i = 0; i < bidi.runCount; ++i) {
            int length = runs[i].limit - visualStart;
            int offset = logicalIndex - runs[i].start;
            if(offset >= 0 && offset < length) {
               if(runs[i].isEvenRun()) {
                  visualIndex = visualStart + offset;
               } else {
                  visualIndex = visualStart + length - offset - 1;
               }
               break;
            }

            visualStart += length;
         }

         if(i >= bidi.runCount) {
            return -1;
         }
      }

      if(bidi.insertPoints.size > 0) {
         BidiRun[] runs = bidi.runs;
         int visualStart = 0;
         int markFound = 0;
         int i = 0;

         while(true) {
            int length = runs[i].limit - visualStart;
            int insertRemove = runs[i].insertRemove;
            if((insertRemove & 5) > 0) {
               ++markFound;
            }

            if(visualIndex < runs[i].limit) {
               return visualIndex + markFound;
            }

            if((insertRemove & 10) > 0) {
               ++markFound;
            }

            ++i;
            visualStart += length;
         }
      } else if(bidi.controlCount <= 0) {
         return visualIndex;
      } else {
         BidiRun[] runs = bidi.runs;
         int visualStart = 0;
         int controlFound = 0;
         char uchar = bidi.text[logicalIndex];
         if(Bidi.IsBidiControlChar(uchar)) {
            return -1;
         } else {
            int i = 0;

            while(true) {
               int length = runs[i].limit - visualStart;
               int insertRemove = runs[i].insertRemove;
               if(visualIndex < runs[i].limit) {
                  if(insertRemove == 0) {
                     return visualIndex - controlFound;
                  } else {
                     int start;
                     int limit;
                     if(runs[i].isEvenRun()) {
                        start = runs[i].start;
                        limit = logicalIndex;
                     } else {
                        start = logicalIndex + 1;
                        limit = runs[i].start + length;
                     }

                     for(int j = start; j < limit; ++j) {
                        uchar = bidi.text[j];
                        if(Bidi.IsBidiControlChar(uchar)) {
                           ++controlFound;
                        }
                     }

                     return visualIndex - controlFound;
                  }
               }

               controlFound -= insertRemove;
               ++i;
               visualStart += length;
            }
         }
      }
   }

   static int getLogicalIndex(Bidi bidi, int visualIndex) {
      BidiRun[] runs = bidi.runs;
      int runCount = bidi.runCount;
      if(bidi.insertPoints.size > 0) {
         int markFound = 0;
         int visualStart = 0;
         int i = 0;

         while(true) {
            int length = runs[i].limit - visualStart;
            int insertRemove = runs[i].insertRemove;
            if((insertRemove & 5) > 0) {
               if(visualIndex <= visualStart + markFound) {
                  return -1;
               }

               ++markFound;
            }

            if(visualIndex < runs[i].limit + markFound) {
               visualIndex -= markFound;
               break;
            }

            if((insertRemove & 10) > 0) {
               if(visualIndex == visualStart + length + markFound) {
                  return -1;
               }

               ++markFound;
            }

            ++i;
            visualStart += length;
         }
      } else if(bidi.controlCount > 0) {
         int controlFound = 0;
         int visualStart = 0;
         int i = 0;

         while(true) {
            int length = runs[i].limit - visualStart;
            int insertRemove = runs[i].insertRemove;
            if(visualIndex < runs[i].limit - controlFound + insertRemove) {
               if(insertRemove == 0) {
                  visualIndex += controlFound;
               } else {
                  int logicalStart = runs[i].start;
                  boolean evenRun = runs[i].isEvenRun();
                  int logicalEnd = logicalStart + length - 1;

                  for(int j = 0; j < length; ++j) {
                     int k = evenRun?logicalStart + j:logicalEnd - j;
                     char uchar = bidi.text[k];
                     if(Bidi.IsBidiControlChar(uchar)) {
                        ++controlFound;
                     }

                     if(visualIndex + controlFound == visualStart + j) {
                        break;
                     }
                  }

                  visualIndex += controlFound;
               }
               break;
            }

            controlFound -= insertRemove;
            ++i;
            visualStart += length;
         }
      }

      int i;
      if(runCount <= 10) {
         for(i = 0; visualIndex >= runs[i].limit; ++i) {
            ;
         }
      } else {
         int begin = 0;
         int limit = runCount;

         while(true) {
            i = begin + limit >>> 1;
            if(visualIndex >= runs[i].limit) {
               begin = i + 1;
            } else {
               if(i == 0 || visualIndex >= runs[i - 1].limit) {
                  break;
               }

               limit = i;
            }
         }
      }

      int start = runs[i].start;
      if(runs[i].isEvenRun()) {
         if(i > 0) {
            visualIndex -= runs[i - 1].limit;
         }

         return start + visualIndex;
      } else {
         return start + runs[i].limit - visualIndex - 1;
      }
   }

   static int[] getLogicalMap(Bidi bidi) {
      BidiRun[] runs = bidi.runs;
      int[] indexMap = new int[bidi.length];
      if(bidi.length > bidi.resultLength) {
         Arrays.fill(indexMap, -1);
      }

      int visualStart = 0;

      for(int j = 0; j < bidi.runCount; ++j) {
         int logicalStart = runs[j].start;
         int visualLimit = runs[j].limit;
         if(runs[j].isEvenRun()) {
            while(true) {
               indexMap[logicalStart++] = visualStart++;
               if(visualStart >= visualLimit) {
                  break;
               }
            }
         } else {
            logicalStart = logicalStart + (visualLimit - visualStart);

            while(true) {
               --logicalStart;
               indexMap[logicalStart] = visualStart++;
               if(visualStart >= visualLimit) {
                  break;
               }
            }
         }
      }

      if(bidi.insertPoints.size > 0) {
         int markFound = 0;
         int runCount = bidi.runCount;
         runs = bidi.runs;
         visualStart = 0;

         int length;
         for(int i = 0; i < runCount; visualStart += length) {
            length = runs[i].limit - visualStart;
            int insertRemove = runs[i].insertRemove;
            if((insertRemove & 5) > 0) {
               ++markFound;
            }

            if(markFound > 0) {
               int logicalStart = runs[i].start;
               int logicalLimit = logicalStart + length;

               for(int j = logicalStart; j < logicalLimit; ++j) {
                  indexMap[j] += markFound;
               }
            }

            if((insertRemove & 10) > 0) {
               ++markFound;
            }

            ++i;
         }
      } else if(bidi.controlCount > 0) {
         int controlFound = 0;
         int runCount = bidi.runCount;
         runs = bidi.runs;
         visualStart = 0;

         int length;
         for(int i = 0; i < runCount; visualStart += length) {
            length = runs[i].limit - visualStart;
            int insertRemove = runs[i].insertRemove;
            if(controlFound - insertRemove != 0) {
               int logicalStart = runs[i].start;
               boolean evenRun = runs[i].isEvenRun();
               int logicalLimit = logicalStart + length;
               if(insertRemove == 0) {
                  for(int j = logicalStart; j < logicalLimit; ++j) {
                     indexMap[j] -= controlFound;
                  }
               } else {
                  for(int j = 0; j < length; ++j) {
                     int k = evenRun?logicalStart + j:logicalLimit - j - 1;
                     char uchar = bidi.text[k];
                     if(Bidi.IsBidiControlChar(uchar)) {
                        ++controlFound;
                        indexMap[k] = -1;
                     } else {
                        indexMap[k] -= controlFound;
                     }
                  }
               }
            }

            ++i;
         }
      }

      return indexMap;
   }

   static int[] getVisualMap(Bidi bidi) {
      BidiRun[] runs = bidi.runs;
      int allocLength = bidi.length > bidi.resultLength?bidi.length:bidi.resultLength;
      int[] indexMap = new int[allocLength];
      int visualStart = 0;
      int idx = 0;

      for(int j = 0; j < bidi.runCount; ++j) {
         int logicalStart = runs[j].start;
         int visualLimit = runs[j].limit;
         if(runs[j].isEvenRun()) {
            while(true) {
               indexMap[idx++] = logicalStart++;
               ++visualStart;
               if(visualStart >= visualLimit) {
                  break;
               }
            }
         } else {
            logicalStart = logicalStart + (visualLimit - visualStart);

            while(true) {
               int var10001 = idx++;
               --logicalStart;
               indexMap[var10001] = logicalStart;
               ++visualStart;
               if(visualStart >= visualLimit) {
                  break;
               }
            }
         }
      }

      if(bidi.insertPoints.size > 0) {
         int markFound = 0;
         int runCount = bidi.runCount;
         runs = bidi.runs;

         for(int i = 0; i < runCount; ++i) {
            int insertRemove = runs[i].insertRemove;
            if((insertRemove & 5) > 0) {
               ++markFound;
            }

            if((insertRemove & 10) > 0) {
               ++markFound;
            }
         }

         int k = bidi.resultLength;

         for(int length = runCount - 1; length >= 0 && markFound > 0; --length) {
            int insertRemove = runs[length].insertRemove;
            if((insertRemove & 10) > 0) {
               --k;
               indexMap[k] = -1;
               --markFound;
            }

            visualStart = length > 0?runs[length - 1].limit:0;

            for(int j = runs[length].limit - 1; j >= visualStart && markFound > 0; --j) {
               --k;
               indexMap[k] = indexMap[j];
            }

            if((insertRemove & 5) > 0) {
               --k;
               indexMap[k] = -1;
               --markFound;
            }
         }
      } else if(bidi.controlCount > 0) {
         int runCount = bidi.runCount;
         runs = bidi.runs;
         visualStart = 0;
         int k = 0;

         int length;
         for(int i = 0; i < runCount; visualStart += length) {
            length = runs[i].limit - visualStart;
            int insertRemove = runs[i].insertRemove;
            if(insertRemove == 0 && k == visualStart) {
               k += length;
            } else if(insertRemove == 0) {
               int visualLimit = runs[i].limit;

               for(int j = visualStart; j < visualLimit; ++j) {
                  indexMap[k++] = indexMap[j];
               }
            } else {
               int logicalStart = runs[i].start;
               boolean evenRun = runs[i].isEvenRun();
               int logicalEnd = logicalStart + length - 1;

               for(int j = 0; j < length; ++j) {
                  int m = evenRun?logicalStart + j:logicalEnd - j;
                  char uchar = bidi.text[m];
                  if(!Bidi.IsBidiControlChar(uchar)) {
                     indexMap[k++] = m;
                  }
               }
            }

            ++i;
         }
      }

      if(allocLength == bidi.resultLength) {
         return indexMap;
      } else {
         int[] newMap = new int[bidi.resultLength];
         System.arraycopy(indexMap, 0, newMap, 0, bidi.resultLength);
         return newMap;
      }
   }

   static int[] invertMap(int[] srcMap) {
      int srcLength = srcMap.length;
      int destLength = -1;
      int count = 0;

      for(int i = 0; i < srcLength; ++i) {
         int srcEntry = srcMap[i];
         if(srcEntry > destLength) {
            destLength = srcEntry;
         }

         if(srcEntry >= 0) {
            ++count;
         }
      }

      ++destLength;
      int[] destMap = new int[destLength];
      if(count < destLength) {
         Arrays.fill(destMap, -1);
      }

      for(int var8 = 0; var8 < srcLength; ++var8) {
         int srcEntry = srcMap[var8];
         if(srcEntry >= 0) {
            destMap[srcEntry] = var8;
         }
      }

      return destMap;
   }
}
