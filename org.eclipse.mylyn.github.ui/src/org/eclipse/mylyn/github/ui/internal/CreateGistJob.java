/*******************************************************************************
 * Copyright (c) 2011 Red Hat and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.github.ui.internal;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.github.internal.GitHubCredentials;
import org.eclipse.mylyn.github.internal.GitHubService;
import org.eclipse.mylyn.github.internal.GitHubServiceException;
import org.eclipse.swt.widgets.Display;

public class CreateGistJob extends Job {

	private String title;
	private String extension;
	private String content;
	private GitHubCredentials credentials;
	private GitHubService service;

	public CreateGistJob(String name, String title, String extension, String content, GitHubCredentials credentials, GitHubService service) {
		super(name);
		this.title = title;
		this.extension = extension;
		this.content = content;
		this.credentials = credentials;
		this.service = service;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			final String url = service.createGist(title, extension, content, credentials);
			Display.getDefault().asyncExec(new Runnable() {
				@SuppressWarnings("restriction")
				public void run() {
					GistNotificationPopup popup = new GistNotificationPopup(Display.getDefault(), url, title);
					popup.create();
					popup.open();
				}
			});
		} catch (GitHubServiceException e) {
			GitHubUi.logError(e);
		} catch (IOException e) {
			GitHubUi.logError(e);
		}
		return Status.OK_STATUS;
	}
}