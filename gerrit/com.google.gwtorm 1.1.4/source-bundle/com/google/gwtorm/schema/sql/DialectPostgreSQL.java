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
import com.google.gwtorm.schema.RelationModel;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashSet;
import java.util.Set;

/** Dialect for <a href="http://www.postgresql.org/>PostgreSQL</a> */
public class DialectPostgreSQL extends SqlDialect {
  public DialectPostgreSQL() {
    typeNames.put(Types.VARBINARY, "BYTEA");
    typeNames.put(Types.TIMESTAMP, "TIMESTAMP WITH TIME ZONE");
  }

  @Override
  public SqlDialect refine(final Connection c) throws SQLException {
    final int major = c.getMetaData().getDatabaseMajorVersion();
    final int minor = c.getMetaData().getDatabaseMinorVersion();
    if (major < 8 || (major == 8 && minor < 2)) {
      return new Pre82();
    }
    return this;
  }

  @Override
  public OrmException convertError(final String op, final String entity,
      final SQLException err) {
    switch (getSQLStateInt(err)) {
      case 23505: // UNIQUE CONSTRAINT VIOLATION
        return new OrmDuplicateKeyException(entity, err);

      case 23514: // CHECK CONSTRAINT VIOLATION
      case 23503: // FOREIGN KEY CONSTRAINT VIOLATION
      case 23502: // NOT NULL CONSTRAINT VIOLATION
      case 23001: // RESTRICT VIOLATION
      default:
        return super.convertError(op, entity, err);
    }
  }

  @Override
  public String getNextSequenceValueSql(final String seqname) {
    return "SELECT nextval('" + seqname + "')";
  }

  @Override
  public void appendCreateTableStorage(final StringBuilder sqlBuffer,
      final RelationModel relationModel) {
    sqlBuffer.append("WITH (OIDS = FALSE)");
  }

  @Override
  public void renameColumn(StatementExecutor stmt, String tableName,
      String fromColumn, ColumnModel col) throws OrmException {
    final StringBuilder r = new StringBuilder();
    r.append("ALTER TABLE ");
    r.append(tableName);
    r.append(" RENAME COLUMN ");
    r.append(fromColumn);
    r.append(" TO ");
    r.append(col.getColumnName());
    stmt.execute(r.toString());
  }

  @Override
  public Set<String> listSequences(Connection db) throws SQLException {
    Statement s = db.createStatement();
    try {
      ResultSet rs =
          s.executeQuery("SELECT relname FROM pg_class WHERE relkind = 'S'");
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

  private static class Pre82 extends DialectPostgreSQL {
    @Override
    public void appendCreateTableStorage(final StringBuilder sqlBuffer,
        final RelationModel relationModel) {
      sqlBuffer.append("WITHOUT OIDS");
    }
  }
}
