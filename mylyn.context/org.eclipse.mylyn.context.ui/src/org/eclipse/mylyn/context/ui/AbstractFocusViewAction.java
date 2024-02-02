/*******************************************************************************
 * Copyright (c) 2004, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiImages;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.Messages;
import org.eclipse.mylyn.monitor.ui.MonitorUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;

/**
 * Extending this class makes it possible to apply Mylyn management to a structured view (e.g. to provide interest-based filtering).
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public abstract class AbstractFocusViewAction extends Action
		implements IViewActionDelegate, IActionDelegate2, ISelectionListener {

	public static final String PREF_ID_PREFIX = "org.eclipse.mylyn.ui.interest.filter."; //$NON-NLS-1$

	private static Map<IViewPart, AbstractFocusViewAction> partMap = new WeakHashMap<>();

	protected String globalPrefId;

	protected IAction initAction = null;

	protected final InterestFilter interestFilter;

	protected IViewPart viewPart;

	protected Map<StructuredViewer, List<ViewerFilter>> previousFilters = new WeakHashMap<>();

	private final boolean manageViewer;

	private final boolean manageFilters;

	private final boolean manageLinking;

	private boolean wasLinkingEnabled = false;

	private boolean wasRun = false;

	private Set<String> cachedPreservedFilters;

	/**
	 * Work-around for suppressing expansion without breaking API. Will be remove post 3.0
	 */
	@Deprecated
	protected boolean internalSuppressExpandAll = false;

	/**
	 * @since 3.0
	 */
	protected boolean showEmptyViewMessage = false;

	private final Map<StructuredViewer, EmptyContextDrawer> viewerToDrawerMap = new HashMap<>();

	private class EmptyContextDrawer implements Listener {

		private final String LABEL = getEmptyViewMessage();

		private final Image IMAGE = CommonImages.getImage(ContextUiImages.CONTEXT_FOCUS);

		private final Tree tree;

		EmptyContextDrawer(Tree tree) {
			this.tree = tree;
		}

		@Override
		public void handleEvent(Event event) {
			if (tree != null && tree.getItemCount() == 0) {
				switch (event.type) {
					case SWT.Paint: {
						int offset = 7;
						event.gc.drawImage(IMAGE, offset, offset);
						event.gc.drawText(LABEL, offset + IMAGE.getBounds().width + 5, offset);
						break;
					}
				}
			}
		}
	}

	private final AbstractContextListener CONTEXT_LISTENER = new AbstractContextListener() {

		@Override
		public void contextChanged(ContextChangeEvent event) {
			switch (event.getEventKind()) {
				case ACTIVATED:
					if (updateEnablementWithContextActivation()) {
						updateEnablement(initAction);
					}
					break;
				case DEACTIVATED:
					if (updateEnablementWithContextActivation()) {
						updateEnablement(initAction);
						update(false);
					}
					break;
			}
		};
	};

	private final IWorkbenchListener WORKBENCH_LISTENER = new IWorkbenchListener() {

		@Override
		public boolean preShutdown(IWorkbench workbench, boolean forced) {
			// restore the viewers' previous state
			if (wasRun && manageLinking) {
				setDefaultLinkingEnabled(wasLinkingEnabled);
			}

			List<StructuredViewer> viewers = getViewers();
			Set<String> excludedFilters = getPreservedFilterClasses(false);
			for (StructuredViewer viewer : viewers) {
				if (previousFilters.containsKey(viewer)) {
					if (!viewer.getControl().isDisposed()) {
						for (ViewerFilter filter : previousFilters.get(viewer)) {
							if (!excludedFilters.contains(filter.getClass().getName())) {
								try {
									viewer.addFilter(filter);
								} catch (Throwable t) {
									StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
											"Failed to restore filter: " + filter, t)); //$NON-NLS-1$
								}
							}
						}
					}
					previousFilters.remove(viewer);
				}
			}
			return true;
		}

		@Override
		public void postShutdown(IWorkbench workbench) {
			// ignore
		}
	};

	/**
	 * TODO: not thread safe.
	 */
	public static AbstractFocusViewAction getActionForPart(IViewPart part) {
		return partMap.get(part);
	}

	public IViewPart getPartForAction() {
		if (viewPart == null) {
			if (this instanceof IWorkbenchWindowActionDelegate) {
				if (!PlatformUI.getWorkbench().isClosing()) {
					// ignore, could be called from the Configure Perspective dialog, see bug 2308532
				}
			} else {
				throw new RuntimeException("error: viewPart is null"); //$NON-NLS-1$
			}
		}
		return viewPart;
	}

	public AbstractFocusViewAction(InterestFilter interestFilter, boolean manageViewer, boolean manageFilters,
			boolean manageLinking) {
		this.interestFilter = interestFilter;
		this.manageViewer = manageViewer;
		this.manageFilters = manageFilters;
		this.manageLinking = manageLinking;
		setText(Messages.AbstractFocusViewAction_Apply_Mylyn);
		setToolTipText(Messages.AbstractFocusViewAction_Apply_Mylyn);
		setImageDescriptor(ContextUiImages.CONTEXT_FOCUS);
		PlatformUI.getWorkbench().addWorkbenchListener(WORKBENCH_LISTENER);
		ContextCore.getContextManager().addListener(CONTEXT_LISTENER);
	}

	@Override
	public void dispose() {
		partMap.remove(getPartForAction());
		if (viewPart != null && !PlatformUI.getWorkbench().isClosing()) {
			for (StructuredViewer viewer : getViewers()) {
				ContextUiPlugin.getViewerManager().removeManagedViewer(viewer, viewPart);
			}
		}

		MonitorUi.removeWindowPostSelectionListener(this);
		ContextCore.getContextManager().removeListener(CONTEXT_LISTENER);
		PlatformUI.getWorkbench().removeWorkbenchListener(WORKBENCH_LISTENER);
	}

	@Override
	public void init(IAction action) {
		initAction = action;
		initAction.setChecked(action.isChecked());
	}

	@Override
	public void init(IViewPart view) {
		String id = view.getSite().getId();
		globalPrefId = PREF_ID_PREFIX + id;
		viewPart = view;
		partMap.put(view, this);
		wasLinkingEnabled = isDefaultLinkingEnabled();
	}

	protected boolean updateEnablementWithContextActivation() {
		return true;
	}

	@Override
	public void run(IAction action) {
		setChecked(action.isChecked());
		valueChanged(action, action.isChecked(), true);
		wasRun = true;
	}

	@Override
	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

	/**
	 * Don't update if the preference has not been initialized.
	 */
	public void update() {
		if (globalPrefId != null) {
			update(ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(globalPrefId));
		}
	}

	/**
	 * This operation is expensive.
	 */
	public void update(boolean on) {
		valueChanged(initAction, on, false);
		updateEnablement(initAction);
	}

	protected void valueChanged(IAction action, final boolean on, boolean store) {
		if (PlatformUI.getWorkbench().isClosing()) {
			return;
		}

		boolean wasPaused = ContextCore.getContextManager().isContextCapturePaused();
		try {
			if (!wasPaused) {
				ContextCore.getContextManager().setContextCapturePaused(true);
			}
			setChecked(on);
			if (action != null) {
				action.setChecked(on);
			}
			if (store && ContextCorePlugin.getDefault() != null) {
				ContextUiPlugin.getDefault().getPreferenceStore().setValue(globalPrefId, on);
			}

			List<StructuredViewer> viewers = getViewers();
			for (StructuredViewer viewer : viewers) {
				if (viewPart != null && !viewer.getControl().isDisposed() && manageViewer) {
					ContextUiPlugin.getViewerManager().addManagedViewer(viewer, viewPart);
				}
				updateInterestFilter(on, viewer);
			}

			setManualFilteringAndLinkingEnabled(!on);
			if (manageLinking) {
				updateLinking(on);
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
					"Could not install viewer manager on: " + globalPrefId, t)); //$NON-NLS-1$
		} finally {
			if (!wasPaused) {
				ContextCore.getContextManager().setContextCapturePaused(false);
			}
		}
	}

	protected void updateEnablement(IAction action) {
		if (updateEnablementWithContextActivation()) {
			if (action != null) {
				action.setEnabled(ContextCore.getContextManager().isContextActivePropertySet());
			}
		}
	}

	private void updateLinking(boolean on) {
		if (on) {
			wasLinkingEnabled = isDefaultLinkingEnabled();
			MonitorUi.addWindowPostSelectionListener(this);
		} else {
			MonitorUi.removeWindowPostSelectionListener(this);
			setDefaultLinkingEnabled(wasLinkingEnabled);
		}
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (manageLinking && selection instanceof ITextSelection && part instanceof IEditorPart) {
			try {
				List<StructuredViewer> viewers = getViewers();
				if (viewers.size() == 1) {
					StructuredViewer viewer = getViewers().get(0);
					ITextSelection textSelection = (ITextSelection) selection;
					ISelection toSelect = resolveSelection((IEditorPart) part, textSelection, viewer);
					if (toSelect != null) {
						ISelection currentSelection = viewer.getSelection();
						if (!toSelect.equals(currentSelection)) {
							select(viewer, toSelect);
						}
					}
					// NOTE: if we could make the code below work we could set the selection the first time the elment is shown
//					boolean canSelectElement = true;
//					if (viewer instanceof TreeViewer) {
//						TreeViewer treeViewer = (TreeViewer) viewer;
//						Object[] expanded = treeViewer.getExpandedElements();
//						canSelectElement = false;
//						if (Arrays.asList(expanded).contains(((StructuredSelection) toSelect).getFirstElement())) {
//							canSelectElement = true;
//						}
//					}
				}
			} catch (Throwable t) {
				// ignore, linking failure is not fatal
			}
		}
	}

	protected void select(StructuredViewer viewer, ISelection selection) {
		viewer.setSelection(selection, true);
	}

	/**
	 * Override to provide managed linking
	 */
	protected ISelection resolveSelection(IEditorPart part, ITextSelection selection, StructuredViewer viewer)
			throws CoreException {
		return null;
	}

	/**
	 * Override to provide managed linking
	 */
	protected void setDefaultLinkingEnabled(boolean on) {
		// ignore
	}

	/**
	 * Override to provide managed linking
	 */
	protected boolean isDefaultLinkingEnabled() {
		return false;
	}

	protected void setManualFilteringAndLinkingEnabled(boolean on) {
		// ignore
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		updateEnablement(action);
	}

	/**
	 * Public for testing
	 */
	public void updateInterestFilter(final boolean on, StructuredViewer viewer) {
		if (viewer != null) {
			if (on) {
				if (showEmptyViewMessage && viewer instanceof TreeViewer) {
					Tree tree = ((TreeViewer) viewer).getTree();
					Listener drawingListener = viewerToDrawerMap.get(viewer);
					if (drawingListener == null && !tree.isDisposed()) {
						EmptyContextDrawer drawer = new EmptyContextDrawer(tree);
						viewerToDrawerMap.put(viewer, drawer);
						tree.addListener(SWT.Paint, drawer);
					}
				}

				installInterestFilter(viewer);
				ContextUiPlugin.getViewerManager().addFilteredViewer(viewer, this);
			} else {
				if (showEmptyViewMessage && viewer instanceof TreeViewer) {
					Tree tree = ((TreeViewer) viewer).getTree();
					EmptyContextDrawer drawer = viewerToDrawerMap.remove(viewer);
					if (drawer != null && !tree.isDisposed()) {
						tree.removeListener(SWT.Paint, drawer);
					}
				}
				ContextUiPlugin.getViewerManager().removeFilteredViewer(viewer);
				uninstallInterestFilter(viewer);
			}
		}
	}

	/**
	 * Public for testing
	 */
	public abstract List<StructuredViewer> getViewers();

	/**
	 * @return filters that should not be removed when the interest filter is installed
	 */
	private Set<String> getPreservedFilterClasses(boolean cacheFilters) {
		if (ContextUiPlugin.getDefault() == null || viewPart == null) {
			return Collections.emptySet();
		}
		if (cachedPreservedFilters == null && cacheFilters) {
			try {
				cachedPreservedFilters = ContextUiPlugin.getDefault()
						.getPreservedFilterClasses(viewPart.getSite().getId());
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
						"Could not determine preserved filters", e)); //$NON-NLS-1$
			}
		}

		if (cachedPreservedFilters != null) {
			return cachedPreservedFilters;
		} else {
			// fall back for if the preserved filters have never been cached or there was a problem getting them from context core
			return Collections.emptySet();
		}
	}

	protected boolean installInterestFilter(StructuredViewer viewer) {
		if (viewer == null) {
			// FIXME Assert.isNotNull(viewer)
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
					"The viewer to install interest filter is null", new Exception())); //$NON-NLS-1$
			return false;
		} else if (viewer.getControl().isDisposed() && manageViewer) {
			// TODO: do this with part listener, not lazily?
			return false;
		} else if (previousFilters.containsKey(viewer) && hasInterestFilter(viewer)) {
			// install has already run, this can happen if AbstractAutoFocusViewAction.init() executes
			// initialization asynchronously
			return false;
		}

		try {
			viewer.getControl().setRedraw(false);
			previousFilters.put(viewer, Arrays.asList(viewer.getFilters()));

			if (viewPart != null && manageFilters) {
				Set<ViewerFilter> toAdd = new HashSet<>();
				Set<String> preservedFilterClasses = getPreservedFilterClasses(true);

				for (ViewerFilter filter : previousFilters.get(viewer)) {
					if (preservedFilterClasses.contains(filter.getClass().getName()) || isPreservedFilter(filter)) {
						toAdd.add(filter);
					}
				}

				toAdd.add(interestFilter);
				viewer.setFilters(toAdd.toArray(new ViewerFilter[toAdd.size()]));
			} else {
				viewer.addFilter(interestFilter);
			}

			if (viewer instanceof TreeViewer && !internalSuppressExpandAll) {
				((TreeViewer) viewer).expandAll();
			}
			return true;
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
					"Could not install viewer filter on: " + globalPrefId, t)); //$NON-NLS-1$
		} finally {
			viewer.getControl().setRedraw(true);
			internalSuppressExpandAll = false;
		}
		return false;
	}

	private boolean hasInterestFilter(StructuredViewer viewer) {
		for (ViewerFilter filter : viewer.getFilters()) {
			if (filter == getInterestFilter()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Subclasses can provide additional viewer filters that should not be removed when focusing.
	 * 
	 * @since 3.1
	 */
	protected boolean isPreservedFilter(ViewerFilter filter) {
		return false;
	}

	protected void uninstallInterestFilter(StructuredViewer viewer) {
		if (viewer == null) {
			// FIXME Assert.isNotNull(viewer)
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
					"The viewer to uninstall interest filter is null", new Exception())); //$NON-NLS-1$
			return;
		} else if (viewer.getControl().isDisposed()) {
			// TODO: do this with part listener, not lazily?
			ContextUiPlugin.getViewerManager().removeManagedViewer(viewer, viewPart);
			return;
		}

		try {
			viewer.getControl().setRedraw(false);

			List<ViewerFilter> restoreFilters = previousFilters.remove(viewer);
			if (restoreFilters != null && viewPart != null && manageFilters) {
				// install all previous filters and all current filters
				Set<ViewerFilter> filters = new HashSet<>(restoreFilters);
				filters.addAll(Arrays.asList(viewer.getFilters()));
				// ensure that all interest filters are removed
				for (Iterator<ViewerFilter> it = filters.iterator(); it.hasNext();) {
					if (it.next() instanceof InterestFilter) {
						it.remove();
					}
				}
				viewer.setFilters(filters.toArray(new ViewerFilter[filters.size()]));
			}
			viewer.removeFilter(interestFilter);
			interestFilter.resetTemporarilyUnfiltered();
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
					"Could not uninstall interest viewer filter on: " + globalPrefId, t)); //$NON-NLS-1$
		} finally {
			viewer.getControl().setRedraw(true);
		}
	}

	public String getGlobalPrefId() {
		return globalPrefId;
	}

	/**
	 * For testing.
	 */
	public InterestFilter getInterestFilter() {
		return interestFilter;
	}

	/**
	 * @since 3.0
	 */
	protected String getEmptyViewMessage() {
		return Messages.AbstractFocusViewAction_Empty_task_context;
	}
}
