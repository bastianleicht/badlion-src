package org.apache.commons.io.monitor;

import java.io.File;
import java.io.Serializable;

public class FileEntry implements Serializable {
   static final FileEntry[] EMPTY_ENTRIES = new FileEntry[0];
   private final FileEntry parent;
   private FileEntry[] children;
   private final File file;
   private String name;
   private boolean exists;
   private boolean directory;
   private long lastModified;
   private long length;

   public FileEntry(File file) {
      this((FileEntry)null, file);
   }

   public FileEntry(FileEntry parent, File file) {
      if(file == null) {
         throw new IllegalArgumentException("File is missing");
      } else {
         this.file = file;
         this.parent = parent;
         this.name = file.getName();
      }
   }

   public boolean refresh(File file) {
      boolean origExists = this.exists;
      long origLastModified = this.lastModified;
      boolean origDirectory = this.directory;
      long origLength = this.length;
      this.name = file.getName();
      this.exists = file.exists();
      this.directory = this.exists?file.isDirectory():false;
      this.lastModified = this.exists?file.lastModified():0L;
      this.length = this.exists && !this.directory?file.length():0L;
      return this.exists != origExists || this.lastModified != origLastModified || this.directory != origDirectory || this.length != origLength;
   }

   public FileEntry newChildInstance(File file) {
      return new FileEntry(this, file);
   }

   public FileEntry getParent() {
      return this.parent;
   }

   public int getLevel() {
      return this.parent == null?0:this.parent.getLevel() + 1;
   }

   public FileEntry[] getChildren() {
      return this.children != null?this.children:EMPTY_ENTRIES;
   }

   public void setChildren(FileEntry[] children) {
      this.children = children;
   }

   public File getFile() {
      return this.file;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public long getLastModified() {
      return this.lastModified;
   }

   public void setLastModified(long lastModified) {
      this.lastModified = lastModified;
   }

   public long getLength() {
      return this.length;
   }

   public void setLength(long length) {
      this.length = length;
   }

   public boolean isExists() {
      return this.exists;
   }

   public void setExists(boolean exists) {
      this.exists = exists;
   }

   public boolean isDirectory() {
      return this.directory;
   }

   public void setDirectory(boolean directory) {
      this.directory = directory;
   }
}
