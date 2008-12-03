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

package org.eclipse.mylyn.internal.team.ui.actions;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.patch.ApplyPatchOperation;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskAttachmentStorage;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 */
public class ApplyPatchAction extends BaseSelectionListenerAction implements IViewActionDelegate {

	public ApplyPatchAction() {
		super(Messages.ApplyPatchAction_Apply_Patch);
	}

	protected ApplyPatchAction(String text) {
		super(text);
	}

	private ISelection currentSelection;

	public void init(IViewPart view) {
		// ignore
	}

	public void run(IAction action) {
		if (currentSelection instanceof StructuredSelection) {
			Object object = ((StructuredSelection) currentSelection).getFirstElement();
			if (object instanceof ITaskAttachment) {
				final ITaskAttachment attachment = (ITaskAttachment) object;
				IStorage storage;
				try {
					storage = TaskAttachmentStorage.create(attachment);
				} catch (CoreException e) {
					TasksUiInternal.displayStatus(Messages.ApplyPatchAction_Error_Retrieving_Context, e.getStatus());
					return;
				}
				ApplyPatchOperation op = new ApplyPatchOperation(PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow()
						.getActivePage()
						.getActivePart(), storage, null, new CompareConfiguration());
				BusyIndicator.showWhile(Display.getDefault(), op);
			}
		}
	}

	public void selectionChanged(IAction action, ISelection selection) {
		this.currentSelection = selection;
	}
}
