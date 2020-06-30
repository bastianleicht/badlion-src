package org.apache.logging.log4j.core.appender.db.jdbc;

import java.sql.Connection;
import java.sql.SQLException;

public interface ConnectionSource {
   Connection getConnection() throws SQLException;

   String toString();
}
