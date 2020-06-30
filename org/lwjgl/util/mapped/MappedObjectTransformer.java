package org.lwjgl.util.mapped;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.MemoryUtil;
import org.lwjgl.util.mapped.CacheLinePad;
import org.lwjgl.util.mapped.CacheUtil;
import org.lwjgl.util.mapped.MappedField;
import org.lwjgl.util.mapped.MappedHelper;
import org.lwjgl.util.mapped.MappedObject;
import org.lwjgl.util.mapped.MappedObjectClassLoader;
import org.lwjgl.util.mapped.MappedObjectUnsafe;
import org.lwjgl.util.mapped.MappedSet;
import org.lwjgl.util.mapped.MappedSet2;
import org.lwjgl.util.mapped.MappedSet3;
import org.lwjgl.util.mapped.MappedSet4;
import org.lwjgl.util.mapped.MappedType;
import org.lwjgl.util.mapped.Pointer;
import org.objectweb.asm.ClassAdapter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.tree.analysis.Analyzer;
import org.objectweb.asm.tree.analysis.AnalyzerException;
import org.objectweb.asm.tree.analysis.BasicValue;
import org.objectweb.asm.tree.analysis.Frame;
import org.objectweb.asm.tree.analysis.SimpleVerifier;
import org.objectweb.asm.util.TraceClassVisitor;

public class MappedObjectTransformer {
   static final boolean PRINT_ACTIVITY = LWJGLUtil.DEBUG && LWJGLUtil.getPrivilegedBoolean("org.lwjgl.util.mapped.PrintActivity");
   static final boolean PRINT_TIMING = PRINT_ACTIVITY && LWJGLUtil.getPrivilegedBoolean("org.lwjgl.util.mapped.PrintTiming");
   static final boolean PRINT_BYTECODE = LWJGLUtil.DEBUG && LWJGLUtil.getPrivilegedBoolean("org.lwjgl.util.mapped.PrintBytecode");
   static final Map className_to_subtype = new HashMap();
   static final String MAPPED_OBJECT_JVM = jvmClassName(MappedObject.class);
   static final String MAPPED_HELPER_JVM = jvmClassName(MappedHelper.class);
   static final String MAPPEDSET_PREFIX = jvmClassName(MappedSet.class);
   static final String MAPPED_SET2_JVM = jvmClassName(MappedSet2.class);
   static final String MAPPED_SET3_JVM = jvmClassName(MappedSet3.class);
   static final String MAPPED_SET4_JVM = jvmClassName(MappedSet4.class);
   static final String CACHE_LINE_PAD_JVM = "L" + jvmClassName(CacheLinePad.class) + ";";
   static final String VIEWADDRESS_METHOD_NAME = "getViewAddress";
   static final String NEXT_METHOD_NAME = "next";
   static final String ALIGN_METHOD_NAME = "getAlign";
   static final String SIZEOF_METHOD_NAME = "getSizeof";
   static final String CAPACITY_METHOD_NAME = "capacity";
   static final String VIEW_CONSTRUCTOR_NAME = "constructView$LWJGL";
   static final Map OPCODE_TO_NAME = new HashMap();
   static final Map INSNTYPE_TO_NAME = new HashMap();
   static boolean is_currently_computing_frames;

   public static void register(Class type) {
      if(!MappedObjectClassLoader.FORKED) {
         MappedType mapped = (MappedType)type.getAnnotation(MappedType.class);
         if(mapped != null && mapped.padding() < 0) {
            throw new ClassFormatError("Invalid mapped type padding: " + mapped.padding());
         } else if(type.getEnclosingClass() != null && !Modifier.isStatic(type.getModifiers())) {
            throw new InternalError("only top-level or static inner classes are allowed");
         } else {
            String className = jvmClassName(type);
            Map<String, MappedObjectTransformer.FieldInfo> fields = new HashMap();
            long sizeof = 0L;

            for(Field field : type.getDeclaredFields()) {
               MappedObjectTransformer.FieldInfo fieldInfo = registerField(mapped == null || mapped.autoGenerateOffsets(), className, sizeof, field);
               if(fieldInfo != null) {
                  fields.put(field.getName(), fieldInfo);
                  sizeof = Math.max(sizeof, fieldInfo.offset + fieldInfo.lengthPadded);
               }
            }

            int align = 4;
            int padding = 0;
            boolean cacheLinePadded = false;
            if(mapped != null) {
               align = mapped.align();
               if(mapped.cacheLinePadding()) {
                  if(mapped.padding() != 0) {
                     throw new ClassFormatError("Mapped type padding cannot be specified together with cacheLinePadding.");
                  }

                  int cacheLineMod = (int)(sizeof % (long)CacheUtil.getCacheLineSize());
                  if(cacheLineMod != 0) {
                     padding = CacheUtil.getCacheLineSize() - cacheLineMod;
                  }

                  cacheLinePadded = true;
               } else {
                  padding = mapped.padding();
               }
            }

            sizeof = sizeof + (long)padding;
            MappedObjectTransformer.MappedSubtypeInfo mappedType = new MappedObjectTransformer.MappedSubtypeInfo(className, fields, (int)sizeof, align, padding, cacheLinePadded);
            if(className_to_subtype.put(className, mappedType) != null) {
               throw new InternalError("duplicate mapped type: " + mappedType.className);
            }
         }
      }
   }

