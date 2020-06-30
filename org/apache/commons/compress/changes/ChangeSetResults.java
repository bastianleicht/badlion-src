package org.apache.commons.compress.changes;

import java.util.ArrayList;
import java.util.List;

public class ChangeSetResults {
   private final List addedFromChangeSet = new ArrayList();
   private final List addedFromStream = new ArrayList();
   private final List deleted = new ArrayList();

   void deleted(String fileName) {
      this.deleted.add(fileName);
   }

   void addedFromStream(String fileName) {
      this.addedFromStream.add(fileName);
   }

   void addedFromChangeSet(String fileName) {
      this.addedFromChangeSet.add(fileName);
   }

   public List getAddedFromChangeSet() {
      return this.addedFromChangeSet;
   }

   public List getAddedFromStream() {
      return this.addedFromStream;
   }

   public List getDeleted() {
      return this.deleted;
   }

   boolean hasBeenAdded(String filename) {
      return this.addedFromChangeSet.contains(filename) || this.addedFromStream.contains(filename);
   }
}
