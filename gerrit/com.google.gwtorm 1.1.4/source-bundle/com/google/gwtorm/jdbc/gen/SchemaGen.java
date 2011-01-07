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
import com.google.gwtorm.jdbc.Database;
import com.google.gwtorm.jdbc.JdbcSchema;
import com.google.gwtorm.schema.RelationModel;
import com.google.gwtorm.schema.SequenceModel;
import com.google.gwtorm.schema.Util;
import com.google.gwtorm.schema.java.JavaSchemaModel;
import com.google.gwtorm.schema.sql.SqlDialect;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/** Generates a concrete implementation of a {@link Schema} extension. */
public class SchemaGen implements Opcodes {
  private final GeneratedClassLoader classLoader;
  private final JavaSchemaModel schema;
  private final SqlDialect dialect;
  private List<RelationGen> relations;
  private ClassWriter cw;
  private String superTypeName;
  private String implClassName;
  private String implTypeName;

  public SchemaGen(final GeneratedClassLoader loader,
      final JavaSchemaModel schemaModel, final SqlDialect sqlDialect) {
    classLoader = loader;
    schema = schemaModel;
    dialect = sqlDialect;
  }

  public void defineClass() throws OrmException {
    defineRelationClasses();

    init();
    implementRelationFields();
    implementConstructor();
    implementSequenceMethods();
    implementRelationMethods();
    cw.visitEnd();
    classLoader.defineClass(getImplClassName(), cw.toByteArray());
  }

  String getSchemaClassName() {
    return schema.getSchemaClassName();
  }

  String getImplClassName() {
    return implClassName;
  }

  String getImplTypeName() {
    return implTypeName;
  }

  private void defineRelationClasses() throws OrmException {
    relations = new ArrayList<RelationGen>();
    for (final RelationModel rel : schema.getRelations()) {
      final RelationGen g = new RelationGen(rel);
      relations.add(g);
      new AccessGen(classLoader, g).defineClass();
    }
  }

  private void init() {
    superTypeName = Type.getInternalName(JdbcSchema.class);
    implClassName = getSchemaClassName() + "_Schema_" + Util.createRandomName();
    implTypeName = implClassName.replace('.', '/');

    cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    cw.visit(V1_3, ACC_PUBLIC | ACC_FINAL | ACC_SUPER, implTypeName, null,
        superTypeName, new String[] {getSchemaClassName().replace('.', '/')});
  }

  private void implementRelationFields() {
    for (final RelationGen info : relations) {
      info.implementField();
    }
  }

  private void implementConstructor() {
    final String consName = "<init>";
    final String consDesc =
        Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {
            Type.getType(Database.class), Type.getType(Connection.class)});
    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC, consName, consDesc, null, null);
    mv.visitCode();

    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitVarInsn(ALOAD, 2);
    mv.visitMethodInsn(INVOKESPECIAL, superTypeName, consName, consDesc);

    for (final RelationGen info : relations) {
      mv.visitVarInsn(ALOAD, 0);
      mv.visitTypeInsn(NEW, info.accessType.getInternalName());
      mv.visitInsn(DUP);
      mv.visitVarInsn(ALOAD, 0);
      mv.visitMethodInsn(INVOKESPECIAL, info.accessType.getInternalName(),
          consName, Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {Type
              .getType(JdbcSchema.class)}));
      mv.visitFieldInsn(PUTFIELD, implTypeName, info
          .getAccessInstanceFieldName(), info.accessType.getDescriptor());
    }

    mv.visitInsn(RETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  private void implementSequenceMethods() {
    for (final SequenceModel seq : schema.getSequences()) {
      final Type retType = Type.getType(seq.getResultType());
      final MethodVisitor mv =
          cw
              .visitMethod(ACC_PUBLIC, seq.getMethodName(), Type
                  .getMethodDescriptor(retType, new Type[] {}), null,
                  new String[] {Type.getType(OrmException.class)
                      .getInternalName()});
      mv.visitCode();

      mv.visitVarInsn(ALOAD, 0);
      mv.visitLdcInsn(dialect.getNextSequenceValueSql(seq.getSequenceName()));
      mv.visitMethodInsn(INVOKEVIRTUAL, superTypeName, "nextLong", Type
          .getMethodDescriptor(Type.getType(Long.TYPE), new Type[] {Type
              .getType(String.class)}));
      if (retType.getSize() == 1) {
        mv.visitInsn(L2I);
        mv.visitInsn(IRETURN);
      } else {
        mv.visitInsn(LRETURN);
      }
      mv.visitMaxs(-1, -1);
      mv.visitEnd();
    }
  }

  private void implementRelationMethods() {
    for (final RelationGen info : relations) {
      info.implementMethod();
    }
  }

  class RelationGen {
    final RelationModel model;
    String accessClassName;
    Type accessType;

    RelationGen(final RelationModel model) {
      this.model = model;
    }

    SqlDialect getDialect() {
      return SchemaGen.this.dialect;
    }

    void implementField() {
      accessType = Type.getObjectType(accessClassName.replace('.', '/'));
      cw.visitField(ACC_PRIVATE | ACC_FINAL, getAccessInstanceFieldName(),
          accessType.getDescriptor(), null, null).visitEnd();
    }

    String getAccessInstanceFieldName() {
      return "access_" + model.getMethodName();
    }

    void implementMethod() {
      final MethodVisitor mv =
          cw.visitMethod(ACC_PUBLIC | ACC_FINAL, model.getMethodName(), Type
              .getMethodDescriptor(Type.getObjectType(model
                  .getAccessInterfaceName().replace('.', '/')), new Type[] {}),
              null, null);
      mv.visitCode();
      mv.visitVarInsn(ALOAD, 0);
      mv.visitFieldInsn(GETFIELD, implTypeName, getAccessInstanceFieldName(),
          accessType.getDescriptor());
      mv.visitInsn(ARETURN);
      mv.visitMaxs(-1, -1);
      mv.visitEnd();
    }
  }
}
