/*******************************************************************************
 * Copyright (c) 2016 Frank Becker and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui;

import java.util.Objects;

/**
 * @since 3.22
 */
public class TableColumnDescriptor {

	public static final String TABLE_COLUMN_DESCRIPTOR_KEY = "org.eclipse.mylyn.commons.ui.TableColumnDescriptors"; //$NON-NLS-1$

	private int width;

	private final String name;

	private int alignment;

	private boolean defaultSortColumn;

	private int sortDirection;

	private boolean autoSize;

	public TableColumnDescriptor(int width, String name, int alignment, boolean defaultSortColumn, int sortDirection,
			boolean autoSize) {
		this.width = width;
		this.name = name;
		this.alignment = alignment;
		this.defaultSortColumn = defaultSortColumn;
		this.sortDirection = sortDirection;
		this.autoSize = autoSize;
	}

	public TableColumnDescriptor(TableColumnDescriptor other) {
		width = other.width;
		name = other.name;
		alignment = other.alignment;
		defaultSortColumn = other.defaultSortColumn;
		sortDirection = other.sortDirection;
		autoSize = other.autoSize;
	}

	public int getWidth() {
		return width;
	}

	public String getName() {
		return name;
	}

	public int getAlignment() {
		return alignment;
	}

	public boolean isDefaultSortColumn() {
		return defaultSortColumn;
	}

	public int getSortDirection() {
		return sortDirection;
	}

	public boolean isAutoSize() {
		return autoSize;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}

	public void setDefaultSortColumn(boolean defaultSortColumn) {
		this.defaultSortColumn = defaultSortColumn;
	}

	public void setSortDirection(int sortDirection) {
		this.sortDirection = sortDirection;
	}

	public void setAutoSize(boolean autoSize) {
		this.autoSize = autoSize;
	}

	@Override
	public int hashCode() {
		return Objects.hash(alignment, autoSize, defaultSortColumn, name, sortDirection, width);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (getClass() != obj.getClass())) {
			return false;
		}
		TableColumnDescriptor other = (TableColumnDescriptor) obj;
		if (alignment != other.alignment) {
			return false;
		}
		if (autoSize != other.autoSize) {
			return false;
		}
		if (defaultSortColumn != other.defaultSortColumn) {
			return false;
		}
		if (!Objects.equals(name, other.name)) {
			return false;
		}
		if (sortDirection != other.sortDirection) {
			return false;
		}
		if (width != other.width) {
			return false;
		}
		return true;
	}

}
