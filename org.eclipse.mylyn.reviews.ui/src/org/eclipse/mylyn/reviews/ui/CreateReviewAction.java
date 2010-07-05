package org.eclipse.mylyn.reviews.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.reviews.ui.wizard.CreateReviewWizard;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.swt.widgets.Display;
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
				CreateReviewWizard wizard = new CreateReviewWizard(model,new PatchCreator(attachment.getTaskAttribute()).create());
				WizardDialog dialog = new WizardDialog(Display.getDefault()
						.getActiveShell(), wizard);
				dialog.create();
				dialog.open();

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

}
