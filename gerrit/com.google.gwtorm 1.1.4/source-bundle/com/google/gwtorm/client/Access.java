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

import java.util.Map;

/**
 * Data access interface for an entity type.
 * <p>
 * Applications should extend this interface for each entity type they need to
 * access. At runtime the application extension will be automatically
 * implemented with a generated class, providing concrete implementations for
 * all methods.
 * <p>
 * Instances should be acquired through the application's extension interface of
 * {@link Schema}.
 * <p>
 * Applications should implement a query method using the {@link PrimaryKey}
 * annotation, for example:
 *
 * <pre>
 * public interface FooAccess extends Access&lt;Foo, Foo.Key&gt; {
 *   &#064;PrimaryKey(&quot;key&quot;)
 *   Foo byKey(Foo.Key k) throws OrmException;
 * }
 *</pre>
 *<p>
 * otherwise the primaryKey, get, update and delete operations declared by this
 * interface will be unsupported.
 *
 * @param <T> type of the entity. Any object type is suitable, so long as at
 *        least one field uses a {@link Column} annotation.
 * @param <K> type of the primary key of entity. If the primary key is a
 *        primitive type then use Key directly, otherwise use a Key
 *        implementation. Entity specific key subclasses are recommended.
 */
public interface Access<T extends Object, K extends Key<?>> {
  /**
   * Obtain the primary key of an entity instance.
   *
   * @param entity the entity to get the key of; must not be null.
   * @return the primary key. Null if this entity has no primary key declared,
   *         or if the primary key does not implement the Key interface.
   */
  K primaryKey(T entity);

  /**
   * Convert a collection of objects into a map, keyed by their primary key.
   *
   * @param c the collection
   * @return a map of the objects, indexed by their primary key.
   */
  Map<K, T> toMap(Iterable<T> c);

  /**
   * Lookup a single entity via its primary key.
   * <p>
   * This method is only implemented if the entity's primary key is defined to
   * be an implementation of the {@link Key} interface. Otherwise the method
   * throws {@link UnsupportedOperationException}.
   *
   * @param key the primary key instance; must not be null.
   * @return the entity; null if no entity has this key.
   * @throws OrmException the data lookup failed.
   * @throws UnsupportedOperationException the key type doesn't implement Key.
   */
  T get(K key) throws OrmException;

  /**
   * Lookup multiple entities via their primary key.
   * <p>
   * This method is only implemented if the entity's primary key is defined to
   * be an implementation of the {@link Key} interface. Otherwise the method
   * throws {@link UnsupportedOperationException}.
   * <p>
   * This method is a batch form of {@link #get(Key)} and may be optimized to
   * reduce round-trips to the data store.
   *
   * @param keys collection of zero or more keys to perform lookup with.
   * @return collection of all matching entities; this may be a smaller result
   *         than the keys supplied if one or more of the keys does not match an
   *         existing entity.
   * @throws OrmException the data lookup failed.
   * @throws UnsupportedOperationException the key type doesn't implement Key.
   */
  ResultSet<T> get(Iterable<K> keys) throws OrmException;

  /**
   * Immediately insert new entities into the data store.
   *
   * @param instances the instances to insert. The iteration occurs only once.
   * @throws OrmException data insertion failed.
   */
  void insert(Iterable<T> instances) throws OrmException;

  /**
   * Insert new entities into the data store.
   *
   * @param instances the instances to insert. The iteration occurs only once.
   * @param txn transaction to batch the operation into. If not null the data
   *        store changes will be delayed to {@link Transaction#commit()} is
   *        invoked; if null the operation occurs immediately.
   * @throws OrmException data insertion failed.
   */
  void insert(Iterable<T> instances, Transaction txn) throws OrmException;

  /**
   * Immediately update existing entities in the data store.
   *
   * @param instances the instances to update. The iteration occurs only once.
   * @throws OrmException data modification failed.
   * @throws UnsupportedOperationException no PrimaryKey was declared.
   */
  void update(Iterable<T> instances) throws OrmException;

  /**
   * Update existing entities in the data store.
   *
   * @param instances the instances to update. The iteration occurs only once.
   * @param txn transaction to batch the operation into. If not null the data
   *        store changes will be delayed to {@link Transaction#commit()} is
   *        invoked; if null the operation occurs immediately.
   * @throws OrmException data modification failed.
   * @throws UnsupportedOperationException no PrimaryKey was declared.
   */
  void update(Iterable<T> instances, Transaction txn) throws OrmException;

  /**
   * Immediately update or insert entities in the data store.
   *
   * @param instances the instances to update. The iteration occurs only once.
   * @throws OrmException data modification failed.
   * @throws UnsupportedOperationException no PrimaryKey was declared.
   */
  void upsert(Iterable<T> instances) throws OrmException;

  /**
   * Update or insert entities in the data store.
   *
   * @param instances the instances to update. The iteration occurs only once.
   * @param txn transaction to batch the operation into. If not null the data
   *        store changes will be delayed to {@link Transaction#commit()} is
   *        invoked; if null the operation occurs immediately.
   * @throws OrmException data modification failed.
   * @throws UnsupportedOperationException no PrimaryKey was declared.
   */
  void upsert(Iterable<T> instances, Transaction txn) throws OrmException;

  /**
   * Immediately delete existing entities from the data store.
   *
   * @param keys the keys to delete. The iteration occurs only once.
   * @throws OrmException data removal failed.
   * @throws UnsupportedOperationException no PrimaryKey was declared.
   */
  void deleteKeys(Iterable<K> keys) throws OrmException;

  /**
   * Immediately delete existing entities from the data store.
   *
   * @param instances the instances to delete. The iteration occurs only once.
   * @throws OrmException data removal failed.
   * @throws UnsupportedOperationException no PrimaryKey was declared.
   */
  void delete(Iterable<T> instances) throws OrmException;

  /**
   * Delete existing entities from the data store.
   *
   * @param instances the instances to delete. The iteration occurs only once.
   * @param txn transaction to batch the operation into. If not null the data
   *        store changes will be delayed to {@link Transaction#commit()} is
   *        invoked; if null the operation occurs immediately.
   * @throws OrmException data removal failed.
   * @throws UnsupportedOperationException no PrimaryKey was declared.
   */
  void delete(Iterable<T> instances, Transaction txn) throws OrmException;

  /**
   * Atomically update a single entity.
   * <p>
   * If the entity does not exist, the method returns {@code null} without
   * invoking {@code update}.
   * <p>
   * If the entity exists, the method invokes {@code update} with a current copy
   * of the entity. The update function should edit the passed instance
   * in-place. The return value will be returned to the caller, but is otherwise
   * ignored by this update function.
   *
   * @param key key which identifies the entity.
   * @param update the update function.
   * @return the updated copy of the entity; or {@code null}.
   * @throws OrmException data update failed.
   */
  T atomicUpdate(K key, AtomicUpdate<T> update) throws OrmException;
}
