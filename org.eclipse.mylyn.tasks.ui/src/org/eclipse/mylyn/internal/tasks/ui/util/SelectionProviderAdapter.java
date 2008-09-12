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

package org.eclipse.mylyn.internal.tasks.ui.util;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;

/**
 * @author Steffen Pingel
 */
public class SelectionProviderAdapter implements ISelectionProvider {

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		// ignore
	}

	public ISelection getSelection() {
		// ignore
		return null;
	}

	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		// ignore
	}

	public void setSelection(ISelection selection) {
		// ignore
	}

}
