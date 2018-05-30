/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.github.ui.gist;

import org.eclipse.mylyn.internal.github.core.gist.GistConnector;
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
 * Gist task editor page factory class
 */
public class GistTaskEditorPageFactory extends AbstractTaskEditorPageFactory {

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory#canCreatePageFor(org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput)
	 */
	public boolean canCreatePageFor(TaskEditorInput input) {
		ITask task = input.getTask();
		return GistConnector.KIND.equals(task.getConnectorKind())
				|| TasksUiUtil.isOutgoingNewTask(task, GistConnector.KIND);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory#createPage(org.eclipse.mylyn.tasks.ui.editors.TaskEditor)
	 */
	public IFormPage createPage(TaskEditor parentEditor) {
		return new GistTaskEditorPage(parentEditor, GistConnector.KIND);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory#getPageImage()
	 */
	public Image getPageImage() {
		return GitHubImages.get(GitHubImages.GITHUB_LOGO_OBJ);
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory#getPageText()
	 */
	public String getPageText() {
		return Messages.GistTaskEditorPageFactory_PageText;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory#getPriority()
	 */
	public int getPriority() {
		return PRIORITY_TASK;
	}

	/**
	 * @see org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPageFactory#getConflictingIds(org.eclipse.mylyn.tasks.ui.editors.TaskEditorInput)
	 */
	public String[] getConflictingIds(TaskEditorInput input) {
		return new String[] { ITasksUiConstants.ID_PAGE_PLANNING };
	}

}
