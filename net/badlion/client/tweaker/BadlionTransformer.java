package net.badlion.client.tweaker;

import net.badlion.client.tweaker.BadlionTweaker;
import net.badlion.client.tweaker.visitors.InventoryEffectVisitor;
import net.badlion.client.tweaker.visitors.ModelPlayerVisitor;
import net.badlion.client.tweaker.visitors.RenderGlobalVisitor;
import net.badlion.client.tweaker.visitors.RenderManagerVisitor;
import net.badlion.client.tweaker.visitors.enittyrenderer.UpdateCameraVisitor;
import net.badlion.client.tweaker.visitors.guiingame.RenderGameOverlayVisitor;
import net.badlion.client.tweaker.visitors.guiingame.RenderPlayerStatsVisitor;
import net.badlion.client.tweaker.visitors.guiingame.RenderScoreboardVisitor;
import net.badlion.client.tweaker.visitors.layerarmorbase.CombineTexturesVisitor;
import net.badlion.client.tweaker.visitors.layerarmorbase.RenderLayerVisitor;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

public class BadlionTransformer implements IClassTransformer {
   public byte[] transform(String name, String transformedName, byte[] bytes) {
      if(bytes == null) {
         return null;
      } else if(transformedName.contains("avo") && BadlionTweaker.OPTIFINE_LOADED) {
         System.out.println("Found Class " + name);
         ClassReader classreader6 = new ClassReader(bytes);
         ClassWriter classwriter6 = new ClassWriter(1);
         BadlionTransformer.GuiIngameClassVisitor badliontransformer$guiingameclassvisitor = new BadlionTransformer.GuiIngameClassVisitor(262144, classwriter6);
         classreader6.accept(badliontransformer$guiingameclassvisitor, 0);
         return classwriter6.toByteArray();
      } else if(transformedName.contains("bfk") && BadlionTweaker.OPTIFINE_LOADED) {
         System.out.println("Found Class " + name);
         ClassReader classreader5 = new ClassReader(bytes);
         ClassWriter classwriter5 = new ClassWriter(1);
         BadlionTransformer.EntityRendererClassVisitor badliontransformer$entityrendererclassvisitor = new BadlionTransformer.EntityRendererClassVisitor(262144, classwriter5);
         classreader5.accept(badliontransformer$entityrendererclassvisitor, 0);
         return classwriter5.toByteArray();
      } else if(transformedName.contains("bkp") && BadlionTweaker.OPTIFINE_LOADED) {
         System.out.println("Found Class " + name);
         ClassReader classreader4 = new ClassReader(bytes);
         ClassWriter classwriter4 = new ClassWriter(1);
         BadlionTransformer.LayerCapeClassVisitor badliontransformer$layercapeclassvisitor = new BadlionTransformer.LayerCapeClassVisitor(262144, classwriter4);
         classreader4.accept(badliontransformer$layercapeclassvisitor, 0);
         return classwriter4.toByteArray();
      } else if(transformedName.contains("bbr") && BadlionTweaker.OPTIFINE_LOADED) {
         System.out.println("Found Class " + name);
         ClassReader classreader3 = new ClassReader(bytes);
         ClassWriter classwriter3 = new ClassWriter(1);
         BadlionTransformer.ModelPlayerClassVisitor badliontransformer$modelplayerclassvisitor = new BadlionTransformer.ModelPlayerClassVisitor(262144, classwriter3);
         classreader3.accept(badliontransformer$modelplayerclassvisitor, 0);
         return classwriter3.toByteArray();
      } else if(transformedName.contains("biu") && BadlionTweaker.OPTIFINE_LOADED) {
         System.out.println("Found Class " + name);
         ClassReader classreader2 = new ClassReader(bytes);
         ClassWriter classwriter2 = new ClassWriter(1);
         BadlionTransformer.RenderManagerClassVisitor badliontransformer$rendermanagerclassvisitor = new BadlionTransformer.RenderManagerClassVisitor(262144, classwriter2);
         classreader2.accept(badliontransformer$rendermanagerclassvisitor, 0);
         return classwriter2.toByteArray();
      } else if(transformedName.contains("bfr") && BadlionTweaker.OPTIFINE_LOADED) {
         System.out.println("Found Class " + name);
         ClassReader classreader1 = new ClassReader(bytes);
         ClassWriter classwriter1 = new ClassWriter(1);
         BadlionTransformer.RenderGlobalClassVisitor badliontransformer$renderglobalclassvisitor = new BadlionTransformer.RenderGlobalClassVisitor(262144, classwriter1);
         classreader1.accept(badliontransformer$renderglobalclassvisitor, 0);
         return classwriter1.toByteArray();
      } else if(transformedName.contains("bkn") && BadlionTweaker.OPTIFINE_LOADED) {
         System.out.println("Found Class " + name);
         ClassReader classreader = new ClassReader(bytes);
         ClassWriter classwriter = new ClassWriter(1);
         BadlionTransformer.LayerArmorBaseClassVisitor badliontransformer$layerarmorbaseclassvisitor = new BadlionTransformer.LayerArmorBaseClassVisitor(262144, classwriter);
         classreader.accept(badliontransformer$layerarmorbaseclassvisitor, 0);
         return classwriter.toByteArray();
      } else {
         return bytes;
      }
   }

