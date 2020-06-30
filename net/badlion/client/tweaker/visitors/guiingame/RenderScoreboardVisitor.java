package net.badlion.client.tweaker.visitors.guiingame;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class RenderScoreboardVisitor extends MethodVisitor {
   public RenderScoreboardVisitor(int api, MethodVisitor methodVisitor) {
      super(api, methodVisitor);
   }

   public void visitCode() {
      super.visitCode();
      Label label = new Label();
      this.mv.visitLabel(label);
      this.mv.visitVarInsn(25, 1);
      this.mv.visitVarInsn(25, 2);
      this.mv.visitMethodInsn(184, "net/badlion/client/mods/render/Scoreboard", "renderScoreboard", "(Lauk;Lavr;)V");
      Label label1 = new Label();
      this.mv.visitLabel(label1);
      this.mv.visitInsn(177);
   }
}
