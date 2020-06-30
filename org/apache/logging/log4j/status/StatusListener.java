package org.apache.logging.log4j.status;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.status.StatusData;

public interface StatusListener {
   void log(StatusData var1);

   Level getStatusLevel();
}
