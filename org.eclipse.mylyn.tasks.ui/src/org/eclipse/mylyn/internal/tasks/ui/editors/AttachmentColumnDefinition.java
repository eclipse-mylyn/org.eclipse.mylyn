/*******************************************************************************
 * Copyright (c) 2011 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.swt.graphics.Image;

public abstract class AttachmentColumnDefinition {

	protected <T> int compare(Comparable<T> key1, T key2) {
		if (key1 == null) {
			return (key2 != null) ? 1 : 0;
		} else if (key2 == null) {
			return -1;
		}
		return key1.compareTo(key2);
	}

	private final int index;

	private final int width;

	private final String label;

	private final int alignment;

	private final boolean sortColumn;

	private final int sortDirection;

	public AttachmentColumnDefinition(int index, int width, String label, int alignment, boolean sortColumn,
			int sortDirection) {
		super();
		this.index = index;
		this.width = width;
		this.label = label;
		this.alignment = alignment;
		this.sortColumn = sortColumn;
		this.sortDirection = sortDirection;
	}

	public int getIndex() {
		return index;
	}

	public int getWidth() {
		return width;
	}

	public String getLabel() {
		return label;
	}

	public int getAlignment() {
		return alignment;
	}

	public boolean isSortColumn() {
		return sortColumn;
	}

	public int getSortDirection() {
		return sortDirection;
	}

	public Image getColumnImage(ITaskAttachment attachment, int columnIndex) {
		return null;
	}

	public String getColumnText(ITaskAttachment attachment, int columnIndex) {
		return ""; //$NON-NLS-1$
	}

	public abstract int compare(TableViewer viewer, ITaskAttachment attachment1, ITaskAttachment attachment2,
			int columnIndex);

}
