package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.AbstractFileFilter;

public class WildcardFileFilter extends AbstractFileFilter implements Serializable {
   private final String[] wildcards;
   private final IOCase caseSensitivity;

   public WildcardFileFilter(String wildcard) {
      this((String)wildcard, (IOCase)null);
   }

   public WildcardFileFilter(String wildcard, IOCase caseSensitivity) {
      if(wildcard == null) {
         throw new IllegalArgumentException("The wildcard must not be null");
      } else {
         this.wildcards = new String[]{wildcard};
         this.caseSensitivity = caseSensitivity == null?IOCase.SENSITIVE:caseSensitivity;
      }
   }

   public WildcardFileFilter(String[] wildcards) {
      this((String[])wildcards, (IOCase)null);
   }

   public WildcardFileFilter(String[] wildcards, IOCase caseSensitivity) {
      if(wildcards == null) {
         throw new IllegalArgumentException("The wildcard array must not be null");
      } else {
         this.wildcards = new String[wildcards.length];
         System.arraycopy(wildcards, 0, this.wildcards, 0, wildcards.length);
         this.caseSensitivity = caseSensitivity == null?IOCase.SENSITIVE:caseSensitivity;
      }
   }

   public WildcardFileFilter(List wildcards) {
      this((List)wildcards, (IOCase)null);
   }

   public WildcardFileFilter(List wildcards, IOCase caseSensitivity) {
      if(wildcards == null) {
         throw new IllegalArgumentException("The wildcard list must not be null");
      } else {
         this.wildcards = (String[])wildcards.toArray(new String[wildcards.size()]);
         this.caseSensitivity = caseSensitivity == null?IOCase.SENSITIVE:caseSensitivity;
      }
   }

   public boolean accept(File dir, String name) {
      for(String wildcard : this.wildcards) {
         if(FilenameUtils.wildcardMatch(name, wildcard, this.caseSensitivity)) {
            return true;
         }
      }

      return false;
   }

   public boolean accept(File file) {
      String name = file.getName();

      for(String wildcard : this.wildcards) {
         if(FilenameUtils.wildcardMatch(name, wildcard, this.caseSensitivity)) {
            return true;
         }
      }

      return false;
   }

   public String toString() {
      StringBuilder buffer = new StringBuilder();
      buffer.append(super.toString());
      buffer.append("(");
      if(this.wildcards != null) {
         for(int i = 0; i < this.wildcards.length; ++i) {
            if(i > 0) {
               buffer.append(",");
            }

            buffer.append(this.wildcards[i]);
         }
      }

      buffer.append(")");
      return buffer.toString();
   }
}
