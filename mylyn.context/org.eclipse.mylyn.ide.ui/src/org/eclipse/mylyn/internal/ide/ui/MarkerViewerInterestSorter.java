/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.InterestComparator;
import org.eclipse.ui.views.markers.internal.ProblemMarker;

/**
 * @author Mik Kersten
 */
public class MarkerViewerInterestSorter extends ViewerSorter {

	protected InterestComparator<IInteractionElement> interestComparator = new InterestComparator<IInteractionElement>();

	@Override
	public int compare(Viewer viewer, Object obj1, Object obj2) {
		if (obj1 instanceof ProblemMarker && obj2 instanceof ProblemMarker) {
			ProblemMarker marker1 = (ProblemMarker) obj1;
			ProblemMarker marker2 = (ProblemMarker) obj2;
			if (marker1.getSeverity() == IMarker.SEVERITY_ERROR && marker2.getSeverity() < IMarker.SEVERITY_ERROR) {
				return -1;
			} else if (marker2.getSeverity() == IMarker.SEVERITY_ERROR
					&& marker1.getSeverity() < IMarker.SEVERITY_ERROR) {
				return 1;
			} else {
				if (ContextCore.getContextManager().isContextActive()) {
					AbstractContextStructureBridge bridge = ContextCore
							.getStructureBridge(marker1.getResource().getFileExtension());
					IInteractionElement node1 = ContextCore.getContextManager()
							.getElement(bridge.getHandleForOffsetInObject(marker1, 0));
					IInteractionElement node2 = ContextCore.getContextManager()
							.getElement(bridge.getHandleForOffsetInObject(marker2, 0));
					return interestComparator.compare(node1, node2);
				}
			}
		}
		return super.compare(viewer, obj1, obj2);
	}
}
