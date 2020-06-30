package org.apache.commons.lang3.builder;

import java.util.Collection;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class RecursiveToStringStyle extends ToStringStyle {
   private static final long serialVersionUID = 1L;

   public void appendDetail(StringBuffer buffer, String fieldName, Object value) {
      if(!ClassUtils.isPrimitiveWrapper(value.getClass()) && !String.class.equals(value.getClass()) && this.accept(value.getClass())) {
         buffer.append(ReflectionToStringBuilder.toString(value, this));
      } else {
         super.appendDetail(buffer, fieldName, value);
      }

   }

   protected void appendDetail(StringBuffer buffer, String fieldName, Collection coll) {
      this.appendClassName(buffer, coll);
      this.appendIdentityHashCode(buffer, coll);
      this.appendDetail(buffer, fieldName, (Object[])coll.toArray());
   }

   protected boolean accept(Class clazz) {
      return true;
   }
}
