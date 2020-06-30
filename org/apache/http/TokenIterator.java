package org.apache.http;

import java.util.Iterator;

public interface TokenIterator extends Iterator {
   boolean hasNext();

   String nextToken();
}
