package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.AbstractFileFilter;

public class SuffixFileFilter extends AbstractFileFilter implements Serializable {
   private final String[] suffixes;
   private final IOCase caseSensitivity;

   public SuffixFileFilter(String suffix) {
      this(suffix, IOCase.SENSITIVE);
   }

   public SuffixFileFilter(String suffix, IOCase caseSensitivity) {
      if(suffix == null) {
         throw new IllegalArgumentException("The suffix must not be null");
      } else {
         this.suffixes = new String[]{suffix};
         this.caseSensitivity = caseSensitivity == null?IOCase.SENSITIVE:caseSensitivity;
      }
   }

   public SuffixFileFilter(String[] suffixes) {
      this(suffixes, IOCase.SENSITIVE);
   }

   public SuffixFileFilter(String[] suffixes, IOCase caseSensitivity) {
      if(suffixes == null) {
         throw new IllegalArgumentException("The array of suffixes must not be null");
      } else {
         this.suffixes = new String[suffixes.length];
         System.arraycopy(suffixes, 0, this.suffixes, 0, suffixes.length);
         this.caseSensitivity = caseSensitivity == null?IOCase.SENSITIVE:caseSensitivity;
      }
   }

   public SuffixFileFilter(List suffixes) {
      this(suffixes, IOCase.SENSITIVE);
   }

   public SuffixFileFilter(List suffixes, IOCase caseSensitivity) {
      if(suffixes == null) {
         throw new IllegalArgumentException("The list of suffixes must not be null");
      } else {
         this.suffixes = (String[])suffixes.toArray(new String[suffixes.size()]);
         this.caseSensitivity = caseSensitivity == null?IOCase.SENSITIVE:caseSensitivity;
      }
   }

   public boolean accept(File file) {
      String name = file.getName();

      for(String suffix : this.suffixes) {
         if(this.caseSensitivity.checkEndsWith(name, suffix)) {
            return true;
         }
      }

      return false;
   }

   public boolean accept(File file, String name) {
      for(String suffix : this.suffixes) {
         if(this.caseSensitivity.checkEndsWith(name, suffix)) {
            return true;
         }
      }

      return false;
   }

   public String toString() {
      StringBuilder buffer = new StringBuilder();
      buffer.append(super.toString());
      buffer.append("(");
      if(this.suffixes != null) {
         for(int i = 0; i < this.suffixes.length; ++i) {
            if(i > 0) {
               buffer.append(",");
            }

            buffer.append(this.suffixes[i]);
         }
      }

      buffer.append(")");
      return buffer.toString();
   }
}
