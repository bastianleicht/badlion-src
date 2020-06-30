package org.apache.http.impl.conn.tsccm;

import org.apache.http.impl.conn.tsccm.WaitingThread;

/** @deprecated */
@Deprecated
public class WaitingThreadAborter {
   private WaitingThread waitingThread;
   private boolean aborted;

   public void abort() {
      this.aborted = true;
      if(this.waitingThread != null) {
         this.waitingThread.interrupt();
      }

   }

   public void setWaitingThread(WaitingThread waitingThread) {
      this.waitingThread = waitingThread;
      if(this.aborted) {
         waitingThread.interrupt();
      }

   }
}
