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

package org.eclipse.mylar.internal.tasklist.ui.wizards;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylar.internal.tasklist.ui.TaskListImages;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Mik Kersten
 */
public class EditRepositoryWizard extends Wizard implements INewWizard {

	private static final String TITLE = "Task Repository Settings";

	private AbstractRepositorySettingsPage abstractRepositorySettingsPage;

	private TaskRepository repository;

	public EditRepositoryWizard(TaskRepository repository) {
		super();
		this.repository = repository;
		AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getKind());
		abstractRepositorySettingsPage = connector.getSettingsPage();
		abstractRepositorySettingsPage.setRepository(repository);
		abstractRepositorySettingsPage.setVersion(repository.getVersion());
		abstractRepositorySettingsPage.setWizard(this);		
		setNeedsProgressMonitor(true);
		setDefaultPageImageDescriptor(TaskListImages.BANNER_REPOSITORY);
		setWindowTitle(TITLE);
	}

	@Override
	public boolean performFinish() {
		if (canFinish()) {
			String oldUrl = repository.getUrl();
			String newUrl = abstractRepositorySettingsPage.getServerUrl();
			MylarTaskListPlugin.getTaskListManager().refactorRepositoryUrl(oldUrl, newUrl);
			
			repository.flushAuthenticationCredentials();
			repository.setUrl(newUrl);
			repository.setVersion(abstractRepositorySettingsPage.getVersion());
			repository.setCharacterEncoding(abstractRepositorySettingsPage.getCharacterEncoding());
//			oldRepository.setTimeZoneId(abstractRepositorySettingsPage.getTimeZoneId());			
			repository.setAuthenticationCredentials(abstractRepositorySettingsPage.getUserName(), abstractRepositorySettingsPage.getPassword());
			
			MylarTaskListPlugin.getRepositoryManager().saveRepositories();
			
			// MylarTaskListPlugin.getRepositoryManager().removeRepository(oldRepository);
			// TaskRepository repository = abstractRepositorySettingsPage.createTaskRepository();
			// MylarTaskListPlugin.getRepositoryManager().addRepository(repository);
			// MylarTaskListPlugin.getRepositoryManager().setSyncTime(repository, oldRepository.getSyncTimeStamp());
			return true;
		}
		return false;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}

	@Override
	public void addPages() {
		addPage(abstractRepositorySettingsPage);
	}

	@Override
	public boolean canFinish() {
		return abstractRepositorySettingsPage.isPageComplete();
	}
	
	/** public for testing */
	public AbstractRepositorySettingsPage getSettingsPage() {
		return abstractRepositorySettingsPage;
	}

	public TaskRepository getRepository() {
		return repository;
	}
}
