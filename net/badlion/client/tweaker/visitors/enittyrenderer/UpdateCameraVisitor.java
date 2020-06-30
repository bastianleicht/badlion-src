package net.badlion.client.tweaker.visitors.enittyrenderer;

import org.objectweb.asm.MethodVisitor;

public class UpdateCameraVisitor extends MethodVisitor {
   public UpdateCameraVisitor(int api, MethodVisitor methodVisitor) {
      super(api, methodVisitor);
   }

   public void visitMethodInsn(int opcode, String owner, String name, String desc) {
      if(opcode != 184 || !name.equals("drawFps")) {
         super.visitMethodInsn(opcode, owner, name, desc);
      }

   }
}
