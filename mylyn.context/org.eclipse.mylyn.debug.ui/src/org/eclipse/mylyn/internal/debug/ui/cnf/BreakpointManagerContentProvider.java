/*******************************************************************************
 * Copyright (c) 2012, 2014 Sebastian Schmidt and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Sebastian Schmidt - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.debug.ui.cnf;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * @author Sebastian Schmidt
 */
public class BreakpointManagerContentProvider implements ITreeContentProvider {

	private final IBreakpointManager breakpointManager;

	public BreakpointManagerContentProvider() {
		breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof IWorkspaceRoot) {
			return new Object[] { breakpointManager };
		} else if (parentElement.equals(breakpointManager)) {
			return breakpointManager.getBreakpoints();
		}
		return new Object[0];
	}

	public Object getParent(Object element) {
		return null;
	}

	public boolean hasChildren(Object element) {
		if (element.equals(breakpointManager) && breakpointManager.getBreakpoints().length > 0) {
			return true;
		}

		return false;
	}
}
