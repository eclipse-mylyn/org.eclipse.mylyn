// Copyright 2009 Google Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.gwtorm.protobuf;

import com.google.gwtorm.client.Column;
import com.google.gwtorm.client.OrmException;
import com.google.gwtorm.jdbc.gen.CodeGenSupport;
import com.google.gwtorm.jdbc.gen.GeneratedClassLoader;
import com.google.gwtorm.schema.ColumnModel;
import com.google.gwtorm.schema.Util;
import com.google.gwtorm.schema.java.JavaColumnModel;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.WireFormat;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;

/** Generates {@link ProtobufCodec} implementations. */
class CodecGen<T> implements Opcodes {
  private static final Type string = Type.getType(String.class);
  private static final Type byteStringOutput =
      Type.getType(ByteString.Output.class);
  private static final Type byteString = Type.getType(ByteString.class);
  private static final Type object = Type.getType(Object.class);
  private static final Type codedOutputStream =
      Type.getType(CodedOutputStream.class);
  private static final Type codedInputStream =
      Type.getType(CodedInputStream.class);
  private final GeneratedClassLoader classLoader;
  private final Class<T> pojo;
  private final Type pojoType;

  private ClassWriter cw;
  private JavaColumnModel[] myFields;
  private String superTypeName;
  private String implClassName;
  private String implTypeName;

  public CodecGen(final GeneratedClassLoader loader, final Class<T> t) {
    classLoader = loader;
    pojo = t;
    pojoType = Type.getType(pojo);
  }

  public ProtobufCodec<T> create() throws OrmException {
    myFields = scanFields(pojo);

    init();
    implementConstructor();
    implementSizeof();
    implementEncode();
    implementDecode();
    cw.visitEnd();
    classLoader.defineClass(implClassName, cw.toByteArray());

    try {
      final Class<?> c = Class.forName(implClassName, true, classLoader);
      return cast(c.newInstance());
    } catch (InstantiationException e) {
      throw new OrmException("Cannot create new encoder", e);
    } catch (IllegalAccessException e) {
      throw new OrmException("Cannot create new encoder", e);
    } catch (ClassNotFoundException e) {
      throw new OrmException("Cannot create new encoder", e);
    }
  }

  private static JavaColumnModel[] scanFields(Class<?> in) throws OrmException {
    final Collection<JavaColumnModel> col = new ArrayList<JavaColumnModel>();
    while (in != null) {
      for (final Field f : in.getDeclaredFields()) {
        if (f.getAnnotation(Column.class) != null) {
          col.add(new JavaColumnModel(f));
        }
      }
      in = in.getSuperclass();
    }
    return sort(col);
  }

  private static JavaColumnModel[] sort(
      final Collection<? extends ColumnModel> col) {
    JavaColumnModel[] out = col.toArray(new JavaColumnModel[col.size()]);
    Arrays.sort(out, new Comparator<JavaColumnModel>() {
      @Override
      public int compare(JavaColumnModel o1, JavaColumnModel o2) {
        return o1.getColumnID() - o2.getColumnID();
      }
    });
    return out;
  }

  @SuppressWarnings("unchecked")
  private static <T> ProtobufCodec<T> cast(final Object c) {
    return (ProtobufCodec<T>) c;
  }

  private void init() {
    superTypeName = Type.getInternalName(ProtobufCodec.class);
    implClassName = pojo.getName() + "_protobuf_" + Util.createRandomName();
    implTypeName = implClassName.replace('.', '/');

    cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    cw.visit(V1_3, ACC_PUBLIC | ACC_FINAL | ACC_SUPER, implTypeName, null,
        superTypeName, new String[] {});
  }

