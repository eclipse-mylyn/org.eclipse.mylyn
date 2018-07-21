/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
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

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.tasks.core.ITaskComment;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;

/**
 * @author Steffen Pingel
 */
public class CommentActionGroup extends ActionGroup {

	private CopyCommentDetailsAction copyDetailsAction;

	private CopyCommenterNameAction copyCommenterNameAction;

	private CopyCommentDetailsUrlAction copyCommentDetailsURL;

	private boolean initialized;

	private void initialize() {
		if (initialized) {
			return;
		}
		initialized = true;
		copyDetailsAction = new CopyCommentDetailsAction();
		copyCommenterNameAction = new CopyCommenterNameAction();
		copyCommentDetailsURL = new CopyCommentDetailsUrlAction();
	}

	@Override
	public void fillContextMenu(IMenuManager manager) {
		updateActions();
		manager.add(copyDetailsAction);
		manager.add(copyCommenterNameAction);
		manager.add(copyCommentDetailsURL);
	}

	private void updateActions() {
		initialize();
		IStructuredSelection selection = getStructuredSelection();
		copyDetailsAction.selectionChanged(selection);
		copyCommenterNameAction.selectionChanged(selection);
		copyCommentDetailsURL.selectionChanged(selection);
		Object firstElement = selection.getFirstElement();
		if (firstElement instanceof ITaskComment) {
			copyCommentDetailsURL.setEnabled(((ITaskComment) firstElement).getUrl() != null);
		}
	}

	public IStructuredSelection getStructuredSelection() {
		ActionContext context = getContext();
		if (context != null) {
			ISelection selection = context.getSelection();
			if (selection instanceof IStructuredSelection) {
				return (IStructuredSelection) selection;
			}
		}
		return StructuredSelection.EMPTY;
	}

}
