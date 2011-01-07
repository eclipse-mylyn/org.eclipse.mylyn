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

import com.google.gwtorm.client.KeyUtil;
import com.google.gwtorm.client.OrmException;
import com.google.gwtorm.client.Schema;
import com.google.gwtorm.client.SchemaFactory;
import com.google.gwtorm.jdbc.gen.GeneratedClassLoader;
import com.google.gwtorm.jdbc.gen.SchemaFactoryGen;
import com.google.gwtorm.jdbc.gen.SchemaGen;
import com.google.gwtorm.schema.SchemaModel;
import com.google.gwtorm.schema.java.JavaSchemaModel;
import com.google.gwtorm.schema.sql.DialectH2;
import com.google.gwtorm.schema.sql.DialectMySQL;
import com.google.gwtorm.schema.sql.DialectPostgreSQL;
import com.google.gwtorm.schema.sql.SqlDialect;
import com.google.gwtorm.server.StandardKeyEncoder;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import javax.sql.DataSource;

/**
 * Constructor for application {@link Schema} extensions.
 * <p>
 * Applications should use the Database class to create instances of their
 * Schema extension interface, and thus open and connect to the JDBC data store.
 * <p>
 * Creating a new Database instance is expensive, due to the type analysis and
 * code generation performed to implement the Schema and Access interfaces.
 * Applications should create and cache their Database instance for the live of
 * the application.
 * <p>
 * Database instances are thread-safe, but returned Schema instances are not.
 *
 * @param <T>
 */
public class Database<T extends Schema> implements SchemaFactory<T> {
  private static final Map<SchemaKey, String> schemaFactoryNames =
      Collections.synchronizedMap(new WeakHashMap<SchemaKey, String>());

  private static class SchemaKey {
    final Class<?> schema;
    final SqlDialect dialect;

    SchemaKey(Class<?> s, SqlDialect d) {
      schema = s;
      dialect = d;
    }

    @Override
    public int hashCode() {
      return schema.hashCode() * 31 + dialect.getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof SchemaKey) {
        SchemaKey a = this;
        SchemaKey b = (SchemaKey) o;

        return a.schema == b.schema
            && a.dialect.getClass() == b.dialect.getClass();
      }
      return false;
    }
  }

  static {
    KeyUtil.setEncoderImpl(new StandardKeyEncoder());
  }

  private final DataSource dataSource;
  private final JavaSchemaModel schemaModel;
  private final AbstractSchemaFactory<T> implFactory;
  private final SqlDialect implDialect;

  /**
   * Create a new database interface, generating the interface implementations.
   *
   * @param ds JDBC connection information
   * @param schema application extension of the Schema interface to implement.
   * @throws OrmException the schema interface is incorrectly defined, or the
   *         driver class is not available through the current class loader.
   */
  public Database(final DataSource ds, final Class<T> schema)
      throws OrmException {
    dataSource = ds;

    SqlDialect dialect;
    try {
      final Connection c = ds.getConnection();
      try {
        final String url = c.getMetaData().getURL();
        if (url.startsWith("jdbc:postgresql:")) {
          dialect = new DialectPostgreSQL();

        } else if (url.startsWith("jdbc:h2:")) {
          dialect = new DialectH2();

        } else if (url.startsWith("jdbc:mysql:")) {
          dialect = new DialectMySQL();

        } else {
          throw new OrmException("No dialect known for " + url);
        }

        dialect = dialect.refine(c);
      } finally {
        c.close();
      }
    } catch (SQLException e) {
      throw new OrmException("Unable to determine driver URL", e);
    }

    schemaModel = new JavaSchemaModel(schema);
    final GeneratedClassLoader loader = newLoader(schema);
    final SchemaKey key = new SchemaKey(schema, dialect);
    final String cachedName = schemaFactoryNames.get(key);
    AbstractSchemaFactory<T> factory = null;
    if (cachedName != null) {
      factory = newFactory(loader, cachedName);
    }
    if (factory == null) {
      final SchemaGen gen = new SchemaGen(loader, schemaModel, dialect);
      gen.defineClass();
      factory = new SchemaFactoryGen<T>(loader, gen).create();
      schemaFactoryNames.put(key, factory.getClass().getName());
    }
    implFactory = factory;
    implDialect = dialect;
  }

  @SuppressWarnings("unchecked")
  private AbstractSchemaFactory<T> newFactory(final ClassLoader cl,
      final String name) {
    try {
      final Class<?> ft = Class.forName(name, true, cl);
      return (AbstractSchemaFactory<T>) ft.newInstance();
    } catch (InstantiationException e) {
      return null;
    } catch (IllegalAccessException e) {
      return null;
    } catch (ClassNotFoundException e) {
      return null;
    }
  }

  SqlDialect getDialect() {
    return implDialect;
  }

  SchemaModel getSchemaModel() {
    return schemaModel;
  }

  /**
   * Open a new connection to the database and get a Schema wrapper.
   *
   * @return a new JDBC connection, wrapped up in the application's Schema.
   * @throws OrmException the connection could not be opened to the database.
   *         The JDBC exception detail should be examined to determine the root
   *         cause of the connection failure.
   */
  public T open() throws OrmException {
    final Connection conn;
    try {
      conn = dataSource.getConnection();
    } catch (SQLException e) {
      throw new OrmException("Cannot open database connection", e);
    }

    try {
      if (!conn.getAutoCommit()) {
        conn.setAutoCommit(true);
      }
    } catch (SQLException e) {
      try {
        conn.close();
      } catch (SQLException e2) {
      }
      throw new OrmException("Cannot force auto-commit on connection", e);
    }

    return implFactory.create(this, conn);
  }

  private static <T> GeneratedClassLoader newLoader(final Class<T> schema) {
    return new GeneratedClassLoader(schema.getClassLoader());
  }
}
