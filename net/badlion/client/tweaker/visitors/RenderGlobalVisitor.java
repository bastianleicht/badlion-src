package net.badlion.client.tweaker.visitors;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class RenderGlobalVisitor extends MethodVisitor {
   public RenderGlobalVisitor(int api, MethodVisitor methodVisitor) {
      super(api, methodVisitor);
   }

   public void visitCode() {
      super.visitCode();
      Label label = new Label();
      this.mv.visitLabel(label);
      this.mv.visitVarInsn(25, 1);
      this.mv.visitVarInsn(25, 2);
      this.mv.visitVarInsn(21, 3);
      this.mv.visitVarInsn(23, 4);
      this.mv.visitMethodInsn(184, "net/badlion/client/mods/misc/BlockOverlay", "drawSelectionBox", "(Lwn;Lauh;IF)V");
      Label label1 = new Label();
      this.mv.visitLabel(label1);
      this.mv.visitInsn(177);
   }
}
