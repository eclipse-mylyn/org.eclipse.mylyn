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
import java.text.DateFormat;
import java.util.Date;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.internal.builds.core.BuildModel;
import org.eclipse.mylyn.internal.builds.core.util.BuildsConstants;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.actions.CollapseAllAction;
import org.eclipse.mylyn.internal.provisional.commons.ui.actions.ExpandAllAction;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewSite;
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

	private BuildElementPropertiesAction propertiesAction;

	private Date lastRefresh;

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
		initActions();
		createPopupMenu(parent);
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
							lastRefresh = new Date();
							refresh();
							// FIXME show result of last update
							updateDecoration(Status.OK_STATUS);
						}
					}
				});
			}
		};
		model.eAdapters().add(modelListener);
		viewer.setInput(model);
		viewer.expandAll();

		getSite().setSelectionProvider(viewer);
		getSite().getSelectionProvider().addSelectionChangedListener(propertiesAction);

		updateDecoration(Status.OK_STATUS);
	}

	private void initActions() {
		collapseAllAction = new CollapseAllAction(viewer);
		expandAllAction = new ExpandAllAction(viewer);
		propertiesAction = new BuildElementPropertiesAction();
	}

	protected void createPopupMenu(Composite parent) {
		MenuManager menuManager = new MenuManager();

		OpenInBrowserAction openInBrowserAction = new OpenInBrowserAction();
		menuManager.add(openInBrowserAction);
		viewer.addSelectionChangedListener(openInBrowserAction);

		RunBuildAction runBuildAction = new RunBuildAction();
		menuManager.add(runBuildAction);
		viewer.addSelectionChangedListener(runBuildAction);

		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		menuManager.add(new Separator("properties"));
		menuManager.add(propertiesAction);

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

	public void updateDecoration(IStatus status) {
		String statusMessage = "";
		if (status.isOK()) {
			BuildStatus planStatus = getPlanStatus();
			if (planStatus == BuildStatus.SUCCESS) {
				setTitleImage(CommonImages.getImageWithOverlay(BuildImages.VIEW_BUILDS, CommonImages.OVERLAY_SUCCESS,
						false, false));
			} else if (planStatus == BuildStatus.UNSTABLE) {
				setTitleImage(CommonImages.getImageWithOverlay(BuildImages.VIEW_BUILDS, CommonImages.OVERLAY_FAILED,
						false, false));

			} else if (planStatus == BuildStatus.FAILED) {
				setTitleImage(CommonImages.getImageWithOverlay(BuildImages.VIEW_BUILDS, CommonImages.OVERLAY_ERROR,
						false, false));
			} else {
				setTitleImage(CommonImages.getImage(BuildImages.VIEW_BUILDS));
			}
			if (lastRefresh != null) {
				statusMessage = NLS.bind("Last update: {0}", DateFormat.getDateTimeInstance().format(lastRefresh));
			}
		} else {
			setTitleImage(CommonImages.getImageWithOverlay(BuildImages.VIEW_BUILDS, CommonImages.OVERLAY_WARNING,
					false, false));
			statusMessage = "Last update failed";
		}

		if (statusMessage != null) {
			final IViewSite viewSite = getViewSite();
			if (viewSite != null) {
				final IStatusLineManager statusLine = viewSite.getActionBars().getStatusLineManager();
				if (statusLine != null) {
					statusLine.setMessage(statusMessage);
				}
			}
		}
	}

	protected BuildStatus getPlanStatus() {
		BuildStatus planStatus = null;
		boolean plansSelected = false;
		if (contentProvider != null) {
			for (TreeIterator<EObject> it = model.eAllContents(); it.hasNext();) {
				Object element = it.next();
				if (element instanceof IBuildPlan) {
					IBuildPlan plan = (IBuildPlan) element;
					if (plan.isSelected()) {
						if (plan.getStatus() != null) {
							switch (plan.getStatus()) {
							case SUCCESS:
								if (planStatus == null) {
									planStatus = BuildStatus.SUCCESS;
								}
								break;
							case UNSTABLE:
								if (planStatus == null || planStatus == BuildStatus.SUCCESS) {
									planStatus = BuildStatus.UNSTABLE;
								}
								break;
							case FAILED:
								planStatus = BuildStatus.FAILED;
								break;
							}
						}
						plansSelected = true;
					}
				}
			}
		}
		if (plansSelected) {
			return (planStatus != null) ? planStatus : BuildStatus.DISABLED;
		}
		return null;
	}

}
