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

package org.eclipse.mylyn.internal.tasks.ui.actions;

import java.util.Set;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class NewCategoryAction extends Action implements IViewActionDelegate {

	public static final String ID = "org.eclipse.mylyn.tasks.ui.actions.create.category"; //$NON-NLS-1$

	public NewCategoryAction() {
		setText(Messages.NewCategoryAction_New_Category_);
		setToolTipText(Messages.NewCategoryAction_New_Category_);
		setId(ID);
		setImageDescriptor(TasksUiImages.CATEGORY_NEW);
	}

	public void init(IViewPart view) {
	}

	public void run(IAction action) {
		run();
	}

	@Override
	public void run() {
		createCategory();
	}

	public TaskCategory createCategory() {
		InputDialog dialog = new InputDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				Messages.NewCategoryAction_Enter_name, Messages.NewCategoryAction_Enter_a_name_for_the_Category, "", null); //$NON-NLS-1$
		int dialogResult = dialog.open();
		if (dialogResult == Window.OK) {
			String name = dialog.getValue();
			Set<RepositoryQuery> queries = TasksUiInternal.getTaskList().getQueries();
			Set<AbstractTaskCategory> categories = TasksUiInternal.getTaskList().getCategories();

			for (AbstractTaskCategory category : categories) {
				if (name != null && name.equals(category.getSummary())) {
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							Messages.NewCategoryAction_New_Category,
							Messages.NewCategoryAction_A_category_with_this_name_already_exists);
					return null;
				}
			}
			for (RepositoryQuery query : queries) {
				if (name != null && name.equals(query.getSummary())) {
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							Messages.NewCategoryAction_New_Category,
							Messages.NewCategoryAction_A_query_with_this_name_already_exists);
					return null;
				}
			}

			TaskCategory category = new TaskCategory(TasksUiPlugin.getTaskList().getUniqueHandleIdentifier(), name);
			TasksUiPlugin.getTaskList().addCategory(category);
			return category;
		}
		return null;
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}
