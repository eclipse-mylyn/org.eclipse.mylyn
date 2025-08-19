/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.core;

import java.io.File;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.mylyn.internal.tasks.core.externalization.AbstractExternalizationParticipant;
import org.eclipse.mylyn.internal.tasks.core.externalization.ExternalizationManager;
import org.eclipse.mylyn.tasks.core.IRepositoryListener;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Rob Elves
 * @since 3.0
 */
public class RepositoryExternalizationParticipant extends AbstractExternalizationParticipant
		implements IRepositoryListener {

	private static final String DESCRIPTION = Messages.RepositoryExternalizationParticipant_Task_Repositories;

	private final TaskRepositoryManager repositoryManager;

	private final ExternalizationManager externalizationManager;

	private boolean dirty = false;

	public RepositoryExternalizationParticipant(ExternalizationManager exManager, TaskRepositoryManager manager) {
		repositoryManager = manager;
		externalizationManager = exManager;
		repositoryManager.addListener(this);
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
	public void load(File sourceFile, IProgressMonitor monitor) throws CoreException {
		repositoryManager.readRepositories(sourceFile.getAbsolutePath());
	}

	@Override
	public void save(File targetFile, IProgressMonitor monitor) throws CoreException {
		synchronized (RepositoryExternalizationParticipant.this) {
			dirty = false;
		}
		repositoryManager.saveRepositories(targetFile.getAbsolutePath());
	}

	@Override
	public String getFileName() {
		return TaskRepositoryManager.DEFAULT_REPOSITORIES_FILE;
	}

	@Override
	public void repositoryUrlChanged(TaskRepository repository, String oldUrl) {
		requestSave();
	}

	public void repositoriesRead() {
		// ignore
	}

	@Override
	public void repositoryAdded(TaskRepository repository) {
		requestSave();
	}

	@Override
	public void repositoryRemoved(TaskRepository repository) {
		requestSave();
	}

	@Override
	public void repositorySettingsChanged(TaskRepository repository) {
		requestSave();
	}

}
