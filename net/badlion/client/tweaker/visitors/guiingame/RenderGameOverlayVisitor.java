package net.badlion.client.tweaker.visitors.guiingame;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class RenderGameOverlayVisitor extends MethodVisitor {
   private int line = 0;
   private Label crosshairBreakLabel;

   public RenderGameOverlayVisitor(int api, MethodVisitor methodVisitor) {
      super(api, methodVisitor);
   }

   public void visitLineNumber(int line, Label start) {
      this.line = line;
      super.visitLineNumber(line, start);
      if(line == 170) {
         this.visitMethodInsn(184, "net/badlion/client/Wrapper", "getInstance", "()Lnet/badlion/client/Wrapper;");
         this.visitMethodInsn(182, "net/badlion/client/Wrapper", "getActiveModProfile", "()Lnet/badlion/client/mods/ModProfile;");
         this.visitMethodInsn(182, "net/badlion/client/mods/ModProfile", "getCrosshair", "()Lnet/badlion/client/mods/render/Crosshair;");
         this.visitMethodInsn(182, "net/badlion/client/mods/render/Crosshair", "isEnabled", "()Z");
         this.visitJumpInsn(154, this.crosshairBreakLabel);
      }

   }

   public void visitMethodInsn(int opcode, String owner, String name, String desc) {
      if(this.line == 358 && owner.equals("bfl")) {
         this.visitVarInsn(25, 0);
         this.visitMethodInsn(184, "net/badlion/client/manager/ModProfileManager", "callRenderGame", "(Lavo;)V");
      }

      super.visitMethodInsn(opcode, owner, name, desc);
   }

   public void visitJumpInsn(int opcode, Label label) {
      if(this.line == 166) {
         this.crosshairBreakLabel = label;
      }

      super.visitJumpInsn(opcode, label);
   }
}
