/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.externalization.AbstractExternalizationParticipant;
import org.eclipse.mylyn.internal.tasks.core.externalization.ExternalizationManager;
import org.eclipse.mylyn.internal.tasks.core.externalization.IExternalizationContext;
import org.eclipse.mylyn.tasks.core.ITaskRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Rob Elves
 * @since 3.0
 */
public class RepositoryExternalizationParticipant extends AbstractExternalizationParticipant implements
		ITaskRepositoryListener {

	private final TaskRepositoryManager repositoryManager;

	private boolean dirty = false;

	private final ExternalizationManager externalizationManager;

	public RepositoryExternalizationParticipant(ExternalizationManager exManager, TaskRepositoryManager manager) {
		this.repositoryManager = manager;
		this.externalizationManager = exManager;
		this.repositoryManager.addListener(this);
	}

	@Override
	public void execute(IExternalizationContext context, IProgressMonitor monitor) throws CoreException {
		Assert.isNotNull(context);
		String filePath = context.getRootPath() + File.separator + TaskRepositoryManager.DEFAULT_REPOSITORIES_FILE;

		final File repositoriesFile = new File(filePath);

		if (!repositoriesFile.exists()) {
			try {
				repositoriesFile.createNewFile();
			} catch (IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
						"Task Repositories file not found, error creating new file.", e));
			}
		}

		switch (context.getKind()) {
		case SAVE:
			if (!takeSnapshot(repositoriesFile)) {
				StatusHandler.fail(new Status(IStatus.WARNING, ITasksCoreConstants.ID_PLUGIN,
						"Task List snapshot failed"));
			}
			repositoryManager.saveRepositories(filePath);
			synchronized (RepositoryExternalizationParticipant.this) {
				dirty = false;
			}
			break;
		case LOAD:
			try {
				repositoryManager.readRepositories(filePath);
			} catch (Exception e) {
				if (restoreSnapshot(repositoriesFile)) {
					repositoryManager.readRepositories(filePath);
				} else {
					throw new CoreException(new Status(IStatus.ERROR, ITasksCoreConstants.ID_PLUGIN,
							"Failed to load repositories", e));
				}
			}
			break;
		}

	}

	@Override
	public String getDescription() {
		return "Task Repositories";
	}

	@Override
	public ISchedulingRule getSchedulingRule() {
		return ITasksCoreConstants.TASKLIST_SCHEDULING_RULE;
	}

	@Override
	public boolean isDirty() {
		return dirty;
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

	private void requestSave() {
		synchronized (RepositoryExternalizationParticipant.this) {
			dirty = true;
		}
		externalizationManager.requestSave();
	}
}
