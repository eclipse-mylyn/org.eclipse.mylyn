/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

public class LayoutHint {

	public final static int DEFAULT_PRIORITY = 10;

	public enum ColumnSpan {
		MULTIPLE, SINGLE
	};

	public enum RowSpan {
		MULTIPLE, SINGLE
	};

	public RowSpan rowSpan;

	public ColumnSpan columnSpan;

	public LayoutHint(RowSpan rowHint, ColumnSpan columnHint) {
		this.rowSpan = rowHint;
		this.columnSpan = columnHint;
	}

	public int getPriority() {
		if (columnSpan == null || columnSpan == ColumnSpan.SINGLE) {
			if (rowSpan == null || rowSpan == RowSpan.SINGLE) {
				return DEFAULT_PRIORITY;
			} else {
				return DEFAULT_PRIORITY * 2;
			}
		} else {
			if (rowSpan == null || rowSpan == RowSpan.SINGLE) {
				return DEFAULT_PRIORITY * 3;
			} else {
				return DEFAULT_PRIORITY * 4;
			}
		}
	}

}
