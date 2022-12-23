/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.versions.tasks.ui;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.tasks.core.TaskChangeSet;
import org.eclipse.mylyn.versions.ui.ScmUi;
import org.eclipse.ui.IActionDelegate;

/**
 * @author Kilian Matt
 */
public class OpenCommitAction implements IActionDelegate {

	private IStructuredSelection selection;

	public void run(IAction action) {
		TaskChangeSet taskChangeSet = (TaskChangeSet) selection.getFirstElement();

		ChangeSet changeset = taskChangeSet.getChangeset();
		ScmUi.getUiConnector(changeset.getRepository().getConnector()).showChangeSetInView(changeset);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			this.selection = (IStructuredSelection) selection;
		} else {
			this.selection = null;
		}
	}

}
