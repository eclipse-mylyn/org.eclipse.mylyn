package org.eclipse.mylar.tasklist;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.core.util.ActiveTimerThread;
import org.eclipse.mylar.core.util.IActiveTimerListener;
import org.eclipse.mylar.core.util.IInteractionEventListener;

public class TaskActiveTimerListener implements IActiveTimerListener, IInteractionEventListener {

	private ActiveTimerThread timer;
	
	private ITask task;

	private boolean isTaskStalled = false;
	
	public TaskActiveTimerListener(ITask task){
		this.task = task;
		timer = new ActiveTimerThread(TaskListManager.INACTIVITY_TIME, this);
		timer.start();
		MylarPlugin.getDefault().addInteractionListener(this);
	}
	
	public void fireTimedOut() {
		task.setActive(task.isActive(), true);
		isTaskStalled = true;
		timer.resetTimer();
	}

	public void interactionObserved(InteractionEvent event) {
		timer.resetTimer();		
		
		if(isTaskStalled){
			task.setActive(task.isActive(), false);
		}
		isTaskStalled = false;
	}

	public void start() {}

	public void stopTimer() {
		timer.killThread();
		MylarPlugin.getDefault().removeInteractionListener(this);
	}

	public void stop() {}

}
