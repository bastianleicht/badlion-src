package org.apache.commons.compress.changes;

import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.changes.Change;

public final class ChangeSet {
   private final Set changes = new LinkedHashSet();

   public void delete(String filename) {
      this.addDeletion(new Change(filename, 1));
   }

   public void deleteDir(String dirName) {
      this.addDeletion(new Change(dirName, 4));
   }

   public void add(ArchiveEntry pEntry, InputStream pInput) {
      this.add(pEntry, pInput, true);
   }

   public void add(ArchiveEntry pEntry, InputStream pInput, boolean replace) {
      this.addAddition(new Change(pEntry, pInput, replace));
   }

   private void addAddition(Change pChange) {
      if(2 == pChange.type() && pChange.getInput() != null) {
         if(!this.changes.isEmpty()) {
            Iterator<Change> it = this.changes.iterator();

            while(it.hasNext()) {
               Change change = (Change)it.next();
               if(change.type() == 2 && change.getEntry() != null) {
                  ArchiveEntry entry = change.getEntry();
                  if(entry.equals(pChange.getEntry())) {
                     if(pChange.isReplaceMode()) {
                        it.remove();
                        this.changes.add(pChange);
                        return;
                     }

                     return;
                  }
               }
            }
         }

         this.changes.add(pChange);
      }
   }

   private void addDeletion(Change pChange) {
      if((1 == pChange.type() || 4 == pChange.type()) && pChange.targetFile() != null) {
         String source = pChange.targetFile();
         if(source != null && !this.changes.isEmpty()) {
            Iterator<Change> it = this.changes.iterator();

            while(it.hasNext()) {
               Change change = (Change)it.next();
               if(change.type() == 2 && change.getEntry() != null) {
                  String target = change.getEntry().getName();
                  if(target != null) {
                     if(1 == pChange.type() && source.equals(target)) {
                        it.remove();
                     } else if(4 == pChange.type() && target.matches(source + "/.*")) {
                        it.remove();
                     }
                  }
               }
            }
         }

         this.changes.add(pChange);
      }
   }

   Set getChanges() {
      return new LinkedHashSet(this.changes);
   }
}
