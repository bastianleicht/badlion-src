package net.badlion.client.tweaker.visitors;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class ModelPlayerVisitor extends MethodVisitor {
   private int line = 0;

   public ModelPlayerVisitor(int api, MethodVisitor methodVisitor) {
      super(api, methodVisitor);
   }

   public void visitLineNumber(int line, Label label) {
      super.visitLineNumber(line, label);
      this.line = line;
   }

   public void visitMethodInsn(int opcode, String owner, String name, String desc) {
      super.visitMethodInsn(opcode, owner, name, desc);
      if(opcode == 182 && owner.contains("bct") && this.line == 25) {
         this.mv.visitMethodInsn(184, "net/badlion/client/Wrapper", "getInstance", "()Lnet/badlion/client/Wrapper;");
         this.mv.visitMethodInsn(182, "net/badlion/client/Wrapper", "getCapeManager", "()Lnet/badlion/client/manager/CapeManager;");
         this.mv.visitVarInsn(25, 0);
         this.mv.visitMethodInsn(182, "net/badlion/client/manager/CapeManager", "initCapes", "(Lbbr;)V");
      }

   }
}
