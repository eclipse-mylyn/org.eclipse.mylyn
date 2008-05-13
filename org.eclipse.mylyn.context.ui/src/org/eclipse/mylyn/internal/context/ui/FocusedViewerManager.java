/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * Encapsulates the element refresh and expansion state policy for all viewers focused on context.
 * 
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class FocusedViewerManager extends AbstractContextListener implements ISelectionListener {

	private final CopyOnWriteArrayList<StructuredViewer> managedViewers = new CopyOnWriteArrayList<StructuredViewer>();

	private final CopyOnWriteArrayList<StructuredViewer> filteredViewers = new CopyOnWriteArrayList<StructuredViewer>();

	private final Map<StructuredViewer, BrowseFilteredListener> listenerMap = new HashMap<StructuredViewer, BrowseFilteredListener>();

	private final Map<IWorkbenchPart, StructuredViewer> partToViewerMap = new HashMap<IWorkbenchPart, StructuredViewer>();

	/**
	 * For testing.
	 */
	private boolean syncRefreshMode = false;

	public FocusedViewerManager() {
		// NOTE: no longer using viewer part tracker due to bug 162346
//		VIEWER_PART_TRACKER.install(PlatformUI.getWorkbench());
	}

	public void dispose() {
//		VIEWER_PART_TRACKER.dispose(PlatformUI.getWorkbench());
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// ignore
	}

	public void addManagedViewer(StructuredViewer viewer, IWorkbenchPart viewPart) {
		if (viewer != null && !managedViewers.contains(viewer)) {
			managedViewers.add(viewer);
			partToViewerMap.put(viewPart, viewer);
			BrowseFilteredListener listener = new BrowseFilteredListener(viewer);
			listenerMap.put(viewer, listener);
			viewer.getControl().addMouseListener(listener);
			viewer.getControl().addKeyListener(listener);
		}
	}

	public void removeManagedViewer(StructuredViewer viewer, IWorkbenchPart viewPart) {
		managedViewers.remove(viewer);
		partToViewerMap.remove(viewPart);
		BrowseFilteredListener listener = listenerMap.get(viewer);
		if (listener != null && viewer != null && !viewer.getControl().isDisposed()) {
			viewer.getControl().removeMouseListener(listener);
			viewer.getControl().removeKeyListener(listener);
		}
	}

	public void addFilteredViewer(StructuredViewer viewer) {
		if (viewer != null && !filteredViewers.contains(viewer)) {
			filteredViewers.add(viewer);
		}
	}

	public void removeFilteredViewer(StructuredViewer viewer) {
		filteredViewers.remove(viewer);
	}

	@Override
	public void contextActivated(IInteractionContext context) {
		refreshViewers();
	}

	@Override
	public void contextDeactivated(IInteractionContext context) {
		refreshViewers();
		for (StructuredViewer structuredViewer : managedViewers) {
			if (structuredViewer instanceof TreeViewer) {
				((TreeViewer) structuredViewer).collapseAll();
			}
		}
	}

	@Override
	public void contextCleared(IInteractionContext context) {
		contextDeactivated(context);
	}

	protected void refreshViewers() {
		List<IInteractionElement> toRefresh = Collections.emptyList();
		refreshViewers(toRefresh, true);
	}

	protected void refreshViewers(IInteractionElement node, boolean updateLabels) {
		List<IInteractionElement> toRefresh = new ArrayList<IInteractionElement>();
		toRefresh.add(node);
		refreshViewers(toRefresh, updateLabels);
	}

	@Override
	public void interestChanged(final List<IInteractionElement> nodes) {
		refreshViewers(nodes, false);
	}

	protected void refreshViewers(final List<IInteractionElement> nodesToRefresh, final boolean updateLabels) {
		if (nodesToRefresh == null) {
			return;
		} else {
			if (syncRefreshMode) {
				internalRefresh(new HashSet<IInteractionElement>(nodesToRefresh), updateLabels);
			} else {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						internalRefresh(new HashSet<IInteractionElement>(nodesToRefresh), updateLabels);
					}
				});
			}
		}
	}

	private void internalRefresh(final Set<IInteractionElement> nodesToRefresh, final boolean updateLabels) {
		try {
			for (StructuredViewer viewer : managedViewers) {
				refreshViewer(nodesToRefresh, updateLabels, viewer);
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Could not refresh viewer", t));
		}
	}

	private void refreshViewer(final Set<IInteractionElement> nodesToRefresh, final boolean minor,
			StructuredViewer viewer) {
		if (viewer == null) {
			return;
		} else if (viewer.getControl().isDisposed()) {
			managedViewers.remove(viewer);
		} else {
			if (nodesToRefresh == null || nodesToRefresh.isEmpty()) {
				if (!minor) {
					viewer.refresh(false);
					updateExpansionState(viewer, null);
				} else {
					try {
						viewer.getControl().setRedraw(false);
						viewer.refresh(true);
						updateExpansionState(viewer, null);
					} finally {
						viewer.getControl().setRedraw(true);
					}
				}
			} else {
				if (filteredViewers.contains(viewer)) {
					try {
						viewer.getControl().setRedraw(false);
						viewer.refresh(minor);
						updateExpansionState(viewer, null);
					} finally {
						viewer.getControl().setRedraw(true);
					}
				} else { // don't need to worry about content changes
					try {
						viewer.getControl().setRedraw(false);
						for (IInteractionElement node : nodesToRefresh) {
							AbstractContextStructureBridge structureBridge = ContextCorePlugin.getDefault()
									.getStructureBridge(node.getContentType());
							Object objectToRefresh = structureBridge.getObjectForHandle(node.getHandleIdentifier());
							if (objectToRefresh != null) {
								viewer.update(objectToRefresh, null);
								updateExpansionState(viewer, objectToRefresh);
							}
						}
					} finally {
						viewer.getControl().setRedraw(true);
					}
				}
			}
		}
	}

	private void updateExpansionState(StructuredViewer viewer, Object objectToRefresh) {
		if (viewer instanceof TreeViewer
				&& filteredViewers.contains(viewer)
				&& ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
						ContextUiPrefContstants.AUTO_MANAGE_EXPANSION)) {
			TreeViewer treeViewer = (TreeViewer) viewer;
			if (objectToRefresh == null) {
				treeViewer.expandAll();
			} else {
				treeViewer.expandToLevel(objectToRefresh, AbstractTreeViewer.ALL_LEVELS);
			}
		}
	}

	/**
	 * TODO: consider making this work per-element and parent
	 */
	@Override
	public void elementsDeleted(List<IInteractionElement> elements) {
		for (IInteractionElement interactionElement : elements) {
			AbstractContextStructureBridge structureBridge = ContextCore.getStructureBridge(interactionElement.getContentType());
			IInteractionElement parent = ContextCore.getContextManager().getElement(
					structureBridge.getParentHandle(interactionElement.getHandleIdentifier()));

			if (parent != null) {
				ArrayList<IInteractionElement> toRefresh = new ArrayList<IInteractionElement>();
				toRefresh.add(parent);
				refreshViewers(toRefresh, false);
			}
		}
	}

	@Override
	public void landmarkAdded(IInteractionElement node) {
		refreshViewers(node, true);
	}

	@Override
	public void landmarkRemoved(IInteractionElement node) {
		refreshViewers(node, true);
	}

	/**
	 * Set to true for testing
	 */
	public void setSyncRefreshMode(boolean syncRefreshMode) {
		this.syncRefreshMode = syncRefreshMode;
	}

	@Override
	public void contextPreActivated(IInteractionContext context) {
		// ignore

	}

	public void forceReferesh() {
		refreshViewers();
	}

}
