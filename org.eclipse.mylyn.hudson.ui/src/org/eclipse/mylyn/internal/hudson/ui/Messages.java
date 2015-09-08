/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.ui;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.mylyn.internal.hudson.ui.messages"; //$NON-NLS-1$

	public static String Discovery_CouldNotStartService;

	public static String Discovery_IncorrectURI;

	public static String HudsonDiscovery_MessageText;

	public static String HudsonDiscovery_MessageTitle;

	public static String HudsonDiscovery_MissingURL;

	public static String HudsonDiscovery_ServerName;

	public static String JenkinsDiscovery_MessageText;

	public static String JenkinsDiscovery_MessageTitle;

	public static String JenkinsDiscovery_MissingURL;

	public static String JenkinsDiscovery_ServerName;

	public static String NewServerWizard_Message;

	public static String NewServerWizard_Title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
