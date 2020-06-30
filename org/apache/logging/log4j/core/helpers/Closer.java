package org.apache.logging.log4j.core.helpers;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class Closer {
   public static void closeSilent(Closeable closeable) {
      try {
         if(closeable != null) {
            closeable.close();
         }
      } catch (Exception var2) {
         ;
      }

   }

   public static void close(Closeable closeable) throws IOException {
      if(closeable != null) {
         closeable.close();
      }

   }

   public static void closeSilent(Statement statement) {
      try {
         if(statement != null) {
            statement.close();
         }
      } catch (Exception var2) {
         ;
      }

   }

   public static void close(Statement statement) throws SQLException {
      if(statement != null) {
         statement.close();
      }

   }

   public static void closeSilent(Connection connection) {
      try {
         if(connection != null) {
            connection.close();
         }
      } catch (Exception var2) {
         ;
      }

   }

   public static void close(Connection connection) throws SQLException {
      if(connection != null) {
         connection.close();
      }

   }
}
