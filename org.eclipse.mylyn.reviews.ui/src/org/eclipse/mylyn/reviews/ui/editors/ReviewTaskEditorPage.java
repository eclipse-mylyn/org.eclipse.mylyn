/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Kilian Matt (Research Group for Industrial Software (INSO), Vienna University of Technology) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.reviews.ui.editors;

import java.io.ByteArrayOutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.mylyn.internal.provisional.tasks.core.TasksUtil;
import org.eclipse.mylyn.internal.tasks.ui.editors.ToolBarButtonContribution;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.core.ReviewConstants;
import org.eclipse.mylyn.reviews.core.ReviewsUtil;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.ui.Images;
import org.eclipse.mylyn.reviews.ui.ReviewCommentTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/*
 * @author Kilian Matt
 */
public class ReviewTaskEditorPage extends AbstractTaskEditorPage {
	private static final String PAGE_ID = "org.eclipse.mylyn.reviews.ui.editors.ReviewTaskEditorPage"; //$NON-NLS-1$
	private Review review;
	private boolean dirty;
	private boolean submitResult;

	public ReviewTaskEditorPage(TaskEditor editor) {
		super(editor, PAGE_ID, "ReviewTaskFormPage", "mylynreviews"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	protected Set<TaskEditorPartDescriptor> createPartDescriptors() {
		Set<TaskEditorPartDescriptor> taskDescriptors = new HashSet<TaskEditorPartDescriptor>();
		try {
			TaskData data = TasksUi.getTaskDataManager().getTaskData(getTask());
			if (data != null) {
				taskDescriptors.add(new TaskEditorPartDescriptor(
						DelegateReviewTaskEditorPart.ID_PART_REVIEW) {
					@Override
					public AbstractTaskEditorPart createPart() {
						return new DelegateReviewTaskEditorPart();
					}

				});
				taskDescriptors.add(new TaskEditorPartDescriptor(
						ReviewTaskEditorPart.ID_PART_REVIEW) {
					@Override
					public AbstractTaskEditorPart createPart() {
						return new ReviewTaskEditorPart();
					}

				});
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		return taskDescriptors;
	}

	public void setSubmitResult(boolean submitResult) {
		this.submitResult = submitResult;
	}

	@Override
	public synchronized void doSubmit() {
		if (isDirty()) {

			TaskRepository taskRepository = getModel().getTaskRepository();
			AbstractRepositoryConnector connector = TasksUi
					.getRepositoryConnector(taskRepository.getConnectorKind());
			if (submitResult) {
				final TaskDataModel model = getModel();
				Review review = getReview();
				TaskAttribute attachmentAttribute = model.getTaskData()
						.getAttributeMapper()
						.createTaskAttachment(model.getTaskData());

				final byte[] attachmentBytes = createAttachment(model, review);

				ReviewCommentTaskAttachmentSource attachment = new ReviewCommentTaskAttachmentSource(
						attachmentBytes);

				if (connector != null) {
					TasksUiInternal
							.getJobFactory()
							.createSubmitTaskAttachmentJob(connector,
									model.getTaskRepository(), model.getTask(),
									attachment, "review result",
									attachmentAttribute).schedule();
				}
			} else {
				try {
					getModel().save(new NullProgressMonitor());
					SubmitJob submitJob = TasksUiInternal.getJobFactory()
							.createSubmitTaskJob(connector, taskRepository,
									getTask(), getModel().getTaskData(),
									getModel().getChangedOldAttributes());
					submitJob.schedule();
					;
				} catch (CoreException e) {
					e.printStackTrace();
				}
			}
			dirty = false;
		}
	}

	private byte[] createAttachment(TaskDataModel model, Review review) {
		try {
			ResourceSet resourceSet = new ResourceSetImpl();

			Resource resource = resourceSet.createResource(URI
					.createFileURI("")); //$NON-NLS-1$

			resource.getContents().add(review);
			resource.getContents().add(review.getScope().get(0));
			if (review.getResult() != null)
				resource.getContents().add(review.getResult());
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			ZipOutputStream outputStream = new ZipOutputStream(
					byteArrayOutputStream);
			outputStream.putNextEntry(new ZipEntry(
					ReviewConstants.REVIEW_DATA_FILE));
			resource.save(outputStream, null);
			outputStream.closeEntry();
			outputStream.close();
			return byteArrayOutputStream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}

	}

	@Override
	public void fillToolBar(IToolBarManager toolBarManager) {

		ToolBarButtonContribution submitButtonContribution = new ToolBarButtonContribution(
				"org.eclipse.mylyn.tasks.toolbars.submit") { //$NON-NLS-1$
			@Override
			protected Control createButton(Composite composite) {
				Button submitButton = new Button(composite, SWT.FLAT);
				submitButton.setText("Submit Review");
				submitButton.setImage(Images.SMALL_ICON.createImage());
				submitButton.setBackground(null);
				submitButton.addListener(SWT.Selection, new Listener() {
					public void handleEvent(Event e) {
						doSubmit();
					}
				});
				return submitButton;
			}
		};
		submitButtonContribution.marginLeft = 10;
		toolBarManager.add(submitButtonContribution);

	}

	public Review getReview() {
		if (review == null) {
			try {
				final TaskDataModel model = getModel();
				List<Review> reviews = ReviewsUtil.getReviewAttachmentFromTask(
						TasksUi.getTaskDataManager(),
						TasksUi.getRepositoryModel(), model.getTask());

				if (reviews.size() > 0) {
					review = reviews.get(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return review;
	}

	public void setDirty() {
		this.dirty = true;
	}

	@Override
	public boolean isDirty() {
		return dirty || super.isDirty();
	}
}
