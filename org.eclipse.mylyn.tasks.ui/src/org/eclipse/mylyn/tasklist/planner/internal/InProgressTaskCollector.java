package org.eclipse.mylar.tasklist.planner.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.InteractionEvent;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.mylar.tasklist.ITask;

/**
 * Collects tasks that are not complete but have been worked 
 * on during the specified number of previous days.
 * 
 * @author Wesley Coelho (Adapted from CompletedTaskCollector by Key Sueda)
 */
public class InProgressTaskCollector implements ITasksCollector {

	private Map<String, ITask> inProgressTasks = new HashMap<String, ITask>();
	private Date periodStartDate = null;
	private long DAY = 24*3600*1000;
	
	public InProgressTaskCollector(int prevDays) {
		periodStartDate = new Date(new Date().getTime() - prevDays * DAY);
	}
	
	public String getLabel() {
		return "Tasks in Progress";
	}

	public void consumeTask(ITask task) {
		if (!task.isCompleted() && hasActivitySince(task, periodStartDate)  && !inProgressTasks.containsKey(task.getHandleIdentifier())){
			inProgressTasks.put(task.getHandleIdentifier(), task);
		}
	}
	
	protected boolean hasActivitySince(ITask task, Date startDate){
		IMylarContext mylarContext = MylarPlugin.getContextManager().loadContext(task.getHandleIdentifier(),task.getContextPath());
		if (mylarContext != null){
			List<InteractionEvent> events = mylarContext.getInteractionHistory();
			if (events.size() > 0){
				InteractionEvent latestEvent = events.get(events.size() - 1);
				if (latestEvent.getDate().compareTo(periodStartDate) > 0){
					return true;
				}
			}
		}
		return false;
	}
	
	public List<ITask> getTasks() {
		List<ITask> tasks = new ArrayList<ITask>();
		tasks.addAll(inProgressTasks.values());
		return tasks;
	}
}
