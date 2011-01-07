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

package com.google.gwtorm.schema.sql;

import com.google.gwtorm.jdbc.gen.CodeGenSupport;
import com.google.gwtorm.schema.ColumnModel;

import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.sql.Types;

public class SqlBooleanTypeInfo extends SqlTypeInfo {
  @Override
  public String getSqlType(final ColumnModel column, final SqlDialect dialect) {
    final String name = column.getColumnName();
    final String t = getTrueLiteralValue();
    final String f = getFalseLiteralValue();
    final StringBuilder r = new StringBuilder();
    r.append("CHAR(1)");
    if (column.isNotNull()) {
      r.append(" DEFAULT " + f);
      r.append(" NOT NULL");
    }
    return r.toString();
  }

  @Override
  public String getCheckConstraint(final ColumnModel column,
      final SqlDialect dialect) {
    final String name = column.getColumnName();
    final String t = getTrueLiteralValue();
    final String f = getFalseLiteralValue();
    return " CHECK (" + name + " IN (" + t + "," + f + "))";
  }

  @Override
  protected String getJavaSqlTypeAlias() {
    return "String";
  }

  @Override
  protected int getSqlTypeConstant() {
    return Types.CHAR;
  }

  public String getTrueValue() {
    return "Y";
  }

  public String getFalseValue() {
    return "N";
  }

  public String getTrueLiteralValue() {
    return "'" + getTrueValue() + "'";
  }

  public String getFalseLiteralValue() {
    return "'" + getFalseValue() + "'";
  }

  @Override
  public void generatePreparedStatementSet(final CodeGenSupport cgs) {
    cgs.pushSqlHandle();
    cgs.pushColumnIndex();
    cgs.pushFieldValue();

    final Label useNo = new Label();
    final Label end = new Label();
    cgs.mv.visitJumpInsn(Opcodes.IFEQ, useNo);
    cgs.mv.visitLdcInsn(getTrueValue());
    cgs.mv.visitJumpInsn(Opcodes.GOTO, end);
    cgs.mv.visitLabel(useNo);
    cgs.mv.visitLdcInsn(getFalseValue());
    cgs.mv.visitLabel(end);
    cgs.invokePreparedStatementSet(getJavaSqlTypeAlias());
  }

  @Override
  public void generateResultSetGet(final CodeGenSupport cgs) {
    cgs.fieldSetBegin();
    cgs.mv.visitLdcInsn(getTrueValue());
    cgs.pushSqlHandle();
    cgs.pushColumnIndex();
    cgs.invokeResultSetGet(getJavaSqlTypeAlias());
    cgs.mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type
        .getInternalName(String.class), "equals", Type.getMethodDescriptor(
        Type.BOOLEAN_TYPE, new Type[] {Type.getType(Object.class)}));
    cgs.fieldSetEnd();
  }
}
