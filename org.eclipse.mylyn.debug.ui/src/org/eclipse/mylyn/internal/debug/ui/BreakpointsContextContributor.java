/*******************************************************************************
 * Copyright (c) 2012 Sebastian Schmidt and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Schmidt - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.debug.ui;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.mylyn.context.core.AbstractContextContributor;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextChangeEvent.ContextChangeKind;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.monitor.core.InteractionEvent;

/**
 * @author Sebastian Schmidt
 */
public class BreakpointsContextContributor extends AbstractContextContributor {

	private BreakpointsListener breakpointsListener;

	private final AbstractContextStructureBridge structureBridge = ContextCore.getStructureBridge(DebugUiPlugin.CONTENT_TYPE);

	public InputStream getDataAsStream(IInteractionContext context) {
		List<IBreakpoint> breakpoints = getContextBreakpoints(context);
		if (breakpoints.size() == 0) {
			return null;
		}
		return BreakpointsContextUtil.exportBreakpoints(breakpoints, new NullProgressMonitor());
	}

	public String getIdentifier() {
		return DebugUiPlugin.CONTRIBUTOR_ID;
	}

	public void contextChanged(ContextChangeEvent event) {
		BreakpointsStateUtil stateUtil = new BreakpointsStateUtil(Platform.getStateLocation(DebugUiPlugin.getDefault()
				.getBundle()));
		if (event.getEventKind().equals(ContextChangeKind.PRE_ACTIVATED)) {
			stateUtil.saveState();
			breakpointsListener = new BreakpointsListener();
			DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(breakpointsListener);
			BreakpointsContextUtil.importBreakpoints(event.getContext(), new NullProgressMonitor());
		} else if (event.getEventKind().equals(ContextChangeKind.DEACTIVATED)) {
			DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(breakpointsListener);
			BreakpointsContextUtil.removeBreakpoints(getContextBreakpoints(event.getContext()));
			stateUtil.restoreState();
		}
	}

	private List<IBreakpoint> getContextBreakpoints(IInteractionContext context) {
		List<IBreakpoint> breakpoints = new ArrayList<IBreakpoint>();
		for (InteractionEvent element : context.getInteractionHistory()) {
			Object object = structureBridge.getObjectForHandle(element.getStructureHandle());
			if (object != null && object instanceof IBreakpoint) {
				breakpoints.add((IBreakpoint) object);
			}
		}
		return breakpoints;
	}
}
