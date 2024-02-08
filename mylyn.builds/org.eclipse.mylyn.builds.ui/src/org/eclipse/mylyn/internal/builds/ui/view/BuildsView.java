/*******************************************************************************
 * Copyright (c) 2010, 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Itema AS - bug 325079 added support for build service messages
 *     Itema AS - bug 331008 automatic resize of view columns
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.view;

import java.io.File;
import java.text.DateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.internal.core.BuildModel;
import org.eclipse.mylyn.builds.internal.core.operations.RefreshOperation;
import org.eclipse.mylyn.builds.internal.core.util.BuildsConstants;
import org.eclipse.mylyn.builds.ui.BuildsUiConstants;
import org.eclipse.mylyn.commons.core.CoreUtil;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor.OperationFlag;
import org.eclipse.mylyn.commons.ui.AbstractColumnViewerSupport;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.ui.TreeSorter;
import org.eclipse.mylyn.commons.ui.TreeViewerSupport;
import org.eclipse.mylyn.commons.ui.actions.CollapseAllAction;
import org.eclipse.mylyn.commons.ui.actions.ExpandAllAction;
import org.eclipse.mylyn.commons.workbench.SubstringPatternFilter;
import org.eclipse.mylyn.internal.builds.ui.BuildImages;
import org.eclipse.mylyn.internal.builds.ui.BuildToolTip;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.builds.ui.actions.AbortBuildAction;
import org.eclipse.mylyn.internal.builds.ui.actions.RunBuildAction;
import org.eclipse.mylyn.internal.builds.ui.actions.ShowBuildOutputAction;
import org.eclipse.mylyn.internal.builds.ui.actions.ShowTestResultsAction;
import org.eclipse.mylyn.internal.builds.ui.commands.OpenHandler;
import org.eclipse.mylyn.internal.builds.ui.notifications.BuildsServiceMessageControl;
import org.eclipse.mylyn.internal.builds.ui.view.BuildContentProvider.Presentation;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
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
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * @author Steffen Pingel
 * @author Torkild U. Resheim
 * @author Lucas Panjer
 */
public class BuildsView extends ViewPart implements IShowInTarget {

	public static class BuildsSummary {
		int numSuccess;

		int numUnstable;

		int numFailed;

		public boolean isSuccess() {
			return numSuccess > 0 && numUnstable == 0 && numFailed == 0;
		}

		public boolean isUnstable() {
			return numUnstable > 0 && numFailed == 0;
		}

		public boolean isFailed() {
			return numFailed > 0;
		}

		public boolean isEmpty() {
			return numSuccess == 0 && numSuccess == 0 && numFailed == 0;
		}

		@Override
		public String toString() {
			return NLS.bind(Messages.BuildsView_BuildStatusSummary,
					new Object[] { numSuccess, numUnstable, numFailed });
		}
	}

	private static class ToggleableFilterTree extends FilteredTree {
		private ToggleableFilterTree(Composite parent, int treeStyle, PatternFilter filter, boolean useNewLook) {
			super(parent, treeStyle, filter, useNewLook, true);
		}

		public void setFilterVisible(boolean visible) {
			clearText();
			((GridData) filterComposite.getLayoutData()).exclude = !visible;
			filterComposite.setVisible(visible);
			if (visible) {
				filterText.forceFocus();
			}
			this.layout();
		}
	}

	private class ToggleFilterAction extends Action {

		public ToggleFilterAction() {
			super(Messages.BuildsView_ShowTextFilter, IAction.AS_CHECK_BOX);
			setImageDescriptor(CommonImages.FIND);
		}

		@Override
		public void run() {
			if (filteredTree != null) {
				filteredTree.setFilterVisible(isChecked());
			}
		}
	}

	private static class BuildTreeSorter extends TreeSorter {

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
			if (e1 instanceof IBuildPlan p1 && e2 instanceof IBuildPlan p2) {
				switch (columnIndex) {
					case -1: // default
						return CoreUtil.compare(p1.getLabel(), p2.getLabel());
					case 0: // build
						return CoreUtil.compare(p1.getStatus(), p2.getStatus());
					case 1: // summary
						return p1.getHealth() - p2.getHealth();
					case 2: // last build
						Long t1 = p1.getLastBuild() != null ? p1.getLastBuild().getTimestamp() : null;
						Long t2 = p2.getLastBuild() != null ? p2.getLastBuild().getTimestamp() : null;
						return CoreUtil.compare(t1, t2);
				}
			}
			return super.compare(viewer, e1, e2, columnIndex);
		}

