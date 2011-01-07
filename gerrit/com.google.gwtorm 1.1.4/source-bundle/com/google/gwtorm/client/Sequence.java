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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation marking a method in {@link Schema} interface as number generator.
 * <p>
 * Sequence methods must return a primitive <code>int</code> or
 * <code>long</code> type.
 *
 * <pre>
 * public interface BarSchema extends Schema {
 *   &#064;Sequence
 *   int nextId();
 * }
 * </pre>
 * <p>
 * The sequence name will be taken from the method name, after removing the
 * optional prefix "next".
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Sequence {
  /**
   * @return the name of the sequence. Defaults to the method name.
   */
  String name() default "";

  /**
   * @return the initial value of the sequence. Defaults to 1, or whatever the
   *         database dialect defaults to if the sequence starting value is not
   *         supplied in the sequence declaration.
   */
  long startWith() default 0;

  /**
   * @return maximum number of values to cache in memory from the sequence.
   *         Defaults to -1, indicating a default caching level should be
   *         determined by the database. Cached values may be lost (never
   *         returned by the sequence, creating gaps) if the application or the
   *         database is shutdown and restarted.
   */
  int cache() default -1;
}
