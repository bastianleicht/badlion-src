package org.apache.commons.lang3.time;

import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public interface DateParser {
   Date parse(String var1) throws ParseException;

   Date parse(String var1, ParsePosition var2);

   String getPattern();

   TimeZone getTimeZone();

   Locale getLocale();

   Object parseObject(String var1) throws ParseException;

   Object parseObject(String var1, ParsePosition var2);
}
