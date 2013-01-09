/*******************************************************************************
 * Copyright (c) 2012 Ericsson
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.providers;

import org.apache.commons.lang.ObjectUtils;
import org.eclipse.mylyn.reviews.core.model.IReview;

/**
 * Simply represents the node used to contain global comments on the review.
 * 
 * @author Miles Parker
 */
class GlobalCommentsNode {
	private final IReview review;

	public GlobalCommentsNode(IReview review) {
		this.review = review;
	}

	public IReview getReview() {
		return review;
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof GlobalCommentsNode)
				&& ObjectUtils.equals(this.getReview(), ((GlobalCommentsNode) other).getReview());
	}

	@Override
	public int hashCode() {
		return getReview() != null ? getReview().hashCode() : 1;
	}
}