   private static MappedObjectTransformer.FieldInfo registerField(boolean autoGenerateOffsets, String className, long advancingOffset, Field field) {
      if(Modifier.isStatic(field.getModifiers())) {
         return null;
      } else if(!field.getType().isPrimitive() && field.getType() != ByteBuffer.class) {
         throw new ClassFormatError("field \'" + className + "." + field.getName() + "\' not supported: " + field.getType());
      } else {
         MappedField meta = (MappedField)field.getAnnotation(MappedField.class);
         if(meta == null && !autoGenerateOffsets) {
            throw new ClassFormatError("field \'" + className + "." + field.getName() + "\' missing annotation " + MappedField.class.getName() + ": " + className);
         } else {
            Pointer pointer = (Pointer)field.getAnnotation(Pointer.class);
            if(pointer != null && field.getType() != Long.TYPE) {
               throw new ClassFormatError("The @Pointer annotation can only be used on long fields. @Pointer field found: " + className + "." + field.getName() + ": " + field.getType());
            } else if(!Modifier.isVolatile(field.getModifiers()) || pointer == null && field.getType() != ByteBuffer.class) {
               long byteLength;
               if(field.getType() != Long.TYPE && field.getType() != Double.TYPE) {
                  if(field.getType() == Double.TYPE) {
                     byteLength = 8L;
                  } else if(field.getType() != Integer.TYPE && field.getType() != Float.TYPE) {
                     if(field.getType() != Character.TYPE && field.getType() != Short.TYPE) {
                        if(field.getType() == Byte.TYPE) {
                           byteLength = 1L;
                        } else {
                           if(field.getType() != ByteBuffer.class) {
                              throw new ClassFormatError(field.getType().getName());
                           }

                           byteLength = meta.byteLength();
                           if(byteLength < 0L) {
                              throw new IllegalStateException("invalid byte length for mapped ByteBuffer field: " + className + "." + field.getName() + " [length=" + byteLength + "]");
                           }
                        }
                     } else {
                        byteLength = 2L;
                     }
                  } else {
                     byteLength = 4L;
                  }
               } else if(pointer == null) {
                  byteLength = 8L;
               } else {
                  byteLength = (long)MappedObjectUnsafe.INSTANCE.addressSize();
               }

               if(field.getType() != ByteBuffer.class && advancingOffset % byteLength != 0L) {
                  throw new IllegalStateException("misaligned mapped type: " + className + "." + field.getName());
               } else {
                  CacheLinePad pad = (CacheLinePad)field.getAnnotation(CacheLinePad.class);
                  long byteOffset = advancingOffset;
                  if(meta != null && meta.byteOffset() != -1L) {
                     if(meta.byteOffset() < 0L) {
                        throw new ClassFormatError("Invalid field byte offset: " + className + "." + field.getName() + " [byteOffset=" + meta.byteOffset() + "]");
                     }

                     if(pad != null) {
                        throw new ClassFormatError("A field byte offset cannot be specified together with cache-line padding: " + className + "." + field.getName());
                     }

                     byteOffset = meta.byteOffset();
                  }

                  long byteLengthPadded = byteLength;
                  if(pad != null) {
                     if(pad.before() && byteOffset % (long)CacheUtil.getCacheLineSize() != 0L) {
                        byteOffset += (long)CacheUtil.getCacheLineSize() - (byteOffset & (long)(CacheUtil.getCacheLineSize() - 1));
                     }

                     if(pad.after() && (byteOffset + byteLength) % (long)CacheUtil.getCacheLineSize() != 0L) {
                        byteLengthPadded = byteLength + ((long)CacheUtil.getCacheLineSize() - (byteOffset + byteLength) % (long)CacheUtil.getCacheLineSize());
                     }

                     assert !pad.before() || byteOffset % (long)CacheUtil.getCacheLineSize() == 0L;

                     assert !pad.after() || (byteOffset + byteLengthPadded) % (long)CacheUtil.getCacheLineSize() == 0L;
                  }

                  if(PRINT_ACTIVITY) {
                     LWJGLUtil.log(MappedObjectTransformer.class.getSimpleName() + ": " + className + "." + field.getName() + " [type=" + field.getType().getSimpleName() + ", offset=" + byteOffset + "]");
                  }

                  return new MappedObjectTransformer.FieldInfo(byteOffset, byteLength, byteLengthPadded, Type.getType(field.getType()), Modifier.isVolatile(field.getModifiers()), pointer != null);
               }
            } else {
               throw new ClassFormatError("The volatile keyword is not supported for @Pointer or ByteBuffer fields. Volatile field found: " + className + "." + field.getName() + ": " + field.getType());
            }
         }
      }
   }

