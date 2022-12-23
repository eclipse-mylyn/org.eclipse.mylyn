/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.context;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.ITasksCoreConstants;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.context.AbstractTaskContextStore;

/**
 * @author Steffen Pingel
 */
public class DefaultTaskContextStore extends AbstractTaskContextStore {

	public static final String CONTEXT_FILENAME_ENCODING = "UTF-8"; //$NON-NLS-1$

	public static final String CONTEXT_FILE_EXTENSION = ".xml.zip"; //$NON-NLS-1$

	private File directory;

	private File contextDirectory;

	@Override
	public IAdaptable copyContext(ITask sourceTask, ITask destinationTask) {
		return null;
	}

	@Override
	public void clearContext(ITask task) {
		File file = getFileForContext(task);
		if (file.exists()) {
			file.delete();
		}
	}

	@Override
	public void deleteContext(ITask task) {
		File file = getFileForContext(task);
		if (file.exists()) {
			file.delete();
		}
	}

	@Override
	public File getFileForContext(ITask task) {
		String handleIdentifier = task.getHandleIdentifier();
		String encoded;
		try {
			encoded = URLEncoder.encode(handleIdentifier, CONTEXT_FILENAME_ENCODING);
			File contextDirectory = getContextDirectory();
			File contextFile = new File(contextDirectory, encoded + CONTEXT_FILE_EXTENSION);
			return contextFile;
		} catch (UnsupportedEncodingException e) {
			StatusHandler.log(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
					"Could not determine path for context", e)); //$NON-NLS-1$
		}
		return null;
	}

	@Override
	public boolean hasContext(ITask task) {
		File file = getFileForContext(task);
		return file.exists();
	}

	@Override
	public void mergeContext(ITask sourceTask, ITask targetTask) {
		// ignore		
	}

	@Override
	public IAdaptable moveContext(ITask sourceTask, ITask destinationTask) {
		// ignore
		return null;
	}

	@Override
	public void refactorRepositoryUrl(TaskRepository repository, String oldRepositoryUrl, String newRepositoryUrl) {
		// ignore
	}

	@Override
	public void saveActiveContext() {
		// ignore
	}

	@Override
	public synchronized void setDirectory(File directory) {
		this.directory = directory;

		contextDirectory = new File(directory.getParent(), ITasksCoreConstants.CONTEXTS_DIRECTORY);
		if (!contextDirectory.exists()) {
			contextDirectory.mkdirs();
		}
	}

	public File getDirectory() {
		return directory;
	}

	private File getContextDirectory() {
		return contextDirectory;
	}

}
