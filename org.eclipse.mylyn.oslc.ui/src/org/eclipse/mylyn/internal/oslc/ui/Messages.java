/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Tasktop EULA
 * which accompanies this distribution, and is available at
 * http://tasktop.com/legal
 *******************************************************************************/

package org.eclipse.mylyn.internal.oslc.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.oslc.ui.messages"; //$NON-NLS-1$

	public static String OslcRepositorySettingsPage_Enter_Base_Above_And_Validate;

	public static String OslcRepositorySettingsPage_Enter_Base_Url_Above;

	public static String OslcServiceDiscoveryWizardPage_Browse_Available_Services_Below;

	public static String OslcServiceDiscoveryWizardPage_Serivce_Discovery;

	public static String OslcServiceLabelProvider_Loading;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
