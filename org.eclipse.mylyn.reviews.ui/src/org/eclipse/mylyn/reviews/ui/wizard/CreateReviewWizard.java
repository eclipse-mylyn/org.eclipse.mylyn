package org.eclipse.mylyn.reviews.ui.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.tasks.core.data.TaskDataManager;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.core.ReviewConstants;
import org.eclipse.mylyn.reviews.core.ReviewDataManager;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.core.model.review.ReviewFactory;
import org.eclipse.mylyn.reviews.core.model.review.ScopeItem;
import org.eclipse.mylyn.reviews.ui.ReviewsUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskMapping;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.core.data.TaskMapper;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.commands.Priority;

public class CreateReviewWizard extends Wizard {

	private TaskDataModel model;
	private ReviewAssignmentPage assignmentsPage;
	private ReviewScopeWizardPage scopePage;
	private ChooseReviewTypeWizardPage typePage;
	private ScopeItem scope;

	public CreateReviewWizard(TaskDataModel model) {
		this.model = model;
		
	}

	public CreateReviewWizard(TaskDataModel model, ScopeItem scope) {
		this(model);
		this.scope = scope;
	}

	public void addPages() {
		super.addPages();
		if(scope==null) {
			scopePage = new ReviewScopeWizardPage();
			typePage = new ChooseReviewTypeWizardPage();
			addPage(typePage);
			addPage(scopePage);
		}
		assignmentsPage = new ReviewAssignmentPage();
		addPage(assignmentsPage);
	}

	public boolean performFinish() {

		try {
			Review review = ReviewFactory.eINSTANCE.createReview();
			review.getScope().add(getScope());
			TaskRepository taskRepository=model.getTaskRepository();
			ITask newTask = TasksUiUtil.createOutgoingNewTask(taskRepository.getConnectorKind(), taskRepository.getRepositoryUrl());

			newTask.setAttribute(ReviewConstants.ATTR_REVIEW_FLAG, Boolean.TRUE.toString());
			TaskMapper initializationData=new TaskMapper(model.getTaskData());
			TaskData taskData = TasksUiInternal.createTaskData(taskRepository, initializationData, null,
					new NullProgressMonitor());
			AbstractRepositoryConnector connector=TasksUiPlugin.getConnector(taskRepository.getConnectorKind());
			connector.getTaskDataHandler().initializeSubTaskData(
					taskRepository, taskData, model.getTaskData(),
					new NullProgressMonitor());
			String reviewer = assignmentsPage.getReviewer();

			taskData.getRoot().getMappedAttribute(TaskAttribute.SUMMARY).setValue("Review of " + model.getTask().getSummary());
			taskData.getRoot().getMappedAttribute(TaskAttribute.USER_ASSIGNED).setValue(reviewer);

			ReviewsUiPlugin.getDataManager().storeOutgoingTask(newTask, review);
			
			
			TasksUiInternal.createAndOpenNewTask(newTask, taskData);
		} catch (CoreException e1) {
			throw new RuntimeException(e1);
		}

		return true;
	}

	private ScopeItem getScope() throws CoreException {
		if (scope != null) {
			return scope;
		} else {
			return scopePage.getScope();
		}
	}

	public TaskDataModel getModel() {
		return model;
	}


	private TaskAttribute createAttribute( TaskData taskData, String mappedAttributeName, String value) {
		TaskAttribute attribute = taskData.getRoot().createMappedAttribute(mappedAttributeName);
		attribute.setValue(value);
		return attribute;
	}
}