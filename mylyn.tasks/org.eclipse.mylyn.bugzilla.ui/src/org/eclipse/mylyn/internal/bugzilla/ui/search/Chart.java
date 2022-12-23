/*******************************************************************************
 * Copyright (c) 2011, 2013 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.search;

import java.io.Serializable;
import java.util.ArrayList;

public class Chart implements Serializable {

	private static final long serialVersionUID = 733808118770522535L;

	private final ArrayList<ArrayList<ChartExpression>> expressions;

	private boolean negate;

	public Chart() {
		super();
		ChartExpression expression = new ChartExpression(0, 0, ""); //$NON-NLS-1$
		ArrayList<ChartExpression> column = new ArrayList<ChartExpression>(1);
		column.add(expression);
		expressions = new ArrayList<ArrayList<ChartExpression>>(1);
		expressions.add(column);
		negate = false;
	}

	public boolean isNegate() {
		return negate;
	}

	public void setNegate(boolean negate) {
		this.negate = negate;
	}

	public void addExpression(int rowIndex, int columnIndex) {
		ChartExpression expression = new ChartExpression(0, 0, ""); //$NON-NLS-1$
		int size = expressions.size();
		if (rowIndex > size + 1) {
			rowIndex = size + 1;
		}
		if (rowIndex < 0) {
			rowIndex = 0;
		}
		ArrayList<ChartExpression> row;
		if (rowIndex == size) {
			row = new ArrayList<ChartExpression>();
			expressions.add(rowIndex, row);
		} else {
			row = expressions.get(rowIndex);
		}
		if (row != null) {
			int size1 = expressions.size();
			if (columnIndex > size1 + 1) {
				columnIndex = size1 + 1;
			}
			if (columnIndex < 0) {
				columnIndex = 0;
			}
			row.add(columnIndex, expression);
		}
	}

	public int getRowSize() {
		return expressions.size();
	}

	public int getColumnSize(int row) {
		int size = expressions.size();
		if (row > size) {
			row = size;
		}
		if (row < 0) {
			row = 0;
		}
		return expressions.get(row).size();
	}

	public ChartExpression getChartExpression(int row, int column) {
		int rowSize = expressions.size();
		if (row > rowSize) {
			row = rowSize;
		}
		if (row < 0) {
			row = 0;
		}

		int columnSize = getColumnSize(row);
		if (column > columnSize) {
			column = columnSize;
		}
		if (column < 0) {
			column = 0;
		}
		return expressions.get(row).get(column);
	}

	public void removeColumn(int row, int column) {
		int rowSize = expressions.size();
		if (row > rowSize) {
			row = rowSize;
		}
		if (row < 0) {
			row = 0;
		}

		int columnSize = getColumnSize(row);
		if (column > columnSize) {
			column = columnSize;
		}
		if (column < 0) {
			column = 0;
		}
		expressions.get(row).remove(column);
		if (column == 0) {
			expressions.remove(row);
		}
	}

}
