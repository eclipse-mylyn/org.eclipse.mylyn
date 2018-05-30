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

import java.text.MessageFormat;

import org.eclipse.egit.github.core.Gist;
import org.eclipse.mylyn.commons.ui.dialogs.AbstractNotificationPopup;
import org.eclipse.mylyn.internal.github.ui.GitHubImages;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;

/**
 * Gist notification popup class
 */
@SuppressWarnings("restriction")
public class GistNotificationPopup extends AbstractNotificationPopup {

	private Gist gist;

	private String title;

	private TaskRepository repository;

	/**
	 * Create Gist notification popup
	 *
	 * @param display
	 * @param gist
	 * @param title
	 * @param repository
	 */
	public GistNotificationPopup(Display display, Gist gist, String title,
			TaskRepository repository) {
		super(display);
		this.gist = gist;
		this.title = title;
		this.repository = repository;
	}

	@Override
	protected void createContentArea(Composite composite) {
		composite.setLayout(new GridLayout(1, true));
		Label label = new Label(composite, SWT.NONE);
		label.setText(MessageFormat.format(
				Messages.GistNotificationPopup_GistTitle, title));
		Link link = new Link(composite, SWT.WRAP);
		link.setText(MessageFormat.format(
				Messages.GistNotificationPopup_GistLink, gist.getId()));
		link.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		link.setBackground(composite.getBackground());
		link.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				AbstractTask task = TasksUiInternal.getTask(
						repository.getRepositoryUrl(), gist.getId(),
						gist.getHtmlUrl());
				if (task != null)
					TasksUiInternal.refreshAndOpenTaskListElement(task);
				else
					TasksUiInternal.openTask(repository, gist.getId(), null);
			}
		});
	}

	@Override
	protected String getPopupShellTitle() {
		return Messages.GistNotificationPopup_ShellTitle;
	}

	@Override
	protected Image getPopupShellImage(int maximumHeight) {
		return GitHubImages.get(GitHubImages.GITHUB_LOGO_OBJ);
	}

}
