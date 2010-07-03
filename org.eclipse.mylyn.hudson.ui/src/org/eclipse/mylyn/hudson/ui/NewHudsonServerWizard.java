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

package org.eclipse.mylyn.hudson.ui;

import java.net.URI;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.builds.ui.spi.NewBuildServerWizard;
import org.eclipse.mylyn.commons.repository.RepositoryLocation;
import org.eclipse.mylyn.commons.ui.repositories.RepositoryWizardPage;
import org.eclipse.ui.IWorkbench;

/**
 * @author Steffen Pingel
 */
public class NewHudsonServerWizard extends NewBuildServerWizard {

	public NewHudsonServerWizard() {
	}

	@Override
	public void addPages() {
		RepositoryWizardPage page = new RepositoryWizardPage("newHudsonServer");
		page.setElement(new RepositoryLocation((URI) null));
		addPage(page);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// ignore
	}

	@Override
	public boolean performFinish() {
		// ignore
		return false;
	}

}
