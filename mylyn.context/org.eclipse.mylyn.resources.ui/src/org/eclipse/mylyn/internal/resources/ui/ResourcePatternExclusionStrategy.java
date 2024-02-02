/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.resources.ui;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.tools.ant.types.selectors.SelectorUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;

/**
 * @author Shawn Minto
 */
public class ResourcePatternExclusionStrategy implements IResourceExclusionStrategy {

	private static final String TRAILING_PATH_WILDCARD = "/**"; //$NON-NLS-1$

	private static final String LEADING_PATH_WILDCARD = "**/"; //$NON-NLS-1$

	private Set<String> excludedPatterns = new HashSet<>();

	@Override
	public void init() {
		// ignore
	}

	@Override
	public void dispose() {
		// ignore
	}

	@Override
	public void update() {
		Set<String> excludedPatterns = new HashSet<>();
		Set<String> excludedResourcePatterns = ResourcesUiPreferenceInitializer.getExcludedResourcePatterns();
		excludedResourcePatterns.addAll(ResourcesUiPreferenceInitializer.getForcedExcludedResourcePatterns());
		for (String pattern : excludedResourcePatterns) {
			if (pattern != null && pattern.length() > 0) {
				excludedPatterns.add(pattern);
			}
		}

		this.excludedPatterns = excludedPatterns;
	}

	@Override
	public boolean isExcluded(IResource resource) {
		return isExcluded(resource.getProjectRelativePath(), resource, excludedPatterns);
	}

	/**
	 * Public for testing.
	 * 
	 * @param resource
	 *            can be null
	 */
	public static boolean isExcluded(IPath path, IResource resource, Set<String> excludedPatterns) {
		if (resource != null && resource.isDerived()) {
			return true;
		}

		String pathString = path.toPortableString();
		for (String pattern : excludedPatterns) {

			if (resource != null && pattern.startsWith("file:/") //$NON-NLS-1$
					&& isUriExcluded(resource.getLocationURI().toString(), pattern)) {
				return true;
			} else if (SelectorUtils.matchPath(pattern, pathString, false)
					|| SelectorUtils.match(pattern, pathString, false)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Public for testing.
	 */
	public static boolean isUriExcluded(String uri, String pattern) {
		if (uri != null && uri.startsWith(pattern)) {
			return true;
		} else {
			return false;
		}
	}

	public static Collection<String> convertToAntPattern(String basicPattern) {
		Set<String> patterns = new HashSet<>();
		patterns.add(basicPattern);
		if (!basicPattern.contains(LEADING_PATH_WILDCARD) && !basicPattern.contains(TRAILING_PATH_WILDCARD)) {
			// we don't want to migrate patterns that are already ant-style paterns
			patterns.add(LEADING_PATH_WILDCARD + basicPattern + TRAILING_PATH_WILDCARD);
			patterns.add(LEADING_PATH_WILDCARD + basicPattern);
			patterns.add(basicPattern + TRAILING_PATH_WILDCARD);
		}
		return patterns;
	}

}
