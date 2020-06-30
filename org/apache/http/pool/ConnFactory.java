package org.apache.http.pool;

import java.io.IOException;

public interface ConnFactory {
   Object create(Object var1) throws IOException;
}
