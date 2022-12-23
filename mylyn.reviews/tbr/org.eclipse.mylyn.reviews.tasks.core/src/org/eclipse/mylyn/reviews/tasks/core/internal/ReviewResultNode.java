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

import org.eclipse.mylyn.reviews.tasks.core.Rating;
import org.eclipse.mylyn.reviews.tasks.core.ReviewResult;
/**
 * @author mattk
 *
 */
public class ReviewResultNode extends AbstractTreeNode {

	private ReviewResult result;

	public ReviewResultNode(ReviewResult result) {
		super(null);
		this.result = result;
	}

	public String getDescription() {
		return result.getComment();
	}

	public Rating getResult() {
		return result.getRating();
	}

	public String getPerson() {
		return result.getReviewer();
	}
	

}