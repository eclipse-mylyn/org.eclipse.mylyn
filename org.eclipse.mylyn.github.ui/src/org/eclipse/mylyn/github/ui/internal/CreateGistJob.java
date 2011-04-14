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
import java.util.Collections;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.mylyn.github.internal.Gist;
import org.eclipse.mylyn.github.internal.GistFile;
import org.eclipse.mylyn.github.internal.GistService;
import org.eclipse.mylyn.github.internal.User;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class CreateGistJob extends Job {

	private String title;
	private String extension;
	private String content;
	private GistService service;
	private String user;

	public CreateGistJob(String name, String title, String content,
			GistService service, String user) {
		super(name);
		this.title = title;
		this.content = content;
		this.service = service;
		this.user = user;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			Gist gist = new Gist().setPublic(true);
			if (user != null)
				gist.setUser(new User().setLogin(user));
			gist.setDescription(title);
			GistFile file = new GistFile().setContent(content);
			gist.setFiles(Collections.singletonMap(title, file));
			final Gist created = service.createGist(gist);
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				@SuppressWarnings("restriction")
				public void run() {
					GistNotificationPopup popup = new GistNotificationPopup(
							PlatformUI.getWorkbench().getDisplay(), created
									.getId(), title);
					popup.create();
					popup.open();
				}
			});
		} catch (IOException e) {
			GitHubUi.logError(e);
		}
		return Status.OK_STATUS;
	}
}