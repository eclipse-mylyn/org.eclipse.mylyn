/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Christian Dupuis - bug 193003
 *     David Green - fix for bug 261446
 *******************************************************************************/

package org.eclipse.mylyn.context.ui;

import java.util.HashSet;
import java.util.Set;

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
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.CompositeContextElement;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.internal.WorkingSet;

/**
 * A generic degree-of-interest viewer filter that can be applied to any StructuredViewer. It figures out whether an object is interesting
 * by getting it's handle from the corresponding structure bridge.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public class InterestFilter extends ViewerFilter {

	private Set<Object> temporarilyUnfiltered = null;

	private Object lastTemporarilyUnfiltered = null;

	private IInteractionContext context;

	public InterestFilter() {
		// ignore
	}

	/**
	 * @since 3.6
	 */
	public InterestFilter(IInteractionContext context) {
		this.context = context;

	}

	@Override
	public boolean select(Viewer viewer, Object parent, Object object) {
		try {
			if (!(viewer instanceof StructuredViewer)) {// || !containsMylarInterestFilter((StructuredViewer) viewer)) {
				return true;
			}
			if (isTemporarilyUnfiltered(parent)) {
				return true;
			} else if (temporarilyUnfiltered != null && temporarilyUnfiltered.contains(Tree.class)
					&& (isRootElement(object) || isRootElement(viewer, parent, object))) {
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
					if (context == null) {
						element = ContextCore.getContextManager().getElement(handle);
					} else {
						element = context.get(handle);
					}

					// if we can't find the element, check the parent bridge
					if (element == null || element instanceof CompositeContextElement
							&& ((CompositeContextElement) element).getNodes().isEmpty()) {
						String parentContentType = bridge.getParentContentType();
						AbstractContextStructureBridge parentBridge = ContextCore.getStructureBridge(parentContentType);
						if (parentBridge != null) {
							String parentHandle = parentBridge.getHandleIdentifier(object);
							IInteractionElement parentElement;
							if (context == null) {
								parentElement = ContextCore.getContextManager().getElement(parentHandle);
							} else {
								parentElement = context.get(parentHandle);
							}
							if (parentElement != null && isInteresting(parentElement)) {
								// do a sanity check to make sure that we are trying to display the element
								// and not some other representation
								// If this is removed, you can see the undesired behavior of the parent default
								// packages showing up in the package explorer
								Object objectForHandle = parentBridge.getObjectForHandle(parentHandle);
								return objectForHandle != null && objectForHandle.equals(object);
							}
						}
					}

				} else {
					return true;
				}
			}
			if (element != null) {
				return isInteresting(element);
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Interest filter failed on viewer: " //$NON-NLS-1$
					+ viewer.getClass(), t));
		}
		return false;
	}

	private boolean isRootElement(Viewer viewer, Object parent, Object object) {
		if (viewer.getInput() == parent) {
			return true;
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

	/**
	 * @since 3.5
	 */
	public boolean isTemporarilyUnfiltered(Object parent) {
		if (parent instanceof TreePath treePath) {
			parent = treePath.getLastSegment();
		}
		return temporarilyUnfiltered != null && temporarilyUnfiltered.contains(parent);

	}

	@Deprecated
	public void setTemporarilyUnfiltered(Object temporarilyUnfiltered) {
		addTemporarilyUnfiltered(temporarilyUnfiltered);
	}

	/**
	 * @since 3.5
	 */
	public void addTemporarilyUnfiltered(Object temporarilyUnfilteredObject) {
		if (temporarilyUnfiltered == null) {
			temporarilyUnfiltered = new HashSet<>();
		}
		if (temporarilyUnfilteredObject instanceof Tree) {
			temporarilyUnfiltered.add(Tree.class);
		} else {
			// make sure to remove the tree so that we dont have weird performance issues
			temporarilyUnfiltered.remove(Tree.class);
			temporarilyUnfiltered.add(temporarilyUnfilteredObject);
		}
		lastTemporarilyUnfiltered = temporarilyUnfilteredObject;
	}

	/**
	 * @return true if there was an unfiltered node
	 */
	public boolean resetTemporarilyUnfiltered() {
		if (temporarilyUnfiltered != null || lastTemporarilyUnfiltered != null) {
			temporarilyUnfiltered = null;
			lastTemporarilyUnfiltered = null;
			return true;
		} else {
			return false;
		}
	}

	/**
	 * @since 3.5
	 */
	public Object getLastTemporarilyUnfiltered() {
		return lastTemporarilyUnfiltered;
	}

	@Deprecated
	public Object getTemporarilyUnfiltered() {
		return getLastTemporarilyUnfiltered();
	}

}
