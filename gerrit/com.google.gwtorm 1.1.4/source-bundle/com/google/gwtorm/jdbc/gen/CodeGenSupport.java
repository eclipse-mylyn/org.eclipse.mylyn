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

import com.google.gwtorm.schema.ColumnModel;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CodeGenSupport implements Opcodes {
  public final MethodVisitor mv;
  private ColumnModel col;
  private int dupOnSet;
  private int columnIdx;
  private Type entityType;

  private int lastLocal = 2;
  private List<Integer> freeLocals = new ArrayList<Integer>(4);

  public CodeGenSupport(final MethodVisitor method) {
    mv = method;
  }

  public void push(final int val) {
    switch (val) {
      case -1:
        mv.visitInsn(ICONST_M1);
        break;
      case 0:
        mv.visitInsn(ICONST_0);
        break;
      case 1:
        mv.visitInsn(ICONST_1);
        break;
      case 2:
        mv.visitInsn(ICONST_2);
        break;
      case 3:
        mv.visitInsn(ICONST_3);
        break;
      case 4:
        mv.visitInsn(ICONST_4);
        break;
      case 5:
        mv.visitInsn(ICONST_5);
        break;
      default:
        if (Byte.MIN_VALUE >= val && val < Byte.MAX_VALUE) {
          mv.visitIntInsn(BIPUSH, val);
        } else if (Short.MIN_VALUE >= val && val < Short.MAX_VALUE) {
          mv.visitIntInsn(SIPUSH, val);
        } else {
          mv.visitLdcInsn(Integer.valueOf(val));
        }
        break;
    }
  }

  public void loadVar(final Type type, final int index) {
    mv.visitVarInsn(type.getOpcode(ILOAD), index);
  }

  public int newLocal() {
    if (freeLocals.isEmpty()) {
      return ++lastLocal;
    }
    return freeLocals.remove(freeLocals.size() - 1);
  }

  public void freeLocal(final int index) {
    freeLocals.add(index);
  }

  public void setEntityType(final Type et) {
    entityType = et;
  }

  public void setFieldReference(final ColumnModel cm) {
    col = cm;
    dupOnSet = -1;
    columnIdx++;
  }

  public void resetColumnIndex(final int s) {
    columnIdx = s;
  }

  public int getColumnIndex() {
    return columnIdx;
  }

  public ColumnModel getFieldReference() {
    return col;
  }

  public void pushSqlHandle() {
    mv.visitVarInsn(ALOAD, 1);
  }

  public void pushEntity() {
    mv.visitVarInsn(ALOAD, 2);
  }

  public void pushColumnIndex() {
    push(columnIdx);
  }

  public void invokePreparedStatementSet(final String sqlTypeName) {
    final Method m;
    try {
      m =
          PreparedStatement.class.getMethod("set" + sqlTypeName, Integer.TYPE,
              ResultSet.class.getMethod("get" + sqlTypeName, Integer.TYPE)
                  .getReturnType());
    } catch (SecurityException e) {
      throw new RuntimeException("java.sql has no " + sqlTypeName);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("java.sql has no " + sqlTypeName, e);
    }
    mv.visitMethodInsn(INVOKEINTERFACE, Type
        .getInternalName(PreparedStatement.class), m.getName(), Type
        .getMethodDescriptor(m));
  }

  public void invokeResultSetGet(final String sqlTypeName) {
    final Method m;
    try {
      m = ResultSet.class.getMethod("get" + sqlTypeName, Integer.TYPE);
    } catch (SecurityException e) {
      throw new RuntimeException("java.sql has no " + sqlTypeName);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("java.sql has no " + sqlTypeName, e);
    }
    mv.visitMethodInsn(INVOKEINTERFACE, Type.getInternalName(ResultSet.class),
        m.getName(), Type.getMethodDescriptor(m));
  }

  public void fieldSetBegin() {
    pushEntity();
    if (col.getParent() != null) {
      appendGetField(col.getParent());
    }
  }

  public void fieldSetEnd() {
    final Type c = containerClass(col);
    if (dupOnSet >= 0) {
      mv.visitInsn(DUP);
      mv.visitVarInsn(ASTORE, dupOnSet);
    }
    mv.visitFieldInsn(PUTFIELD, c.getInternalName(), col.getFieldName(),
        toType(col).getDescriptor());
  }

  public void setDupOnFieldSetEnd(final int varIdx) {
    dupOnSet = varIdx;
  }

  public void pushFieldValue() {
    pushEntity();
    appendGetField(col);
  }

  protected void appendGetField(final ColumnModel c) {
    if (c.getParent() != null) {
      appendGetField(c.getParent());
    }
    final Type t = containerClass(c);
    mv.visitFieldInsn(GETFIELD, t.getInternalName(), c.getFieldName(),
        toType(c).getDescriptor());
  }

  private Type containerClass(final ColumnModel c) {
    if (c.getParent() == null) {
      return entityType;
    }
    final String n = c.getParent().getNestedClassName();
    return Type.getObjectType(n.replace('.', '/'));
  }

  static Type toType(final ColumnModel c) {
    if (c.isSqlPrimitive()) {
      return Type.getType(c.getPrimitiveType());
    }
    return Type.getObjectType(c.getNestedClassName().replace('.', '/'));
  }
}
