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

import com.google.gwtorm.client.OrmException;
import com.google.gwtorm.client.Relation;
import com.google.gwtorm.client.Schema;
import com.google.gwtorm.client.Sequence;
import com.google.gwtorm.schema.SchemaModel;
import com.google.gwtorm.schema.SequenceModel;

import java.lang.reflect.Method;


public class JavaSchemaModel extends SchemaModel {
  private final Class<?> schema;

  public JavaSchemaModel(final Class<?> schemaInterface) throws OrmException {
    schema = schemaInterface;

    if (!schema.isInterface()) {
      throw new OrmException("Schema " + schema.getName()
          + " must be an interface");
    }

    if (schema.getInterfaces().length != 1
        || schema.getInterfaces()[0] != Schema.class) {
      throw new OrmException("Schema " + schema.getName()
          + " must only extend " + Schema.class.getName());
    }

    for (final Method m : schema.getDeclaredMethods()) {
      if (m.getAnnotation(Relation.class) != null) {
        add(new JavaRelationModel(m));
        continue;
      }

      final Sequence seq = m.getAnnotation(Sequence.class);
      if (seq != null) {
        add(new SequenceModel(m.getName(), seq, m.getReturnType()));
        continue;
      }
    }
  }

  @Override
  public String getSchemaClassName() {
    return schema.getName();
  }
}
