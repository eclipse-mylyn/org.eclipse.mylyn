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

package org.eclipse.mylar.tasks.tests.connector;

import java.util.List;

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

	public static final String REPOSITORY_KIND = "mock";

	@Override
	public boolean canCreateNewTask(TaskRepository repository) {
		// ignore
		return false;
	}

	@Override
	public boolean canCreateTaskFromKey(TaskRepository repository) {
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
		return REPOSITORY_KIND;
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

	@Override
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) {
		// ignore
	}

	@Override
	public boolean hasRichEditor() {
		// ignore
		return false;
	}

	@Override
	public boolean validate(TaskRepository repository) {
		return true;
	}

}
