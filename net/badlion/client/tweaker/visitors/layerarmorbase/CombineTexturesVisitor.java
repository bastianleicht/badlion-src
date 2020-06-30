package net.badlion.client.tweaker.visitors.layerarmorbase;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class CombineTexturesVisitor extends MethodVisitor {
   public CombineTexturesVisitor(int api, MethodVisitor mv) {
      super(api, mv);
   }

   public void visitCode() {
      super.visitCode();
      Label label = new Label();
      this.mv.visitLabel(label);
      this.mv.visitMethodInsn(184, "net/badlion/client/Wrapper", "getInstance", "()Lnet/badlion/client/Wrapper;");
      this.mv.visitMethodInsn(182, "net/badlion/client/Wrapper", "getLegacyAnimations", "()Lnet/badlion/client/mods/misc/LegacyAnimations;");
      this.mv.visitMethodInsn(182, "net/badlion/client/mods/misc/LegacyAnimations", "getDamageAnimation", "()Lnet/badlion/client/mods/misc/LegacyAnimations$AnimationMode;");
      this.mv.visitFieldInsn(178, "net/badlion/client/mods/misc/LegacyAnimations$AnimationMode", "LEGACY", "Lnet/badlion/client/mods/misc/LegacyAnimations$AnimationMode;");
      this.mv.visitMethodInsn(182, "net/badlion/client/mods/misc/LegacyAnimations$AnimationMode", "equals", "(Ljava/lang/Object;)Z");
      this.mv.visitInsn(172);
   }
}