  private void implementConstructor() {
    final String consName = "<init>";
    final String consDesc =
        Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {});
    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC, consName, consDesc, null, null);
    mv.visitCode();

    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, superTypeName, consName, consDesc);

    mv.visitInsn(RETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  private void implementSizeof() throws OrmException {
    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC, "sizeof", Type.getMethodDescriptor(
            Type.INT_TYPE, new Type[] {object}), null, new String[] {});
    mv.visitCode();
    final SizeofCGS cgs = new SizeofCGS(mv);
    cgs.setEntityType(pojoType);

    mv.visitVarInsn(ALOAD, 1);
    mv.visitTypeInsn(CHECKCAST, pojoType.getInternalName());
    mv.visitVarInsn(ASTORE, 1);

    cgs.push(0);
    mv.visitVarInsn(ISTORE, cgs.sizeVar);
    sizeofMessage(myFields, mv, cgs);

    mv.visitVarInsn(ILOAD, cgs.sizeVar);
    mv.visitInsn(IRETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  private static void sizeofMessage(final JavaColumnModel[] myFields,
      final MethodVisitor mv, final SizeofCGS cgs) throws OrmException {
    for (final JavaColumnModel f : myFields) {
      if (f.isNested()) {
        final Label end = new Label();
        cgs.setFieldReference(f);
        cgs.pushFieldValue();
        mv.visitJumpInsn(IFNULL, end);

        final int oldVar = cgs.sizeVar;
        final int msgVar = cgs.newLocal();
        cgs.sizeVar = msgVar;
        cgs.push(0);
        mv.visitVarInsn(ISTORE, cgs.sizeVar);

        sizeofMessage(sort(f.getNestedColumns()), mv, cgs);
        cgs.sizeVar = oldVar;

        cgs.push(f.getColumnID());
        cgs.inc("computeTagSize", Type.INT_TYPE);

        mv.visitVarInsn(ILOAD, msgVar);
        cgs.inc("computeRawVarint32Size", Type.INT_TYPE);

        mv.visitVarInsn(ILOAD, msgVar);
        cgs.inc();

        cgs.freeLocal(msgVar);
        mv.visitLabel(end);
      } else {
        sizeofScalar(mv, cgs, f);
      }
    }
  }

  private static void sizeofScalar(final MethodVisitor mv, final SizeofCGS cgs,
      final JavaColumnModel f) throws OrmException {
    cgs.setFieldReference(f);

    switch (Type.getType(f.getPrimitiveType()).getSort()) {
      case Type.BOOLEAN:
        cgs.push(f.getColumnID());
        cgs.pushFieldValue();
        cgs.inc("computeBoolSize", Type.INT_TYPE, Type.BOOLEAN_TYPE);
        break;

      case Type.CHAR:
        cgs.push(f.getColumnID());
        cgs.pushFieldValue();
        cgs.inc("computeUInt32Size", Type.INT_TYPE, Type.INT_TYPE);
        break;

      case Type.BYTE:
      case Type.SHORT:
      case Type.INT:
        cgs.push(f.getColumnID());
        cgs.pushFieldValue();
        cgs.inc("computeSInt32Size", Type.INT_TYPE, Type.INT_TYPE);
        break;

      case Type.FLOAT:
        cgs.push(f.getColumnID());
        cgs.pushFieldValue();
        cgs.inc("computeFloatSize", Type.INT_TYPE, Type.FLOAT_TYPE);
        break;

      case Type.DOUBLE:
        cgs.push(f.getColumnID());
        cgs.pushFieldValue();
        cgs.inc("computeDoubleSize", Type.INT_TYPE, Type.DOUBLE_TYPE);
        break;

      case Type.LONG:
        cgs.push(f.getColumnID());
        cgs.pushFieldValue();
        cgs.inc("computeSInt64", Type.INT_TYPE, Type.LONG_TYPE);
        break;

      case Type.ARRAY:
      case Type.OBJECT: {
        final Label end = new Label();
        cgs.pushFieldValue();
        mv.visitJumpInsn(IFNULL, end);

        if (f.getPrimitiveType() == byte[].class) {
          cgs.push(f.getColumnID());
          cgs.inc("computeTagSize", Type.INT_TYPE);

          cgs.pushFieldValue();
          mv.visitInsn(ARRAYLENGTH);
          cgs.inc("computeRawVarint32Size", Type.INT_TYPE);

          cgs.pushFieldValue();
          mv.visitInsn(ARRAYLENGTH);
          cgs.inc();

        } else if (f.getPrimitiveType() == String.class) {
          cgs.push(f.getColumnID());
          cgs.pushFieldValue();
          cgs.inc("computeStringSize", Type.INT_TYPE, string);

        } else if (f.getPrimitiveType() == java.sql.Timestamp.class
            || f.getPrimitiveType() == java.util.Date.class
            || f.getPrimitiveType() == java.sql.Date.class) {
          cgs.push(f.getColumnID());
          String tsType = Type.getType(f.getPrimitiveType()).getInternalName();
          mv.visitMethodInsn(INVOKEVIRTUAL, tsType, "getTime", Type
              .getMethodDescriptor(Type.LONG_TYPE, new Type[] {}));
          cgs.inc("computeFixed64Size", Type.INT_TYPE, Type.LONG_TYPE);

        } else {
          throw new OrmException("Type " + f.getPrimitiveType()
              + " not supported for field " + f.getPathToFieldName());
        }
        mv.visitLabel(end);
        break;
      }

      default:
        throw new OrmException("Type " + f.getPrimitiveType()
            + " not supported for field " + f.getPathToFieldName());
    }
  }

  private void implementEncode() throws OrmException {
    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC, "encode", Type.getMethodDescriptor(
            byteString, new Type[] {object}), null, new String[] {});
    mv.visitCode();
    final EncodeCGS cgs = new EncodeCGS(mv);
    cgs.setEntityType(pojoType);

    mv.visitVarInsn(ALOAD, 1);
    mv.visitTypeInsn(CHECKCAST, pojoType.getInternalName());
    mv.visitVarInsn(ASTORE, 1);

    encodeMessage(myFields, mv, cgs);

    mv.visitInsn(ARETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  private static void encodeMessage(final JavaColumnModel[] myFields,
      final MethodVisitor mv, final EncodeCGS cgs) throws OrmException {
    final int oldVar = cgs.codedOutputStreamVar;
    cgs.codedOutputStreamVar = cgs.newLocal();

    final int strVar = cgs.newLocal();
    mv.visitMethodInsn(INVOKESTATIC, byteString.getInternalName(), "newOutput",
        Type.getMethodDescriptor(byteStringOutput, new Type[] {}));
    mv.visitVarInsn(ASTORE, strVar);

    mv.visitVarInsn(ALOAD, strVar);
    mv.visitMethodInsn(INVOKESTATIC, codedOutputStream.getInternalName(),
        "newInstance", Type.getMethodDescriptor(codedOutputStream,
            new Type[] {Type.getType(OutputStream.class)}));
    mv.visitVarInsn(ASTORE, cgs.codedOutputStreamVar);

    for (final JavaColumnModel f : myFields) {
      if (f.isNested()) {
        final Label end = new Label();
        cgs.setFieldReference(f);
        cgs.pushFieldValue();
        mv.visitJumpInsn(IFNULL, end);

        final int v = cgs.newLocal();
        encodeMessage(sort(f.getNestedColumns()), mv, cgs);
        mv.visitVarInsn(ASTORE, v);

        mv.visitVarInsn(ALOAD, v);
        mv.visitMethodInsn(INVOKEVIRTUAL, byteString.getInternalName(), "size",
            Type.getMethodDescriptor(Type.INT_TYPE, new Type[] {}));
        mv.visitJumpInsn(IFEQ, end);

        cgs.pushCodedOutputStream();
        cgs.push(f.getColumnID());
        mv.visitVarInsn(ALOAD, v);
        cgs.write("writeBytes", byteString);

        cgs.freeLocal(v);
        mv.visitLabel(end);
      } else {
        encodeScalar(mv, cgs, f);
      }
    }

    cgs.pushCodedOutputStream();
    mv.visitMethodInsn(INVOKEVIRTUAL, codedOutputStream.getInternalName(),
        "flush", Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {}));

    cgs.freeLocal(cgs.codedOutputStreamVar);
    cgs.codedOutputStreamVar = oldVar;

    mv.visitVarInsn(ALOAD, strVar);
    mv.visitMethodInsn(INVOKEVIRTUAL, byteStringOutput.getInternalName(),
        "toByteString", Type.getMethodDescriptor(byteString, new Type[] {}));
    cgs.freeLocal(strVar);
  }

  private static void encodeScalar(final MethodVisitor mv, final EncodeCGS cgs,
      final JavaColumnModel f) throws OrmException {
    cgs.setFieldReference(f);

    switch (Type.getType(f.getPrimitiveType()).getSort()) {
      case Type.BOOLEAN:
        cgs.pushCodedOutputStream();
        cgs.push(f.getColumnID());
        cgs.pushFieldValue();
        cgs.write("writeBool", Type.BOOLEAN_TYPE);
        break;

      case Type.CHAR:
        cgs.pushCodedOutputStream();
        cgs.push(f.getColumnID());
        cgs.pushFieldValue();
        cgs.write("writeUInt32", Type.INT_TYPE);
        break;

      case Type.BYTE:
      case Type.SHORT:
      case Type.INT:
        cgs.pushCodedOutputStream();
        cgs.push(f.getColumnID());
        cgs.pushFieldValue();
        cgs.write("writeSInt32", Type.INT_TYPE);
        break;

      case Type.FLOAT:
        cgs.pushCodedOutputStream();
        cgs.push(f.getColumnID());
        cgs.pushFieldValue();
        cgs.write("writeFloat", Type.FLOAT_TYPE);
        break;

      case Type.DOUBLE:
        cgs.pushCodedOutputStream();
        cgs.push(f.getColumnID());
        cgs.pushFieldValue();
        cgs.write("writeDouble", Type.DOUBLE_TYPE);
        break;

      case Type.LONG:
        cgs.pushCodedOutputStream();
        cgs.push(f.getColumnID());
        cgs.pushFieldValue();
        cgs.write("writeSInt64", Type.LONG_TYPE);
        break;

      case Type.ARRAY:
      case Type.OBJECT: {
        final Label end = new Label();
        cgs.pushFieldValue();
        mv.visitJumpInsn(IFNULL, end);

        if (f.getPrimitiveType() == byte[].class) {
          cgs.pushCodedOutputStream();
          cgs.push(f.getColumnID());
          cgs.push(WireFormat.FieldType.BYTES.getWireType());
          mv.visitMethodInsn(INVOKEVIRTUAL,
              codedOutputStream.getInternalName(), "writeTag", Type
                  .getMethodDescriptor(Type.VOID_TYPE, new Type[] {
                      Type.INT_TYPE, Type.INT_TYPE}));

          cgs.pushCodedOutputStream();
          cgs.pushFieldValue();
          mv.visitInsn(ARRAYLENGTH);
          mv.visitMethodInsn(INVOKEVIRTUAL,
              codedOutputStream.getInternalName(), "writeRawVarint32", Type
                  .getMethodDescriptor(Type.VOID_TYPE,
                      new Type[] {Type.INT_TYPE}));

          cgs.pushCodedOutputStream();
          cgs.pushFieldValue();
          mv.visitMethodInsn(INVOKEVIRTUAL,
              codedOutputStream.getInternalName(), "writeRawBytes", Type
                  .getMethodDescriptor(Type.VOID_TYPE, new Type[] {Type
                      .getType(byte[].class)}));

        } else {
          cgs.pushCodedOutputStream();
          cgs.push(f.getColumnID());
          cgs.pushFieldValue();

          if (f.getPrimitiveType() == String.class) {
            cgs.write("writeString", string);

          } else if (f.getPrimitiveType() == java.sql.Timestamp.class
              || f.getPrimitiveType() == java.util.Date.class
              || f.getPrimitiveType() == java.sql.Date.class) {
            String tsType =
                Type.getType(f.getPrimitiveType()).getInternalName();
            mv.visitMethodInsn(INVOKEVIRTUAL, tsType, "getTime", Type
                .getMethodDescriptor(Type.LONG_TYPE, new Type[] {}));
            cgs.write("writeFixed64", Type.LONG_TYPE);

          } else {
            throw new OrmException("Type " + f.getPrimitiveType()
                + " not supported for field " + f.getPathToFieldName());
          }
        }
        mv.visitLabel(end);
        break;
      }

      default:
        throw new OrmException("Type " + f.getPrimitiveType()
            + " not supported for field " + f.getPathToFieldName());
    }
  }

  private void implementDecode() throws OrmException {
    final Type retType = object;
    final MethodVisitor mv =
        cw.visitMethod(ACC_PROTECTED, "decode", Type.getMethodDescriptor(
            retType, new Type[] {codedInputStream}), null, new String[] {});
    mv.visitCode();
    final DecodeCGS cgs = new DecodeCGS(mv);

    cgs.setEntityType(pojoType);

    mv.visitTypeInsn(NEW, pojoType.getInternalName());
    mv.visitInsn(DUP);
    mv.visitMethodInsn(INVOKESPECIAL, pojoType.getInternalName(), "<init>",
        Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {}));
    mv.visitVarInsn(ASTORE, cgs.objVar);

    final int tagVar = cgs.newLocal();
    decodeMessage(myFields, mv, cgs);

    cgs.pushEntity();
    mv.visitInsn(ARETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  private static void decodeMessage(final JavaColumnModel[] myFields,
      final MethodVisitor mv, final DecodeCGS cgs) throws OrmException {
    final Label nextField = new Label();
    final Label end = new Label();
    mv.visitLabel(nextField);

    // while (!ci.isAtEnd) { ...
    cgs.call("readTag", Type.INT_TYPE);
    mv.visitInsn(DUP);
    mv.visitVarInsn(ISTORE, cgs.tagVar);

    cgs.push(3);
    mv.visitInsn(IUSHR);

    final Label badField = new Label();
    final int[] caseTags = new int[1 + myFields.length];
    final Label[] caseLabels = new Label[caseTags.length];

    caseTags[0] = 0;
    caseLabels[0] = new Label();

    int gaps = 0;
    for (int i = 1; i < caseTags.length; i++) {
      caseTags[i] = myFields[i - 1].getColumnID();
      caseLabels[i] = new Label();
      gaps += caseTags[i] - (caseTags[i - 1] + 1);
    }

    if (2 * gaps / 3 <= myFields.length) {
      final int min = 0;
      final int max = caseTags[caseTags.length - 1];
      final Label[] table = new Label[max + 1];
      Arrays.fill(table, badField);
      for (int idx = 0; idx < caseTags.length; idx++) {
        table[caseTags[idx]] = caseLabels[idx];
      }
      mv.visitTableSwitchInsn(min, max, badField, table);
    } else {
      mv.visitLookupSwitchInsn(badField, caseTags, caseLabels);
    }

    mv.visitLabel(caseLabels[0]);
    mv.visitJumpInsn(GOTO, end);

    for (int idx = 1; idx < caseTags.length; idx++) {
      final JavaColumnModel f = myFields[idx - 1];
      mv.visitLabel(caseLabels[idx]);
      if (f.isNested()) {
        final Label load = new Label();
        cgs.setFieldReference(f);
        cgs.pushFieldValue();
        mv.visitJumpInsn(IFNONNULL, load);
        cgs.fieldSetBegin();
        mv.visitTypeInsn(NEW, Type.getType(f.getNestedClass())
            .getInternalName());
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, Type.getType(f.getNestedClass())
            .getInternalName(), "<init>", Type.getMethodDescriptor(
            Type.VOID_TYPE, new Type[] {}));
        cgs.fieldSetEnd();

        // read the length, set a new limit, decode the message, validate
        // we stopped at the end of it as expected.
        //
        mv.visitLabel(load);
        final int limitVar = cgs.newLocal();
        cgs.pushCodedInputStream();
        cgs.call("readRawVarint32", Type.INT_TYPE);
        cgs.ncallInt("pushLimit", Type.INT_TYPE);
        mv.visitVarInsn(ISTORE, limitVar);

        decodeMessage(sort(f.getNestedColumns()), mv, cgs);

        cgs.pushCodedInputStream();
        mv.visitVarInsn(ILOAD, limitVar);
        cgs.ncallInt("popLimit", Type.VOID_TYPE);
        cgs.freeLocal(limitVar);

      } else {
        decodeScalar(mv, cgs, f);
      }
      mv.visitJumpInsn(GOTO, nextField);
    }

    // default:
    mv.visitLabel(badField);
    cgs.pushCodedInputStream();
    mv.visitVarInsn(ILOAD, cgs.tagVar);
    cgs.ncallInt("skipField", Type.BOOLEAN_TYPE);
    mv.visitInsn(POP);
    mv.visitJumpInsn(GOTO, nextField);

    mv.visitLabel(end);
    cgs.pushCodedInputStream();
    cgs.push(0);
    cgs.ncallInt("checkLastTagWas", Type.VOID_TYPE);
  }

  private static void decodeScalar(final MethodVisitor mv, final DecodeCGS cgs,
      final JavaColumnModel f) throws OrmException {
    cgs.setFieldReference(f);
    cgs.fieldSetBegin();
    switch (Type.getType(f.getPrimitiveType()).getSort()) {
      case Type.BOOLEAN:
        cgs.call("readBool", Type.BOOLEAN_TYPE);
        break;

      case Type.CHAR:
        cgs.call("readUInt32", Type.INT_TYPE);
        break;

      case Type.BYTE:
      case Type.SHORT:
      case Type.INT:
        cgs.call("readSInt32", Type.INT_TYPE);
        break;

      case Type.FLOAT:
        cgs.call("readFloat", Type.FLOAT_TYPE);
        break;

      case Type.DOUBLE:
        cgs.call("readDouble", Type.DOUBLE_TYPE);
        break;

      case Type.LONG:
        cgs.call("readSInt64", Type.LONG_TYPE);
        break;

      default:
        if (f.getPrimitiveType() == byte[].class) {
          cgs.call("readBytes", byteString);
          mv.visitMethodInsn(INVOKEVIRTUAL, byteString.getInternalName(),
              "toByteArray", Type.getMethodDescriptor(Type
                  .getType(byte[].class), new Type[] {}));

        } else if (f.getPrimitiveType() == String.class) {
          cgs.call("readString", string);

        } else if (f.getPrimitiveType() == java.sql.Timestamp.class
            || f.getPrimitiveType() == java.util.Date.class
            || f.getPrimitiveType() == java.sql.Date.class) {
          String tsType = Type.getType(f.getPrimitiveType()).getInternalName();
          mv.visitTypeInsn(NEW, tsType);
          mv.visitInsn(DUP);
          cgs.call("readFixed64", Type.LONG_TYPE);
          mv.visitMethodInsn(INVOKESPECIAL, tsType, "<init>", Type
              .getMethodDescriptor(Type.VOID_TYPE, new Type[] {}));

        } else {
          throw new OrmException("Type " + f.getPrimitiveType()
              + " not supported for field " + f.getPathToFieldName());
        }
        break;
    }
    cgs.fieldSetEnd();
  }

  private static final class SizeofCGS extends CodeGenSupport {
    int sizeVar;

    private SizeofCGS(MethodVisitor method) {
      super(method);
      sizeVar = newLocal();
    }

    void inc(String name, Type... args) {
      mv.visitMethodInsn(INVOKESTATIC, codedOutputStream.getInternalName(),
          name, Type.getMethodDescriptor(Type.INT_TYPE, args));
      inc();
    }

    void inc() {
      mv.visitVarInsn(ILOAD, sizeVar);
      mv.visitInsn(IADD);
      mv.visitVarInsn(ISTORE, sizeVar);
    }

    @Override
    public void pushEntity() {
      mv.visitVarInsn(ALOAD, 1);
    }
  }

  private static final class EncodeCGS extends CodeGenSupport {
    int codedOutputStreamVar;

    private EncodeCGS(MethodVisitor method) {
      super(method);
    }

    void pushCodedOutputStream() {
      mv.visitVarInsn(ALOAD, codedOutputStreamVar);
    }

    void write(String name, Type arg) {
      mv.visitMethodInsn(INVOKEVIRTUAL, codedOutputStream.getInternalName(),
          name, Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {
              Type.INT_TYPE, arg}));
    }

    @Override
    public void pushEntity() {
      mv.visitVarInsn(ALOAD, 1);
    }
  }

  private static final class DecodeCGS extends CodeGenSupport {
    final int codedInputStreamVar = 1;
    final int objVar;
    final int tagVar;

    private DecodeCGS(MethodVisitor method) {
      super(method);
      objVar = newLocal();
      tagVar = newLocal();
    }

    void pushCodedInputStream() {
      mv.visitVarInsn(ALOAD, codedInputStreamVar);
    }

    void call(String name, Type ret) {
      pushCodedInputStream();
      mv.visitMethodInsn(INVOKEVIRTUAL, codedInputStream.getInternalName(),
          name, Type.getMethodDescriptor(ret, new Type[] {}));
    }

    void ncallInt(String name, Type ret) {
      mv.visitMethodInsn(INVOKEVIRTUAL, codedInputStream.getInternalName(),
          name, Type.getMethodDescriptor(ret, new Type[] {Type.INT_TYPE}));
    }

    @Override
    public void pushEntity() {
      mv.visitVarInsn(ALOAD, objVar);
    }
  }
}
