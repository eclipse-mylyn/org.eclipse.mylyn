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

package org.eclipse.mylyn.internal.builds.ui.history;

import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.DecoratingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
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
import org.eclipse.mylyn.builds.ui.BuildsUiConstants;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.ui.AbstractColumnViewerSupport;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
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
import org.eclipse.ui.PartInitException;
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

	public boolean isValidInput(Object object) {
		return canShowHistoryFor(object);
	}

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

	public String getName() {
		IBuildPlan plan = getPlan();
		return (plan != null) ? NLS.bind("Build Plan {0}", plan.getLabel()) : null;
	}

	public String getDescription() {
		return NLS.bind("Build history for {0}", getName());
	}

	public Object getAdapter(@SuppressWarnings("rawtypes")
	Class adapter) {
		return null;
	}

	@Override
	public void dispose() {
		cancelRefresh();
		super.dispose();
	}

	@Override
	public boolean inputSet() {
		cancelRefresh();

		if (viewer == null) {
			return false;
		}

		final IBuildPlan plan = getPlan();
		if (plan != null) {
			GetBuildsRequest request = new GetBuildsRequest(plan, Kind.ALL, Scope.HISTORY);
			refreshOperation = new GetBuildsOperation(BuildsUiInternal.getFactory().getService(), request) {
				protected void schedule(List<BuildJob> jobs) {
					for (BuildJob job : jobs) {
						BuildHistoryPage.this.schedule(job);
					}
				}
			};
			refreshOperation.addOperationChangeListener(new OperationChangeListener() {
				@Override
				public void done(OperationChangeEvent event) {
					if (!event.getStatus().isOK()) {
						return;
					}
					if (Display.getDefault().isDisposed()) {
						return;
					}
					final GetBuildsOperation operation = (GetBuildsOperation) event.getOperation();
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
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
						}
					});
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
			IWorkbenchSiteProgressService progress = (IWorkbenchSiteProgressService) site.getAdapter(IWorkbenchSiteProgressService.class);
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
		buildViewerColumn.setLabelProvider(new DecoratingStyledCellLabelProvider(new BuildLabelProvider(true), null,
				null));
		TreeColumn buildColumn = buildViewerColumn.getColumn();
		buildColumn.setText("ID");
		buildColumn.setWidth(70);
		buildColumn.setData(AbstractColumnViewerSupport.KEY_COLUMN_CAN_HIDE, false);

		TreeViewerColumn timeViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		timeViewerColumn.setLabelProvider(new BuildTimeLabelProvider());
		TreeColumn timeColumn = timeViewerColumn.getColumn();
		timeColumn.setText("Time");
		timeColumn.setWidth(140);

		TreeViewerColumn durationViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		durationViewerColumn.setLabelProvider(new BuildDurationLabelProvider());
		TreeColumn durationColumn = durationViewerColumn.getColumn();
		durationColumn.setText("Duration");
		durationColumn.setWidth(70);

		TreeViewerColumn summaryViewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
		summaryViewerColumn.setLabelProvider(new BuildSummaryLabelProvider());
		TreeColumn summaryColumn = summaryViewerColumn.getColumn();
		summaryColumn.setText("Summary");
		summaryColumn.setWidth(220);

		contentProvider = new BuildHistoryContentProvider();
		viewer.setContentProvider(contentProvider);

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				Object item = ((IStructuredSelection) event.getSelection()).getFirstElement();
				if (item instanceof IBuild) {
					IBuild build = (IBuild) item;
					final IBuildPlan plan = build.getPlan();
					GetBuildsRequest request = new GetBuildsRequest(build.getPlan(),
							Collections.singletonList(build.getLabel()), Scope.FULL);
					GetBuildsOperation operation = BuildsUiInternal.getFactory().getGetBuildsOperation(request);
					operation.addOperationChangeListener(new OperationChangeListener() {
						@Override
						public void done(OperationChangeEvent event) {
							if (!event.getStatus().isOK()) {
								return;
							}
							if (Display.getDefault().isDisposed()) {
								return;
							}
							final GetBuildsOperation operation = (GetBuildsOperation) event.getOperation();
							Display.getDefault().asyncExec(new Runnable() {
								public void run() {
									if (viewer.getControl() != null && !viewer.getControl().isDisposed()) {
										IBuild build2 = operation.getBuilds().get(0);
										build2.setPlan(plan);
										build2.setServer(plan.getServer());
										BuildEditorInput input = new BuildEditorInput(build2);
										try {
											getSite().getPage().openEditor(input, BuildsUiConstants.ID_EDITOR_BUILDS);
										} catch (PartInitException e) {
											StatusHandler.log(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN,
													"Unexpected error while opening build", e)); //$NON-NLS-1$
										}
									}
								}
							});
						}
					});
					operation.execute();
				}
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
