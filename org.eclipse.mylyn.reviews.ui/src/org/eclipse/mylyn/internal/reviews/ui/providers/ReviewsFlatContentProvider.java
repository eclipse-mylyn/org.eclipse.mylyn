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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.reviews.core.model.ITopicContainer;

/**
 * Flattens all contents of a review to their lowest level of detail. (In current implementations, this is comments.)
 * 
 * @author Miles Parker
 */
public class ReviewsFlatContentProvider extends GenericTreeContentProvider {

	public Object[] getElements(Object element) {
		if (element instanceof ITopicContainer) {
			List<Object> children = new ArrayList<Object>();
			children.addAll(((ITopicContainer) element).getAllComments());
			return children.toArray();
		}
		return getCollectionChildren(element);
	}

	@Override
	public boolean hasChildren(Object element) {
		return ((element instanceof ITopicContainer) && ((ITopicContainer) element).getAllComments().size() > 0)
				|| hasCollectionChildren(element);
	}
}
