package org.apache.http.cookie;

import java.io.Serializable;
import java.util.Comparator;
import org.apache.http.annotation.Immutable;
import org.apache.http.cookie.Cookie;

@Immutable
public class CookiePathComparator implements Serializable, Comparator {
   private static final long serialVersionUID = 7523645369616405818L;

   private String normalizePath(Cookie cookie) {
      String path = cookie.getPath();
      if(path == null) {
         path = "/";
      }

      if(!path.endsWith("/")) {
         path = path + '/';
      }

      return path;
   }

   public int compare(Cookie c1, Cookie c2) {
      String path1 = this.normalizePath(c1);
      String path2 = this.normalizePath(c2);
      return path1.equals(path2)?0:(path1.startsWith(path2)?-1:(path2.startsWith(path1)?1:0));
   }
}
