package org.eclipse.mylar.tasklist.ui.actions;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.mylar.tasklist.ITask;
import org.eclipse.mylar.tasklist.TaskListImages;
import org.eclipse.mylar.tasklist.ui.views.TaskActivationHistory;
import org.eclipse.mylar.tasklist.ui.views.TaskListView;

/**
 * @author Wesley Coelho
 */
public class PreviousTaskDropDownAction extends DropDownTaskNavigateAction {
	public static final String ID = "org.eclipse.mylar.tasklist.actions.navigate.previous";

	public PreviousTaskDropDownAction(TaskListView view, TaskActivationHistory history){
    	super(view, history);
		setText("Previous Task");
        setToolTipText("Previous Task");
        setId(ID);
        setEnabled(true);
        setImageDescriptor(TaskListImages.NAVIGATE_PREVIOUS);
	}
	
	protected void addActionsToMenu(){
		List<ITask> tasks = taskHistory.getPreviousTasks();
		
		if(tasks.size() > MAX_ITEMS){
			tasks = tasks.subList(tasks.size() - MAX_ITEMS, tasks.size());
		}
		
		for(int i = tasks.size() - 1; i >= 0; i--){
			ITask currTask = tasks.get(i);
			Action taskNavAction = new TaskNavigateAction(currTask);
			ActionContributionItem item= new ActionContributionItem(taskNavAction);
			item.fill(dropDownMenu, -1);
		}
	}
	
    public void run() {
		if (taskHistory.hasPrevious()) {
			new TaskActivateAction(taskHistory.getPreviousTask()).run();
			setButtonStatus();
			view.getViewer().refresh();			
		} 
	}

}
