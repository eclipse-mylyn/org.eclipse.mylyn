package org.eclipse.mylar.tasklist;


public interface IQueryHit extends ITaskListElement {

	public ITask getOrCreateCorrespondingTask();
	
	public abstract boolean hasCorrespondingActivatableTask();

	public void setAssociatedTask(ITask task);
}
