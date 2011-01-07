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

import com.google.gwtorm.client.Access;
import com.google.gwtorm.client.Key;
import com.google.gwtorm.client.OrmException;
import com.google.gwtorm.client.impl.ListResultSet;
import com.google.gwtorm.jdbc.JdbcAccess;
import com.google.gwtorm.jdbc.JdbcSchema;
import com.google.gwtorm.schema.ColumnModel;
import com.google.gwtorm.schema.KeyModel;
import com.google.gwtorm.schema.QueryModel;
import com.google.gwtorm.schema.RelationModel;
import com.google.gwtorm.schema.Util;
import com.google.gwtorm.schema.sql.SqlDialect;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/** Generates a concrete implementation of an {@link Access} extension. */
public class AccessGen implements Opcodes {
  private static final String REL_ALIAS = "T";

  private static enum DmlType {
    INSERT("bindOneInsert"),

    UPDATE("bindOneUpdate"),

    DELETE("bindOneDelete");

    final String methodName;

    DmlType(final String m) {
      methodName = m;
    }
  }

  private final GeneratedClassLoader classLoader;
  private final SchemaGen.RelationGen info;
  private final RelationModel model;
  private final SqlDialect dialect;

  private ClassWriter cw;
  private String superTypeName;
  private String implClassName;
  private String implTypeName;
  private Type entityType;


  public AccessGen(final GeneratedClassLoader loader,
      final SchemaGen.RelationGen ri) {
    classLoader = loader;
    info = ri;
    model = info.model;
    dialect = ri.getDialect();
    entityType =
        Type.getObjectType(model.getEntityTypeClassName().replace('.', '/'));
  }

  public void defineClass() throws OrmException {
    init();
    implementConstructor();
    implementGetString("getRelationName", model.getRelationName());
    implementGetString("getInsertOneSql", model.getInsertOneSql(dialect));

    if (model.getPrimaryKey() != null) {
      if (model.getDependentColumns().isEmpty()) {
        implementMissingGetString("getUpdateOneSql", "update");
      } else {
        implementGetString("getUpdateOneSql", model.getUpdateOneSql(dialect));
      }
      implementGetString("getDeleteOneSql", model.getDeleteOneSql(dialect));
    } else {
      implementMissingGetString("getUpdateOneSql", "update");
      implementMissingGetString("getDeleteOneSql", "delete");
    }

    implementPrimaryKey();
    implementGetOne();
    implementNewEntityInstance();
    implementBindOne(DmlType.INSERT);
    implementBindOne(DmlType.UPDATE);
    implementBindOne(DmlType.DELETE);
    implementBindOneFetch();

    if (model.getPrimaryKey() != null) {
      implementKeyQuery(model.getPrimaryKey());
      if ((model.getPrimaryKey().getField().isNested() || !model
          .getPrimaryKey().getField().getPrimitiveType().isPrimitive())
          && model.getPrimaryKey().getAllLeafColumns().size() == 1) {
        overrideGetMany();
      }
    }

    for (final QueryModel q : model.getQueries()) {
      implementQuery(q);
    }

    cw.visitEnd();
    classLoader.defineClass(implClassName, cw.toByteArray());
    info.accessClassName = implClassName;
  }


  private void init() {
    superTypeName = Type.getInternalName(JdbcAccess.class);
    implClassName =
        model.getEntityTypeClassName() + "_Access_" + model.getMethodName()
            + "_" + Util.createRandomName();
    implTypeName = implClassName.replace('.', '/');

    cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    cw.visit(V1_3, ACC_PUBLIC | ACC_FINAL | ACC_SUPER, implTypeName, null,
        superTypeName, new String[] {model.getAccessInterfaceName().replace(
            '.', '/')});
  }

