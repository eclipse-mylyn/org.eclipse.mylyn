package org.eclipse.mylyn.reviews.ui.editors;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.core.ReviewConstants;
import org.eclipse.mylyn.reviews.core.ReviewData;
import org.eclipse.mylyn.reviews.core.ReviewDataManager;
import org.eclipse.mylyn.reviews.core.ReviewsUtil;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.ui.ReviewCommentTaskAttachmentSource;
import org.eclipse.mylyn.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPage;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.ITaskEditorPartDescriptorAdvisor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

public class ReviewTaskEditorPartAdvisor implements
		ITaskEditorPartDescriptorAdvisor {

	public boolean canCustomize(ITask task) {
		if (!ReviewsUtil.hasReviewMarker(task)) {
			try {
				IRepositoryModel repositoryModel = TasksUi.getRepositoryModel();
				TaskData taskData = TasksUiPlugin.getTaskDataManager()
						.getTaskData(task);
				if (ReviewsUtil.getReviewAttachments(repositoryModel, taskData)
						.size() > 0) {
					ReviewsUtil.markAsReview(task);
				}
			} catch (CoreException e) {
				// FIXME
				e.printStackTrace();
			}
		}

		boolean isReview = ReviewsUtil.isMarkedAsReview(task);
		return isReview;
	}

	public Set<String> getBlockingIds(ITask task) {
		return Collections.emptySet();
	}

	public Set<String> getBlockingPaths(ITask task) {
		Set<String> blockedPaths = new HashSet<String>();
		blockedPaths.add(AbstractTaskEditorPage.PATH_ATTRIBUTES);
		blockedPaths.add(AbstractTaskEditorPage.PATH_ATTACHMENTS);
		blockedPaths.add(AbstractTaskEditorPage.PATH_PLANNING);

		return blockedPaths;
	}

	public Set<TaskEditorPartDescriptor> getPartContributions(ITask task) {
		Set<TaskEditorPartDescriptor> parts = new HashSet<TaskEditorPartDescriptor>();
		parts.add(new TaskEditorPartDescriptor(
				ReviewTaskEditorPart.ID_PART_REVIEW) {

			@Override
			public AbstractTaskEditorPart createPart() {
				return new ReviewTaskEditorPart();
			}
		});
		return parts;
	}

	public void taskMigration(ITask oldTask, ITask newTask) {
		ReviewDataManager dataManager = ReviewsUiPlugin.getDataManager();
		Review review = dataManager.getReviewData(oldTask).getReview();
		dataManager.storeOutgoingTask(newTask, review);
		ReviewsUtil.markAsReview(newTask);
	}

	public void afterSubmit(ITask task) {
		try {
			ReviewData reviewData = ReviewsUiPlugin.getDataManager()
					.getReviewData(task);
			Review review = reviewData.getReview();

			if (reviewData.isOutgoing() || reviewData.isDirty()) {
				TaskRepository taskRepository = TasksUiPlugin
						.getRepositoryManager().getRepository(
								task.getRepositoryUrl());
				TaskData taskData = TasksUiPlugin.getTaskDataManager()
						.getTaskData(task);
				// todo get which attachments have to be submitted
				TaskAttribute attachmentAttribute = taskData
						.getAttributeMapper().createTaskAttachment(taskData);
				byte[] attachmentBytes = createAttachment(review);

				ReviewCommentTaskAttachmentSource attachment = new ReviewCommentTaskAttachmentSource(
						attachmentBytes);

				AbstractRepositoryConnector connector = TasksUi
						.getRepositoryConnector(taskRepository
								.getConnectorKind());
				connector.getTaskAttachmentHandler().postContent(
						taskRepository, task, attachment, "review result", //$NON-NLS-1$
						attachmentAttribute, new NullProgressMonitor());

				TasksUiInternal.synchronizeTask(connector, task, false, null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	public void prepareSubmit(ITask task) {
	}

	private byte[] createAttachment(Review review) {
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

}