package org.lwjgl.opencl;

import org.lwjgl.LWJGLUtil;
import org.lwjgl.opencl.CLObjectChild;
import org.lwjgl.opencl.FastLongMap;

class CLObjectRegistry {
   private FastLongMap registry;

   final boolean isEmpty() {
      return this.registry == null || this.registry.isEmpty();
   }

   final CLObjectChild getObject(long id) {
      return this.registry == null?null:(CLObjectChild)this.registry.get(id);
   }

   final boolean hasObject(long id) {
      return this.registry != null && this.registry.containsKey(id);
   }

   final Iterable getAll() {
      return this.registry;
   }

   void registerObject(CLObjectChild object) {
      FastLongMap<T> map = this.getMap();
      Long key = Long.valueOf(object.getPointer());
      if(LWJGLUtil.DEBUG && map.containsKey(key.longValue())) {
         throw new IllegalStateException("Duplicate object found: " + object.getClass() + " - " + key);
      } else {
         this.getMap().put(object.getPointer(), object);
      }
   }

   void unregisterObject(CLObjectChild object) {
      this.getMap().remove(object.getPointerUnsafe());
   }

   private FastLongMap getMap() {
      if(this.registry == null) {
         this.registry = new FastLongMap();
      }

      return this.registry;
   }
}
