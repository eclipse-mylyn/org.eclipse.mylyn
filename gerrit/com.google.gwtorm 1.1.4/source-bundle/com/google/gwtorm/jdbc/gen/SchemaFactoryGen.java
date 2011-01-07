// Copyright 2008 Google Inc.
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

package com.google.gwtorm.jdbc.gen;

import com.google.gwtorm.client.OrmException;
import com.google.gwtorm.client.Schema;
import com.google.gwtorm.jdbc.AbstractSchemaFactory;
import com.google.gwtorm.jdbc.Database;
import com.google.gwtorm.schema.Util;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.sql.Connection;

/** Generates a factory to efficiently create new Schema instances. */
public class SchemaFactoryGen<T extends Schema> implements Opcodes {
  private final GeneratedClassLoader classLoader;
  private final SchemaGen schemaGen;
  private ClassWriter cw;
  private String superTypeName;
  private String implClassName;
  private String implTypeName;

  public SchemaFactoryGen(final GeneratedClassLoader loader, final SchemaGen gen) {
    classLoader = loader;
    schemaGen = gen;
  }

  public void defineClass() throws OrmException {
    init();
    implementEmptyConstructor();
    implementCreate();
    cw.visitEnd();
    classLoader.defineClass(implClassName, cw.toByteArray());
  }

  public AbstractSchemaFactory<T> create() throws OrmException {
    defineClass();
    try {
      return cast(Class.forName(implClassName, true, classLoader).newInstance());
    } catch (InstantiationException e) {
      throw new OrmException("Cannot create schema factory", e);
    } catch (IllegalAccessException e) {
      throw new OrmException("Cannot create schema factory", e);
    } catch (ClassNotFoundException e) {
      throw new OrmException("Cannot create schema factory", e);
    }
  }

  @SuppressWarnings("unchecked")
  private AbstractSchemaFactory<T> cast(final Object newInstance) {
    return (AbstractSchemaFactory<T>) newInstance;
  }

  private void init() {
    superTypeName = Type.getInternalName(AbstractSchemaFactory.class);
    implClassName =
        schemaGen.getSchemaClassName() + "_Factory_" + Util.createRandomName();
    implTypeName = implClassName.replace('.', '/');

    cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    cw.visit(V1_3, ACC_PUBLIC | ACC_FINAL | ACC_SUPER, implTypeName, null,
        superTypeName, null);
  }

  private void implementEmptyConstructor() {
    final String consName = "<init>";
    final String consDesc =
        Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {});
    final MethodVisitor mv;
    mv = cw.visitMethod(ACC_PUBLIC, consName, consDesc, null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(INVOKESPECIAL, superTypeName, consName, consDesc);
    mv.visitInsn(RETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  private void implementCreate() {
    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC | ACC_FINAL, "create", Type
            .getMethodDescriptor(Type.getType(Schema.class), new Type[] {
                Type.getType(Database.class), Type.getType(Connection.class)}),
            null, null);
    mv.visitCode();

    mv.visitTypeInsn(NEW, schemaGen.getImplTypeName());
    mv.visitInsn(DUP);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitVarInsn(ALOAD, 2);
    mv.visitMethodInsn(INVOKESPECIAL, schemaGen.getImplTypeName(), "<init>",
        Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {
            Type.getType(Database.class), Type.getType(Connection.class)}));
    mv.visitInsn(ARETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }
}
