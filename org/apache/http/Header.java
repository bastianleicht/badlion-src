package org.apache.http;

import org.apache.http.HeaderElement;
import org.apache.http.ParseException;

public interface Header {
   String getName();

   String getValue();

   HeaderElement[] getElements() throws ParseException;
}
