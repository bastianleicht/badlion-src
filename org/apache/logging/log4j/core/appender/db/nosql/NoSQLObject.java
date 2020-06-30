package org.apache.logging.log4j.core.appender.db.nosql;

public interface NoSQLObject {
   void set(String var1, Object var2);

   void set(String var1, NoSQLObject var2);

   void set(String var1, Object[] var2);

   void set(String var1, NoSQLObject[] var2);

   Object unwrap();
}
