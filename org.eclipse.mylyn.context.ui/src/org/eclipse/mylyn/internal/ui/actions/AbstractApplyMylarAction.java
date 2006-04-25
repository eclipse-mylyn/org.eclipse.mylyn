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

package org.eclipse.mylar.internal.ui.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylar.internal.core.util.MylarStatusHandler;
import org.eclipse.mylar.internal.ui.MylarImages;
import org.eclipse.mylar.provisional.core.MylarPlugin;
import org.eclipse.mylar.provisional.ui.InterestFilter;
import org.eclipse.mylar.provisional.ui.MylarUiPlugin;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IActionDelegate2;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;

/**
 * Extending this class makes it possible to apply Mylar management to a
 * structured view (e.g. to provide interest-based filtering).
 * 
 * @author Mik Kersten
 */
public abstract class AbstractApplyMylarAction extends Action implements IViewActionDelegate, IActionDelegate2,
		IPropertyChangeListener {

	private static final String ACTION_LABEL = "Apply Mylar";

	public static final String PREF_ID_PREFIX = "org.eclipse.mylar.ui.interest.filter.";

	protected String prefId;

	protected IAction initAction = null;

	protected final InterestFilter interestFilter;
	
	protected IViewPart viewPart;

	protected List<ViewerFilter> previousFilters = new ArrayList<ViewerFilter>();
	
	public AbstractApplyMylarAction(InterestFilter interestFilter) {
		super();
		this.interestFilter = interestFilter;
		setText(ACTION_LABEL);
		setToolTipText(ACTION_LABEL);
		setImageDescriptor(MylarImages.INTEREST_FILTERING);
	}

	public void init(IAction action) {
		initAction = action;
		setChecked(action.isChecked());
	}

	public void init(IViewPart view) {
		String id = view.getSite().getId();
		prefId = PREF_ID_PREFIX + id;
		viewPart = view;
	}

	public void run(IAction action) {
		setChecked(action.isChecked());
		valueChanged(action, action.isChecked(), true);
	}

	/**
	 * Don't update if the preference has not been initialized.
	 */
	public void update() {
		if (prefId != null) {
			update(MylarPlugin.getDefault().getPreferenceStore().getBoolean(prefId));
		}
	}

	/**
	 * This operation is expensive.
	 */
	public void update(boolean on) {
		valueChanged(initAction, on, false);
	}

	protected void valueChanged(IAction action, final boolean on, boolean store) {
		try {
			MylarPlugin.getContextManager().setContextCapturePaused(true);
			setChecked(on);
			action.setChecked(on);
			if (store && MylarPlugin.getDefault() != null)
				MylarPlugin.getDefault().getPreferenceStore().setValue(prefId, on);

			for (StructuredViewer viewer : getViewers()) {
				MylarUiPlugin.getDefault().getViewerManager().addManagedViewer(viewer, viewPart);
				installInterestFilter(on, viewer);
			}
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "Could not install viewer manager on: " + prefId, false);
		} finally {
			MylarPlugin.getContextManager().setContextCapturePaused(false);
		}
	}

	/**
	 * Public for testing
	 */
	public void installInterestFilter(final boolean on, StructuredViewer viewer) {
		if (viewer != null) {
			boolean installed = false;
			if (on) {
				installed = installInterestFilter(viewer);
				MylarUiPlugin.getDefault().getViewerManager().addFilteredViewer(viewer);
			} else {
				MylarUiPlugin.getDefault().getViewerManager().removeFilteredViewer(viewer);
				uninstallInterestFilter(viewer);
			}
			if (installed && on && viewer instanceof TreeViewer) {
				((TreeViewer)viewer).expandAll();
			}
		}
	}

	/**
	 * Public for testing
	 */
	public abstract List<StructuredViewer> getViewers();

	/**
	 * @return	filters that should not be removed when the interest filter is installed
	 */
	public abstract List<Class> getPreservedFilters();
	
	protected boolean installInterestFilter(StructuredViewer viewer) {
		if (viewer == null) {
			MylarStatusHandler.log("The viewer to install InterestFilter is null", this);
			return false;
		}
		
		try {
			viewer.getControl().setRedraw(false);
			previousFilters.addAll(Arrays.asList(viewer.getFilters()));
			List<Class> excludedFilters = getPreservedFilters();
			for (ViewerFilter filter : previousFilters) {
				if (!excludedFilters.contains(filter.getClass())) {
					try {
						viewer.removeFilter(filter);
					} catch (Throwable t) {
						MylarStatusHandler.fail(t, "Failed to remove filter: " + filter, false);
					}
				}
			}
			viewer.addFilter(interestFilter);
			viewer.getControl().setRedraw(true);
			return true;
		} catch (Throwable t) {
			MylarStatusHandler.fail(t, "Could not install viewer filter on: " + prefId, false);
		}
		return false;
	}

	protected void uninstallInterestFilter(StructuredViewer viewer) {
		if (viewer == null) {
            MylarStatusHandler.log("Could not uninstall interest filter", this);
            return;
        }

        viewer.getControl().setRedraw(false);
        List<Class> excludedFilters = getPreservedFilters();
        for (ViewerFilter filter : previousFilters) {
        	if (!excludedFilters.contains(filter.getClass())) {
        		try {
        			viewer.addFilter(filter);
				} catch (Throwable t) {
					MylarStatusHandler.fail(t, "Failed to remove filter: " + filter, false);
				}
        	}
        }
		previousFilters.clear();
        viewer.removeFilter(interestFilter);
		viewer.getControl().setRedraw(true);
    }

	public void selectionChanged(IAction action, ISelection selection) {
		// ignore
	}

	public void dispose() {
		for (StructuredViewer viewer : getViewers()) {
			MylarUiPlugin.getDefault().getViewerManager().removeManagedViewer(viewer, viewPart);
		}
	}

	public void runWithEvent(IAction action, Event event) {
		run(action);
	}

	public String getPrefId() {
		return prefId;
	}

	/**
	 * For testing.
	 */
	public InterestFilter getInterestFilter() {
		return interestFilter;
	}

	protected IViewPart getView(String id) {
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		if (activePage == null)
			return null;
		IViewPart view = activePage.findView(id);
		return view;
	}
}
