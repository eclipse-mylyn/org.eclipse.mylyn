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

package com.google.gwtorm.client;

/**
 * Runs within an isolated database transaction, retrying if necessary.
 * <p>
 * The {@link Schema} is free to invoke this runnable multiple times if an
 * {@link OrmConcurrencyException} is thrown by the run method.
 *
 * @param <T> type of object the run method returns.
 * @param <S> type of schema the run method needs to perform its work.
 */
public interface OrmRunnable<T, S extends Schema> {
  /**
   * Execute the task once.
   * <p>
   * Implementations should read any state they need within the method, to
   * ensure they are looking at the most current copy of the data from the
   * database. If a method is invoked a second time to recover from a
   * concurrency error it would need to read the data again.
   *
   * @param db active schema handle to query through, and make updates on.
   * @param txn the current transaction handle. Commit is invoked by the caller.
   * @param retry true if this is not the first attempt to execute this task.
   * @return the return value of the function, if any.
   * @throws OrmException any database error. {@link OrmConcurrencyException}
   *         may cause the transaction to be retried.
   */
  T run(S db, Transaction txn, boolean retry) throws OrmException;
}
