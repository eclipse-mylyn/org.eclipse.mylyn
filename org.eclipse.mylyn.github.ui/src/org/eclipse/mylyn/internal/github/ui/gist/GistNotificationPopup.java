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
package org.eclipse.mylyn.internal.github.ui.gist;

import java.util.Set;

import org.eclipse.egit.github.core.Gist;
import org.eclipse.mylyn.github.ui.internal.GitHubImages;
import org.eclipse.mylyn.internal.github.core.gist.GistConnector;
import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractNotificationPopup;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
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

@SuppressWarnings("restriction")
public class GistNotificationPopup extends AbstractNotificationPopup {

	private Gist gist;

	private String title;

	public GistNotificationPopup(Display display, Gist gist, String title) {
		super(display);
		this.gist = gist;
		this.title = title;
	}

	@Override
	protected void createContentArea(Composite composite) {
		composite.setLayout(new GridLayout(1, true));
		Label label = new Label(composite, SWT.NONE);
		label.setText("Title: " + title);
		Link link = new Link(composite, SWT.WRAP);
		link.setText("Created Gist: <a>" + gist.getId() + "</a>");
		link.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		link.setBackground(composite.getBackground());
		link.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				Set<TaskRepository> repositories = TasksUi
						.getRepositoryManager().getRepositories(
								GistConnector.KIND);
				if (!repositories.isEmpty())
					TasksUiUtil.openTask(repositories.iterator().next(), gist.getId());
			}
		});
	}

	@Override
	protected String getPopupShellTitle() {
		return "GitHub Notification";
	}

	@Override
	protected Image getPopupShellImage(int maximumHeight) {
		return GitHubImages.get(GitHubImages.GITHUB_LOGO_OBJ);
	}

}
