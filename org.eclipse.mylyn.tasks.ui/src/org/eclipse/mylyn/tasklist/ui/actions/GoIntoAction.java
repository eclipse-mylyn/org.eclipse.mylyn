package org.eclipse.mylar.tasklist.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.mylar.tasklist.TaskListImages;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;
import org.eclipse.ui.part.DrillDownAdapter;

public class GoIntoAction extends Action {

	public static final String ID = "org.eclipse.mylar.tasklist.actions.view.go.into";
		
	private DrillDownAdapter drillDownAdapter;
	
	public GoIntoAction(DrillDownAdapter drillDownAdapter) {
		this.drillDownAdapter = drillDownAdapter;
		
		setId(ID);
		setText("Go Into Category");
		setToolTipText("Go Into Category");
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
