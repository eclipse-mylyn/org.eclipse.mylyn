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
package org.eclipse.mylyn.reviews.tasks.dsl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author mattk
 *
 */
public class ReviewDslScope {
	List<ReviewDslScopeItem> items = new ArrayList<ReviewDslScopeItem>();
	public void addItem(ReviewDslScopeItem item) {
		items.add(item);
	}

	public List<ReviewDslScopeItem> getItems() {
		return Collections.unmodifiableList(items);
	}

	public StringBuilder serialize(StringBuilder sb) {
		sb.append("Review scope:");
		for(ReviewDslScopeItem item : items) {
			item.serialize(sb);
			sb.append("\n");
		}
		return sb;
	}
}
