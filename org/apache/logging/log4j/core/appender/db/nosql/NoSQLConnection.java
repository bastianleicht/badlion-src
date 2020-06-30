package org.apache.logging.log4j.core.appender.db.nosql;

import java.io.Closeable;
import org.apache.logging.log4j.core.appender.db.nosql.NoSQLObject;

public interface NoSQLConnection extends Closeable {
   NoSQLObject createObject();

   NoSQLObject[] createList(int var1);

   void insertObject(NoSQLObject var1);

   void close();

   boolean isClosed();
}