   public class EntityRendererClassVisitor extends ClassVisitor {
      public EntityRendererClassVisitor(int api, ClassWriter classWriter) {
         super(api, classWriter);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         MethodVisitor methodvisitor = super.visitMethod(access, name, desc, signature, exceptions);
         return (MethodVisitor)(name.equals("a") && desc.equals("(FJ)V")?new UpdateCameraVisitor(this.api, methodvisitor):methodvisitor);
      }
   }

   public class GuiIngameClassVisitor extends ClassVisitor {
      public GuiIngameClassVisitor(int api, ClassWriter classWriter) {
         super(api, classWriter);
      }

      public void visitSource(String source, String debug) {
         super.visitSource(source, debug);
         this.visitInnerClass("net/badlion/client/mods/misc/LegacyAnimations$AnimationMode", "net/badlion/client/mods/misc/LegacyAnimations", "AnimationMode", 16409);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         MethodVisitor methodvisitor = super.visitMethod(access, name, desc, signature, exceptions);
         return (MethodVisitor)(name.equals("a") && desc.equals("(F)V")?new RenderGameOverlayVisitor(this.api, methodvisitor):(name.equals("a") && desc.equals("(Lauk;Lavr;)V")?new RenderScoreboardVisitor(this.api, methodvisitor):(name.equals("d") && desc.equals("(Lavr;)V")?new RenderPlayerStatsVisitor(this.api, methodvisitor):methodvisitor)));
      }
   }

   public class InventoryEffectRendererVisitor extends ClassVisitor {
      public InventoryEffectRendererVisitor(int api, ClassWriter classWriter) {
         super(api, classWriter);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         MethodVisitor methodvisitor = super.visitMethod(access, name, desc, signature, exceptions);
         return (MethodVisitor)(name.equals("b") && desc.equals("()V")?new InventoryEffectVisitor(this.api, methodvisitor):methodvisitor);
      }
   }

   private class LayerArmorBaseClassVisitor extends ClassVisitor {
      public LayerArmorBaseClassVisitor(int api, ClassVisitor cv) {
         super(api, cv);
      }

      public void visitSource(String source, String debug) {
         super.visitSource(source, debug);
         this.visitInnerClass("net/badlion/client/mods/misc/LegacyAnimations$AnimationMode", "net/badlion/client/mods/misc/LegacyAnimations", "AnimationMode", 16409);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         MethodVisitor methodvisitor = super.visitMethod(access, name, desc, signature, exceptions);
         return (MethodVisitor)(name.equals("b") && desc.equals("()Z")?new CombineTexturesVisitor(this.api, methodvisitor):(name.equals("a") && desc.equals("(Lpr;FFFFFFFI)V")?new RenderLayerVisitor(this.api, methodvisitor):methodvisitor));
      }
   }

   public class LayerCapeClassVisitor extends ClassVisitor {
      public LayerCapeClassVisitor(int api, ClassWriter classWriter) {
         super(api, classWriter);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         MethodVisitor methodvisitor = super.visitMethod(access, name, desc, signature, exceptions);
         if(name.equals("a") && desc.equals("(Lbet;FFFFFFF)V")) {
            methodvisitor.visitCode();
            Label label = new Label();
            methodvisitor.visitLabel(label);
            methodvisitor.visitLineNumber(17, label);
            methodvisitor.visitVarInsn(25, 0);
            methodvisitor.visitFieldInsn(180, "bkp", "a", "Lbln;");
            methodvisitor.visitVarInsn(25, 1);
            methodvisitor.visitVarInsn(23, 2);
            methodvisitor.visitVarInsn(23, 3);
            methodvisitor.visitVarInsn(23, 4);
            methodvisitor.visitVarInsn(23, 5);
            methodvisitor.visitVarInsn(23, 6);
            methodvisitor.visitVarInsn(23, 7);
            methodvisitor.visitVarInsn(23, 8);
            methodvisitor.visitMethodInsn(184, "net/badlion/client/manager/CapeManager", "renderCape", "(Lbln;Lbet;FFFFFFF)V");
            Label label1 = new Label();
            methodvisitor.visitLabel(label1);
            methodvisitor.visitLineNumber(18, label1);
            methodvisitor.visitInsn(177);
         }

         return methodvisitor;
      }
   }

