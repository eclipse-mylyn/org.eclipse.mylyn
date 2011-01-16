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

package org.eclipse.mylyn.internal.gerrit.ui.editor;

import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.mylyn.internal.gerrit.core.GerritConnector;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritClient;
import org.eclipse.mylyn.internal.gerrit.core.client.GerritException;
import org.eclipse.mylyn.internal.gerrit.ui.GerritUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.editors.AbstractTaskEditorSection;
import org.eclipse.mylyn.reviews.core.model.IFileItem;
import org.eclipse.mylyn.reviews.core.model.IReview;
import org.eclipse.mylyn.reviews.core.model.IReviewItem;
import org.eclipse.mylyn.reviews.core.model.IReviewItemSet;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import com.atlassian.connector.eclipse.internal.crucible.ui.annotations.CrucibleCompareAnnotationModel;
import com.atlassian.connector.eclipse.internal.crucible.ui.operations.CrucibleFileInfoCompareEditorInput;

/**
 * @author Steffen Pingel
 */
public class ReviewSection extends AbstractTaskEditorSection {

	private class GetReviewJob extends Job {

		private final TaskRepository repository;

		private IReview review;

		private final String reviewId;

		public GetReviewJob(TaskRepository repository, String reviewId) {
			super("Get Review");
			this.repository = repository;
			this.reviewId = reviewId;
		}

		public IReview getReview() {
			return review;
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			GerritConnector connector = (GerritConnector) TasksUi.getRepositoryConnector(repository.getConnectorKind());
			GerritClient client = connector.getClient(repository);
			try {
				review = client.getReview(reviewId, monitor);
			} catch (GerritException e) {
				return new Status(IStatus.ERROR, GerritUiPlugin.PLUGIN_ID, "Review retrieval failed", e);
			}
			return Status.OK_STATUS;
		}

	}

	private Composite composite;

	private GetReviewJob job;

	private FormToolkit toolkit;

	public ReviewSection() {
		setPartName("Review");
	}

	@Override
	public void dispose() {
		job.cancel();
		super.dispose();
	}

	@Override
	public void initialize(AbstractTaskEditorPage taskEditorPage) {
		super.initialize(taskEditorPage);
		job = new GetReviewJob(taskEditorPage.getTaskRepository(), taskEditorPage.getTask().getTaskId());
		job.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(final IJobChangeEvent event) {
				if (event.getResult().isOK()) {
					Display.getDefault().asyncExec(new Runnable() {
						public void run() {
							if (getControl() != null && !getControl().isDisposed()) {
								updateContent(((GetReviewJob) event.getJob()).getReview());
							}
						}

					});
				}
			}
		});
	}

	private void updateContent(IReview review) {
		for (IReviewItem item : review.getItems()) {
			if (item instanceof IReviewItemSet) {
				createSubSection(review, (IReviewItemSet) item);
			}
		}
		getTaskEditorPage().reflow();
	}

	private void createSubSection(final IReview review, IReviewItemSet item) {
		int style = ExpandableComposite.TWISTIE;
		if (item.getItems().size() > 0) {
			style |= ExpandableComposite.EXPANDED;
		}
		Section subSection = toolkit.createSection(composite, style);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(subSection);
		subSection.setText(item.getName());

		subSection.setTextClient(toolkit.createLabel(subSection, item.getId()));

		if (item.getItems().size() > 0) {
			ListViewer viewer = new ListViewer(subSection);
			viewer.setContentProvider(new IStructuredContentProvider() {
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
					// ignore
				}

				public void dispose() {
					// ignore					
				}

				public Object[] getElements(Object inputElement) {
					return ((List) inputElement).toArray();
				}
			});
			viewer.setInput(item.getItems());
			viewer.setLabelProvider(new LabelProvider() {
				@Override
				public String getText(Object element) {
					return ((IFileItem) element).getName();
				}
			});
			viewer.addOpenListener(new IOpenListener() {
				public void open(OpenEvent event) {
					IStructuredSelection selection = (IStructuredSelection) event.getSelection();
					IFileItem item = (IFileItem) selection.getFirstElement();
					CrucibleCompareAnnotationModel model = new CrucibleCompareAnnotationModel(item, review, null);
					CompareConfiguration configuration = new CompareConfiguration();
					CompareUI.openCompareEditor(new CrucibleFileInfoCompareEditorInput(item, model, configuration));
				}
			});
			subSection.setClient(viewer.getControl());
		}
	}

	@Override
	protected Control createContent(FormToolkit toolkit, Composite parent) {
		this.toolkit = toolkit;

		job.schedule();

		composite = toolkit.createComposite(parent);
		GridLayoutFactory.fillDefaults().applyTo(composite);
		return composite;
	}

	@Override
	protected boolean shouldExpandOnCreate() {
		return true;
	}

}
