package org.apache.logging.log4j.message;

import java.lang.Thread.State;
import org.apache.logging.log4j.message.ThreadInformation;

class BasicThreadInformation implements ThreadInformation {
   private static final int HASH_SHIFT = 32;
   private static final int HASH_MULTIPLIER = 31;
   private final long id;
   private final String name;
   private final String longName;
   private final State state;
   private final int priority;
   private final boolean isAlive;
   private final boolean isDaemon;
   private final String threadGroupName;

   public BasicThreadInformation(Thread thread) {
      this.id = thread.getId();
      this.name = thread.getName();
      this.longName = thread.toString();
      this.state = thread.getState();
      this.priority = thread.getPriority();
      this.isAlive = thread.isAlive();
      this.isDaemon = thread.isDaemon();
      ThreadGroup group = thread.getThreadGroup();
      this.threadGroupName = group == null?null:group.getName();
   }

   public boolean equals(Object o) {
      if(this == o) {
         return true;
      } else if(o != null && this.getClass() == o.getClass()) {
         BasicThreadInformation that = (BasicThreadInformation)o;
         if(this.id != that.id) {
            return false;
         } else {
            if(this.name != null) {
               if(!this.name.equals(that.name)) {
                  return false;
               }
            } else if(that.name != null) {
               return false;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int result = (int)(this.id ^ this.id >>> 32);
      result = 31 * result + (this.name != null?this.name.hashCode():0);
      return result;
   }

   public void printThreadInfo(StringBuilder sb) {
      sb.append("\"").append(this.name).append("\" ");
      if(this.isDaemon) {
         sb.append("daemon ");
      }

      sb.append("prio=").append(this.priority).append(" tid=").append(this.id).append(" ");
      if(this.threadGroupName != null) {
         sb.append("group=\"").append(this.threadGroupName).append("\"");
      }

      sb.append("\n");
      sb.append("\tThread state: ").append(this.state.name()).append("\n");
   }

   public void printStack(StringBuilder sb, StackTraceElement[] trace) {
      for(StackTraceElement element : trace) {
         sb.append("\tat ").append(element).append("\n");
      }

   }
}
