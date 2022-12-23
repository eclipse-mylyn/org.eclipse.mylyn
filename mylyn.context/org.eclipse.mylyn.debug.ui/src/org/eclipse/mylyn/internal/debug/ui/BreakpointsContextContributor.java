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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
	public static final String AUTO_MANAGE_BREAKPOINTS = "org.eclipse.mylyn.context.breakpoints.auto.manage"; //$NON-NLS-1$

	public static final String JOB_FAMILY = "org.eclipse.mylyn.debug.job.family"; //$NON-NLS-1$

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

	public void contextChanged(final ContextChangeEvent event) {
		if (!DebugUiPlugin.getDefault().getPreferenceStore().getBoolean(AUTO_MANAGE_BREAKPOINTS)) {
			if (event.getEventKind() == ContextChangeKind.DEACTIVATED && breakpointsListener != null) {
				DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(breakpointsListener);
			}
			return;
		}
		if (event.getEventKind() == ContextChangeKind.PRE_ACTIVATED
				|| event.getEventKind() == ContextChangeKind.DEACTIVATED) {
			Job importJob = new Job("Update Context Breakpoints") { //$NON-NLS-1$
				@Override
				public IStatus run(IProgressMonitor monitor) {
					BreakpointsStateUtil stateUtil = new BreakpointsStateUtil(
							Platform.getStateLocation(DebugUiPlugin.getDefault().getBundle()));
					if (event.getEventKind() == ContextChangeKind.PRE_ACTIVATED) {
						stateUtil.saveState();
						breakpointsListener = new BreakpointsListener();
						DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(breakpointsListener);
						BreakpointsContextUtil.importBreakpoints(event.getContext(), monitor);
					} else if (event.getEventKind() == ContextChangeKind.DEACTIVATED) {
						if (breakpointsListener != null) {
							DebugPlugin.getDefault()
									.getBreakpointManager()
									.removeBreakpointListener(breakpointsListener);
						}
						BreakpointsContextUtil.removeBreakpoints(getContextBreakpoints(event.getContext()));
						stateUtil.restoreState();
					}
					return Status.OK_STATUS;
				}

				@Override
				public boolean belongsTo(Object family) {
					return family.equals(JOB_FAMILY);
				}
			};
			importJob.setSystem(true);
			importJob.setUser(false);
			importJob.schedule();
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
