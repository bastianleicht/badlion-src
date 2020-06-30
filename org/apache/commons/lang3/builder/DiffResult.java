package org.apache.commons.lang3.builder;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.lang3.builder.Diff;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class DiffResult implements Iterable {
   public static final String OBJECTS_SAME_STRING = "";
   private static final String DIFFERS_STRING = "differs from";
   private final List diffs;
   private final Object lhs;
   private final Object rhs;
   private final ToStringStyle style;

   DiffResult(Object lhs, Object rhs, List diffs, ToStringStyle style) {
      if(lhs == null) {
         throw new IllegalArgumentException("Left hand object cannot be null");
      } else if(rhs == null) {
         throw new IllegalArgumentException("Right hand object cannot be null");
      } else if(diffs == null) {
         throw new IllegalArgumentException("List of differences cannot be null");
      } else {
         this.diffs = diffs;
         this.lhs = lhs;
         this.rhs = rhs;
         if(style == null) {
            this.style = ToStringStyle.DEFAULT_STYLE;
         } else {
            this.style = style;
         }

      }
   }

   public List getDiffs() {
      return Collections.unmodifiableList(this.diffs);
   }

   public int getNumberOfDiffs() {
      return this.diffs.size();
   }

   public ToStringStyle getToStringStyle() {
      return this.style;
   }

   public String toString() {
      return this.toString(this.style);
   }

   public String toString(ToStringStyle style) {
      if(this.diffs.size() == 0) {
         return "";
      } else {
         ToStringBuilder lhsBuilder = new ToStringBuilder(this.lhs, style);
         ToStringBuilder rhsBuilder = new ToStringBuilder(this.rhs, style);

         for(Diff<?> diff : this.diffs) {
            lhsBuilder.append(diff.getFieldName(), diff.getLeft());
            rhsBuilder.append(diff.getFieldName(), diff.getRight());
         }

         return String.format("%s %s %s", new Object[]{lhsBuilder.build(), "differs from", rhsBuilder.build()});
      }
   }

   public Iterator iterator() {
      return this.diffs.iterator();
   }
}
