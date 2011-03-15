/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tasks.dsl.internal;

import org.eclipse.mylyn.reviews.tasks.dsl.IReviewDslSerializer;
import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslResult;
import org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslScope;

/**
 * 
 * @author mattk
 *
 */
public class ReviewDslSerializer implements IReviewDslSerializer {
	
	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.reviews.tasks.dsl.internal.IReviewDslSerializer#serialize(org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslScope)
	 */
	public String serialize(ReviewDslScope scope) {
		return scope.serialize(new StringBuilder()).toString();
	}
	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.reviews.tasks.dsl.internal.IReviewDslSerializer#serialize(org.eclipse.mylyn.reviews.tasks.dsl.ReviewDslResult)
	 */
	public String serialize(ReviewDslResult result) {
		return result.serialize(new StringBuilder()).toString();
	}
}
