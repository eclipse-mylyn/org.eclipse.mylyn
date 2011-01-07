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

import com.google.gwtorm.client.Column;
import com.google.gwtorm.jdbc.gen.CodeGenSupport;
import com.google.gwtorm.schema.ColumnModel;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class SqlStringTypeInfo extends SqlTypeInfo {
  @Override
  protected String getJavaSqlTypeAlias() {
    return "String";
  }

  @Override
  protected int getSqlTypeConstant() {
    return Types.VARCHAR;
  }

  @Override
  public String getSqlType(final ColumnModel col, final SqlDialect dialect) {
    final Column column = col.getColumnAnnotation();
    final StringBuilder r = new StringBuilder();

    if (column.length() <= 0) {
      r.append("VARCHAR(255)");
    } else if (column.length() <= 255) {
      r.append("VARCHAR(" + column.length() + ")");
    } else {
      r.append(dialect.getSqlTypeName(Types.LONGVARCHAR));
    }

    if (col.isNotNull()) {
      r.append(" DEFAULT ''");
      r.append(" NOT NULL");
    }

    return r.toString();
  }

  @Override
  public void generatePreparedStatementSet(final CodeGenSupport cgs) {
    if (cgs.getFieldReference().getColumnAnnotation().length() <= 255) {
      super.generatePreparedStatementSet(cgs);
    } else {
      cgs.pushSqlHandle();
      cgs.pushColumnIndex();
      cgs.pushFieldValue();
      cgs.mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type
          .getInternalName(SqlStringTypeInfo.class), "toPreparedStatement",
          Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {
              Type.getType(PreparedStatement.class), Type.INT_TYPE,
              Type.getType(String.class)}));
    }
  }

  @Override
  public void generateResultSetGet(final CodeGenSupport cgs) {
    if (cgs.getFieldReference().getColumnAnnotation().length() <= 255) {
      super.generateResultSetGet(cgs);
    } else {
      cgs.fieldSetBegin();
      cgs.pushSqlHandle();
      cgs.pushColumnIndex();
      cgs.mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type
          .getInternalName(SqlStringTypeInfo.class), "fromResultSet", Type
          .getMethodDescriptor(Type.getType(String.class), new Type[] {
              Type.getType(ResultSet.class), Type.INT_TYPE}));
      cgs.fieldSetEnd();
    }
  }

  public static String fromResultSet(final ResultSet rs, final int col)
      throws SQLException {
    final Reader r = rs.getCharacterStream(col);
    if (r == null) {
      return null;
    }
    try {
      try {
        final StringWriter w = new StringWriter();
        final char[] buf = new char[1024];
        int n;
        while ((n = r.read(buf)) > 0) {
          w.write(buf, 0, n);
        }
        return w.toString();
      } finally {
        r.close();
      }
    } catch (IOException e) {
      throw new SQLException("Unable to read CharacterStream in column " + col);
    }
  }

  public static void toPreparedStatement(final PreparedStatement ps,
      final int col, final String txt) throws SQLException {
    if (txt != null) {
      ps.setCharacterStream(col, new StringReader(txt), txt.length());
    } else {
      ps.setNull(col, Types.LONGVARCHAR);
    }
  }
}
