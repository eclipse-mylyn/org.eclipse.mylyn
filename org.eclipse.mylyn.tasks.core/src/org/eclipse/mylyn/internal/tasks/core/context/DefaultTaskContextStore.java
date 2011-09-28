/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
import org.eclipse.mylyn.tasks.core.context.AbstractTaskContextStore;

/**
 * @author Steffen Pingel
 */
public class DefaultTaskContextStore extends AbstractTaskContextStore {

	public static final String CONTEXT_FILENAME_ENCODING = "UTF-8"; //$NON-NLS-1$

	public static final String CONTEXT_FILE_EXTENSION = ".xml.zip"; //$NON-NLS-1$

	private File contextDirectory;

	@Override
	public IAdaptable cloneContext(ITask sourceTask, ITask destinationTask) {
		return null;
	}

	@Override
	public void deleteContext(ITask task) {
		File file = getFileForContext(task);
		if (file.exists()) {
			file.delete();
		}
	}

	public File getContextDirectory() {
		return contextDirectory;
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

	/**
	 * @since 3.7
	 */
	@Override
	public void refactorRepositoryUrl(String oldRepositoryUrl, String newRepositoryUrl) {
		// ignore
	}

	@Override
	public void saveActiveContext() {
		// ignore
	}

	@Override
	public synchronized void setContextDirectory(File directory) {
		this.contextDirectory = directory;
	}

	@Override
	public void mergeContext(ITask sourceTask, ITask targetTask) {
		// ignore		
	}

}
