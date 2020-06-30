package org.apache.commons.compress.archivers.sevenz;

import java.util.BitSet;
import org.apache.commons.compress.archivers.sevenz.Folder;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.StreamMap;
import org.apache.commons.compress.archivers.sevenz.SubStreamsInfo;

class Archive {
   long packPos;
   long[] packSizes;
   BitSet packCrcsDefined;
   long[] packCrcs;
   Folder[] folders;
   SubStreamsInfo subStreamsInfo;
   SevenZArchiveEntry[] files;
   StreamMap streamMap;
}
