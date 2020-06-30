package org.apache.http;

import java.util.Iterator;
import org.apache.http.Header;

public interface HeaderIterator extends Iterator {
   boolean hasNext();

   Header nextHeader();
}
