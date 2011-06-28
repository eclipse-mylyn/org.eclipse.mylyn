/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.notifications;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.osgi.service.resolver.VersionRange;
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.Version;

/**
 * @author Steffen Pingel
 */
public class Environment {

	private final Dictionary<Object, Object> environment;

	public Environment() {
		environment = new Hashtable<Object, Object>(System.getProperties());
	}

	public Version getFrameworkVersion() {
		Bundle bundle = Platform.getBundle("org.eclipse.mylyn"); //$NON-NLS-1$
		if (bundle != null) {
			return CoreUtil.getVersion(bundle);
		} else {
			return Version.emptyVersion;
		}
	}

	public Version getPlatformVersion() {
		Bundle bundle = Platform.getBundle("org.eclipse.platform"); //$NON-NLS-1$
		if (bundle == null) {
			bundle = Platform.getBundle(Platform.PI_RUNTIME);
		}
		if (bundle != null) {
			String versionString = (String) bundle.getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
			try {
				return new Version(versionString);
			} catch (IllegalArgumentException e) {
				// should never happen
			}
		}
		return Version.emptyVersion;
	}

	public Version getRuntimeVersion() {
		Version result = parseRuntimeVersion(System.getProperty("java.runtime.version")); //$NON-NLS-1$
		if (result == Version.emptyVersion) {
			result = parseRuntimeVersion(System.getProperty("java.version")); //$NON-NLS-1$
		}
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean matches(FeedEntry entry, IProgressMonitor monitor) {
		if (!matchesVersion(entry.getFilter("frameworkVersion"), getFrameworkVersion())) { //$NON-NLS-1$
			return false;
		}
		if (!matchesVersion(entry.getFilter("platformVersion"), getPlatformVersion())) { //$NON-NLS-1$
			return false;
		}
		if (!matchesVersion(entry.getFilter("runtimeVersion"), getRuntimeVersion())) { //$NON-NLS-1$
			return false;
		}
		List<String> filterExpressions = entry.getFilters("filter"); //$NON-NLS-1$
		for (String filterExpression : filterExpressions) {
			try {
				Filter filter = FrameworkUtil.createFilter(filterExpression);
				if (!filter.match((Dictionary) environment)) {
					return false;
				}
			} catch (InvalidSyntaxException e) {
				// ignore that filter
			}
		}
		List<String> requiredFeatures = entry.getFilters("requires"); //$NON-NLS-1$
		for (String requiredFeature : requiredFeatures) {
			if (!getInstalledFeatures(monitor).contains(parseFeature(requiredFeature))) {
				return false;
			}
		}
		List<String> conflictedFeatures = entry.getFilters("conflicts"); //$NON-NLS-1$
		for (String conflictedFeature : conflictedFeatures) {
			if (getInstalledFeatures(monitor).contains(parseFeature(conflictedFeature))) {
				return false;
			}
		}
		return true;
	}

	private int findLastNumberIndex(String versionString, int secondSeparator) {
		int lastDigit = secondSeparator;
		for (int i = secondSeparator + 1; i < versionString.length(); i++) {
			if (Character.isDigit(versionString.charAt(i))) {
				lastDigit++;
			} else {
				break;
			}
		}
		if (lastDigit == secondSeparator) {
			return secondSeparator - 1;
		}
		return lastDigit;
	}

	public Set<String> getInstalledFeatures(IProgressMonitor monitor) {
		return Collections.emptySet();
	}

	private boolean matchesVersion(String expectedVersionRangeExpression, Version actualVersion) {
		if (expectedVersionRangeExpression != null) {
			try {
				VersionRange versionRange = new VersionRange(expectedVersionRangeExpression);
				if (!versionRange.isIncluded(actualVersion)) {
					return false;
				}
			} catch (IllegalArgumentException e) {
				// ignore
			}
		}
		return true;
	}

	private String parseFeature(String requiredFeature) {
		int i = requiredFeature.indexOf(";"); //$NON-NLS-1$
		if (i != -1) {
			return requiredFeature.substring(0, i);
		}
		return requiredFeature;
	}

	private Version parseRuntimeVersion(String versionString) {
		if (versionString != null) {
			int firstSeparator = versionString.indexOf('.');
			if (firstSeparator != -1) {
				try {
					int secondSeparator = versionString.indexOf('.', firstSeparator + 1);
					if (secondSeparator != -1) {
						return new Version(versionString.substring(0,
								findLastNumberIndex(versionString, secondSeparator) + 1));
					}
					return new Version(versionString.substring(0,
							findLastNumberIndex(versionString, firstSeparator) + 1));
				} catch (IllegalArgumentException e) {
					// ignore
				}
			}
		}
		return Version.emptyVersion;
	}

}
