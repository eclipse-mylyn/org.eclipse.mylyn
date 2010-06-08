/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.actions;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
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
