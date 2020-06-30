package org.apache.http.protocol;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import org.apache.http.annotation.GuardedBy;
import org.apache.http.annotation.ThreadSafe;

@ThreadSafe
public class HttpDateGenerator {
   public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
   public static final TimeZone GMT = TimeZone.getTimeZone("GMT");
   @GuardedBy("this")
   private final DateFormat dateformat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US);
   @GuardedBy("this")
   private long dateAsLong = 0L;
   @GuardedBy("this")
   private String dateAsText = null;

   public HttpDateGenerator() {
      this.dateformat.setTimeZone(GMT);
   }

   public synchronized String getCurrentDate() {
      long now = System.currentTimeMillis();
      if(now - this.dateAsLong > 1000L) {
         this.dateAsText = this.dateformat.format(new Date(now));
         this.dateAsLong = now;
      }

      return this.dateAsText;
   }
}
