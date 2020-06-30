package org.apache.commons.compress.archivers.dump;

class Dirent {
   private final int ino;
   private final int parentIno;
   private final int type;
   private final String name;

   Dirent(int ino, int parentIno, int type, String name) {
      this.ino = ino;
      this.parentIno = parentIno;
      this.type = type;
      this.name = name;
   }

   int getIno() {
      return this.ino;
   }

   int getParentIno() {
      return this.parentIno;
   }

   int getType() {
      return this.type;
   }

   String getName() {
      return this.name;
   }

   public String toString() {
      return String.format("[%d]: %s", new Object[]{Integer.valueOf(this.ino), this.name});
   }
}
