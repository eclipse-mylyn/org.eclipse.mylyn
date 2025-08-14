/*******************************************************************************
 * Copyright (c) 2012, 2013 Sebastian Schmidt and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Sebastian Schmidt - initial API and implementation
 *     See git history
 *******************************************************************************/
package org.eclipse.mylyn.internal.debug.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.ui.actions.ExportBreakpointsOperation;
import org.eclipse.debug.ui.actions.ImportBreakpointsOperation;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;

/**
 * @author Sebastian Schmidt
 */
public class BreakpointsContextUtil {

	public static InputStream exportBreakpoints(Collection<IBreakpoint> breakpoints, IProgressMonitor progressMonitor) {
		if (breakpoints.size() == 0) {
			return null;
		}

		ExportBreakpointsOperation exportBreakpointOperation = new ExportBreakpointsOperation(
				breakpoints.toArray(new IBreakpoint[0]));
		try {
			exportBreakpointOperation.run(progressMonitor);
			return new ByteArrayInputStream(exportBreakpointOperation.getBuffer().toString().getBytes("UTF-8")); //$NON-NLS-1$
		} catch (Exception e) {
			StatusHandler.log(
					new Status(IStatus.WARNING, DebugUiPlugin.ID_PLUGIN, "Could not export context breakpoints", e));//$NON-NLS-1$
		}
		return null;
	}

	public static List<IBreakpoint> importBreakpoints(IInteractionContext context, IProgressMonitor progressMonitor) {
		InputStream stream = ContextCore.getContextManager()
				.getAdditionalContextData(context, DebugUiPlugin.CONTRIBUTOR_ID);
		if (stream == null) {
			return new ArrayList<>();
		}
		return importBreakpoints(stream, progressMonitor);
	}

	public static List<IBreakpoint> importBreakpoints(InputStream stream, IProgressMonitor progressMonitor) {
		try (Scanner scanner = new Scanner(stream)) {
			scanner.useDelimiter("\\A"); //$NON-NLS-1$
			String breakpoints = scanner.next();

			ImportBreakpointsOperation importBreakpointOperation = new ImportBreakpointsOperation(
					new StringBuffer(breakpoints), true, false);
			try {
				importBreakpointOperation.run(progressMonitor);
				return new ArrayList<>(Arrays.asList(importBreakpointOperation.getImportedBreakpoints()));
			} catch (InvocationTargetException e) {
				StatusHandler.log(
						new Status(IStatus.WARNING, DebugUiPlugin.ID_PLUGIN, "Could not import context breakpoints", e));//$NON-NLS-1$
			}
			return new ArrayList<>();
		}
	}

	public static void removeBreakpoints(Collection<IBreakpoint> breakpoints) {
		try {
			DebugPlugin.getDefault()
			.getBreakpointManager()
			.removeBreakpoints(breakpoints.toArray(new IBreakpoint[0]), true);
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.WARNING, DebugUiPlugin.ID_PLUGIN,
					"Could not remove obsolete breakpoints from workspace", e)); //$NON-NLS-1$
		}
	}
}
