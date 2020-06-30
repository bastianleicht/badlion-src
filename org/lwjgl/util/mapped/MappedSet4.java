package org.lwjgl.util.mapped;

import org.lwjgl.util.mapped.MappedObject;

public class MappedSet4 {
   private final MappedObject a;
   private final MappedObject b;
   private final MappedObject c;
   private final MappedObject d;
   public int view;

   MappedSet4(MappedObject a, MappedObject b, MappedObject c, MappedObject d) {
      this.a = a;
      this.b = b;
      this.c = c;
      this.d = d;
   }

   void view(int view) {
      this.a.setViewAddress(this.a.getViewAddress(view));
      this.b.setViewAddress(this.b.getViewAddress(view));
      this.c.setViewAddress(this.c.getViewAddress(view));
      this.d.setViewAddress(this.d.getViewAddress(view));
   }

   public void next() {
      this.a.next();
      this.b.next();
      this.c.next();
      this.d.next();
   }
}
