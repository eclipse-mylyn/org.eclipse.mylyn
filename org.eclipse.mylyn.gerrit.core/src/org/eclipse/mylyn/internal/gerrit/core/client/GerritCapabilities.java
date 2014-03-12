/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
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

	private static final Version VERSION_2_7_0 = new Version(2, 7, 0);

	private static final Version VERSION_2_9_0 = new Version(2, 9, 0);

	private final Version version;

	public GerritCapabilities(Version version) {
		this.version = version;
	}

	/**
	 * Returns true, if this version of Gerrit has been tested with the connector.
	 */
	public boolean isSupported() {
		return version.compareTo(VERSION_2_9_0) < 0;
	}

	public boolean supportsCommentLinks() {
		// see bug 417271
		return version.compareTo(VERSION_2_7_0) < 0;
	}

}
