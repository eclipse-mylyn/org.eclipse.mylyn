/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.actions.ActionContext;
import org.eclipse.ui.actions.ActionGroup;

/**
 * @author Steffen Pingel
 */
public class CommentActionGroup extends ActionGroup {

	private CopyCommentDetailsAction copyDetailsAction;

	private CopyCommenterNameAction copyCommenterNameAction;

	private boolean initialized;

	private void initialize() {
		if (initialized) {
			return;
		}
		initialized = true;
		copyDetailsAction = new CopyCommentDetailsAction();
		copyCommenterNameAction = new CopyCommenterNameAction();
	}

	@Override
	public void fillContextMenu(IMenuManager manager) {
		updateActions();
		manager.add(copyDetailsAction);
		manager.add(copyCommenterNameAction);
	}

	private void updateActions() {
		initialize();
		IStructuredSelection selection = getStructuredSelection();
		copyDetailsAction.selectionChanged(selection);
		copyCommenterNameAction.selectionChanged(selection);
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
