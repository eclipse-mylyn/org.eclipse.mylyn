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
package org.eclipse.mylyn.reviews.tasks.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author mattk
 *
 */
public class ReviewScope {
	private List<IReviewScopeItem> items = new ArrayList<IReviewScopeItem>();
	private String creator;

	public List<IReviewScopeItem> getItems() {
		return Collections.unmodifiableList( items );
	}
	public void addScope(IReviewScopeItem item)  {
		this.items.add(item);
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator=creator;
	}

}
