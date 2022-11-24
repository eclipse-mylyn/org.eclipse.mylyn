/*******************************************************************************
 * Copyright (c) 2009, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.discovery.core;

import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.osgi.util.NLS;
import org.osgi.framework.Version;

/**
 * @author David Green
 */
public abstract class DiscoveryCore {
	public static final String ID_PLUGIN = "org.eclipse.mylyn.discovery.core"; //$NON-NLS-1$

	/**
	 * The system property to override the URL of the Mylyn discovery directory.
	 */
	private static final String SYSTEM_PROPERTY_DIRECTORY_URL = "mylyn.discovery.directory"; //$NON-NLS-1$

	private DiscoveryCore() {
	}

	public static String getDiscoveryUrl() {
		Version v = CoreUtil.getFrameworkVersion();
		String defaultUrl = NLS.bind(
				"https://www.eclipse.org/mylyn/discovery/directory-{0}.{1}.xml", v.getMajor(), v.getMinor()); //$NON-NLS-1$
		return System.getProperty(DiscoveryCore.SYSTEM_PROPERTY_DIRECTORY_URL, defaultUrl);
	}
}
