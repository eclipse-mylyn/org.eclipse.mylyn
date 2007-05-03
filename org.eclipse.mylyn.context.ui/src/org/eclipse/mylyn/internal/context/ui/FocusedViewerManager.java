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

package org.eclipse.mylar.internal.context.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylar.context.core.AbstractContextStructureBridge;
import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.IMylarContext;
import org.eclipse.mylar.context.core.IMylarContextListener;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.context.ui.ContextUiPlugin;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;

/**
 * Encapsulates the element refresh and expansion state policy for all viewers
 * showing Mylar context.
 * 
 * @author Mik Kersten
 */
public class FocusedViewerManager implements IMylarContextListener, ISelectionListener { // ,
																							// IPropertyChangeListener
																							// {

	private List<StructuredViewer> managedViewers = new ArrayList<StructuredViewer>();

	private List<StructuredViewer> filteredViewers = new ArrayList<StructuredViewer>();

	private Map<StructuredViewer, BrowseFilteredListener> listenerMap = new HashMap<StructuredViewer, BrowseFilteredListener>();

	private Map<IWorkbenchPart, StructuredViewer> partToViewerMap = new HashMap<IWorkbenchPart, StructuredViewer>();

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
		if (!managedViewers.contains(viewer)) {
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
		if (!filteredViewers.contains(viewer)) {
			filteredViewers.add(viewer);
		}
	}

	public void removeFilteredViewer(StructuredViewer viewer) {
		filteredViewers.remove(viewer);
	}

	public void contextActivated(IMylarContext context) {
		refreshViewers();
	}

	public void contextDeactivated(IMylarContext context) {
		refreshViewers();
		for (StructuredViewer structuredViewer : managedViewers) {
			if (structuredViewer instanceof TreeViewer) {
				((TreeViewer) structuredViewer).collapseAll();
			}
		}
	}

	protected void refreshViewers() {
		List<IMylarElement> toRefresh = Collections.emptyList();
		refreshViewers(toRefresh, true);
	}

	protected void refreshViewers(IMylarElement node, boolean updateLabels) {
		List<IMylarElement> toRefresh = new ArrayList<IMylarElement>();
		toRefresh.add(node);
		refreshViewers(toRefresh, updateLabels);
	}

	public void interestChanged(final List<IMylarElement> nodes) {
		refreshViewers(nodes, false);
	}

	protected void refreshViewers(final List<IMylarElement> nodesToRefresh, final boolean updateLabels) {
		if (syncRefreshMode) {
			internalRefresh(nodesToRefresh, updateLabels);
		} else {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					internalRefresh(nodesToRefresh, updateLabels);
				}
			});
		}
	}

	private void internalRefresh(final List<IMylarElement> nodesToRefresh, final boolean updateLabels) {
		try {
			for (StructuredViewer viewer : new ArrayList<StructuredViewer>(managedViewers)) {
				refreshViewer(nodesToRefresh, updateLabels, viewer);
			}
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "could not refresh viewer", false);
		}
	}

	private void refreshViewer(final List<IMylarElement> nodesToRefresh, final boolean minor, StructuredViewer viewer) {
		if (viewer == null) {
			return;
		} else if (viewer.getControl().isDisposed()) {
			managedViewers.remove(viewer);
		} else { // if (viewer.getControl().isVisible()) {
			if (nodesToRefresh == null || nodesToRefresh.isEmpty()) {
				if (!minor) {
					viewer.refresh(false);
					updateExpansionState(viewer, null);
				} else {
					viewer.getControl().setRedraw(false);
					viewer.refresh(true);
					updateExpansionState(viewer, null);
					viewer.getControl().setRedraw(true);
				}
			} else {
				if (filteredViewers.contains(viewer)) {
					viewer.getControl().setRedraw(false);
					viewer.refresh(minor);
					updateExpansionState(viewer, null);
					viewer.getControl().setRedraw(true);
				} else { // don't need to worry about content changes
					viewer.getControl().setRedraw(false);
					for (IMylarElement node : nodesToRefresh) {
						AbstractContextStructureBridge structureBridge = ContextCorePlugin.getDefault()
								.getStructureBridge(node.getContentType());
						Object objectToRefresh = structureBridge.getObjectForHandle(node.getHandleIdentifier());
						if (objectToRefresh != null) {
							viewer.update(objectToRefresh, null);
							updateExpansionState(viewer, objectToRefresh);
						}
					}
					viewer.getControl().setRedraw(true);
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
				treeViewer.expandToLevel(objectToRefresh, TreeViewer.ALL_LEVELS);
			}
		}
	}

	public void elementDeleted(IMylarElement node) {
		AbstractContextStructureBridge structureBridge = ContextCorePlugin.getDefault().getStructureBridge(
				node.getContentType());
		IMylarElement parent = ContextCorePlugin.getContextManager().getElement(
				structureBridge.getParentHandle(node.getHandleIdentifier()));
		ArrayList<IMylarElement> toRefresh = new ArrayList<IMylarElement>();

		if (parent != null) {
			toRefresh.add(parent);
			refreshViewers(toRefresh, false);
		}
	}

	public void landmarkAdded(IMylarElement node) {
		refreshViewers(node, true);
	}

	public void landmarkRemoved(IMylarElement node) {
		refreshViewers(node, true);
	}

	public void relationsChanged(IMylarElement node) {
		// ignore
	}

	/**
	 * Set to true for testing
	 */
	public void setSyncRefreshMode(boolean syncRefreshMode) {
		this.syncRefreshMode = syncRefreshMode;
	}

}
