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
import java.util.List;

import org.eclipse.core.runtime.IBundleGroup;

/**
 * A container for features that map to the same name.
 * 
 * @author Steffen Pingel
 */
public class BundleGroupContainer {

	private final List<IBundleGroup> groups;

	private final String name;

	public BundleGroupContainer(String name) {
		this.name = name;
		this.groups = new ArrayList<IBundleGroup>();
	}

	public void addBundleGroup(IBundleGroup bundleGroup) {
		groups.add(bundleGroup);
	}

	public List<IBundleGroup> getGroups() {
		return groups;
	}

	public String getName() {
		return name;
	}

}