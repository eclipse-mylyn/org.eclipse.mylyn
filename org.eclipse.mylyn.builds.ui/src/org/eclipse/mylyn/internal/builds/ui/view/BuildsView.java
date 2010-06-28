/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.view;

import java.io.IOException;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.mylyn.internal.builds.core.BuildModel;
import org.eclipse.mylyn.internal.builds.core.util.BuildsConstants;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.actions.CollapseAllAction;
import org.eclipse.mylyn.internal.provisional.commons.ui.actions.ExpandAllAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * @author Steffen Pingel
 */
public class BuildsView extends ViewPart {

	private BuildContentProvider contentProvider;

	private BuildModel model;

	private AdapterImpl modelListener;

	private TreeViewer viewer;

	private Action expandAllAction;

	private Action collapseAllAction;

	public BuildsView() {
		BuildsUiPlugin.getDefault().initializeRefresh();
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	@Override
	public void createPartControl(Composite parent) {
		createViewer(parent);
		createPopupMenu(parent);

		initActions();
		contributeToActionBars();

		IWorkbenchSiteProgressService progress = (IWorkbenchSiteProgressService) getSite().getAdapter(
				IWorkbenchSiteProgressService.class);
		if (progress != null) {
			// show indicator for all running refreshes
			progress.showBusyForFamily(BuildsConstants.JOB_FAMILY);
		}

		model = BuildsUiInternal.getModel();
		modelListener = new AdapterImpl() {
			@Override
			public void notifyChanged(Notification msg) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (!viewer.getControl().isDisposed()) {
							refresh();
						}
					}

				});
			}
		};
		model.eAdapters().add(modelListener);
		viewer.setInput(model);
		viewer.expandAll();

		getSite().setSelectionProvider(viewer);
	}

	private void initActions() {
		collapseAllAction = new CollapseAllAction(viewer);
		expandAllAction = new ExpandAllAction(viewer);
	}

	protected void createPopupMenu(Composite parent) {
		MenuManager menuManager = new MenuManager();

		GroupMarker marker = new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS);

		OpenInBrowserAction openInBrowserAction = new OpenInBrowserAction();
		menuManager.add(openInBrowserAction);
		viewer.addSelectionChangedListener(openInBrowserAction);

		RunBuildAction runBuildAction = new RunBuildAction();
		menuManager.add(runBuildAction);
		viewer.addSelectionChangedListener(runBuildAction);

		menuManager.add(marker);
		Menu contextMenu = menuManager.createContextMenu(parent);

		viewer.getTree().setMenu(contextMenu);
		getSite().registerContextMenu(menuManager, viewer);
	}

	protected void createViewer(Composite parent) {
		viewer = new TreeViewer(parent, SWT.FULL_SELECTION);
		Tree tree = viewer.getTree();
		tree.setHeaderVisible(true);

		TreeViewerColumn buildViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		buildViewerColumn.setLabelProvider(new DecoratingStyledCellLabelProvider(new BuildLabelProvider(), PlatformUI
				.getWorkbench().getDecoratorManager().getLabelDecorator(), null));
		TreeColumn buildColumn = buildViewerColumn.getColumn();
		buildColumn.setText("Builds");
		buildColumn.setWidth(220);

		TreeViewerColumn summaryViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		summaryViewerColumn.setLabelProvider(new BuildSummaryLabelProvider());
		TreeColumn summaryColumn = summaryViewerColumn.getColumn();
		summaryColumn.setText("Summary");
		summaryColumn.setWidth(220);

		TreeViewerColumn statusViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		statusViewerColumn.setLabelProvider(new BuildStatusLabelProvider());
		TreeColumn statusColumn = statusViewerColumn.getColumn();
		statusColumn.setText("Status");
		statusColumn.setWidth(50);

		contentProvider = new BuildContentProvider();
		contentProvider.setSelectedOnly(true);
		viewer.setContentProvider(contentProvider);

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				OpenInBrowserAction action = new OpenInBrowserAction();
				action.selectionChanged((IStructuredSelection) event.getSelection());
				action.run();
			}
		});
	}

	@Override
	public void dispose() {
		super.dispose();
		model.eAdapters().remove(modelListener);
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(collapseAllAction);
		manager.add(expandAllAction);
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(new Separator());
		manager.add(new OpenBuildsPreferencesAction());

	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(new NewBuildServerMenuAction());

		RefreshAction refresh = new RefreshAction() {
			@Override
			public void run() {
				super.run();
				// TODO remove
				try {
					BuildsUiInternal.save();
				} catch (IOException e) {
					e.printStackTrace();
				}
			};
		};
		manager.add(refresh);

		manager.add(new Separator());

		OpenInBrowserAction openInBrowserAction = new OpenInBrowserAction();
		viewer.addSelectionChangedListener(openInBrowserAction);
		manager.add(openInBrowserAction);

		RunBuildAction runBuildAction = new RunBuildAction();
		viewer.addSelectionChangedListener(runBuildAction);
		manager.add(runBuildAction);
	}

	TreeViewer getViewer() {
		return viewer;
	}

	private void refresh() {
		viewer.refresh();
		viewer.expandAll();
	}

	@Override
	public void setFocus() {
		getViewer().getControl().setFocus();
	}

}
