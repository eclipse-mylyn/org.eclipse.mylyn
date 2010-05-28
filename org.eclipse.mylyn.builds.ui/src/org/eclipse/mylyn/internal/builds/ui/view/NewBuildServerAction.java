/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.view;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.builds.core.tasks.BuildTaskConnector;
import org.eclipse.mylyn.internal.tasks.ui.wizards.NewRepositoryWizard;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.ui.wizards.TaskRepositoryWizardDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Steffen Pingel
 */
public class NewBuildServerAction extends Action {

	public NewBuildServerAction() {
		setImageDescriptor(TasksUiImages.REPOSITORY_NEW);
		setToolTipText("Add Build Server Location");
		setText("Add Build Server...");
	}

	@Override
	public void run() {
		NewRepositoryWizard wizard = new NewRepositoryWizard(BuildTaskConnector.CONNECTOR_KIND);
		WizardDialog dialog = new TaskRepositoryWizardDialog(Display.getCurrent().getActiveShell(), wizard);
		dialog.create();
		dialog.getShell().setText("Add Build Server");
		dialog.setBlockOnOpen(true);
		dialog.open();
	}

}
