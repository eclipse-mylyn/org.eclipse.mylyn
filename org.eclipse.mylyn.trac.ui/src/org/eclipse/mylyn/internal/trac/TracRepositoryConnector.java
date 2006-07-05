/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.trac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.internal.tasklist.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.ITracClient.Version;
import org.eclipse.mylar.internal.trac.ui.wizard.TracRepositorySettingsPage;
import org.eclipse.mylar.provisional.tasklist.AbstractQueryHit;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryQuery;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.IAttachmentHandler;
import org.eclipse.mylar.provisional.tasklist.IOfflineTaskHandler;
import org.eclipse.mylar.provisional.tasklist.ITask;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * @author Steffen Pingel
 */
public class TracRepositoryConnector extends AbstractRepositoryConnector {

	private final static String CLIENT_LABEL = "Trac (supports 0.9.0 and later)";

	private List<String> supportedVersions;

	@Override
	public boolean canCreateNewTask() {
		return false;
	}

	@Override
	public boolean canCreateTaskFromKey() {
		return false;
	}

	@Override
	public Set<AbstractRepositoryTask> getChangedSinceLastSync(TaskRepository repository,
			Set<AbstractRepositoryTask> tasks) throws Exception {
		return Collections.emptySet();
	}

	@Override
	public String getLabel() {
		return CLIENT_LABEL;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository) {
		return null;
	}

	@Override
	public String getRepositoryType() {
		return MylarTracPlugin.REPOSITORY_KIND;
	}

	@Override
	public String getRepositoryUrlFromTaskUrl(String url) {
		if (url == null) {
			return null;
		}
		int i = url.lastIndexOf(ITracClient.TICKET_URL);
		return (i != -1) ? url.substring(0, i) : null;
	}

	@Override
	public AbstractRepositorySettingsPage getSettingsPage() {
		return new TracRepositorySettingsPage(this);
	}

	@Override
	public List<String> getSupportedVersions() {
		if (supportedVersions == null) {
			supportedVersions = new ArrayList<String>();
			for (Version version : Version.values()) {
				supportedVersions.add(version.toString());
			}
		}
		return supportedVersions;
	}

	@Override
	public IAttachmentHandler getAttachmentHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IOfflineTaskHandler getOfflineTaskHandler() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void updateTaskState(AbstractRepositoryTask repositoryTask) {
		// TODO Auto-generated method stub
	}

	@Override
	public IWizard getAddExistingTaskWizard(TaskRepository repository) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IWizard getNewQueryWizard(TaskRepository repository) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void openEditQueryDialog(AbstractRepositoryQuery query) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<AbstractQueryHit> performQuery(AbstractRepositoryQuery query, IProgressMonitor monitor,
			MultiStatus queryStatus) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ITask createTaskFromExistingKey(TaskRepository repository, String id) {
		// TODO Auto-generated method stub
		return null;
	}

}
