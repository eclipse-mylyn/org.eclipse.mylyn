/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.gist;

import java.io.IOException;
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.egit.github.core.Gist;
import org.eclipse.egit.github.core.GistFile;
import org.eclipse.egit.github.core.service.GistService;
import org.eclipse.mylyn.internal.github.core.GitHubException;
import org.eclipse.mylyn.internal.github.ui.GitHubUi;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

/**
 * Create Gist job class
 */
public class CreateGistJob extends Job {

	private String title;
	private String content;
	private GistService service;
	private boolean isPublic;
	private TaskRepository repository;

	/**
	 * Create job that will create a Gist with the specified parameters
	 *
	 * @param name
	 * @param title
	 * @param content
	 * @param service
	 * @param isPublic
	 * @param repository
	 */
	public CreateGistJob(String name, String title, String content,
			GistService service, boolean isPublic, TaskRepository repository) {
		super(name);
		this.title = title;
		this.content = content;
		this.service = service;
		this.isPublic = isPublic;
		this.repository = repository;
	}

	@Override
	@SuppressWarnings("restriction")
	protected IStatus run(IProgressMonitor monitor) {
		try {
			Gist gist = new Gist().setPublic(isPublic);
			gist.setDescription(title);
			GistFile file = new GistFile().setContent(content);
			gist.setFiles(Collections.singletonMap(title, file));
			final Gist created = service.createGist(gist);
			final Display display = PlatformUI.getWorkbench().getDisplay();
			display.asyncExec(new Runnable() {

				public void run() {
					GistNotificationPopup popup = new GistNotificationPopup(
							display, created, title, repository);
					popup.create();
					popup.open();
				}
			});
			TasksUiPlugin
					.getTaskJobFactory()
					.createSynchronizeRepositoriesJob(
							GistConnectorUi.getRepositories()).schedule();
		} catch (IOException e) {
			GitHubUi.logError(GitHubException.wrap(e));
		}
		return Status.OK_STATUS;
	}
}
