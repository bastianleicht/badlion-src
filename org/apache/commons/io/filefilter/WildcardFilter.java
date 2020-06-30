package org.apache.commons.io.filefilter;

import java.io.File;
import java.io.Serializable;
import java.util.List;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.AbstractFileFilter;

/** @deprecated */
@Deprecated
public class WildcardFilter extends AbstractFileFilter implements Serializable {
   private final String[] wildcards;

   public WildcardFilter(String wildcard) {
      if(wildcard == null) {
         throw new IllegalArgumentException("The wildcard must not be null");
      } else {
         this.wildcards = new String[]{wildcard};
      }
   }

   public WildcardFilter(String[] wildcards) {
      if(wildcards == null) {
         throw new IllegalArgumentException("The wildcard array must not be null");
      } else {
         this.wildcards = new String[wildcards.length];
         System.arraycopy(wildcards, 0, this.wildcards, 0, wildcards.length);
      }
   }

   public WildcardFilter(List wildcards) {
      if(wildcards == null) {
         throw new IllegalArgumentException("The wildcard list must not be null");
      } else {
         this.wildcards = (String[])wildcards.toArray(new String[wildcards.size()]);
      }
   }

   public boolean accept(File dir, String name) {
      if(dir != null && (new File(dir, name)).isDirectory()) {
         return false;
      } else {
         for(String wildcard : this.wildcards) {
            if(FilenameUtils.wildcardMatch(name, wildcard)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean accept(File file) {
      if(file.isDirectory()) {
         return false;
      } else {
         for(String wildcard : this.wildcards) {
            if(FilenameUtils.wildcardMatch(file.getName(), wildcard)) {
               return true;
            }
         }

         return false;
      }
   }
}
