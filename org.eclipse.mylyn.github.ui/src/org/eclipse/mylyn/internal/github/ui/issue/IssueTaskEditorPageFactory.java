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
 *     David Green <david.green@tasktop.com> - initial contribution
 *     Christian Trutz <christian.trutz@gmail.com> - initial contribution
 *     Chris Aniszczyk <caniszczyk@gmail.com> - initial contribution
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.issue;

import org.eclipse.mylyn.internal.github.core.GitHub;
import org.eclipse.mylyn.internal.github.ui.GitHubImages;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.ITasksUiConstants;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * Editor page factory for GitHub.
 */
public class IssueTaskEditorPageFactory extends AbstractTaskEditorPageFactory {

	@Override
	public boolean canCreatePageFor(TaskEditorInput input) {
		ITask task = input.getTask();
		return GitHub.CONNECTOR_KIND.equals(task.getConnectorKind())
				|| TasksUiUtil.isOutgoingNewTask(task, GitHub.CONNECTOR_KIND);
	}

	@Override
	public Image getPageImage() {
		return GitHubImages.get(GitHubImages.GITHUB_LOGO_OBJ);
	}

	@Override
	public String getPageText() {
		return Messages.IssueTaskEditorPageFactory_PageText;
	}

	@Override
	public int getPriority() {
		return PRIORITY_TASK;
	}

	@Override
	public IFormPage createPage(TaskEditor parentEditor) {
		return new IssueTaskEditorPage(parentEditor);
	}

	@Override
	public String[] getConflictingIds(TaskEditorInput input) {
		return new String[] { ITasksUiConstants.ID_PAGE_PLANNING };
	}

}
