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
 * Annotation marking a field as the row version used for optimistic locking.
 * <p>
 * Fields marked with <code>RowVersion</code> must also be marked with
 * {@link Column} and must be of type <code>int</code>. The field will be
 * automatically incremented during INSERT and UPDATE operations, and will be
 * tested during UPDATE and DELETE operations. Concurrent modifications of the
 * same entity fail as the row version won't match.
 * <p>
 * At most one RowVersion annotation should appear in any entity.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface RowVersion {
}
