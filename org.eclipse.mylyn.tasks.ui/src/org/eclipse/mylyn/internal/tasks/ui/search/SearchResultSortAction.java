/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
