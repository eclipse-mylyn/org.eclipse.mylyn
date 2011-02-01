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
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataExternalizer.Xml11InputStream;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.xml.sax.SAXException;

/**
 * @author Steffen Pingel
 */
public class TaskDataStore {

	private static final String FILE_NAME_INTERNAL = "data.xml"; //$NON-NLS-1$

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
								"Reading of existing task data failed. Forcing synchronization will override outgoing changes.", //$NON-NLS-1$
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

	private TaskDataState readStateInternal(File file, boolean xml11) throws IOException, SAXException {
		ZipInputStream in = new ZipInputStream(new BufferedInputStream(new FileInputStream(file)));
		try {
			in.getNextEntry();
			// bug 268456: When TaskData that contains C0 control characters is written to disk using XML 1.0 reading it back 
			// in fails with a SAXException. The XML 1.1 standard allows C0 entities but fails if C1 entities. If C0 control 
			// characters are detected while parsing file as XML 1.0 a second attempt is made using XML 1.1. If the file contains 
			// C0 and C1 control characters reading will fail regardless.
			if (xml11) {
				return externalizer.readState(new Xml11InputStream(in));
			} else {
				return externalizer.readState(in);
			}
		} finally {
			in.close();
		}
	}

	private TaskDataState readState(File file) throws CoreException {
		try {
			if (file.exists()) {
				try {
					try {
						return readStateInternal(file, false);
					} catch (SAXException e) {
						// bug 268456: if reading fails, try again using a different XML version
						if (e.getMessage() != null && e.getMessage().contains("invalid XML character")) { //$NON-NLS-1$
							return readStateInternal(file, true);
						} else {
							throw e;
						}
					}

				} catch (SAXException e) {
					throw new IOException("Error parsing task data: " + e.getMessage()); //$NON-NLS-1$
				}
			}
			return null;
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Error reading task data", //$NON-NLS-1$
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
			throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN, "Error writing task data", //$NON-NLS-1$
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
