package org.eclipse.mylar.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.tasklist.TaskListImages;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.ui.part.DrillDownAdapter;

public class GoUpAction extends Action {


	public static final String ID = "org.eclipse.mylar.tasklist.actions.view.go.up";
	
	private DrillDownAdapter drillDownAdapter;
	
	public GoUpAction(DrillDownAdapter drillDownAdapter) {
		this.drillDownAdapter = drillDownAdapter;
		setText("Go Up To Root");
		setToolTipText("Go Up To Root");
		setId(ID);
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
