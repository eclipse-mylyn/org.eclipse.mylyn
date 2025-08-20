/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.editors;

/**
 * A layout hint for attribute editors.
 *
 * @author Steffen Pingel
 * @since 3.0
 */
public class LayoutHint {

	/**
	 * @since 3.0
	 */
	public final static int DEFAULT_PRIORITY = 10;

	/**
	 * @since 3.0
	 */
	public enum ColumnSpan {
		MULTIPLE, SINGLE
	}

	/**
	 * @since 3.0
	 */
	public enum RowSpan {
		MULTIPLE, SINGLE
	}

	/**
	 * @since 3.0
	 */
	public RowSpan rowSpan;

	/**
	 * @since 3.0
	 */
	public ColumnSpan columnSpan;

	/**
	 * @since 3.0
	 */
	public LayoutHint(RowSpan rowHint, ColumnSpan columnHint) {
		rowSpan = rowHint;
		columnSpan = columnHint;
	}

	/**
	 * Clones <code>source</code>. Constructs a layout hint with a priority of {@link #DEFAULT_PRIORITY}, if <code>source</code> is null.
	 *
	 * @param source
	 *            the layout hint to clone or <code>null</code>
	 * @since 3.1
	 */
	public LayoutHint(LayoutHint source) {
		if (source != null) {
			rowSpan = source.rowSpan;
			columnSpan = source.columnSpan;
		}
	}

	/**
	 * Constructs a layout hint with a priority of {@link #DEFAULT_PRIORITY}.
	 *
	 * @since 3.1
	 */
	public LayoutHint() {
	}

	/**
	 * Returns a priority based on the size of the layout hint. The bigger the size the bigger the returned priority.
	 * <p>
	 * The priority is used to layout attribute editors.
	 *
	 * @since 3.0
	 */
	public int getPriority() {
		if (columnSpan == null || columnSpan == ColumnSpan.SINGLE) {
			if (rowSpan == null || rowSpan == RowSpan.SINGLE) {
				return DEFAULT_PRIORITY;
			} else {
				return DEFAULT_PRIORITY * 2;
			}
		} else if (rowSpan == null || rowSpan == RowSpan.SINGLE) {
			return DEFAULT_PRIORITY * 3;
		} else {
			return DEFAULT_PRIORITY * 4;
		}
	}

}
