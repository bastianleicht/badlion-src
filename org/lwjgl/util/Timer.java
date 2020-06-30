package org.lwjgl.util;

import org.lwjgl.Sys;

public class Timer {
   private static long resolution = Sys.getTimerResolution();
   private static final int QUERY_INTERVAL = 50;
   private static int queryCount;
   private static long currentTime;
   private long startTime;
   private long lastTime;
   private boolean paused;

   public Timer() {
      this.reset();
      this.resume();
   }

   public float getTime() {
      if(!this.paused) {
         this.lastTime = currentTime - this.startTime;
      }

      return (float)((double)this.lastTime / (double)resolution);
   }

   public boolean isPaused() {
      return this.paused;
   }

   public void pause() {
      this.paused = true;
   }

   public void reset() {
      this.set(0.0F);
   }

   public void resume() {
      this.paused = false;
      this.startTime = currentTime - this.lastTime;
   }

   public void set(float newTime) {
      long newTimeInTicks = (long)((double)newTime * (double)resolution);
      this.startTime = currentTime - newTimeInTicks;
      this.lastTime = newTimeInTicks;
   }

   public static void tick() {
      currentTime = Sys.getTime();
      ++queryCount;
      if(queryCount > 50) {
         queryCount = 0;
         resolution = Sys.getTimerResolution();
      }

   }

   public String toString() {
      return "Timer[Time=" + this.getTime() + ", Paused=" + this.paused + "]";
   }

   static {
      tick();
   }
}
