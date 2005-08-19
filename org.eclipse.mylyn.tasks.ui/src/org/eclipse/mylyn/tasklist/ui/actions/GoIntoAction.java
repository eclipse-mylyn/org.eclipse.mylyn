package org.eclipse.mylar.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.tasklist.TaskListImages;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.ui.part.DrillDownAdapter;

public class GoIntoAction extends Action {

	private DrillDownAdapter drillDownAdapter;
	
	public GoIntoAction(DrillDownAdapter drillDownAdapter) {
		this.drillDownAdapter = drillDownAdapter;
		
		setText("Go Into");
		setToolTipText("Go into category");
		setImageDescriptor(TaskListImages.GO_INTO);
	}

	@Override
	public void run() {
		drillDownAdapter.goInto();
		if(TaskListView.getDefault() != null){
			TaskListView.getDefault().updateDrillDownActions();
		}
	}
}
