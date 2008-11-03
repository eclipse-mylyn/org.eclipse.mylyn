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

package org.eclipse.mylyn.internal.ide.ui;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.ide.ui.AbstractMarkerInterestFilter;
import org.eclipse.ui.views.markers.MarkerItem;

/**
 * @author Mik Kersten
 */
public class MarkerInterestFilter extends AbstractMarkerInterestFilter {

	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {

		if (element instanceof MarkerItem) {
			if (element.getClass().getSimpleName().equals("MarkerCategory")) { //$NON-NLS-1$
//				Class<?> clazz;
//				try {
//					clazz = Class.forName("org.eclipse.ui.internal.views.markers.MarkerCategory");
//					Method method = clazz.getDeclaredMethod("getChildren", new Class[] {});
//					method.setAccessible(true);
//					Object result = method.invoke(element, new Object[] {});
//				} catch (Exception e) {
//					e.printStackTrace();
//				}

				return true;
			} else if (element.getClass().getSimpleName().equals("MarkerEntry")) { //$NON-NLS-1$
				return isInteresting(((MarkerItem) element).getMarker(), viewer, parent);
			}
		}

		return false;
	}

	@Override
	protected boolean isImplicitlyInteresting(IMarker marker) {
		try {
			Object severity = marker.getAttribute(IMarker.SEVERITY);
			return severity != null && severity.equals(IMarker.SEVERITY_ERROR);
		} catch (CoreException e) {
			// ignore
		}
		return false;
	}
}
