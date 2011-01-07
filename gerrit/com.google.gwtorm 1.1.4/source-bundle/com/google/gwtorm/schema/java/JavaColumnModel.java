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

package com.google.gwtorm.schema.java;

import com.google.gwtorm.client.Column;
import com.google.gwtorm.client.OrmException;
import com.google.gwtorm.client.RowVersion;
import com.google.gwtorm.schema.ColumnModel;
import com.google.gwtorm.schema.Util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;


public class JavaColumnModel extends ColumnModel {
  private final Field field;

  public JavaColumnModel(final Field columnField) throws OrmException {
    field = columnField;
    initName(field.getName(), field.getAnnotation(Column.class));

    if (Modifier.isPrivate(field.getModifiers())) {
      throw new OrmException("Field " + field.getName() + " of "
          + field.getDeclaringClass().getName() + " must not be private");
    }
    if (Modifier.isFinal(field.getModifiers())) {
      throw new OrmException("Field " + field.getName() + " of "
          + field.getDeclaringClass().getName() + " must not be final");
    }

    rowVersion = field.getAnnotation(RowVersion.class) != null;
    if (rowVersion && field.getType() != Integer.TYPE) {
      throw new OrmException("Field " + field.getName() + " of "
          + field.getDeclaringClass().getName() + " must have type 'int'");
    }

    if (isNested()) {
      final List<JavaColumnModel> col = new ArrayList<JavaColumnModel>();
      Class<?> in = field.getType();
      while (in != null) {
        for (final Field f : in.getDeclaredFields()) {
          if (f.getAnnotation(Column.class) != null) {
            col.add(new JavaColumnModel(f));
          }
        }
        in = in.getSuperclass();
      }
      initNestedColumns(col);
    }
  }

  @Override
  public String getFieldName() {
    return field.getName();
  }

  @Override
  public Class<?> getPrimitiveType() {
    return isPrimitive() ? field.getType() : null;
  }

  @Override
  public String getNestedClassName() {
    return isPrimitive() ? null : field.getType().getName();
  }

  public Class<?> getNestedClass() {
    return field.getType();
  }

  private boolean isPrimitive() {
    return Util.isSqlPrimitive(field.getType());
  }
}
