package org.apache.logging.log4j.core.net;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.net.Facility;
import org.apache.logging.log4j.core.net.Severity;

public class Priority {
   private final Facility facility;
   private final Severity severity;

   public Priority(Facility facility, Severity severity) {
      this.facility = facility;
      this.severity = severity;
   }

   public static int getPriority(Facility facility, Level level) {
      return (facility.getCode() << 3) + Severity.getSeverity(level).getCode();
   }

   public Facility getFacility() {
      return this.facility;
   }

   public Severity getSeverity() {
      return this.severity;
   }

   public int getValue() {
      return this.facility.getCode() << 3 + this.severity.getCode();
   }

   public String toString() {
      return Integer.toString(this.getValue());
   }
}
