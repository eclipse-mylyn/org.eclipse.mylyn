/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
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

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * @author Mik Kersten
 */
public class DeleteTaskEditorAction extends DeleteAction {

	public static final String ID = "org.eclipse.mylyn.editor.actions.delete"; //$NON-NLS-1$

	private final ITask task;

	public DeleteTaskEditorAction(ITask task) {
		Assert.isNotNull(task);
		this.task = task;
		setId(ID);
		setActionDefinitionId(null);
		//setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setImageDescriptor(CommonImages.REMOVE);
	}

	public DeleteTaskEditorAction() {
		super();
		setText(Messages.DeleteTaskEditorAction_Delete_Task);
		setId(ID);
		setActionDefinitionId(null);
		task = null;
	}

	@Override
	public IStructuredSelection getStructuredSelection() {
		if (task != null) {
			return new StructuredSelection(task);
		} else {
			return super.getStructuredSelection();
		}
	}

}
