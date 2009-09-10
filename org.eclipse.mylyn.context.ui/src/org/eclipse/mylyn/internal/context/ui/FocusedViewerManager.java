/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
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
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.DelayedRefreshJob;
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

	private final Map<StructuredViewer, FocusedViewerDelayedRefreshJob> fullRefreshJobs = new HashMap<StructuredViewer, FocusedViewerDelayedRefreshJob>();

	// TODO: consider merging in order to discard minors when majors come in, see bug 209846
	private final Map<StructuredViewer, FocusedViewerDelayedRefreshJob> minorRefreshJobs = new HashMap<StructuredViewer, FocusedViewerDelayedRefreshJob>();

	private class FocusedViewerDelayedRefreshJob extends DelayedRefreshJob {

		private boolean minor = false;

		private boolean updateExpansion;

		public FocusedViewerDelayedRefreshJob(StructuredViewer viewer, String name, boolean minor) {
			super(viewer, name);
			this.minor = minor;

		}

		@Override
		protected void doRefresh(Object[] items) {
			try {
				if (viewer == null) {
					return;
				} else if (viewer.getControl().isDisposed()) {
					managedViewers.remove(viewer);
				} else {
					if (items == null || items.length == 0) {
						if (!minor) {
							viewer.refresh(false);
							if (updateExpansion) {
								FocusedViewerManager.this.updateExpansionState(viewer, null);
							}
						} else {
							try {
								viewer.getControl().setRedraw(false);
								viewer.refresh(true);
								if (updateExpansion) {
									FocusedViewerManager.this.updateExpansionState(viewer, null);
								}
							} finally {
								viewer.getControl().setRedraw(true);
							}
						}
					} else {
						if (filteredViewers.contains(viewer)) {
							try {
								viewer.getControl().setRedraw(false);
								viewer.refresh(minor);
								if (updateExpansion) {
									FocusedViewerManager.this.updateExpansionState(viewer, null);
								}
							} finally {
								viewer.getControl().setRedraw(true);
							}
						} else { // don't need to worry about content changes
							try {
								viewer.getControl().setRedraw(false);
								for (Object item : items) {
									Object objectToRefresh = item;
									if (item instanceof IInteractionElement) {
										IInteractionElement node = (IInteractionElement) item;
										AbstractContextStructureBridge structureBridge = ContextCorePlugin.getDefault()
												.getStructureBridge(node.getContentType());
										objectToRefresh = structureBridge.getObjectForHandle(node.getHandleIdentifier());
									}
									if (objectToRefresh != null) {
										viewer.update(objectToRefresh, null);
										if (updateExpansion) {
											FocusedViewerManager.this.updateExpansionState(viewer, objectToRefresh);
										}
									}
								}
							} finally {
								viewer.getControl().setRedraw(true);
							}
						}
					}
				}
			} finally {
				updateExpansion = false;
			}

		}

		public void refreshElements(Object[] elements, boolean updateExpansion) {
			this.updateExpansion |= updateExpansion;
			super.refreshElements(elements);
		}

		@Override
		public void refreshElements(Object[] elements) {
			refreshElements(elements, true);
		}
	}

	/**
	 * For testing.
	 */
	private boolean syncRefreshMode = false;

