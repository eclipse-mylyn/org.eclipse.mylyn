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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.reviews.core.model.ITopic;
import org.eclipse.mylyn.reviews.core.model.ITopicContainer;

/**
 * Provides a common tree hierarchy similar to Gerrit Web UI hierarchy, except that when used with ReviewsSorter, global
 * comments appear as members of a single "Global" node. Hierarchy is:
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

	public Object[] getElements(Object element) {
		if (element instanceof GlobalCommentsNode) {
			return ((GlobalCommentsNode) element).getReview().getTopics().toArray();
		} else if (element instanceof EObject) {
			List<Object> children = new ArrayList<Object>();
			if (element instanceof IReview) {
				children.add(new GlobalCommentsNode((IReview) element));
				children.addAll(((IReview) element).getItems());
				return children.toArray();
			}
			if (element instanceof IReviewItemSet) {
				IReviewItemSet itemSet = (IReviewItemSet) element;
				children.addAll(itemSet.getTopics());
				children.addAll(itemSet.getItems());
			}
			if (element instanceof ITopic) {
				ITopic topic = (ITopic) element;
				children.addAll(topic.getReplies());
			}
			if (element instanceof IFileItem) {
				IFileItem item = (IFileItem) element;
				for (ITopic topic : item.getBase().getTopics()) {
					children.add(topic);
				}
				for (ITopic topic : item.getTarget().getTopics()) {
					children.add(topic);
				}
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
			return ((IReview) element).getItems().size() > 0;
		}
		if (element instanceof GlobalCommentsNode) {
			return ((GlobalCommentsNode) element).getReview().getTopics().size() > 0;
		}
		return ((element instanceof ITopicContainer) && ((ITopicContainer) element).getAllComments().size() > 0)
				|| (element instanceof IReview && ((IReview) element).getItems().size() > 0)
				|| (element instanceof GlobalCommentsNode && hasChildren(((GlobalCommentsNode) element).getReview()) || (element instanceof IReviewItemSet && ((IReviewItemSet) element).getItems()
						.size() > 0)) || hasCollectionChildren(element);
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IReviewItem) {
			return ((IReviewItem) element).getReview();
		}
		return null;
	}
}
