/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *  
 *  Contributors:
 *      Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.oslc.ui;

import java.util.List;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.oslc.core.IOslcConnector;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceDescriptor;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceProvider;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Robert Elves
 */
public class OslcServiceDiscoveryWizard extends Wizard {

	private final IOslcConnector connector;

	private final TaskRepository repository;

	private OslcServiceDiscoveryWizardPage page;

	private final List<OslcServiceProvider> providers;

	public OslcServiceDiscoveryWizard(IOslcConnector connector, TaskRepository repository,
			List<OslcServiceProvider> providers) {
		setNeedsProgressMonitor(true);
		this.connector = connector;
		this.repository = repository;
		this.providers = providers;
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public void addPages() {
		page = new OslcServiceDiscoveryWizardPage(connector, repository);
		page.setRootProviders(providers);
		addPage(page);
	}

	@Override
	public boolean canFinish() {
		return (page.getSelectedServiceProvider() != null);
	}

	public OslcServiceDescriptor getSelectedServiceDescriptor() {
		return page.getSelectedServiceProvider();
	}

}
