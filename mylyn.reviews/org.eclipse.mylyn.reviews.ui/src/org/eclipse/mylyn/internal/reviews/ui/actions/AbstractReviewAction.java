/*******************************************************************************
 * Copyright (c) 2009, 2012 Atlassian and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Atlassian - initial API and implementation
 ******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.reviews.ui.ReviewUi;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * Abstract action of review actions
 * 
 * @author Thomas Ehrnhoefer
 */
public abstract class AbstractReviewAction extends BaseSelectionListenerAction
		implements IWorkbenchWindowActionDelegate {

	protected IWorkbenchWindow workbenchWindow;

	public AbstractReviewAction(String text) {
		super(text);
	}

	public void dispose() {
		// ignore
	}

	public void init(IWorkbenchWindow window) {
		this.workbenchWindow = window;
	}

	@Override
	public void run() {
		run(this);
	}

	public void selectionChanged(IAction action, ISelection selection) {
		if (ReviewUi.getActiveReview() != null) {
			action.setEnabled(true);
			setEnabled(true);
		} else {
			action.setEnabled(false);
			setEnabled(false);
		}
	}

	protected IEditorPart getActiveEditor() {
		IWorkbenchWindow window = workbenchWindow;
		if (window == null) {
			window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		}
		if (window != null && window.getActivePage() != null) {
			return window.getActivePage().getActiveEditor();
		}
		return null;
	}

	protected IEditorInput getEditorInputFromSelection(ISelection selection) {
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = ((IStructuredSelection) selection);
			if (structuredSelection.getFirstElement() instanceof IEditorInput) {
				return (IEditorInput) structuredSelection.getFirstElement();
			}
		}
		return null;
	}

}