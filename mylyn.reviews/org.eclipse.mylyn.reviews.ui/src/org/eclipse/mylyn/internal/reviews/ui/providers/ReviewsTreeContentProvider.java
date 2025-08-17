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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.mylyn.reviews.core.model.IComment;
import org.eclipse.mylyn.reviews.core.model.ICommentContainer;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;

/**
 * Provides a common tree hierarchy similar to Gerrit Web UI hierarchy, except that when used with ReviewsSorter, global comments appear as
 * members of a single "Global" node. Hierarchy is:
 * <ol>
 * <li>Global Node</li>
 * <li>-Comments</li>
 * <li>Patch Sets</li>
 * <li>-Artifacts</li>
 * <li>--Artifact Comments</li>
 * </ol>
 *
 * @see ReviewsSorter
 * @author Miles Parker
 */
public class ReviewsTreeContentProvider extends GenericTreeContentProvider {

	@Override
	public Object[] getElements(Object element) {
		if (element instanceof GlobalCommentsNode) {
			return ((GlobalCommentsNode) element).getReview().getComments().toArray();
		} else if (element instanceof EObject) {
			List<Object> children = new ArrayList<>();
			if (element instanceof IReview) {
				children.add(new GlobalCommentsNode((IReview) element));
				children.addAll(((IReview) element).getSets());
				return children.toArray();
			}
			if (element instanceof IReviewItemSet itemSet) {
				if (itemSet.getItems().size() > 0) {
					children.addAll(itemSet.getComments());
					children.addAll(itemSet.getItems());
				} else {
					children.add(new RetrievingContentsNode(itemSet));
				}
			}
			if (element instanceof IComment comment) {
				children.addAll(comment.getReplies());
			}
			if (element instanceof IFileItem item) {
				children.addAll(item.getBase().getComments());
				children.addAll(item.getTarget().getComments());
			}
			return children.toArray();
		}
		return getCollectionChildren(element);
	}

	/**
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof IReview) {
			return ((IReview) element).getSets().size() > 0;
		}
		if (element instanceof GlobalCommentsNode) {
			return ((GlobalCommentsNode) element).getReview().getComments().size() > 0;
		}
		return element instanceof ICommentContainer && ((ICommentContainer) element).getAllComments().size() > 0
				|| element instanceof IReview && ((IReview) element).getSets().size() > 0
				|| element instanceof GlobalCommentsNode && hasChildren(((GlobalCommentsNode) element).getReview())
				|| element instanceof IReviewItemSet || hasCollectionChildren(element);
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IReviewItem) {
			return ((IReviewItem) element).getReview();
		}
		return null;
	}
}
