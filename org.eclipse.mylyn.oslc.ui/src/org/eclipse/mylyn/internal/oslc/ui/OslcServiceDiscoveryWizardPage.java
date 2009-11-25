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

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.oslc.core.IOslcConnector;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceDescriptor;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceProvider;
import org.eclipse.mylyn.internal.oslc.core.OslcServiceProviderCatalog;
import org.eclipse.mylyn.internal.oslc.ui.OslcServiceDiscoveryProvider.ServiceProviderCatalogWrapper;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiImages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Robert Elves
 */
public class OslcServiceDiscoveryWizardPage extends WizardPage {

	private static final String TITLE_SERVICE_DISCOVERY = Messages.OslcServiceDiscoveryWizardPage_Serivce_Discovery;

	private TreeViewer v;

	private OslcServiceDescriptor selectedServiceDescriptor;

	private final TaskRepository repository;

	private final OslcServiceDiscoveryProvider provider;

	private List<OslcServiceProvider> rootProviders;

	protected OslcServiceDiscoveryWizardPage(IOslcConnector connector, TaskRepository repository) {
		super(TITLE_SERVICE_DISCOVERY, TITLE_SERVICE_DISCOVERY, TasksUiImages.BANNER_REPOSITORY);
		this.repository = repository;
		this.provider = new OslcServiceDiscoveryProvider(connector, repository, null);
		setMessage(Messages.OslcServiceDiscoveryWizardPage_Browse_Available_Services_Below);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		GridDataFactory.fillDefaults().grab(true, true).applyTo(composite);

		v = new TreeViewer(composite, SWT.VIRTUAL | SWT.BORDER);
		v.setUseHashlookup(true);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(v.getTree());

		v.setLabelProvider(new OslcServiceLabelProvider());
		v.setContentProvider(provider);
		v.addSelectionChangedListener(new ISelectionChangedListener() {

			public void selectionChanged(SelectionChangedEvent event) {
				TreeSelection selection = (TreeSelection) v.getSelection();
				Object o = selection.getFirstElement();
				if (o instanceof ServiceProviderCatalogWrapper) {
					Object provObj = ((ServiceProviderCatalogWrapper) o).getServiceObject();
					if (provObj instanceof OslcServiceProviderCatalog) {
						setSelectedServiceDescriptor(null);
					} else if (provObj instanceof OslcServiceProvider) {
						setSelectedServiceDescriptor(null);
					} else if (provObj instanceof OslcServiceDescriptor) {
						setSelectedServiceDescriptor((OslcServiceDescriptor) provObj);
					}
				} else {
					// TODO: disable OK button
				}
			}
		});
		if (rootProviders != null && !rootProviders.isEmpty()) {
			v.setInput(rootProviders);
		} else {
			v.setInput(new OslcServiceProviderCatalog(repository.getRepositoryLabel(), repository.getUrl()));
		}
		setControl(composite);
	}

	private void setSelectedServiceDescriptor(OslcServiceDescriptor selectedServiceDescriptor) {
		this.selectedServiceDescriptor = selectedServiceDescriptor;
		setPageComplete(selectedServiceDescriptor != null);
	}

	public OslcServiceDescriptor getSelectedServiceProvider() {
		return selectedServiceDescriptor;
	}

	public void setRootProviders(List<OslcServiceProvider> providers) {
		this.rootProviders = providers;
	}

}
