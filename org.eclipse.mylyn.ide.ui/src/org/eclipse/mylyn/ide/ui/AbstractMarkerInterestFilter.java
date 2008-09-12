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

package org.eclipse.mylyn.ide.ui;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.ui.InterestFilter;

/**
 * @author Mik Kersten
 * @since 3.0
 */
public abstract class AbstractMarkerInterestFilter extends InterestFilter {

	protected boolean isInteresting(IMarker marker, Viewer viewer, Object parent) {
		if (isImplicitlyInteresting(marker)) {
			return true;
		} else {
			String handle = ContextCore.getStructureBridge(marker.getResource().getFileExtension())
					.getHandleForOffsetInObject(marker, 0);
			if (handle == null) {
				return false;
			} else {
				return super.select(viewer, parent, ContextCore.getContextManager().getElement(handle));
			}
		}
	}

	protected abstract boolean isImplicitlyInteresting(IMarker marker);

}
