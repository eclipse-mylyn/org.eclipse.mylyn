package org.eclipse.mylyn.reviews.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.core.ReviewConstants;
import org.eclipse.mylyn.reviews.core.ReviewsUtil;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.core.model.review.ReviewFactory;
import org.eclipse.mylyn.reviews.core.model.review.ScopeItem;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.IActionDelegate;

public class CreateReviewAction extends Action implements IActionDelegate {

	private ITaskAttachment attachment;

	public void run(IAction action) {
		if (attachment != null) {
			try {
				ITaskDataWorkingCopy taskDataState = TasksUiPlugin
						.getTaskDataManager().getWorkingCopy(
								attachment.getTask());
				TaskDataModel model = new TaskDataModel(
						attachment.getTaskRepository(), attachment.getTask(),
						taskDataState);
				
				performFinish(model, new PatchCreator(attachment.getTaskAttribute()).create());
			} catch (CoreException e) {
				throw new RuntimeException(e);
			}
		}

	}

	public void selectionChanged(IAction action, ISelection selection) {
		action.setEnabled(false);
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection structuredSelection = (IStructuredSelection) selection;
			if (structuredSelection.size() == 1) {
				attachment = (ITaskAttachment) structuredSelection
						.getFirstElement();
				if (attachment.isPatch()) {
					action.setEnabled(true);
				}
			}
		}
	}

	
	public boolean performFinish(TaskDataModel model,ScopeItem scope) {
		String reviewer=model.getTaskRepository().getUserName();
		try {
			Review review = ReviewFactory.eINSTANCE.createReview();
			review.getScope().add(scope);
			TaskRepository taskRepository=model.getTaskRepository();
			ITask newTask = TasksUiUtil.createOutgoingNewTask(taskRepository.getConnectorKind(), taskRepository.getRepositoryUrl());

			ReviewsUtil.markAsReview(newTask);
			TaskMapper initializationData=new TaskMapper(model.getTaskData());
			TaskData taskData = TasksUiInternal.createTaskData(taskRepository, initializationData, null,
					new NullProgressMonitor());
			AbstractRepositoryConnector connector=TasksUiPlugin.getConnector(taskRepository.getConnectorKind());
			connector.getTaskDataHandler().initializeSubTaskData(
					taskRepository, taskData, model.getTaskData(),
					new NullProgressMonitor());

			
			taskData.getRoot().getMappedAttribute(TaskAttribute.SUMMARY).setValue("Review of " + model.getTask().getSummary());
			taskData.getRoot().getMappedAttribute(TaskAttribute.USER_ASSIGNED).setValue(reviewer);
			taskData.getRoot().getMappedAttribute(TaskAttribute.DESCRIPTION).setValue("Review of " + model.getTask().getSummary() );

			ReviewsUiPlugin.getDataManager().storeOutgoingTask(newTask, review);
			
			
			TasksUiInternal.createAndOpenNewTask(newTask, taskData);
		} catch (CoreException e1) {
			throw new RuntimeException(e1);
		}

		return true;
	}

}
