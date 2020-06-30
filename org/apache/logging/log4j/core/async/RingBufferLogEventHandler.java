package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.Sequence;
import com.lmax.disruptor.SequenceReportingEventHandler;
import org.apache.logging.log4j.core.async.RingBufferLogEvent;

public class RingBufferLogEventHandler implements SequenceReportingEventHandler {
   private static final int NOTIFY_PROGRESS_THRESHOLD = 50;
   private Sequence sequenceCallback;
   private int counter;

   public void setSequenceCallback(Sequence sequenceCallback) {
      this.sequenceCallback = sequenceCallback;
   }

   public void onEvent(RingBufferLogEvent event, long sequence, boolean endOfBatch) throws Exception {
      event.execute(endOfBatch);
      event.clear();
      if(++this.counter > 50) {
         this.sequenceCallback.set(sequence);
         this.counter = 0;
      }

   }
}
