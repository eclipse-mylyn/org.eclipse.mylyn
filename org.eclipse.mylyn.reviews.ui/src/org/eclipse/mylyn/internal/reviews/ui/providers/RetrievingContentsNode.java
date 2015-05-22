/*******************************************************************************
 * Copyright (c) 2012, 2013 Ericsson
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
		return (other instanceof RetrievingContentsNode)
				&& ObjectUtils.equals(this.parent, ((RetrievingContentsNode) other).parent);
	}

	@Override
	public int hashCode() {
		return parent != null ? parent.hashCode() : 1;
	}
}
