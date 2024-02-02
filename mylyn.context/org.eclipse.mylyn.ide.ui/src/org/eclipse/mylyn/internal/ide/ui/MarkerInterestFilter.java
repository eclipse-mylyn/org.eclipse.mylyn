/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ui;

import java.lang.reflect.Method;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.ide.ui.AbstractMarkerInterestFilter;
import org.eclipse.ui.views.markers.MarkerItem;

/**
 * @author Mik Kersten
 */
public class MarkerInterestFilter extends AbstractMarkerInterestFilter {

	private Method markerCategoryMethod = null;

	@Override
	public boolean select(Viewer viewer, Object parent, Object element) {

		if (element instanceof MarkerItem) {
			if (element.getClass().getSimpleName().equals("MarkerCategory")) { //$NON-NLS-1$
				try {
					if (markerCategoryMethod == null) {
						Class<?> markerCategoryClass = Class
								.forName("org.eclipse.ui.internal.views.markers.MarkerCategory"); //$NON-NLS-1$
						markerCategoryMethod = markerCategoryClass.getDeclaredMethod("getChildren"); //$NON-NLS-1$
						markerCategoryMethod.setAccessible(true);
					}

					Object[] entries = (Object[]) markerCategoryMethod.invoke(element);
					if (entries != null && entries.length == 0) {
						return false;
					} else if (entries != null && entries.length != 0) {
						// PERFORMANCE: need to look down children, so O(n^2) complexity
						for (Object markerEntry : entries) {
							if (markerEntry.getClass().getSimpleName().equals("MarkerEntry") //$NON-NLS-1$
									&& isInteresting(((MarkerItem) markerEntry).getMarker(), viewer, parent)) {
								return true;
							}
						}
						return false;
					}
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, IdeUiBridgePlugin.ID_PLUGIN,
							"Could not access marker view elements.")); //$NON-NLS-1$
				}

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
