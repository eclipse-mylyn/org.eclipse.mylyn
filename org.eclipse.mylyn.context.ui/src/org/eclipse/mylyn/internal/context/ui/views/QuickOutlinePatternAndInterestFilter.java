/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.views;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.context.ui.InterestFilter;
import org.eclipse.ui.internal.misc.StringMatcher;

/**
 * Derived from {@link QuickOutlinePatternAndInterestFilter}
 * 
 * @author Mik Kersten
 */
public class QuickOutlinePatternAndInterestFilter extends ViewerFilter {

	private final InterestFilter interestFilter = new InterestFilter();

	private StringMatcher stringMatcher;

	public QuickOutlinePatternAndInterestFilter() {
		stringMatcher = null;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		boolean isInteresting = interestFilter.select(viewer, parentElement, element);
		if (!isInteresting) {
			return false;
		} else {
			// Element passes the filter if the string matcher is undefined or
			// the
			// viewer is not a tree viewer
			if ((stringMatcher == null) || ((viewer instanceof TreeViewer) == false)) {
				return true;
			}
			TreeViewer treeViewer = (TreeViewer) viewer;
			// Match the pattern against the label of the given element
			String matchName = ((ILabelProvider) treeViewer.getLabelProvider()).getText(element);
			// Element passes the filter if it matches the pattern
			if ((matchName != null) && stringMatcher.match(matchName)) {
				return true;
			}
			// Determine whether the element has children that pass the filter
			return hasUnfilteredChild(treeViewer, element);
		}
	}

	/**
	 * @param viewer
	 * @param element
	 * @return
	 */
	private boolean hasUnfilteredChild(TreeViewer viewer, Object element) {
		// No point calling hasChildren() because the operation is the same cost
		// as getting the children
		// If the element has a child that passes the filter, then we want to
		// keep the parent around - even if it does not pass the filter itself
		Object[] children = ((ITreeContentProvider) viewer.getContentProvider()).getChildren(element);
		for (Object element2 : children) {
			if (select(viewer, element, element2)) {
				return true;
			}
		}
		// Element does not pass the filter
		return false;
	}

	public void setStringMatcher(StringMatcher stringMatcher) {
		this.stringMatcher = stringMatcher;
	}

}
