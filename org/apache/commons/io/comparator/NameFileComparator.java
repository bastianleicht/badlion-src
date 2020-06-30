package org.apache.commons.io.comparator;

import java.io.File;
import java.io.Serializable;
import java.util.Comparator;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.comparator.AbstractFileComparator;
import org.apache.commons.io.comparator.ReverseComparator;

public class NameFileComparator extends AbstractFileComparator implements Serializable {
   public static final Comparator NAME_COMPARATOR = new NameFileComparator();
   public static final Comparator NAME_REVERSE = new ReverseComparator(NAME_COMPARATOR);
   public static final Comparator NAME_INSENSITIVE_COMPARATOR = new NameFileComparator(IOCase.INSENSITIVE);
   public static final Comparator NAME_INSENSITIVE_REVERSE = new ReverseComparator(NAME_INSENSITIVE_COMPARATOR);
   public static final Comparator NAME_SYSTEM_COMPARATOR = new NameFileComparator(IOCase.SYSTEM);
   public static final Comparator NAME_SYSTEM_REVERSE = new ReverseComparator(NAME_SYSTEM_COMPARATOR);
   private final IOCase caseSensitivity;

   public NameFileComparator() {
      this.caseSensitivity = IOCase.SENSITIVE;
   }

   public NameFileComparator(IOCase caseSensitivity) {
      this.caseSensitivity = caseSensitivity == null?IOCase.SENSITIVE:caseSensitivity;
   }

   public int compare(File file1, File file2) {
      return this.caseSensitivity.checkCompareTo(file1.getName(), file2.getName());
   }

   public String toString() {
      return super.toString() + "[caseSensitivity=" + this.caseSensitivity + "]";
   }
}
