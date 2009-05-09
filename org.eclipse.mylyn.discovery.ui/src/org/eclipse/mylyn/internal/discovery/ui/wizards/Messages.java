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

package org.eclipse.mylyn.internal.discovery.ui.wizards;

import org.eclipse.osgi.util.NLS;

class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.discovery.ui.wizards.messages"; //$NON-NLS-1$

	public static String ConnectorDescriptorToolTip_detailsLink;

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
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
