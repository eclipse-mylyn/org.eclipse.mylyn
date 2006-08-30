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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.IOfflineTaskHandler;
import org.eclipse.mylar.tasks.core.IQueryHitCollector;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.TaskRepository;

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
	public IAttachmentHandler getAttachmentHandler() {
		// ignore
		return null;
	}

	@Override
	public String getLabel() {
		return "Mock Repository (for unit tests)";
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
	public List<String> getSupportedVersions() {
		// ignore
		return null;
	}
	
	@Override
	public void updateAttributes(TaskRepository repository, IProgressMonitor monitor) {
		// ignore
	}

	@Override
	public boolean validate(TaskRepository repository) {
		return true;
	}

	@Override
	public void updateTaskState(AbstractRepositoryTask repositoryTask) {
		// ignore
	}

	@Override
	public IStatus performQuery(AbstractRepositoryQuery query, IProgressMonitor monitor, IQueryHitCollector resultCollector) {
		return null;
	}

}
