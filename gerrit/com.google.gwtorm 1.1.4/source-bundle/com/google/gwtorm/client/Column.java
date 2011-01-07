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
 * Annotation marking an entity field for persistence in the data store.
 * <p>
 * Fields marked with <code>Column</code> must not be final and must not be
 * private. Fields which might be accessed cross-packages (such as those
 * declared in a common Key type like {@link StringKey}) must be declared with
 * public access so generated code can access them directly.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Column {
  /** Special value for {@link #name()} to indicate the name is empty. */
  public static final String NONE = "--NONE--";

  /** @return unique identity of this field within its parent object. */
  int id();

  /**
   * @return name of the column in the data store. Defaults to the field name.
   */
  String name() default "";

  /**
   * @return maximum length (in characters). Only valid for String.
   */
  int length() default 0;

  /**
   * @return is a value required. Defaults to true (NOT NULL).
   */
  boolean notNull() default true;
}
