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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;

public class RepositoryConfigurationData implements Serializable {

	private static final long serialVersionUID = -6370616800459566227L;

	private final Map<BugzillaAttribute, List<RepositoryConfigurationItem>> itemsByAttribute;

	private final Map<BugzillaAttribute, Map<String, RepositoryConfigurationItem>> itemsByNamedAttribute;

	public RepositoryConfigurationData() {
		itemsByAttribute = new HashMap<BugzillaAttribute, List<RepositoryConfigurationItem>>();
		itemsByNamedAttribute = new HashMap<BugzillaAttribute, Map<String, RepositoryConfigurationItem>>();
	}

	public List<RepositoryConfigurationItem> getConfigurationItems(BugzillaAttribute element) {
		Assert.isNotNull(element);
		return itemsByAttribute.get(element);
	}

	public boolean containsItemWithName(BugzillaAttribute element, String name) {
		Assert.isNotNull(element);
		Assert.isNotNull(name);
		List<RepositoryConfigurationItem> items = itemsByAttribute.get(element);
		if (items == null) {
			return false;
		}
		for (RepositoryConfigurationItem repositoryConfigurationItem : items) {
			if (repositoryConfigurationItem.equals(name)) {
				return true;
			}
		}
		return false;
	}

	public RepositoryConfigurationItem getNamedItem(BugzillaAttribute element, String name) {
		if (itemsByNamedAttribute.get(element) != null) {
			return itemsByNamedAttribute.get(element).get(name);
		}
		return null;
	}

	public void addItem(BugzillaAttribute element, RepositoryConfigurationItem item) {
		Assert.isNotNull(element);
		Assert.isNotNull(item);
		if (itemsByAttribute.get(element) == null) {
			itemsByAttribute.put(element, new ArrayList<RepositoryConfigurationItem>());
		}
		itemsByAttribute.get(element).add(item);
	}

	public void addNamedItem(BugzillaAttribute element, RepositoryConfigurationItem item) {
		Assert.isNotNull(element);
		Assert.isNotNull(item);
		if (itemsByNamedAttribute.get(element) == null) {
			itemsByNamedAttribute.put(element, new HashMap<String, RepositoryConfigurationItem>());
		}
		itemsByNamedAttribute.get(element).put(item.getName(), item);
	}

	public RepositoryConfigurationItem[] getConfigurationItemsArray(BugzillaAttribute element) {
		Assert.isNotNull(element);
		return itemsByAttribute.get(element) != null ? itemsByAttribute.get(element).toArray(
				new RepositoryConfigurationItem[0]) : null;
	}

	public List<String> getConfigurationItemsAsStringList(BugzillaAttribute element) {
		Assert.isNotNull(element);
		synchronized (this) {
			List<RepositoryConfigurationItem> items = itemsByAttribute.get(element);
			if (items != null) {
				List<String> result = new ArrayList<String>(items.size());
				for (RepositoryConfigurationItem repositoryConfigurationItem : items) {
					result.add(repositoryConfigurationItem.getName());
				}
				return result;
			}
			return Collections.emptyList();
		}
	}

	public List<String> getConfigurationNamedItemsAsStringList(BugzillaAttribute element) {
		Assert.isNotNull(element);
		synchronized (this) {
			Map<String, RepositoryConfigurationItem> namedItem = itemsByNamedAttribute.get(element);
			if (namedItem != null) {
				ArrayList<String> namedItemKeyList = new ArrayList<String>(namedItem.keySet());
				Collections.sort(namedItemKeyList, String.CASE_INSENSITIVE_ORDER);
				return namedItemKeyList;
			}
			return Collections.emptyList();
		}
	}

}
