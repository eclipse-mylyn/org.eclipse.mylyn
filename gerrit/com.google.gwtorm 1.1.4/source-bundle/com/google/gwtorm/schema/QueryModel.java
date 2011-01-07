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
import com.google.gwtorm.client.Query;
import com.google.gwtorm.schema.QueryParser.Column;
import com.google.gwtorm.schema.sql.SqlBooleanTypeInfo;
import com.google.gwtorm.schema.sql.SqlDialect;

import org.antlr.runtime.CommonToken;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.Tree;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class QueryModel {
  private final RelationModel model;
  private final String name;
  private final Query query;
  private final Tree parsedQuery;

  public QueryModel(final RelationModel rel, final String queryName,
      final Query q) throws OrmException {
    if (q == null) {
      throw new OrmException("Query " + queryName + " is missing "
          + Query.class.getName() + " annotation");
    }

    model = rel;
    name = queryName;
    query = q;

    try {
      parsedQuery = QueryParser.parse(model, q.value());
    } catch (QueryParseException e) {
      throw new OrmException("Cannot parse query " + q.value(), e);
    }
  }

  public String getName() {
    return name;
  }

  public Tree getParseTree() {
    return parsedQuery;
  }

  public List<ColumnModel> getParameters() {
    final ArrayList<ColumnModel> r = new ArrayList<ColumnModel>();
    if (parsedQuery != null) {
      findParameters(r, parsedQuery);
    }
    return r;
  }

  private void findParameters(final List<ColumnModel> r, final Tree node) {
    switch (node.getType()) {
      case QueryParser.WHERE:
        extractParameters(r, node);
        break;

      default:
        for (int i = 0; i < node.getChildCount(); i++) {
          findParameters(r, node.getChild(i));
        }
        break;
    }
  }

  private void extractParameters(final List<ColumnModel> r, final Tree node) {
    switch (node.getType()) {
      case QueryParser.LT:
      case QueryParser.LE:
      case QueryParser.GT:
      case QueryParser.GE:
      case QueryParser.EQ:
      case QueryParser.NE:
        if (node.getChild(1).getType() == QueryParser.PLACEHOLDER) {
          r.add(((QueryParser.Column) node.getChild(0)).getField());
        }
        break;

      default:
        for (int i = 0; i < node.getChildCount(); i++) {
          extractParameters(r, node.getChild(i));
        }
        break;
    }
  }

  public boolean hasLimit() {
    return findLimit(parsedQuery) != null;
  }

  public boolean hasLimitParameter() {
    final Tree limit = findLimit(parsedQuery);
    return limit != null
        && limit.getChild(0).getType() == QueryParser.PLACEHOLDER;
  }

  public int getStaticLimit() {
    return Integer.parseInt(findLimit(parsedQuery).getChild(0).getText());
  }

  private Tree findLimit(final Tree node) {
    if (node == null) {
      return null;
    }
    switch (node.getType()) {
      case QueryParser.LIMIT:
        return node;
      default:
        for (int i = 0; i < node.getChildCount(); i++) {
          final Tree r = findLimit(node.getChild(i));
          if (r != null) {
            return r;
          }
        }
        return null;
    }
  }

  public String getSelectSql(final SqlDialect dialect, final String tableAlias) {
    final StringBuilder buf = new StringBuilder();
    buf.append(model.getSelectSql(dialect, tableAlias));
    if (parsedQuery != null) {
      final FormatInfo fmt = new FormatInfo(buf, dialect, tableAlias);
      final Tree t = expand(parsedQuery);
      if (t.getType() == 0) {
        formatChilden(fmt, t);
      } else {
        format(fmt, t);
      }
    }
    return buf.toString();
  }

  private void formatChilden(final FormatInfo fmt, final Tree node) {
    for (int i = 0; i < node.getChildCount(); i++) {
      format(fmt, node.getChild(i));
    }
  }

  private void format(final FormatInfo fmt, final Tree node) {
    switch (node.getType()) {
      case QueryParser.WHERE:
        fmt.buf.append(" WHERE ");
        formatChilden(fmt, node);
        break;

      case QueryParser.AND:
        for (int i = 0; i < node.getChildCount(); i++) {
          if (i > 0) {
            fmt.buf.append(" AND ");
          }
          format(fmt, node.getChild(i));
        }
        break;

      case QueryParser.LT:
      case QueryParser.LE:
      case QueryParser.GT:
      case QueryParser.GE:
      case QueryParser.EQ:
        format(fmt, node.getChild(0));
        fmt.buf.append(node.getText());
        format(fmt, node.getChild(1));
        break;
      case QueryParser.NE:
        format(fmt, node.getChild(0));
        fmt.buf.append("<>");
        format(fmt, node.getChild(1));
        break;

      case QueryParser.ID: {
        final ColumnModel col = ((QueryParser.Column) node).getField();
        if (!col.isSqlPrimitive()) {
          throw new IllegalStateException("Unexpanded nested field");
        }
        fmt.buf.append(fmt.tableAlias);
        fmt.buf.append('.');
        fmt.buf.append(col.getColumnName());
        break;
      }

      case QueryParser.PLACEHOLDER:
        fmt.buf.append(fmt.dialect.getParameterPlaceHolder(fmt.nthParam++));
        break;

      case QueryParser.TRUE:
        fmt.buf.append(((SqlBooleanTypeInfo) fmt.dialect
            .getSqlTypeInfo(Boolean.TYPE)).getTrueLiteralValue());
        break;

      case QueryParser.FALSE:
        fmt.buf.append(((SqlBooleanTypeInfo) fmt.dialect
            .getSqlTypeInfo(Boolean.TYPE)).getFalseLiteralValue());
        break;

      case QueryParser.CONSTANT_INTEGER:
      case QueryParser.CONSTANT_STRING:
        fmt.buf.append(node.getText());
        break;

      case QueryParser.ORDER:
        fmt.buf.append(" ORDER BY ");
        for (int i = 0; i < node.getChildCount(); i++) {
          final Tree sortOrder = node.getChild(i);
          final Tree id = sortOrder.getChild(0);
          if (i > 0) {
            fmt.buf.append(',');
          }
          final ColumnModel col = ((QueryParser.Column) id).getField();
          if (col.isNested()) {
            for (final Iterator<ColumnModel> cItr =
                col.getAllLeafColumns().iterator(); cItr.hasNext();) {
              fmt.buf.append(fmt.tableAlias);
              fmt.buf.append('.');
              fmt.buf.append(cItr.next().getColumnName());
              if (sortOrder.getType() == QueryParser.DESC) {
                fmt.buf.append(" DESC");
              }
              if (cItr.hasNext()) {
                fmt.buf.append(',');
              }
            }
          } else {
            fmt.buf.append(fmt.tableAlias);
            fmt.buf.append('.');
            fmt.buf.append(col.getColumnName());
            if (sortOrder.getType() == QueryParser.DESC) {
              fmt.buf.append(" DESC");
            }
          }
        }
        break;

      case QueryParser.LIMIT:
        if (fmt.dialect.selectHasLimit()) {
          final Tree p = node.getChild(0);
          if (p.getType() == QueryParser.CONSTANT_INTEGER) {
            fmt.buf.append(" LIMIT ");
            fmt.buf.append(p.getText());
          }
        }
        break;

      default:
        throw new IllegalStateException("Unsupported query token");
    }
  }

  @Override
  public String toString() {
    return "Query[" + name + " " + query.value() + "]";
  }

  private Tree expand(final Tree node) {
    switch (node.getType()) {
      case QueryParser.LT:
      case QueryParser.LE:
      case QueryParser.GT:
      case QueryParser.GE:
      case QueryParser.EQ:
      case QueryParser.NE: {
        final Column qpc = (QueryParser.Column) node.getChild(0);
        final ColumnModel f = qpc.getField();
        if (f.isNested()) {
          final CommonTree join;

          join = new CommonTree(new CommonToken(QueryParser.AND));
          for (final ColumnModel c : f.getAllLeafColumns()) {
            final Tree op;

            op = node.dupNode();
            op.addChild(new QueryParser.Column(qpc, c));
            op.addChild(node.getChild(1).dupNode());
            join.addChild(op);
          }
          return join;
        }
      }
    }

    final Tree r = node.dupNode();
    for (int i = 0; i < node.getChildCount(); i++) {
      r.addChild(expand(node.getChild(i)));
    }
    return r;
  }

  static class FormatInfo {
    final StringBuilder buf;
    final SqlDialect dialect;
    final String tableAlias;
    int nthParam = 1;

    FormatInfo(StringBuilder r, SqlDialect dialect, String tableAlias) {
      this.buf = r;
      this.dialect = dialect;
      this.tableAlias = tableAlias;
    }
  }
}
