/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.discovery.core.util;

import java.util.Comparator;

import org.eclipse.mylyn.internal.discovery.core.model.ConnectorCategory;
import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryConnector;
import org.eclipse.mylyn.internal.discovery.core.model.Group;

/**
 * a comparator that orders connectors by group and alphabetically by their name
 * 
 * @author David Green
 */
public class DiscoveryConnectorComparator implements Comparator<DiscoveryConnector> {

	private final ConnectorCategory category;

	public DiscoveryConnectorComparator(ConnectorCategory category) {
		if (category == null) {
			throw new IllegalArgumentException();
		}
		this.category = category;
	}

	/**
	 * compute the index of the group id
	 * 
	 * @param groupId
	 *            the group id or null
	 * @return the index, or -1 if not found
	 */
	private int computeGroupIndex(String groupId) {
		if (groupId != null) {
			int index = -1;
			for (Group group : category.getGroup()) {
				++index;
				if (group.getId().equals(groupId)) {
					return index;
				}
			}
		}
		return -1;
	}

	public int compare(DiscoveryConnector o1, DiscoveryConnector o2) {
		if (o1.getCategory() != category || o2.getCategory() != category) {
			throw new IllegalArgumentException();
		}
		if (o1 == o2) {
			return 0;
		}
		int g1 = computeGroupIndex(o1.getGroupId());
		int g2 = computeGroupIndex(o2.getGroupId());
		int i;
		if (g1 != g2) {
			if (g1 == -1) {
				i = 1;
			} else if (g2 == -1) {
				i = -1;
			} else {
				i = g1 - g2;
			}
		} else {
			i = o1.getName().compareToIgnoreCase(o2.getName());
			if (i == 0) {
				i = o1.getId().compareTo(o2.getId());
			}
		}
		return i;
	}

}
