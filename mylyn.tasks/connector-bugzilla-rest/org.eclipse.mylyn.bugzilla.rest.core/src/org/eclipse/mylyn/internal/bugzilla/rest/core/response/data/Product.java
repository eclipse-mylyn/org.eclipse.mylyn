/*******************************************************************************
 * Copyright (c) 2014 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core.response.data;

public class Product implements Named {

	private int id;

	private String name;

	private boolean is_active;

	private String description;

	private String default_milestone;

	private boolean has_unconfirmed;

	private String classification;

	private Component[] components;

	private SortableActiveEntry[] versions;

	private SortableActiveEntry[] milestones;

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public boolean isActive() {
		return is_active;
	}

	public String getDescription() {
		return description;
	}

	public String getDefaultMilestone() {
		return default_milestone;
	}

	public boolean hasUnconfirmed() {
		return has_unconfirmed;
	}

	public String getClassification() {
		return classification;
	}

	public Component[] getComponents() {
		return components;
	}

	public SortableActiveEntry[] getVersions() {
		return versions;
	}

	public SortableActiveEntry[] getMilestones() {
		return milestones;
	}

	public Component getComponentWithName(String name) {
		for (Component componentEntry : getComponents()) {
			if (componentEntry.getName().equals(name)) {
				return componentEntry;
			}
		}
		return null;
	}

}
