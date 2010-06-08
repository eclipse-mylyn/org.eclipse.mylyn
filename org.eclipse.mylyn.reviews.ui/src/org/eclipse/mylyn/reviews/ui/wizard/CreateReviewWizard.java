package org.eclipse.mylyn.reviews.ui.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.mylyn.reviews.ui.editors.NewReviewTaskEditorInput;
import org.eclipse.mylyn.reviews.ui.editors.ReviewEditor;
import org.eclipse.mylyn.reviews.ui.editors.ReviewTaskEditorInput;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class CreateReviewWizard extends Wizard {

	private TaskDataModel model;
	private ReviewAssignmentPage assignmentsPage;
	private ReviewScopeWizardPage scopePage;
	private ChooseReviewTypeWizardPage typePage;
	public CreateReviewWizard(TaskDataModel model) {
		this.model=model;
		typePage=new ChooseReviewTypeWizardPage();
		scopePage=new ReviewScopeWizardPage();
		assignmentsPage=new ReviewAssignmentPage();
	}
	@Override
	public void addPages() {
		super.addPages();
		addPage(typePage);
		addPage(scopePage);
		addPage(assignmentsPage);
	}
	@Override
	public boolean performFinish() {

			try {
				ReviewTaskEditorInput input = new NewReviewTaskEditorInput(
						getModel(), scopePage.getSelectedPatch().create());

				IWorkbenchWindow window = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow();

				window.getActivePage().openEditor(input, ReviewEditor.ID, true);
			} catch (CoreException e1) {
				throw new RuntimeException(e1);
			}

		return true;
	}
	public TaskDataModel getModel() {
		return model;
	}

}