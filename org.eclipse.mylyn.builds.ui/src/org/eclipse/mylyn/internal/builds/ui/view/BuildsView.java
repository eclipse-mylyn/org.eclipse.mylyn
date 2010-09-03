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

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.util.EContentAdapter;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildModel;
import org.eclipse.mylyn.builds.internal.core.util.BuildsConstants;
import org.eclipse.mylyn.builds.ui.BuildsUiConstants;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.mylyn.internal.builds.ui.BuildToolTip;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.builds.ui.view.BuildContentProvider.Presentation;
import org.eclipse.mylyn.internal.provisional.commons.ui.AbstractColumnViewerSupport;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.provisional.commons.ui.TreeSorter;
import org.eclipse.mylyn.internal.provisional.commons.ui.TreeViewerSupport;
import org.eclipse.mylyn.internal.provisional.commons.ui.actions.CollapseAllAction;
import org.eclipse.mylyn.internal.provisional.commons.ui.actions.ExpandAllAction;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * @author Steffen Pingel
 */
public class BuildsView extends ViewPart implements IShowInTarget {

	private class BuildTreeSorter extends TreeSorter {

		@Override
		public int compare(TreeViewer viewer, Object e1, Object e2, int columnIndex) {
			if (e1 instanceof IBuildServer && e2 instanceof IBuildServer) {
				// keep server sort order stable for now
				int res = getComparator().compare(((IBuildElement) e1).getLabel(), ((IBuildElement) e2).getLabel());
				if (getSortDirection(viewer) == SWT.UP) {
					return -res;
				}
				return res;
			}
			if (e1 instanceof IBuildPlan && e2 instanceof IBuildPlan) {
				IBuildPlan p1 = (IBuildPlan) e1;
				IBuildPlan p2 = (IBuildPlan) e2;
				switch (columnIndex) {
				case -1: // default
					return compare(p1.getLabel(), p2.getLabel());
				case 0: // build
					return compare(p1.getStatus(), p2.getStatus());
				case 1: // summary
					return p1.getHealth() - p2.getHealth();
				case 2: // last build
					Long t1 = (p1.getLastBuild() != null) ? p1.getLastBuild().getTimestamp() : null;
					Long t2 = (p2.getLastBuild() != null) ? p2.getLastBuild().getTimestamp() : null;
					return compare(t1, t2);
				}
			}
			return super.compare(viewer, e1, e2, columnIndex);
		}

		@Override
		protected int compareDefault(TreeViewer viewer, Object e1, Object e2) {
			return compare(viewer, e1, e2, -1);
		}

	}

	public static BuildsView openInActivePerspective() {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (window != null) {
			IWorkbenchPage page = window.getActivePage();
			if (page != null) {
				try {
					return (BuildsView) page.showView(BuildsUiConstants.ID_VIEW_BUILDS);
				} catch (PartInitException e) {
					// ignore
				}
			}
		}
		return null;
	}

	private BuildStatusFilter buildStatusFilter;

	private Action collapseAllAction;

	private BuildContentProvider contentProvider;

	private Action expandAllAction;

	private FilterByStatusAction filterDisabledAction;

	private Date lastRefresh;

	private Composite messageComposite;

	private BuildModel model;

	private AdapterImpl modelListener;

	private BuildElementPropertiesAction propertiesAction;

