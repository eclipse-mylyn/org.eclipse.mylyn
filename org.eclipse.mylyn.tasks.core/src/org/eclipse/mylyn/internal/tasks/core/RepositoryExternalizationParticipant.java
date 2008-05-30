/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.externalization.AbstractExternalizationParticipant;
import org.eclipse.mylyn.internal.tasks.core.externalization.ExternalizationManager;
import org.eclipse.mylyn.tasks.core.IRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Rob Elves
 * @since 3.0
 */
public class RepositoryExternalizationParticipant extends AbstractExternalizationParticipant implements
		IRepositoryListener {

	private static final String DESCRIPTION = "Task Repositories";

	private final TaskRepositoryManager repositoryManager;

	private final ExternalizationManager externalizationManager;

	private boolean dirty = false;

	public RepositoryExternalizationParticipant(ExternalizationManager exManager, TaskRepositoryManager manager) {
		this.repositoryManager = manager;
		this.externalizationManager = exManager;
		this.repositoryManager.addListener(this);
	}

	@Override
	public String getDescription() {
		return DESCRIPTION;
	}

	@Override
	public ISchedulingRule getSchedulingRule() {
		return ITasksCoreConstants.TASKLIST_SCHEDULING_RULE;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	private void requestSave() {
		synchronized (RepositoryExternalizationParticipant.this) {
			dirty = true;
		}
		externalizationManager.requestSave();
	}

	@Override
	public void load(String rootPath, IProgressMonitor monitor) throws CoreException {
		File repositoriesFile = getFile(rootPath);
		try {
			repositoryManager.readRepositories(repositoriesFile.getAbsolutePath());
		} catch (Exception e) {
			if (restoreSnapshot(repositoriesFile)) {
				repositoryManager.readRepositories(repositoriesFile.getAbsolutePath());
			} else {
				throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
						"Failed to load repositories", e));
			}
		}
	}

	@Override
	public void save(String rootPath, IProgressMonitor monitor) throws CoreException {
		File repositoriesFile = getFile(rootPath);
		if (!takeSnapshot(repositoriesFile)) {
			StatusHandler.fail(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN, "Task List snapshot failed"));
		}
		repositoryManager.saveRepositories(repositoriesFile.getAbsolutePath());
		synchronized (RepositoryExternalizationParticipant.this) {
			dirty = false;
		}
	}

	@Override
	public String getFileName() {
		return TaskRepositoryManager.DEFAULT_REPOSITORIES_FILE;
	}

	public void repositoryUrlChanged(TaskRepository repository, String oldUrl) {
		requestSave();
	}

	public void repositoriesRead() {
		// ignore
	}

	public void repositoryAdded(TaskRepository repository) {
		requestSave();
	}

	public void repositoryRemoved(TaskRepository repository) {
		requestSave();
	}

	public void repositorySettingsChanged(TaskRepository repository) {
		requestSave();
	}

}
