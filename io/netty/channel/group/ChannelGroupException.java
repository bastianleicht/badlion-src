package io.netty.channel.group;

import io.netty.channel.ChannelException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class ChannelGroupException extends ChannelException implements Iterable {
   private static final long serialVersionUID = -4093064295562629453L;
   private final Collection failed;

   public ChannelGroupException(Collection causes) {
      if(causes == null) {
         throw new NullPointerException("causes");
      } else if(causes.isEmpty()) {
         throw new IllegalArgumentException("causes must be non empty");
      } else {
         this.failed = Collections.unmodifiableCollection(causes);
      }
   }

   public Iterator iterator() {
      return this.failed.iterator();
   }
}
