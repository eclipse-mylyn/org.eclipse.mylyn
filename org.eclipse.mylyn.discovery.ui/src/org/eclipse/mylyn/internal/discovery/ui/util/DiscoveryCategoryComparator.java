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
package org.eclipse.mylyn.internal.discovery.ui.util;

import java.util.Comparator;

import org.eclipse.mylyn.internal.discovery.core.model.DiscoveryCategory;

/**
 * 
 * @author David Green
 */
public class DiscoveryCategoryComparator implements Comparator<DiscoveryCategory> {

	public int compare(DiscoveryCategory o1, DiscoveryCategory o2) {
		if (o1 == o2) {
			return 0;
		}
		int i = o1.getName().compareTo(o2.getName());
		if (i == 0) {
			i = o1.getId().compareTo(o2.getId());
		}
		return i;
	}

}
