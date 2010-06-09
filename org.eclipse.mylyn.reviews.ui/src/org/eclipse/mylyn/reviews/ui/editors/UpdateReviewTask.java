/*******************************************************************************
 * Copyright (c) 2010 Research Group for Industrial Software (INSO), Vienna University of Technology
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.mylyn.reviews.core.ReviewConstants;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.ui.editors.Messages;
import org.eclipse.mylyn.reviews.ui.ReviewCommentTaskAttachmentSource;
import org.eclipse.mylyn.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUi;

/*
 * @author Kilian Matt
 */
public class UpdateReviewTask extends Job {



	private TaskDataModel model;
	private Review review;
	private TaskRepository taskRepository;
	private AbstractRepositoryConnector connector;

	public UpdateReviewTask(TaskDataModel model, Review review) {
		super(Messages.UpdateReviewTask_Title);
		this.model = model;
		this.review = review;

		this.taskRepository = model.getTaskRepository();

		this.connector = TasksUi.getRepositoryConnector(taskRepository
				.getConnectorKind());
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		try {


			final byte[] attachmentBytes = createAttachment(model, review);

				TaskAttribute attachmentAttribute = model.getTaskData().getAttributeMapper()
						.createTaskAttachment( model.getTaskData());
				try {
					ReviewCommentTaskAttachmentSource attachment = new ReviewCommentTaskAttachmentSource(
							attachmentBytes);

					monitor.subTask(org.eclipse.mylyn.reviews.ui.Messages.CreateTask_UploadingAttachment);
					connector.getTaskAttachmentHandler().postContent(
							taskRepository, model.getTask(), attachment,
							"review result", //$NON-NLS-1$
							attachmentAttribute, monitor);
				} catch (CoreException e) {
					e.printStackTrace();
				}


			return new Status(IStatus.OK, ReviewsUiPlugin.PLUGIN_ID,
					org.eclipse.mylyn.reviews.ui.Messages.CreateTask_Success);
		} catch (Exception e) {
			return new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID, e
					.getMessage());
		}
	}

	private byte[] createAttachment(TaskDataModel model, Review review) {
		try {
			ResourceSet resourceSet = new ResourceSetImpl();

			Resource resource = resourceSet.createResource(URI
					.createFileURI("")); //$NON-NLS-1$

			resource.getContents().add(review);
			resource.getContents().add(review.getScope().get(0));
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

}
