package net.badlion.client.tweaker.visitors;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class RenderManagerVisitor extends MethodVisitor {
   public RenderManagerVisitor(int api, MethodVisitor methodVisitor) {
      super(api, methodVisitor);
   }

   public void visitCode() {
      super.visitCode();
      Label label = new Label();
      this.mv.visitLabel(label);
      this.mv.visitVarInsn(25, 1);
      this.mv.visitVarInsn(24, 2);
      this.mv.visitVarInsn(24, 4);
      this.mv.visitVarInsn(24, 6);
      this.mv.visitMethodInsn(184, "net/badlion/client/mods/render/Hitboxes", "renderHitbox", "(Lpk;DDD)V");
      Label label1 = new Label();
      this.mv.visitLabel(label1);
      this.mv.visitInsn(177);
   }
}
