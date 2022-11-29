/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.ide.ui;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.mylyn.internal.ide.ui.IdeUiBridgePlugin;
import org.eclipse.osgi.util.NLS;

/**
 * @author Mik Kersten
 * @since 3.0
 */
public abstract class AbstractMarkerInterestFilter extends InterestFilter {

	protected boolean isInteresting(IMarker marker, Viewer viewer, Object parent) {
		try {
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
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, IdeUiBridgePlugin.ID_PLUGIN,
					NLS.bind("Unable to get handle for marker: {0}", marker.getResource()), t)); //$NON-NLS-1$
		}
		return false;
	}

	protected abstract boolean isImplicitlyInteresting(IMarker marker);

}
