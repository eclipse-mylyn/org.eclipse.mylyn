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

import java.util.UUID;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.commons.repositories.RepositoryLocation;
import org.eclipse.mylyn.internal.builds.core.BuildServer;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.commons.repositories.InMemoryCredentialsStore;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

/**
 * @author Steffen Pingel
 */
public class BuildServerWizard extends Wizard implements INewWizard {

	private IBuildServer model;

	private final IBuildServer original;

	public BuildServerWizard(IBuildServer server) {
		this.original = server;
	}

	@Override
	public void addPages() {
		BuildServerWizardPage page = new BuildServerWizardPage("newBuildServer");
		page.init(getModel());
		addPage(page);
	}

	public IBuildServer getModel() {
		if (model == null) {
			model = ((BuildServer) original).createWorkingCopy();
			RepositoryLocation workingCopy = new RepositoryLocation(original.getLocation());
			if (workingCopy.getProperty(RepositoryLocation.PROPERTY_ID) == null) {
				workingCopy.setProperty(RepositoryLocation.PROPERTY_ID, UUID.randomUUID().toString());
			}
			workingCopy.setCredentialsStore(new InMemoryCredentialsStore(workingCopy.getCredentialsStore()));
			((BuildServer) model).setLocation(workingCopy);
		}
		return model;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// ignore
	}

	@Override
	public boolean performFinish() {
		((BuildServer) getModel()).applyToOriginal();
		original.getLocation().apply(getModel().getLocation());
		if (((BuildServer) original).eContainer() != BuildsUiInternal.getModel()) {
			BuildsUiInternal.getModel().getServers().add(getModel());
		}
		return true;
	}

}