//	private boolean internalExpandExceptionLogged;

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

			try {
				// NOTE: this needs to be done because some views (e.g. Project Explorer) are not
				// correctly initialized on startup and do not have the dummy selection event
				// sent to them.  See PartPluginAction and bug 213545.
				// TODO consider a mechanism to identify only views that provide focus
				UiUtil.initializeViewerSelection(viewPart);
				Set<IInteractionElement> emptySet = Collections.emptySet();
				refreshViewer(emptySet, true, viewer, true);
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
						"Could not initialize focused viewer", e)); //$NON-NLS-1$
			}
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
	public void contextChanged(ContextChangeEvent event) {
		switch (event.getEventKind()) {
		case ACTIVATED:
			refreshViewers();
			break;
		case DEACTIVATED:
			refreshViewers();
			for (StructuredViewer structuredViewer : managedViewers) {
				if (structuredViewer instanceof TreeViewer) {
					((TreeViewer) structuredViewer).collapseAll();
				}
			}
			break;
		case CLEARED:
			if (event.isActiveContext()) {
				// ensure we dont refresh the viewers if a context other than the active one is deleted or cleared
				// bug #265688
				refreshViewers();
				for (StructuredViewer structuredViewer : managedViewers) {
					if (structuredViewer instanceof TreeViewer) {
						((TreeViewer) structuredViewer).collapseAll();
					}
				}
			}
			break;
		case INTEREST_CHANGED:
			if (event.isActiveContext()) {
				refreshViewers(event.getElements(), false, true);
			}
			break;
		case LANDMARKS_ADDED:
			if (event.isActiveContext()) {
				refreshViewers(event.getElements(), true, false);
			}
			break;
		case LANDMARKS_REMOVED:
			if (event.isActiveContext()) {
				refreshViewers(event.getElements(), true, false);
			}
			break;
		case ELEMENTS_DELETED:
			if (event.isActiveContext()) {
				/*
				 * TODO: consider making this work per-element and parent
				 * Should we collect all parents before calling refresh?
				 */
				ArrayList<IInteractionElement> toRefresh = new ArrayList<IInteractionElement>();
				for (IInteractionElement interactionElement : event.getElements()) {
					AbstractContextStructureBridge structureBridge = ContextCore.getStructureBridge(interactionElement.getContentType());
					IInteractionElement parent = ContextCore.getContextManager().getElement(
							structureBridge.getParentHandle(interactionElement.getHandleIdentifier()));
					if (parent != null) {
						toRefresh.add(parent);
					}
				}
				refreshViewers(toRefresh, false, false);
			}
			break;
		}
	}

	protected void refreshViewers() {
		List<IInteractionElement> toRefresh = Collections.emptyList();
		refreshViewers(toRefresh, true, true);
	}

	@Deprecated
	protected void refreshViewers(IInteractionElement node, boolean updateLabels) {
		refreshViewers(node, updateLabels, true);
	}

	protected void refreshViewers(IInteractionElement node, boolean updateLabels, boolean updateExpansion) {
		List<IInteractionElement> toRefresh = new ArrayList<IInteractionElement>();
		toRefresh.add(node);
		refreshViewers(toRefresh, updateLabels, updateExpansion);
	}

	@Deprecated
	protected void refreshViewers(final List<IInteractionElement> nodesToRefresh, final boolean updateLabels) {
		refreshViewers(nodesToRefresh, updateLabels, true);
	}

	protected void refreshViewers(final List<IInteractionElement> nodesToRefresh, final boolean updateLabels,
			final boolean updateExpansion) {
		// TODO replace by Assert.isNotNull(nodesToRefresh);
		if (nodesToRefresh == null) {
			return;
		}

		if (syncRefreshMode) {
			internalRefresh(new HashSet<IInteractionElement>(nodesToRefresh), updateLabels, updateExpansion);
		} else {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					internalRefresh(new HashSet<IInteractionElement>(nodesToRefresh), updateLabels, updateExpansion);
				}
			});
		}
	}

	private void internalRefresh(final Set<IInteractionElement> nodesToRefresh, final boolean updateLabels,
			final boolean updateExpansion) {
		try {
			for (StructuredViewer viewer : managedViewers) {
				refreshViewer(nodesToRefresh, updateLabels, viewer, updateExpansion);
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Could not refresh viewer", t)); //$NON-NLS-1$
		}
	}

	@Deprecated
	public void refreshViewer(final Set<IInteractionElement> nodesToRefresh, final boolean updateLabels,
			StructuredViewer viewer) {
		refreshViewer(nodesToRefresh, updateLabels, viewer, true);
	}

	public void refreshViewer(final Set<IInteractionElement> nodesToRefresh, final boolean updateLabels,
			StructuredViewer viewer, boolean updateExpansion) {

		Map<StructuredViewer, FocusedViewerDelayedRefreshJob> refreshJobs = null;
		if (updateLabels) {
			refreshJobs = minorRefreshJobs;
		} else {
			refreshJobs = fullRefreshJobs;
		}
		FocusedViewerDelayedRefreshJob job = refreshJobs.get(viewer);
		if (job == null) {
			job = new FocusedViewerDelayedRefreshJob(viewer, "refresh viewer", updateLabels); //$NON-NLS-1$
			refreshJobs.put(viewer, job);
		}
		job.refreshElements(nodesToRefresh.toArray(), updateExpansion);

	}

	private void updateExpansionState(StructuredViewer viewer, Object objectToRefresh) {
		if (viewer instanceof TreeViewer
				&& filteredViewers.contains(viewer)
				&& ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
						IContextUiPreferenceContstants.AUTO_MANAGE_EXPANSION)) {
			TreeViewer treeViewer = (TreeViewer) viewer;

			// HACK to fix bug 278569: [context] errors with Markers view and active Mylyn task
			if ("org.eclipse.ui.internal.views.markers.MarkersTreeViewer".equals(treeViewer.getClass() //$NON-NLS-1$
					.getCanonicalName())) {
				objectToRefresh = null;
			}

			if (objectToRefresh == null) {
				treeViewer.expandAll();
			} else {
				treeViewer.expandToLevel(objectToRefresh, AbstractTreeViewer.ALL_LEVELS);
			}
		}
	}

	/**
	 * Set to true for testing
	 */
	public void setSyncRefreshMode(boolean syncRefreshMode) {
		this.syncRefreshMode = syncRefreshMode;
	}

	public void forceRefresh() {
		refreshViewers();
	}

}
