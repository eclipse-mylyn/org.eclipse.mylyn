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

package org.eclipse.mylyn.builds.ui.spi;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Steffen Pingel
 */
public class BuildServerWizard extends Wizard implements INewWizard {

	private final IBuildServer model;

	public BuildServerWizard(IBuildServer model) {
		this.model = model;
	}

	@Override
	public void addPages() {
		BuildServerWizardPage page = new BuildServerWizardPage("newBuildServer");
		page.setModel(model);
		addPage(page);
	}

	public IBuildServer getModel() {
		return model;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// ignore
	}

	@Override
	public boolean performFinish() {
		BuildsUiInternal.getModel().getServers().add(getModel());
		return true;
	}

}
