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

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.sql.PreparedStatement;


public abstract class SqlTypeInfo {
  protected SqlTypeInfo() {
  }

  public abstract String getSqlType(ColumnModel column, SqlDialect dialect);

  public String getCheckConstraint(final ColumnModel column,
      final SqlDialect dialect) {
    return null;
  }

  protected abstract String getJavaSqlTypeAlias();

  protected abstract int getSqlTypeConstant();

  public void generatePreparedStatementSet(final CodeGenSupport cgs) {
    cgs.pushSqlHandle();
    cgs.pushColumnIndex();
    cgs.pushFieldValue();
    cgs.invokePreparedStatementSet(getJavaSqlTypeAlias());
  }

  public void generatePreparedStatementNull(final CodeGenSupport cgs) {
    cgs.pushSqlHandle();
    cgs.pushColumnIndex();
    cgs.push(getSqlTypeConstant());
    cgs.mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, Type
        .getInternalName(PreparedStatement.class), "setNull", Type
        .getMethodDescriptor(Type.VOID_TYPE, new Type[] {Type.INT_TYPE,
            Type.INT_TYPE}));
  }

  public void generateResultSetGet(final CodeGenSupport cgs) {
    cgs.fieldSetBegin();
    cgs.pushSqlHandle();
    cgs.pushColumnIndex();
    cgs.invokeResultSetGet(getJavaSqlTypeAlias());
    cgs.fieldSetEnd();
  }
}
