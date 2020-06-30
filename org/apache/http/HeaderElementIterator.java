package org.apache.http;

import java.util.Iterator;
import org.apache.http.HeaderElement;

public interface HeaderElementIterator extends Iterator {
   boolean hasNext();

   HeaderElement nextElement();
}
