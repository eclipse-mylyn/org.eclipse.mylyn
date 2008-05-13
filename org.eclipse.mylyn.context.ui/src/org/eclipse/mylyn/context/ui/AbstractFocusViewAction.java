/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.context.ui;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
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
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiImages;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.monitor.ui.MonitorUiPlugin;
import org.eclipse.swt.widgets.Event;
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
 * Extending this class makes it possible to apply Mylyn management to a structured view (e.g. to provide interest-based
 * filtering).
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public abstract class AbstractFocusViewAction extends Action implements IViewActionDelegate, IActionDelegate2,
		ISelectionListener {

	private static final String ACTION_LABEL = "Apply Mylyn";

	public static final String PREF_ID_PREFIX = "org.eclipse.mylyn.ui.interest.filter.";

	private static Map<IViewPart, AbstractFocusViewAction> partMap = new WeakHashMap<IViewPart, AbstractFocusViewAction>();

	protected String globalPrefId;

	protected IAction initAction = null;

	protected final InterestFilter interestFilter;

	protected IViewPart viewPart;

	protected Map<StructuredViewer, List<ViewerFilter>> previousFilters = new WeakHashMap<StructuredViewer, List<ViewerFilter>>();

	private boolean manageViewer = true;

	private boolean manageFilters = true;

	private boolean manageLinking = false;

	private boolean wasLinkingEnabled = false;

	private boolean wasRun = false;

	/**
	 * API-3.0: Work-around for suppressing expansion without breaking API.
	 * 
	 * Will be remove for 3.0
	 */
	protected boolean internalSuppressExpandAll = false;

	private final AbstractContextListener CONTEXT_LISTENER = new AbstractContextListener() {

		@Override
		public void contextActivated(IInteractionContext context) {
			if (updateEnablementWithContextActivation()) {
				updateEnablement(initAction);
			}
		}

		@Override
		public void contextDeactivated(IInteractionContext context) {
			if (updateEnablementWithContextActivation()) {
				updateEnablement(initAction);
				update(false);
			}
		}
	};

	private final IWorkbenchListener WORKBENCH_LISTENER = new IWorkbenchListener() {

		public boolean preShutdown(IWorkbench workbench, boolean forced) {
			// restore the viewers' previous state
			if (wasRun && manageLinking) {
				setDefaultLinkingEnabled(wasLinkingEnabled);
			}

			List<StructuredViewer> viewers = getViewers();
			Set<Class<?>> excludedFilters = getPreservedFilterClasses();
			for (StructuredViewer viewer : viewers) {
				if (previousFilters.containsKey(viewer)) {
					for (ViewerFilter filter : previousFilters.get(viewer)) {
						if (!excludedFilters.contains(filter.getClass())) {
							try {
								viewer.addFilter(filter);
							} catch (Throwable t) {
								StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
										"Failed to restore filter: " + filter, t));
							}
						}
					}
					previousFilters.remove(viewer);
				}
			}
			return true;
		}

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
					throw new RuntimeException("not supported on IWorkbenchWindowActionDelegate");
				}
			} else {
				throw new RuntimeException("error: viewPart is null");
			}
		}
		return viewPart;
	}

	public AbstractFocusViewAction(InterestFilter interestFilter, boolean manageViewer, boolean manageFilters,
			boolean manageLinking) {
		super();
		this.interestFilter = interestFilter;
		this.manageViewer = manageViewer;
		this.manageFilters = manageFilters;
		this.manageLinking = manageLinking;
		setText(ACTION_LABEL);
		setToolTipText(ACTION_LABEL);
		setImageDescriptor(ContextUiImages.INTEREST_FILTERING);
		PlatformUI.getWorkbench().addWorkbenchListener(WORKBENCH_LISTENER);
		ContextCore.getContextManager().addListener(CONTEXT_LISTENER);
	}

	public void dispose() {
		partMap.remove(getPartForAction());
		if (viewPart != null && !PlatformUI.getWorkbench().isClosing()) {
			for (StructuredViewer viewer : getViewers()) {
				ContextUiPlugin.getViewerManager().removeManagedViewer(viewer, viewPart);
			}
		}
		MonitorUiPlugin.getDefault().removeWindowPostSelectionListener(this);
		ContextCore.getContextManager().removeListener(CONTEXT_LISTENER);
		PlatformUI.getWorkbench().removeWorkbenchListener(WORKBENCH_LISTENER);
	}

	public void init(IAction action) {
		initAction = action;
		initAction.setChecked(action.isChecked());
	}

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

	public void run(IAction action) {
		setChecked(action.isChecked());
		valueChanged(action, action.isChecked(), true);
		wasRun = true;
	}

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
			action.setChecked(on);
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
					"Could not install viewer manager on: " + globalPrefId, t));
		} finally {
			if (!wasPaused) {
				ContextCore.getContextManager().setContextCapturePaused(false);
			}
		}
	}

	protected void updateEnablement(IAction action) {
		if (updateEnablementWithContextActivation()) {
			action.setEnabled(ContextCore.getContextManager().isContextActivePropertySet());
		}
	}

	private void updateLinking(boolean on) {
		if (on) {
			wasLinkingEnabled = isDefaultLinkingEnabled();
			MonitorUiPlugin.getDefault().addWindowPostSelectionListener(this);
		} else {
			MonitorUiPlugin.getDefault().removeWindowPostSelectionListener(this);
			setDefaultLinkingEnabled(wasLinkingEnabled);
		}
	}

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
						if (!selection.equals(currentSelection)) {
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

	public void selectionChanged(IAction action, ISelection selection) {
		updateEnablement(action);
	}

	/**
	 * Public for testing
	 */
	public void updateInterestFilter(final boolean on, StructuredViewer viewer) {
		if (viewer != null) {
			if (on) {
				installInterestFilter(viewer);
				ContextUiPlugin.getViewerManager().addFilteredViewer(viewer);
			} else {
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
	private Set<Class<?>> getPreservedFilterClasses() {
		if (ContextUiPlugin.getDefault() == null || viewPart == null) {
			return Collections.emptySet();
		}
		try {
			return ContextUiPlugin.getDefault().getPreservedFilterClasses(viewPart.getSite().getId());
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
					"Could not determine preserved filters", e));
			return Collections.emptySet();
		}
	}

	protected boolean installInterestFilter(StructuredViewer viewer) {
		if (viewer == null) {
			// FIXME Assert.isNotNull(viewer)
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
					"The viewer to install interest filter is null", new Exception()));
			return false;
		} else if (viewer.getControl().isDisposed() && manageViewer) {
			// TODO: do this with part listener, not lazily?
			return false;
		}

		try {
			viewer.getControl().setRedraw(false);
			previousFilters.put(viewer, Arrays.asList(viewer.getFilters()));

			if (viewPart != null && manageFilters) {
				Set<ViewerFilter> toAdd = new HashSet<ViewerFilter>();
				Set<Class<?>> excludedFilters = getPreservedFilterClasses();
				for (ViewerFilter filter : previousFilters.get(viewer)) {
					if (excludedFilters.contains(filter.getClass())) {
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
					"Could not install viewer filter on: " + globalPrefId, t));
		} finally {
			viewer.getControl().setRedraw(true);
			internalSuppressExpandAll = false;
		}
		return false;
	}

	protected void uninstallInterestFilter(StructuredViewer viewer) {
		if (viewer == null) {
			// FIXME Assert.isNotNull(viewer)
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
					"The viewer to uninstall interest filter is null", new Exception()));
			return;
		} else if (viewer.getControl().isDisposed()) {
			// TODO: do this with part listener, not lazily?
			ContextUiPlugin.getViewerManager().removeManagedViewer(viewer, viewPart);
			return;
		}

		try {
			viewer.getControl().setRedraw(false);
			if (viewPart != null && manageFilters) {
				if (previousFilters.containsKey(viewer)) {
					Set<ViewerFilter> filters = new HashSet<ViewerFilter>(previousFilters.get(viewer));
					previousFilters.remove(viewer);
					for (ViewerFilter filter : viewer.getFilters()) {
						if (!(filter instanceof InterestFilter)) {
							filters.add(filter);
						}
					}
					viewer.setFilters(filters.toArray(new ViewerFilter[filters.size()]));
				}
			}
			if (Arrays.asList(viewer.getFilters()).contains(interestFilter)) {
				viewer.removeFilter(interestFilter);
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, ContextUiPlugin.ID_PLUGIN,
					"Could not uninstall interest viewer filter on: " + globalPrefId, t));
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

}
