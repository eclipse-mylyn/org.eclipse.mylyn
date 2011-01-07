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

import java.util.Collection;
import java.util.Collections;

public class KeyModel {
  protected final String accessName;
  protected final ColumnModel key;

  public KeyModel(final String name, final ColumnModel keyField) {
    accessName = name;
    key = keyField;
  }

  public String getName() {
    return accessName;
  }

  public ColumnModel getField() {
    return key;
  }

  public Collection<ColumnModel> getAllLeafColumns() {
    if (key.isNested()) {
      return key.getAllLeafColumns();
    }
    return Collections.singleton(key);
  }

  @Override
  public String toString() {
    final StringBuilder r = new StringBuilder();
    r.append("Key[");
    r.append(getName());
    r.append(" / ");
    r.append(key.getPathToFieldName());
    r.append(":");
    for (final ColumnModel c : getAllLeafColumns()) {
      r.append(" " + c.getColumnName());
    }
    r.append("]");
    return r.toString();
  }
}
