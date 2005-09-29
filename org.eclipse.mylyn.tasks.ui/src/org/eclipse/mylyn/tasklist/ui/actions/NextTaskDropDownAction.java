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
public class NextTaskDropDownAction extends DropDownTaskNavigateAction {
	public static final String ID = "org.eclipse.mylar.tasklist.actions.navigate.next";

	public NextTaskDropDownAction(TaskListView view, TaskActivationHistory history){
    	super(view, history);
		setText("Next Task");
        setToolTipText("Next Task");
        setId(ID);
        setEnabled(false);
        setImageDescriptor(TaskListImages.NAVIGATE_NEXT);
	}
	
	protected void addActionsToMenu(){
		List<ITask> tasks = taskHistory.getNextTasks();
		
		if(tasks.size() > MAX_ITEMS){
			tasks = tasks.subList(0, MAX_ITEMS);
		}
		
		for(int i = 0; i < tasks.size(); i++){
			ITask currTask = tasks.get(i);
			Action taskNavAction = new TaskNavigateAction(currTask);
			ActionContributionItem item= new ActionContributionItem(taskNavAction);
			item.fill(dropDownMenu, -1);
		}
	}
	
    public void run() {
  		if (taskHistory.hasNext()) {
			new TaskActivateAction(taskHistory.getNextTask()).run();
			setButtonStatus();
			view.getViewer().refresh();			
		} 
	}
}
