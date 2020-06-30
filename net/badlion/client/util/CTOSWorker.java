package net.badlion.client.util;

import java.util.concurrent.ConcurrentLinkedQueue;
import net.badlion.client.Wrapper;
import net.badlion.client.util.HashIndex;

public class CTOSWorker implements Runnable {
   private ConcurrentLinkedQueue clientToServerQueue = new ConcurrentLinkedQueue();

   public void addToQueue(HashIndex index) {
      this.clientToServerQueue.add(index);
   }

   public void run() {
      try {
         while(true) {
            if(this.clientToServerQueue.size() <= 0) {
               synchronized(this) {
                  this.wait(100L);
               }
            } else if(this.clientToServerQueue.size() > 0) {
               HashIndex hashindex = (HashIndex)this.clientToServerQueue.poll();
               if(hashindex != null) {
                  Wrapper.getInstance().sendClientToServerComparisonHash(hashindex.getHash(), hashindex.getIndex());
               } else {
                  System.exit(0);
               }
            }
         }
      } catch (InterruptedException var3) {
         System.exit(0);
      }
   }
}
