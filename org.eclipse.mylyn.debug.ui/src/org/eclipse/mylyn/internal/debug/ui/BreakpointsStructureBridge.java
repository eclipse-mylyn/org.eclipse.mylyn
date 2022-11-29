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

package org.eclipse.mylyn.internal.debug.ui;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.osgi.util.NLS;

/**
 * @author Sebastian Schmidt
 */
public class BreakpointsStructureBridge extends AbstractContextStructureBridge {

	private static final String ATTRIBUTE_ID_DEFAULT = "breakpoint[unknown]"; //$NON-NLS-1$

	static final String ATTRIBUTE_ID = "org.eclipse.mylyn.debug.ui.breakpointId"; //$NON-NLS-1$

	public static final String HANDLE_DEFAULT_BREAKPOINT_MANAGER = "breakpointmanager[default]"; //$NON-NLS-1$

	private final IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();

	@Override
	public String getContentType() {
		return DebugUiPlugin.CONTENT_TYPE;
	}

	@Override
	public String getHandleIdentifier(Object object) {
		if (object instanceof IBreakpoint) {
			IBreakpoint breakpoint = (IBreakpoint) object;
			updateBreakpointId((IBreakpoint) object);
			return breakpoint.getMarker().getAttribute(ATTRIBUTE_ID, ATTRIBUTE_ID_DEFAULT);
		} else if (object instanceof IBreakpointManager) {
			return HANDLE_DEFAULT_BREAKPOINT_MANAGER;
		}

		return null;
	}

	private void updateBreakpointId(IBreakpoint object) {
		if (object.getMarker().getAttribute(ATTRIBUTE_ID, null) == null) {
			try {
				// FIXME: there are better *unique* random number generators than Math.random...
				object.getMarker().setAttribute(ATTRIBUTE_ID, "breakpoint[" + (Math.random() * 10000) + "]"); //$NON-NLS-1$ //$NON-NLS-2$
			} catch (CoreException e) {
				IResource resource = object.getMarker().getResource();
				StatusHandler.log(new Status(IStatus.WARNING, DebugUiPlugin.ID_PLUGIN,
						NLS.bind("Breakpoint could not be updated for resource {0} ", resource.getFullPath()))); //$NON-NLS-1$
			}
		}
	}

	@Override
	public String getParentHandle(String handle) {
		Object object = getObjectForHandle(handle);
		if (object != null && object instanceof IBreakpoint) {
			return HANDLE_DEFAULT_BREAKPOINT_MANAGER;
		}
		return null;
	}

	@Override
	public Object getObjectForHandle(String handle) {
		if (handle.equals(HANDLE_DEFAULT_BREAKPOINT_MANAGER)) {
			return breakpointManager;
		}

		for (IBreakpoint breakpoint : breakpointManager.getBreakpoints()) {
			if (breakpoint.getMarker().getAttribute(ATTRIBUTE_ID, ATTRIBUTE_ID_DEFAULT).equals(handle)) {
				return breakpoint;
			}
		}
		return null;
	}

	@Override
	public List<String> getChildHandles(String handle) {
		return Collections.emptyList();
	}

	@Override
	public String getLabel(Object object) {
		return object.toString();
	}

	@Override
	public boolean canBeLandmark(String handle) {
		return true;
	}

	@Override
	public boolean acceptsObject(Object object) {
		return object instanceof IBreakpointManager || object instanceof IBreakpoint;
	}

	@Override
	public boolean canFilter(Object element) {
		return true;
	}

	@Override
	public boolean isDocument(String handle) {
		return false;
	}

	@Override
	public String getHandleForOffsetInObject(Object resource, int offset) {
		return null;
	}

	@Override
	public String getContentType(String elementHandle) {
		Object object = getObjectForHandle(elementHandle);
		if (object != null) {
			return DebugUiPlugin.CONTENT_TYPE;
		}
		return null;
	}
}