		@Override
		protected int compareDefault(TreeViewer viewer, Object e1, Object e2) {
			return compare(viewer, e1, e2, -1);
		}

	}

	private static class BuildElementSubstringPatternFilter extends SubstringPatternFilter {

		@Override
		protected boolean isLeafMatch(Viewer viewer, Object element) {
			String labelText = null;
			if (element instanceof IBuildElement) {
				labelText = ((IBuildElement) element).getLabel();
			}

			if (labelText == null) {
				return false;
			}
			return wordMatches(labelText);
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

	private FilterByStatusAction filterSucceedingAction;

	private ToggleFilterAction toggleFilterAction;

	private Date lastRefresh;

	private Composite messageComposite;

	private BuildModel model;

	private AdapterImpl modelListener;

	private BuildElementPropertiesAction propertiesAction;

	private RefreshAutomaticallyAction refreshAutomaticallyAction;

	private StackLayout stackLayout;

	private IMemento stateMemento;

	private ToggleableFilterTree filteredTree;

	private TreeViewer viewer;

	private PresentationMenuAction presentationsMenuAction;

	private BuildToolTip toolTip;

	private BuildsServiceMessageControl serviceMessageControl;

	private final IPropertyChangeListener propertyChangeListener = event -> {
		if (org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal.PREF_AUTO_REFRESH_ENABLED
				.equals(event.getProperty())) {
			Display.getDefault().asyncExec(() -> {
				if (refreshAutomaticallyAction != null) {
					refreshAutomaticallyAction.updateState();
				}
			});
		}
	};

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
		messageComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		Link link = new Link(messageComposite, SWT.WRAP);
		link.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
		link.setText(Messages.BuildsView_NoServersAvailable);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if ("create".equals(event.text)) { //$NON-NLS-1$
					new NewBuildServerAction().run();
				}
			}
		});
	}

	@Override
	public void createPartControl(Composite parent) {
		Composite body = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		layout.numColumns = 1;
		body.setLayout(layout);

		Composite composite = new Composite(body, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		stackLayout = new StackLayout();
		composite.setLayout(stackLayout);
		composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		createMessage(composite);
		createViewer(composite);

		initActions();
		createPopupMenu(body);
		contributeToActionBars();

		toolTip = new BuildToolTip(getViewer().getControl());
		toolTip.setViewer(viewer);
		// bug#160897: set to empty string to disable native tooltips (windows only?)
		viewer.getTree().setToolTipText(""); //$NON-NLS-1$

		IWorkbenchSiteProgressService progress = getSite().getAdapter(IWorkbenchSiteProgressService.class);
		if (progress != null) {
			// show indicator for all running refreshes
			progress.showBusyForFamily(BuildsConstants.JOB_FAMILY);
		}

		model = BuildsUiInternal.getModel();
		modelListener = new BuildModelContentAdapter() {
			@Override
			public void doNotifyChanged(Notification msg) {
				if (!viewer.getControl().isDisposed()) {
					lastRefresh = new Date();
					updateContents(Status.OK_STATUS);
				}
			}
		};
		model.eAdapters().add(modelListener);

		if (stateMemento != null) {
			restoreState(stateMemento);
			stateMemento = null;
		}
		serviceMessageControl = new BuildsServiceMessageControl(body);

		viewer.setInput(model);
		viewer.setComparator(new BuildTreeSorter());
		viewer.expandAll();

		installAutomaticResize(viewer.getTree());

		getSite().setSelectionProvider(viewer);
		getSite().getSelectionProvider().addSelectionChangedListener(propertiesAction);
		propertiesAction.selectionChanged((IStructuredSelection) getSite().getSelectionProvider().getSelection());

		updateContents(Status.OK_STATUS);

		new TreeViewerSupport(viewer, getStateFile());
		// Make sure we get notifications
		NotificationSinkProxy.setControl(serviceMessageControl);
	}

	/**
	 * Initializes automatic resize of the tree control columns. The size of these will be adjusted when a node is expanded or collapsed and
	 * when the tree changes size.
	 *
	 * @param tree
	 *            the tree to resize
	 */
	private void installAutomaticResize(final Tree tree) {
		Listener listener = e -> packColumnsAsync(tree);
		// Automatically resize columns when we expand tree nodes.
		tree.addListener(SWT.Collapse, listener);
		tree.addListener(SWT.Expand, listener);
		tree.getParent().addListener(SWT.Resize, listener);
	}

	void packColumnsAsync(final Tree tree) {
		tree.getDisplay().asyncExec(() -> {
			if (!tree.isDisposed()) {
				try {
					tree.setRedraw(false);
					packColumns(tree);
				} finally {
					tree.setRedraw(true);
				}
			}
		});
	}

	private File getStateFile() {
		IPath stateLocation = Platform.getStateLocation(BuildsUiPlugin.getDefault().getBundle());
		return stateLocation.append("BuildView.xml").toFile(); //$NON-NLS-1$
	}

	protected void createPopupMenu(Composite parent) {
		MenuManager menuManager = new MenuManager();

		menuManager.add(new GroupMarker("group.open")); //$NON-NLS-1$
		menuManager.add(new Separator("group.edit")); //$NON-NLS-1$
		menuManager.add(new Separator("group.file")); //$NON-NLS-1$
		menuManager.add(new Separator("group.run")); //$NON-NLS-1$
		menuManager.add(new Separator("group.refresh")); //$NON-NLS-1$
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		menuManager.add(new Separator(BuildsUiConstants.GROUP_PROPERTIES));

		Menu contextMenu = menuManager.createContextMenu(parent);
		viewer.getTree().setMenu(contextMenu);
		getSite().registerContextMenu(menuManager, viewer);
	}

	protected void createViewer(Composite parent) {
		BuildElementSubstringPatternFilter patternFilter = new BuildElementSubstringPatternFilter();
		filteredTree = new ToggleableFilterTree(parent, SWT.FULL_SELECTION, patternFilter, true);
		filteredTree.setFilterVisible(false);
		viewer = filteredTree.getViewer();

		Tree tree = viewer.getTree();
		tree.setHeaderVisible(true);

		TreeViewerColumn buildViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		buildViewerColumn.setLabelProvider(new DecoratingStyledCellLabelProvider(new BuildLabelProvider(true),
				PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator(), null));
		TreeColumn buildColumn = buildViewerColumn.getColumn();
		buildColumn.setText(Messages.BuildsView_Build);
		buildColumn.setWidth(220);
		buildColumn.setData(AbstractColumnViewerSupport.KEY_COLUMN_CAN_HIDE, false);

		TreeViewerColumn summaryViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		summaryViewerColumn.setLabelProvider(new BuildSummaryLabelProvider());
		TreeColumn summaryColumn = summaryViewerColumn.getColumn();
		summaryColumn.setText(Messages.BuildsView_Summary);
		summaryColumn.setWidth(220);

		TreeViewerColumn lastBuiltViewerColumn = new TreeViewerColumn(viewer, SWT.RIGHT);
		lastBuiltViewerColumn.setLabelProvider(new RelativeBuildTimeLabelProvider());
		TreeColumn lastBuiltColumn = lastBuiltViewerColumn.getColumn();
		lastBuiltColumn.setText(Messages.BuildsView_LastBuilt);
		lastBuiltColumn.setWidth(50);

		contentProvider = new BuildContentProvider();
		contentProvider.setSelectedOnly(true);
		viewer.setContentProvider(contentProvider);

		viewer.addOpenListener(event -> {
			Object element = ((IStructuredSelection) event.getSelection()).getFirstElement();
			if (element instanceof IBuildElement) {
				OpenHandler.open(getSite().getPage(), Collections.singletonList((IBuildElement) element));
			}
		});
	}

	@Override
	public void dispose() {
		super.dispose();
		getModel().eAdapters().remove(modelListener);
		BuildsUiPlugin.getDefault().getPreferenceStore().removePropertyChangeListener(propertyChangeListener);
	}

	private void fillLocalPullDown(IMenuManager manager) {
		manager.add(presentationsMenuAction);
		manager.add(new Separator());
		manager.add(collapseAllAction);
		manager.add(expandAllAction);
		manager.add(new Separator(BuildsUiConstants.GROUP_FILTER));
		manager.add(filterDisabledAction);
		manager.add(filterSucceedingAction);
		manager.add(new Separator(BuildsUiConstants.GROUP_NAVIGATE));
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(new Separator(BuildsUiConstants.GROUP_PROPERTIES));
		manager.add(refreshAutomaticallyAction);
		manager.add(new Separator(BuildsUiConstants.GROUP_PREFERENCES));
		manager.add(new OpenBuildsPreferencesAction());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(new NewBuildServerMenuAction());

		manager.add(new Separator(BuildsUiConstants.GROUP_REFRESH));

		RefreshAction refresh = new RefreshAction();
		manager.add(refresh);
		manager.add(filterSucceedingAction);
		manager.add(toggleFilterAction);
		manager.add(new Separator(BuildsUiConstants.GROUP_OPEN));

		OpenWithBrowserAction openInBrowserAction = new OpenWithBrowserAction();
		openInBrowserAction.selectionChanged(StructuredSelection.EMPTY);
		viewer.addSelectionChangedListener(openInBrowserAction);
		manager.add(openInBrowserAction);

		RunBuildAction runBuildAction = new RunBuildAction();
		runBuildAction.selectionChanged(StructuredSelection.EMPTY);
		viewer.addSelectionChangedListener(runBuildAction);
		manager.add(runBuildAction);

		AbortBuildAction abortBuildAction = new AbortBuildAction();
		abortBuildAction.selectionChanged(StructuredSelection.EMPTY);
		viewer.addSelectionChangedListener(abortBuildAction);
		manager.add(abortBuildAction);

		manager.add(new Separator(BuildsUiConstants.GROUP_FILE));

		ShowBuildOutputAction openConsoleAction = new ShowBuildOutputAction();
		openConsoleAction.selectionChanged(StructuredSelection.EMPTY);
		viewer.addSelectionChangedListener(openConsoleAction);
		manager.add(openConsoleAction);

		ShowTestResultsAction showTestResultsAction = new ShowTestResultsAction();
		showTestResultsAction.selectionChanged(StructuredSelection.EMPTY);
		viewer.addSelectionChangedListener(showTestResultsAction);
		manager.add(showTestResultsAction);
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

	protected BuildsSummary getBuildsSummary() {
		BuildsSummary buildsSummary = new BuildsSummary();
		if (getContentProvider() != null) {
			for (IBuildPlan plan : getModel().getPlans()) {
				if (plan.getStatus() != null) {
					switch (plan.getStatus()) {
						case SUCCESS:
							buildsSummary.numSuccess++;
							break;
						case UNSTABLE:
							buildsSummary.numUnstable++;
							break;
						case FAILED:
							buildsSummary.numFailed++;
							break;
					}
				}
			}
		}
		return buildsSummary;
	}

	TreeViewer getViewer() {
		return viewer;
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		BuildsUiPlugin.getDefault().getPreferenceStore().addPropertyChangeListener(propertyChangeListener);

		// defer restore until view is created
		stateMemento = memento;
	}

	private void initActions() {
		collapseAllAction = new CollapseAllAction(viewer);
		expandAllAction = new ExpandAllAction(viewer);
		propertiesAction = new BuildElementPropertiesAction();
		refreshAutomaticallyAction = new RefreshAutomaticallyAction();
		filterDisabledAction = new FilterByStatusAction(this, BuildStatus.DISABLED);
		filterSucceedingAction = new FilterByStatusAction(this, BuildStatus.SUCCESS);
		filterSucceedingAction.setText(Messages.BuildsView_HideSucceedingPlans);
		filterSucceedingAction.setImageDescriptor(BuildImages.FILTER_SUCCEEDING);
		toggleFilterAction = new ToggleFilterAction();
		presentationsMenuAction = new PresentationMenuAction(this);
	}

	private void restoreState(IMemento memento) {
		IMemento child = memento.getChild("statusFilter"); //$NON-NLS-1$
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
				filterSucceedingAction.update();
			}
		}
		child = memento.getChild("presentation"); //$NON-NLS-1$
		if (child != null) {
			String id = child.getString("id"); //$NON-NLS-1$
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
				IMemento child = memento.createChild("statusFilter"); //$NON-NLS-1$
				for (BuildStatus status : statuses) {
					child.putBoolean(status.name(), true);
				}
			}
		}
		IMemento child = memento.createChild("presentation"); //$NON-NLS-1$
		child.putString("id", getContentProvider().getPresentation().name()); //$NON-NLS-1$
	}

	@Override
	public void setFocus() {
		getViewer().getControl().setFocus();
		if (BuildsUiPlugin.getDefault().getPreferenceStore().getBoolean(BuildsUiInternal.PREF_REFRESH_ON_FOCUS)) {
			RefreshOperation operation = BuildsUiInternal.getFactory().getRefreshOperation();
			operation.addFlag(OperationFlag.BACKGROUND);
			operation.execute();
		}
	}

	private void setTopControl(Control control) {
		if (stackLayout.topControl != control) {
			stackLayout.topControl = control;
			control.getParent().layout();
		}
	}

	@Override
	public boolean show(ShowInContext context) {
		if (context.getSelection() != null) {
			getViewer().setSelection(context.getSelection());
			return true;
		}
		return false;
	}

	private void updateContents(IStatus status) {
		boolean isShowingViewer = stackLayout.topControl == filteredTree;
		boolean hasContents = false;
		if (contentProvider != null) {
			if (getModel().getPlans().size() > 0 || getModel().getServers().size() > 0) {
				hasContents = true;
			}
		}
		if (hasContents) {
			setTopControl(filteredTree);
			if (!isShowingViewer) {
				// initial flip
				packColumnsAsync(viewer.getTree());
			}
		} else {
			setTopControl(messageComposite);
		}
		updateDecoration(status);
	}

	public void updateDecoration(IStatus status) {
		String statusMessage = ""; //$NON-NLS-1$
		if (status.isOK()) {
			BuildsSummary buildsSummary = getBuildsSummary();
			if (buildsSummary.isSuccess()) {
				setTitleImage(CommonImages.getImageWithOverlay(BuildImages.VIEW_BUILDS, CommonImages.OVERLAY_SUCCESS,
						false, false));
			} else if (buildsSummary.isUnstable()) {
				setTitleImage(CommonImages.getImageWithOverlay(BuildImages.VIEW_BUILDS, CommonImages.OVERLAY_FAILED,
						false, false));
			} else if (buildsSummary.isFailed()) {
				setTitleImage(CommonImages.getImageWithOverlay(BuildImages.VIEW_BUILDS, CommonImages.OVERLAY_ERROR,
						false, false));
			} else {
				setTitleImage(CommonImages.getImage(BuildImages.VIEW_BUILDS));
			}
			if (!buildsSummary.isEmpty()) {
				setContentDescription(buildsSummary.toString());
			} else {
				setContentDescription(""); //$NON-NLS-1$
			}
			if (lastRefresh != null) {
				statusMessage = NLS.bind(Messages.BuildsView_LastUpdate,
						DateFormat.getDateTimeInstance().format(lastRefresh));
			}
		} else {
			setTitleImage(CommonImages.getImageWithOverlay(BuildImages.VIEW_BUILDS, CommonImages.OVERLAY_WARNING, false,
					false));
			statusMessage = Messages.BuildsView_LastUpdateFailed;
		}

		if (statusMessage != null) {
			final IViewSite viewSite = getViewSite();
			if (viewSite != null) {
				final IStatusLineManager statusLine = viewSite.getActionBars().getStatusLineManager();
				if (statusLine != null) {
					updateStatusLine(statusMessage, statusLine);
				}
			}
		}
	}

	private void updateStatusLine(String statusMessage, final IStatusLineManager statusLine) {
		Display.getDefault().asyncExec(() -> {
			statusLine.setMessage(statusMessage);
		});
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (adapter == IShowInTargetList.class) {
			return adapter.cast((IShowInTargetList) () -> new String[] { "org.eclipse.team.ui.GenericHistoryView" }); //$NON-NLS-1$
		} else if (adapter == IShowInSource.class) {
			return adapter
					.cast((IShowInSource) () -> new ShowInContext(getViewer().getInput(), getViewer().getSelection()));
		}
		return super.getAdapter(adapter);
	}

	/**
	 * Packs all columns so that they are able to display all content. If there is any space left, the second column (summary) will be
	 * resized so that the entire width of the control is used.
	 *
	 * @param tree
	 *            the tree to resize
	 */
	private void packColumns(final Tree tree) {
		int totalColumnWidth = 0;
		for (TreeColumn tc : tree.getColumns()) {
			if (tc.getResizable()) {
				tc.pack();
				totalColumnWidth += tc.getWidth() + 1;
			} else {
				totalColumnWidth += 1; // TODO: Determine the exact width of the column separator
			}
		}

		// Adjust the width of the "Summary" column unless it's not resizeable
		// which case we adjust the width of the "Build" column instead.
		TreeColumn column = tree.getColumn(1).getResizable() ? tree.getColumn(1) : tree.getColumn(0);
		int nw = column.getWidth() + tree.getClientArea().width - totalColumnWidth;
		column.setWidth(nw);
	}

	protected BuildModel getModel() {
		return model;
	}

}
