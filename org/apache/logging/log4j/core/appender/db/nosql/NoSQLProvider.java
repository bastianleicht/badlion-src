package org.apache.logging.log4j.core.appender.db.nosql;

import org.apache.logging.log4j.core.appender.db.nosql.NoSQLConnection;

public interface NoSQLProvider {
   NoSQLConnection getConnection();

   String toString();
}
