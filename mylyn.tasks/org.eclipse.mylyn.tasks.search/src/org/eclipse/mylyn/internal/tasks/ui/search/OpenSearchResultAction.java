/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.search;

import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/**
 * This class is used to open a bug report in an editor.
 */
public class OpenSearchResultAction extends Action {

	/** The view this action works on */
	private final RepositorySearchResultView resultView;

	/**
	 * Constructor
	 * 
	 * @param text
	 *            The text for this action
	 * @param resultView
	 *            The <code>RepositorySearchResultView</code> this action works on
	 */
	public OpenSearchResultAction(String text, RepositorySearchResultView resultView) {
		setText(text);
		this.resultView = resultView;
	}

	/**
	 * Open the selected bug reports in their own editors.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		// Get the selected items
		ISelection s = resultView.getViewer().getSelection();
		if (s instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) s;

			// go through each of the selected items and show it in an editor
			for (Iterator<AbstractTask> it = selection.iterator(); it.hasNext();) {
				AbstractTask repositoryHit = it.next();
				TasksUiUtil.openTask(repositoryHit.getRepositoryUrl(), repositoryHit.getTaskId(),
						repositoryHit.getUrl());
			}

		}
	}

}
