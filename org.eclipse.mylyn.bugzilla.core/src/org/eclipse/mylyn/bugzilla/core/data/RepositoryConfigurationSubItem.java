/*******************************************************************************
 * Copyright (c) 2012 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.core.data;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;

public class RepositoryConfigurationSubItem extends RepositoryConfigurationItem {

	private static final long serialVersionUID = 5876901980766940630L;

	private final RepositoryConfigurationData data = new RepositoryConfigurationData();

	public RepositoryConfigurationSubItem(String name) {
		super(name);
		// ignore
	}

	public void addItem(BugzillaAttribute element, RepositoryConfigurationItem item) {
		Assert.isNotNull(element);
		Assert.isNotNull(item);
		if (!data.containsItemWithName(element, item.getName())) {
			data.addItem(element, item);
		}
	}

	public List<String> getConfigurationItemsAsStringList(BugzillaAttribute element) {
		return data.getConfigurationItemsAsStringList(element);
	}

	public RepositoryConfigurationItem[] getConfigurationItemsArray(BugzillaAttribute element) {
		return data.getConfigurationItemsArray(element);
	}

	public RepositoryConfigurationItem getNamedItem(BugzillaAttribute element, String name) {
		return data.getNamedItem(element, name);
	}

}
