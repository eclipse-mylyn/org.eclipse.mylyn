/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.history;

import java.util.List;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest.Kind;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest.Scope;
import org.eclipse.mylyn.builds.internal.core.operations.BuildJob;
import org.eclipse.mylyn.builds.internal.core.operations.GetBuildsOperation;
import org.eclipse.mylyn.builds.internal.core.operations.OperationChangeEvent;
import org.eclipse.mylyn.builds.internal.core.operations.OperationChangeListener;
import org.eclipse.mylyn.commons.ui.AbstractColumnViewerSupport;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.commands.OpenHandler;
import org.eclipse.mylyn.internal.builds.ui.editor.BuildEditorInput;
import org.eclipse.mylyn.internal.builds.ui.view.BuildDurationLabelProvider;
import org.eclipse.mylyn.internal.builds.ui.view.BuildLabelProvider;
import org.eclipse.mylyn.internal.builds.ui.view.BuildSummaryLabelProvider;
import org.eclipse.mylyn.internal.builds.ui.view.BuildTimeLabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.team.ui.history.HistoryPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.progress.IWorkbenchSiteProgressService;

/**
 * @author Steffen Pingel
 */
public class BuildHistoryPage extends HistoryPage {

	private TreeViewer viewer;

	private BuildHistoryContentProvider contentProvider;

	private GetBuildsOperation refreshOperation;

	public BuildHistoryPage() {
	}

	@Override
	public boolean isValidInput(Object object) {
		return canShowHistoryFor(object);
	}

	@Override
	public void refresh() {
		inputSet();
	}

	public IBuildPlan getPlan() {
		if (getInput() instanceof IBuild) {
			return ((IBuild) getInput()).getPlan();
		} else if (getInput() instanceof IBuildPlan) {
			return (IBuildPlan) getInput();
		} else if (getInput() instanceof BuildEditorInput) {
			return ((BuildEditorInput) getInput()).getPlan();
		}
		return null;
	}

	@Override
	public String getName() {
		IBuildPlan plan = getPlan();
		return plan != null ? NLS.bind(Messages.BuildHistoryPage_buildPlan, plan.getLabel()) : null;
	}

	@Override
	public String getDescription() {
		return NLS.bind(Messages.BuildHistoryPage_buildHistoryFor, getName());
	}

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return null;
	}

	@Override
	public void dispose() {
		cancelRefresh();
		super.dispose();
	}

	@Override
	public boolean inputSet() {

		if (viewer == null) {
			return false;
		}

		final IBuildPlan plan = getPlan();
		if (plan != null && refreshOperation == null) {
			GetBuildsRequest request = new GetBuildsRequest(plan, Kind.ALL, Scope.HISTORY);
			refreshOperation = new GetBuildsOperation(BuildsUiInternal.getFactory().getService(), request) {
				@Override
				protected void schedule(List<BuildJob> jobs) {
					for (BuildJob job : jobs) {
						BuildHistoryPage.this.schedule(job);
					}
				}
			};
			refreshOperation.addOperationChangeListener(new OperationChangeListener() {
				@Override
				public void done(OperationChangeEvent event) {
					if (event.getStatus().isOK() && !Display.getDefault().isDisposed()) {
						final GetBuildsOperation operation = (GetBuildsOperation) event.getOperation();
						Display.getDefault().asyncExec(() -> {
							if (viewer.getControl() != null && !viewer.getControl().isDisposed()) {
								List<IBuild> builds = operation.getBuilds();
								if (builds != null) {
									for (IBuild build : builds) {
										build.setPlan(plan);
										build.setServer(plan.getServer());
									}
									viewer.setInput(builds);
								}
							}
						});
					}
					refreshOperation = null;
				}
			});
			refreshOperation.execute();
			return true;
		}
		return false;
	}

	private void cancelRefresh() {
		if (refreshOperation != null) {
			refreshOperation.cancel();
			refreshOperation = null;
		}
	}

	private IWorkbenchPartSite getWorkbenchSite() {
		final IWorkbenchPart part = getHistoryPageSite().getPart();
		return part != null ? part.getSite() : null;
	}

	private void schedule(final Job job) {
		final IWorkbenchPartSite site = getWorkbenchSite();
		if (site != null) {
			IWorkbenchSiteProgressService progress = site.getAdapter(IWorkbenchSiteProgressService.class);
			if (progress != null) {
				progress.schedule(job, 0, true);
				return;
			}
		}
		// fall-back
		job.schedule();
	}

	@Override
	public void createControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.FULL_SELECTION);
		Tree tree = viewer.getTree();
		tree.setHeaderVisible(true);

		TreeViewerColumn buildViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		buildViewerColumn
		.setLabelProvider(new DecoratingStyledCellLabelProvider(new BuildLabelProvider(true), null, null));
		TreeColumn buildColumn = buildViewerColumn.getColumn();
		buildColumn.setText(Messages.BuildHistoryPage_id);
		buildColumn.setWidth(70);
		buildColumn.setData(AbstractColumnViewerSupport.KEY_COLUMN_CAN_HIDE, false);

		TreeViewerColumn timeViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		timeViewerColumn.setLabelProvider(new BuildTimeLabelProvider());
		TreeColumn timeColumn = timeViewerColumn.getColumn();
		timeColumn.setText(Messages.BuildHistoryPage_time);
		timeColumn.setWidth(140);

		TreeViewerColumn durationViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		durationViewerColumn.setLabelProvider(new BuildDurationLabelProvider());
		TreeColumn durationColumn = durationViewerColumn.getColumn();
		durationColumn.setText(Messages.BuildHistoryPage_duration);
		durationColumn.setWidth(70);

		TreeViewerColumn summaryViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		summaryViewerColumn.setLabelProvider(new BuildSummaryLabelProvider());
		TreeColumn summaryColumn = summaryViewerColumn.getColumn();
		summaryColumn.setText(Messages.BuildHistoryPage_summary);
		summaryColumn.setWidth(220);

		contentProvider = new BuildHistoryContentProvider();
		viewer.setContentProvider(contentProvider);

		viewer.addDoubleClickListener(event -> {
			Object item = ((IStructuredSelection) event.getSelection()).getFirstElement();
			if (item instanceof IBuild build) {
				OpenHandler.fetchAndOpen(getSite().getPage(), build);
			}
		});
	}

	@Override
	public Control getControl() {
		return viewer.getControl();
	}

	@Override
	public void setFocus() {
		if (viewer != null) {
			viewer.getControl().setFocus();
		}
	}

	public static boolean canShowHistoryFor(Object object) {
		return object instanceof IBuildPlan || object instanceof IBuild || object instanceof BuildEditorInput;
	}

}
