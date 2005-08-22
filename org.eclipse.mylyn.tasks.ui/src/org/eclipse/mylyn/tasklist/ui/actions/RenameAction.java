package org.eclipse.mylar.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.tasklist.ITaskListElement;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.SWT;

public class RenameAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.rename";
	
	private TaskListView view;
	
	public RenameAction(TaskListView view) {
		this.view = view;
		setText("Rename");
		setId(ID);
		setAccelerator(SWT.F2);
	}

	@Override
	public void run() {
		Object selectedObject = ((IStructuredSelection) this.view
				.getViewer().getSelection()).getFirstElement();
		if(selectedObject instanceof ITaskListElement){
			ITaskListElement element = (ITaskListElement)selectedObject;
			view.setInRenameAction(true);
			view.getViewer().editElement(element, 3);
			view.setInRenameAction(false);
		}
	}
}
