/*******************************************************************************
 * Copyright (c) 2024 George Lindholm
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.builds.ui.spi;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = Messages.class.getPackageName() + ".messages"; //$NON-NLS-1$

	public static String BuildServerPart_buildPlans;

	public static String BuildServerPart_deselectAllShortcut;

	public static String BuildServerPart_refreshShortcut;

	public static String BuildServerPart_savePassword;

	public static String BuildServerPart_selectAllShortcut;

	public static String BuildServerPart_serverValidationFailed;

	public static String BuildServerPart_validatingRepository;

	public static String BuildServerWizard_buildServerProperties;

	public static String BuildServerWizard_newBuildServer;

	public static String BuildServerWizard_unexpectedErrorWhileSavingBuilds;

	public static String BuildServerWizardPage_buildServerProperties;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
