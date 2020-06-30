package com.ibm.icu.impl;

public class ICURWLock {
   private Object writeLock = new Object();
   private Object readLock = new Object();
   private int wwc;
   private int rc;
   private int wrc;
   private ICURWLock.Stats stats = new ICURWLock.Stats();
   private static final int NOTIFY_NONE = 0;
   private static final int NOTIFY_WRITERS = 1;
   private static final int NOTIFY_READERS = 2;

   public synchronized ICURWLock.Stats resetStats() {
      ICURWLock.Stats result = this.stats;
      this.stats = new ICURWLock.Stats();
      return result;
   }

   public synchronized ICURWLock.Stats clearStats() {
      ICURWLock.Stats result = this.stats;
      this.stats = null;
      return result;
   }

   public synchronized ICURWLock.Stats getStats() {
      return this.stats == null?null:new ICURWLock.Stats(this.stats);
   }

   private synchronized boolean gotRead() {
      ++this.rc;
      if(this.stats != null) {
         ++this.stats._rc;
         if(this.rc > 1) {
            ++this.stats._mrc;
         }
      }

      return true;
   }

   private synchronized boolean getRead() {
      if(this.rc >= 0 && this.wwc == 0) {
         return this.gotRead();
      } else {
         ++this.wrc;
         return false;
      }
   }

   private synchronized boolean retryRead() {
      if(this.stats != null) {
         ++this.stats._wrc;
      }

      if(this.rc >= 0 && this.wwc == 0) {
         --this.wrc;
         return this.gotRead();
      } else {
         return false;
      }
   }

   private synchronized boolean finishRead() {
      if(this.rc <= 0) {
         throw new IllegalStateException("no current reader to release");
      } else {
         return 0 == --this.rc && this.wwc > 0;
      }
   }

   private synchronized boolean gotWrite() {
      this.rc = -1;
      if(this.stats != null) {
         ++this.stats._wc;
      }

      return true;
   }

   private synchronized boolean getWrite() {
      if(this.rc == 0) {
         return this.gotWrite();
      } else {
         ++this.wwc;
         return false;
      }
   }

   private synchronized boolean retryWrite() {
      if(this.stats != null) {
         ++this.stats._wwc;
      }

      if(this.rc == 0) {
         --this.wwc;
         return this.gotWrite();
      } else {
         return false;
      }
   }

   private synchronized int finishWrite() {
      if(this.rc < 0) {
         this.rc = 0;
         return this.wwc > 0?1:(this.wrc > 0?2:0);
      } else {
         throw new IllegalStateException("no current writer to release");
      }
   }

   public void acquireRead() {
      if(!this.getRead()) {
         while(true) {
            try {
               synchronized(this.readLock) {
                  this.readLock.wait();
               }

               if(this.retryRead()) {
                  return;
               }
            } catch (InterruptedException var4) {
               ;
            }
         }
      }
   }

   public void releaseRead() {
      if(this.finishRead()) {
         synchronized(this.writeLock) {
            this.writeLock.notify();
         }
      }

   }

   public void acquireWrite() {
      if(!this.getWrite()) {
         while(true) {
            try {
               synchronized(this.writeLock) {
                  this.writeLock.wait();
               }

               if(this.retryWrite()) {
                  return;
               }
            } catch (InterruptedException var4) {
               ;
            }
         }
      }
   }

   public void releaseWrite() {
      switch(this.finishWrite()) {
      case 0:
      default:
         break;
      case 1:
         synchronized(this.writeLock) {
            this.writeLock.notify();
            break;
         }
      case 2:
         synchronized(this.readLock) {
            this.readLock.notifyAll();
         }
      }

   }

   public static final class Stats {
      public int _rc;
      public int _mrc;
      public int _wrc;
      public int _wc;
      public int _wwc;

      private Stats() {
      }

      private Stats(int rc, int mrc, int wrc, int wc, int wwc) {
         this._rc = rc;
         this._mrc = mrc;
         this._wrc = wrc;
         this._wc = wc;
         this._wwc = wwc;
      }

      private Stats(ICURWLock.Stats rhs) {
         this(rhs._rc, rhs._mrc, rhs._wrc, rhs._wc, rhs._wwc);
      }

      public String toString() {
         return " rc: " + this._rc + " mrc: " + this._mrc + " wrc: " + this._wrc + " wc: " + this._wc + " wwc: " + this._wwc;
      }
   }
}
