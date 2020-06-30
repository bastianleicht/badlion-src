package org.apache.logging.log4j.core;

public interface LifeCycle {
   void start();

   void stop();

   boolean isStarted();
}
