/*******************************************************************************
 * Copyright (c) 2013, 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.client;

import org.osgi.framework.Version;

/**
 * Specifies capabilities of a specific version of Gerrit.
 */
public class GerritCapabilities {

	public static final Version MINIMUM_SUPPORTED_VERSION = new Version(2, 9, 0);

	public static final Version MAXIMUM_SUPPORTED_VERSION = new Version(2, 11, 10);

	private final Version version;

	public GerritCapabilities(Version version) {
		this.version = version;
	}

	/**
	 * Returns true, if this version of Gerrit has been tested with the connector.
	 */
	public boolean isSupported() {
		return version.compareTo(MINIMUM_SUPPORTED_VERSION) >= 0 && version.compareTo(MAXIMUM_SUPPORTED_VERSION) <= 0;
	}
}
