/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.discovery.ui.util;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;

public class SimpleSelectionProvider implements ISelectionProvider {

	private ISelection selection;

	public SimpleSelectionProvider() {
		this(new StructuredSelection());
	}

	public SimpleSelectionProvider(ISelection selection) {
		setSelection(selection);
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		// ignore	
	}

	public ISelection getSelection() {
		return selection;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		// ignore
	}

	public void setSelection(ISelection selection) {
		if (selection == null) {
			selection = new StructuredSelection();
		}
		this.selection = selection;
	}

}
