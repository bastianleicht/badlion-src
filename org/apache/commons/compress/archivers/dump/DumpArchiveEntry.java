package org.apache.commons.compress.archivers.dump;

import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.dump.DumpArchiveConstants;
import org.apache.commons.compress.archivers.dump.DumpArchiveSummary;
import org.apache.commons.compress.archivers.dump.DumpArchiveUtil;

public class DumpArchiveEntry implements ArchiveEntry {
   private String name;
   private DumpArchiveEntry.TYPE type = DumpArchiveEntry.TYPE.UNKNOWN;
   private int mode;
   private Set permissions = Collections.emptySet();
   private long size;
   private long atime;
   private long mtime;
   private int uid;
   private int gid;
   private final DumpArchiveSummary summary = null;
   private final DumpArchiveEntry.TapeSegmentHeader header = new DumpArchiveEntry.TapeSegmentHeader();
   private String simpleName;
   private String originalName;
   private int volume;
   private long offset;
   private int ino;
   private int nlink;
   private long ctime;
   private int generation;
   private boolean isDeleted;

   public DumpArchiveEntry() {
   }

   public DumpArchiveEntry(String name, String simpleName) {
      this.setName(name);
      this.simpleName = simpleName;
   }

   protected DumpArchiveEntry(String name, String simpleName, int ino, DumpArchiveEntry.TYPE type) {
      this.setType(type);
      this.setName(name);
      this.simpleName = simpleName;
      this.ino = ino;
      this.offset = 0L;
   }

   public String getSimpleName() {
      return this.simpleName;
   }

   protected void setSimpleName(String simpleName) {
      this.simpleName = simpleName;
   }

   public int getIno() {
      return this.header.getIno();
   }

   public int getNlink() {
      return this.nlink;
   }

   public void setNlink(int nlink) {
      this.nlink = nlink;
   }

   public Date getCreationTime() {
      return new Date(this.ctime);
   }

   public void setCreationTime(Date ctime) {
      this.ctime = ctime.getTime();
   }

   public int getGeneration() {
      return this.generation;
   }

   public void setGeneration(int generation) {
      this.generation = generation;
   }

   public boolean isDeleted() {
      return this.isDeleted;
   }

   public void setDeleted(boolean isDeleted) {
      this.isDeleted = isDeleted;
   }

   public long getOffset() {
      return this.offset;
   }

   public void setOffset(long offset) {
      this.offset = offset;
   }

   public int getVolume() {
      return this.volume;
   }

   public void setVolume(int volume) {
      this.volume = volume;
   }

   public DumpArchiveConstants.SEGMENT_TYPE getHeaderType() {
      return this.header.getType();
   }

   public int getHeaderCount() {
      return this.header.getCount();
   }

   public int getHeaderHoles() {
      return this.header.getHoles();
   }

   public boolean isSparseRecord(int idx) {
      return (this.header.getCdata(idx) & 1) == 0;
   }

   public int hashCode() {
      return this.ino;
   }

   public boolean equals(Object o) {
      if(o == this) {
         return true;
      } else if(o != null && o.getClass().equals(this.getClass())) {
         DumpArchiveEntry rhs = (DumpArchiveEntry)o;
         return this.header != null && rhs.header != null?(this.ino != rhs.ino?false:(this.summary != null || rhs.summary == null) && (this.summary == null || this.summary.equals(rhs.summary))):false;
      } else {
         return false;
      }
   }

   public String toString() {
      return this.getName();
   }

   static DumpArchiveEntry parse(byte[] buffer) {
      DumpArchiveEntry entry = new DumpArchiveEntry();
      DumpArchiveEntry.TapeSegmentHeader header = entry.header;
      header.type = DumpArchiveConstants.SEGMENT_TYPE.find(DumpArchiveUtil.convert32(buffer, 0));
      header.volume = DumpArchiveUtil.convert32(buffer, 12);
      entry.ino = header.ino = DumpArchiveUtil.convert32(buffer, 20);
      int m = DumpArchiveUtil.convert16(buffer, 32);
      entry.setType(DumpArchiveEntry.TYPE.find(m >> 12 & 15));
      entry.setMode(m);
      entry.nlink = DumpArchiveUtil.convert16(buffer, 34);
      entry.setSize(DumpArchiveUtil.convert64(buffer, 40));
      long t = 1000L * (long)DumpArchiveUtil.convert32(buffer, 48) + (long)(DumpArchiveUtil.convert32(buffer, 52) / 1000);
      entry.setAccessTime(new Date(t));
      t = 1000L * (long)DumpArchiveUtil.convert32(buffer, 56) + (long)(DumpArchiveUtil.convert32(buffer, 60) / 1000);
      entry.setLastModifiedDate(new Date(t));
      t = 1000L * (long)DumpArchiveUtil.convert32(buffer, 64) + (long)(DumpArchiveUtil.convert32(buffer, 68) / 1000);
      entry.ctime = t;
      entry.generation = DumpArchiveUtil.convert32(buffer, 140);
      entry.setUserId(DumpArchiveUtil.convert32(buffer, 144));
      entry.setGroupId(DumpArchiveUtil.convert32(buffer, 148));
      header.count = DumpArchiveUtil.convert32(buffer, 160);
      header.holes = 0;

      for(int i = 0; i < 512 && i < header.count; ++i) {
         if(buffer[164 + i] == 0) {
            header.holes++;
         }
      }

      System.arraycopy(buffer, 164, header.cdata, 0, 512);
      entry.volume = header.getVolume();
      return entry;
   }

