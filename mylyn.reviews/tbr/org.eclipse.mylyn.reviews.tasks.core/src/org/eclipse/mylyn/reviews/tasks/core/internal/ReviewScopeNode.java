/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.tasks.core.internal;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.eclipse.mylyn.reviews.tasks.core.IReviewScopeItem;
import org.eclipse.mylyn.reviews.tasks.core.ITaskProperties;
import org.eclipse.mylyn.reviews.tasks.core.Rating;
import org.eclipse.mylyn.reviews.tasks.core.ReviewResult;
import org.eclipse.mylyn.reviews.tasks.core.ReviewScope;
/**
 * 
 * @author mattk
 *
 */
public class ReviewScopeNode extends AbstractTreeNode {

	private ReviewScope scope;
	private String description;
	private List<ReviewResult> results;

	public ReviewScopeNode(ITaskProperties task, ReviewScope scope, List<ReviewResult> results) {
		super(task);
		this.scope = scope;
		this.results = results;
		for (ReviewResult result : results) {
			addChildren(new ReviewResultNode(result));
		}
	}

	public String getDescription() {
		if (description == null) {
			description = convertScopeToDescription();
		}
		return description;
	}
	private static class Counter {
		int counter;
		IReviewScopeItem item;
		public Counter(IReviewScopeItem item) {
			this.item=item;
		}
	}
	private String convertScopeToDescription() {
		StringBuilder sb = new StringBuilder();
		Map<String, Counter> counts = new TreeMap<String, Counter>();
		for (IReviewScopeItem item : scope.getItems()) {
			String key = item.getType(1);
			if (!counts.containsKey(key)) {
				counts.put(key, new Counter(item));
			}
			counts.get(key).counter++;
		}
		boolean isFirstElement = true;
		for (Entry<String,Counter> type : counts.entrySet()) {
			if (isFirstElement) {
				isFirstElement = false;
			} else {
				sb.append(", ");
			}

			int count = type.getValue().counter;
			sb.append(count);
			sb.append(" ");
			sb.append(type.getValue().item.getType(count));
		}
		return sb.toString();
	}

	public Rating getResult() {
		Rating rating = null;
		for (ReviewResult res : results) {
			if (rating == null
					|| res.getRating().getPriority() > rating.getPriority()) {
				rating = res.getRating();
			}
		}
		return rating;
	}

	public String getPerson() {
		// TODO
		return scope.getCreator();
	}
}