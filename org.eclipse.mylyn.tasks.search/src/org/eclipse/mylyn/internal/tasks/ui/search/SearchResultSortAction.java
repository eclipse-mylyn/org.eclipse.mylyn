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

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.ui.dialogs.TaskCompareDialog;

/**
 * @author Steffen Pingel
 */
public class SearchResultSortAction extends Action {

	private final RepositorySearchResultView view;

	public SearchResultSortAction(RepositorySearchResultView view) {
		super(Messages.SearchResultSortAction_Sort_Label);
		this.view = view;
		setEnabled(true);
	}

	@Override
	public void run() {
		TaskCompareDialog dialog = new TaskCompareDialog(view.getSite(), view.getSorter().getTaskComparator());
		if (dialog.open() == Window.OK) {
			view.getViewer().refresh();
		}
	}
}
