package org.eclipse.mylyn.reviews.ui.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.internal.provisional.tasks.core.TasksUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.reviews.core.model.review.Review;
import org.eclipse.mylyn.reviews.core.model.review.ReviewFactory;
import org.eclipse.mylyn.reviews.ui.CreateTask;
import org.eclipse.mylyn.reviews.ui.ReviewStatus;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public class CreateReviewWizard extends Wizard {

	private TaskDataModel model;
	private ReviewAssignmentPage assignmentsPage;
	private ReviewScopeWizardPage scopePage;
	private ChooseReviewTypeWizardPage typePage;

	public CreateReviewWizard(TaskDataModel model) {
		this.model = model;
		typePage = new ChooseReviewTypeWizardPage();
		scopePage = new ReviewScopeWizardPage();
		assignmentsPage = new ReviewAssignmentPage();
	}

	public void addPages() {
		super.addPages();
		addPage(typePage);
		addPage(scopePage);
		addPage(assignmentsPage);
	}

	public boolean performFinish() {

		try {
			Review review = ReviewFactory.eINSTANCE.createReview();
			review.getScope().add(scopePage.getScope());

			CreateTask createTask = new CreateTask(model, review,
					assignmentsPage.getReviewer());

			createTask.schedule();
			if (assignmentsPage.isOpenReviewOnFinish()) {

				createTask.addJobChangeListener(new JobChangeAdapter() {
					@Override
					public void done(IJobChangeEvent event) {
						super.done(event);
						IStatus result = event.getResult();
						if (result instanceof ReviewStatus) {
							final ReviewStatus reviewResult = (ReviewStatus) result;
							Display.getDefault().asyncExec(new Runnable() {

								public void run() {
									TasksUiInternal.synchronizeTaskInBackground(
											TasksUi.getRepositoryConnector(reviewResult
													.getTask()
													.getConnectorKind()),
											reviewResult.getTask());
									// TODO 
									TasksUiUtil.openTask(
											reviewResult.getTask());

								}
							});

						}
					}
				});
			}
		} catch (CoreException e1) {
			throw new RuntimeException(e1);
		}

		return true;
	}

	public TaskDataModel getModel() {
		return model;
	}

}