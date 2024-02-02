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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.mylyn.reviews.core.model.ICommentContainer;

/**
 * Flattens all contents of a review to their lowest level of detail. (In current implementations, this is comments.)
 * 
 * @author Miles Parker
 */
public class ReviewsFlatContentProvider extends GenericTreeContentProvider {

	@Override
	public Object[] getElements(Object element) {
		if (element instanceof ICommentContainer) {
			List<Object> children = new ArrayList<>();
			children.addAll(((ICommentContainer) element).getAllComments());
			return children.toArray();
		}
		return getCollectionChildren(element);
	}

	@Override
	public boolean hasChildren(Object element) {
		return element instanceof ICommentContainer && ((ICommentContainer) element).getAllComments().size() > 0
				|| hasCollectionChildren(element);
	}
}
