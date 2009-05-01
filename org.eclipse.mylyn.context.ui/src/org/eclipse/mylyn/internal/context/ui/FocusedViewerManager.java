/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

import java.lang.reflect.Method;
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

		public FocusedViewerDelayedRefreshJob(StructuredViewer viewer, String name, boolean minor) {
			super(viewer, name);
			this.minor = minor;
		}

		@Override
		protected void doRefresh(Object[] items) {
			if (viewer == null) {
				return;
			} else if (viewer.getControl().isDisposed()) {
				managedViewers.remove(viewer);
			} else {
				if (items == null || items.length == 0) {
					if (!minor) {
						viewer.refresh(false);
						FocusedViewerManager.this.updateExpansionState(viewer, null);
					} else {
						try {
							viewer.getControl().setRedraw(false);
							viewer.refresh(true);
							FocusedViewerManager.this.updateExpansionState(viewer, null);
						} finally {
							viewer.getControl().setRedraw(true);
						}
					}
				} else {
					if (filteredViewers.contains(viewer)) {
						try {
							viewer.getControl().setRedraw(false);
							viewer.refresh(minor);
							// prior to Mylyn 3.1 used: FocusedViewerManager.this.updateExpansionState(viewer, null);
							for (Object item : items) {
								Object objectToRefresh = getObjectToRefresh(item);
								if (objectToRefresh != null) {
									FocusedViewerManager.this.updateExpansionState(viewer, objectToRefresh);
								}
							}

						} finally {
							viewer.getControl().setRedraw(true);
						}
					} else { // don't need to worry about content changes
						try {
							viewer.getControl().setRedraw(false);

							for (Object item : items) {
								Object objectToRefresh = getObjectToRefresh(item);
								if (objectToRefresh != null) {
									viewer.update(objectToRefresh, null);
									FocusedViewerManager.this.updateExpansionState(viewer, objectToRefresh);
								}
							}
						} finally {
							viewer.getControl().setRedraw(true);
						}
					}
				}
			}

		}

		private Object getObjectToRefresh(Object item) {
			Object objectToRefresh = item;
			if (item instanceof IInteractionElement) {
				IInteractionElement node = (IInteractionElement) item;
				AbstractContextStructureBridge structureBridge = ContextCorePlugin.getDefault().getStructureBridge(
						node.getContentType());
				objectToRefresh = structureBridge.getObjectForHandle(node.getHandleIdentifier());
			}
			return objectToRefresh;
		}
	}

	/**
	 * For testing.
	 */
	private boolean syncRefreshMode = false;

	private boolean internalExpandExceptionLogged;

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
				refreshViewer(emptySet, true, viewer);
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
			refreshViewers(event.getElements(), false);
			break;
		case LANDMARKS_ADDED:
			refreshViewers(event.getElements(), true);
			break;
		case LANDMARKS_REMOVED:
			refreshViewers(event.getElements(), true);
			break;
		case ELEMENTS_DELETED:
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
			refreshViewers(toRefresh, false);

			break;
		}
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

	protected void refreshViewers(final List<IInteractionElement> nodesToRefresh, final boolean updateLabels) {
		// TODO replace by Assert.isNotNull(nodesToRefresh);
		if (nodesToRefresh == null) {
			return;
		}

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

	private void internalRefresh(final Set<IInteractionElement> nodesToRefresh, final boolean updateLabels) {
		try {
			for (StructuredViewer viewer : managedViewers) {
				refreshViewer(nodesToRefresh, updateLabels, viewer);
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN, "Could not refresh viewer", t)); //$NON-NLS-1$
		}
	}

	public void refreshViewer(final Set<IInteractionElement> nodesToRefresh, final boolean updateLabels,
			StructuredViewer viewer) {

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
		job.refreshElements(nodesToRefresh.toArray());

	}

	private void updateExpansionState(StructuredViewer viewer, Object objectToRefresh) {
		if (viewer instanceof TreeViewer
				&& filteredViewers.contains(viewer)
				&& ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
						IContextUiPreferenceContstants.AUTO_MANAGE_EXPANSION)) {
			TreeViewer treeViewer = (TreeViewer) viewer;
			if (objectToRefresh == null) {
				treeViewer.expandAll();
			} else {
				treeViewer.reveal(objectToRefresh);
				boolean failed = false;
				try {
					// reveal will fail if the content provider does not properly implement getParent();
					// check if node is now visible in view and fallback to expandAll() in 
					// case of an error
					Method method = AbstractTreeViewer.class.getDeclaredMethod(
							"internalGetWidgetToSelect", Object.class); //$NON-NLS-1$
					method.setAccessible(true);
					if (method.invoke(treeViewer, objectToRefresh) == null) {
						failed = true;
					}
				} catch (Exception e) {
					if (!internalExpandExceptionLogged) {
						internalExpandExceptionLogged = true;
						StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
								"Failed to verify expansion state, falling back to expanding all nodes", e)); //$NON-NLS-1$
					}
					failed = true;
				}
				if (failed) {
					treeViewer.expandAll();
				}
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
