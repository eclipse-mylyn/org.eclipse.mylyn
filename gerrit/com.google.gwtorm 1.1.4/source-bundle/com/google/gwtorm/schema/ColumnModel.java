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

import com.google.gwtorm.client.Column;
import com.google.gwtorm.client.OrmException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class ColumnModel {
  protected ColumnModel parent;
  private String origName;
  protected int columnId;
  protected String columnName;
  protected Column column;
  protected Collection<ColumnModel> nestedColumns;
  protected boolean rowVersion;
  protected boolean inPrimaryKey;
  protected boolean notNull;

  protected ColumnModel() {
    nestedColumns = Collections.<ColumnModel> emptyList();
  }

  protected void initName(final String fieldName, final Column col)
      throws OrmException {
    if (col == null) {
      throw new OrmException("Field " + fieldName + " is missing "
          + Column.class.getName() + " annotation");
    }
    column = col;
    origName = Util.any(column.name(), Util.makeSqlFriendly(fieldName));
    columnName = origName;
    columnId = column.id();
    if (columnId < 1) {
      throw new OrmException("Field " + fieldName + " cannot have id < 1");
    }
    notNull = column.notNull();
  }

  protected void initNestedColumns(final Collection<? extends ColumnModel> col)
      throws OrmException {
    if (col == null || col.isEmpty()) {
      throw new OrmException("Field " + getPathToFieldName()
          + " has no nested members inside type " + getNestedClassName());
    }

    nestedColumns = new ArrayList<ColumnModel>(col);
    recomputeColumnNames();
    if (!isNotNull()) {
      // If we permit NULL we need to make sure our contained columns
      // would also accept NULL, even if they wouldn't otherwise.
      //
      for (final ColumnModel c : getAllLeafColumns()) {
        c.notNull = false;
      }
    }

    Set<Integer> ids = new HashSet<Integer>();
    for (final ColumnModel c : nestedColumns) {
      if (!ids.add(c.columnId)) {
        throw new OrmException("Duplicate @Column id " + c.columnId + " in "
            + c.getPathToFieldName());
      }
    }
  }

  private void recomputeColumnNames() {
    final boolean thisHasName = !columnName.equals(Column.NONE);
    for (final ColumnModel c : nestedColumns) {
      c.parent = this;
      if (nestedColumns.size() == 1) {
        c.columnName = thisHasName ? columnName : c.origName;
      } else {
        if (thisHasName) {
          c.columnName = columnName + "_" + c.origName;
        } else {
          c.columnName = c.origName;
        }
      }
      c.recomputeColumnNames();
    }
  }

  public Collection<ColumnModel> getNestedColumns() {
    return nestedColumns;
  }

  public ColumnModel getField(final String name) {
    for (final ColumnModel c : nestedColumns) {
      if (c.getFieldName().equals(name)) {
        return c;
      }
    }
    return null;
  }

  public Collection<ColumnModel> getAllLeafColumns() {
    final ArrayList<ColumnModel> r = new ArrayList<ColumnModel>();
    for (final ColumnModel c : nestedColumns) {
      if (c.isNested()) {
        r.addAll(c.getAllLeafColumns());
      } else {
        r.add(c);
      }
    }
    return r;
  }

  public ColumnModel getParent() {
    return parent;
  }

  public String getPathToFieldName() {
    if (getParent() == null) {
      return getFieldName();
    }
    return getParent().getPathToFieldName() + "." + getFieldName();
  }

  public int getColumnID() {
    return columnId;
  }

  public String getColumnName() {
    return columnName;
  }

  public boolean isSqlPrimitive() {
    return getPrimitiveType() != null;
  }

  public boolean isNested() {
    return getPrimitiveType() == null;
  }

  public boolean isRowVersion() {
    return rowVersion;
  }

  public Column getColumnAnnotation() {
    return column;
  }

  public boolean isNotNull() {
    return notNull;
  }

  public abstract String getFieldName();

  public abstract Class<?> getPrimitiveType();

  public abstract String getNestedClassName();

  @Override
  public String toString() {
    final StringBuilder r = new StringBuilder();
    r.append("Column[\n");
    r.append("  id:       " + getColumnID() + "\n");
    r.append("  field:    " + getPathToFieldName() + "\n");
    r.append("  column:   " + getColumnName() + "\n");
    if (isSqlPrimitive()) {
      r.append("  type:     " + getPrimitiveType().getName() + "\n");
    } else {
      r.append("  contains: " + getNestedClassName() + "\n");
    }
    r.append("]");
    return r.toString();
  }
}
