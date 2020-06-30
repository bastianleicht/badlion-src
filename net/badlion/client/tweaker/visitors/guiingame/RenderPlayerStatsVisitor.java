package net.badlion.client.tweaker.visitors.guiingame;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class RenderPlayerStatsVisitor extends MethodVisitor {
   boolean heathBlock = false;
   boolean flag = false;
   boolean done = false;

   public RenderPlayerStatsVisitor(int api, MethodVisitor methodVisitor) {
      super(api, methodVisitor);
   }

   public void visitLdcInsn(Object cst) {
      super.visitLdcInsn(cst);
      if(cst instanceof String && cst.equals("health")) {
         this.heathBlock = true;
      }

   }

   public void visitMethodInsn(int opcode, String owner, String name, String desc) {
      super.visitMethodInsn(opcode, owner, name, desc);
      if(this.heathBlock && opcode == 182 && owner.equals("avo") && name.equals("b") && desc.equals("(IIIIII)V")) {
         this.flag = true;
      }

   }

   public void visitJumpInsn(int opcode, Label label) {
      super.visitJumpInsn(opcode, label);
      if(this.heathBlock && this.flag && !this.done) {
         this.heathBlock = false;
         this.flag = false;
         this.done = true;
         this.mv.visitMethodInsn(184, "net/badlion/client/Wrapper", "getInstance", "()Lnet/badlion/client/Wrapper;");
         this.mv.visitMethodInsn(182, "net/badlion/client/Wrapper", "getLegacyAnimations", "()Lnet/badlion/client/mods/misc/LegacyAnimations;");
         this.mv.visitMethodInsn(182, "net/badlion/client/mods/misc/LegacyAnimations", "getHeartAnimation", "()Lnet/badlion/client/mods/misc/LegacyAnimations$AnimationMode;");
         this.mv.visitFieldInsn(178, "net/badlion/client/mods/misc/LegacyAnimations$AnimationMode", "CURRENT", "Lnet/badlion/client/mods/misc/LegacyAnimations$AnimationMode;");
         this.mv.visitMethodInsn(182, "net/badlion/client/mods/misc/LegacyAnimations$AnimationMode", "equals", "(Ljava/lang/Object;)Z");
         this.mv.visitJumpInsn(153, label);
      }

   }
}
