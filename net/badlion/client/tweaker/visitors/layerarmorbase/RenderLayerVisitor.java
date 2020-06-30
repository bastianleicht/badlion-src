package net.badlion.client.tweaker.visitors.layerarmorbase;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class RenderLayerVisitor extends MethodVisitor {
   private boolean flag = false;
   private Label label;

   public RenderLayerVisitor(int api, MethodVisitor mv) {
      super(api, mv);
   }

   public void visitJumpInsn(int opcode, Label label) {
      super.visitJumpInsn(opcode, label);
      this.label = label;
   }

   public void visitMethodInsn(int opcode, String owner, String name, String desc) {
      super.visitMethodInsn(opcode, owner, name, desc);
      if(opcode == 182 && owner.equals("zx") && name.equals("w") && desc.equals("()Z")) {
         if(!this.flag) {
            this.flag = true;
            return;
         }

         this.mv.visitJumpInsn(153, this.label);
         this.mv.visitMethodInsn(184, "net/badlion/client/Wrapper", "getInstance", "()Lnet/badlion/client/Wrapper;");
         this.mv.visitMethodInsn(182, "net/badlion/client/Wrapper", "getActiveModProfile", "()Lnet/badlion/client/mods/ModProfile;");
         this.mv.visitMethodInsn(182, "net/badlion/client/mods/ModProfile", "getEnchantGlint", "()Lnet/badlion/client/mods/misc/EnchantGlint;");
         this.mv.visitMethodInsn(182, "net/badlion/client/mods/misc/EnchantGlint", "isEnabled", "()Z");
      }

   }
}
