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
import org.eclipse.mylyn.internal.provisional.tasks.core.TasksUtil;
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

	public CreateTask(TaskDataModel model, Review review) {
		super(Messages.CreateTask_Title);
		this.model = model;
		this.review = review;

		this.taskRepository = model.getTaskRepository();

		this.connector = TasksUi.getRepositoryConnector(taskRepository
				.getConnectorKind());
	}

	@Override
	protected IStatus run(final IProgressMonitor monitor) {
		try {

			final ITask newLocalTask = TasksUiUtil.createOutgoingNewTask(
					taskRepository.getConnectorKind(), taskRepository
							.getRepositoryUrl());

			TaskAttributeMapper mapper = connector.getTaskDataHandler()
					.getAttributeMapper(taskRepository);

			final TaskData data = new TaskData(mapper, taskRepository
					.getConnectorKind(), taskRepository.getRepositoryUrl(), ""); //$NON-NLS-1$

			connector.getTaskDataHandler().initializeSubTaskData(
					taskRepository, data, model.getTaskData(),
					new NullProgressMonitor());

			data.getRoot()
					.createMappedAttribute(TaskAttribute.SUMMARY)
					.setValue(
							"Review of " + model.getTask().getTaskKey() + " " + model.getTask().getSummary()); //$NON-NLS-1$ //$NON-NLS-2$

			// TODO - set human readable Result here
			 data.getRoot().createMappedAttribute(TaskAttribute.DESCRIPTION).setValue("Result");

			data.getRoot().createMappedAttribute(TaskAttribute.COMPONENT)
					.setValue(
							model.getTaskData().getRoot().getMappedAttribute(
									TaskAttribute.COMPONENT).getValue());
			data.getRoot().createMappedAttribute(TaskAttribute.STATUS)
					.setValue("NEW"); //$NON-NLS-1$
			data.getRoot().createMappedAttribute(TaskAttribute.VERSION)
					.setValue(
							model.getTaskData().getRoot().getMappedAttribute(
									TaskAttribute.VERSION).getValue());
			data.getRoot().createMappedAttribute(TaskAttribute.PRODUCT)
					.setValue(
							model.getTaskData().getRoot().getMappedAttribute(
									TaskAttribute.PRODUCT).getValue());

			// TODO get correct status
			// data.getRoot().createMappedAttribute(TaskAttribute.STATUS).setValue("FIXED");

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

			}

			return new Status(IStatus.OK, ReviewsUiPlugin.PLUGIN_ID,
					Messages.CreateTask_Success);
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
