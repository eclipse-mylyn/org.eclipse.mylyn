/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.bugs.wizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IBundleGroup;

/**
 * @author Steffen Pingel
 */
public class FeatureGroup {

	private final Map<String, BundleGroupContainer> containerByName;

	private final List<IBundleGroup> bundleGroups;

	private final String name;

	private final String description;

	private final String title;

	private final String category;

	public FeatureGroup(String name, String description, String title, String category) {
		Assert.isNotNull(name);
		Assert.isNotNull(category);
		this.name = name;
		this.description = description;
		this.title = title;
		this.category = category;
		this.containerByName = new HashMap<String, BundleGroupContainer>();
		this.bundleGroups = new ArrayList<IBundleGroup>();
	}

	public void addBundleGroup(IBundleGroup bundleGroup, String featureName) {
		BundleGroupContainer container = containerByName.get(featureName);
		if (container == null) {
			container = new BundleGroupContainer(featureName);
			container.addBundleGroup(bundleGroup);
			containerByName.put(featureName, container);
		} else {
			container.addBundleGroup(bundleGroup);
		}
		bundleGroups.add(bundleGroup);
	}

	public Collection<BundleGroupContainer> getContainers() {
		return containerByName.values();
	}

	public List<IBundleGroup> getBundleGroups() {
		return bundleGroups;
	}

	public String getCategory() {
		return category;
	}

	public String getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public String getTitle() {
		return title;
	}

	public boolean requiresSelection() {
		return containerByName.size() > 1;
	}

}
