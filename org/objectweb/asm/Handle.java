package org.objectweb.asm;

public final class Handle {
   final int a;
   final String b;
   final String c;
   final String d;

   public Handle(int var1, String var2, String var3, String var4) {
      this.a = var1;
      this.b = var2;
      this.c = var3;
      this.d = var4;
   }

   public int getTag() {
      return this.a;
   }

   public String getOwner() {
      return this.b;
   }

   public String getName() {
      return this.c;
   }

   public String getDesc() {
      return this.d;
   }

   public boolean equals(Object var1) {
      if(var1 == this) {
         return true;
      } else if(!(var1 instanceof Handle)) {
         return false;
      } else {
         Handle var2 = (Handle)var1;
         return this.a == var2.a && this.b.equals(var2.b) && this.c.equals(var2.c) && this.d.equals(var2.d);
      }
   }

   public int hashCode() {
      return this.a + this.b.hashCode() * this.c.hashCode() * this.d.hashCode();
   }

   public String toString() {
      return this.b + '.' + this.c + this.d + " (" + this.a + ')';
   }
}
