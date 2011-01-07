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

package com.google.gwtorm.client;

/**
 * Creates new application Schema instances on demand.
 *
 * @param <S> schema type which defines the application database's.
 */
public interface SchemaFactory<S extends Schema> {
  /**
   * Open a new connection to the database and get a Schema wrapper.
   *
   * @return a new connection, wrapped up in the application's Schema.
   * @throws OrmException the connection could not be opened to the database.
   *         The exception detail should be examined to determine the root cause
   *         of the connection failure.
   */
  S open() throws OrmException;
}
