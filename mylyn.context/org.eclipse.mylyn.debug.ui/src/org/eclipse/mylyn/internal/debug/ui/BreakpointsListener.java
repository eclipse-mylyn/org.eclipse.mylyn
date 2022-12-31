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

import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.debug.core.IBreakpointsListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Sebastian Schmidt
 */
public class BreakpointsListener implements IBreakpointsListener {

	private final String ORIGIN_ID = "org.eclipse.debug.ui"; //$NON-NLS-1$

	private final AbstractContextStructureBridge structureBridge = ContextCore
			.getStructureBridge(DebugUiPlugin.CONTENT_TYPE);

	public void breakpointsAdded(IBreakpoint[] breakpoints) {
		breakpointsChanged(breakpoints, new IMarkerDelta[breakpoints.length]);
	}

	public void breakpointsRemoved(IBreakpoint[] breakpoints, IMarkerDelta[] deltas) {
		for (IBreakpoint breakpoint : breakpoints) {
			IInteractionElement element = ContextCore.getContextManager()
					.getElement(structureBridge.getHandleIdentifier(breakpoint));
			if (element != null) {
				ContextCore.getContextManager().deleteElement(element);
			}
		}
	}

	public void breakpointsChanged(IBreakpoint[] breakpoints, IMarkerDelta[] deltas) {
		for (int i = 0; i < breakpoints.length; i++) {
			IBreakpoint breakpoint = breakpoints[i];
			IMarkerDelta delta = deltas[i];
			if (delta != null
					&& breakpoint.getMarker().getAttribute(BreakpointsStructureBridge.ATTRIBUTE_ID, null) != null) {
				if (delta.getAttribute(BreakpointsStructureBridge.ATTRIBUTE_ID, null) == null) {
					// generating an unique id for this breakpoint shouldn't increase interest.
					continue;
				}
			}

			InteractionEvent editEvent = new InteractionEvent(InteractionEvent.Kind.EDIT, DebugUiPlugin.CONTENT_TYPE,
					structureBridge.getHandleIdentifier(breakpoint), ORIGIN_ID);
			ContextCore.getContextManager().processInteractionEvent(editEvent);
		}
	}
}
