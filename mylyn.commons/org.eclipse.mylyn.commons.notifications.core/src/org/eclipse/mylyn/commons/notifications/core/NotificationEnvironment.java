/*******************************************************************************
 * Copyright (c) 2011, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.notifications.core;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IAdaptable;
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
public class NotificationEnvironment {

	private final Dictionary<Object, Object> environment;

	public NotificationEnvironment() {
		environment = new Hashtable<>(System.getProperties());
	}

	public Version getFrameworkVersion() {
		return CoreUtil.getFrameworkVersion();
	}

	public Version getPlatformVersion() {
		Bundle bundle = Platform.getBundle("org.eclipse.platform"); //$NON-NLS-1$
		if (bundle == null) {
			bundle = Platform.getBundle(Platform.PI_RUNTIME);
		}
		if (bundle != null) {
			String versionString = bundle.getHeaders().get(org.osgi.framework.Constants.BUNDLE_VERSION);
			try {
				return new Version(versionString);
			} catch (IllegalArgumentException e) {
				// should never happen
			}
		}
		return Version.emptyVersion;
	}

	public Version getRuntimeVersion() {
		return CoreUtil.getRuntimeVersion();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public boolean matches(IAdaptable item, IProgressMonitor monitor) {
		IFilterable entry = item.getAdapter(IFilterable.class);
		if (entry == null) {
			return true;
		}

		if (!matchesVersion(entry.getFilter("frameworkVersion"), getFrameworkVersion()) || !matchesVersion(entry.getFilter("platformVersion"), getPlatformVersion()) || !matchesVersion(entry.getFilter("runtimeVersion"), getRuntimeVersion())) { //$NON-NLS-1$
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

}
