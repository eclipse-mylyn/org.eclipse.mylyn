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

import com.google.gwtorm.client.Access;
import com.google.gwtorm.client.Column;
import com.google.gwtorm.client.OrmException;
import com.google.gwtorm.client.PrimaryKey;
import com.google.gwtorm.client.Query;
import com.google.gwtorm.client.Relation;
import com.google.gwtorm.client.ResultSet;
import com.google.gwtorm.schema.QueryModel;
import com.google.gwtorm.schema.RelationModel;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

class JavaRelationModel extends RelationModel {
  private final Method method;
  private final Class<?> accessType;
  private final Class<?> entityType;

  JavaRelationModel(final Method m) throws OrmException {
    method = m;
    initName(method.getName(), m.getAnnotation(Relation.class));

    accessType = method.getReturnType();
    if (accessType.getInterfaces().length != 1
        || accessType.getInterfaces()[0] != Access.class) {
      throw new OrmException("Method " + method.getName() + " in "
          + method.getDeclaringClass().getName()
          + " must return a direct extension of " + Access.class);
    }

    final Type gt = accessType.getGenericInterfaces()[0];
    if (!(gt instanceof ParameterizedType)) {
      throw new OrmException(accessType.getName()
          + " must specify entity type parameter for " + Access.class);
    }

    entityType =
        (Class<?>) ((ParameterizedType) gt).getActualTypeArguments()[0];

    initColumns();
    initQueriesAndKeys();
  }

  private void initColumns() throws OrmException {
    final List<JavaColumnModel> col = new ArrayList<JavaColumnModel>();
    Class<?> in = entityType;
    while (in != null) {
      for (final Field f : in.getDeclaredFields()) {
        if (f.getAnnotation(Column.class) != null) {
          col.add(new JavaColumnModel(f));
        }
      }
      in = in.getSuperclass();
    }
    initColumns(col);
  }

  private void initQueriesAndKeys() throws OrmException {
    for (final Method m : accessType.getDeclaredMethods()) {
      if (m.getAnnotation(PrimaryKey.class) != null) {
        if (m.getReturnType() != entityType) {
          throw new OrmException("PrimaryKey " + m.getName() + " must return "
              + entityType.getName());
        }
        initPrimaryKey(m.getName(), m.getAnnotation(PrimaryKey.class));

      } else if (m.getAnnotation(Query.class) != null) {
        if (!ResultSet.class.isAssignableFrom(m.getReturnType())
            || !(m.getGenericReturnType() instanceof ParameterizedType)
            || ((ParameterizedType) m.getGenericReturnType())
                .getActualTypeArguments()[0] != entityType) {
          throw new OrmException("Query " + m.getName() + " must return"
              + " ResultSet<" + entityType.getName() + ">");
        }
        addQuery(new QueryModel(this, m.getName(), m.getAnnotation(Query.class)));
      }
    }
  }

  @Override
  public String getAccessInterfaceName() {
    return accessType.getName();
  }

  @Override
  public String getEntityTypeClassName() {
    return entityType.getName();
  }
}
