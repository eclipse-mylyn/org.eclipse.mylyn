/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.mylyn.reviews.core.model.IReviewItem;

/**
 * @author Steffen Pingel
 */
public class ReviewItemCache {

	private final Map<String, IReviewItem> reviewItemById;

	public ReviewItemCache() {
		reviewItemById = new HashMap<String, IReviewItem>();
	}

	public IReviewItem getItem(String id) {
		return reviewItemById.get(id);
	}

	public void put(IReviewItem item) {
		reviewItemById.put(item.getId(), item);
	}

}