  private void implementConstructor() {
    final String consName = "<init>";
    final String consDesc =
        Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {Type
            .getType(JdbcSchema.class)});
    final MethodVisitor mv;
    mv = cw.visitMethod(ACC_PUBLIC, consName, consDesc, null, null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitMethodInsn(INVOKESPECIAL, superTypeName, consName, consDesc);
    mv.visitInsn(RETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  private void implementGetString(final String methodName,
      final String returnValue) {
    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC | ACC_FINAL, methodName, Type
            .getMethodDescriptor(Type.getType(String.class), new Type[] {}),
            null, null);
    mv.visitCode();
    mv.visitLdcInsn(returnValue);
    mv.visitInsn(ARETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  private void implementMissingGetString(final String methodName,
      final String why) {
    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC | ACC_FINAL, methodName, Type
            .getMethodDescriptor(Type.getType(String.class), new Type[] {}),
            null, null);
    mv.visitCode();
    throwUnsupported(mv, model.getMethodName() + " does not support " + why);
    mv.visitInsn(RETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  private void throwUnsupported(final MethodVisitor mv, final String message) {
    final Type eType = Type.getType(UnsupportedOperationException.class);
    mv.visitTypeInsn(NEW, eType.getInternalName());
    mv.visitInsn(DUP);
    mv.visitLdcInsn(message);
    mv.visitMethodInsn(INVOKESPECIAL, eType.getInternalName(), "<init>", Type
        .getMethodDescriptor(Type.VOID_TYPE, new Type[] {Type
            .getType(String.class)}));
    mv.visitInsn(ATHROW);
  }

  private void implementPrimaryKey() {
    final KeyModel pk = model.getPrimaryKey();
    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC | ACC_FINAL, "primaryKey", Type
            .getMethodDescriptor(Type.getType(Key.class), new Type[] {Type
                .getType(Object.class)}), null, null);
    mv.visitCode();
    if (pk != null && pk.getField().isNested()) {
      final ColumnModel pkf = pk.getField();
      mv.visitVarInsn(ALOAD, 1);
      mv.visitTypeInsn(CHECKCAST, entityType.getInternalName());
      mv.visitFieldInsn(GETFIELD, entityType.getInternalName(), pkf
          .getFieldName(), CodeGenSupport.toType(pkf).getDescriptor());
    } else {
      mv.visitInsn(ACONST_NULL);
    }
    mv.visitInsn(ARETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  private void implementGetOne() {
    final KeyModel pk = model.getPrimaryKey();

    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC | ACC_FINAL, "get", Type.getMethodDescriptor(
            Type.getType(Object.class), new Type[] {Type.getType(Key.class)}),
            null, new String[] {Type.getType(OrmException.class)
                .getInternalName()});
    mv.visitCode();
    if (pk != null && pk.getField().isNested()) {
      final Type keyType = CodeGenSupport.toType(pk.getField());
      mv.visitVarInsn(ALOAD, 0);
      mv.visitVarInsn(ALOAD, 1);
      mv.visitTypeInsn(CHECKCAST, keyType.getInternalName());
      mv.visitMethodInsn(INVOKEVIRTUAL, implTypeName, pk.getName(), Type
          .getMethodDescriptor(entityType, new Type[] {keyType}));
      mv.visitInsn(ARETURN);
    } else {
      throwUnsupported(mv, model.getMethodName() + " does not support get(Key)");
    }
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  private void implementNewEntityInstance() {
    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC | ACC_FINAL, "newEntityInstance", Type
            .getMethodDescriptor(Type.getType(Object.class), new Type[] {}),
            null, null);
    mv.visitCode();
    mv.visitTypeInsn(NEW, entityType.getInternalName());
    mv.visitInsn(DUP);
    mv.visitMethodInsn(INVOKESPECIAL, entityType.getInternalName(), "<init>",
        Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {}));
    mv.visitInsn(ARETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  private void implementBindOne(final DmlType type) {
    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC | ACC_FINAL, type.methodName, Type
            .getMethodDescriptor(Type.VOID_TYPE, new Type[] {
                Type.getType(PreparedStatement.class),
                Type.getType(Object.class)}), null, new String[] {Type.getType(
            SQLException.class).getInternalName()});
    mv.visitCode();

    if (type != DmlType.INSERT && model.getPrimaryKey() == null) {
      throwUnsupported(mv, model.getMethodName() + " has no primary key");
      mv.visitInsn(RETURN);
      mv.visitMaxs(-1, -1);
      mv.visitEnd();
    }

    mv.visitVarInsn(ALOAD, 2);
    mv.visitTypeInsn(CHECKCAST, entityType.getInternalName());
    mv.visitVarInsn(ASTORE, 2);

    final CodeGenSupport cgs = new CodeGenSupport(mv);
    cgs.setEntityType(entityType);

    for (final ColumnModel col : model.getRowVersionColumns()) {
      cgs.setFieldReference(col);
      cgs.fieldSetBegin();
      cgs.pushFieldValue();
      mv.visitInsn(ICONST_1);
      mv.visitInsn(IADD);
      cgs.fieldSetEnd();
    }
    cgs.resetColumnIndex(0);

    if (type != DmlType.DELETE) {
      final List<ColumnModel> cols = new ArrayList<ColumnModel>();
      cols.addAll(model.getDependentFields());
      cols.addAll(model.getRowVersionFields());
      for (final ColumnModel field : cols) {
        doBindOne(mv, cgs, field);
      }
    }

    for (final ColumnModel col : model.getPrimaryKeyColumns()) {
      cgs.setFieldReference(col);
      dialect.getSqlTypeInfo(col).generatePreparedStatementSet(cgs);
    }
    if (type != DmlType.INSERT) {
      for (final ColumnModel col : model.getRowVersionColumns()) {
        cgs.setFieldReference(col);
        cgs.pushSqlHandle();
        cgs.pushColumnIndex();
        cgs.pushFieldValue();
        mv.visitInsn(ICONST_1);
        mv.visitInsn(ISUB);
        cgs.invokePreparedStatementSet("Int");
      }
    }

    mv.visitInsn(RETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  private void doBindOne(final MethodVisitor mv, final CodeGenSupport cgs,
      final ColumnModel field) {
    if (field.isNested() && field.isNotNull()) {
      for (final ColumnModel c : field.getAllLeafColumns()) {
        doBindOne(mv, cgs, c);
      }

    } else if (field.isNested()) {
      final int colIdx = cgs.getColumnIndex();
      final Label isnull = new Label();
      final Label end = new Label();

      cgs.setFieldReference(field);
      cgs.pushFieldValue();
      mv.visitJumpInsn(IFNULL, isnull);
      cgs.resetColumnIndex(colIdx);
      for (final ColumnModel c : field.getNestedColumns()) {
        doBindOne(mv, cgs, c);
      }
      mv.visitJumpInsn(GOTO, end);

      mv.visitLabel(isnull);
      cgs.resetColumnIndex(colIdx);
      for (final ColumnModel c : field.getAllLeafColumns()) {
        cgs.setFieldReference(c);
        dialect.getSqlTypeInfo(c).generatePreparedStatementNull(cgs);
      }

      mv.visitLabel(end);
    } else {
      cgs.setFieldReference(field);
      dialect.getSqlTypeInfo(field).generatePreparedStatementSet(cgs);
    }
  }

  private void implementBindOneFetch() {
    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC | ACC_FINAL, "bindOneFetch", Type
            .getMethodDescriptor(Type.VOID_TYPE, new Type[] {
                Type.getType(ResultSet.class), Type.getType(Object.class)}),
            null, new String[] {Type.getType(SQLException.class)
                .getInternalName()});
    mv.visitCode();

    mv.visitVarInsn(ALOAD, 2);
    mv.visitTypeInsn(CHECKCAST, entityType.getInternalName());
    mv.visitVarInsn(ASTORE, 2);

    final CodeGenSupport cgs = new CodeGenSupport(mv);
    cgs.setEntityType(entityType);

    if (model.getPrimaryKey() != null
        && model.getPrimaryKey().getField().isNested()) {
      final ColumnModel pkf = model.getPrimaryKey().getField();
      final Type vType = CodeGenSupport.toType(pkf);
      final int oldIdx = cgs.getColumnIndex();
      cgs.setFieldReference(pkf);
      cgs.fieldSetBegin();
      mv.visitTypeInsn(NEW, vType.getInternalName());
      mv.visitInsn(DUP);
      mv.visitMethodInsn(INVOKESPECIAL, vType.getInternalName(), "<init>", Type
          .getMethodDescriptor(Type.VOID_TYPE, new Type[] {}));
      cgs.fieldSetEnd();
      cgs.resetColumnIndex(oldIdx);
    }

    final List<ColumnModel> cols = new ArrayList<ColumnModel>();
    cols.addAll(model.getDependentFields());
    cols.addAll(model.getRowVersionFields());
    cols.addAll(model.getPrimaryKeyColumns());
    for (final ColumnModel field : cols) {
      doFetchOne(mv, cgs, field, -1);
    }

    mv.visitInsn(RETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  private void doFetchOne(final MethodVisitor mv, final CodeGenSupport cgs,
      final ColumnModel field, final int reportLiveInto) {
    if (field.isNested()) {
      int oldIdx = cgs.getColumnIndex();
      final Type vType = CodeGenSupport.toType(field);
      final int livecnt;

      if (field.isNotNull()) {
        livecnt = -1;
      } else {
        livecnt = cgs.newLocal();
        cgs.push(0);
        mv.visitVarInsn(ISTORE, livecnt);
      }

      cgs.setFieldReference(field);
      cgs.fieldSetBegin();
      mv.visitTypeInsn(NEW, vType.getInternalName());
      mv.visitInsn(DUP);
      mv.visitMethodInsn(INVOKESPECIAL, vType.getInternalName(), "<init>", Type
          .getMethodDescriptor(Type.VOID_TYPE, new Type[] {}));
      cgs.fieldSetEnd();

      cgs.resetColumnIndex(oldIdx);
      for (final ColumnModel c : field.getNestedColumns()) {
        doFetchOne(mv, cgs, c, livecnt);
      }

      if (livecnt >= 0) {
        oldIdx = cgs.getColumnIndex();

        final Label islive = new Label();
        mv.visitVarInsn(ILOAD, livecnt);
        mv.visitJumpInsn(IFNE, islive);
        cgs.setFieldReference(field);
        cgs.fieldSetBegin();
        mv.visitInsn(ACONST_NULL);
        cgs.fieldSetEnd();

        if (reportLiveInto >= 0) {
          final Label end = new Label();
          mv.visitJumpInsn(GOTO, end);
          mv.visitLabel(islive);
          mv.visitIincInsn(reportLiveInto, 1);
          mv.visitLabel(end);
        } else {
          mv.visitLabel(islive);
        }

        cgs.resetColumnIndex(oldIdx);
        cgs.freeLocal(livecnt);
      }

    } else {
      final int dupTo;
      if (reportLiveInto >= 0
          && CodeGenSupport.toType(field).getSort() == Type.OBJECT) {
        dupTo = cgs.newLocal();
      } else {
        dupTo = -1;
      }

      cgs.setFieldReference(field);
      cgs.setDupOnFieldSetEnd(dupTo);
      dialect.getSqlTypeInfo(field).generateResultSetGet(cgs);

      if (reportLiveInto >= 0) {
        final Label wasnull = new Label();
        if (dupTo >= 0) {
          mv.visitVarInsn(ALOAD, dupTo);
          mv.visitJumpInsn(IFNULL, wasnull);
          cgs.freeLocal(dupTo);
        } else {
          cgs.pushSqlHandle();
          mv.visitMethodInsn(INVOKEINTERFACE, Type.getType(ResultSet.class)
              .getInternalName(), "wasNull", Type.getMethodDescriptor(
              Type.BOOLEAN_TYPE, new Type[] {}));
          mv.visitJumpInsn(IFNE, wasnull);
        }
        mv.visitIincInsn(reportLiveInto, 1);
        mv.visitLabel(wasnull);
      }
    }
  }

  private void implementKeyQuery(final KeyModel info) {
    final Type keyType = CodeGenSupport.toType(info.getField());
    final StringBuilder query = new StringBuilder();
    query.append(model.getSelectSql(dialect, REL_ALIAS));
    query.append(" WHERE ");
    int nth = 1;
    for (final Iterator<ColumnModel> i = info.getAllLeafColumns().iterator(); i
        .hasNext();) {
      final ColumnModel c = i.next();
      query.append(REL_ALIAS);
      query.append('.');
      query.append(c.getColumnName());
      query.append('=');
      query.append(dialect.getParameterPlaceHolder(nth++));
      if (i.hasNext()) {
        query.append(" AND ");
      }
    }

    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC | ACC_FINAL, info.getName(), Type
            .getMethodDescriptor(entityType, new Type[] {keyType}), null,
            new String[] {Type.getType(OrmException.class).getInternalName()});
    mv.visitCode();

    final int keyvar = 1, psvar = keyvar + keyType.getSize();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitLdcInsn(query.toString());
    mv.visitMethodInsn(INVOKEVIRTUAL, superTypeName, "prepareStatement", Type
        .getMethodDescriptor(Type.getType(PreparedStatement.class),
            new Type[] {Type.getType(String.class)}));
    mv.visitVarInsn(ASTORE, psvar);

    final CodeGenSupport cgs = new CodeGenSupport(mv) {
      @Override
      public void pushSqlHandle() {
        mv.visitVarInsn(ALOAD, psvar);
      }

      @Override
      public void pushFieldValue() {
        appendGetField(getFieldReference());
      }

      @Override
      protected void appendGetField(final ColumnModel c) {
        if (c.getParent() == null) {
          loadVar(keyType, keyvar);
        } else {
          super.appendGetField(c);
        }
      }
    };
    for (final ColumnModel c : info.getAllLeafColumns()) {
      cgs.setFieldReference(c);
      dialect.getSqlTypeInfo(c).generatePreparedStatementSet(cgs);
    }

    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, psvar);
    mv.visitMethodInsn(INVOKEVIRTUAL, superTypeName, "queryOne", Type
        .getMethodDescriptor(Type.getType(Object.class), new Type[] {Type
            .getType(PreparedStatement.class)}));
    mv.visitTypeInsn(CHECKCAST, entityType.getInternalName());
    mv.visitInsn(ARETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  private void overrideGetMany() {
    final KeyModel pk = model.getPrimaryKey();
    final StringBuilder query = new StringBuilder();
    query.append(model.getSelectSql(dialect, REL_ALIAS));
    query.append(" WHERE ");
    final ColumnModel pkcol = pk.getAllLeafColumns().iterator().next();
    query.append(REL_ALIAS);
    query.append('.');
    query.append(pkcol.getColumnName());
    query.append(" IN");

    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC | ACC_FINAL, "getBySqlIn", Type
            .getMethodDescriptor(Type
                .getType(com.google.gwtorm.client.ResultSet.class),
                new Type[] {Type.getType(Collection.class)}), null,
            new String[] {Type.getType(OrmException.class).getInternalName()});
    mv.visitCode();

    final int keyset = 1;
    final int psvar = 2;
    final int itrvar = 3;
    final int colvar = 4;
    final int keyvar = 5;

    mv.visitVarInsn(ALOAD, 0);
    mv.visitLdcInsn(query.toString());
    mv.visitVarInsn(ALOAD, keyset);
    mv.visitMethodInsn(INVOKEVIRTUAL, superTypeName, "prepareBySqlIn", Type
        .getMethodDescriptor(Type.getType(PreparedStatement.class), new Type[] {
            Type.getType(String.class), Type.getType(Collection.class)}));
    mv.visitVarInsn(ASTORE, psvar);

    mv.visitVarInsn(ALOAD, keyset);
    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Collection.class),
        "iterator", Type.getMethodDescriptor(Type.getType(Iterator.class),
            new Type[] {}));
    mv.visitVarInsn(ASTORE, itrvar);

    mv.visitInsn(ICONST_1);
    mv.visitVarInsn(ISTORE, colvar);

    final Label endbind = new Label();
    final Label again = new Label();
    mv.visitLabel(again);
    mv.visitVarInsn(ALOAD, itrvar);
    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Iterator.class),
        "hasNext", Type.getMethodDescriptor(Type.BOOLEAN_TYPE, new Type[] {}));
    mv.visitJumpInsn(IFEQ, endbind);

    mv.visitVarInsn(ALOAD, itrvar);
    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(Iterator.class),
        "next", Type.getMethodDescriptor(Type.getType(Object.class),
            new Type[] {}));
    mv.visitTypeInsn(CHECKCAST, CodeGenSupport.toType(pk.getField())
        .getInternalName());
    mv.visitVarInsn(ASTORE, keyvar);

    final CodeGenSupport cgs = new CodeGenSupport(mv) {
      @Override
      public void pushSqlHandle() {
        mv.visitVarInsn(ALOAD, psvar);
      }

      @Override
      public void pushFieldValue() {
        appendGetField(getFieldReference());
      }

      @Override
      public void pushColumnIndex() {
        mv.visitVarInsn(ILOAD, colvar);
      }

      @Override
      protected void appendGetField(final ColumnModel c) {
        if (c.getParent() == null) {
          mv.visitVarInsn(ALOAD, keyvar);
        } else {
          super.appendGetField(c);
        }
      }
    };

    cgs.setFieldReference(pkcol);
    dialect.getSqlTypeInfo(pkcol).generatePreparedStatementSet(cgs);
    mv.visitIincInsn(colvar, 1);
    mv.visitJumpInsn(GOTO, again);

    mv.visitLabel(endbind);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, psvar);
    mv.visitMethodInsn(INVOKEVIRTUAL, superTypeName, "queryList", Type
        .getMethodDescriptor(Type.getType(ListResultSet.class),
            new Type[] {Type.getType(PreparedStatement.class)}));
    mv.visitInsn(ARETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }

  private void implementQuery(final QueryModel info) {
    final List<ColumnModel> pCols = info.getParameters();
    final boolean hasLimitParam = info.hasLimitParameter();
    final Type[] pTypes = new Type[pCols.size() + (hasLimitParam ? 1 : 0)];
    final int[] pVars = new int[pTypes.length];
    int nextVar = 1;
    for (int i = 0; i < pCols.size(); i++) {
      pTypes[i] = CodeGenSupport.toType(pCols.get(i));
      pVars[i] = nextVar;
      nextVar += pTypes[i].getSize();
    }
    if (hasLimitParam) {
      pTypes[pTypes.length - 1] = Type.INT_TYPE;
      pVars[pTypes.length - 1] = nextVar;
      nextVar += Type.INT_TYPE.getSize();
    }

    final int psvar = nextVar++;
    final MethodVisitor mv =
        cw.visitMethod(ACC_PUBLIC | ACC_FINAL, info.getName(), Type
            .getMethodDescriptor(Type
                .getType(com.google.gwtorm.client.ResultSet.class), pTypes),
            null, new String[] {Type.getType(OrmException.class)
                .getInternalName()});
    mv.visitCode();

    mv.visitVarInsn(ALOAD, 0);
    mv.visitLdcInsn(info.getSelectSql(dialect, REL_ALIAS));
    mv.visitMethodInsn(INVOKEVIRTUAL, superTypeName, "prepareStatement", Type
        .getMethodDescriptor(Type.getType(PreparedStatement.class),
            new Type[] {Type.getType(String.class)}));
    mv.visitVarInsn(ASTORE, psvar);

    final int argIdx[] = new int[] {0};
    final CodeGenSupport cgs = new CodeGenSupport(mv) {
      @Override
      public void pushSqlHandle() {
        mv.visitVarInsn(ALOAD, psvar);
      }

      @Override
      public void pushFieldValue() {
        appendGetField(getFieldReference());
      }

      @Override
      protected void appendGetField(final ColumnModel c) {
        final int n = argIdx[0];
        if (c == pCols.get(n)) {
          loadVar(pTypes[n], pVars[n]);
        } else {
          super.appendGetField(c);
        }
      }
    };
    for (final ColumnModel c : pCols) {
      if (c.isNested()) {
        for (final ColumnModel n : c.getAllLeafColumns()) {
          cgs.setFieldReference(n);
          dialect.getSqlTypeInfo(n).generatePreparedStatementSet(cgs);
        }
      } else {
        cgs.setFieldReference(c);
        dialect.getSqlTypeInfo(c).generatePreparedStatementSet(cgs);
      }
      argIdx[0]++;
    }

    if (info.hasLimit()) {
      if (hasLimitParam || !dialect.selectHasLimit()) {
        mv.visitVarInsn(ALOAD, psvar);
        if (hasLimitParam) {
          mv.visitVarInsn(ILOAD, pVars[pTypes.length - 1]);
        } else {
          cgs.push(info.getStaticLimit());
        }
        mv.visitMethodInsn(INVOKEINTERFACE, Type.getType(
            PreparedStatement.class).getInternalName(), "setMaxRows", Type
            .getMethodDescriptor(Type.VOID_TYPE, new Type[] {Type.INT_TYPE}));
      }
    }

    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, psvar);
    mv.visitMethodInsn(INVOKEVIRTUAL, superTypeName, "queryList", Type
        .getMethodDescriptor(Type.getType(ListResultSet.class),
            new Type[] {Type.getType(PreparedStatement.class)}));
    mv.visitInsn(ARETURN);
    mv.visitMaxs(-1, -1);
    mv.visitEnd();
  }
}