   public class ModelPlayerClassVisitor extends ClassVisitor {
      public ModelPlayerClassVisitor(int api, ClassWriter classWriter) {
         super(api, classWriter);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         MethodVisitor methodvisitor = super.visitMethod(access, name, desc, signature, exceptions);
         return (MethodVisitor)(name.equals("<init>") && desc.equals("(FZ)V")?new ModelPlayerVisitor(this.api, methodvisitor):methodvisitor);
      }
   }

   public class RenderGlobalClassVisitor extends ClassVisitor {
      public RenderGlobalClassVisitor(int api, ClassWriter classWriter) {
         super(api, classWriter);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         MethodVisitor methodvisitor = super.visitMethod(access, name, desc, signature, exceptions);
         return (MethodVisitor)(name.equals("a") && desc.equals("(Lwn;Lauh;IF)V")?new RenderGlobalVisitor(this.api, methodvisitor):methodvisitor);
      }
   }

   public class RenderManagerClassVisitor extends ClassVisitor {
      public RenderManagerClassVisitor(int api, ClassWriter classWriter) {
         super(api, classWriter);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         MethodVisitor methodvisitor = super.visitMethod(access, name, desc, signature, exceptions);
         return (MethodVisitor)(name.equals("b") && desc.equals("(Lpk;DDDFF)V")?new RenderManagerVisitor(this.api, methodvisitor):methodvisitor);
      }

      public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
         return name.equals("t") && desc.equals("Z")?super.visitField(1, name, desc, signature, value):super.visitField(access, name, desc, signature, value);
      }
   }

   public class TextureAltasMethodVisitor extends MethodVisitor {
      private boolean firstLine = true;

      public TextureAltasMethodVisitor(int api, MethodVisitor methodVisitor) {
         super(api, methodVisitor);
      }

      public void visitLineNumber(int line, Label start) {
         super.visitLineNumber(line, start);
         if(this.firstLine) {
            this.firstLine = false;
            System.out.println("Injecting Into bmi Class");
            this.mv.visitVarInsn(25, 0);
            this.mv.visitMethodInsn(184, "net/badlion/client/util/TextureCheckUtil", "checkTexture", "([[I)[[I");
            this.mv.visitVarInsn(58, 0);
         }

      }
   }

   public class TextureAtlasSpriteVisitor extends ClassVisitor {
      public TextureAtlasSpriteVisitor(int api, ClassWriter classWriter) {
         super(api, classWriter);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         MethodVisitor methodvisitor = super.visitMethod(access, name, desc, signature, exceptions);
         System.out.println("Name: " + name + " desc: " + desc);
         Object var10000;
         if(name.equals("a") && desc.equals("([[IIII)[[I")) {
            BadlionTransformer var10002 = BadlionTransformer.this;
            BadlionTransformer.this.getClass();
            var10000 = var10002.new TextureAltasMethodVisitor(this.api, methodvisitor);
         } else {
            var10000 = methodvisitor;
         }

         return (MethodVisitor)var10000;
      }
   }

   public class TextureMapVisitor extends ClassVisitor {
      public TextureMapVisitor(int api, ClassWriter classWriter) {
         super(api, classWriter);
      }

      public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
         MethodVisitor methodvisitor = super.visitMethod(access, name, desc, signature, exceptions);
         Object var10000;
         if(name.equals("b") && desc.equals("(Lbni;)V")) {
            BadlionTransformer var10002 = BadlionTransformer.this;
            BadlionTransformer.this.getClass();
            var10000 = var10002.new TextureMapVisitorMethodVisitor(this.api, methodvisitor);
         } else {
            var10000 = methodvisitor;
         }

         return (MethodVisitor)var10000;
      }
   }

   public class TextureMapVisitorMethodVisitor extends MethodVisitor {
      public TextureMapVisitorMethodVisitor(int api, MethodVisitor methodVisitor) {
         super(api, methodVisitor);
      }

      public void visitLineNumber(int line, Label start) {
         super.visitLineNumber(line, start);
         if(line == 119 && !BadlionTweaker.OPTIFINE_LOADED || line == 291 && BadlionTweaker.OPTIFINE_LOADED) {
            System.out.println("Injecting Into bmh Class");
            this.visitVarInsn(25, 8);
            this.visitMethodInsn(185, "java/util/Map$Entry", "getKey", "()Ljava/lang/Object;");
            this.visitTypeInsn(192, "java/lang/String");
            this.visitFieldInsn(179, "net/badlion/client/util/TextureCheckUtil", "TEXTURE_NAME", "Ljava/lang/String;");
         }

      }
   }
}
