/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Apr 7, 2005
 */
package org.eclipse.mylyn.context.ui;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IImplicitlyIntersting;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.internal.WorkingSet;

/**
 * A generic degree-of-interest viewer filter that can be applied to any StructuredViewer. It figures out whether an
 * object is interesting by getting it's handle from the corresponding structure bridge.
 * 
 * @author Mik Kersten
 * @author Christian Dupuis (bug 193003)
 * @since 2.0
 */
public class InterestFilter extends ViewerFilter {

	private Object temporarilyUnfiltered = null;

	@Override
	public boolean select(Viewer viewer, Object parent, Object object) {
		try {
			if (!(viewer instanceof StructuredViewer)) {// || !containsMylarInterestFilter((StructuredViewer) viewer)) {
				return true;
			}
			if (isTemporarilyUnfiltered(parent)) {
				return true;
			} else if (temporarilyUnfiltered instanceof Tree && isRootElement(object)) {
				return true;
			}

			IInteractionElement element = null;
			if (object instanceof IImplicitlyIntersting) {
				return true;
			} else if (object instanceof IInteractionElement) {
				element = (IInteractionElement) object;
			} else {
				AbstractContextStructureBridge bridge = ContextCore.getStructureBridge(object);
				if (bridge.getContentType() == null) {
					// try to resolve the resource
					if (object instanceof IAdaptable) {
						Object adapted = ((IAdaptable) object).getAdapter(IResource.class);
						if (adapted instanceof IResource) {
							object = adapted;
						}
						bridge = ContextCore.getStructureBridge(object);
					} else {
						return false;
					}
				}
				if (!bridge.canFilter(object)) {
					return true;
				}

				if (!object.getClass().getName().equals(Object.class.getCanonicalName())) {
					String handle = bridge.getHandleIdentifier(object);
					element = ContextCore.getContextManager().getElement(handle);
				} else {
					return true;
				}
			}
			if (element != null) {
				return isInteresting(element);
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Interest filter failed on viewer: "
					+ viewer.getClass(), t));
		}
		return false;
	}

	protected boolean isRootElement(Object object) {
		return object instanceof IProjectNature || object instanceof IProject || object instanceof WorkingSet;
	}

	protected boolean isInteresting(IInteractionElement element) {
		// FIXME temporarily disabled, see bug 210011
		//		if (element.getInterest().isPredicted()) {
//			return false;
//		} else {
		return element.getInterest().isInteresting();
//		}
	}

	private boolean isTemporarilyUnfiltered(Object parent) {
		if (parent instanceof TreePath) {
			TreePath treePath = (TreePath) parent;
			parent = treePath.getLastSegment();
		}
		return temporarilyUnfiltered != null && temporarilyUnfiltered.equals(parent);
	}

	public void setTemporarilyUnfiltered(Object temprarilyUnfiltered) {
		this.temporarilyUnfiltered = temprarilyUnfiltered;
	}

	/**
	 * @return true if there was an unfiltered node
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

}
