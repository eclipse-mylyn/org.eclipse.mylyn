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

package com.google.gwtorm.client.impl;

import com.google.gwtorm.client.Key;
import com.google.gwtorm.client.OrmException;
import com.google.gwtorm.client.Transaction;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractTransaction implements Transaction {
  private static LinkedHashMap<Object, Action<?, Key<?>, AbstractTransaction>> newMap() {
    return new LinkedHashMap<Object, Action<?, Key<?>, AbstractTransaction>>();
  }

  protected final Map<Object, Action<?, Key<?>, AbstractTransaction>> pendingInsert;
  protected final Map<Object, Action<?, Key<?>, AbstractTransaction>> pendingUpdate;
  protected final Map<Object, Action<?, Key<?>, AbstractTransaction>> pendingUpsert;
  protected final Map<Object, Action<?, Key<?>, AbstractTransaction>> pendingDelete;

  protected AbstractTransaction() {
    pendingInsert = newMap();
    pendingUpdate = newMap();
    pendingUpsert = newMap();
    pendingDelete = newMap();
  }

  public void commit() throws OrmException {
    for (Action<?, Key<?>, AbstractTransaction> a : pendingDelete.values()) {
      a.doDelete(this);
    }
    for (Action<?, Key<?>, AbstractTransaction> a : pendingInsert.values()) {
      a.doInsert(this);
    }
    for (Action<?, Key<?>, AbstractTransaction> a : pendingUpdate.values()) {
      a.doUpdate(this);
    }
    for (Action<?, Key<?>, AbstractTransaction> a : pendingUpsert.values()) {
      a.doUpsert(this);
    }
  }

  <E, K extends Key<?>, T extends AbstractTransaction> void queueInsert(
      final AbstractAccess<E, ?, T> access, final Iterable<E> list) {
    queue(pendingInsert, access, list);
  }

  <E, K extends Key<?>, T extends AbstractTransaction> void queueUpdate(
      final AbstractAccess<E, ?, T> access, final Iterable<E> list) {
    queue(pendingUpdate, access, list);
  }

  <E, K extends Key<?>, T extends AbstractTransaction> void queueUpsert(
      final AbstractAccess<E, ?, T> access, final Iterable<E> list) {
    queue(pendingUpsert, access, list);
  }

  <E, K extends Key<?>, T extends AbstractTransaction> void queueDelete(
      final AbstractAccess<E, ?, T> access, final Iterable<E> list) {
    queue(pendingDelete, access, list);
  }

  private static <E, K extends Key<?>, T extends AbstractTransaction> void queue(
      final Map<Object, Action<?, Key<?>, AbstractTransaction>> queue,
      final AbstractAccess<E, K, T> access, final Iterable<E> list) {
    Action<E, K, T> c = get(queue, access);
    if (c == null) {
      c = new Action<E, K, T>(access);
      put(queue, c);
    }
    c.addAll(list);
  }

  @SuppressWarnings("unchecked")
  private static <E, K extends Key<?>, T extends AbstractTransaction> Action<E, K, T> get(
      final Map<Object, Action<?, Key<?>, AbstractTransaction>> q,
      final AbstractAccess<E, K, T> access) {
    return (Action<E, K, T>) q.get(access);
  }

  @SuppressWarnings("unchecked")
  private static <E, K extends Key<?>, T extends AbstractTransaction> void put(
      final Map queue, Action<E, K, T> c) {
    // This silly little method was needed to defeat the Java compiler's
    // generic type checking. Somehow we got lost in the anonymous types
    // from all the ? in our Map definition and the compiler just won't let
    // us do a put into the map.
    //
    queue.put(c.access, c);
  }

  private static class Action<E, K extends Key<?>, T extends AbstractTransaction> {
    private final AbstractAccess<E, K, T> access;
    private final Set<E> instances;

    Action(final AbstractAccess<E, K, T> a) {
      access = a;
      instances = new LinkedHashSet<E>();
    }

    void addAll(final Iterable<E> list) {
      for (final E o : list) {
        instances.add(o);
      }
    }

    void doInsert(final T t) throws OrmException {
      access.doInsert(instances, t);
    }

    void doUpdate(final T t) throws OrmException {
      access.doUpdate(instances, t);
    }

    void doUpsert(final T t) throws OrmException {
      access.doUpsert(instances, t);
    }

    void doDelete(final T t) throws OrmException {
      access.doDelete(instances, t);
    }
  }
}
