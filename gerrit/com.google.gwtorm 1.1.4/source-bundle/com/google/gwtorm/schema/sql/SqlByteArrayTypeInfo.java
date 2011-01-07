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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

public class SqlByteArrayTypeInfo extends SqlTypeInfo {
  @Override
  protected String getJavaSqlTypeAlias() {
    return "BinaryStream";
  }

  @Override
  protected int getSqlTypeConstant() {
    return Types.VARBINARY;
  }

  @Override
  public String getSqlType(final ColumnModel col, final SqlDialect dialect) {
    final StringBuilder r = new StringBuilder();
    r.append(dialect.getSqlTypeName(getSqlTypeConstant()));
    if (col.isNotNull()) {
      r.append(" DEFAULT ''");
      r.append(" NOT NULL");
    }
    return r.toString();
  }

  @Override
  public void generatePreparedStatementSet(final CodeGenSupport cgs) {
    cgs.pushSqlHandle();
    cgs.pushColumnIndex();
    cgs.pushFieldValue();
    cgs.mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type
        .getInternalName(SqlByteArrayTypeInfo.class), "toPreparedStatement",
        Type.getMethodDescriptor(Type.VOID_TYPE, new Type[] {
            Type.getType(PreparedStatement.class), Type.INT_TYPE,
            Type.getType(byte[].class)}));
  }

  @Override
  public void generateResultSetGet(final CodeGenSupport cgs) {
    cgs.fieldSetBegin();
    cgs.pushSqlHandle();
    cgs.pushColumnIndex();
    cgs.mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type
        .getInternalName(SqlByteArrayTypeInfo.class), "fromResultSet", Type
        .getMethodDescriptor(Type.getType(byte[].class), new Type[] {
            Type.getType(ResultSet.class), Type.INT_TYPE}));
    cgs.fieldSetEnd();
  }

  public static byte[] fromResultSet(final ResultSet rs, final int col)
      throws SQLException {
    final InputStream r = rs.getBinaryStream(col);
    if (r == null) {
      return null;
    }
    try {
      try {
        final ByteArrayOutputStream w = new ByteArrayOutputStream();
        final byte[] buf = new byte[1024];
        int n;
        while ((n = r.read(buf)) > 0) {
          w.write(buf, 0, n);
        }
        return w.toByteArray();
      } finally {
        r.close();
      }
    } catch (IOException e) {
      throw new SQLException("Unable to read BinaryStream in column " + col);
    }
  }

  public static void toPreparedStatement(final PreparedStatement ps,
      final int col, final byte[] raw) throws SQLException {
    if (raw != null) {
      ps.setBinaryStream(col, new ByteArrayInputStream(raw), raw.length);
    } else {
      ps.setNull(col, Types.VARBINARY);
    }
  }
}
