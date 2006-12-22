/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.actions;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.TasksUiUtil;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Mik Kersten
 */
public class EditRepositoryPropertiesAction extends BaseSelectionListenerAction {

	private static final String ID = "org.eclipse.mylar.tasklist.repositories.properties";

	public EditRepositoryPropertiesAction() {
		super("Properties");
		setId(ID);
		setEnabled(false);
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		return selection != null && !selection.isEmpty();
	}
	
	@Override
	public void run() {
		IStructuredSelection selection = getStructuredSelection();
		if (selection.getFirstElement() instanceof TaskRepository) {
			TasksUiUtil.openEditRepositoryWizard((TaskRepository) selection.getFirstElement());
		}
	}
}