	private final IPropertyChangeListener propertyChangeListener = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal.PREF_AUTO_REFRESH_ENABLED.equals(event
					.getProperty())) {
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (refreshAutomaticallyAction != null) {
							refreshAutomaticallyAction.updateState();
						}
					}
				});
			}
		}
	};

	private RefreshAutomaticallyAction refreshAutomaticallyAction;

	private StackLayout stackLayout;

	private IMemento stateMemento;

	private TreeViewer viewer;

	private PresentationMenuAction presentationsMenuAction;

	private BuildToolTip toolTip;

	public BuildsView() {
		BuildsUiPlugin.getDefault().initializeRefresh();
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void createMessage(Composite parent) {
		messageComposite = new Composite(parent, SWT.NONE);
		FillLayout layout = new FillLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		messageComposite.setLayout(layout);
		//GridLayoutFactory.swtDefaults().applyTo(messageComposite);
		messageComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		Link link = new Link(messageComposite, SWT.WRAP);
		link.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		link.setText("No build servers available. Create a <a href=\"create\">build server</a>...");
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if ("create".equals(event.text)) {
					new NewBuildServerAction().run();
				}
			}
		});
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		stackLayout = new StackLayout();
		composite.setLayout(stackLayout);
		composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		createMessage(composite);
		createViewer(composite);

		initActions();
		createPopupMenu(parent);
		contributeToActionBars();

		toolTip = new BuildToolTip(getViewer().getControl());
		toolTip.setViewer(viewer);

		IWorkbenchSiteProgressService progress = (IWorkbenchSiteProgressService) getSite().getAdapter(
				IWorkbenchSiteProgressService.class);
		if (progress != null) {
			// show indicator for all running refreshes
			progress.showBusyForFamily(BuildsConstants.JOB_FAMILY);
		}

		model = BuildsUiInternal.getModel();
		modelListener = new EContentAdapter() {
			@Override
			public void notifyChanged(Notification msg) {
				super.notifyChanged(msg);
				if (!viewer.getControl().isDisposed()) {
					lastRefresh = new Date();
					// FIXME show result of last update
					updateContents(Status.OK_STATUS);
				}
			}
		};
		model.eAdapters().add(modelListener);

		if (stateMemento != null) {
			restoreState(stateMemento);
			stateMemento = null;
		}

		viewer.setInput(model);
		viewer.setSorter(new BuildTreeSorter());
		viewer.expandAll();

		getSite().setSelectionProvider(viewer);
		getSite().getSelectionProvider().addSelectionChangedListener(propertiesAction);
		propertiesAction.selectionChanged((IStructuredSelection) getSite().getSelectionProvider().getSelection());

		updateContents(Status.OK_STATUS);

		new TreeViewerSupport(viewer, getStateFile());
	}

	private File getStateFile() {
		IPath stateLocation = Platform.getStateLocation(BuildsUiPlugin.getDefault().getBundle());
		return stateLocation.append("BuildView.xml").toFile(); //$NON-NLS-1$
	}

	protected void createPopupMenu(Composite parent) {
		MenuManager menuManager = new MenuManager();

		menuManager.add(new GroupMarker("group.open"));
		menuManager.add(new Separator("group.edit"));
		menuManager.add(new Separator("group.file"));
		menuManager.add(new Separator("group.run"));
		menuManager.add(new Separator("group.refresh"));
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.add(new Separator("group.properties"));

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
		buildColumn.setText("Build");
		buildColumn.setWidth(220);
		buildColumn.setData(AbstractColumnViewerSupport.KEY_COLUMN_CAN_HIDE, false);

		TreeViewerColumn summaryViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		summaryViewerColumn.setLabelProvider(new BuildSummaryLabelProvider());
		TreeColumn summaryColumn = summaryViewerColumn.getColumn();
		summaryColumn.setText("Summary");
		summaryColumn.setWidth(220);

		TreeViewerColumn lastBuiltViewerColumn = new TreeViewerColumn(viewer, SWT.RIGHT);
		lastBuiltViewerColumn.setLabelProvider(new BuildTimeLabelProvider());
		TreeColumn statusColumn = lastBuiltViewerColumn.getColumn();
		statusColumn.setText("Last Built");
		statusColumn.setWidth(50);

		contentProvider = new BuildContentProvider();
		contentProvider.setSelectedOnly(true);
		viewer.setContentProvider(contentProvider);

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				OpenWithBrowserAction action = new OpenWithBrowserAction();
				action.selectionChanged((IStructuredSelection) event.getSelection());
				action.run();
			}
		});
	}

	@Override
	public void dispose() {
		super.dispose();
		model.eAdapters().remove(modelListener);
		BuildsUiPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(propertyChangeListener);
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(presentationsMenuAction);
		manager.add(new Separator());
		manager.add(collapseAllAction);
		manager.add(expandAllAction);
		manager.add(new Separator("group.filter")); //$NON-NLS-1$
		manager.add(filterDisabledAction);
		manager.add(new Separator("group.navigate")); //$NON-NLS-1$
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(new Separator("group.properties")); //$NON-NLS-1$
		manager.add(refreshAutomaticallyAction);
		manager.add(new Separator("group.preferences")); //$NON-NLS-1$
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

		OpenWithBrowserAction openInBrowserAction = new OpenWithBrowserAction();
		viewer.addSelectionChangedListener(openInBrowserAction);
		manager.add(openInBrowserAction);

		RunBuildAction runBuildAction = new RunBuildAction();
		viewer.addSelectionChangedListener(runBuildAction);
		manager.add(runBuildAction);

		ShowBuildOutputAction openConsoleAction = new ShowBuildOutputAction();
		viewer.addSelectionChangedListener(openConsoleAction);
		manager.add(openConsoleAction);
	}

	public BuildStatusFilter getBuildStatusFilter() {
		if (buildStatusFilter == null) {
			buildStatusFilter = new BuildStatusFilter();
			getViewer().addFilter(buildStatusFilter);
		}
		return buildStatusFilter;
	}

	public BuildContentProvider getContentProvider() {
		return contentProvider;
	}

	protected BuildStatus getPlanStatus() {
		BuildStatus planStatus = null;
		boolean plansSelected = false;
		if (contentProvider != null) {
			for (IBuildPlan plan : model.getPlans()) {
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
		if (plansSelected) {
			return (planStatus != null) ? planStatus : BuildStatus.DISABLED;
		}
		return null;
	}

	TreeViewer getViewer() {
		return viewer;
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		BuildsUiPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(propertyChangeListener);

		// defer restore until view is created
		this.stateMemento = memento;
	}

	private void initActions() {
		collapseAllAction = new CollapseAllAction(viewer);
		expandAllAction = new ExpandAllAction(viewer);
		propertiesAction = new BuildElementPropertiesAction();
		refreshAutomaticallyAction = new RefreshAutomaticallyAction();
		filterDisabledAction = new FilterByStatusAction(this, BuildStatus.DISABLED);
		presentationsMenuAction = new PresentationMenuAction(this);
	}

	private void restoreState(IMemento memento) {
		IMemento child = memento.getChild("statusFilter");
		if (child != null) {
			boolean changed = false;
			for (BuildStatus status : BuildStatus.values()) {
				Boolean value = child.getBoolean(status.name());
				if (value != null && value.booleanValue()) {
					getBuildStatusFilter().addFiltered(status);
					changed = true;
				}
			}
			if (changed) {
				filterDisabledAction.update();
			}
		}
		child = memento.getChild("presentation");
		if (child != null) {
			String id = child.getString("id");
			if (id != null) {
				try {
					getContentProvider().setPresentation(Presentation.valueOf(id));
				} catch (IllegalArgumentException e) {
					// use default
				}
			}
		}
	}

	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);

		if (buildStatusFilter != null) {
			Set<BuildStatus> statuses = buildStatusFilter.getFiltered();
			if (statuses.size() > 0) {
				IMemento child = memento.createChild("statusFilter");
				for (BuildStatus status : statuses) {
					child.putBoolean(status.name(), true);
				}
			}
		}
		IMemento child = memento.createChild("presentation");
		child.putString("id", getContentProvider().getPresentation().name());
	}

	@Override
	public void setFocus() {
		getViewer().getControl().setFocus();
	}

	private void setTopControl(Control control) {
		if (stackLayout.topControl != control) {
			stackLayout.topControl = control;
			control.getParent().layout();
		}
	}

	public boolean show(ShowInContext context) {
		if (context.getSelection() != null) {
			getViewer().setSelection(context.getSelection());
			return true;
		}
		return false;
	}

	private void updateContents(IStatus status) {
		boolean hasContents = false;
		if (contentProvider != null) {
			if (model.getPlans().size() > 0 || model.getServers().size() > 0) {
				hasContents = true;
			}
		}
		if (hasContents) {
			setTopControl(viewer.getControl());
		} else {
			setTopControl(messageComposite);
		}
		updateDecoration(status);
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

}
