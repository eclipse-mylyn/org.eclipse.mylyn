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

package com.google.gwtorm.jdbc;

import com.google.gwtorm.client.OrmException;
import com.google.gwtorm.client.Transaction;
import com.google.gwtorm.client.impl.AbstractTransaction;

import java.sql.SQLException;

/** Implementation of the {@link Transaction} interface, on JDBC. */
class JdbcTransaction extends AbstractTransaction {
  private final JdbcSchema schema;
  private boolean inProgress;
  private boolean committed;

  JdbcTransaction(final JdbcSchema s) {
    schema = s;
  }

  @Override
  public void commit() throws OrmException {
    notCommitted();

    if (!inProgress) {
      try {
        schema.getConnection().setAutoCommit(false);
      } catch (SQLException e) {
        throw new OrmException("Cannot start transaction", e);
      }
      inProgress = true;
    }

    try {
      super.commit();
    } catch (OrmException e) {
      try {
        rollback();
      } catch (OrmException e2) {
        // Ignore the cascaded rollback error.
      }
      throw e;
    } catch (RuntimeException e) {
      try {
        rollback();
      } catch (OrmException e2) {
        // Ignore the cascaded rollback error.
      }
      throw e;
    }

    try {
      schema.getConnection().commit();
      committed = true;
    } catch (SQLException e) {
      throw new OrmException("Transaction failed", e);
    } finally {
      exitTransaction();
    }
  }

  public void rollback() throws OrmException {
    notCommitted();

    if (inProgress) {
      try {
        schema.getConnection().rollback();
      } catch (SQLException e) {
        throw new OrmException("Rollback failed", e);
      } finally {
        exitTransaction();
      }
    }
  }

  private void notCommitted() throws OrmException {
    if (committed) {
      throw new OrmException("Transaction already committed");
    }
  }

  private void exitTransaction() {
    try {
      schema.getConnection().setAutoCommit(true);
    } catch (SQLException e) {
    } finally {
      inProgress = false;
    }
  }
}
