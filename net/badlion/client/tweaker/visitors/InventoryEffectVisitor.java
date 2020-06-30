package net.badlion.client.tweaker.visitors;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class InventoryEffectVisitor extends MethodVisitor {
   private boolean delete = false;

   public InventoryEffectVisitor(int api, MethodVisitor methodVisitor) {
      super(api, methodVisitor);
   }

   public void visitLineNumber(int line, Label start) {
      if(line == 24) {
         this.delete = false;
      }

      if(line == 23) {
         this.delete = true;
      }

      if(!this.delete) {
         super.visitLineNumber(line, start);
      }

   }

   public void visitVarInsn(int opcode, int var) {
      if(!this.delete) {
         super.visitVarInsn(opcode, var);
      }

   }

   public void visitIntInsn(int opcode, int operand) {
      if(!this.delete) {
         super.visitIntInsn(opcode, operand);
      }

   }

   public void visitFieldInsn(int opcode, String owner, String name, String desc) {
      if(!this.delete) {
         super.visitFieldInsn(opcode, owner, name, desc);
      }

   }

   public void visitInsn(int opcode) {
      if(!this.delete) {
         super.visitInsn(opcode);
      }

   }
}