   static byte[] transformMappedObject(byte[] bytecode) {
      final ClassWriter cw = new ClassWriter(0);
      ClassVisitor cv = new ClassAdapter(cw) {
         private final String[] DEFINALIZE_LIST = new String[]{"getViewAddress", "next", "getAlign", "getSizeof", "capacity"};

         public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            for(String method : this.DEFINALIZE_LIST) {
               if(name.equals(method)) {
                  access &= -17;
                  break;
               }
            }

            return super.visitMethod(access, name, desc, signature, exceptions);
         }
      };
      (new ClassReader(bytecode)).accept(cv, 0);
      return cw.toByteArray();
   }

   static byte[] transformMappedAPI(String className, byte[] bytecode) {
      ClassWriter cw = new ClassWriter(2) {
         protected String getCommonSuperClass(String a, String b) {
            return (!MappedObjectTransformer.is_currently_computing_frames || a.startsWith("java/")) && b.startsWith("java/")?super.getCommonSuperClass(a, b):"java/lang/Object";
         }
      };
      MappedObjectTransformer.TransformationAdapter ta = new MappedObjectTransformer.TransformationAdapter(cw, className);
      ClassVisitor cv = ta;
      if(className_to_subtype.containsKey(className)) {
         cv = getMethodGenAdapter(className, ta);
      }

      (new ClassReader(bytecode)).accept(cv, 4);
      if(!ta.transformed) {
         return bytecode;
      } else {
         bytecode = cw.toByteArray();
         if(PRINT_BYTECODE) {
            printBytecode(bytecode);
         }

         return bytecode;
      }
   }

   private static ClassAdapter getMethodGenAdapter(final String className, final ClassVisitor cv) {
      return new ClassAdapter(cv) {
         public void visitEnd() {
            MappedObjectTransformer.MappedSubtypeInfo mappedSubtype = (MappedObjectTransformer.MappedSubtypeInfo)MappedObjectTransformer.className_to_subtype.get(className);
            this.generateViewAddressGetter();
            this.generateCapacity();
            this.generateAlignGetter(mappedSubtype);
            this.generateSizeofGetter();
            this.generateNext();

            for(String fieldName : mappedSubtype.fields.keySet()) {
               MappedObjectTransformer.FieldInfo field = (MappedObjectTransformer.FieldInfo)mappedSubtype.fields.get(fieldName);
               if(field.type.getDescriptor().length() > 1) {
                  this.generateByteBufferGetter(fieldName, field);
               } else {
                  this.generateFieldGetter(fieldName, field);
                  this.generateFieldSetter(fieldName, field);
               }
            }

            super.visitEnd();
         }

         private void generateViewAddressGetter() {
            MethodVisitor mv = super.visitMethod(1, "getViewAddress", "(I)J", (String)null, (String[])null);
            mv.visitCode();
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, MappedObjectTransformer.MAPPED_OBJECT_JVM, "baseAddress", "J");
            mv.visitVarInsn(21, 1);
            mv.visitFieldInsn(178, className, "SIZEOF", "I");
            mv.visitInsn(104);
            mv.visitInsn(133);
            mv.visitInsn(97);
            if(MappedObject.CHECKS) {
               mv.visitInsn(92);
               mv.visitVarInsn(25, 0);
               mv.visitMethodInsn(184, MappedObjectTransformer.MAPPED_HELPER_JVM, "checkAddress", "(JL" + MappedObjectTransformer.MAPPED_OBJECT_JVM + ";)V");
            }

            mv.visitInsn(173);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
         }

         private void generateCapacity() {
            MethodVisitor mv = super.visitMethod(1, "capacity", "()I", (String)null, (String[])null);
            mv.visitCode();
            mv.visitVarInsn(25, 0);
            mv.visitMethodInsn(182, MappedObjectTransformer.MAPPED_OBJECT_JVM, "backingByteBuffer", "()L" + MappedObjectTransformer.jvmClassName(ByteBuffer.class) + ";");
            mv.visitInsn(89);
            mv.visitMethodInsn(182, MappedObjectTransformer.jvmClassName(ByteBuffer.class), "capacity", "()I");
            mv.visitInsn(95);
            mv.visitMethodInsn(184, MappedObjectTransformer.jvmClassName(MemoryUtil.class), "getAddress0", "(L" + MappedObjectTransformer.jvmClassName(Buffer.class) + ";)J");
            mv.visitVarInsn(25, 0);
            mv.visitFieldInsn(180, MappedObjectTransformer.MAPPED_OBJECT_JVM, "baseAddress", "J");
            mv.visitInsn(101);
            mv.visitInsn(136);
            mv.visitInsn(96);
            mv.visitFieldInsn(178, className, "SIZEOF", "I");
            mv.visitInsn(108);
            mv.visitInsn(172);
            mv.visitMaxs(3, 1);
            mv.visitEnd();
         }

         private void generateAlignGetter(MappedObjectTransformer.MappedSubtypeInfo mappedSubtype) {
            MethodVisitor mv = super.visitMethod(1, "getAlign", "()I", (String)null, (String[])null);
            mv.visitCode();
            MappedObjectTransformer.visitIntNode(mv, mappedSubtype.sizeof);
            mv.visitInsn(172);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
         }

         private void generateSizeofGetter() {
            MethodVisitor mv = super.visitMethod(1, "getSizeof", "()I", (String)null, (String[])null);
            mv.visitCode();
            mv.visitFieldInsn(178, className, "SIZEOF", "I");
            mv.visitInsn(172);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
         }

         private void generateNext() {
            MethodVisitor mv = super.visitMethod(1, "next", "()V", (String)null, (String[])null);
            mv.visitCode();
            mv.visitVarInsn(25, 0);
            mv.visitInsn(89);
            mv.visitFieldInsn(180, MappedObjectTransformer.MAPPED_OBJECT_JVM, "viewAddress", "J");
            mv.visitFieldInsn(178, className, "SIZEOF", "I");
            mv.visitInsn(133);
            mv.visitInsn(97);
            mv.visitMethodInsn(182, className, "setViewAddress", "(J)V");
            mv.visitInsn(177);
            mv.visitMaxs(3, 1);
            mv.visitEnd();
         }

         private void generateByteBufferGetter(String fieldName, MappedObjectTransformer.FieldInfo field) {
            MethodVisitor mv = super.visitMethod(9, MappedObjectTransformer.getterName(fieldName), "(L" + className + ";I)" + field.type.getDescriptor(), (String)null, (String[])null);
            mv.visitCode();
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(21, 1);
            mv.visitMethodInsn(182, className, "getViewAddress", "(I)J");
            MappedObjectTransformer.visitIntNode(mv, (int)field.offset);
            mv.visitInsn(133);
            mv.visitInsn(97);
            MappedObjectTransformer.visitIntNode(mv, (int)field.length);
            mv.visitMethodInsn(184, MappedObjectTransformer.MAPPED_HELPER_JVM, "newBuffer", "(JI)L" + MappedObjectTransformer.jvmClassName(ByteBuffer.class) + ";");
            mv.visitInsn(176);
            mv.visitMaxs(3, 2);
            mv.visitEnd();
         }

         private void generateFieldGetter(String fieldName, MappedObjectTransformer.FieldInfo field) {
            MethodVisitor mv = super.visitMethod(9, MappedObjectTransformer.getterName(fieldName), "(L" + className + ";I)" + field.type.getDescriptor(), (String)null, (String[])null);
            mv.visitCode();
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(21, 1);
            mv.visitMethodInsn(182, className, "getViewAddress", "(I)J");
            MappedObjectTransformer.visitIntNode(mv, (int)field.offset);
            mv.visitInsn(133);
            mv.visitInsn(97);
            mv.visitMethodInsn(184, MappedObjectTransformer.MAPPED_HELPER_JVM, field.getAccessType() + "get", "(J)" + field.type.getDescriptor());
            mv.visitInsn(field.type.getOpcode(172));
            mv.visitMaxs(3, 2);
            mv.visitEnd();
         }

         private void generateFieldSetter(String fieldName, MappedObjectTransformer.FieldInfo field) {
            MethodVisitor mv = super.visitMethod(9, MappedObjectTransformer.setterName(fieldName), "(L" + className + ";I" + field.type.getDescriptor() + ")V", (String)null, (String[])null);
            mv.visitCode();
            int load = 0;
            switch(field.type.getSort()) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
               load = 21;
               break;
            case 6:
               load = 23;
               break;
            case 7:
               load = 22;
               break;
            case 8:
               load = 24;
            }

            mv.visitVarInsn(load, 2);
            mv.visitVarInsn(25, 0);
            mv.visitVarInsn(21, 1);
            mv.visitMethodInsn(182, className, "getViewAddress", "(I)J");
            MappedObjectTransformer.visitIntNode(mv, (int)field.offset);
            mv.visitInsn(133);
            mv.visitInsn(97);
            mv.visitMethodInsn(184, MappedObjectTransformer.MAPPED_HELPER_JVM, field.getAccessType() + "put", "(" + field.type.getDescriptor() + "J)V");
            mv.visitInsn(177);
            mv.visitMaxs(4, 4);
            mv.visitEnd();
         }
      };
   }

   static int transformMethodCall(InsnList instructions, int i, Map frameMap, MethodInsnNode methodInsn, MappedObjectTransformer.MappedSubtypeInfo mappedType, Map arrayVars) {
      switch(methodInsn.getOpcode()) {
      case 182:
         if("asArray".equals(methodInsn.name) && methodInsn.desc.equals("()[L" + MAPPED_OBJECT_JVM + ";")) {
            AbstractInsnNode nextInstruction;
            checkInsnAfterIsArray(nextInstruction = methodInsn.getNext(), 192);
            checkInsnAfterIsArray(nextInstruction = nextInstruction.getNext(), 58);
            Frame<BasicValue> frame = (Frame)frameMap.get(nextInstruction);
            String targetType = ((BasicValue)frame.getStack(frame.getStackSize() - 1)).getType().getElementType().getInternalName();
            if(!methodInsn.owner.equals(targetType)) {
               throw new ClassCastException("Source: " + methodInsn.owner + " - Target: " + targetType);
            }

            VarInsnNode varInstruction = (VarInsnNode)nextInstruction;
            arrayVars.put(Integer.valueOf(varInstruction.var), mappedType);
            instructions.remove(methodInsn.getNext());
            instructions.remove(methodInsn);
         }

         if("dup".equals(methodInsn.name) && methodInsn.desc.equals("()L" + MAPPED_OBJECT_JVM + ";")) {
            i = replace(instructions, i, methodInsn, generateDupInstructions(methodInsn));
         } else if("slice".equals(methodInsn.name) && methodInsn.desc.equals("()L" + MAPPED_OBJECT_JVM + ";")) {
            i = replace(instructions, i, methodInsn, generateSliceInstructions(methodInsn));
         } else if("runViewConstructor".equals(methodInsn.name) && "()V".equals(methodInsn.desc)) {
            i = replace(instructions, i, methodInsn, generateRunViewConstructorInstructions(methodInsn));
         } else if("copyTo".equals(methodInsn.name) && methodInsn.desc.equals("(L" + MAPPED_OBJECT_JVM + ";)V")) {
            i = replace(instructions, i, methodInsn, generateCopyToInstructions(mappedType));
         } else if("copyRange".equals(methodInsn.name) && methodInsn.desc.equals("(L" + MAPPED_OBJECT_JVM + ";I)V")) {
            i = replace(instructions, i, methodInsn, generateCopyRangeInstructions(mappedType));
         }
         break;
      case 183:
         if(methodInsn.owner.equals(MAPPED_OBJECT_JVM) && "<init>".equals(methodInsn.name) && "()V".equals(methodInsn.desc)) {
            instructions.remove(methodInsn.getPrevious());
            instructions.remove(methodInsn);
            i -= 2;
         }
         break;
      case 184:
         boolean isMapDirectMethod = "map".equals(methodInsn.name) && methodInsn.desc.equals("(JI)L" + MAPPED_OBJECT_JVM + ";");
         boolean isMapBufferMethod = "map".equals(methodInsn.name) && methodInsn.desc.equals("(Ljava/nio/ByteBuffer;)L" + MAPPED_OBJECT_JVM + ";");
         boolean isMallocMethod = "malloc".equals(methodInsn.name) && methodInsn.desc.equals("(I)L" + MAPPED_OBJECT_JVM + ";");
         if(isMapDirectMethod || isMapBufferMethod || isMallocMethod) {
            i = replace(instructions, i, methodInsn, generateMapInstructions(mappedType, methodInsn.owner, isMapDirectMethod, isMallocMethod));
         }
      }

      return i;
   }

   private static InsnList generateCopyRangeInstructions(MappedObjectTransformer.MappedSubtypeInfo mappedType) {
      InsnList list = new InsnList();
      list.add(getIntNode(mappedType.sizeof));
      list.add(new InsnNode(104));
      list.add(new MethodInsnNode(184, MAPPED_HELPER_JVM, "copy", "(L" + MAPPED_OBJECT_JVM + ";L" + MAPPED_OBJECT_JVM + ";I)V"));
      return list;
   }

   private static InsnList generateCopyToInstructions(MappedObjectTransformer.MappedSubtypeInfo mappedType) {
      InsnList list = new InsnList();
      list.add(getIntNode(mappedType.sizeof - mappedType.padding));
      list.add(new MethodInsnNode(184, MAPPED_HELPER_JVM, "copy", "(L" + MAPPED_OBJECT_JVM + ";L" + MAPPED_OBJECT_JVM + ";I)V"));
      return list;
   }

   private static InsnList generateRunViewConstructorInstructions(MethodInsnNode methodInsn) {
      InsnList list = new InsnList();
      list.add(new InsnNode(89));
      list.add(new MethodInsnNode(182, methodInsn.owner, "constructView$LWJGL", "()V"));
      return list;
   }

   private static InsnList generateSliceInstructions(MethodInsnNode methodInsn) {
      InsnList list = new InsnList();
      list.add(new TypeInsnNode(187, methodInsn.owner));
      list.add(new InsnNode(89));
      list.add(new MethodInsnNode(183, methodInsn.owner, "<init>", "()V"));
      list.add(new MethodInsnNode(184, MAPPED_HELPER_JVM, "slice", "(L" + MAPPED_OBJECT_JVM + ";L" + MAPPED_OBJECT_JVM + ";)L" + MAPPED_OBJECT_JVM + ";"));
      return list;
   }

   private static InsnList generateDupInstructions(MethodInsnNode methodInsn) {
      InsnList list = new InsnList();
      list.add(new TypeInsnNode(187, methodInsn.owner));
      list.add(new InsnNode(89));
      list.add(new MethodInsnNode(183, methodInsn.owner, "<init>", "()V"));
      list.add(new MethodInsnNode(184, MAPPED_HELPER_JVM, "dup", "(L" + MAPPED_OBJECT_JVM + ";L" + MAPPED_OBJECT_JVM + ";)L" + MAPPED_OBJECT_JVM + ";"));
      return list;
   }

   private static InsnList generateMapInstructions(MappedObjectTransformer.MappedSubtypeInfo mappedType, String className, boolean mapDirectMethod, boolean mallocMethod) {
      InsnList trg = new InsnList();
      if(mallocMethod) {
         trg.add(getIntNode(mappedType.sizeof));
         trg.add(new InsnNode(104));
         trg.add(new MethodInsnNode(184, mappedType.cacheLinePadded?jvmClassName(CacheUtil.class):jvmClassName(BufferUtils.class), "createByteBuffer", "(I)L" + jvmClassName(ByteBuffer.class) + ";"));
      } else if(mapDirectMethod) {
         trg.add(new MethodInsnNode(184, MAPPED_HELPER_JVM, "newBuffer", "(JI)L" + jvmClassName(ByteBuffer.class) + ";"));
      }

      trg.add(new TypeInsnNode(187, className));
      trg.add(new InsnNode(89));
      trg.add(new MethodInsnNode(183, className, "<init>", "()V"));
      trg.add(new InsnNode(90));
      trg.add(new InsnNode(95));
      trg.add(getIntNode(mappedType.align));
      trg.add(getIntNode(mappedType.sizeof));
      trg.add(new MethodInsnNode(184, MAPPED_HELPER_JVM, "setup", "(L" + MAPPED_OBJECT_JVM + ";Ljava/nio/ByteBuffer;II)V"));
      return trg;
   }

   static InsnList transformFieldAccess(FieldInsnNode fieldInsn) {
      MappedObjectTransformer.MappedSubtypeInfo mappedSubtype = (MappedObjectTransformer.MappedSubtypeInfo)className_to_subtype.get(fieldInsn.owner);
      if(mappedSubtype == null) {
         return "view".equals(fieldInsn.name) && fieldInsn.owner.startsWith(MAPPEDSET_PREFIX)?generateSetViewInstructions(fieldInsn):null;
      } else if("SIZEOF".equals(fieldInsn.name)) {
         return generateSIZEOFInstructions(fieldInsn, mappedSubtype);
      } else if("view".equals(fieldInsn.name)) {
         return generateViewInstructions(fieldInsn, mappedSubtype);
      } else if(!"baseAddress".equals(fieldInsn.name) && !"viewAddress".equals(fieldInsn.name)) {
         MappedObjectTransformer.FieldInfo field = (MappedObjectTransformer.FieldInfo)mappedSubtype.fields.get(fieldInsn.name);
         return field == null?null:(fieldInsn.desc.equals("L" + jvmClassName(ByteBuffer.class) + ";")?generateByteBufferInstructions(fieldInsn, mappedSubtype, field.offset):generateFieldInstructions(fieldInsn, field));
      } else {
         return generateAddressInstructions(fieldInsn);
      }
   }

   private static InsnList generateSetViewInstructions(FieldInsnNode fieldInsn) {
      if(fieldInsn.getOpcode() == 180) {
         throwAccessErrorOnReadOnlyField(fieldInsn.owner, fieldInsn.name);
      }

      if(fieldInsn.getOpcode() != 181) {
         throw new InternalError();
      } else {
         InsnList list = new InsnList();
         if(MAPPED_SET2_JVM.equals(fieldInsn.owner)) {
            list.add(new MethodInsnNode(184, MAPPED_HELPER_JVM, "put_views", "(L" + MAPPED_SET2_JVM + ";I)V"));
         } else if(MAPPED_SET3_JVM.equals(fieldInsn.owner)) {
            list.add(new MethodInsnNode(184, MAPPED_HELPER_JVM, "put_views", "(L" + MAPPED_SET3_JVM + ";I)V"));
         } else {
            if(!MAPPED_SET4_JVM.equals(fieldInsn.owner)) {
               throw new InternalError();
            }

            list.add(new MethodInsnNode(184, MAPPED_HELPER_JVM, "put_views", "(L" + MAPPED_SET4_JVM + ";I)V"));
         }

         return list;
      }
   }

   private static InsnList generateSIZEOFInstructions(FieldInsnNode fieldInsn, MappedObjectTransformer.MappedSubtypeInfo mappedSubtype) {
      if(!"I".equals(fieldInsn.desc)) {
         throw new InternalError();
      } else {
         InsnList list = new InsnList();
         if(fieldInsn.getOpcode() == 178) {
            list.add(getIntNode(mappedSubtype.sizeof));
            return list;
         } else {
            if(fieldInsn.getOpcode() == 179) {
               throwAccessErrorOnReadOnlyField(fieldInsn.owner, fieldInsn.name);
            }

            throw new InternalError();
         }
      }
   }

   private static InsnList generateViewInstructions(FieldInsnNode fieldInsn, MappedObjectTransformer.MappedSubtypeInfo mappedSubtype) {
      if(!"I".equals(fieldInsn.desc)) {
         throw new InternalError();
      } else {
         InsnList list = new InsnList();
         if(fieldInsn.getOpcode() == 180) {
            if(mappedSubtype.sizeof_shift != 0) {
               list.add(getIntNode(mappedSubtype.sizeof_shift));
               list.add(new MethodInsnNode(184, MAPPED_HELPER_JVM, "get_view_shift", "(L" + MAPPED_OBJECT_JVM + ";I)I"));
            } else {
               list.add(getIntNode(mappedSubtype.sizeof));
               list.add(new MethodInsnNode(184, MAPPED_HELPER_JVM, "get_view", "(L" + MAPPED_OBJECT_JVM + ";I)I"));
            }

            return list;
         } else if(fieldInsn.getOpcode() == 181) {
            if(mappedSubtype.sizeof_shift != 0) {
               list.add(getIntNode(mappedSubtype.sizeof_shift));
               list.add(new MethodInsnNode(184, MAPPED_HELPER_JVM, "put_view_shift", "(L" + MAPPED_OBJECT_JVM + ";II)V"));
            } else {
               list.add(getIntNode(mappedSubtype.sizeof));
               list.add(new MethodInsnNode(184, MAPPED_HELPER_JVM, "put_view", "(L" + MAPPED_OBJECT_JVM + ";II)V"));
            }

            return list;
         } else {
            throw new InternalError();
         }
      }
   }

   private static InsnList generateAddressInstructions(FieldInsnNode fieldInsn) {
      if(!"J".equals(fieldInsn.desc)) {
         throw new IllegalStateException();
      } else if(fieldInsn.getOpcode() == 180) {
         return null;
      } else {
         if(fieldInsn.getOpcode() == 181) {
            throwAccessErrorOnReadOnlyField(fieldInsn.owner, fieldInsn.name);
         }

         throw new InternalError();
      }
   }

   private static InsnList generateByteBufferInstructions(FieldInsnNode fieldInsn, MappedObjectTransformer.MappedSubtypeInfo mappedSubtype, long fieldOffset) {
      if(fieldInsn.getOpcode() == 181) {
         throwAccessErrorOnReadOnlyField(fieldInsn.owner, fieldInsn.name);
      }

      if(fieldInsn.getOpcode() == 180) {
         InsnList list = new InsnList();
         list.add(new FieldInsnNode(180, mappedSubtype.className, "viewAddress", "J"));
         list.add(new LdcInsnNode(Long.valueOf(fieldOffset)));
         list.add(new InsnNode(97));
         list.add(new LdcInsnNode(Long.valueOf(((MappedObjectTransformer.FieldInfo)mappedSubtype.fields.get(fieldInsn.name)).length)));
         list.add(new InsnNode(136));
         list.add(new MethodInsnNode(184, MAPPED_HELPER_JVM, "newBuffer", "(JI)L" + jvmClassName(ByteBuffer.class) + ";"));
         return list;
      } else {
         throw new InternalError();
      }
   }

   private static InsnList generateFieldInstructions(FieldInsnNode fieldInsn, MappedObjectTransformer.FieldInfo field) {
      InsnList list = new InsnList();
      if(fieldInsn.getOpcode() == 181) {
         list.add(getIntNode((int)field.offset));
         list.add(new MethodInsnNode(184, MAPPED_HELPER_JVM, field.getAccessType() + "put", "(L" + MAPPED_OBJECT_JVM + ";" + fieldInsn.desc + "I)V"));
         return list;
      } else if(fieldInsn.getOpcode() == 180) {
         list.add(getIntNode((int)field.offset));
         list.add(new MethodInsnNode(184, MAPPED_HELPER_JVM, field.getAccessType() + "get", "(L" + MAPPED_OBJECT_JVM + ";I)" + fieldInsn.desc));
         return list;
      } else {
         throw new InternalError();
      }
   }

   static int transformArrayAccess(InsnList instructions, int i, Map frameMap, VarInsnNode loadInsn, MappedObjectTransformer.MappedSubtypeInfo mappedSubtype, int var) {
      int loadStackSize = ((Frame)frameMap.get(loadInsn)).getStackSize() + 1;
      AbstractInsnNode nextInsn = loadInsn;

      while(true) {
         nextInsn = nextInsn.getNext();
         if(nextInsn == null) {
            throw new InternalError();
         }

         Frame<BasicValue> frame = (Frame)frameMap.get(nextInsn);
         if(frame != null) {
            int stackSize = frame.getStackSize();
            if(stackSize == loadStackSize + 1 && nextInsn.getOpcode() == 50) {
               AbstractInsnNode aaLoadInsn = nextInsn;

               while(true) {
                  nextInsn = nextInsn.getNext();
                  if(nextInsn == null) {
                     break;
                  }

                  frame = (Frame)frameMap.get(nextInsn);
                  if(frame != null) {
                     stackSize = frame.getStackSize();
                     if(stackSize == loadStackSize + 1 && nextInsn.getOpcode() == 181) {
                        FieldInsnNode fieldInsn = (FieldInsnNode)nextInsn;
                        instructions.insert(nextInsn, new MethodInsnNode(184, mappedSubtype.className, setterName(fieldInsn.name), "(L" + mappedSubtype.className + ";I" + fieldInsn.desc + ")V"));
                        instructions.remove(nextInsn);
                        break;
                     }

                     if(stackSize == loadStackSize && nextInsn.getOpcode() == 180) {
                        FieldInsnNode fieldInsn = (FieldInsnNode)nextInsn;
                        instructions.insert(nextInsn, new MethodInsnNode(184, mappedSubtype.className, getterName(fieldInsn.name), "(L" + mappedSubtype.className + ";I)" + fieldInsn.desc));
                        instructions.remove(nextInsn);
                        break;
                     }

                     if(stackSize == loadStackSize && nextInsn.getOpcode() == 89 && nextInsn.getNext().getOpcode() == 180) {
                        FieldInsnNode fieldInsn = (FieldInsnNode)nextInsn.getNext();
                        MethodInsnNode getter = new MethodInsnNode(184, mappedSubtype.className, getterName(fieldInsn.name), "(L" + mappedSubtype.className + ";I)" + fieldInsn.desc);
                        instructions.insert(nextInsn, new InsnNode(92));
                        instructions.insert(nextInsn.getNext(), getter);
                        instructions.remove(nextInsn);
                        instructions.remove(fieldInsn);
                        nextInsn = getter;
                     } else if(stackSize < loadStackSize) {
                        throw new ClassFormatError("Invalid " + mappedSubtype.className + " view array usage detected: " + getOpcodeName(nextInsn));
                     }
                  }
               }

               instructions.remove(aaLoadInsn);
               return i;
            }

            if(stackSize == loadStackSize && nextInsn.getOpcode() == 190) {
               if(LWJGLUtil.DEBUG && loadInsn.getNext() != nextInsn) {
                  throw new InternalError();
               }

               instructions.remove(nextInsn);
               loadInsn.var = var;
               instructions.insert(loadInsn, new MethodInsnNode(182, mappedSubtype.className, "capacity", "()I"));
               return i + 1;
            }

            if(stackSize < loadStackSize) {
               throw new ClassFormatError("Invalid " + mappedSubtype.className + " view array usage detected: " + getOpcodeName(nextInsn));
            }
         }
      }
   }

   private static void getClassEnums(Class clazz, Map map, String... prefixFilters) {
      try {
         for(Field field : clazz.getFields()) {
            if(Modifier.isStatic(field.getModifiers()) && field.getType() == Integer.TYPE) {
               String[] arr$ = prefixFilters;
               int len$ = prefixFilters.length;
               int i$ = 0;

               while(true) {
                  if(i$ >= len$) {
                     if(map.put((Integer)field.get((Object)null), field.getName()) != null) {
                        throw new IllegalStateException();
                     }
                     break;
                  }

                  String filter = arr$[i$];
                  if(field.getName().startsWith(filter)) {
                     break;
                  }

                  ++i$;
               }
            }
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }

   }

   static String getOpcodeName(AbstractInsnNode insn) {
      String op = (String)OPCODE_TO_NAME.get(Integer.valueOf(insn.getOpcode()));
      return (String)INSNTYPE_TO_NAME.get(Integer.valueOf(insn.getType())) + ": " + insn.getOpcode() + (op == null?"":" [" + (String)OPCODE_TO_NAME.get(Integer.valueOf(insn.getOpcode())) + "]");
   }

   static String jvmClassName(Class type) {
      return type.getName().replace('.', '/');
   }

   static String getterName(String fieldName) {
      return "get$" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1) + "$LWJGL";
   }

   static String setterName(String fieldName) {
      return "set$" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1) + "$LWJGL";
   }

   private static void checkInsnAfterIsArray(AbstractInsnNode instruction, int opcode) {
      if(instruction == null) {
         throw new ClassFormatError("Unexpected end of instructions after .asArray() method.");
      } else if(instruction.getOpcode() != opcode) {
         throw new ClassFormatError("The result of .asArray() must be stored to a local variable. Found: " + getOpcodeName(instruction));
      }
   }

   static AbstractInsnNode getIntNode(int value) {
      return (AbstractInsnNode)(value <= 5 && -1 <= value?new InsnNode(2 + value + 1):(value >= -128 && value <= 127?new IntInsnNode(16, value):(value >= -32768 && value <= 32767?new IntInsnNode(17, value):new LdcInsnNode(Integer.valueOf(value)))));
   }

   static void visitIntNode(MethodVisitor mv, int value) {
      if(value <= 5 && -1 <= value) {
         mv.visitInsn(2 + value + 1);
      } else if(value >= -128 && value <= 127) {
         mv.visitIntInsn(16, value);
      } else if(value >= -32768 && value <= 32767) {
         mv.visitIntInsn(17, value);
      } else {
         mv.visitLdcInsn(Integer.valueOf(value));
      }

   }

   static int replace(InsnList instructions, int i, AbstractInsnNode location, InsnList list) {
      int size = list.size();
      instructions.insert(location, list);
      instructions.remove(location);
      return i + (size - 1);
   }

   private static void throwAccessErrorOnReadOnlyField(String className, String fieldName) {
      throw new IllegalAccessError("The " + className + "." + fieldName + " field is final.");
   }

   private static void printBytecode(byte[] bytecode) {
      StringWriter sw = new StringWriter();
      ClassVisitor tracer = new TraceClassVisitor(new ClassWriter(0), new PrintWriter(sw));
      (new ClassReader(bytecode)).accept(tracer, 0);
      String dump = sw.toString();
      LWJGLUtil.log(dump);
   }

   static {
      getClassEnums(Opcodes.class, OPCODE_TO_NAME, new String[]{"V1_", "ACC_", "T_", "F_", "MH_"});
      getClassEnums(AbstractInsnNode.class, INSNTYPE_TO_NAME, new String[0]);
      className_to_subtype.put(MAPPED_OBJECT_JVM, new MappedObjectTransformer.MappedSubtypeInfo(MAPPED_OBJECT_JVM, (Map)null, -1, -1, -1, false));
      String vmName = System.getProperty("java.vm.name");
      if(vmName != null && !vmName.contains("Server")) {
         System.err.println("Warning: " + MappedObject.class.getSimpleName() + "s have inferiour performance on Client VMs, please consider switching to a Server VM.");
      }

   }

   private static class FieldInfo {
      final long offset;
      final long length;
      final long lengthPadded;
      final Type type;
      final boolean isVolatile;
      final boolean isPointer;

      FieldInfo(long offset, long length, long lengthPadded, Type type, boolean isVolatile, boolean isPointer) {
         this.offset = offset;
         this.length = length;
         this.lengthPadded = lengthPadded;
         this.type = type;
         this.isVolatile = isVolatile;
         this.isPointer = isPointer;
      }

      String getAccessType() {
         return this.isPointer?"a":this.type.getDescriptor().toLowerCase() + (this.isVolatile?"v":"");
      }
   }

   private static class MappedSubtypeInfo {
      final String className;
      final int sizeof;
      final int sizeof_shift;
      final int align;
      final int padding;
      final boolean cacheLinePadded;
      final Map fields;

      MappedSubtypeInfo(String className, Map fields, int sizeof, int align, int padding, boolean cacheLinePadded) {
         this.className = className;
         this.sizeof = sizeof;
         if((sizeof - 1 & sizeof) == 0) {
            this.sizeof_shift = getPoT(sizeof);
         } else {
            this.sizeof_shift = 0;
         }

         this.align = align;
         this.padding = padding;
         this.cacheLinePadded = cacheLinePadded;
         this.fields = fields;
      }

      private static int getPoT(int value) {
         int pot;
         for(pot = -1; value > 0; value >>= 1) {
            ++pot;
         }

         return pot;
      }
   }

   private static class TransformationAdapter extends ClassAdapter {
      final String className;
      boolean transformed;

      TransformationAdapter(ClassVisitor cv, String className) {
         super(cv);
         this.className = className;
      }

      public FieldVisitor visitField(final int access, final String name, final String desc, final String signature, final Object value) {
         MappedObjectTransformer.MappedSubtypeInfo mappedSubtype = (MappedObjectTransformer.MappedSubtypeInfo)MappedObjectTransformer.className_to_subtype.get(this.className);
         if(mappedSubtype != null && mappedSubtype.fields.containsKey(name)) {
            if(MappedObjectTransformer.PRINT_ACTIVITY) {
               LWJGLUtil.log(MappedObjectTransformer.class.getSimpleName() + ": discarding field: " + this.className + "." + name + ":" + desc);
            }

            return null;
         } else {
            return (FieldVisitor)((access & 8) == 0?new FieldNode(access, name, desc, signature, value) {
               public void visitEnd() {
                  if(this.visibleAnnotations == null) {
                     this.accept(TransformationAdapter.this.cv);
                  } else {
                     boolean before = false;
                     boolean after = false;
                     int byteLength = 0;

                     for(AnnotationNode pad : this.visibleAnnotations) {
                        if(MappedObjectTransformer.CACHE_LINE_PAD_JVM.equals(pad.desc)) {
                           if(!"J".equals(this.desc) && !"D".equals(this.desc)) {
                              if(!"I".equals(this.desc) && !"F".equals(this.desc)) {
                                 if(!"S".equals(this.desc) && !"C".equals(this.desc)) {
                                    if(!"B".equals(this.desc) && !"Z".equals(this.desc)) {
                                       throw new ClassFormatError("The @CacheLinePad annotation cannot be used on non-primitive fields: " + TransformationAdapter.this.className + "." + this.name);
                                    }

                                    byteLength = 1;
                                 } else {
                                    byteLength = 2;
                                 }
                              } else {
                                 byteLength = 4;
                              }
                           } else {
                              byteLength = 8;
                           }

                           TransformationAdapter.this.transformed = true;
                           after = true;
                           if(pad.values != null) {
                              for(int i = 0; i < pad.values.size(); i += 2) {
                                 boolean value = pad.values.get(i + 1).equals(Boolean.TRUE);
                                 if("before".equals(pad.values.get(i))) {
                                    before = value;
                                 } else {
                                    after = value;
                                 }
                              }
                           }
                           break;
                        }
                     }

                     if(before) {
                        int count = CacheUtil.getCacheLineSize() / byteLength - 1;

                        for(int i = count; i >= 1; --i) {
                           TransformationAdapter.this.cv.visitField(this.access | 1 | 4096, this.name + "$PAD_" + i, this.desc, this.signature, (Object)null);
                        }
                     }

                     this.accept(TransformationAdapter.this.cv);
                     if(after) {
                        int count = CacheUtil.getCacheLineSize() / byteLength - 1;

                        for(int i = 1; i <= count; ++i) {
                           TransformationAdapter.this.cv.visitField(this.access | 1 | 4096, this.name + "$PAD" + i, this.desc, this.signature, (Object)null);
                        }
                     }

                  }
               }
            }:super.visitField(access, name, desc, signature, value));
         }
      }

      public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
         if("<init>".equals(name)) {
            MappedObjectTransformer.MappedSubtypeInfo mappedSubtype = (MappedObjectTransformer.MappedSubtypeInfo)MappedObjectTransformer.className_to_subtype.get(this.className);
            if(mappedSubtype != null) {
               if(!"()V".equals(desc)) {
                  throw new ClassFormatError(this.className + " can only have a default constructor, found: " + desc);
               }

               MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
               mv.visitVarInsn(25, 0);
               mv.visitMethodInsn(183, MappedObjectTransformer.MAPPED_OBJECT_JVM, "<init>", "()V");
               mv.visitInsn(177);
               mv.visitMaxs(0, 0);
               name = "constructView$LWJGL";
            }
         }

         final MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
         return new MethodNode(access, name, desc, signature, exceptions) {
            boolean needsTransformation;

            public void visitMaxs(int a, int b) {
               try {
                  MappedObjectTransformer.is_currently_computing_frames = true;
                  super.visitMaxs(a, b);
               } finally {
                  MappedObjectTransformer.is_currently_computing_frames = false;
               }

            }

            public void visitFieldInsn(int opcode, String owner, String name, String desc) {
               if(MappedObjectTransformer.className_to_subtype.containsKey(owner) || owner.startsWith(MappedObjectTransformer.MAPPEDSET_PREFIX)) {
                  this.needsTransformation = true;
               }

               super.visitFieldInsn(opcode, owner, name, desc);
            }

            public void visitMethodInsn(int opcode, String owner, String name, String desc) {
               if(MappedObjectTransformer.className_to_subtype.containsKey(owner)) {
                  this.needsTransformation = true;
               }

               super.visitMethodInsn(opcode, owner, name, desc);
            }

            public void visitEnd() {
               if(this.needsTransformation) {
                  TransformationAdapter.this.transformed = true;

                  try {
                     this.transformMethod(this.analyse());
                  } catch (Exception var2) {
                     throw new RuntimeException(var2);
                  }
               }

               this.accept(mv);
            }

            private Frame[] analyse() throws AnalyzerException {
               Analyzer<BasicValue> a = new Analyzer(new SimpleVerifier());
               a.analyze(TransformationAdapter.this.className, this);
               return a.getFrames();
            }

            private void transformMethod(Frame[] frames) {
               InsnList instructions = this.instructions;
               Map<Integer, MappedObjectTransformer.MappedSubtypeInfo> arrayVars = new HashMap();
               Map<AbstractInsnNode, Frame<BasicValue>> frameMap = new HashMap();

               for(int i = 0; i < frames.length; ++i) {
                  frameMap.put(instructions.get(i), frames[i]);
               }

               for(int i = 0; i < instructions.size(); ++i) {
                  AbstractInsnNode instruction = instructions.get(i);
                  switch(instruction.getType()) {
                  case 2:
                     if(instruction.getOpcode() == 25) {
                        VarInsnNode varInsn = (VarInsnNode)instruction;
                        MappedObjectTransformer.MappedSubtypeInfo mappedSubtype = (MappedObjectTransformer.MappedSubtypeInfo)arrayVars.get(Integer.valueOf(varInsn.var));
                        if(mappedSubtype != null) {
                           i = MappedObjectTransformer.transformArrayAccess(instructions, i, frameMap, varInsn, mappedSubtype, varInsn.var);
                        }
                     }
                  case 3:
                  default:
                     break;
                  case 4:
                     FieldInsnNode fieldInsn = (FieldInsnNode)instruction;
                     InsnList list = MappedObjectTransformer.transformFieldAccess(fieldInsn);
                     if(list != null) {
                        i = MappedObjectTransformer.replace(instructions, i, instruction, list);
                     }
                     break;
                  case 5:
                     MethodInsnNode methodInsn = (MethodInsnNode)instruction;
                     MappedObjectTransformer.MappedSubtypeInfo mappedType = (MappedObjectTransformer.MappedSubtypeInfo)MappedObjectTransformer.className_to_subtype.get(methodInsn.owner);
                     if(mappedType != null) {
                        i = MappedObjectTransformer.transformMethodCall(instructions, i, frameMap, methodInsn, mappedType, arrayVars);
                     }
                  }
               }

            }
         };
      }
   }
}
