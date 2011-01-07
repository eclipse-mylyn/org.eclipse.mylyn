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

import com.google.gwtorm.client.OrmDuplicateKeyException;
import com.google.gwtorm.client.OrmException;
import com.google.gwtorm.client.StatementExecutor;
import com.google.gwtorm.schema.ColumnModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

/** Dialect for <a href="http://www.h2database.com/">H2</a> */
public class DialectH2 extends SqlDialect {
  @Override
  public OrmException convertError(final String op, final String entity,
      final SQLException err) {
    switch (getSQLStateInt(err)) {
      case 23001: // UNIQUE CONSTRAINT VIOLATION
        return new OrmDuplicateKeyException(entity, err);

      case 23000: // CHECK CONSTRAINT VIOLATION
      default:
        return super.convertError(op, entity, err);
    }
  }

  @Override
  public String getNextSequenceValueSql(final String seqname) {
    return "SELECT NEXT VALUE FOR " + seqname;
  }

  @Override
  public Set<String> listSequences(Connection db) throws SQLException {
    Statement s = db.createStatement();
    try {
      ResultSet rs =
          s.executeQuery("SELECT SEQUENCE_NAME"
              + " FROM INFORMATION_SCHEMA.SEQUENCES"
              + " WHERE SEQUENCE_SCHEMA = 'PUBLIC'");
      try {
        HashSet<String> sequences = new HashSet<String>();
        while (rs.next()) {
          sequences.add(rs.getString(1).toLowerCase());
        }
        return sequences;
      } finally {
        rs.close();
      }
    } finally {
      s.close();
    }
  }

  @Override
  public void addColumn(StatementExecutor stmt, String tableName,
      ColumnModel col) throws OrmException {
    final StringBuilder r = new StringBuilder();
    r.append("ALTER TABLE ");
    r.append(tableName);
    r.append(" ADD ");
    r.append(col.getColumnName());
    r.append(" ");
    r.append(getSqlTypeInfo(col).getSqlType(col, this));
    stmt.execute(r.toString());

    String check = getSqlTypeInfo(col).getCheckConstraint(col, this);
    if (check != null) {
      r.setLength(0);
      r.append("ALTER TABLE ");
      r.append(tableName);
      r.append(" ADD CONSTRAINT ");
      r.append(col.getColumnName() + "_check");
      r.append(' ');
      r.append(check);
      stmt.execute(r.toString());
    }
  }

  @Override
  public void renameColumn(StatementExecutor stmt, String tableName,
      String fromColumn, ColumnModel col) throws OrmException {
    final StringBuilder r = new StringBuilder();
    r.append("ALTER TABLE ");
    r.append(tableName);
    r.append(" ALTER COLUMN ");
    r.append(fromColumn);
    r.append(" RENAME TO ");
    r.append(col.getColumnName());
    stmt.execute(r.toString());
  }
}
