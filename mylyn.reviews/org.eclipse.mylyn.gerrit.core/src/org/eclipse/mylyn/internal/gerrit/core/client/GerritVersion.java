/*******************************************************************************
 * Copyright (c) 2013, 2015 Tasktop Technologies and others.
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

import static java.lang.Integer.parseInt;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.Assert;
import org.osgi.framework.Version;

public class GerritVersion {

	public static final Version VERSION_2_11_2 = new Version(2, 11, 2);

	public static final Version VERSION_2_12_0 = new Version(2, 12, 0);

	// e.g. 2.6 or 2.6.0
	private static final Pattern MAJOR_MINOR_MICRO_VERSION_PATTERN = Pattern.compile("V?\\d+\\.\\d+(\\.\\d+)?"); //$NON-NLS-1$

	// e.g. 2.6-rc3
	private static final Pattern MAJOR_MINOR_QUALIFIER_VERSION_PATTERN = Pattern
			.compile("V?(\\d+)\\.(\\d+)-([-\\w]+).*"); //$NON-NLS-1$

	// e.g. 2.6.1-rc1, 2.8.6.1
	private static final Pattern MAJOR_MINOR_MICRO_QUALIFIER_VERSION_PATTERN = Pattern
			.compile("V?(\\d+)\\.(\\d+)\\.(\\d+)[-\\.]([-\\w]+).*"); //$NON-NLS-1$

	private GerritVersion() {
	}

	public static Version parseGerritVersion(String version) {
		Assert.isLegal(version != null);
		Assert.isLegal(!version.isEmpty());

		Matcher matcher = MAJOR_MINOR_MICRO_VERSION_PATTERN.matcher(version);
		if (matcher.matches()) {
			return Version.parseVersion(version);
		}
		matcher = MAJOR_MINOR_QUALIFIER_VERSION_PATTERN.matcher(version);
		if (matcher.matches()) {
			return new Version(parseInt(matcher.group(1)), parseInt(matcher.group(2)), 0, matcher.group(3));
		}
		matcher = MAJOR_MINOR_MICRO_QUALIFIER_VERSION_PATTERN.matcher(version);
		if (matcher.matches()) {
			return new Version(parseInt(matcher.group(1)), parseInt(matcher.group(2)), parseInt(matcher.group(3)),
					matcher.group(4));
		}
		throw new IllegalArgumentException("Unrecognized version pattern : " + version); //$NON-NLS-1$
	}

	public static boolean isVersion2112OrLater(Version version) {
		return version.compareTo(VERSION_2_11_2) >= 0;
	}

	public static boolean isVersion2120OrLater(Version version) {
		return version.compareTo(VERSION_2_12_0) >= 0;
	}

}
