/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.view;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.builds.ui.BuildsUiConstants;
import org.eclipse.mylyn.commons.repositories.ui.RepositoryUi;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * @author Steffen Pingel
 */
public class NewBuildServerAction extends Action {

	public NewBuildServerAction() {
		setImageDescriptor(TasksUiImages.REPOSITORY_NEW);
		setToolTipText(Messages.NewBuildServerAction_addBuildServerLocation);
		setText(Messages.NewBuildServerAction_addBuildServer);
	}

	@Override
	public void run() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		RepositoryUi.openNewRepositoryDialog(window, BuildsUiConstants.ID_REPOSITORY_CATEGORY_BUILDS);
	}

}
