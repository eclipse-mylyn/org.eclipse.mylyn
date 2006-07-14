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

package org.eclipse.mylar.tasks.tests.mockconnector;

import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.internal.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.tasks.core.AbstractQueryHit;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnector;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class MockRepositoryConnector extends AbstractRepositoryConnector {

	public static final String REPOSITORY_TYPE = "mock";

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
	public Wizard getAddExistingTaskWizard(TaskRepository repository) {
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
		return "Mock Repository (for unit tests)";
	}

	@Override
	public IWizard getNewQueryWizard(TaskRepository repository, IStructuredSelection selection) {
		// ignore
		return null;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository, IStructuredSelection selection) {
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
		return REPOSITORY_TYPE;
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
