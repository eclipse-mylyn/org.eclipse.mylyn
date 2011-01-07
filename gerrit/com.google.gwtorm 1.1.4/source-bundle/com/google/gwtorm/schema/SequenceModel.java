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

package com.google.gwtorm.schema;

import com.google.gwtorm.client.OrmException;
import com.google.gwtorm.client.Sequence;
import com.google.gwtorm.schema.sql.SqlDialect;

public class SequenceModel {
  protected String methodName;
  protected String name;
  protected Sequence sequence;
  protected Class<?> returnType;

  public SequenceModel(final String method, final Sequence seq,
      final Class<?> type) throws OrmException {
    if (seq == null) {
      throw new OrmException("Method " + method + " is missing "
          + Sequence.class.getName() + " annotation");
    }
    if (type != Integer.TYPE && type != Long.TYPE) {
      throw new OrmException("Sequence method " + method
          + " must return int or long");
    }

    sequence = seq;
    methodName = method;

    final String n;
    if (methodName.startsWith("next")) {
      n = methodName.substring(4);
    } else {
      n = methodName;
    }
    name = Util.any(sequence.name(), Util.makeSqlFriendly(n));
    returnType = type;
  }

  public String getMethodName() {
    return methodName;
  }

  public String getSequenceName() {
    return name;
  }

  public Class<?> getResultType() {
    return returnType;
  }

  public Sequence getSequence() {
    return sequence;
  }

  public String getCreateSequenceSql(final SqlDialect dialect) {
    return dialect.getCreateSequenceSql(this);
  }

  @Override
  public String toString() {
    final StringBuilder r = new StringBuilder();
    r.append("Sequence[\n");
    r.append("  method: " + getMethodName() + "\n");
    r.append("  name:   " + getSequenceName() + "\n");
    r.append("]");
    return r.toString();
  }
}
