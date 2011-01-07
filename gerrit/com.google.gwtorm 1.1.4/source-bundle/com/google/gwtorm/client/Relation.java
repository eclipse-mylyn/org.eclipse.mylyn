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
 * Annotation marking a method in a {@link Schema} interface as entity access.
 * <p>
 * Access methods must return an interface extending {@link Access}, for
 * example:
 *
 * <pre>
 * public interface FooAccess extends Access&lt;Foo, Foo.Key&gt; {
 * }
 *
 * public interface BarSchema extends Schema {
 *   &#064;Relation
 *   FooAccess foos();
 * }
 * </pre>
 * <p>
 * The table name within the data store will be derived from the relation
 * annotation, or the method name.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Relation {
  /**
   * @return the name of the data store table. Defaults to the method name.
   */
  String name() default "";
}
