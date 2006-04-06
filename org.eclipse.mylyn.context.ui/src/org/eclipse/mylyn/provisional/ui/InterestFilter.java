/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Apr 7, 2005
 */
package org.eclipse.mylar.provisional.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylar.internal.core.MylarContextManager;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.ui.MylarUiPrefContstants;
import org.eclipse.mylar.provisional.core.IMylarElement;
import org.eclipse.mylar.provisional.core.IMylarStructureBridge;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.swt.widgets.Tree;

/**
 * This is a generic interest filter that can be applied to any
 * StructuredViewer. It figures out whether an object is interesting by getting
 * it's handle from the corresponding structure bridge.
 * 
 * @author Mik Kersten
 */
public class InterestFilter extends ViewerFilter implements IPropertyChangeListener {

	private Object temporarilyUnfiltered = null;

	private String excludedMatches = null;

	public InterestFilter() {
		MylarUiPlugin.getPrefs().addPropertyChangeListener(this);
	}

	@Override
	public boolean select(Viewer viewer, Object parent, Object object) {
		try {
			if (!(viewer instanceof StructuredViewer) || 
				!containsMylarInterestFilter((StructuredViewer) viewer)) {
				return true;
			}
			if (isTemporarilyUnfiltered(parent)) {
				return true;
			} else if (temporarilyUnfiltered instanceof Tree) {
				// HACK: should also work for trees without project as root
				if (object instanceof IProjectNature || object instanceof IProject) {
					return true;
				}
//				TreeItem[] items = ((Tree)temporarilyUnfiltered).getItems();
//				((StructuredViewer)viewer).get
//				for (int i = 0; i < items.length; i++) {
//					if (items[i].getData()!= null && items[i].getData().equals(object)) {
//						return true;
//					}
//				}
			}

			IMylarElement element = null;
			if (object instanceof IMylarElement) {
				element = (IMylarElement) object;
			} else {
				IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(object);
				if (!bridge.canFilter(object)) {
					return true;
				}
				if (isImplicitlyInteresting(object, bridge)) {
					return true;
				}
				 
				if (!object.getClass().getName().equals("java.lang.Object")) {
					String handle = bridge.getHandleIdentifier(object);
					element = MylarPlugin.getContextManager().getElement(handle);
				} else {
					return true;
				}
			}
			if (element != null) {
				if (element.getInterest().isPredicted()) {
					return false;
				} else {
					return element.getInterest().getValue() > MylarContextManager.getScalingFactors().getInteresting();
				}
			}
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "interest filter failed on viewer: " + viewer.getClass(), false);
		}
		return false;
	}

	private boolean isTemporarilyUnfiltered(Object parent) {
		if (parent instanceof TreePath) {
			TreePath treePath = (TreePath)parent;
			parent = treePath.getLastSegment();
		}
		return temporarilyUnfiltered != null && temporarilyUnfiltered.equals(parent);
	}

	protected boolean isImplicitlyInteresting(Object element, IMylarStructureBridge bridge) {
		if (excludedMatches == null)
			return false;
		if (excludedMatches.equals("*"))
			return false;
		try {
			String name = bridge.getName(element);
			return name.matches(excludedMatches.replaceAll("\\*", ".*").replaceAll("\\.", "\\."));
		} catch (Throwable t) {
			return false;
		}
	}

	protected boolean containsMylarInterestFilter(StructuredViewer viewer) {
	    for (ViewerFilter filter : viewer.getFilters()) {
	        if (filter instanceof InterestFilter) {
				return true;
	        }
		} 
		return false;
	}

	public void setTemporarilyUnfiltered(Object temprarilyUnfiltered) {
		this.temporarilyUnfiltered = temprarilyUnfiltered;
	}

	/**
	 * @return	true if there was an unfiltered node
	 */
	public boolean resetTemporarilyUnfiltered() {
		if (temporarilyUnfiltered != null) {
			this.temporarilyUnfiltered = null;
			return true;
		} else {
			return false;
		}
	}

	public Object getTemporarilyUnfiltered() {
		return temporarilyUnfiltered;
	}

	public String getExcludedMatches() {
		return excludedMatches;
	}

	public void setExcludedMatches(String excludedMatches) {
		this.excludedMatches = excludedMatches;
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (MylarUiPrefContstants.INTEREST_FILTER_EXCLUSION.equals(event.getProperty())
				&& event.getNewValue() instanceof String) {

			excludedMatches = (String) event.getNewValue();
		}
	}
}


//boolean testselect(Viewer viewer, Object parent, Object element) {
//	try {
//		if (!(viewer instanceof StructuredViewer))
//			return true;
//		if (!containsMylarInterestFilter((StructuredViewer) viewer))
//			return true;
//		if (isTemporarilyUnfiltered(parent))
//			return true;
//
//		IMylarElement node = null;
//		if (element instanceof IMylarElement) {
//			node = (IMylarElement) element;
//		} else {
//			IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(element);
//			if (!bridge.canFilter(element))
//				return true;
//			if (isImplicitlyInteresting(element, bridge))
//				return true;
//
//			String handle = bridge.getHandleIdentifier(element);
//			node = MylarPlugin.getContextManager().getElement(handle);
//		}
//		if (node != null) {
//			if (node.getInterest().isPredicted()) {
//				return false;
//			} else {
//				return node.getInterest().getValue() > MylarContextManager.getScalingFactors().getInteresting();
//			}
//		}
//	} catch (Throwable t) {
//		MylarStatusHandler.log(t, "interest filter failed on viewer: " + viewer.getClass());
//	}
//	return false;
//}