   void update(byte[] buffer) {
      this.header.volume = DumpArchiveUtil.convert32(buffer, 16);
      this.header.count = DumpArchiveUtil.convert32(buffer, 160);
      this.header.holes = 0;

      for(int i = 0; i < 512 && i < this.header.count; ++i) {
         if(buffer[164 + i] == 0) {
            this.header.holes++;
         }
      }

      System.arraycopy(buffer, 164, this.header.cdata, 0, 512);
   }

   public String getName() {
      return this.name;
   }

   String getOriginalName() {
      return this.originalName;
   }

   public final void setName(String name) {
      this.originalName = name;
      if(name != null) {
         if(this.isDirectory() && !name.endsWith("/")) {
            name = name + "/";
         }

         if(name.startsWith("./")) {
            name = name.substring(2);
         }
      }

      this.name = name;
   }

   public Date getLastModifiedDate() {
      return new Date(this.mtime);
   }

   public boolean isDirectory() {
      return this.type == DumpArchiveEntry.TYPE.DIRECTORY;
   }

   public boolean isFile() {
      return this.type == DumpArchiveEntry.TYPE.FILE;
   }

   public boolean isSocket() {
      return this.type == DumpArchiveEntry.TYPE.SOCKET;
   }

   public boolean isChrDev() {
      return this.type == DumpArchiveEntry.TYPE.CHRDEV;
   }

   public boolean isBlkDev() {
      return this.type == DumpArchiveEntry.TYPE.BLKDEV;
   }

   public boolean isFifo() {
      return this.type == DumpArchiveEntry.TYPE.FIFO;
   }

   public DumpArchiveEntry.TYPE getType() {
      return this.type;
   }

   public void setType(DumpArchiveEntry.TYPE type) {
      this.type = type;
   }

   public int getMode() {
      return this.mode;
   }

   public void setMode(int mode) {
      this.mode = mode & 4095;
      this.permissions = DumpArchiveEntry.PERMISSION.find(mode);
   }

   public Set getPermissions() {
      return this.permissions;
   }

   public long getSize() {
      return this.isDirectory()?-1L:this.size;
   }

   long getEntrySize() {
      return this.size;
   }

   public void setSize(long size) {
      this.size = size;
   }

   public void setLastModifiedDate(Date mtime) {
      this.mtime = mtime.getTime();
   }

   public Date getAccessTime() {
      return new Date(this.atime);
   }

   public void setAccessTime(Date atime) {
      this.atime = atime.getTime();
   }

   public int getUserId() {
      return this.uid;
   }

   public void setUserId(int uid) {
      this.uid = uid;
   }

   public int getGroupId() {
      return this.gid;
   }

   public void setGroupId(int gid) {
      this.gid = gid;
   }

   public static enum PERMISSION {
      SETUID(2048),
      SETGUI(1024),
      STICKY(512),
      USER_READ(256),
      USER_WRITE(128),
      USER_EXEC(64),
      GROUP_READ(32),
      GROUP_WRITE(16),
      GROUP_EXEC(8),
      WORLD_READ(4),
      WORLD_WRITE(2),
      WORLD_EXEC(1);

      private int code;

      private PERMISSION(int code) {
         this.code = code;
      }

      public static Set find(int code) {
         Set<DumpArchiveEntry.PERMISSION> set = new HashSet();

         for(DumpArchiveEntry.PERMISSION p : values()) {
            if((code & p.code) == p.code) {
               set.add(p);
            }
         }

         if(set.isEmpty()) {
            return Collections.emptySet();
         } else {
            return EnumSet.copyOf(set);
         }
      }
   }

   public static enum TYPE {
      WHITEOUT(14),
      SOCKET(12),
      LINK(10),
      FILE(8),
      BLKDEV(6),
      DIRECTORY(4),
      CHRDEV(2),
      FIFO(1),
      UNKNOWN(15);

      private int code;

      private TYPE(int code) {
         this.code = code;
      }

      public static DumpArchiveEntry.TYPE find(int code) {
         DumpArchiveEntry.TYPE type = UNKNOWN;

         for(DumpArchiveEntry.TYPE t : values()) {
            if(code == t.code) {
               type = t;
            }
         }

         return type;
      }
   }

   static class TapeSegmentHeader {
      private DumpArchiveConstants.SEGMENT_TYPE type;
      private int volume;
      private int ino;
      private int count;
      private int holes;
      private final byte[] cdata = new byte[512];

      public DumpArchiveConstants.SEGMENT_TYPE getType() {
         return this.type;
      }

      public int getVolume() {
         return this.volume;
      }

      public int getIno() {
         return this.ino;
      }

      void setIno(int ino) {
         this.ino = ino;
      }

      public int getCount() {
         return this.count;
      }

      public int getHoles() {
         return this.holes;
      }

      public int getCdata(int idx) {
         return this.cdata[idx];
      }
   }
}
