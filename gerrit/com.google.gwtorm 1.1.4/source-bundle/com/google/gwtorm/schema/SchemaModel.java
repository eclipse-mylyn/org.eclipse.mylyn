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
import com.google.gwtorm.schema.sql.SqlDialect;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


public abstract class SchemaModel {
  protected final Set<String> allNames;
  protected final Map<String, RelationModel> relations;
  protected final Map<String, SequenceModel> sequences;

  protected SchemaModel() {
    allNames = new HashSet<String>();
    relations = new LinkedHashMap<String, RelationModel>();
    sequences = new LinkedHashMap<String, SequenceModel>();
  }

  protected void add(final RelationModel r) throws OrmException {
    final String n = r.getRelationName();
    checkNotUsed(n);
    if (relations.put(n, r) != null) {
      throw new OrmException("Duplicate relations " + n);
    }
    allNames.add(n);
  }

  protected void add(final SequenceModel s) throws OrmException {
    final String n = s.getSequenceName();
    checkNotUsed(n);
    if (sequences.put(n, s) != null) {
      throw new OrmException("Duplicate sequences " + n);
    }
    allNames.add(n);
  }

  private void checkNotUsed(final String n) throws OrmException {
    if (allNames.contains(n)) {
      throw new OrmException("Name " + n + " already used");
    }
  }

  public Collection<RelationModel> getRelations() {
    return relations.values();
  }

  public Collection<SequenceModel> getSequences() {
    return sequences.values();
  }

  public String getCreateDatabaseSql(final SqlDialect dialect) {
    final StringBuffer r = new StringBuffer();

    for (final SequenceModel seq : getSequences()) {
      r.append(seq.getCreateSequenceSql(dialect));
      r.append(";\n");
    }
    if (!getSequences().isEmpty()) {
      r.append("\n");
    }

    for (final RelationModel rel : getRelations()) {
      r.append(rel.getCreateTableSql(dialect));
      r.append(";\n\n");
    }

    return r.toString();
  }

  public abstract String getSchemaClassName();

  @Override
  public String toString() {
    return "Schema[" + getSchemaClassName() + "]";
  }
}
