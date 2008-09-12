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

package org.eclipse.mylyn.internal.tasks.core.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 */
public class TaskDataStore {

	private static final String FILE_NAME_INTERNAL = "data.xml";

	private final TaskDataExternalizer externalizer;

	public TaskDataStore(IRepositoryManager taskRepositoryManager) {
		this.externalizer = new TaskDataExternalizer(taskRepositoryManager);
	}

	public synchronized TaskDataState discardEdits(File file) throws CoreException {
		TaskDataState state = readState(file);
		if (state != null) {
			state.setEditsData(null);
		}
		writeState(file, state);
		return state;
	}

	public synchronized TaskDataState getTaskDataState(File file) throws CoreException {
		return readState(file);
	}

	public synchronized void putEdits(File file, TaskData data) throws CoreException {
		Assert.isNotNull(file);
		Assert.isNotNull(data);
		TaskDataState state = readState(file);
		if (state == null) {
			state = new TaskDataState(data.getConnectorKind(), data.getRepositoryUrl(), data.getTaskId());
		}
		state.setEditsData(data);
		writeState(file, state);
	}

	public synchronized void putTaskData(File file, TaskData data, boolean setLastRead, boolean user)
			throws CoreException {
		Assert.isNotNull(file);
		Assert.isNotNull(data);
		TaskDataState state = null;
		try {
			state = readState(file);
		} catch (CoreException e) {
			if (!user) {
				throw new CoreException(
						new Status(
								IStatus.ERROR,
								ITasksCoreConstants.ID_PLUGIN,
								"Reading of existing task data failed. Forcing synchronization will override outgoing changes.",
								e));
			}
		}
		if (state == null) {
			state = new TaskDataState(data.getConnectorKind(), data.getRepositoryUrl(), data.getTaskId());
		}
		if (setLastRead) {
			state.setLastReadData(state.getRepositoryData());
		}
		state.setRepositoryData(data);
		writeState(file, state);
	}

	public synchronized void setTaskData(File file, TaskData data) throws CoreException {
		Assert.isNotNull(file);
		Assert.isNotNull(data);

		// TODO consider reading old task data and compare submitted results to check if all outgoing changes were accepted by repository

		TaskDataState state = new TaskDataState(data.getConnectorKind(), data.getRepositoryUrl(), data.getTaskId());
		state.setRepositoryData(data);
		state.setEditsData(null);
		state.setLastReadData(data);
		writeState(file, state);
	}

	private TaskDataState readState(File file) throws CoreException {
		try {
			if (file.exists()) {
				ZipInputStream in = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
				try {
					in.getNextEntry();
					return externalizer.readState(in);
				} finally {
					in.close();
				}
			}
			return null;
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Error reading task data",
					e));
		}
	}

	private void writeState(File file, TaskDataState state) throws CoreException {
		try {
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			try {
				out.setMethod(ZipOutputStream.DEFLATED);

				ZipEntry entry = new ZipEntry(FILE_NAME_INTERNAL);
				out.putNextEntry(entry);

				externalizer.writeState(out, state);
			} finally {
				out.close();
			}
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Error writing task data",
					e));
		}
	}

//	public synchronized void putLastRead(File file, TaskData data) throws CoreException {
//		Assert.isNotNull(file);
//		Assert.isNotNull(data);
//
//		TaskDataState state = readState(file);
//		if (state == null) {
//			state = new TaskDataState(data.getConnectorKind(), data.getRepositoryUrl(), data.getTaskId());
//		}
//		state.setLastReadData(data);
//		writeState(file, state);
//	}

	public synchronized void putTaskData(File file, TaskDataState state) throws CoreException {
		writeState(file, state);
	}

	public synchronized boolean deleteTaskData(File file) {
		return file.delete();
	}

}
