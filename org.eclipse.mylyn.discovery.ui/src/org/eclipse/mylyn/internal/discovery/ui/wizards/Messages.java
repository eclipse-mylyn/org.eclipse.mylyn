/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.discovery.ui.wizards;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author David Green
 */
class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.discovery.ui.wizards.messages"; //$NON-NLS-1$

	public static String ConnectorDescriptorToolTip_detailsLink;

	public static String ConnectorDescriptorToolTip_detailsLink_tooltip;

	public static String ConnectorDiscoveryWizard_cannotInstall;

	public static String ConnectorDiscoveryWizard_connectorDiscovery;

	public static String ConnectorDiscoveryWizard_installProblems;

	public static String InstallConnectorsJob_commaSeparator;

	public static String InstallConnectorsJob_connectorsNotAvailable;

	public static String InstallConnectorsJob_profileProblem;

	public static String InstallConnectorsJob_questionProceed;

	public static String InstallConnectorsJob_questionProceed_long;

	public static String InstallConnectorsJob_task_configuring;

	public static String InstallConnectorsJob_unexpectedError_url;

	public static String ConnectorDiscoveryWizardMainPage_clearButton_accessibleListener;

	public static String ConnectorDiscoveryWizardMainPage_clearButton_toolTip;

	public static String ConnectorDiscoveryWizardMainPage_connectorDiscovery;

	public static String ConnectorDiscoveryWizardMainPage_errorTitle;

	public static String ConnectorDiscoveryWizardMainPage_filter_documents;

	public static String ConnectorDiscoveryWizardMainPage_filter_tasks;

	public static String ConnectorDiscoveryWizardMainPage_filter_vcs;

	public static String ConnectorDiscoveryWizardMainPage_filterLabel;

	public static String ConnectorDiscoveryWizardMainPage_noConnectorsFound;

	public static String ConnectorDiscoveryWizardMainPage_noConnectorsFound_description;

	public static String ConnectorDiscoveryWizardMainPage_noMatchingItems_filteredType;

	public static String ConnectorDiscoveryWizardMainPage_noMatchingItems_noFilter;

	public static String ConnectorDiscoveryWizardMainPage_noMatchingItems_withFilterText;

	public static String ConnectorDiscoveryWizardMainPage_pageDescription;

	public static String ConnectorDiscoveryWizardMainPage_typeFilterText;

	public static String ConnectorDiscoveryWizardMainPage_unexpectedException;

	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
