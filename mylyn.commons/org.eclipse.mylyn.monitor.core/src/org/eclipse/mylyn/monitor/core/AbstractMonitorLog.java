/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.monitor.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.monitor.core.IMonitorCoreConstants;

/**
 * Used for logging interaction events.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public abstract class AbstractMonitorLog {

	protected File outputFile;

	protected FileOutputStream outputStream;

	protected boolean started = false;

	public AbstractMonitorLog() {
	}

	public void startMonitoring() {
		synchronized (this) {
			if (started) {
				return;
			} else {
				started = true;
			}
		}
		try {
			if (!outputFile.exists()) {
				outputFile.createNewFile();
			}
			outputStream = new FileOutputStream(outputFile, true);
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, IMonitorCoreConstants.ID_PLUGIN, "Could not log to file: " //$NON-NLS-1$
					+ outputFile.getAbsolutePath(), e));
		}
	}

	public void stopMonitoring() {
		try {
			if (outputStream != null) {
				outputStream.flush();
				outputStream.close();
			}
			started = false;
		} catch (IOException e) {
			StatusHandler.log(new Status(IStatus.ERROR, IMonitorCoreConstants.ID_PLUGIN,
					"Could not close interaction event stream", e)); //$NON-NLS-1$
		}
	}

	public File moveOutputFile(String newPath) {
		stopMonitoring();
		File newFile = new File(newPath);
		try {
			if (outputFile.exists() && !newFile.exists()) {
				outputFile.renameTo(newFile);
			} else if (!newFile.exists()) {
				newFile.createNewFile();
				outputFile.delete();
			} else {
				outputFile.delete();
			}
			outputFile = newFile;
		} catch (Exception e) {
			StatusHandler.log(
					new Status(IStatus.ERROR, IMonitorCoreConstants.ID_PLUGIN, "Could not set logger output file", e)); //$NON-NLS-1$
		}
		startMonitoring();
		return newFile;
	}

	public File getOutputFile() {
		return outputFile;
	}

}
