package org.apache.http.impl.cookie;

import java.util.Date;
import java.util.TimeZone;
import org.apache.http.annotation.Immutable;
import org.apache.http.impl.cookie.DateParseException;

/** @deprecated */
@Deprecated
@Immutable
public final class DateUtils {
   public static final String PATTERN_RFC1123 = "EEE, dd MMM yyyy HH:mm:ss zzz";
   public static final String PATTERN_RFC1036 = "EEE, dd-MMM-yy HH:mm:ss zzz";
   public static final String PATTERN_ASCTIME = "EEE MMM d HH:mm:ss yyyy";
   public static final TimeZone GMT = TimeZone.getTimeZone("GMT");

   public static Date parseDate(String dateValue) throws DateParseException {
      return parseDate(dateValue, (String[])null, (Date)null);
   }

   public static Date parseDate(String dateValue, String[] dateFormats) throws DateParseException {
      return parseDate(dateValue, dateFormats, (Date)null);
   }

   public static Date parseDate(String dateValue, String[] dateFormats, Date startDate) throws DateParseException {
      Date d = org.apache.http.client.utils.DateUtils.parseDate(dateValue, dateFormats, startDate);
      if(d == null) {
         throw new DateParseException("Unable to parse the date " + dateValue);
      } else {
         return d;
      }
   }

   public static String formatDate(Date date) {
      return org.apache.http.client.utils.DateUtils.formatDate(date);
   }

   public static String formatDate(Date date, String pattern) {
      return org.apache.http.client.utils.DateUtils.formatDate(date, pattern);
   }
}
