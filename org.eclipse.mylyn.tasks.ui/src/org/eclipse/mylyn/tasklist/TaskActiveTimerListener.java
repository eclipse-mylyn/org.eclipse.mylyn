package org.eclipse.mylar.tasklist;

import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.monitor.IInteractionEventListener;
import org.eclipse.mylar.monitor.MylarMonitorPlugin;
import org.eclipse.mylar.monitor.planning.ActiveTimerThread;
import org.eclipse.mylar.monitor.planning.IActiveTimerListener;

public class TaskActiveTimerListener implements IActiveTimerListener, IInteractionEventListener {

	private ActiveTimerThread timer;
	
	private ITask task;

	private boolean isTaskStalled = false;
	
	public TaskActiveTimerListener(ITask task){
		this.task = task;
		timer = new ActiveTimerThread(TaskListManager.INACTIVITY_TIME, this);
		timer.start();
		MylarMonitorPlugin.getDefault().addListener(this);
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
		MylarMonitorPlugin.getDefault().removeListener(this);
	}

	public void stop() {}

}
