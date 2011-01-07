// Copyright 2009 Google Inc.
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

package com.google.gwtorm.jdbc;

import com.google.gwtorm.client.OrmException;
import com.google.gwtorm.client.StatementExecutor;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcExecutor implements StatementExecutor {
  private final Statement stmt;

  public JdbcExecutor(final JdbcSchema schema) throws OrmException {
    this(schema.getConnection());
  }

  public JdbcExecutor(final Connection c) throws OrmException {
    try {
      stmt = c.createStatement();
    } catch (SQLException e) {
      throw new OrmException("Cannot create statement for executor", e);
    }
  }

  @Override
  public void execute(String sql) throws OrmException {
    try {
      stmt.execute(sql);
    } catch (SQLException e) {
      throw new OrmException("Cannot apply SQL\n" + sql, e);
    }
  }

  public void close() {
    try {
      stmt.close();
    } catch (SQLException e) {
      //
    }
  }
}
