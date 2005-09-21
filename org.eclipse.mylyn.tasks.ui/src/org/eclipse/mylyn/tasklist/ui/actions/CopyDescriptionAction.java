package org.eclipse.mylar.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylar.tasklist.ITaskListElement;
import org.eclipse.mylar.tasklist.TaskListImages;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;

public class CopyDescriptionAction extends Action {

	private static final String DESCRIPTION_PREFIX = "Bugzilla Bug ";

	public static final String ID = "org.eclipse.mylar.tasklist.actions.copy";
	
	private TaskListView view;
	
	public CopyDescriptionAction(TaskListView view) {
		this.view = view;
		setText("Copy Description");
		setToolTipText("Copy Description");
		setId(ID);
		setImageDescriptor(TaskListImages.COPY);
	}

	@Override
	public void run() {
		 ISelection selection = this.view.getViewer().getSelection();
	    Object obj = ((IStructuredSelection)selection).getFirstElement();
	    if (obj instanceof ITaskListElement) {
	    	ITaskListElement element = (ITaskListElement)obj;
	    	String description = DESCRIPTION_PREFIX + element.getDescription(true);
	    	
	    	// HACK: this should be done using proper copying
	    	StyledText styledText = new StyledText(view.getFakeComposite(), SWT.NULL);
	    	styledText.setText(description);
	    	styledText.selectAll();
	    	styledText.copy();
	    	styledText.dispose();
	    }
	}
}
