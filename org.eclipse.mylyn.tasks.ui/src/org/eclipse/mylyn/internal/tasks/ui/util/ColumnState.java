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

package org.eclipse.mylyn.internal.tasks.ui.util;

import java.io.Serializable;

import org.eclipse.swt.SWT;
import org.eclipse.ui.IMemento;

public class ColumnState implements Serializable {

	private static final long serialVersionUID = 6303376618126218826L;

	private String name;

	private int widths;

	private int alignment;

	public ColumnState(String name, int widths) {
		super();
		this.name = name;
		this.widths = widths;
		alignment = SWT.LEFT;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getWidths() {
		return widths;
	}

	public void setWidths(int widths) {
		this.widths = widths;
	}

	public int getAlignment() {
		return alignment;
	}

	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}

	public void saveState(IMemento memento) {
		IMemento child = memento.createChild("ColumnState"); //$NON-NLS-1$
		child.putString("name", name); //$NON-NLS-1$
		child.putInteger("widths", widths); //$NON-NLS-1$
		child.putInteger("alignment", alignment); //$NON-NLS-1$

	}

	public static ColumnState createState(IMemento memento) {
		ColumnState erg;
		erg = new ColumnState(memento.getString("name"), memento.getInteger("widths"));
		erg.setAlignment(memento.getInteger("alignment"));
		return erg;
	}
}
