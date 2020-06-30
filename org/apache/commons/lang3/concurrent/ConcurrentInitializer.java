package org.apache.commons.lang3.concurrent;

import org.apache.commons.lang3.concurrent.ConcurrentException;

public interface ConcurrentInitializer {
   Object get() throws ConcurrentException;
}
