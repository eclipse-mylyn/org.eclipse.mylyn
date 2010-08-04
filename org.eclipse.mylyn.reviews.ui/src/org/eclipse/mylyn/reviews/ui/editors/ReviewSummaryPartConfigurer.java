package org.eclipse.mylyn.reviews.ui.editors;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.tasks.ui.editors.ITaskEditorPartDescriptorAdvisor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditorPartDescriptor;

public class ReviewSummaryPartConfigurer implements
		ITaskEditorPartDescriptorAdvisor {

	public boolean canCustomize(ITask task) {
		try {

			// TODO change to detecting review sub tasks!

			TaskData taskData = TasksUi.getTaskDataManager().getTaskData(task);
			IRepositoryModel repositoryModel = TasksUi.getRepositoryModel();
			if (taskData != null) {
				List<TaskAttribute> attributesByType = taskData
						.getAttributeMapper().getAttributesByType(taskData,
								TaskAttribute.TYPE_ATTACHMENT);
				for (TaskAttribute attribute : attributesByType) {
					// TODO move RepositoryModel.createTaskAttachment to
					// interface?
					ITaskAttachment taskAttachment = ((RepositoryModel) repositoryModel)
							.createTaskAttachment(attribute);
					if (taskAttachment.isPatch())
						return true;

				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return false;
	}

	public Set<String> getBlockingIds(ITask task) {
		return Collections.emptySet();
	}

	public Set<String> getBlockingPaths(ITask task) {
		return Collections.emptySet();
	}

	public Set<TaskEditorPartDescriptor> getPartContributions(ITask task) {
		Set<TaskEditorPartDescriptor> descriptors = new HashSet<TaskEditorPartDescriptor>();
		descriptors.add(new TaskEditorPartDescriptor(
				ReviewSummaryTaskEditorPart.ID_PART_REVIEWSUMMARY) {

			@Override
			public AbstractTaskEditorPart createPart() {
				return new ReviewSummaryTaskEditorPart();
			}
		});
		return descriptors;
	}

	public void afterSubmit(ITask task) {
	}

	public void prepareSubmit(ITask task) {
	}

	public void taskMigration(ITask oldTask, ITask newTask) {
	}
}
