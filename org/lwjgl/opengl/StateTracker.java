package org.lwjgl.opengl;

import java.nio.IntBuffer;
import org.lwjgl.opengl.BaseReferences;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.FastIntMap;
import org.lwjgl.opengl.References;
import org.lwjgl.opengl.ReferencesStack;
import org.lwjgl.opengl.StateStack;

final class StateTracker {
   private ReferencesStack references_stack;
   private final StateStack attrib_stack = new StateStack(0);
   private boolean insideBeginEnd;
   private final FastIntMap vaoMap = new FastIntMap();

   void init() {
      this.references_stack = new ReferencesStack();
   }

   static void setBeginEnd(ContextCapabilities caps, boolean inside) {
      caps.tracker.insideBeginEnd = inside;
   }

   boolean isBeginEnd() {
      return this.insideBeginEnd;
   }

   static void popAttrib(ContextCapabilities caps) {
      caps.tracker.doPopAttrib();
   }

   private void doPopAttrib() {
      this.references_stack.popState(this.attrib_stack.popState());
   }

   static void pushAttrib(ContextCapabilities caps, int mask) {
      caps.tracker.doPushAttrib(mask);
   }

   private void doPushAttrib(int mask) {
      this.attrib_stack.pushState(mask);
      this.references_stack.pushState();
   }

   static References getReferences(ContextCapabilities caps) {
      return caps.tracker.references_stack.getReferences();
   }

   static void bindBuffer(ContextCapabilities caps, int target, int buffer) {
      BaseReferences references = getReferences(caps);
      switch(target) {
      case 34962:
         references.arrayBuffer = buffer;
         break;
      case 34963:
         if(references.vertexArrayObject != 0) {
            ((StateTracker.VAOState)caps.tracker.vaoMap.get(references.vertexArrayObject)).elementArrayBuffer = buffer;
         } else {
            references.elementArrayBuffer = buffer;
         }
         break;
      case 35051:
         references.pixelPackBuffer = buffer;
         break;
      case 35052:
         references.pixelUnpackBuffer = buffer;
         break;
      case 36671:
         references.indirectBuffer = buffer;
      }

   }

   static void bindVAO(ContextCapabilities caps, int array) {
      FastIntMap<StateTracker.VAOState> vaoMap = caps.tracker.vaoMap;
      if(!vaoMap.containsKey(array)) {
         vaoMap.put(array, new StateTracker.VAOState());
      }

      getReferences(caps).vertexArrayObject = array;
   }

   static void deleteVAO(ContextCapabilities caps, IntBuffer arrays) {
      for(int i = arrays.position(); i < arrays.limit(); ++i) {
         deleteVAO(caps, arrays.get(i));
      }

   }

   static void deleteVAO(ContextCapabilities caps, int array) {
      caps.tracker.vaoMap.remove(array);
      BaseReferences references = getReferences(caps);
      if(references.vertexArrayObject == array) {
         references.vertexArrayObject = 0;
      }

   }

   static int getElementArrayBufferBound(ContextCapabilities caps) {
      BaseReferences references = getReferences(caps);
      return references.vertexArrayObject == 0?references.elementArrayBuffer:((StateTracker.VAOState)caps.tracker.vaoMap.get(references.vertexArrayObject)).elementArrayBuffer;
   }

   private static class VAOState {
      int elementArrayBuffer;

      private VAOState() {
      }
   }
}
