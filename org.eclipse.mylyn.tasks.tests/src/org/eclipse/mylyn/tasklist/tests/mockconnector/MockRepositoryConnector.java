/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.tests.mockconnector;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.IAttachmentHandler;
import org.eclipse.mylar.provisional.tasklist.IOfflineTaskHandler;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class MockRepositoryConnector extends AbstractRepositoryConnector {

	@Override
	public boolean canCreateNewTask() {
		// ignore
		return false;
	}

	@Override
	public boolean canCreateTaskFromKey() {
		// ignore
		return false;
	}

	@Override
	public ITask createTaskFromExistingKey(TaskRepository repository, String id) {
		// ignore
		return null;
	}

	@Override
	public IWizard getAddExistingTaskWizard(TaskRepository repository) {
		// ignore
		return null;
	}

	@Override
	public IAttachmentHandler getAttachmentHandler() {
		// ignore
		return null;
	}

	@Override
	public Set<AbstractRepositoryTask> getChangedSinceLastSync(TaskRepository repository, Set<AbstractRepositoryTask> tasks) throws Exception {
		// ignore
		return null;
	}

	@Override
	public String getLabel() {
		// ignore
		return null;
	}

	@Override
	public IWizard getNewQueryWizard(TaskRepository repository) {
		// ignore
		return null;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository) {
		// ignore
		return null;
	}

	@Override
	public IOfflineTaskHandler getOfflineTaskHandler() {
		// ignore
		return null;
	}

	@Override
	public String getRepositoryType() {
		// ignore
		return null;
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String url) {
		// ignore
		return null;
	}

	@Override
	public AbstractRepositorySettingsPage getSettingsPage() {
		// ignore
		return null;
	}

	@Override
	public List<String> getSupportedVersions() {
		// ignore
		return null;
	}

	@Override
	public void openEditQueryDialog(AbstractRepositoryQuery query) {
		// ignore
	}

	@Override
	public List<AbstractQueryHit> performQuery(AbstractRepositoryQuery query, IProgressMonitor monitor, MultiStatus queryStatus) {
		return null;
	}

	@Override
	protected void updateTaskState(AbstractRepositoryTask repositoryTask) {
		// ignore
	}


}
