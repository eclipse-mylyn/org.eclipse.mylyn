/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     David Green
 *     Shawn Minto bug 275513
 * 	   Steffen Pingel bug 276012 code review, bug 277191 gradient canvas
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.ui.wizards;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.discovery.core.model.ConnectorDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * The main wizard page that allows users to select connectors that they wish to install.
 * 
 * @author David Green
 */
public class ConnectorDiscoveryWizardMainPage extends WizardPage implements IShellProvider {

	private static final String DEFAULT_DIRECTORY_URL = "http://www.eclipse.org/mylyn/discovery/directory.xml"; //$NON-NLS-1$

	private static final String SYSTEM_PROPERTY_DIRECTORY_URL = "mylyn.discovery.directory"; //$NON-NLS-1$

	private static final int MINIMUM_HEIGHT = 480;

	private DiscoveryViewer viewer;

	public ConnectorDiscoveryWizardMainPage() {
		super(ConnectorDiscoveryWizardMainPage.class.getSimpleName());
		setTitle(org.eclipse.mylyn.internal.discovery.ui.wizards.Messages.ConnectorDiscoveryWizardMainPage_connectorDiscovery);
		// setImageDescriptor(image);
		setDescription(org.eclipse.mylyn.internal.discovery.ui.wizards.Messages.ConnectorDiscoveryWizardMainPage_pageDescription);
		setPageComplete(false);
	}

	public void createControl(Composite parent) {
		viewer = new DiscoveryViewer(this, getContainer());
		viewer.setShowConnectorDescriptorKindFilter(getWizard().isShowConnectorDescriptorKindFilter());
		viewer.setShowConnectorDescriptorTextFilter(getWizard().isShowConnectorDescriptorTextFilter());
		viewer.setMinimumHeight(MINIMUM_HEIGHT);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				setPageComplete(!viewer.getInstallableConnectors().isEmpty());
			}
		});
		viewer.createControl(parent);

		String url = System.getProperty(SYSTEM_PROPERTY_DIRECTORY_URL, DEFAULT_DIRECTORY_URL);
		if (url.length() > 0) {
			viewer.setDirectoryUrl(url);
		}
		viewer.setEnvironment(getWizard().getEnvironment());
		setControl(viewer.getControl());
	}

	@Override
	public ConnectorDiscoveryWizard getWizard() {
		return (ConnectorDiscoveryWizard) super.getWizard();
	}

	public List<ConnectorDescriptor> getInstallableConnectors() {
		return viewer.getInstallableConnectors();
	}

	private void maybeUpdateDiscovery() {
		if (!getControl().isDisposed() && isCurrentPage() && viewer.getDiscovery() == null) {
			viewer.updateDiscovery();
		}
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible && viewer.getDiscovery() == null) {
			Display.getCurrent().asyncExec(new Runnable() {
				public void run() {
					maybeUpdateDiscovery();
				}
			});
		}
	}

}
