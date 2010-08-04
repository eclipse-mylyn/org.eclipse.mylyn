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
package org.eclipse.mylyn.reviews.ui;

import java.io.ByteArrayOutputStream;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.mylyn.internal.tasks.ui.editors.TaskMigrator;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.core.ReviewConstants;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.sync.SubmitJob;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;

/*
 * @author Kilian Matt
 */
public class CreateTask extends Job {

	private TaskDataModel model;
	private Review review;
	private TaskRepository taskRepository;
	private AbstractRepositoryConnector connector;
	private String reviewer;

	public CreateTask(TaskDataModel model, Review review, String reviewer) {
		super(Messages.CreateTask_Title);
		this.model = model;
		this.review = review;

		this.taskRepository = model.getTaskRepository();

		this.connector = TasksUi.getRepositoryConnector(taskRepository
				.getConnectorKind());
		this.reviewer = reviewer;
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		try {

			final ITask newLocalTask = TasksUiUtil.createOutgoingNewTask(
					taskRepository.getConnectorKind(),
					taskRepository.getRepositoryUrl());

			TaskAttributeMapper mapper = connector.getTaskDataHandler()
					.getAttributeMapper(taskRepository);

			final TaskData data = new TaskData(mapper,
					taskRepository.getConnectorKind(),
					taskRepository.getRepositoryUrl(), ""); //$NON-NLS-1$

			connector.getTaskDataHandler().initializeSubTaskData(
					taskRepository, data, model.getTaskData(),
					new NullProgressMonitor());

			if (reviewer != null && reviewer.isEmpty()) {
				createAttribute(data,TaskAttribute.USER_ASSIGNED,reviewer);
			}

			createAttribute(data,TaskAttribute.SUMMARY,
							"Review of " + model.getTask().getTaskKey() + " " + model.getTask().getSummary()); //$NON-NLS-1$ //$NON-NLS-2$

			createAttribute(data,TaskAttribute.COMPONENT,
							model.getTaskData()
									.getRoot()
									.getMappedAttribute(TaskAttribute.COMPONENT)
									.getValue());
			createAttribute(data,TaskAttribute.STATUS, "NEW"); //$NON-NLS-1$
			createAttribute(data,TaskAttribute.VERSION,
							model.getTaskData().getRoot()
									.getMappedAttribute(TaskAttribute.VERSION)
									.getValue());
			createAttribute(data,TaskAttribute.PRODUCT,
							model.getTaskData().getRoot()
									.getMappedAttribute(TaskAttribute.PRODUCT)
									.getValue());

			final byte[] attachmentBytes = createAttachment(model, review);

			final SubmitJob submitJob = TasksUiInternal.getJobFactory()
					.createSubmitTaskJob(connector, taskRepository,
							newLocalTask, data, new TreeSet<TaskAttribute>());
			submitJob.schedule();
			submitJob.join();

			if (submitJob.getStatus() == null) {
				ITask newRepoTask = submitJob.getTask();

				TaskMigrator migrator = new TaskMigrator(newLocalTask);
				migrator.setDelete(true);
				migrator.execute(newRepoTask);

				TaskAttribute attachmentAttribute = data.getAttributeMapper()
						.createTaskAttachment(data);
				try {
					ReviewCommentTaskAttachmentSource attachment = new ReviewCommentTaskAttachmentSource(
							attachmentBytes);

					monitor.subTask(Messages.CreateTask_UploadingAttachment);
					connector.getTaskAttachmentHandler().postContent(
							taskRepository, newRepoTask, attachment,
							"review result", //$NON-NLS-1$
							attachmentAttribute, monitor);
				} catch (CoreException e) {
					e.printStackTrace();
				}

				return new ReviewStatus(Messages.CreateTask_Success,
						newRepoTask);
			}
			return new Status(IStatus.WARNING, ReviewsUiPlugin.PLUGIN_ID, "");
		} catch (Exception e) {
			return new Status(IStatus.ERROR, ReviewsUiPlugin.PLUGIN_ID,
					e.getMessage());
		}
	}
	private TaskAttribute createAttribute( TaskData taskData, String mappedAttributeName, String value) {
		TaskAttribute attribute = taskData.getRoot().createMappedAttribute(mappedAttributeName);
		attribute.setValue(value);
		return attribute;
	}

	private byte[] createAttachment(TaskDataModel model, Review review) {
		try {
			ResourceSet resourceSet = new ResourceSetImpl();

			Resource resource = resourceSet.createResource(URI
					.createFileURI("")); //$NON-NLS-1$

			resource.getContents().add(review);
			resource.getContents().add(review.getScope().get(0));
			if(review.getResult()!=null)
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
