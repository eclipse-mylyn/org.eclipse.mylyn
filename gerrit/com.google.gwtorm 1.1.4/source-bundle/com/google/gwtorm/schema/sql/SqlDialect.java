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

import com.google.gwtorm.client.OrmException;
import com.google.gwtorm.client.Sequence;
import com.google.gwtorm.client.StatementExecutor;
import com.google.gwtorm.schema.ColumnModel;
import com.google.gwtorm.schema.RelationModel;
import com.google.gwtorm.schema.SequenceModel;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class SqlDialect {
  protected final Map<Class<?>, SqlTypeInfo> types;
  protected final Map<Integer, String> typeNames;

  protected SqlDialect() {
    types = new HashMap<Class<?>, SqlTypeInfo>();
    types.put(Boolean.TYPE, new SqlBooleanTypeInfo());
    types.put(Short.TYPE, new SqlShortTypeInfo());
    types.put(Integer.TYPE, new SqlIntTypeInfo());
    types.put(Long.TYPE, new SqlLongTypeInfo());
    types.put(Character.TYPE, new SqlCharTypeInfo());
    types.put(String.class, new SqlStringTypeInfo());
    types.put(java.sql.Date.class, new SqlDateTypeInfo());
    types.put(java.sql.Timestamp.class, new SqlTimestampTypeInfo());
    types.put(byte[].class, new SqlByteArrayTypeInfo());

    typeNames = new HashMap<Integer, String>();
    typeNames.put(Types.VARBINARY, "BLOB");
    typeNames.put(Types.DATE, "DATE");
    typeNames.put(Types.SMALLINT, "SMALLINT");
    typeNames.put(Types.INTEGER, "INT");
    typeNames.put(Types.BIGINT, "BIGINT");
    typeNames.put(Types.LONGVARCHAR, "TEXT");
    typeNames.put(Types.TIMESTAMP, "TIMESTAMP");
  }

  /** Select a better dialect definition for this connection */
  public SqlDialect refine(final Connection c) throws SQLException {
    return this;
  }

  public String getSqlTypeName(final int typeCode) {
    final String r = typeNames.get(typeCode);
    return r != null ? r : "UNKNOWNTYPE";
  }

  public SqlTypeInfo getSqlTypeInfo(final ColumnModel col) {
    return getSqlTypeInfo(col.getPrimitiveType());
  }

  public SqlTypeInfo getSqlTypeInfo(final Class<?> t) {
    return types.get(t);
  }

  public String getParameterPlaceHolder(final int nthParameter) {
    return "?";
  }

  public boolean selectHasLimit() {
    return true;
  }

  protected static String getSQLState(SQLException err) {
    String ec;
    SQLException next = err;
    do {
      ec = next.getSQLState();
      next = next.getNextException();
    } while (ec == null && next != null);
    return ec;
  }

  protected static int getSQLStateInt(SQLException err) {
    final String s = getSQLState(err);
    if (s != null) {
      try {
        return Integer.parseInt(s);
      } catch (NumberFormatException e) {
        return -1;
      }
    }
    return 0;
  }

  /**
   * Convert a driver specific exception into an {@link OrmException}.
   *
   * @param op short description of the operation, e.g. "update" or "fetch".
   * @param entity name of the entity being accessed by the operation.
   * @param err the driver specific exception.
   * @return an OrmException the caller can throw.
   */
  public OrmException convertError(final String op, final String entity,
      final SQLException err) {
    if (err.getCause() == null && err.getNextException() != null) {
      err.initCause(err.getNextException());
    }
    return new OrmException(op + " failure on " + entity, err);
  }

  public long nextLong(final Connection conn, final String query)
      throws OrmException {
    try {
      final Statement st = conn.createStatement();
      try {
        final ResultSet rs = st.executeQuery(query);
        try {
          if (!rs.next()) {
            throw new SQLException("No result row for sequence query");
          }
          final long r = rs.getLong(1);
          if (rs.next()) {
            throw new SQLException("Too many results from sequence query");
          }
          return r;
        } finally {
          rs.close();
        }
      } finally {
        st.close();
      }
    } catch (SQLException e) {
      throw convertError("sequence", query, e);
    }
  }

  public String getCreateSequenceSql(final SequenceModel seq) {
    final Sequence s = seq.getSequence();
    final StringBuilder r = new StringBuilder();
    r.append("CREATE SEQUENCE ");
    r.append(seq.getSequenceName());

    if (s.startWith() > 0) {
      r.append(" START WITH ");
      r.append(s.startWith());
    }

    if (s.cache() > 0) {
      r.append(" CACHE ");
      r.append(s.cache());
    }

    return r.toString();
  }

  public String getDropSequenceSql(final String name) {
    return "DROP SEQUENCE " + name;
  }

  /**
   * Append driver specific storage parameters to a CREATE TABLE statement.
   *
   * @param sqlBuffer buffer holding the CREATE TABLE, just after the closing
   *        parenthesis after the column list.
   * @param relationModel the model of the table being generated.
   */
  public void appendCreateTableStorage(final StringBuilder sqlBuffer,
      final RelationModel relationModel) {
  }

  /**
   * List all tables in the current database schema.
   *
   * @param db connection to the schema.
   * @return set of declared tables, in lowercase.
   * @throws SQLException the tables cannot be listed.
   */
  public Set<String> listTables(final Connection db) throws SQLException {
    final String[] types = new String[] {"TABLE"};
    final ResultSet rs = db.getMetaData().getTables(null, null, null, types);
    try {
      HashSet<String> tables = new HashSet<String>();
      while (rs.next()) {
        tables.add(rs.getString("TABLE_NAME").toLowerCase());
      }
      return tables;
    } finally {
      rs.close();
    }
  }

  /**
   * List all sequences in the current database schema.
   *
   * @param db connection to the schema.
   * @return set of declared sequences, in lowercase.
   * @throws SQLException the sequence objects cannot be listed.
   */
  public abstract Set<String> listSequences(final Connection db)
      throws SQLException;

  /**
   * List all columns in the given table name.
   *
   * @param db connection to the schema.
   * @param tableName the table to list columns from, in lowercase.
   * @return set of declared columns, in lowercase.
   * @throws SQLException the columns cannot be listed from the relation.
   */
  public Set<String> listColumns(final Connection db, String tableName)
      throws SQLException {
    final DatabaseMetaData meta = db.getMetaData();
    if (meta.storesUpperCaseIdentifiers()) {
      tableName = tableName.toUpperCase();
    } else if (meta.storesLowerCaseIdentifiers()) {
      tableName = tableName.toLowerCase();
    }

    ResultSet rs = meta.getColumns(null, null, tableName, null);
    try {
      HashSet<String> columns = new HashSet<String>();
      while (rs.next()) {
        columns.add(rs.getString("COLUMN_NAME").toLowerCase());
      }
      return columns;
    } finally {
      rs.close();
    }
  }

  /**
   * Add one column to an existing table.
   *
   * @param e statement to use to execute the SQL command(s).
   * @param tableName table to add the column onto.
   * @param col definition of the column.
   * @throws OrmException the column could not be added.
   */
  public void addColumn(StatementExecutor e, String tableName, ColumnModel col)
      throws OrmException {
    final StringBuilder r = new StringBuilder();
    r.append("ALTER TABLE ");
    r.append(tableName);
    r.append(" ADD ");
    r.append(col.getColumnName());
    r.append(" ");
    r.append(getSqlTypeInfo(col).getSqlType(col, this));
    String check = getSqlTypeInfo(col).getCheckConstraint(col, this);
    if (check != null) {
      r.append(' ');
      r.append(check);
    }
    e.execute(r.toString());
  }

  /**
   * Drop one column from an existing table.
   *
   * @param e statement to use to execute the SQL command(s).
   * @param tableName table to add the column onto.
   * @param column name of the column to drop.
   * @throws OrmException the column could not be added.
   */
  public void dropColumn(StatementExecutor e, String tableName, String column)
      throws OrmException {
    final StringBuilder r = new StringBuilder();
    r.append("ALTER TABLE ");
    r.append(tableName);
    r.append(" DROP COLUMN ");
    r.append(column);
    e.execute(r.toString());
  }

  /**
   * Rename an existing column in a table.
   *
   * @param e statement to use to execute the SQL command(s).
   * @param tableName table to add the column onto.
   * @param fromColumn source column name
   * @param col destination column definition
   * @throws OrmException the column could not be renamed.
   */
  public abstract void renameColumn(StatementExecutor e, String tableName,
      String fromColumn, ColumnModel col) throws OrmException;

  public abstract String getNextSequenceValueSql(String seqname);
}
