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

import com.google.gwtorm.client.Schema;

import java.sql.Connection;

/**
 * Internal interface to quickly create Schema instances.
 * <p>
 * Applications should not use this interface. It is automatically implemented
 * at runtime to provide fast construction for new Schema instances within
 * {@link Database#open()}.
 *
 * @param <T> type of the application schema.
 */
public abstract class AbstractSchemaFactory<T extends Schema> {
  /**
   * Create a new schema instance.
   *
   * @param db the database instance which created the connection.
   * @param c the JDBC connection the instance will talk to the database on.
   * @return the new schema instance, wrapping the connection.
   */
  public abstract T create(Database<T> db, Connection c);
}
