package org.eclipse.mylar.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.tasklist.TaskListImages;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.ui.part.DrillDownAdapter;

public class GoUpAction extends Action {

	private DrillDownAdapter drillDownAdapter;
	
	public GoUpAction(DrillDownAdapter drillDownAdapter) {
		this.drillDownAdapter = drillDownAdapter;
		setText("Go Up");
		setToolTipText("Go Up");
		setImageDescriptor(TaskListImages.GO_UP);
	}

	@Override
	public void run() {
		drillDownAdapter.goBack();
		if(TaskListView.getDefault() != null){
			TaskListView.getDefault().updateDrillDownActions();
		}
	}
}
