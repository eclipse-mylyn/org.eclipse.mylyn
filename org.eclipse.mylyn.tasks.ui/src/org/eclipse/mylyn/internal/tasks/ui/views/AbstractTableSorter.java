/*******************************************************************************
 * Copyright (c) 2010 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import java.io.Serializable;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.ui.IMemento;

public abstract class AbstractTableSorter extends ViewerSorter implements Serializable {

	private static final long serialVersionUID = 8048730201424836905L;

	protected int propertyIndex;

	protected static final int ASCENDING = 0;

	protected static final int DESCENDING = 1;

	protected int direction = DESCENDING;

	public AbstractTableSorter() {
		this.propertyIndex = 0;
		direction = DESCENDING;
	}

	public void setColumn(int column) {
		if (column == this.propertyIndex) {
			// Same column as last sort; toggle the direction
			direction = 1 - direction;
		} else {
			// New column; do an ascending sort
			this.propertyIndex = column;
			direction = DESCENDING;
		}
	}

	@Override
	public abstract int compare(Viewer viewer, Object e1, Object e2);

	public abstract void setDefault();

	protected int getPropertyIndex() {
		return propertyIndex;
	}

	protected void setPropertyIndex(int propertyIndex) {
		this.propertyIndex = propertyIndex;
	}

	protected int getDirection() {
		return direction;
	}

	protected void setDirection(int direction) {
		this.direction = direction;
	}

	public void saveState(IMemento memento) {
		IMemento child = memento.createChild("AbstractTableSorter"); //$NON-NLS-1$
		child.putInteger("propertyIndex", propertyIndex); //$NON-NLS-1$
		child.putInteger("direction", direction); //$NON-NLS-1$

	}

	public void readState(IMemento memento) {
		IMemento child = memento.getChild("AbstractTableSorter"); //$NON-NLS-1$
		if (child != null) {
			propertyIndex = child.getInteger("propertyIndex"); //$NON-NLS-1$
			direction = child.getInteger("direction"); //$NON-NLS-1$
		} else {
			setDefault();
		}
	}
}
