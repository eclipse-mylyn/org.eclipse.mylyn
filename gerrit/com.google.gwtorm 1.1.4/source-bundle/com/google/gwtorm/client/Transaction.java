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
 * An active transaction running on a {@link Schema}.
 * <p>
 * Applications must invoke {@link #commit()} to finish a transaction.
 * <p>
 * Use method on one or more {@link Access} instances to schedule changes into
 * an open transaction:
 * <ul>
 * <li>{@link Access#insert(Iterable, Transaction)}</li>
 * <li>{@link Access#update(Iterable, Transaction)}</li>
 * <li>{@link Access#delete(Iterable, Transaction)}</li>
 * <ul>
 *
 * @see Schema#beginTransaction()
 */
public interface Transaction {
  /**
   * Commit this transaction, finishing all actions.
   *
   * @throws OrmException data store refused/rejected one or more actions.
   */
  void commit() throws OrmException;

  /**
   * Rollback (abort) this transaction, performing none of the actions.
   * <p>
   * This method has no affect if the transaction has not made any changes.
   *
   * @throws OrmException data store couldn't undo the transaction, as it is
   *         already committed.
   */
  void rollback() throws OrmException;
}
