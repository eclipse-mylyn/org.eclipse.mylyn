/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui.search;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.ui.search.JavaSearchResult;
import org.eclipse.search.ui.IQueryListener;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.search.ui.text.Match;

/**
 * COPIED FROM: org.eclipse.jdt.internal.ui.search.SearchResultUpdater
 * 
 * @author Shawn Minto
 */
@SuppressWarnings("unchecked")
public class JavaActiveSearchResultUpdater implements IElementChangedListener, IQueryListener {

	private final JavaSearchResult fResult;

	private static final int REMOVED_FLAGS = IJavaElementDelta.F_MOVED_TO | IJavaElementDelta.F_REMOVED_FROM_CLASSPATH
			| IJavaElementDelta.F_CLOSED | IJavaElementDelta.F_CONTENT;

	public JavaActiveSearchResultUpdater(JavaSearchResult result) {
		fResult = result;
		NewSearchUI.addQueryListener(this);
		JavaCore.addElementChangedListener(this);
		// TODO make this work with resources
	}

	public void elementChanged(ElementChangedEvent event) {
		// long t0= System.currentTimeMillis();
		IJavaElementDelta delta = event.getDelta();
		Set removedElements = new HashSet();
		Set potentiallyRemovedElements = new HashSet();
		collectRemoved(potentiallyRemovedElements, removedElements, delta);
		if (removedElements.size() > 0) {
			handleRemoved(removedElements);
		}
		if (potentiallyRemovedElements.size() > 0) {
			handleRemoved(potentiallyRemovedElements);
		}
	}

	private void handleRemoved(Set removedElements) {
		Object[] elements = fResult.getElements();
		for (Object element : elements) {
			if (isContainedInRemoved(removedElements, element)) {
				if (element instanceof IJavaElement) {
					IJavaElement je = (IJavaElement) element;
					if (!je.exists()) {
						Match[] matches = fResult.getMatches(element);
						for (Match matche : matches) {
							fResult.removeMatch(matche);
						}
						// XXX remove edge and element
					}
				} else if (element instanceof IResource) {
					IResource resource = (IResource) element;
					if (!resource.exists()) {
						Match[] matches = fResult.getMatches(element);
						for (Match matche : matches) {
							fResult.removeMatch(matche);
						}
						// XXX remove edge and element
					}

				}
			}
		}
	}

	private boolean isContainedInRemoved(Set removedElements, Object object) {
		for (Iterator elements = removedElements.iterator(); elements.hasNext();) {
			if (isParentOf(elements.next(), object)) {
				return true;
			}
		}
		return false;
	}

	private boolean isParentOf(Object ancestor, Object descendant) {
		while (descendant != null && !ancestor.equals(descendant)) {
			descendant = getParent(descendant);
		}
		return descendant != null;
	}

	private Object getParent(Object object) {
		if (object instanceof IJavaElement) {
			return ((IJavaElement) object).getParent();
		} else if (object instanceof IResource) {
			return ((IResource) object).getParent();
		}
		return null;
	}

	private void collectRemoved(Set potentiallyRemovedSet, Set removedElements, IJavaElementDelta delta) {
		if (delta.getKind() == IJavaElementDelta.REMOVED) {
			removedElements.add(delta.getElement());
		} else if (delta.getKind() == IJavaElementDelta.CHANGED) {
			int flags = delta.getFlags();
			if ((flags & REMOVED_FLAGS) != 0) {
				potentiallyRemovedSet.add(delta.getElement());
			} else {
				IJavaElementDelta[] childDeltas = delta.getAffectedChildren();
				for (IJavaElementDelta childDelta : childDeltas) {
					collectRemoved(potentiallyRemovedSet, removedElements, childDelta);
				}
			}
		}
		IResourceDelta[] resourceDeltas = delta.getResourceDeltas();
		if (resourceDeltas != null) {
			for (IResourceDelta resourceDelta : resourceDeltas) {
				collectRemovals(removedElements, resourceDelta);
			}
		}
	}

	public void queryAdded(ISearchQuery query) {
		// don't care
	}

	public void queryRemoved(ISearchQuery query) {
		if (fResult.equals(query.getSearchResult())) {
			JavaCore.removeElementChangedListener(this);
			NewSearchUI.removeQueryListener(this);
		}
	}

	private void collectRemovals(Set removals, IResourceDelta delta) {
		if (delta.getKind() == IResourceDelta.REMOVED) {
			removals.add(delta.getResource());
		} else {
			IResourceDelta[] children = delta.getAffectedChildren();
			for (IResourceDelta element : children) {
				collectRemovals(removals, element);
			}
		}
	}

	public void queryStarting(ISearchQuery query) {
		// not interested
	}

	public void queryFinished(ISearchQuery query) {
		// not interested
	}

}
