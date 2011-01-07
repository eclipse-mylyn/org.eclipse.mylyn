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
 * Atomically updates exactly one entity.
 */
public interface AtomicUpdate<T> {
  /**
   * Update the one object, in place.
   * <p>
   * This method may be called multiple times, up until the retry limit.
   *
   * @param row a fresh copy of the object. The updater should modify it in
   *        place and return, the caller will attempt to rewrite it into the
   *        database.
   * @return return value for the application code calling the atomic update;
   *         should be either {@code instance} or {@code null}.
   */
  T update(T row);
}
