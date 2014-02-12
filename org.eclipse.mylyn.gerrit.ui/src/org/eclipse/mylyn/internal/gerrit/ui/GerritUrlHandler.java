/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.ui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.commons.workbench.browser.AbstractUrlHandler;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.ui.editor.GerritTaskEditorPage;
import org.eclipse.mylyn.reviews.ui.spi.editor.ReviewSetSection;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.editor.IFormPage;

/**
 * @author Steffen Pingel
 * @author Miles Parker
 */
public class GerritUrlHandler extends AbstractUrlHandler {

	// http://git.eclipse.org/r/123 or https://git.eclipse.org/r/#/c/123/
	private static final Pattern URL_PATTERN = Pattern.compile("/?(#/c)?/(\\d+)"); //$NON-NLS-1$

	public GerritUrlHandler() {
		// ignore
	}

	@Override
	public EditorHandle openUrl(IWorkbenchPage page, String url, int customFlags) {
		for (TaskRepository repository : TasksUi.getRepositoryManager().getRepositories(GerritConnector.CONNECTOR_KIND)) {
			String taskId = getTaskId(repository, url);
			if (taskId != null) {
				EditorHandle editorHandle = TasksUiUtil.openTaskWithResult(repository, taskId);
				int patchSetNumber = getPatchSetNumber(repository, url, taskId);
				if (patchSetNumber > 0) {
					revealPatchSet(editorHandle, patchSetNumber);
				}
				return editorHandle;
			}
		}
		return null;
	}

	private void revealPatchSet(EditorHandle editorHandle, Integer patchSetNumber) {
		IWorkbenchPart part = editorHandle.getPart();
		if (part instanceof TaskEditor) {
			TaskEditor taskEditor = (TaskEditor) part;
			IFormPage activePage = taskEditor.setActivePage(GerritTaskEditorPage.class.getName());
			if (activePage instanceof GerritTaskEditorPage) {
				GerritTaskEditorPage gerritPage = (GerritTaskEditorPage) activePage;
				ReviewSetSection section = (ReviewSetSection) gerritPage.getPart(ReviewSetSection.class.getName());
				if (section != null && !section.getControl().isDisposed()) {
					section.revealPatchSet(patchSetNumber);
				}
			}
		}
	}

	public String getTaskId(TaskRepository repository, String url) {
		if (url.startsWith(repository.getRepositoryUrl())) {
			String path = "/" + url.substring(repository.getRepositoryUrl().length()); //$NON-NLS-1$
			Matcher matcher = URL_PATTERN.matcher(path);
			if (matcher.find()) {
				return matcher.group(2);
			}
		}
		return null;
	}

	/**
	 * Returns the patch set number as encoded in a Gerrit URL. For example,
	 * "http://review.mylyn.org/#/c/4698/5/foo/bar", "4698" -> 5. Returns -1 if the patch set number isn't specified or
	 * is not an integer.
	 */
	int getPatchSetNumber(TaskRepository repository, String url, String taskId) {
		String taskUrl = TasksUi.getRepositoryConnector(GerritConnector.CONNECTOR_KIND).getTaskUrl(repository.getUrl(),
				taskId);
		String urlQualifiers = StringUtils.remove(url, taskUrl);
		String[] fragments = StringUtils.split(urlQualifiers, "/"); //$NON-NLS-1$
		if (fragments.length > 0) {
			String patchSetFragment = fragments[0];
			try {
				return Integer.valueOf(patchSetFragment);
			} catch (NumberFormatException e) {
				//ignore, the patch fragment simply can't be parsed
			}
		}
		return -1;
	}

	@Override
	public int getPriority() {
		return 200;
	}
}
