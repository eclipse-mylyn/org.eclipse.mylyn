/*******************************************************************************
 * Copyright (c) 2013, 2016 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
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
