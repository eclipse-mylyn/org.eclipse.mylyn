/*******************************************************************************
 * Copyright (c) 2012, 2013 Ericsson
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Miles Parker (Tasktop Technologies) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.reviews.ui.providers;

import org.apache.commons.lang3.ObjectUtils;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;

/**
 * A child node used to indicate that a node's children are being retrieved.
 * 
 * @author Miles Parker
 */
class RetrievingContentsNode {

	private final IReviewItemSet parent;

	public RetrievingContentsNode(IReviewItemSet parent) {
		this.parent = parent;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof RetrievingContentsNode
				&& ObjectUtils.equals(parent, ((RetrievingContentsNode) other).parent);
	}

	@Override
	public int hashCode() {
		return parent != null ? parent.hashCode() : 1;
	}
}
