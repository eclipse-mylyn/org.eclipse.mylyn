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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.debug.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;

/**
 * Saves and restores breakpoints state using a designated file.
 * 
 * @author Sebastian Schmidt
 */
public class BreakpointsStateUtil {

	public static final String STATE_FILE = "storedBreakpoints.xml"; //$NON-NLS-1$

	private final IPath pluginStateDir;

	public BreakpointsStateUtil(IPath pluginStateDir) {
		this.pluginStateDir = pluginStateDir;
	}

	public void saveState() {
		IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints();
		InputStream exportedBreakpoints = BreakpointsContextUtil.exportBreakpoints(Arrays.asList(breakpoints),
				new NullProgressMonitor());
		File stateFile = resetStateFile();
		if (exportedBreakpoints != null) {
			saveStateFile(stateFile, exportedBreakpoints);
		}
	}

	public void restoreState() {
		File stateFile = getStateFile();
		try {
			BreakpointsContextUtil.importBreakpoints(new FileInputStream(stateFile), new NullProgressMonitor());
		} catch (FileNotFoundException e) {
			// ok, nothing to restore than.
		}
	}

	private File resetStateFile() {
		File stateFile = getStateFile();
		stateFile.getParentFile().mkdirs();
		if (stateFile.exists()) {
			stateFile.delete();
		}
		return stateFile;
	}

	private File getStateFile() {
		return pluginStateDir.append(BreakpointsStateUtil.STATE_FILE).toFile();
	}

	private void saveStateFile(File stateFile, InputStream exportedBreakpoints) {
		if (!stateFile.exists()) {
			try {
				stateFile.createNewFile();
			} catch (IOException e) {
				return;
			}
		}
		try (FileOutputStream output = new FileOutputStream(stateFile)) {
			IOUtils.copy(exportedBreakpoints, output);
		} catch (FileNotFoundException e) {
			return;
		} catch (IOException e) {
			// FIXME
		}
	}
}
