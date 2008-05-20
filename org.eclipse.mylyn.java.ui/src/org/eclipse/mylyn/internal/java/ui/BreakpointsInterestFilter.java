/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui;

import org.eclipse.core.resources.IMarker;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.ide.ui.AbstractMarkerInterestFilter;

/**
 * @author Mik Kersten
 */
public class BreakpointsInterestFilter extends AbstractMarkerInterestFilter {

	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {
//		if (element instanceof IJavaLineBreakpoint) {
//			IJavaLineBreakpoint breakpoint = (IJavaLineBreakpoint)element;
//		}
		if (element instanceof IBreakpoint) {
//			try {
//				System.err.println(">>> " + ((IBreakpoint) element).getMarker().getAttributes().keySet());
//				System.err.println(">>>> " + ((IBreakpoint) element).getMarker().getAttributes().values());
//			} catch (CoreException e) {
//				// XXX
//			}
			return isInteresting(((IBreakpoint) element).getMarker(), viewer, parent);
		}
		return false;
	}

	@Override
	protected boolean isImplicitlyInteresting(IMarker marker) {
		return false;
	}
}